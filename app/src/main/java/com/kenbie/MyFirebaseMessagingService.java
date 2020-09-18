package com.kenbie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kenbie.fragments.MessageUserListFragment;
import com.kenbie.model.MsgUserItem;
import com.kenbie.util.BadgeUtils;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Map data = remoteMessage.getData();
        try {
            if (data != null) {
                parseNotificationData(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

       /* // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Toast.makeText(getApplicationContext(), remoteMessage.getData().toString(), Toast.LENGTH_LONG).show();
            if (*//* Check if data needs to be processed by long running job *//* true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
*/
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void parseNotificationData(Map data) {
        try {
            Intent intent = null;
            int type = 0, badge = 0;
            if (data.containsKey("type")) {
                type = Integer.valueOf((String) data.get("type"));
                if (data.containsKey("badge"))
                    badge = Integer.valueOf((String) data.get("badge"));
                if (type == 2) { // Message
                    MsgUserItem value = new MsgUserItem();
                    if (data.containsKey("title"))
                        value.setUser_name((String) data.get("title"));
                    if (data.containsKey("sender_profile"))
                        value.setUser_img((String) data.get("sender_profile"));
                    value.setUid(Integer.valueOf((String) data.get("sender_id")));
                    intent = new Intent(this, MessageConvActivity.class);
                    intent.putExtra("MsgItem", value);
                } else if (type == 3 || type == 4 || type == 5) { // Liked you // Favourite // Visitor
                    intent = new Intent(this, KenbieActivity.class);
                    intent.putExtra("NavType", 2);
                    intent.putExtra("Notification", true);
                } else
                    intent = new Intent(this, KenbieActivity.class);
            } else
                intent = new Intent(this, KenbieActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;

            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(getNotificationIcon())
                            .setContentTitle((String) data.get("title"))
                            .setContentText((String) data.get("msg"))
                            .setAutoCancel(true)
                            .setNumber(badge)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        getString(R.string.channel_name),
                        NotificationManager.IMPORTANCE_HIGH);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            assert notificationManager != null;
            notificationManager.notify(m, notificationBuilder.build());

            // Samsung and Sony devices
            BadgeUtils.setBadge(this, badge);
            // Notify for notification
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(KenbieActivity.NOTIFY_ACTIVITY_ACTION);
            sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_stat_notification_small : R.mipmap.ic_stat_notification;
    }

    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }
}