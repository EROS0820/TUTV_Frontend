package com.backstagesupporters.fasttrack.notification;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;

public class MyFirebaseInstanceService extends Service {

        public MyFirebaseInstanceService() {
        }
        /**
         * https://stackoverflow.com/questions/46545372/how-to-send-android-client-registration-token-to-fcm-server
         */

        @Override
        public void onCreate() {
                super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

                if(intent != null){

                        Bundle b = intent.getExtras();

                        if(b != null) {
                                String firebaseInstanceIdToken = b.getString("firebaseInstanceIdToken");
//                                Log.e("SaveFCMTokenService", "firebaseInstanceIdToken : "+firebaseInstanceIdToken);

                                AppPreferences.savePreferences(this, VariablesConstant.FIREBASE_TOKEN, firebaseInstanceIdToken);
//                                Log.i("SaveFCMTokenService", "FIREBASE_TOKEN : " + firebaseInstanceIdToken);

//                sendRegistrationToServer(firebaseInstanceIdToken);
                        }
                }
                return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy() {
                super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent arg0) {
                return null;
        }

  /*  private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference fcmDatabaseRef = ref.child("FCM_Device_Tokens").push();

        FCM_Device_Tokens obj = new FCM_Device_Tokens();
        obj.setToken(token);
        fcmDatabaseRef.setValue(obj);
    }*/


}
