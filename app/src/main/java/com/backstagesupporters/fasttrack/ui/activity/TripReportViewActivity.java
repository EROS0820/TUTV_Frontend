package com.backstagesupporters.fasttrack.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.adapters.TripReportViewAdapter;
import com.backstagesupporters.fasttrack.models.TripModel;
import com.backstagesupporters.fasttrack.responseClass.TripReportResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.utils.MySingleton;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TripReportViewActivity extends BaseActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title,tv_noData,tv_Download;
    private TextView tv_total,tv_from_date,tv_to_date,tv_total_KM;
    private LinearLayout ll_headDRButtom, ll_headDR;

    private String vehicle_id, vehicle_name,vehicle_no, distance,date;;
    private String message,total,from_date,to_date;

    private RecyclerView recyclerView;
    private TripReportViewAdapter adapter;
    private List<TripModel> tripModelList;

    // RoundingMode
    private static DecimalFormat decimalFormat = new DecimalFormat("##.####");

    ApiInterface apiInterface;
    ProgressDialog pd;
    String token="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_report_view);
        mContext= TripReportViewActivity.this;

//        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Using the Singleton and set data
        vehicle_name = MySingleton.getInstance().getData();
//        Log.e(TAG, " MySingleton getData vehicle_name : " + vehicle_name);


        Gson gson = new Gson();
        TripReportResponse tripReportResponse = gson.fromJson(getIntent().getStringExtra("myjson"), TripReportResponse.class);
        try {
            int status = tripReportResponse.getStatus();
            if (status==200) {
                message = tripReportResponse.getMessage();
                total = tripReportResponse.getTotal();
                from_date = tripReportResponse.getFromDate();
                to_date = tripReportResponse.getToDate();
//                Log.i(TAG,"Message : "+message);
//                Log.i(TAG,"total : "+total);
//                Log.i(TAG,"from_date : "+from_date);
//                Log.i(TAG,"to_date : "+to_date);

                /**
                 * "trip": 9,
                 *       "vehicle_no": "HRh23E1234",
                 *       "start_lat": "029.01132",
                 *       "start_long": "077.07653",
                 *       "end_lat": "029.01132",
                 *       "end_long": "077.07653",
                 *       "start_time": "2019-12-12 12:52:23",
                 *       "end_time": "2019-12-12 16:52:23",
                 *       "distance": "20 KM",
                 *       "date": "15-12-2019 14:20:06"
                 *
                 *
                 "trip_no": 2,
                 "distance": 0.06,
                 "start_latitude": "28.628788333333333",
                 "start_longitude": "77.27755333333333",
                 "start_date": "2020-02-21",
                 "start_time": "11:56:28",
                 "end_latitude": "28.62887611111111",
                 "end_longitude": "77.27771",
                 "end_date": "2020-02-21",
                 "end_time": "11:58:18"
                 */

                tripModelList = tripReportResponse.getData();
//                Log.e(TAG, "tripList Size:" + tripModelList.size());

                //data
                String vehicle_id, vehicle_no,vehicle_name;
                String  trip,date, start_latitude,start_longitude,start_date, start_time, end_latitude, end_longitude,end_time ;
                /*for (int i=0; i<tripModelList.size();i++){
//                  vehicle_no = tripModelList.get(i).getVehicleNo();
                    trip = String.valueOf(tripModelList.get(i).getTripNo());
                    distance = String.valueOf(tripModelList.get(i).getDistance());
                    start_latitude = tripModelList.get(i).getStartLatitude();
                    start_longitude = tripModelList.get(i).getStartLongitude();
                    start_date = tripModelList.get(i).getStartDate();
                    start_time = tripModelList.get(i).getStartTime();
                    end_latitude = tripModelList.get(i).getEndLatitude();
                    end_longitude = tripModelList.get(i).getEndLongitude();
//                    end_date = tripModelList.get(i).getDate();
                    end_time = tripModelList.get(i).getEndTime();
                    Log.i(TAG,"distance : "+distance);
                    Log.w(TAG,"start_latitude : "+start_latitude);
                }*/
            }
        }catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG,""+e.toString());
//            Toasty.error(mContext, "Error : "+e.toString(), Toast.LENGTH_SHORT).show();
        }

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
//        tv_tool_title.setText(getString(R.string.distance_report));
        tv_tool_title.setText(vehicle_name);
        iv_tool_back_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // total,from_date,to_date
        tv_from_date.setText("From Date: "+from_date);
        tv_to_date.setText("To Date: "+to_date);

        // System.out.println("double : " + df2.format(input));    //3.14
        float mTotal = Float.parseFloat(total);
        String myTotal = decimalFormat.format(mTotal);
//        Log.w(TAG,"Rounding Total: "+myTotal);
        tv_total_KM.setText(myTotal +" K.M.");

        // setVisibility
        if (tripModelList.isEmpty()){
            tv_noData.setVisibility(View.VISIBLE);
            ll_headDR.setVisibility(View.GONE);
        }else {
            tv_noData.setVisibility(View.GONE);
            ll_headDR.setVisibility(View.VISIBLE);
        }

        if (tripReportResponse==null){
            ll_headDR.setVisibility(View.GONE);
            ll_headDRButtom.setVisibility(View.GONE);
            tv_noData.setVisibility(View.VISIBLE);
        }

        // setAdapter
        setAdapter();
    }

    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);

//        adapter = new TripReportViewAdapter(mContext,tripReportList);
        adapter = new TripReportViewAdapter(mContext,tripModelList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        adapter.setClickListener(this); // Bind the listener

    }


    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        recyclerView = findViewById(R.id.recyclerDistanceReportView);

        tv_noData = findViewById(R.id.tv_noData);
        tv_total = findViewById(R.id.tv_total);
        tv_from_date = findViewById(R.id.tv_from_date);
        tv_to_date = findViewById(R.id.tv_to_date);
        tv_total_KM = findViewById(R.id.tv_total_KM);
        tv_Download = findViewById(R.id.tv_Download);
        ll_headDRButtom = findViewById(R.id.ll_headDRButtom);
        ll_headDR = findViewById(R.id.ll_headDR);

        tv_Download.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, TripReportActivity.class));
        finish();
    }

}
