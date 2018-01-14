package net.killerandroid.heythatsmystop;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import net.killerandroid.heythatsmystop.notification.NotificationSettings;
import net.killerandroid.heythatsmystop.trimet.StopLocation;
import net.killerandroid.heythatsmystop.trimet.TriMetRequest;
import net.killerandroid.heythatsmystop.trimet.TriMetResponse;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        TriMetResponse.Listener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraIdleListener, DialogInterface.OnDismissListener,
        GoogleMap.OnCameraMoveStartedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 17;
    private static final String NAME_DELIMITER = ":";
    // downtown Portland, OR
    public static final LatLng defaultLocation = new LatLng(45.512794, -122.679565);
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private Marker lastKnownMarker;
    private List<Marker> markers = new ArrayList<>();
    private Toolbar toolbar;
    private NotificationSettings settings;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setActionBar(toolbar);
        settings = new NotificationSettings(this, null);
        getLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (map == null)
                    return;
                lastKnownLocation = locationResult.getLastLocation();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(),
                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                notifyNearStop();
                sendRequest();
            }
        };
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnInfoWindowClickListener(this);
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        updateLocationUI();
        getDeviceLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
                break;
        }
        updateLocationUI();
    }

    private void updateLocationUI()
    {
        if (map == null)
            return;

        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        getDeviceLocation();
                        return true;
                    }
                });
                fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(),
                        locationCallback, null );
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                resumeLocationUpdates();
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = (Location) task.getResult();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                        notifyNearStop();
                        sendRequest();
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void sendRequest() {
        TriMetRequest request = new TriMetRequest(new TriMetRequest.Bbox(
                map.getProjection().getVisibleRegion().latLngBounds));
        request.send(this, this);
    }

    @Override
    public void onResponse(TriMetResponse response) {
        map.clear();
        markers.clear();
        Marker marker;
        for (StopLocation stop : response.getStops()) {
            if (stop == null || stop.getRoute() == null)
                continue;
            String tag = stop.getDesc() + NAME_DELIMITER +
                    stop.getRoute().getDesc() + NAME_DELIMITER +
                    stop.getDir();
            marker = map.addMarker(new MarkerOptions()
                .title(stop.getDesc())
                .snippet(stop.getRoute().getDesc() + ", " + stop.getDir())
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        settings.isNotificationSet(tag) ?
                            BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_ROSE)
                )
                .position(new LatLng(Double.parseDouble(stop.getLat()),
                        Double.parseDouble(stop.getLng()))));
            marker.setTag(tag);
            markers.add(marker);
        }
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        lastKnownMarker = marker;
        AddNotificationDialogFragment dialog = new AddNotificationDialogFragment();
        Bundle args = new Bundle();
        args.putString(AddNotificationDialogFragment.Companion.getNAME(),
                marker.getTag().toString());
        args.putParcelable(AddNotificationDialogFragment.Companion.getPOSITION(),
                marker.getPosition());
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), AddNotificationDialogFragment.Companion.getTAG());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (lastKnownMarker != null) {
            NotificationSettings settings = new NotificationSettings(this, null);
            lastKnownMarker.setIcon(BitmapDescriptorFactory.defaultMarker(
                    settings.isNotificationSet(lastKnownMarker.getTag().toString()) ?
                            BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_ROSE));
            lastKnownMarker = null;
        }
    }

    @Override
    public void onCameraIdle() {
        if (!isInfoWindowShowing())
            sendRequest();
    }

    // FIXME: This seems like a bit of a hack to determine if there is an info window showing
    private boolean isInfoWindowShowing() {
        for (Marker marker : markers) {
            if (marker.isInfoWindowShown()) return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(
            new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem notificationsItem = menu.findItem(R.id.notifications);
        notificationsItem.setChecked(settings.areNotificationsEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications:
                boolean enabled = !item.isChecked();
                settings.enableNotifications(enabled);
                item.setChecked(enabled);
                if (enabled) {
                    // TODO: start notifications service
                } else {
                    // TODO: stop notifications service
                }
                break;
            case R.id.edit_stops:
                Intent intent = new Intent(this, EditStopsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeLocationUpdates();
    }

    private void resumeLocationUpdates() {
        try {
            fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(),
                    locationCallback, null);
        } catch (SecurityException e) {
            getLocationPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseLocationUpdates();
    }

    private void pauseLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void notifyNearStop() {
        if (lastKnownLocation == null || !settings.shouldShowNotification(
                lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
            return;
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION)
            return;
        pauseLocationUpdates();
    }
}