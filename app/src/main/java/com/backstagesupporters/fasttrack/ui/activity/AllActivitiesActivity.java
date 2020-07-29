package com.backstagesupporters.fasttrack.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.adapters.AllActivityAdapter;
import com.backstagesupporters.fasttrack.models.CarAllActivity;
import com.backstagesupporters.fasttrack.responseClass.CarActivityResponse;
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

public class AllActivitiesActivity extends BaseActivity implements ItemClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title,tv_noData;

    private RecyclerView recyclerView;
    private AllActivityAdapter adapter;
    List<CarAllActivity> carAllActivityList = new ArrayList<CarAllActivity>();

    ApiInterface apiInterface;
    ProgressDialog pd;
    String token="", vehicle_id ="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_activities);
        mContext=AllActivitiesActivity.this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        vehicle_id = AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_ID);

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.all_activity1));
        iv_tool_back_left.setOnClickListener(v -> {
            onBackPressed();
        });

        //token, vehicle_id, status
        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
//            getCarAllActivityCall(token, vehicle_id, status);
            getCarAllActivityCall(token, vehicle_id);
        } else {
            Toasty.warning(mContext, mContext.getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }
    }


    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        recyclerView = findViewById(R.id.recyclerViewAllActivities);
        tv_noData = findViewById(R.id.tv_noData);
    }

    private void setAdapter() {
//        Log.w(TAG,"setAdapter carAllActivityList size :"+carAllActivityList.size());
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);
        if (!carAllActivityList.isEmpty()){
            adapter = new AllActivityAdapter(mContext,carAllActivityList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setClickListener(this); // Bind the listener
        }else {
            if (carAllActivityList.isEmpty()){
                tv_noData.setText("Notification data can't find from server database ");
                tv_noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                tv_noData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }




    private void getCarAllActivityCall(String token, String vehicle_id) {
//        Log.e(TAG,"getCarAllActivityCall token-"+token);
//        Log.e(TAG,"getCarAllActivityCall vehicle_id-"+vehicle_id);

        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<CarActivityResponse> call = apiInterface.car_activity2(token, vehicle_id);
//        Call<CarActivityResponse> call = apiInterface.car_activity( vehicle_id, token);

        call.enqueue(new Callback<CarActivityResponse>() {
            @Override
            public void onResponse(Call<CarActivityResponse> call, Response<CarActivityResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.w(TAG, "getCarAllActivityCall Response >>>>" + str_response);
                int responseCode  =  response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (response.isSuccessful()) {
                        CarActivityResponse carActivityResponse = response.body();
                        int status = carActivityResponse.getStatus();
                        String message = carActivityResponse.getMessage();
//                        Log.w(TAG, "success :" + status);
//                        Log.w(TAG, "message :" + message);
                        if (status==200) {
                            carAllActivityList = carActivityResponse.getCaractivity();
                            // set Adapter
                            setAdapter();
                        } else {
                            Log.e(TAG, "status Error "+status);
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
            public void onFailure(Call<CarActivityResponse> call, Throwable t) {
                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }


    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
//        CarAllActivity model = carAllActivityList.get(position);
//        Log.i(TAG,"Welcome - "+model.getTitle());
//        Intent intent = new Intent(mContext, HomeActivity.class);
////        i.putExtra("city", model.name);
////        i.putExtra("desc", model.description);
////        i.putExtra("image", model.imageName);
//        startActivity(intent);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }


}
