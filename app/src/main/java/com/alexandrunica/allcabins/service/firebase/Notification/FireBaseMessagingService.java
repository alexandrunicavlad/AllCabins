package com.alexandrunica.allcabins.service.firebase.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alexandrunica.allcabins.MainActivity;
import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.notification.model.NotificationModel;
import com.alexandrunica.allcabins.profile.activities.NotificationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Intent intentNotification = new Intent(this, NotificationActivity.class);
        PendingIntent secondActivityPendingIntent = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_ONE_SHOT);

        Map<String, String> data = remoteMessage.getData();
        String requestId = data.get("requestId").toString();
        String requestBody = data.get("requestBody").toString();
        String requestType = data.get("requestType").toString();

        NotificationModel notification = new NotificationModel(requestId, requestBody, requestType);

        intentNotification.putExtra("notification", notification.getId());

        Log.d("aici", remoteMessage.toString());
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_new)
                        .setContentTitle(notification.getBody())
                        .setContentText(notification.getBody())
                        .setAutoCancel(true)
//                        .addAction(0, "Yes", secondActivityPendingIntent)
//                        .addAction(0, "No", secondActivityPendingIntent)
                        .setSound(defaultSoundUri)
                        .setContentIntent(secondActivityPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}