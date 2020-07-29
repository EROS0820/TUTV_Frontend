package com.backstagesupporters.fasttrack.ui.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.models.User;
import com.backstagesupporters.fasttrack.notification.MyFirebaseInstanceIDService;
import com.backstagesupporters.fasttrack.notification.SaveFCMTokenService;
import com.backstagesupporters.fasttrack.ui.activity.OnboardingActivity;
import com.backstagesupporters.fasttrack.responseClass.LoginResponse;
import com.backstagesupporters.fasttrack.responseClass.LoginResponse2;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.backstagesupporters.fasttrack.utils.vehicle.SoftKeyBoard;
import com.google.android.material.textfield.TextInputEditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginsActivity extends AppCompatActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private TextInputEditText ed_login_idG, ed_passwordG, ed_mobileG;
    private TextView btn_password_forget, btn_login;
    private String loginStatus = "", onBoarding = "",token = "", language = "";
    private ProgressDialog pd;
    //=Interface Declaration
    private ApiInterface apiInterface;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logins);
        mContext = LoginsActivity.this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        findViewById();

        language = AppPreferences.loadPreferences(mContext, VariablesConstant.LANGUAGE);
        onBoarding = AppPreferences.loadPreferences(mContext, VariablesConstant.ONBOARDING_COMPLETE);

        btn_password_forget.setOnClickListener(this);
//        btnSignUp.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        hideKeyboard();

    }

    private void hideKeyboard() {
        ed_mobileG.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence ch, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() ==10){
                  hideSoftKeyboard();
                }
            }
        });
    }
    // use application level context to avoid unnecessary leaks.
    private void hideSoftKeyboard() {
        try {
           /* InputMethodManager inputManager = (InputMethodManager)
                    getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);*/

            SoftKeyBoard.hideKeyboard(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void findViewById() {
        ed_login_idG = findViewById(R.id.ed_login_id);
        ed_passwordG = findViewById(R.id.ed_password);
        ed_mobileG = findViewById(R.id.ed_mobile);
        btn_password_forget = findViewById(R.id.btn_password_forget);
        btn_login = findViewById(R.id.btn_login);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                doValidation();
                break;

            case R.id.btn_password_forget:
                Intent intent1 = new Intent(mContext, ForgetActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
                break;
        }
    }


    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }
    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }


    public String getIMEI(Context context) {
        String deviceId ="";
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 29) {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }else {
                TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    deviceId = mTelephony.getImei();
//                } else {
//                    deviceId = mTelephony.getDeviceId();
//                }
                deviceId = mTelephony.getDeviceId();
            }
        }

//        Log.i(TAG, "DeviceName deviceId : "+deviceId);
        return deviceId;
    }


    private void doValidation() {
         String email = "", password = "", mobile = "", device_type = "", device_IMEI = "";

//        String fToken = String.valueOf(FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL));
//        Log.i(TAG,"Firebase TOPIC_GLOBAL  : "+fToken);
        startService(new Intent(mContext, SaveFCMTokenService.class));
        startService(new Intent(mContext, MyFirebaseInstanceIDService.class));
        String firebase_token = AppPreferences.loadPreferences(mContext, VariablesConstant.FIREBASE_TOKEN);
//        Log.w(TAG, "Firebase token : "+firebase_token);

        device_type = getDeviceName();
//        Log.i(TAG, "DeviceName device_type : "+device_type);

        device_IMEI = getIMEI(mContext);
//        Log.i(TAG, "DeviceName device_IMEI : "+device_IMEI);


        email = ed_login_idG.getText().toString().trim();
        password = ed_passwordG.getText().toString().trim();
        mobile = ed_mobileG.getText().toString().trim();

        AppPreferences.savePreferences(mContext, VariablesConstant.EMAIL, email);
        AppPreferences.savePreferences(mContext, VariablesConstant.PASSWORD, password);
        AppPreferences.savePreferences(mContext, VariablesConstant.MOBILE_LOGIN, mobile);
        AppPreferences.savePreferences(mContext, VariablesConstant.DEVICE_NAME, device_type);
        AppPreferences.savePreferences(mContext, VariablesConstant.DEVICE_IMEI, device_IMEI);

       if (email.equalsIgnoreCase("")) {
           ed_login_idG.setError("Please Enter Username");
           ed_login_idG.requestFocus();
        }  else if (password.equalsIgnoreCase("")) {
            ed_passwordG.setError("Please Enter Password");
            ed_passwordG.requestFocus();
        } else if (mobile.equalsIgnoreCase("")) {
           ed_mobileG.setError("Please Enter Mobile Number");
           ed_mobileG.requestFocus();
        } else if (mobile.length() < 9) {
           ed_mobileG.setError("Please Enter 10 Digit Number");
           ed_mobileG.requestFocus();
        } else {
            // ========== Api Call =============
            if (CheckNetwork.isNetworkAvailable(mContext)) {
//                getLoginResponseCall(email, password, firebase_token,mobile);
//                getLoginResponseCall(email ,password, mobile, device_type, firebase_token);
                getLoginResponseCall(email, password, mobile, device_type, device_IMEI, firebase_token);
            } else {
                Toast.makeText(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getLoginResponseCall(String email, String password, String mobile, String device_type, String deviceIMEI, String device_toke) {
        //   Log.e(TAG, "Test" + email+" "+ password +" "+mobile +" "+device_type +" "+device_toke);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        // email, password, mobile, device_type, device_token
//        Call<LoginResponse> call = apiInterface.login(email, password);
//        Call<LoginResponse> call = apiInterface.login(email, password,device_token);
//        Call<LoginResponse> call = apiInterface.login2(email, password, mobile);
//        Call<LoginResponse> call = apiInterface.login(email, password,mobile,device_type, device_toke);
//        Call<LoginResponse2> call = apiInterface.login2(email, password, mobile,device_type,deviceIMEI, device_toke);
        Call<LoginResponse> call = apiInterface.login(email, password, mobile,device_type,deviceIMEI, device_toke);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
               if (pd!=null)pd.dismiss();
                int responseCode = response.code();
                String str_response = new Gson().toJson(response.body());
//                String str_response_message = response.message();
//                Log.e(TAG, "getLoginResponseCall Response >>>> " + str_response);
//                Log.w(TAG, "response.message() :" + response.message());
                try {
                    if (response.isSuccessful() && responseCode == 200) {
                        // LoginResponse
                        LoginResponse   loginResponse = response.body();
                        boolean success = loginResponse.getSuccess();
//                        Log.w(TAG, "success :" + success);

                        //"success": true,
                        if (success)
                        {
                            // use response data and do some fancy stuff
                            LoginResponse.Data arrayList = loginResponse.getData();
                            token = arrayList.getToken();
//                            Log.w(TAG, "token:  " + token);


                            // User
                            String user_name, user_mobile, user_type, user_email, default_car,user_id,
                                    expiry_date, account_status,device_token,created_at, updated_at;
                            User user = arrayList.getUser();
                            user_id = String.valueOf(user.getId());
                            user_name = user.getName();
                            user_type = user.getUserType();
                            user_email = user.getEmail();
                            user_mobile = user.getMobile();
                            default_car = user.getDefaultCar();
                            expiry_date = user.getExpiryDate();
                            account_status = user.getAccountStatus();
                            device_token = user.getDeviceToken();
                            created_at = user.getCreatedAt();
                            updated_at = user.getUpdatedAt();

//                            Log.w(TAG, "User : " +user_id+", "+ user_name + ", " + user_email + ", " + user_mobile);
//                            Log.i(TAG, "Login User user_type : " + user_type);
//                            Log.i(TAG, "Login User account_status : " + account_status);
//                            Log.i(TAG, "Login User device_token : " + device_token);
                            AppPreferences.savePreferences(mContext, VariablesConstant.TOKEN, token);
                            AppPreferences.savePreferences(mContext, VariablesConstant.USER_ID, user_id);
                            AppPreferences.savePreferences(mContext, VariablesConstant.USER_NAME, user_name);
                            AppPreferences.savePreferences(mContext, VariablesConstant.USER_EMAIL, user_email);
                            AppPreferences.savePreferences(mContext, VariablesConstant.USER_TYPE, user_type);
                            AppPreferences.savePreferences(mContext, VariablesConstant.MOBILE, user_mobile);
                            AppPreferences.savePreferences(mContext, VariablesConstant.LOGIN_STATUS, "1");
                            AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_CAR, default_car);
                            AppPreferences.savePreferences(mContext, VariablesConstant.EXPIRY_DATE, expiry_date);
                            AppPreferences.savePreferences(mContext, VariablesConstant.UPDATED_AT, updated_at);
                            AppPreferences.savePreferences(mContext, VariablesConstant.USER_STATUS, account_status);

//                            Log.w(TAG, "language-" + language);
//                            Log.w(TAG, "onBoarding-" + onBoarding);

                            // account_status
                            if (account_status.equalsIgnoreCase("active"))
                            {
                                if (onBoarding.isEmpty()) {
                                    TaskStackBuilder.create(mContext)
                                            .addNextIntentWithParentStack(new Intent(mContext, HomeActivity.class))
                                            .addNextIntent(new Intent(mContext, OnboardingActivity.class))
                                            .startActivities();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                } else {
                                    Intent intent = new Intent(mContext, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            }
                            else
                                {
                                // TODO: 4/1/2020  
                               // your_account_suspended
                                ErrorUtils.alertDialogMsg(mContext,getString(R.string.user_account),getString(R.string.your_account_suspended));
                                }

                        }
                        else {
                            /**
                             *   "success": false,
                             *     "account_status": "deactivate",
                             *     "error": "Your account is blocked."
                             */
                            String account_status="",msg_error="";
                            account_status =  loginResponse.getAccountStatus();
                            AppPreferences.savePreferences(mContext, "USER_STATUS", account_status);
                            msg_error = loginResponse.getError();
//                            Log.w(TAG, "account_status : "+account_status);
//                            Log.w(TAG, "account_status msg_error : "+msg_error);

                            ErrorUtils.alertDialogMsg(mContext,getString(R.string.user_account), msg_error);
                        }
                    }
                    else
                        {
//                        Log.e(TAG, "Response code :" + responseCode);
                        // error case
                        try {
                            // :{"success":false,"error":"We cant find an account with this credentials."}
                            String jsonString = response.errorBody().string();
                            JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);
                            // convertedObject.get("name").getAsString().equals("Baeldung")
                            String success =  convertedObject.get("success").toString();
                            String error =  convertedObject.get("error").toString();
//                            Log.w(TAG, "response.success :" + success);
//                            Log.w(TAG, "response.error :" + error);

                            //ErrorUtils.alertDialogMsg(mContext,response.message(),"LOGIN_USER");
                            ErrorUtils.apiResponseErrorHandle(TAG,responseCode,error,VariablesConstant.LOGIN_USER, mContext);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (pd!=null)pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
