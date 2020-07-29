package com.backstagesupporters.fasttrack.ui.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.responseClass.ForgetPasswordResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetActivity extends AppCompatActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private TextInputEditText ed_login_idG;
    TextView btn_submit;

    private String userId,password;
    //=Interface Declaration
    ApiInterface apiInterface;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        mContext = ForgetActivity.this;


        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        findViewById();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checkField(TextInputEditText editText, TextInputLayout inputLayout)
                checkField(ed_login_idG);
            }
        });

    }


    // ======= API Call ==========
    private void registerForget2(String userIdEmail) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        // http://3.135.158.46/api/recover
        Call<ForgetPasswordResponse> call = apiInterface.forgotPassword(userIdEmail);
        call.enqueue(new Callback<ForgetPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgetPasswordResponse> call, Response<ForgetPasswordResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "registerForget Response >>>>" + str_response);

                // ForgetPasswordResponse
                ForgetPasswordResponse passwordResponse = response.body();
                boolean success = response.isSuccessful();
//                Log.e(TAG, "success :" + success);
                if (response.body() !=null){


//                boolean success = passwordResponse.getSuccess();
                    if (response.isSuccessful()) {
                        // Data success
                        ForgetPasswordResponse.Data data1 = passwordResponse.getData();
                        String strMessage = data1.getMessage();
//                        Log.e(TAG, "Data strMessage :" + strMessage);
                        Toasty.success(mContext, ""+strMessage, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(mContext, ForgotPassActivity.class);
                        intent.putExtra("EMAIL", userId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();
                    }
                    /*else {
                        // Data Error
                        ForgetPasswordResponse.Error data2 =  passwordResponse.getError();
                        String strMessage =  data2.getEmail();
                        Log.e(TAG, "Error strMessage :" + strMessage);
                        Toasty.success(mContext, " "+strMessage, Toast.LENGTH_SHORT).show();
                    }*/
                }else {
                    Toasty.error(mContext, "Your email address was not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordResponse> call, Throwable t) {
                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }


    private void registerForget(String userIdEmail) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        // http://3.135.158.46/api/recover
        Call<ForgetPasswordResponse> call = apiInterface.forgotPassword(userIdEmail);
        call.enqueue(new Callback<ForgetPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgetPasswordResponse> call, Response<ForgetPasswordResponse> response) {
                pd.dismiss();
                int responseCode = response.code();
                String str_response = new Gson().toJson(response.body());
                String str_response_message = response.message();
//                Log.e(TAG, "registerForget Response >>>> " + str_response);
//                Log.e(TAG, "Response message " + str_response_message);
//                Log.e(TAG, "Response code :" + responseCode);
                try {
                    if (responseCode == 200) {
                        // ForgetPasswordResponse
                        ForgetPasswordResponse passwordResponse = response.body();
                        boolean success = response.isSuccessful();
                        Log.e(TAG, "success :" + success);
                        if (response.isSuccessful()) {
                            // Data success
                            ForgetPasswordResponse.Data data1 = passwordResponse.getData();
                            String strMessage = data1.getMessage();
//                            Log.e(TAG, "Data strMessage :" + strMessage);
                            Toasty.success(mContext, ""+strMessage, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(mContext, ForgotPassActivity.class);
                            intent.putExtra("EMAIL", userId);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            finish();
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Toasty.error(mContext, getString(R.string.err_email_id_not_found), Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"Server side error");
//                                ErrorUtils.error();
                                break;

                            case 404:
                                Toasty.error(mContext, getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Toasty.error(mContext, getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordResponse> call, Throwable t) {
                pd.dismiss();
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }



    private void findViewById() {
        ed_login_idG = findViewById(R.id.ed_login_id);
        btn_submit = findViewById(R.id.btn_submit);


        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }

    }



    public  void checkField(TextInputEditText editText){
        if (editText.getText().toString().trim().equalsIgnoreCase("")) {
            editText.setError(getString(R.string.err_msg_email));
            editText.requestFocus();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
            editText.setError(getString(R.string.err_msg_email_invalid));
            editText.requestFocus();
        }else {
            userId=ed_login_idG.getText().toString();
            // ======= API Call ==========
            if (CheckNetwork.isNetworkAvailable(mContext)) {

                registerForget(userId);

            } else {
                Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public  boolean checkField2(TextInputEditText editText){
        if(editText.getText().toString().trim().contains("@")){
            if (editText.getText().toString().trim().equalsIgnoreCase("")) {
                editText.setError(getString(R.string.err_msg_email));
                editText.requestFocus();
                return false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
                editText.setError(getString(R.string.err_msg_email_invalid));
                editText.requestFocus();
               /* inputLayout.setError(getString(R.string.err_msg_email_invalid));
                inputLayout.requestFocus();*/
                return false;
            }
        } else {
            if (editText.getText().toString().trim().isEmpty()){
                editText.setError(getString(R.string.err_msg_mobile));
                editText.requestFocus();
                return false;
            }else if (editText.getText().toString().trim().length() < 9) {
                editText.setError(getString(R.string.err_msg_mobile_too_short));
                editText.requestFocus();
                return false;
            } else if (!Patterns.PHONE.matcher(editText.getText().toString()).matches()){
                editText.setError(getString(R.string.err_msg_mobile_invalid));
                editText.requestFocus();
                return false;
            }else if (editText.getText().toString().trim().length()>15){
                editText.setError(getString(R.string.err_msg_mobile_too_long));
                editText.requestFocus();
                return false;
            }
        }

        return true;
    }



    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(mContext, LoginsActivity.class));
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }



}
