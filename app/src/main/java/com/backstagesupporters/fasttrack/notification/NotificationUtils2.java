package com.backstagesupporters.fasttrack.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.firebase.BuildConfig;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class NotificationUtils2 {
//    private static String TAG = NotificationUtils2.class.getSimpleName();
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private int NOTIFICATION_COLOR ;
    private Uri NOTIFICATION_SOUND_URI, Buzzer_URI_RINGTONE;
    private final long[] VIBRATE_PATTERN = {000,100,200,300};
    // TODO: 1/25/2020  
    private final int NOTIFICATION_ID = 1;
    private String channelId = "fcm_default_channel";

    NotificationUtils2(Context mContext) {
        this.mContext = mContext;
    }

    void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    void showNotificationMessage(String title, String message, String timeStamp, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        NOTIFICATION_COLOR = mContext.getResources().getColor(R.color.colorPrimary);
        // notification icon
//       int icon = R.mipmap.ic_launcher;
       int icon = R.drawable.logo_new2;

        // Create PendingIntent for Notification Click Action
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // TODO: 1/20/2020
        // Notification with Sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create NotificationCompat.Builder
//        NotificationCompat.Builder mBuilder1 = new NotificationCompat.Builder(mContext);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, channelId);

//        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");
        NOTIFICATION_SOUND_URI = Uri.parse(AppPreferences.loadPreferences(mContext, VariablesConstant.NOTIFICATION_URI_RINGTONE));
        Buzzer_URI_RINGTONE = Uri.parse(AppPreferences.loadPreferences(mContext, VariablesConstant.Buzzer_URI_RINGTONE));
//        Log.i(TAG,"Notification_URI: "+ NOTIFICATION_SOUND_URI);
//        Log.i(TAG,"Buzzer_URI_RINGTONE : "+ Buzzer_URI_RINGTONE);

        // Notification with Vibration
//        setVibration(mBuilder);
//        mBuilder.setVibrate(VIBRATE_PATTERN);
//        mBuilder .setSound(NOTIFICATION_SOUND_URI);
//        mBuilder.setColor(NOTIFICATION_COLOR);

        final Uri alarmSound = Uri.parse(AppPreferences.loadPreferences(mContext, VariablesConstant.NOTIFICATION_URI_RINGTONE));
//        Log.i(TAG,"alarmSound : "+ alarmSound);

        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        // NotificationManager
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    mContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }


        Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        i.putExtra(Settings.EXTRA_APP_PACKAGE, mContext.getPackageName());
        mContext.startActivity(i);
        // set dismiss on click flags
//        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND;
//        notificationManager.notify(NotificationID.getID(), notification);
        notificationManager.notify(NOTIFICATION_ID, notification);
//        Log.w(TAG,"NotificationID: "+NotificationID.getID());
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);

        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

//        notificationManager.notify(NotificationID.getID(), notification);
        notificationManager.notify(NOTIFICATION_ID, notification);
//        Log.w(TAG,"NotificationID: "+NotificationID.getID());
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
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

    // Playing notification sound
    public void playNotificationSound() {
        try {
            //// define sound URI
//            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");;
            NOTIFICATION_SOUND_URI = Uri.parse(AppPreferences.loadPreferences(mContext, VariablesConstant.NOTIFICATION_URI_RINGTONE));
//            Log.w(TAG,"NOTIFICATION_SOUND_URI alarmSound : "+NOTIFICATION_SOUND_URI);
            String notification_check = AppPreferences.loadPreferences(mContext, VariablesConstant.NOTIFICATION_CHECK);
//            Log.i(TAG,"NOTIFICATION_CHECK: "+notification_check);
            // the sound to be played when there's a notification
            Ringtone r = RingtoneManager.getRingtone(mContext, NOTIFICATION_SOUND_URI);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG,"notification Exception: "+e.toString());
        }
    }


    private void setVibration(NotificationCompat.Builder mBuilder) {
        mBuilder.setVibrate(VIBRATE_PATTERN);
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

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
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
}