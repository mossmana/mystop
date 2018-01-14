package net.killerandroid.heythatsmystop;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import net.killerandroid.heythatsmystop.notification.NotificationSettings;
import net.killerandroid.heythatsmystop.util.StopLocationRequest;

public class NotificationService extends Service {

    public static final String TAG = NotificationService.class.getSimpleName();
    public static final String CHANNEL_ID = "StopsNotificationService";
    public static final int ONGOING_NOTIFICATION_ID = 999;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private NotificationSettings settings;

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createNotification(R.string.notification_text);
        startForeground(ONGOING_NOTIFICATION_ID, notification);
        settings = new NotificationSettings(this, null);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private Notification createNotification(@StringRes int messageId) {
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return new NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(messageId))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestLocationUpdates();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyNearStop() {
        if (lastKnownLocation == null || !settings.shouldShowNotification(
                lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())) {
            showNotification(R.string.notification_text);
            return;
        }
        showNotification(R.string.notification_near_stop_text);
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

    private void showNotification(@StringRes int messageId) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(
                ONGOING_NOTIFICATION_ID,
                createNotification(messageId));
    }

    private void requestLocationUpdates() {
        if (!settings.areNotificationsEnabled())
            return;

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                lastKnownLocation = locationResult.getLastLocation();
                notifyNearStop();
            }
        };

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                    new StopLocationRequest().getRequest(),
                    locationCallback, null);
        } catch (SecurityException e) {
            // assume that the check happened in the associated foreground activity
        }
    }


}
