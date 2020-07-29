package com.backstagesupporters.fasttrack.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.adapters.DistanceReportViewAdapter;
import com.backstagesupporters.fasttrack.ui.adapters.DistanceReportViewAllAdapter;

import com.backstagesupporters.fasttrack.models.DistanceReport;
import com.backstagesupporters.fasttrack.responseClass.DistanceReportResponse;
import com.backstagesupporters.fasttrack.utils.MySingleton;
import com.backstagesupporters.fasttrack.utils.fileDownload.DownloadFile;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class DistanceReportViewActivity extends BaseActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    public Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title,tv_noData,tv_Download;
    private TextView tv_total,tv_from_date,tv_to_date,tv_total_KM;

    private RecyclerView recyclerView;
    private DistanceReportViewAdapter adapter;
    private DistanceReportViewAllAdapter adapterAll;
    private List<DistanceReport> distanceList;

    private String vehicle_id, vehicle_name,vehicle_no, distance,date, urlReport;;
    private String message,total,from_date,to_date;

    // RoundingMode
    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_report_view);
        mContext=DistanceReportViewActivity.this;

        // Using the Singleton and set data
        vehicle_name = MySingleton.getInstance().getData();
//        Log.e(TAG, " MySingleton getData vehicle_name : " + vehicle_name);

        Gson gson = new Gson();
        DistanceReportResponse distanceReportResponse = gson.fromJson(getIntent().getStringExtra("myjson"), DistanceReportResponse.class);
//        DistanceReportResponse distanceReportResponse = response.body();
        try{
            int status = distanceReportResponse.getStatus();
            if (status==200) {
                message = distanceReportResponse.getMessage();
                total = distanceReportResponse.getTotal();
                from_date = distanceReportResponse.getFromDate();
                to_date = distanceReportResponse.getToDate();
                urlReport = distanceReportResponse.getReport();
//                Log.i(TAG, "distanceReportResponse Message : " + message);
//                Log.i(TAG, "distanceReportResponse from_date : " + from_date);
//                Log.i(TAG, "distanceReportResponse to_date : " + to_date);
//                Log.i(TAG, "distanceReportResponse total : " + total);
                /**
                 *"vehicle_no": "DLh23E1234",
                 *  "distance": "20 KM",
                 *  "date": "30-11-2019 13:59:43"
                 */
                String vehicle_no, distance, date;
                //data
                distanceList = distanceReportResponse.getData();
                for (int i = 0; i < distanceList.size(); i++) {
                    vehicle_no = distanceList.get(i).getVehicleNo();
                    distance = distanceList.get(i).getDistance();
                    date = distanceList.get(i).getDate();
//                    Log.i(TAG, "distanceList vehicle_no : " + vehicle_no);
//                    Log.i(TAG, "distanceList distance : " + distance);
//                    Log.i(TAG, "distanceList date : " + date);
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG,""+e.toString());
        }


        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
//        tv_tool_title.setText(getString(R.string.distance_report));
        tv_tool_title.setText(vehicle_name);

        iv_tool_back_left.setOnClickListener(v -> {
            onBackPressed();
        });

        if (distanceList.isEmpty()){
            tv_noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            tv_noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }


        // total,from_date,to_date
        if (!from_date.isEmpty() && !total.isEmpty() && !to_date.isEmpty()){
            tv_from_date.setText("From Date :"+from_date);
            tv_to_date.setText("To Date :"+to_date);

            // System.out.println("double : " + df2.format(input));    //3.14
            float mTotal = Float.parseFloat(total);
            String myTotal = decimalFormat.format(mTotal);
//            Log.w(TAG,"Rounding Total: "+myTotal);
            tv_total_KM.setText(myTotal +" K.M.");

        }else {
            Toasty.warning(mContext,"Data of Distance List is Empty ");
        }

        // setAdapter
        setAdapter();
    }

    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);

        if (vehicle_name.equalsIgnoreCase("All Vehicle")){
            adapterAll = new DistanceReportViewAllAdapter(mContext,distanceList);
            recyclerView.setAdapter(adapterAll);
            adapterAll.notifyDataSetChanged();
//        adapter.setClickListener(this); // Bind the listener
        }else {
            adapter = new DistanceReportViewAdapter(mContext,distanceList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
//        adapter.setClickListener(this); // Bind the listener
        }
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
        tv_Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date();
                String mFileName= "FastTrack_"+formatter.format(date);
//                Log.i(TAG, "mFileName: " + mFileName);
//                Log.i(TAG, "urlReport: " + urlReport);
                new DownloadFile().execute(urlReport, mFileName+".pdf");
//                new DownloadTaskFile(mContext, urlReport);

                Toast.makeText(mContext, "Report file is downloaded in the FastTrack folder", Toast.LENGTH_SHORT).show();

            }
        });
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, DistanceReportActivity.class));
        finish();
    }


}
