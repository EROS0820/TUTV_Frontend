package com.backstagesupporters.fasttrack.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import com.backstagesupporters.fasttrack.models.User;
import com.backstagesupporters.fasttrack.responseClass.LoginResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyService extends Service {
    private String TAG= getClass().getSimpleName();
    public static final int notify = 1*60*1000;  //300000//interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    //======Interface Declaration=========
    ApiInterface apiInterface;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
//        Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show();
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    String EMAIL,PASSWORD,MOBILE, DEVICE_NAME, DEVICE_IMEI, FIREBASE_TOKEN,USER_STATUS ;

                    if (CheckNetwork.isNetworkAvailable(MyService.this)) {
                        EMAIL= AppPreferences.loadPreferences(MyService.this,"EMAIL"); //  VariablesConstant.EMAIL
                        PASSWORD=AppPreferences.loadPreferences(MyService.this,"PASSWORD");  // VariablesConstant.PASSWORD
                        MOBILE = AppPreferences.loadPreferences(MyService.this, "MOBILE_LOGIN"); // VariablesConstant.MOBILE_LOGIN
                        DEVICE_NAME=AppPreferences.loadPreferences(MyService.this,"DEVICE_NAME"); // VariablesConstant.DEVICE_NAME
                        DEVICE_IMEI = AppPreferences.loadPreferences(MyService.this, "DEVICE_IMEI"); // VariablesConstant.DEVICE_IMEI
                        FIREBASE_TOKEN = AppPreferences.loadPreferences(MyService.this, "FIREBASE_TOKEN"); //VariablesConstant.FIREBASE_TOKEN

//                        AppPreferences.savePreferences(mContext, VariablesConstant.USER_STATUS, account_status);

//                        Log.w(TAG, "Firebase token : "+FIREBASE_TOKEN);
//                        Log.d(TAG, "DeviceName DEVICE_NAME : "+DEVICE_NAME);
//                        Log.w(TAG, "DeviceName DEVICE_IMEI : "+DEVICE_IMEI);

                        // ========== Api Call =============
                        if (!EMAIL.isEmpty()){
//                            getLoginResponseCall(EMAIL, PASSWORD);
                            getLoginResponseCall(EMAIL ,PASSWORD, MOBILE, DEVICE_NAME,DEVICE_IMEI, FIREBASE_TOKEN);
                        }
                    } else {
//                        Toast.makeText(MyService.this, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


//    private void getLoginResponseCall(String email, String password, String mobile, String device_type, String device_toke) {
    private void getLoginResponseCall(String email, String password, String mobile, String device_type, String deviceIMEI, String device_toke) {

//        Call<LoginResponse> call = apiInterface.login(email, password);
//        Call<LoginResponse> call = apiInterface.login(email, password,mobile,device_type, device_toke);
        Call<LoginResponse> call = apiInterface.login(email, password, mobile,device_type,deviceIMEI, device_toke);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                String str_response = new Gson().toJson(response.body());
//                Log.w(TAG, "Response >>>>" + str_response);
                try {
                    if (response.code() == 200) {
                        String account_status,msg_error;
                        LoginResponse loginResponse = response.body();
                        boolean success = loginResponse.getSuccess();

                        //"success": true,
                        if (success)
                        {
                            LoginResponse.Data data = loginResponse.getData();
                            String token = data.getToken();
                            User user = data.getUser();
                            account_status = user.getAccountStatus();

                            AppPreferences.savePreferences(MyService.this, "TOKEN", token);
                            AppPreferences.savePreferences(MyService.this, "USER_STATUS", account_status);
//                            Log.w(TAG, "account_status : "+account_status);

                        }
                        else {
                            /**
                             *   "success": false,
                             *     "account_status": "deactivate",
                             *     "error": "Your account is blocked."
                             */
                            account_status =  loginResponse.getAccountStatus();
                            AppPreferences.savePreferences(MyService.this, "USER_STATUS", account_status);
                            msg_error = loginResponse.getError();
//                            Log.w(TAG, "account_status : "+account_status);
//                            Log.e(TAG, "account_status msg_error : "+msg_error);

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                Toast.makeText(MyService.this, "" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

}