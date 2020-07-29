package com.backstagesupporters.fasttrack.notification;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.backstagesupporters.fasttrack.ui.activity.NotificationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService2 extends FirebaseMessagingService {
//    private static final String TAG = MyFirebaseMessagingService2.class.getSimpleName();
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
//    private NotificationUtils notificationUtils;
    private NotificationUtils2 notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains add notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage);
        }

        // Check if message contains add data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.w(TAG, "Message data payload: " + remoteMessage.getData());
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            createAndSendNotificationB(remoteMessage);
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    // TODO: 1/20/2020
    private void createAndSendNotificationB(RemoteMessage remoteMessage) {
        //Code here
        // https://www.javatpoint.com/sending-notification-message-using-firebase-cloud-messaging
        // https://firebase.google.com/docs/cloud-messaging/android/client
        // https://stackoverflow.com/questions/57365399/how-to-call-methods-in-firebase-cloud-messaging-integration-with-android-studio
    }

    // title, body
    private void handleNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body  = remoteMessage.getNotification().getBody();
        String icon   = remoteMessage.getNotification().getIcon();
        String messageId = remoteMessage.getMessageId();
        String sound = remoteMessage.getNotification().getSound();
        String iconUrl = remoteMessage.getNotification().getIcon();
        String imageUrl = String.valueOf(remoteMessage.getNotification().getImageUrl());
        String timestamp = String.valueOf(remoteMessage.getSentTime());
        Log.w(TAG, "handleNotification Title: " + title);
        Log.d(TAG, "handleNotificationBody: " +body );
        Log.d(TAG, "handleNotification icon: " +icon );
        Log.e(TAG, "handleNotification : " + body);


        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
        if (!NotificationUtils2.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            pushNotification.putExtra("title", title);
            pushNotification.putExtra("body", body);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            showNotificationMessage(getApplicationContext(), title, body, timestamp, pushNotification);
            // play notification sound
            NotificationUtils2 notificationUtils = new NotificationUtils2(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{

            showNotificationMessage(getApplicationContext(), title, body, timestamp, pushNotification);
            // If the app is in background, firebase itself handles the notification
            NotificationUtils2 notificationUtils = new NotificationUtils2(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
    }



    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "Notifi JSONObject " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String body = data.getString("body");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "body: " + body);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils2.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("title", title);
                pushNotification.putExtra("body", body);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils2 notificationUtils = new NotificationUtils2(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                resultIntent.putExtra("title",title);
                resultIntent.putExtra("message",message);
                resultIntent.putExtra("body",body);
                resultIntent.putExtra("isBackground",isBackground);
                resultIntent.putExtra("payload", payload.toString());
                resultIntent.putExtra("imageUrl",imageUrl);
                resultIntent.putExtra("timestamp",timestamp);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, body, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, body, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils2(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils2(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}