package com.backstagesupporters.fasttrack.ui.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.SpeedLimitResponse;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SpeedLimitActivity extends BaseActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private TextInputEditText edt_speed;
    private Spinner vehicleSpinner;
    private TextView tv_submit;
    private String speed, vehicle_id, vehicle_name,vehicle_type, vehicle_no, vehicle_color;
    List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
    private ArrayList myVehicleListSpinner = new ArrayList();

    ApiInterface apiInterface;
    ProgressDialog pd;
    String token, userId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_limit);
        mContext = SpeedLimitActivity.this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);
//        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.speedLimit));
        String mySpeed = AppPreferences.loadPreferences(mContext, VariablesConstant.SPEED_LIMIT);
//        Log.e(TAG, "mySpeed :"+mySpeed);
        edt_speed.setText(mySpeed);

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getVehicleCall(token, userId);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

        tv_submit.setOnClickListener(view -> {
            speed = edt_speed.getText().toString().trim();
            AppPreferences.savePreferences(mContext,VariablesConstant.SPEED_LIMIT, speed);
            doValidation();
        });

        iv_tool_back_left.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void findViewById() {
        vehicleSpinner = findViewById(R.id.vehicleSpinner);
        edt_speed = findViewById(R.id.edt_speed);
        tv_submit = findViewById(R.id.tv_submit);
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
    }

    private void setVehicleSpinner() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext,R.layout.spinner_item, myVehicleListSpinner);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
        vehicleSpinner.setAdapter(spinnerArrayAdapter);

        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                int mySelectedVehicleId;
                if(selectedItem.equals(getString(R.string.please_select_vehicle))) {
                    Log.i(TAG,"selectedItem "+selectedItem);
                }else {
                    vehicle_name = parent.getItemAtPosition(position).toString();
                    mySelectedVehicleId = parent.getSelectedItemPosition() +1;
                    vehicle_id = String.valueOf(vehicleArrayList.get(position).getVehicleId());
//                    Log.i(TAG,"selectedItem myId "+mySelectedVehicleId);
//                    Log.i(TAG,"vehicle_id : "+vehicle_id);
//                    Log.i(TAG,"selectedItem vehicle_name "+vehicle_name);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Log.i(TAG,"parent "+parent.toString());
            }
        });
    }

    private void doValidation() {
        if (speed.equalsIgnoreCase("")) {
            edt_speed.setError(getString(R.string.enterSpeedLimit));
            edt_speed.requestFocus();
        } else {
            if(vehicle_id.equalsIgnoreCase("0")|| vehicle_name.equalsIgnoreCase("All Vehicle")) {
                Toasty.error(mContext, getString(R.string.please_select_vehicle), Toast.LENGTH_SHORT).show();
            } else {
                if (CheckNetwork.isNetworkAvailable(mContext)) {
//                    Log.e(TAG, "token :"+token);
//                    Log.e(TAG, "vehicle_id :"+vehicle_id);
//                    Log.e(TAG, "speed :"+speed);

                    getSpeedLimitCall(token,vehicle_id,speed);
                    Toasty.success(mContext,"Successfully !!", Toast.LENGTH_SHORT).show();

                } else {
                    Toasty.warning(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }




    //================ Api Call ============================

    private void getVehicleCall(final String token, String user_id) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<VehiclesResponse> call = apiInterface.showVehiclesUserId(user_id);
        call.enqueue(new Callback<VehiclesResponse>() {
            @Override
            public void onResponse(Call<VehiclesResponse> call, Response<VehiclesResponse> response) {
               if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getVehicleCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        VehiclesResponse vehiclesResponse = response.body();
                        int status = vehiclesResponse.getStatus();
                        if (status== 200) {
                            vehicleArrayList  = vehiclesResponse.getVehicles();
//                            Log.e(TAG, "VehiclesResponse-listVehicle size:" + vehicleArrayList.size());
                            for (int i=0; i<vehicleArrayList.size();i++){
                                myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
//                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleName() +" "+ vehicleArrayList.get(i).getVehicleNo());
                            }
                            //vehicleSpinner
                            setVehicleSpinner();
                        } else {
//                            Log.e(TAG, "Status Code :"+status);
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    Log.e(TAG,""+e.toString());
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
                if (pd!=null)pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }


    private void getSpeedLimitCall(String token, String vehicle_id, String speed) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();


        Call<SpeedLimitResponse> call = apiInterface.speedLimit(vehicle_id, speed,token);
        call.enqueue(new Callback<SpeedLimitResponse>() {
            @Override
            public void onResponse(Call<SpeedLimitResponse> call, Response<SpeedLimitResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getSpeedLimitCall Response >>>>" + str_response);
                int responseCode  =response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            SpeedLimitResponse limitResponse = response.body();
                            int status = limitResponse.getStatus();
                            String massage = limitResponse.getMessage();
//                            Log.e(TAG, "status :" + status);
//                            Log.e(TAG, "massage :" + massage);
                            if (status==200) {
                                Toasty.success(mContext,"Successfully !, "+massage, Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(mContext, HomeActivity.class));
//                                getActivity().finish();
                            } else {
                                Log.e(TAG, "status Error "+status);
                            }
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SpeedLimitResponse> call, Throwable t) {
                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }


}
