package com.backstagesupporters.fasttrack.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.NotificationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;




public class MyFirebaseMessagingService_1 extends FirebaseMessagingService{
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private LocalBroadcastManager broadcaster,notificationBroadcast;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    public static String TITLE ="title";
    public static String BODY ="body";
    public static String TYPE ="Type";
    public static String PAGE ="Page";
    public static String ID ="Id";
    public static String SOUND = "sound";
    private Context context;
    public MediaPlayer mp;

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

    @Override
    public void onCreate() {
        context = MyFirebaseMessagingService_1.this;
        broadcaster = LocalBroadcastManager.getInstance(this);
        //  notificationBroadcast = LocalBroadcastManager.getInstance(this);
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            CreateNotificationForChat(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getChannelId());
        }

        try {
            Map<String, String> params   =remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.e("FJson:",object.toString());

            String id    = object.getString(ID);
            String page  = object.getString(PAGE);
            String type  = object.getString(TYPE);
            String title = object.getString(TITLE);
            String icon  = object.getString("icon");
            String sound = object.getString(SOUND);
            String body  = object.getString(BODY);

            if(sound.equals("1")) {
                CreateNotificationForChat(title,body,id);

                //play vibration & ring when message arrived!
//                playVibrate();
                ringAlarm();
            } else {
                //send broadcast to activities
                Intent intent = new Intent(Config.PUSH_NOTIFICATION);
                intent.putExtra("BODY",body);
                intent.putExtra("TITLE",title);
                broadcaster.sendBroadcast(intent);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("excp", Objects.requireNonNull(e.getMessage()));
        }

    }

    @Override
    public void onDeletedMessages()
    {
        //delete pending message
    }


    private void CreateNotification(String title,String body,String nType, String page, String id) {
        //show notification and send data payload to launcher activity
        Intent intent = new Intent(this,NotificationActivity.class);
        intent.putExtra("ID",id);
        intent.putExtra("BODY",body);
        intent.putExtra("TITLE",title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Spanned styledText = Html.fromHtml(body);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(styledText))
                        .setAutoCancel(true)
                        .setLights(Color.RED, 3000, 3000)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        assert notificationManager != null;
        notificationManager.notify(NotificationID.getID() /* ID of notification */, notificationBuilder.build());
    }

    private void CreateNotificationForChat(String title,String body,String id) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("chat",body);
        intent.putExtra("forUser",id);
        intent.putExtra("title",title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Spanned styledText = Html.fromHtml(body);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(styledText)
                        .setAutoCancel(true)
                        .setLights(Color.RED, 3000, 3000)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        assert notificationManager != null;
        notificationManager.notify(NotificationID.getID() /* ID of notification */, notificationBuilder.build());

    }

    private void playVibrate()
    {
        //3 seconds
        long time = 3000;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Objects.requireNonNull(v).vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            Objects.requireNonNull(v).vibrate(time);
        }
    }
    private void ringAlarm()
    {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        try
        {
            mp.setDataSource(this,Uri.parse("android.resource://com.trackright.agentapp/"+R.raw.alarm1));
            mp.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mp.start();

    }
}