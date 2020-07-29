package com.backstagesupporters.fasttrack.ui.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.LanguageActivity;
import com.backstagesupporters.fasttrack.responseClass.SendOTPResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private PinEntryEditText pinEntry;
    private TextView btn_resent_otp;
    TextView tv_mobileNumber; // phone_no
    String token,mobile,otp;

    private ProgressDialog pd;
    ApiInterface apiInterface;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mContext = OtpActivity.this;


        findViewById();

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Intent intent = getIntent();
        if (intent != null) {
            token = intent.getStringExtra("token");
            mobile = intent.getStringExtra("mobile");
        }

//        tv_mobileNumber.setText("+91 8802587111");
        tv_mobileNumber.setText(mobile);

        // tv_continue
       /* tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = pinEntry.getText().toString();
                verifyOtp(token, otp);
            }
        });*/

//        pinEntry.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) { }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() >= 6 ) {
//                    tv_continue.setBackgroundColor(Color.parseColor("#25b8f7"));
//                    tv_continue.setClickable(true);
//                }else {
//                    tv_continue.setBackgroundColor(Color.parseColor("#9a9a9a"));
//                    tv_continue.setClickable(false);
//                }
//            }
//        });



    }

    private void findViewById() {
        tv_mobileNumber = findViewById(R.id.tv_mobileNumber);
        btn_resent_otp = findViewById(R.id.btn_resent_otp);
        pinEntry = findViewById(R.id.edt_PinEntryEditText);

        // btn_resent_otp
        btn_resent_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(mContext, "Otp has been sent", Toast.LENGTH_SHORT).show();
                sendOtp(token);
            }
        });


        // edt_PinEntryEditText
        if (pinEntry != null) {
//            pinEntry.setTypeface(ResourcesCompat.getFont(this, R.font.charmonman_regular));

            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
//                    if (str.toString().equals("1234")) {
                    if (str.toString().length()>=6) {

                    /*    Toasty.success(mContext, "SUCCESS", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(mContext, OnboardingActivity.class));
                        finish();
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        Log.i(TAG,"called OnboardingActivity");*/

                        otp = pinEntry.getText().toString();
                        verifyOtp(token, otp);
//                        Log.e(TAG,"token- "+token);

                    } else {
                        pinEntry.setError(true);
                        Toasty.error(mContext, "FAIL", Toast.LENGTH_SHORT).show();

                        pinEntry.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pinEntry.setText(null);
                            }
                        }, 1000);
                    }

                }
            });
        }

    }


    //============================== Api Call ==============================
    private void verifyOtp(String token, String otp) {

        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        /**
         * POST
         * http://142.93.75.252/api/validate_otp
         */
        Call<SendOTPResponse> call = apiInterface.validateOtp(token, otp);

        call.enqueue(new Callback<SendOTPResponse>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SendOTPResponse sendOTPResponse = response.body();
                        int status = sendOTPResponse.getStatus();
                        String message = sendOTPResponse.getMessage();
//                        Log.e(TAG, "response-message :" + message);

                        if (status == 200) {
                            if (message.equalsIgnoreCase("Otp Missmatch")){
                                Toasty.success(mContext, "Error : "+message, Toast.LENGTH_SHORT).show();
                            }else {
                                Toasty.success(mContext, "SUCCESS : "+message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(mContext, LanguageActivity.class));
                                finish();
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                Log.i(TAG,"called LanguageActivity");
                            }
                        } else {
                            Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
//                    Log.e(TAG,""+e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
//                Toast.makeText(mContext, "" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }



    //============================== Api Call ==============================
    public void sendOtp(final String token) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        /**
         * GET
         * sent on mobile
         * http://142.93.75.252/api/otp?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8xNDIuOTMuNzUuMjUyXC9hcGlcL3JlZ2lzdGVyIiwiaWF0IjoxNTc0MTY4NzA5LCJleHAiOjE1NzQxNzIzMDksIm5iZiI6MTU3NDE2ODcwOSwianRpIjoiR21XTkhTRFkyd2N6MHZnYyIsInN1YiI6MTEsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.ZCMkd04o-IireKptdF7HHCcKjtuBhM4cr2_5Awpn3AA
         *
         */
        Call<SendOTPResponse> call = apiInterface.sendOtp(token);
        call.enqueue(new Callback<SendOTPResponse>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "Response >>>>" + str_response);
                try {
                    if (response.isSuccessful()) {
                        SendOTPResponse sendOTPResponse = response.body();
                        int status = sendOTPResponse.getStatus();
                        String message = sendOTPResponse.getMessage();

                        if (status == 200) {
                            Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(RegistrationActivity.this, RegistrationVerificationActivity.class);
                            /*Intent intent = new Intent(mContext, OtpActivity.class);
                            intent.putExtra("token", token);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                            finish();*/
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else {
                            Toasty.error(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
//                    Log.e(TAG,""+e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                Log.e(TAG,"onFailure"+t.toString());
                pd.dismiss();
//                Toast.makeText(mContext, "" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(mContext, LoginsActivity.class));
        finish();
    }


}
