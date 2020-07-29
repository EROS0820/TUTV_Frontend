package com.backstagesupporters.fasttrack.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.Complaint;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.UserStatusAccount;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintActivity extends AppCompatActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private TextInputEditText  edt_name, edt_mobileno,edit_vehicleNumber;
    private EditText edit_support;
    private Spinner vehicleSpinner;
    private TextView tv_submit;
    String token, userId;
    private String userType, name,phone_number,complaint;
    String vehicle_id, vehicle_name,vehicle_type,vehicle_no,vehicle_image;

    List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
    private ArrayList myVehicleListSpinner = new ArrayList();

    private ProgressDialog pd;
    //=Interface Declaration
    ApiInterface apiInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        mContext=ComplaintActivity.this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userType = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_TYPE);
        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);

//        Log.e(TAG, "token :" + token);
//        Log.e(TAG, "userType :" + userType);

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.complaint));
        iv_tool_back_left.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getVehicleCall(token, userId);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

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
        edt_name = findViewById(R.id.edt_name);
        edt_mobileno = findViewById(R.id.edt_mobileno);
//        edit_vehicleNumber = findViewById(R.id.edit_vehicleNumber);
        vehicleSpinner =findViewById(R.id.vehicleSpinner);
        edit_support = findViewById(R.id.edit_support);

        tv_submit = findViewById(R.id.tv_submit);
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

        switch (v.getId()) {
            case R.id.tv_submit:
                doValidation();
                break;

            case R.id.iv_tool_back_left:
                onBackPressed();
                break;
        }
    }


    private void setVehicleSpinner() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, myVehicleListSpinner);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
        vehicleSpinner.setAdapter(spinnerArrayAdapter);

        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                int mySelectedVehicleId;
                if(selectedItem.equals(getString(R.string.please_select_vehicle_type))) {
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
//                Log.i(TAG,getString(R.string.please_select_vehicle_type)+" : "+parent.toString());
            }
        });
    }


    private void doValidation() {
        name = edt_name.getText().toString().trim();
        phone_number = edt_mobileno.getText().toString().trim();
//        vehicleNumber = edit_vehicleNumber.getText().toString().trim();
        vehicle_no = vehicleSpinner.getSelectedItem().toString().trim();
        complaint = edit_support.getText().toString().trim();

        if (name.equalsIgnoreCase("")) {
            edt_name.setError(getString(R.string.pleaseEnterFullName));
            edt_name.requestFocus();
        }else if (vehicle_no.isEmpty()){
            Toasty.error(mContext,"Plese Select the Vehicle",Toast.LENGTH_SHORT).show();

        }else if (phone_number.isEmpty()){
            edt_mobileno.setError(getString(R.string.err_msg_mobile));
            edt_mobileno.requestFocus();
        }else if (edt_mobileno.length() < 9) {
            edt_mobileno.setError(getString(R.string.err_msg_mobile_too_short));
            edt_mobileno.requestFocus();
        } else if (!Patterns.PHONE.matcher(phone_number).matches()){
            edt_mobileno.setError(getString(R.string.err_msg_mobile_invalid));
            edt_mobileno.requestFocus();
        }else if (phone_number.length()>12){
            edt_mobileno.setError(getString(R.string.err_msg_mobile_too_long));
            edt_mobileno.requestFocus();
        }else if (complaint.isEmpty()){
            edit_support.setError(getString(R.string.pleaseComplaint));
            edit_support.requestFocus();
        } else {

            // ========== Api Call =============
            if (CheckNetwork.isNetworkAvailable(mContext)) {
                getComplaintCall(token,complaint,name,phone_number,vehicle_no);
                Toasty.success(mContext,"Successfully !!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //================ Api Call ============================

    // showVehicles
    private void getVehicleCall(final String token, String user_id) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();
        /**
         * GET
         * http://3.135.158.46/api/show_vehicles?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8zLjEzNS4xNTguNDZcL2FwaVwvbG9naW4iLCJpYXQiOjE1NzYyMjg5MDgsImV4cCI6MTU3NjIzMjUwOCwibmJmIjoxNTc2MjI4OTA4LCJqdGkiOiI5TW1ybFdlRGl6aTdTUTY3Iiwic3ViIjoxLCJwcnYiOiI4N2UwYWYxZWY5ZmQxNTgxMmZkZWM5NzE1M2ExNGUwYjA0NzU0NmFhIn0.eTTp34zaQ08FBJHjtIhGtwQ68lfRuakN7ti3uuVQFkA
         */

        Call<VehiclesResponse> call =  apiInterface.showVehiclesUserId(user_id);
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
                        if (response.isSuccessful()) {
                            VehiclesResponse vehiclesResponse = response.body();
                            assert vehiclesResponse != null;
                            int status = vehiclesResponse.getStatus();
                            if (status== 200) {
                                vehicleArrayList  = vehiclesResponse.getVehicles();
//                                Log.e(TAG, "VehiclesResponse-listVehicle :" + vehicleArrayList.toString());
                                for (int i=0; i<vehicleArrayList.size();i++){
                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
//                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleName() +" "+ vehicleArrayList.get(i).getVehicleNo());
                                }
                                //vehicleSpinner
                                setVehicleSpinner();
                            } else {
                                Toasty.error(mContext, "Status Code :"+status, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,""+e.toString());
//                    Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
                if (pd!=null)pd.dismiss();
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }

    // complaint
    private void getComplaintCall(String token, String complaint, String name, String mobile, String vehicle_no) {
        Complaint complaintModel = new Complaint(token,complaint, name, mobile,vehicle_no);
//        Call<LoginResponse> call = apiInterface.supportQuery2(token,support, name, phone_number);
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();

        /**
         * POST
         * request to Support
         * http://138.197.210.36/api/complaint
         * [{{"key":"token"},{"key":"complaint"},{"key":"name"},{"key":"phone"}, {"key":"vehicle_no"}]
         */
        Call<JsonElement> call =  apiInterface.complaint(complaintModel);
//        Call<JsonElement> call =  apiInterface.complaint(supportModel);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.w(TAG, "getComplaintCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                ComplaintResponse complaintResponse = response.body();
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(str_response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("200")){
                            JSONObject data = jsonObject.getJSONObject("complaint");
                            String user_id = data.getString("user_id");
                            String complaint = data.getString("complaint");
                            String mobile = data.getString("mobile");
                            String vehicle_no = data.getString("vehicle_no");
                            String name = data.getString("name");
                            String id = data.getString("id");

//                            Log.e(TAG,"user_id : "+user_id);
//                            Log.e(TAG,"complaint : "+complaint);
//                            Log.e(TAG,"mobile : "+mobile);
//                            Log.e(TAG,"name : "+name);

                            startActivity(new Intent(mContext, HomeActivity.class));
                            finish();
//                            Log.i(TAG,"called user");

                           // Toasty.success(mContext, "Thanks for submitting the Complaint", Toast.LENGTH_SHORT).show();
                        }

//                        Log.e(TAG,"JSONObject message-"+message);
//                        Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();

                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,""+e.toString());
//                    Toasty.error(mContext, "Massage : "+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (pd!=null)pd.dismiss();
//                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }



}
