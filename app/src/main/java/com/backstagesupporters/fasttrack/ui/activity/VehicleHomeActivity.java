package com.backstagesupporters.fasttrack.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.adapters.VehicleHomeAdapter;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VehicleHomeActivity extends BaseActivity implements ItemClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private TextView tv_tool_title;
    private RecyclerView recyclerView;
    private VehicleHomeAdapter adapter;

    private String vehicle_id, vehicle_name,vehicle_type, vehicle_no, vehicle_color;
    private List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();

    private ApiInterface apiInterface;
    private ProgressDialog pd;
    private String token="";


    private boolean dialogStatus = false, doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_home);
        mContext = VehicleHomeActivity.this;

        findViewById();

        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText("Home Vehicle");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getVehicleCall(token);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

    }

    private void findViewById() {
        recyclerView = findViewById(R.id.recyclerView_vehicle);
        tv_tool_title = findViewById(R.id.tv_title);
    }

    @Override
    public void onStop() {
        super.onStop();
/*
        if (!vehicleArrayList.isEmpty()){
            vehicleArrayList.clear();
        }*/
    }


    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);
        if (!vehicleArrayList.isEmpty()){
            adapter = new VehicleHomeAdapter(mContext,vehicleArrayList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setClickListener(this); // Bind the listener
        }else {
            Toasty.error(mContext, getString(R.string.err_data_not_found), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
        Vehicle model = vehicleArrayList.get(position);
        vehicle_id = String.valueOf(model.getVehicleId());
        vehicle_name = model.getVehicleName();
        vehicle_no = model.getVehicleNo();
        vehicle_type = model.getVehicleType();
        vehicle_color = model.getColor();

        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_ID,vehicle_id);
        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_NUMBER,vehicle_no);
        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_TYPE, vehicle_type);
        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_COLOR, vehicle_color);

        Intent i = new Intent(mContext, HomeActivity.class);
        i.putExtra("vehicle_id", vehicle_id);
        i.putExtra("vehicle_no", vehicle_no);
        i.putExtra("vehicle_type", vehicle_type);
        i.putExtra("vehicle_color", vehicle_color);
        startActivity(i);

//        Bundle bundle = new Bundle();
//        bundle.putString("edttext", "From Activity");
//        bundle.putString("vehicle_id", vehicle_id);
//        bundle.putString("vehicle_no", vehicle_no);
//        bundle.putString("vehicle_type", vehicle_type);
//        // set Fragmentclass Arguments
//        HomeDashboardFragmentMy fragobj = new HomeDashboardFragmentMy();
//        fragobj.setArguments(bundle);
    }


    //================ Api Call ============================

    // showVehicles
    private void getVehicleCall(final String token) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<VehiclesResponse> call = apiInterface.showVehicles(token);
        call.enqueue(new Callback<VehiclesResponse>() {
            @Override
            public void onResponse(Call<VehiclesResponse> call, Response<VehiclesResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.w(TAG, " Response >>>>" + str_response);
                int  responseCode  = response.code();
                try {
                    if (response.isSuccessful() && responseCode ==200){
                        VehiclesResponse vehiclesResponse = response.body();
                        assert vehiclesResponse != null;
                        int status = vehiclesResponse.getStatus();
                        if (status== 200) {
                            vehicleArrayList  = vehiclesResponse.getVehicles();
//                            Log.e(TAG, "listVehicle  size:" + vehicleArrayList.size());

                            // setAdapter
                            setAdapter();
                        } else {
                            Log.e(TAG, "status Error "+status);
//                                Toasty.error(mContext, "Status Code :"+status, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,""+e.toString());
//                    Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
                pd.dismiss();
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }



    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>1){
            super.onBackPressed();
        }else{
//            finish();
            doubleBackPressLogic();
        }
    }


    //====End Double tab back press logic
    private void doubleBackPressLogic() {
        if (doubleBackToExitPressedOnce) {
            if (dialogStatus) {
                moveTaskToBack(true);
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toasty.info(mContext, "Please click back again to exit !!", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(mRunnable, 1000);
    }




}
