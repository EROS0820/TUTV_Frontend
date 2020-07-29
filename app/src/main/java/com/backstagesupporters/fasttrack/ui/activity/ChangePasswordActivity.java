package com.backstagesupporters.fasttrack.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.SplashActivity;
import com.backstagesupporters.fasttrack.responseClass.SendOTPResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.UserStatusAccount;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private Context mContext;
    private TextInputEditText ed_passwordCurrent,ed_passwordNew,ed_passwordConfirm;
    TextView btn_submit;

    String expression = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(.{8,15})$";

    private String token, passwordCurrent,passwordNew,passwordConfirm;
    private ProgressDialog pd;
    ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext=ChangePasswordActivity.this;

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.change_password));
        iv_tool_back_left.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
//        Log.e(TAG, "token :" + token);

    }


    @Override
    protected void onStart() {
        super.onStart();
        /**
         * "account_status": "active",
         *  "account_status": "deactive",
         *  // TODO: 4/1/2020
         */
        UserStatusAccount.checkUserStatus(this,TAG);
    }

    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        ed_passwordCurrent =findViewById(R.id.ed_passwordCurrent);
        ed_passwordNew =findViewById(R.id.ed_passwordNew);
        ed_passwordConfirm =findViewById(R.id.ed_passwordConfirm);
        btn_submit =findViewById(R.id.btn_submit);
    }


    @Override
    public void onClick(View view) {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }

        switch (view.getId()){
            case R.id.btn_submit:
                passwordCurrent = ed_passwordCurrent.getText().toString().trim();
                passwordNew = ed_passwordNew.getText().toString().trim();
                passwordConfirm = ed_passwordConfirm.getText().toString().trim();
                doValidation();
                break;

            case R.id.iv_tool_back_left:
                onBackPressed();
                break;
        }
    }

    private void doValidation() {
//        AppPreferences.savePreferences(mContext, VariablesConstant.MOBILE, mobile);
        // pleaseEnter10DigitNumber, pleaseEnterMobileNumber, err_msg_email_invalid,err_msg_email

        if (passwordCurrent.equalsIgnoreCase("")) {
            ed_passwordCurrent.setError("Please Enter User Name");
            ed_passwordCurrent.requestFocus();
        }else if (passwordNew.equalsIgnoreCase("")) {
            ed_passwordNew.setError(getString(R.string.pleaseEnterPassword));
            ed_passwordNew.requestFocus();
        } else if (passwordNew.length() < 4) {
            ed_passwordNew.setError(getString(R.string.enteredPasswordTooShort));
            ed_passwordNew.requestFocus();
        } else if (!passwordNew.matches(expression)) {
            ed_passwordNew.setError(getString(R.string.password_must_be_8_15_characters_with_at_least_uppercase));
            ed_passwordNew.requestFocus();
        } else if (passwordNew.length() > 15) {
            ed_passwordNew.setError(getString(R.string.enteredPasswordTooLong));
            ed_passwordNew.requestFocus();
        } else if (!passwordNew.equalsIgnoreCase(passwordConfirm)) {
            ed_passwordConfirm.setError(getString(R.string.password_not_match));
            ed_passwordConfirm.requestFocus();
        } else {
            if (CheckNetwork.isNetworkAvailable(mContext)) {

                // ======= API Call ==========
                // token, old_password,new_password
               // String old_password = passwordCurrent;
                changePasswordCall(token, passwordCurrent, passwordNew);

            } else {
                Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
            }
        }

    }


    // ======= API Call  changePassword==========

    // parameter - token, old_password,new_password
    private void changePasswordCall(String token, String curentpassword, String newPass) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SendOTPResponse> call = apiInterface.changePassword(token, curentpassword, newPass);

        call.enqueue(new Callback<SendOTPResponse>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
               if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                int  responseCode  = response.code();
//                Log.w(TAG, "Response >>>>" + str_response);
                try {
                    if (response.isSuccessful()) {
                        SendOTPResponse sendOTPResponse = response.body();

                        int status = sendOTPResponse.getStatus();
                        final String message = sendOTPResponse.getMessage();
//                        Log.e(TAG, "message :" + message);

                        if (status == 200) {
//                            et_new_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_vector_success, 0);
//                            et_retype_new_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_vector_success, 0);

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.custom_dialog_for_change_pass, null);
                            builder.setView(dialogView);
                            builder.setCancelable(false);

                            final AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertDialog.show();

                            TextView btn_yes = dialogView.findViewById(R.id.btn_yes);

                            btn_yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                    AppPreferences.savePreferences(mContext, VariablesConstant.LOGIN_STATUS, "0");
                                    Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, SplashActivity.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });

                        } else {
//                            Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
                            Log.e(TAG,"message"+message);
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
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


    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, ProfileActivity.class));
        finish();
    }



}
