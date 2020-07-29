package com.backstagesupporters.fasttrack.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;

public class AddDeviceActivity extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private TextInputEditText edit_DeviceSrNumber,edit_sim_no;
    private TextView tv_add;
    private String deviceSrNumber,sim_no;

    private ProgressDialog pd;
    //=Interface Declaration
    private ApiInterface apiInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        mContext=AddDeviceActivity.this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        findViewById();

        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.add_device));

        iv_tool_back_left.setOnClickListener(this);
        tv_add.setOnClickListener(this);
    }


    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        edit_DeviceSrNumber = findViewById(R.id.edit_DeviceSrNumber);
        edit_sim_no = findViewById(R.id.edit_sim_no);
        tv_add = findViewById(R.id.tv_add);
    }

    @Override
    public void onClick(View v) {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }

        switch (v.getId()){
            case R.id.iv_tool_back_left:
                onBackPressed();
                break;

            case R.id.tv_add:
                doValidation();
//                Log.i(TAG,"called tv_add");
                break;
        }
    }

    private void doValidation() {
        deviceSrNumber =edit_DeviceSrNumber.getText().toString().trim();
        sim_no =edit_sim_no.getText().toString().trim();

        if (deviceSrNumber.equalsIgnoreCase("")) {
            edit_DeviceSrNumber.setError(getString(R.string.Please_Enter_deviceSrNumber));
            edit_DeviceSrNumber.requestFocus();
        }  else if (sim_no.isEmpty()) {
            edit_sim_no.setError(  getString(R.string.Please_Enter_sim_no));
            edit_sim_no.requestFocus();
        } else {

            // ========== Api Call =============
            if (CheckNetwork.isNetworkAvailable(mContext)) {
//                getAddDeviceCall(deviceSrNumber,license_number,driver_name,phone_number,device_no,sim_no);
                getAddDeviceCall(deviceSrNumber,sim_no);

            } else {
                Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // AddDevice
    private void getAddDeviceCall(String deviceSrNumber, String sim_no) {

        Toasty.success(mContext,"Successfully !!",Toast.LENGTH_SHORT).show();
    }

   /* private void getAddDeviceResponseCall(String deviceSrNumber, String license_number, String driver_name, String phone_number, String device_no, String sim_no) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<LoginResponse> call = apiInterface.login( deviceSrNumber,license_number,driver_name,phone_number,device_no,sim_no);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e(TAG, "Response >>>>" + str_response);
                try {
                    // LoginResponse
                    LoginResponse loginResponse = response.body();
                    String success = loginResponse.getSuccess();
                    if (success.equals("true")) {
                        // Data
                        LoginResponse.Data arrayList = loginResponse.getData();
                        String token = arrayList.getToken();

                        // User
                        User user = arrayList.getUser();
                        String name, mobile,user_type,email,provider,provider_id,updated_at;
                        name = user.getName();
                        user_type = user.getUserType();
                        email = user.getEmail();
                        mobile = user.getMobile();
                        provider = user.getProvider();
                        provider_id = user.getProviderId();
                        updated_at = user.getUpdatedAt();

                        Log.e(TAG, "Login User : "+name+"-"+user_type+"-"+email+"-"+mobile);

                        AppPreferences.savePreferences(mContext, "TOKEN", token);
                        AppPreferences.savePreferences(mContext, "LOGIN_STATUS", "1");
                        Log.e(TAG,"token-"+token);
                        Toasty.success(mContext, "Login Successfully !!", Toast.LENGTH_SHORT).show();


                        if (user_type.equalsIgnoreCase("user")){
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                        }else if (user_type.equalsIgnoreCase("Owner")){
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                        }else if (user_type.equalsIgnoreCase("admin")){
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }


                    } else {
                        Toasty.error(mContext, "We cant find an account with this credentials.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,""+e.toString());
                    Toasty.error(mContext, "We cant find an account with this credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(mContext, "" + t, Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });


    }
*/


    @Override
    public void onBackPressed() {
//        Log.i(TAG,"called user");
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
//        Log.i(TAG,"called HomeActivity");
    }



}
