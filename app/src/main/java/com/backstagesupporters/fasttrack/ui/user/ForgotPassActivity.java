package com.backstagesupporters.fasttrack.ui.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.responseClass.ForgetPasswordResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassActivity extends AppCompatActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    ImageView iv_back;
    TextView tv_continue,tv_email,tv_resend;
    PinEntryEditText txt_pin_entry;

    private String email,password;
    //=Interface Declaration
    ApiInterface apiInterface;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        mContext = ForgotPassActivity.this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        find_All_Ids();

        Intent intent=getIntent();
        if(intent!=null){
            email=intent.getStringExtra("EMAIL");
        }

        tv_email.setText(email);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ForgotPassActivity.this,LoginsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            }
        });

        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ======= API Call ==========
                if (CheckNetwork.isNetworkAvailable(mContext)) {
                    String email = tv_email.getText().toString().trim();
                    Log.e(TAG,"email : "+email);
                    registerForget(email);

                } else {
                    Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                }

            }
        });



     /*   txt_pin_entry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 4) {
                    tv_continue.setBackgroundColor(Color.parseColor("#25b8f7"));
                    tv_continue.setClickable(true);
                }else {
                    tv_continue.setBackgroundColor(Color.parseColor("#9a9a9a"));
                    tv_continue.setClickable(false);
                }
            }
        });*/
    }

    private void find_All_Ids() {
        iv_back=findViewById(R.id.iv_back);
        tv_continue=findViewById(R.id.tv_continue);
        txt_pin_entry=findViewById(R.id.txt_pin_entry);
        tv_email=findViewById(R.id.tv_email);
        tv_resend=findViewById(R.id.tv_resend);
    }



    // ======= API Call ==========
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
//                Log.e(TAG, " Response >>>> " + str_response);
//                Log.e(TAG, "Response code :" + responseCode);
                try {
                    if (responseCode == 200) {
                        // ForgetPasswordResponse
                        ForgetPasswordResponse passwordResponse = response.body();
                        boolean success = response.isSuccessful();
//                        Log.e(TAG, "success :" + success);
                        if (response.isSuccessful()) {
                            // Data success
                            ForgetPasswordResponse.Data data1 = passwordResponse.getData();
                            String strMessage = data1.getMessage();
//                            Log.e(TAG, "Data strMessage :" + strMessage);
                            Toasty.success(mContext, ""+strMessage, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent=new Intent(mContext,ForgetActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


}
