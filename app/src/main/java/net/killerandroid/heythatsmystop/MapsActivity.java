package net.killerandroid.heythatsmystop;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
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

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        TriMetResponse.Listener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraIdleListener, DialogInterface.OnDismissListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 17;
    // downtown Portland, OR
    private final LatLng defaultLocation = new LatLng(45.512794, -122.679565);
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private Marker lastKnownMarker;
    private List<Marker> markers = new ArrayList<>();
    private Toolbar toolbar;
    private NotificationSettings settings;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnInfoWindowClickListener(this);
        map.setOnCameraIdleListener(this);
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
        if (map == null) {
            return;
        }
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
            marker = map.addMarker(new MarkerOptions()
                .title(stop.getDesc())
                .snippet(stop.getRoute().getDesc() + ", " + stop.getDir())
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        settings.isNotificationSet(stop.getDesc()) ?
                            BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_ROSE)
                )
                .position(new LatLng(Double.parseDouble(stop.getLat()),
                        Double.parseDouble(stop.getLng()))));
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
        args.putString(AddNotificationDialogFragment.Companion.getTITLE(),
                marker.getTitle());
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
                    settings.isNotificationSet(lastKnownMarker.getTitle()) ?
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
                // TODO: launch edit stops activity
                break;
        }
        return true;
    }
}