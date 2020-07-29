package com.backstagesupporters.fasttrack.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.SpinnerListener;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.SubUser2;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSubUser extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    TextInputEditText  edt_name,edt_email,edt_password,edt_mobile;
    private Spinner vehicleSpinner;
    private MultiSpinnerSearch searchMultiSpinnerUnlimited;
    TextView tv_add,tv_addList;
    List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
    private ArrayList myVehicleListSpinner = new ArrayList();
    private ArrayList mVehicleListID = new ArrayList();

    String vehicle_id, vehicle_brand,vehicle_name,vehicle_type,vehicle_no,vehicle_image;
    String name,email,password,mobile;
    String myVehicleId ="";
    String token, userId;

    String expression = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(.{8,15})$";

    private ProgressDialog pd;
    //=Interface Declaration
    ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_user);
        mContext=AddSubUser.this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);
//        Log.e(TAG,"token-"+token);

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.add_sub_user));


        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getVehicleCall(token, userId);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

        iv_tool_back_left.setOnClickListener(this);
        tv_add.setOnClickListener(this);


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
        vehicleSpinner = findViewById(R.id.vehicleSpinner);
        searchMultiSpinnerUnlimited = findViewById(R.id.searchMultiSpinnerUnlimited);

        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_mobile = findViewById(R.id.edt_mobile);
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
                break;
        }

    }


    private void doValidation() {
        name =edt_name.getText().toString().trim();
        email = edt_email.getText().toString().trim();
        password =edt_password.getText().toString().trim();
        mobile = edt_mobile.getText().toString().trim();

        // TODO: 3/31/2020
//        vehicle_name =vehicleSpinner.getSelectedItem().toString().trim();
        myVehicleId= getVehicleIdFromList(mVehicleListID);
//        Log.e(TAG, "mVehicleListID myVehicleId: "+myVehicleId);

        if (name.equalsIgnoreCase("")) {
            edt_name.setError("Please Enter User Name");
            edt_name.requestFocus();
        } else if (email.equalsIgnoreCase("")) {
            edt_email.setError("Please Enter Username");
            edt_email.requestFocus();
        } else if (mobile.equalsIgnoreCase("")) {
            edt_mobile.setError("Please Enter Mobile Number");
            edt_mobile.requestFocus();
        } else if (mobile.length() < 9) {
            edt_mobile.setError("Please Enter 10 Digit Number");
            edt_mobile.requestFocus();
        } else if (password.equalsIgnoreCase("")) {
            edt_password.setError("Please Enter Password ");
            edt_password.requestFocus();
        } else if (password.length() < 4) {
            edt_password.setError("Entered Password Too Short");
            edt_password.requestFocus();
        } if (myVehicleId.equalsIgnoreCase("")) {

            Toasty.error(mContext,getString(R.string.please_select_vehicle),Toast.LENGTH_SHORT).show();
        } else {

            // ======= API Call ==========
            if (CheckNetwork.isNetworkAvailable(mContext)) {

               // registerUser(name, email, mobile, password);
                // SubUser2(token, name, email, password, mobile, vehicle_id)
                addSubUserCall(token,name,email,password,mobile,myVehicleId);

            } else {
                Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // TODO: 3/31/2020
    private String getVehicleIdFromList(ArrayList vListId) {
        String mId ="";
        String mId2 ="";
        int vSize = vListId.size();
//        Log.e(TAG, "mVehicleListID vSize: "+vSize);
        for (int i=0; i< vSize; i++) {
            if (i < vSize - 1) {
                mId += vListId.get(i).toString() + ",";
            }else {
                mId += vListId.get(i).toString();
            }
        }
//        Log.e(TAG, "mVehicleListID mId: "+mId);

        // TextUtils - Android
        mId2 = TextUtils.join(",", vListId);
//        Log.i(TAG,"mVehicleListID mId2: "+ mId2);

        return mId2;
    }


    //================ Api Call ============================

    private void addSubUserCall(String token, String name, String email, String password, String mobile, String vId) {
//        Log.w(TAG, "addSubUserCall vId: "+vId);
        // SubUser2(token, name, email, password, mobile, vehicle_id)
        SubUser2 subUser = new SubUser2(token,name,email,password,mobile, vId);
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();


        Call<JsonElement> call =  apiInterface.addSubUser(subUser);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (pd!=null)pd.dismiss();
                int responseCode = response.code();
                String str_response = new Gson().toJson(response.body());
                String str_response_message = response.message();
//                Log.e(TAG, "addSubUserCall Response >>>>" + str_response);
//                Log.e(TAG, "Response code :" + responseCode);

                try {
                    if (responseCode == 200) {
                        String s1 = "{\"success\":false,\"error\":{\"email\":[\"The email has already been taken.\"]}}";

                        JSONObject jsonObject = new JSONObject(str_response);
//                        Log.e(TAG,"Error jsonObject : "+jsonObject.toString());

                        if (str_response.equalsIgnoreCase(s1)){
                            boolean success = Boolean.parseBoolean(jsonObject.getString("success"));
//                            Log.e(TAG,"Error success : "+success);
                            if (!success){
                                JSONObject jsonError = jsonObject.getJSONObject("error");
                                JSONArray jsonArray = jsonError.getJSONArray("email");
                                String errorMassage =jsonArray.get(0).toString();
//                                Log.e(TAG,"error Massage : "+errorMassage);

                                Toasty.error(mContext, errorMassage, Toast.LENGTH_SHORT).show();
                            }else {
//                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                Log.e(TAG,  getString(R.string.err_unknown_error));
                            }
                        }else {
                            if (response.isSuccessful()) {
                                int status = Integer.parseInt(jsonObject.getString("status"));
                                String message = jsonObject.getString("message");
                                if (status==200){
                                    JSONObject data = jsonObject.getJSONObject("sub_user");
                                    String email = data.getString("email");
                                    String name = data.getString("name");
//                                    Log.e(TAG,"email - "+email);
//                                    Log.e(TAG,"name- "+name);

                                    startActivity(new Intent(mContext, SubUserActivity.class));
//                                    Log.e(TAG,"Message : "+message);
                                }
                            }
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,""+e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (pd!=null)pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }


    private void setVehicleSpinner() {
//        Log.e(TAG, "VehiclesResponse-listVehicle size:" + vehicleArrayList.size());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext,R.layout.spinner_item, myVehicleListSpinner);
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
//                Log.i(TAG,"selectedItem "+parent.toString());
            }
        });
    }


    private void setSearchMultiSpinnerUnlimited() {
//        Log.e(TAG, "VehiclesResponse-listVehicle size:" + vehicleArrayList.size());
//        final List<String> list = Arrays.asList(getResources().getStringArray(R.array.sports_array));
        final List<KeyPairBoolData> listArray0 = new ArrayList<>();
        for (int i = 0; i < vehicleArrayList.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i);
//            h.setName(vehicleArrayList.get(i).getVehicleNo() + " " +vehicleArrayList.get(i).getVehicleType());
            h.setName(vehicleArrayList.get(i).getVehicleNo());
            h.setSelected(false);
            listArray0.add(h);
        }

        searchMultiSpinnerUnlimited.setEmptyTitle("Data Found!");
        searchMultiSpinnerUnlimited.setSearchHint("Find Data");
        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {

                        mVehicleListID.add(vehicleArrayList.get(i).getVehicleId());  // vehicle_id

//                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });
    }
    // showVehicles
    private void getVehicleCall(final String token, final String user_id) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

//        Call<VehiclesResponse> call = apiInterface.showVehicles(token);
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
                        if (response.isSuccessful()) {
                            VehiclesResponse vehiclesResponse = response.body();
                            assert vehiclesResponse != null;
                            int status = vehiclesResponse.getStatus();
                            if (status== 200) {
                                vehicleArrayList  = vehiclesResponse.getVehicles();
                                for (int i=0; i<vehicleArrayList.size();i++){
                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
                                }
                                //vehicleSpinner
//                                setVehicleSpinner();
                                setSearchMultiSpinnerUnlimited();
                            } else {
                                Log.e(TAG, "Status Code :"+status);
                            }
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,""+e.toString());
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
                if (pd!=null)pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(mContext, SubUserActivity.class));
        finish();
    }



}
