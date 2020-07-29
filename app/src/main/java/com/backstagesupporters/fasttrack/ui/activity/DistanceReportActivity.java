package com.backstagesupporters.fasttrack.ui.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.DistanceReportResponse;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.MySingleton;
import com.backstagesupporters.fasttrack.utils.UserStatusAccount;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistanceReportActivity extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private TextView tv_dateFrom,tv_dateTo,tv_submit ;
    private RelativeLayout rl_dateFrom,rl_dateTo;
    private Spinner vehicleSpinner;

    int mYear, mMonth, mDay;
    String dateFrom,dateTo;
    String vehicle_id, vehicle_name;
    private String serverDateFormat = "yyyy-MM-dd";
    private String localDateFormat = "dd/MM/yyyy";

    List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
    private ArrayList<String> myVehicleListSpinner = new ArrayList<String>();

    ApiInterface apiInterface;
    ProgressDialog pd;
    String token, userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_report);
        mContext=DistanceReportActivity.this;
//        Log.i(TAG,"called onCreate");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);
//        Log.e(TAG, "token :" + token);

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.distance_report));

        iv_tool_back_left.setOnClickListener(this);
        rl_dateFrom.setOnClickListener(this);
        rl_dateTo.setOnClickListener(this);
        tv_submit.setOnClickListener(this);


        // ======= API Call ==========
        if (vehicleArrayList.isEmpty()){
            if (CheckNetwork.isNetworkAvailable(mContext)) {
                getVehicleCall(token, userId);
            } else {
                Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }

        // DateTo": "4/7/2000"
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(date);
        System.out.println("Today : " + today);

        tv_dateTo.setText(today);
        tv_dateFrom.setText(today);

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
        vehicleSpinner =findViewById(R.id.vehicleSpinner);
        tv_dateFrom = findViewById(R.id.tv_dateFrom);
        rl_dateFrom = findViewById(R.id.rl_dateFrom);
        rl_dateTo = findViewById(R.id.rl_dateTo);
        tv_dateTo = findViewById(R.id.tv_dateTo);
        tv_submit = findViewById(R.id.tv_submit);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_tool_back_left:
                onBackPressed();
                break;

            case R.id.rl_dateFrom:
                geDateFrom();
                break;

            case R.id.rl_dateTo:
                getDateTo();
                break;

            case R.id.tv_submit:
                doValidation();
                break;
        }
    }





    // DateTo": "4/7/2000"
    private void getDateTo() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                tv_dateTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                Log.e(TAG, "tv_dateTo :" + tv_dateTo.getText().toString());
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    // dateFrom": "4/7/2000"
    private void geDateFrom() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                tv_dateFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                Log.e(TAG, "tv_dateFrom :" + tv_dateFrom.getText().toString());
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void setVehicleSpinner() {
//        Log.e(TAG, "ListVehicle size :" + myVehicleListSpinner.size());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext,R.layout.spinner_item, myVehicleListSpinner);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
        vehicleSpinner.setAdapter(spinnerArrayAdapter);
        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
//                ((TextView) parent.getChildAt(0)).setTextSize(5);
                String selectedItem = parent.getItemAtPosition(position).toString();
                int mySelectedVehicleId;
                if(selectedItem.equals(getString(R.string.please_select_vehicle_type))) {
//                    Log.i(TAG,"selectedItem "+selectedItem);
                }else {
                    vehicle_name = parent.getItemAtPosition(position).toString();
                    mySelectedVehicleId = parent.getSelectedItemPosition() +1;
                    vehicle_id = String.valueOf(vehicleArrayList.get(position).getVehicleId());

//                    Log.i(TAG,"selectedItem myId "+mySelectedVehicleId);
//                    Log.i(TAG,"vehicle_id : "+vehicle_id);
//                    Log.i(TAG,"selectedItem vehicle_name "+vehicle_name);

                    // Using the Singleton and set data
                    MySingleton.getInstance().setData(vehicle_name);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG,"selectedItem parent "+parent.toString());
            }
        });
    }

    public static String formatDate (String date, String initDateFormat, String endDateFormat)  {
        Date initDate = null;
        String parsedDate="";
        try {
            initDate = new SimpleDateFormat(initDateFormat).parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
            parsedDate = formatter.format(initDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    private void doValidation() {
        String vehicle = vehicleSpinner.getSelectedItem().toString().trim();
        dateFrom = tv_dateFrom.getText().toString().trim();
        dateTo = tv_dateTo.getText().toString().trim();

        if (vehicle.equalsIgnoreCase("")) {
            Toasty.error(mContext,getString(R.string.please_select_vehicle1),Toast.LENGTH_SHORT).show();
        } if (vehicle.equalsIgnoreCase(getString(R.string.all_Vehicle))) {
            Toasty.error(mContext,getString(R.string.please_select_vehicle),Toast.LENGTH_SHORT).show();
        }else if (dateFrom.isEmpty()){
            tv_dateFrom.setError(getString(R.string.select_date));
            tv_dateFrom.requestFocus();
        }else if (dateTo.isEmpty()){
            tv_dateTo.setError(getString(R.string.select_date));
            tv_dateTo.requestFocus();
        } else {
            // ========== Api Call =============
            if (CheckNetwork.isNetworkAvailable(mContext)) {
                String serverFromDate = formatDate(dateFrom,localDateFormat,serverDateFormat); // Date 2020-01-05
                String serverToDate = formatDate(dateTo,localDateFormat,serverDateFormat);

//                Log.i(TAG, "MySingleton  vehicle_name : " +  MySingleton.getInstance().getData());
                getDistanceReportCall(token,serverFromDate,serverToDate,vehicle_id);

            } else {
                Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //================ Api Call ============================

    private void getDistanceReportCall(String token, String dateFrom, String dateTo, String vehicle_id) {
//        Log.i(TAG,"serverFromDate : "+dateFrom);
//        Log.i(TAG,"serverToDate : "+dateTo);
//        Log.i(TAG,"vehicleId : "+vehicle_id);

        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();

        /**
         * GET
         *http://3.135.158.46/api/distance_report?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8zLjEzNS4xNTguNDZcL2FwaVwvbG9naW4iLCJpYXQiOjE1NzYzMDI0MzQsImV4cCI6MTU3NjMwNjAzNCwibmJmIjoxNTc2MzAyNDM0LCJqdGkiOiJuYVN4RWVlM0ZpZTg2cnQ2Iiwic3ViIjoxLCJwcnYiOiI4N2UwYWYxZWY5ZmQxNTgxMmZkZWM5NzE1M2ExNGUwYjA0NzU0NmFhIn0.L5RB7ZpbNlKnhkiCZVAG00vUFg4LajLeMt70ZGoFHpw&from_date=1/12/2019&to_date=7/12/2019&vehicle_id=1
         *
         * // token, from_date,to_date,vehicle_id -> distanceReport
         */
        Call<DistanceReportResponse> call = apiInterface.distanceReport(token, dateFrom,dateTo,vehicle_id);
        call.enqueue(new Callback<DistanceReportResponse>() {
            @Override
            public void onResponse(Call<DistanceReportResponse> call, Response<DistanceReportResponse> response) {
               if (pd!=null)pd.dismiss();

                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getDistanceReportCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    String message,total,from_date,to_date;
                    if (responseCode ==200){
                        // DistanceReportResponse
                        DistanceReportResponse distanceReportResponse = response.body();
                        assert distanceReportResponse != null;
                        int status = distanceReportResponse.getStatus();
                        if (status==200){
                            message = distanceReportResponse.getMessage();
                            total = distanceReportResponse.getTotal();
                            from_date = distanceReportResponse.getFromDate();
                            to_date = distanceReportResponse.getToDate();
//                            Log.i(TAG,"distanceReportResponse Message : "+message);
//                            Log.i(TAG,"distanceReportResponse from_date : "+from_date);
//                            Log.i(TAG,"distanceReportResponse to_date : "+to_date);
//                            Log.i(TAG,"distanceReportResponse total : "+total);

                            Intent intent = new Intent(mContext, DistanceReportViewActivity.class);
                            intent.putExtra("myjson", str_response);
//                            intent.putExtra("vehicle_name", vehicle_name);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            finish();
                        }else {
//                            Toasty.error(mContext, "Response code :"+status, Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Response code :"+status);
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    // err_unauthorized_token_expired
//                    Toasty.error(mContext, "Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DistanceReportResponse> call, Throwable t) {
                if (pd!=null)pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }


    // showVehicles
    private void getVehicleCall(final String token, String user_id) {
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
//                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleName() +" "+ vehicleArrayList.get(i).getVehicleNo());
                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
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
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }

}
