package com.example.matecarapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.matecarapp.R;
import com.example.matecarapp.channel.NotificationHelper;
import com.example.matecarapp.receiver.AcceptReceiver;
import com.example.matecarapp.receiver.CancelReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessagingTraveller extends FirebaseMessagingService {

    private static final int NOTIFICATION_CODE = 100;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        RemoteMessage.Notification notification = message.getNotification();
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(title.contains("Otostop Çekme Talebi"))
                {
                    String idTraveller = data.get("idTraveller");
                    showNotificationApiOreoAction(title,body,idTraveller);
                }
                else {
                    showNotificationApiOreo(title, body);

                }
            } else {
                if(title.contains("Otostop Çekme Talebi")) {
                    String idTraveller = data.get("idTraveller");
                    showNotificationActions(title,body,idTraveller);

                }
                else{
                    showNotification(title, body);
                }

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreo(String title, String body) {
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getNotification(title, body, intent, sound);
        notificationHelper.getManager().notify(1, builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreoAction(String title, String body, String idTraveller) {
        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idTraveller", idTraveller);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action acceptAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Kabul Et",
                acceptPendingIntent
        ).build();

        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        acceptIntent.putExtra("idTraveller", idTraveller);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action cancelAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Reddet",
                cancelPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getNotificationActions(title, body, sound,acceptAction,cancelAction);
        notificationHelper.getManager().notify(2, builder.build());

    }

    private void showNotification(String title, String body) {
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationOldAPI(title, body, intent, sound);
        notificationHelper.getManager().notify(1, builder.build());

    }
    private void showNotificationActions(String title, String body,String idTraveller) {


        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idTraveller", idTraveller);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Kabul Et",
                acceptPendingIntent
        ).build();
///////////

        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idTraveller", idTraveller);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action cancelAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Reddet",
                cancelPendingIntent
        ).build();
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationOldAPIActions(title, body,sound,acceptAction,cancelAction);
        notificationHelper.getManager().notify(2, builder.build());

    }
}


