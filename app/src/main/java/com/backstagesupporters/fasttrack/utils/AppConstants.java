package com.backstagesupporters.fasttrack.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.responseClass.LoginResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_SHORT;

public class AppConstants {
    private static String TAG="AppConstants";
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;

    private static ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


    public static void ChangeStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        }
    }

    public static void getLoginResponseCall(final Context mContext, final OnResponse onResponse) {
        final String emailId = AppPreferences.loadPreferences(mContext, VariablesConstant.EMAIL);
        final String password = AppPreferences.loadPreferences(mContext, VariablesConstant.PASSWORD);

        Call<LoginResponse> call = apiInterface.login(emailId, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                String str = new Gson().toJson(response.body());
                int responseCode = response.code();
//                Log.e(TAG, "getLoginResponseCall Response: " + str);
                try {
                    if (responseCode == 200) {
                        LoginResponse loginResponse = response.body();
                        boolean success = loginResponse.getSuccess();
                        if (success) {
                            LoginResponse.Data arrayList = loginResponse.getData();
                            String token = arrayList.getToken();
                            AppPreferences.savePreferences(mContext, VariablesConstant.TOKEN, token);
                            onResponse.onSuccess();
                        }
                    } else {
                        Log.e(TAG, mContext.getString(R.string.err_We_cant_find_account));
//                        Toast.makeText(mContext, mContext.getString(R.string.err_We_cant_find_account), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    onResponse.onError();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                Toast.makeText(mContext, "" + t, Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "onFailure: "+t);
                onResponse.onError();
            }
        });

    }

}