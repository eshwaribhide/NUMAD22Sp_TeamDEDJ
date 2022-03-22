package edu.neu.madcourse.numad22sp_teamdedj;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "StickerMessagingActivity";
    private static final String CHANNEL_ID  = "CHANNEL_ID";
    private static final String CHANNEL_NAME  = "CHANNEL_NAME";
    private static final String CHANNEL_DESC  = "CHANNEL_DESCRIPTION";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNewToken(String newToken) {
        Log.d(TAG, "Refreshed token: " + newToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(refreshedToken);
    }






    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage);
        }
    }


    private void showNotification(RemoteMessage remoteMessageNotification) {

        Intent intent = new Intent(this, MessagingService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        // Configure the notification channel
        notificationChannel.setDescription(CHANNEL_DESC);
        notificationManager.createNotificationChannel(notificationChannel);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        // designing the notification bar with the icon in it
        notification = builder.setContentTitle(remoteMessageNotification.getNotification().getTitle())
                .setContentText("You got a sticker!")
                .setSmallIcon(R.mipmap.ic_launcher_dedj)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0, notification);
    }

}
