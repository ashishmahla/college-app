package in.ac.bkbiet.bkbiet.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import in.ac.bkbiet.bkbiet.activities.MainActivity;
import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.activities.Splash;
import in.ac.bkbiet.bkbiet.models.Noty;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * Created by Ashish on 9/6/2017.
 * notifier service
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "FirebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From : " + remoteMessage.getFrom());
        Log.i(TAG, "Notification Message Body : " + remoteMessage.getData());

        String nId = null;
        String title = null;
        String body = null;
        String senderId = null;
        String sender = null;
        String color = null;
        String notyType = null;

        Map<String, String> data = remoteMessage.getData();
        try {
            if (remoteMessage.getNotification() != null) {
                title = remoteMessage.getNotification().getTitle();
                body = remoteMessage.getNotification().getBody();
            } else if (data != null) {
                nId = data.get("nId");
                title = data.get("title");
                body = data.get("body");
                senderId = data.get("senderId");
                sender = data.get("sender");
                color = data.get("color");

                notyType = data.get("notyType");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nId == null)
            nId = "No Id Found";
        if (title == null)
            title = "No Title";
        if (body == null)
            body = "No Body";
        if (sender == null)
            sender = "No Sender";

        if (color == null || (!color.matches("^#([A-Fa-f0-9]{6})$") &&
                !color.matches("^#([A-Fa-f0-9]{8})$")))
            color = "#d50000";

        if (notyType == null)
            notyType = "default";

        String receivedAt = Statics.getTimeStamp(Uv.NOTIFICATION_DATE_FORMAT);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Uv.NOTIFICATION_DATE_FORMAT, Locale.ENGLISH);
        String sentAt = dateFormat.format(new Date(remoteMessage.getSentTime()));

        Noty noty = new Noty(nId, title, body, color, senderId, sender, receivedAt, sentAt, false);

        switch (notyType) {
            case "new_chat_noty":
                if (Uv.currChatWith != null && !Uv.currChatWith.equals(noty.getSenderId())) {
                    sendNotification(noty, Uv.NEW_CHAT_NOTY_ID, noty.getSenderId());
                }
                break;
            default:
                int unreadCount = 0;
                SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                try {
                    db.addNoty(noty);
                    unreadCount = db.getUnreadNotyCount();
                } catch (Exception ignored) {
                } finally {
                    db.close();
                }
                sendNotification(noty, Uv.DEFAULT_NOTIFICATION_ID, noty.getSenderId());
                Statics.sendNotyCountBroadcast(this, unreadCount);
        }
    }

    private void sendNotification(final Noty noty, final int NOTIFICATION_ID, final String NOTIFICATION_TAG) {
        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("fcm_notification", "Y");
        int uniqueInt = (int) (System.currentTimeMillis() & 0xff);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), uniqueInt, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (noty.getColor() == null || (!noty.getColor().matches("^#([A-Fa-f0-9]{6})$") &&
                !noty.getColor().matches("^#([A-Fa-f0-9]{8})$")))
            noty.setColor("#d50000");

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_noties)
                .setContentTitle(noty.getTitle())
                .setContentText(isNotificationVisible() ? "Multiple Messages" : noty.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.parseColor(noty.getColor()))
                .setSubText(noty.getSender())
                .setContentInfo(noty.getSentAt())
                .setContentIntent(pendingIntent)
                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle(notificationBuilder)
                        .bigText(noty.getBody()));


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

    private boolean isNotificationVisible() {
        Intent notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
        PendingIntent test = PendingIntent.getActivity(MyFirebaseMessagingService.this, Uv.NEW_CHAT_NOTY_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }
}