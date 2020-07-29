package com.backstagesupporters.fasttrack.notification;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.NotificationActivity;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class MyFirebaseMessagingService  extends FirebaseMessagingService {
//    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Uri alarmSound;
    private Uri alarmSoundRrl;
    private MediaPlayer mp;
    Intent intent;
    PendingIntent pendingIntent;
    public static final int NOTIFICATION_ID = 100;
    String CHANNEL_ID;
    String title, body, message, timestamp, vehicle_id, imageUrl, icon, sound;

    boolean isBackground;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "onMessageReceived : " + remoteMessage.getFrom());

        CHANNEL_ID = getString(R.string.default_notification_channel_id);

        // The audio url to play
        alarmSoundRrl = Uri.parse("https://mobcup.net/d/q67g6r4a/mp3");
        alarmSound = Uri.parse(AppPreferences.loadPreferences(this, VariablesConstant.NOTIFICATION_URI_RINGTONE));

        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
//        Log.i(TAG, "alarmSound : " + alarmSound);



        if (remoteMessage != null) {
            intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message RemoteMessage : " + remoteMessage.getData());
            try {
                title = remoteMessage.getData().get("title");
                body = remoteMessage.getData().get("body");
                message = remoteMessage.getData().get("message");
                vehicle_id = remoteMessage.getData().get("vehicle_id");
                sound = remoteMessage.getData().get("sound");
                isBackground = Boolean.parseBoolean(remoteMessage.getData().get("is_background"));
                imageUrl = String.valueOf(Uri.parse(remoteMessage.getData().get("image")));
                timestamp = remoteMessage.getData().get("timestamp");
                String payload = remoteMessage.getData().get("payload");

//                Log.e(TAG, "title: " + title);
//                Log.e(TAG, "body: " + body);
//                Log.e(TAG, "message: " + message);
//                Log.e(TAG, "timestamp: " + timestamp);
//                Log.e(TAG, "imageUrl: " + imageUrl);
//                Log.e(TAG, "isBackground: " +isBackground);
//                Log.e(TAG, "payload: " + payload);

                if (body!=null){
                    showNotification(title, body, timestamp, pendingIntent, alarmSound);
                }else {
                    showNotification(title, message, timestamp, pendingIntent, alarmSound);
                }

//                JSONObject json = new JSONObject(remoteMessage.getData());
//                handleDataMessageJson(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            sendNotification(remoteMessage);
        }


    }


    @Override
    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }


    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }



    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    private void sendNotification(RemoteMessage remoteMessage) {
        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();
        sound = remoteMessage.getNotification().getSound();
        icon = remoteMessage.getNotification().getIcon();
        imageUrl = String.valueOf(remoteMessage.getNotification().getImageUrl());

//        Log.d(TAG, "Message getNotification Title: " + title);
//        Log.d(TAG, "Message getNotification Body: " + body);
//        Log.d(TAG, "Message getNotification Sound: " + sound);
//        Log.d(TAG, "Message getNotification Icon: " + icon);
//        Log.d(TAG, "Message getNotification ImageUrl: " + imageUrl);
//        Log.d(TAG, "Message getNotification ChannelId: " + remoteMessage.getNotification().getChannelId());
//        Log.d(TAG, "Message getNotification VibrateTimings: " + remoteMessage.getNotification().getVibrateTimings());
        vehicle_id = remoteMessage.getData().get("vehicle_id");
        message = remoteMessage.getData().get("message");
//        Log.e(TAG, "Message getNotification vehicle_id: " + vehicle_id);
//        Log.e(TAG, "Message getNotification message: " + message);


        if (!isAppIsInBackground(getApplicationContext())){

            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("title", title);
            pushNotification.putExtra("body", body);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            showNotification(title, body, timestamp, pendingIntent, alarmSound);

            //play vibration & ring when message arrived!
//            playVibrate();

//            ringAlarm(alarmSoundRrl);
        }else {

            showNotification(title, message, timestamp, pendingIntent, alarmSound);
            // app is in background, show the notification in notification tray
            Intent resultIntent = new Intent(getApplicationContext(), NotificationActivity.class);
            resultIntent.putExtra("title",title);
            resultIntent.putExtra("message",message);
            resultIntent.putExtra("body",body);
            resultIntent.putExtra("isBackground",isBackground);
            resultIntent.putExtra("imageUrl",imageUrl);
            resultIntent.putExtra("timestamp",timestamp);

        }

        //Here is FILE_NAME is the name of file that you want to play
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.car_lock);

        if (sound.equals("1")){
           ringAlarm(alarmSound);
       }else {
           ringAlarm(soundUri);
       }


    }


    private void handleDataMessageJson(JSONObject json) {
//        Log.e(TAG, "Notifi JSONObject " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");
            title = data.getString("title");
            body = data.getString("body");
            message = data.getString("message");
            isBackground = data.getBoolean("is_background");
            imageUrl = data.getString("image");
            timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

//            Log.e(TAG, "title: " + title);
//            Log.e(TAG, "body: " + body);
//            Log.e(TAG, "message: " + message);
//            Log.e(TAG, "isBackground: " + isBackground);
//            Log.e(TAG, "payload: " + payload.toString());
//            Log.e(TAG, "imageUrl: " + imageUrl);
//            Log.e(TAG, "timestamp: " + timestamp);

            if (body!=null){
                showNotification(title, body, timestamp, pendingIntent, alarmSound);
            }else {
                showNotification(title, message, timestamp, pendingIntent, alarmSound);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    private void showNotification(String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

//        Drawable drawable = new BitmapDrawable(getResources(), imageUrl);
//        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmap = getBitmapFromURL(imageUrl);



        NotificationCompat.Builder notificationBuilder
                = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setSound(alarmSound)
                        .setStyle(inboxStyle)
                        .setSmallIcon(getApplicationInfo().icon)
                        .setLargeIcon(bitmap)
                        .setContentText(message)
                        .setWhen(getTimeMilliSec(timeStamp))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
//        Log.w(TAG, "NotificationID: " + NotificationID.getID());
    }


    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playVibrate() {
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

    private void ringAlarm(Uri SoundRrl) {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        try {
            mp.setDataSource(this,SoundRrl);
            mp.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mp.start();
    }





    /**
     * Method checks if the app is in background or not
     */
    static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                componentInfo = taskInfo.get(0).topActivity;
            }
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    class MyWorker extends Worker {

        private static final String TAG = "MyWorker";

        public MyWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
            super(appContext, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
//            Log.d(TAG, "Performing long running task in scheduled job");
            // TODO(developer): add long running task here.
            return Result.success();
        }
    }
}