package com.backstagesupporters.fasttrack.notification;

import android.content.Intent;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
//    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String refreshedToken) {  //Added onNewToken method
//        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("FIREBASE_TOKEN", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        Log.w("MyFirebaseInstanceID", "FIREBASE_TOKEN : " + refreshedToken);

        Intent intent = new Intent(MyFirebaseInstanceIDService.this, SaveFCMTokenService.class);
        intent.putExtra("firebaseInstanceIdToken",refreshedToken);
        MyFirebaseInstanceIDService.this.startService(intent);

    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
//        Log.w(TAG, "sendRegistrationToServer: " + token);

        //add code here
        /**
         * https://github.com/miskoajkula/Fcm/blob/Fcm/app/src/main/java/testing/fcm/FirebaseInstanceIDService.java
         * https://github.com/evollu/react-native-fcm/issues/1111
         */

    }

    private void storeRegIdInPref(String refreshedToken) {
      /*  SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();*/
        AppPreferences.savePreferences(this, VariablesConstant.FIREBASE_TOKEN, refreshedToken);
//        Log.w(TAG, "FIREBASE_TOKEN : " + refreshedToken);
    }
}

