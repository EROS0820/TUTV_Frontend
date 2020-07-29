package com.backstagesupporters.fasttrack.ui.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.modules.RegistrationModule;
import com.backstagesupporters.fasttrack.responseClass.SendOTPResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.AppConstants;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    // https://jakewharton.github.io/butterknife/
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;

    @BindView(R.id.edt_name)
    TextInputEditText edt_name;
    @BindView(R.id.edt_email)
    TextInputEditText edt_email;
    @BindView(R.id.edt_mobile)
    TextInputEditText edt_mobile;
    @BindView(R.id.edt_password)
    TextInputEditText edt_password;
    @BindView(R.id.edt_Newpassword)
    TextInputEditText edt_Newpassword;

    ImageView iv_back;
    ProgressDialog pd;
    String mobile;

    private CountryCodePicker countryCodePicker;
    TextView tv_continue;

    //======Interface Declaration=========
    ApiInterface apiInterface;

    String expression = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(.{8,15})$";

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mContext = RegistrationActivity.this;
        AppConstants.ChangeStatusBarColor(RegistrationActivity.this);

        ButterKnife.bind(this);

        tv_continue = findViewById(R.id.tv_continue);
        iv_back = findViewById(R.id.iv_back);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        edt_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View editText, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) editText).setSelection(((EditText) editText).getText().length());
                }
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                StartFirebaseLogin();
                doValidation();
            }
        });


    }




    private void doValidation() {
        String name = edt_name.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        mobile = edt_mobile.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
        String newpassword = edt_Newpassword.getText().toString().trim();

        AppPreferences.savePreferences(mContext, VariablesConstant.USER_FULL_NAME, name);
        AppPreferences.savePreferences(mContext, VariablesConstant.EMAIL, email);
        AppPreferences.savePreferences(mContext, VariablesConstant.PASSWORD, password);
        AppPreferences.savePreferences(mContext, VariablesConstant.MOBILE, mobile);


        if (name.equalsIgnoreCase("")) {
            edt_name.setError("Please Enter User Name");
            edt_name.requestFocus();
        } else if (email.equalsIgnoreCase("")) {
            edt_email.setError("Please Enter Email");
            edt_email.requestFocus();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edt_email.getText().toString()).matches()) {
            edt_email.setError("Invalid Email !!");
            edt_email.requestFocus();
        } else if (mobile.equalsIgnoreCase("")) {
            edt_mobile.setError("Please Enter Mobile Number");
            edt_mobile.requestFocus();
        } else if (mobile.length() < 8) {
            edt_mobile.setError("Please Enter 10 Digit Number");
            edt_mobile.requestFocus();
        } else if (password.equalsIgnoreCase("")) {
            edt_password.setError("Please Enter Password ");
            edt_password.requestFocus();
        } else if (password.length() < 4) {
            edt_password.setError("Entered Password Too Short");
            edt_password.requestFocus();
        } else if (!password.matches(expression)) {
            edt_password.setError("Password must be 8-15 characters with at least 1 uppercase 1 lowercase and 1 numeric digit");
            edt_password.requestFocus();
        } else if (password.length() > 15) {
            edt_password.setError("Entered Password Too Long");
            edt_password.requestFocus();

        } else if (!password.equalsIgnoreCase(newpassword)) {
            edt_Newpassword.setError("Password not match");
            edt_Newpassword.requestFocus();
        } else {
            if (CheckNetwork.isNetworkAvailable(mContext)) {

                // ======= API Call ==========
                registerUser(name, email, mobile, password);


            } else {
                Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //============================== Api Call register ==============================
    private void registerUser(String name, String email, String mobile, String password) {
        RegistrationModule registrationModule = new RegistrationModule(name, email, mobile, password);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();

        /**
         * POST
         * register User
         * http://138.197.210.36/api/register
         */

        Call<JsonElement> call =  apiInterface.registerUser(registrationModule);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "Response >>>>" + str_response);
                try {
                    if (response.code() == 200) {
                        JSONObject jsonObject = new JSONObject(str_response);
                        String success = jsonObject.getString("success");

                        if (success.equals("true")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String token = data.getString("token");
//                            Log.e(TAG,"token- "+token);
//                            Log.i(TAG,"Registration Successfully !!");
//                            Toasty.success(mContext, "Registration Successfully !!", Toast.LENGTH_SHORT).show();
                            sendOtp(token);

                        } else {
                            JSONObject error = jsonObject.getJSONObject("error");
                            if (error.has("email") && error.has("mobile")) {
                                JSONArray email = error.getJSONArray("email");
                                JSONArray mobile = error.getJSONArray("mobile");
                                Toast.makeText(mContext, "" + email.get(0) + mobile.get(0), Toast.LENGTH_SHORT).show();
                            } else if (error.has("email")) {
                                JSONArray email = error.getJSONArray("email");
                                Toast.makeText(mContext, "" + email.get(0), Toast.LENGTH_SHORT).show();
                            } else if (error.has("mobile")) {
                                JSONArray mobile = error.getJSONArray("mobile");
                                Toast.makeText(mContext, "" + mobile.get(0), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(str_response);
                        String success = jsonObject.getString("success");

                        JSONObject error = jsonObject.getJSONObject("error");
                        JSONArray email = error.getJSONArray("email");

                        for (int i = 0; i < email.length(); i++) {
                            String message = String.valueOf(email.get(i));
                        }
                        Toast.makeText(mContext, "ss", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                pd.dismiss();
                Toasty.warning(mContext, "" + t, Toast.LENGTH_SHORT).show();
            }
        });


    }

    //============================== Api Call Otp ==============================
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
                            Toasty.success(mContext, "Otp "+message+"  on your mobile "+mobile +"", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(RegistrationActivity.this, RegistrationVerificationActivity.class);
                            Intent intent = new Intent(mContext, OtpActivity.class);
                            intent.putExtra("token", token);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                            finish();
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
        startActivity(new Intent(mContext, LoginsActivity.class));
//        startActivity(new Intent(mContext, WelcomeActivity.class));
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

}
