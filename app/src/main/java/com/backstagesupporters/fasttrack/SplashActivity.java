package com.backstagesupporters.fasttrack;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.backstagesupporters.fasttrack.responseClass.HistoryReplyResponse;
import com.backstagesupporters.fasttrack.service.MyService;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.user.LoginsActivity;
import com.backstagesupporters.fasttrack.utils.AppConstants;
import com.backstagesupporters.fasttrack.utils.language.LanguageHelper;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class SplashActivity extends BaseActivity {
    private String TAG= getClass().getSimpleName();
    private Context mContext;
    String loginStatus="",userType ="",onBoarding="",token="";
    private static final int TIME_DURATION =2 * 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;
        // Crashlytics
//        Fabric.with(this, new Crashlytics());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
//        FirebaseCrashlytics.getInstance().log("Bailing out");

        // Preferences
        AppConstants.ChangeStatusBarColor(SplashActivity.this);
        loginStatus = AppPreferences.loadPreferences(mContext, VariablesConstant.LOGIN_STATUS);
        userType = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_TYPE);
        token =AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        onBoarding = AppPreferences.loadPreferences(mContext, VariablesConstant.ONBOARDING_COMPLETE);
//        Log.i(TAG,"loginStatus-"+loginStatus);
//        Log.i(TAG,"userType-"+userType);
//        Log.i(TAG,"onBoarding-"+onBoarding);
//        Log.w(TAG,"token - "+token);

       String strHistoryReplyResponseAppPreferences = AppPreferences.loadPreferences(mContext, VariablesConstant.HISTORY_REPLAY_FILE);
//        Log.w(TAG,"strHistoryReplyResponseAppPreferences:"+strHistoryReplyResponseAppPreferences);
        HistoryReplyResponse historyReplayResponse1 = new Gson().fromJson(strHistoryReplyResponseAppPreferences, HistoryReplyResponse.class);
        if (historyReplayResponse1 !=null){
//           Log.w(TAG,"strHistoryReplyResponseAppPreferences size:"+historyReplayResponse1.getDataHistory().get(0).getHistoryList().size());
       }


        // TODO: 1/20/2020
        getToken();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckPermissions();
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (loginStatus.equals("1") && userType.equalsIgnoreCase("admin")) {
//                        Intent intent = new Intent(mContext, VehicleHomeActivity.class);
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else if (loginStatus.equals("1") && userType.equalsIgnoreCase("user")) {
//                        Intent intent = new Intent(mContext, VehicleHomeActivity.class);
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else if (loginStatus.equals("1") && userType.equalsIgnoreCase("sub")|| userType.equalsIgnoreCase("subuser")) {
//                        Intent intent = new Intent(mContext, VehicleHomeActivity.class);
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else {
                        Intent intent = new Intent(mContext, LoginsActivity.class);
//                        Intent intent = new Intent(mContext, LoadingActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                }
            }, TIME_DURATION);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //======= start service which is MyService.java ==============
        startService(new Intent(this, MyService.class));
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        AppPreferences.savePreferences(mContext, VariablesConstant.FIREBASE_TOKEN, token);
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG,"Firebase Token-"+ msg);
//                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     .request(
     Manifest.permission.READ_EXTERNAL_STORAGE,
     Manifest.permission.WRITE_EXTERNAL_STORAGE,
     Manifest.permission.CAMERA,
     Manifest.permission.CALL_PHONE,
     Manifest.permission.SEND_SMS,
     Manifest.permission.RECEIVE_SMS,
     Manifest.permission.READ_SMS,
     Manifest.permission.READ_SMS,
     Manifest.permission.READ_PHONE_STATE,
     Manifest.permission.ACCESS_FINE_LOCATION,
     Manifest.permission.ACCESS_COARSE_LOCATION
     )
     */

    private void CheckPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            RxPermissions.getInstance(SplashActivity.this)
                    .request(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            initialize(aBoolean);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    });
        } else {
            // do something
        }
    }


    public void initialize(boolean isAppInitialized) {
        if (isAppInitialized) {
            Thread background = new Thread() {
                public void run() {
                    try {
                        sleep(TIME_DURATION);
                        if (loginStatus.equals("1") && userType.equalsIgnoreCase("admin")) {
//                            Intent intent = new Intent(mContext, VehicleHomeActivity.class);
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }else if (loginStatus.equals("1") && userType.equalsIgnoreCase("user")) {
//                        Intent intent = new Intent(mContext, VehicleHomeActivity.class);
                            Intent intent = new Intent(mContext,HomeActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else if (loginStatus.equals("1") && userType.equalsIgnoreCase("sub")|| userType.equalsIgnoreCase("subuser")) {
//                            Intent intent = new Intent(mContext, VehicleHomeActivity.class);
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }else {
                            Intent intent = new Intent(mContext, LoginsActivity.class);
//                        Intent intent = new Intent(mContext, LoadingActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            background.start();
        } else {
            /* If one Of above permission not grant show alert (force to grant permission)*/
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Alert");
            builder.setMessage("All permissions necessary");

            builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CheckPermissions();
                }
            });

            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }
    }



    @Override
    protected void attachBaseContext(Context newBase) {
//        Log.e(TAG,"LanguageHelper: "+LanguageHelper.onAttach(newBase));
//        Log.e(TAG,"LanguageHelper: "+LanguageHelper.getLanguage(newBase));
        super.attachBaseContext(LanguageHelper.onAttach(newBase));

//        MyApplication.localeManager.setLocale(newBase);
//        Log.d(TAG, "attachBaseContext");
    }






}
