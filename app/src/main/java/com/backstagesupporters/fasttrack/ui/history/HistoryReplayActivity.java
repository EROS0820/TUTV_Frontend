package com.backstagesupporters.fasttrack.ui.history;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.HistoryReplyResponse;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.CustomTimePickerDialog;
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

public class HistoryReplayActivity extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private TextView tv_date,tv_start_time,tv_end_time, tv_dateFrom,tv_dateTo,tv_submit;
    private RelativeLayout rl_dateFrom,rl_dateTo;
    private RelativeLayout rl_date,rl_start_time,rl_end_time;
    private Spinner vehicleSpinner;

    String dateFrom,dateTo;
    int mYear, mMonth, mDay;
    String startTime = "", endTime="",date="";
    String vehicle_id, vehicle_brand,vehicle_name,vehicle_type,distance, vehicle_no,vehicle_image;
    private String colorCar="", car_color="",car_position="";
    private String speed, gps_length,satellites,direction,alarm, signal_strength, position_color;

    List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
    private ArrayList myVehicleListSpinner = new ArrayList();

    ApiInterface apiInterface;
    ProgressDialog pd;
    String token, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_replay);
        mContext=HistoryReplayActivity.this;


        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);


        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.HistoryReplay));
        iv_tool_back_left.setOnClickListener(this);
        rl_date.setOnClickListener(this);
        rl_start_time.setOnClickListener(this);
        rl_end_time.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        rl_dateFrom.setOnClickListener(this);
        rl_dateTo.setOnClickListener(this);

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getVehicleCall(token, userId);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

        // DateTo": "4/7/2000"
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(date);
        System.out.println("Today : " + today);

        tv_date.setText(today);
        tv_dateTo.setText(today);
        tv_dateFrom.setText(today);

    }

    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        vehicleSpinner =findViewById(R.id.vehicleSpinner);
        tv_date = findViewById(R.id.tv_date);
        rl_date = findViewById(R.id.rl_date);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_end_time = findViewById(R.id.rl_end_time);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_submit = findViewById(R.id.tv_submit);

        tv_dateFrom = findViewById(R.id.tv_dateFrom);
        tv_dateTo = findViewById(R.id.tv_dateTo);
        rl_dateFrom = findViewById(R.id.rl_dateFrom);
        rl_dateTo = findViewById(R.id.rl_dateTo);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.e(TAG,"onRestart");
        String strHistoryReplyResponseAppPreferences = AppPreferences.loadPreferences(mContext, VariablesConstant.HISTORY_REPLAY_FILE);
//        Log.w(TAG,"strHistoryReplyResponseAppPreferences: "+strHistoryReplyResponseAppPreferences);
        AppPreferences.savePreferences(mContext,VariablesConstant.HISTORY_REPLAY_FILE , "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.e(TAG,"onDestroy");
        String strHistoryReplyResponseAppPreferences = AppPreferences.loadPreferences(mContext, VariablesConstant.HISTORY_REPLAY_FILE);
//        Log.w(TAG,"strHistoryReplyResponseAppPreferences: "+strHistoryReplyResponseAppPreferences);
        AppPreferences.savePreferences(mContext,VariablesConstant.HISTORY_REPLAY_FILE , "");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_date:
                getDate();
                break;

            case R.id.rl_start_time:
                getStartTime();
                break;

            case R.id.rl_end_time:
                getEndTime();
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

            case R.id.iv_tool_back_left:
                onBackPressed();
                break;

        }
    }

    // rl_end_time , tv_end_time
    private void getEndTime() {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        CustomTimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    String currentTime = hourOfDay + ":" + minute;


                    if (hourOfDay > 12) {
                        endTime = String.valueOf(hourOfDay - 12) + ":" + (String.valueOf(minute) + " PM");
                        tv_end_time.setText(endTime);
                        AppPreferences.savePreferences(mContext, "BOOK_START_TIME", startTime);
                    } else if (hourOfDay == 12) {
                        endTime = "12" + ":" + (String.valueOf(minute) + " PM");
                        tv_end_time.setText(endTime);
                        AppPreferences.savePreferences(mContext, "BOOK_START_TIME", startTime);
                    } else if (hourOfDay < 12) {
                        if (hourOfDay != 0) {
                            endTime = String.valueOf(hourOfDay) + ":" + (String.valueOf(minute) + " AM");
                            tv_end_time.setText(endTime);
                            AppPreferences.savePreferences(mContext, "BOOK_START_TIME", startTime);
                        } else {
                            endTime = "12" + ":" + (String.valueOf(minute) + " AM");
                            tv_end_time.setText(endTime);
                            AppPreferences.savePreferences(mContext, "BOOK_START_TIME", startTime);
                        }
                    }

                }
            }
        };
        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(mContext, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Choose hour:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        timePickerDialog.show();

    }

    // rl_start_time, tv_start_time
    private void getStartTime() {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        CustomTimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);


                    if (hourOfDay > 12) {
                        startTime = String.valueOf(hourOfDay - 12) + ":" + (String.valueOf(minute) + " PM");
                        tv_start_time.setText(startTime);
                        AppPreferences.savePreferences(mContext, "START_TIME", startTime);
                    } else if (hourOfDay == 12) {
                        startTime = "12" + ":" + (String.valueOf(minute) + " PM");
                        tv_start_time.setText(startTime);
                        AppPreferences.savePreferences(mContext, "START_TIME", startTime);
                    } else if (hourOfDay < 12) {
                        if (hourOfDay != 0) {
                            startTime = String.valueOf(hourOfDay) + ":" + (String.valueOf(minute) + " AM");
                            tv_start_time.setText(startTime);
                            AppPreferences.savePreferences(mContext, "START_TIME", startTime);
                        } else {
                            startTime = "12" + ":" + (String.valueOf(minute) + " AM");
                            tv_start_time.setText(startTime);
                            AppPreferences.savePreferences(mContext, "START_TIME", startTime);
                        }
                    }

                }
            }
        };
        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(mContext, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Choose hour:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    // driver_dob": "4/7/2000"
    private void getDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                tv_dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                tv_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                Log.w(TAG, "tv_date :" + tv_date.getText().toString());
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

//                tv_dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                tv_dateFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                Log.w(TAG, "tv_dateFrom :" + tv_dateFrom.getText().toString());
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
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

//                tv_dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                tv_dateTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                Log.e(TAG, "tv_dateTo :" + tv_dateTo.getText().toString());
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
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
//                Log.i(TAG,"parent "+parent.toString());
            }
        });
    }

    private void doValidation() {
        String vehicle = vehicleSpinner.getSelectedItem().toString().trim();
        dateFrom = tv_dateFrom.getText().toString().trim();
        dateTo = tv_dateTo.getText().toString().trim();
        date = tv_date.getText().toString().trim();
        startTime = tv_start_time.getText().toString().trim();
        endTime = tv_end_time.getText().toString().trim();

        if (vehicle.equalsIgnoreCase("All Vehicle")) {
            Toasty.error(mContext,getString(R.string.please_select_vehicle1),Toast.LENGTH_SHORT).show();
        }else if (date.isEmpty()){
            tv_date.setError(getString(R.string.select_date));
            tv_date.requestFocus();
        }else if (startTime.isEmpty()){
            tv_start_time.setError(getString(R.string.select_time));
            tv_start_time.requestFocus();
        }else if (endTime.isEmpty()){
            tv_end_time.setError(getString(R.string.select_time));
            tv_end_time.requestFocus();
        }  else {

            // ========== Api Call =============
            if (CheckNetwork.isNetworkAvailable(mContext)) {

//                String serverFromDate = formatDate(dateFrom,"dd/MM/yyyy","yyyy-MM-dd");
//                String serverToDate = formatDate(dateTo,"dd/MM/yyyy","yyyy-MM-dd");
//                Log.i(TAG,"serverFromDate : "+serverFromDate);

                // date convert into server side
                String serverDate = formatDate(date,"dd/MM/yyyy","yyyy-MM-dd");

//                Log.i(TAG,"startTime : "+startTime);
//                Log.e(TAG,"endTime : "+endTime);
                // time convert 12H to 24H
                String myStartTime = convertTime12To24(startTime);
                String myEndTime = convertTime12To24(endTime);

//                getHistoryReplayCall(token,dateFrom,dateTo,vehicle_id);
//                getHistoryReplayCall(token,date,startTime,endTime,vehicle_id);
                getHistoryReplayCall(token,serverDate,myStartTime,myEndTime,vehicle_id);

            } else {
                Toast.makeText(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private static String convertTime12To24(String T1) {
        Date date1 = null;
        String time="";
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

        try {
            date1 = parseFormat.parse(T1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String myTime = parseFormat.format(date1);
        time = displayFormat.format(date1);

        System.out.println( "My EnterTime: "+myTime);

        return time;
    }

    @SuppressLint("SimpleDateFormat")
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


    //================ Api Call ============================
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
//                Log.e(TAG, " Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        VehiclesResponse vehiclesResponse = response.body();
                        assert vehiclesResponse != null;
                        int status = vehiclesResponse.getStatus();
                        if (status== 200) {
                            vehicleArrayList  = vehiclesResponse.getVehicles();
//                                Log.e(TAG, "VehiclesResponse-listVehicle size:" + vehicleArrayList.size());
                            for (int i=0; i<vehicleArrayList.size();i++){
                                myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
//                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleName() +" "+ vehicleArrayList.get(i).getVehicleNo());
                            }
                            //vehicleSpinner
                            setVehicleSpinner();
                        } else {
                            Log.e(TAG, "Status Code :"+status);
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


    private void getHistoryReplayCall(String token, String data1,String sTime, String eTime, String vehicle_id) {
//        Log.e(TAG, "getHistoryReplayCall vehicle_id: " + vehicle_id);
//        Log.i(TAG,"serverDate: "+data1);
//        Log.i(TAG,"startTime: "+sTime);
//        Log.i(TAG,"endTime: "+eTime);

        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();

//        Call<HistoryReplyResponse> call = apiInterface.historyReplay(token, dateFrom,dateTo,vehicle_id);
        Call<HistoryReplyResponse> call = apiInterface.historyReplay(token,data1,sTime,eTime, vehicle_id);
        call.enqueue(new Callback<HistoryReplyResponse>() {
            @Override
            public void onResponse(Call<HistoryReplyResponse> call, Response<HistoryReplyResponse> response) {
                if (pd!=null)pd.dismiss();
                String message,from_date,to_date;
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, " Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
//                if (response.body() != null) {
//                    UtilityReadWrite.writeJsonToFile(mContext, AppConstantFile.HISTORY_REPLAY_TEXT, new Gson().toJson(response.body()));
//                    Log.d("Response:::", response.body().toString());
//                }

                try {
                    if (response.isSuccessful()){
                        // HistoryReplayResponse
                        HistoryReplyResponse historyReplayResponse = response.body();
                        int status = historyReplayResponse.getStatus();
                        if (status==200){

                            AppPreferences.savePreferences(mContext,VariablesConstant.HISTORY_REPLAY_FILE , str_response);

                            message = historyReplayResponse.getMessage();
//                        total = historyReplayResponse.getTotal();
//                        from_date = historyReplayResponse.getFromDate();
//                        to_date = historyReplayResponse.getToDate();
//                        Log.i(TAG,"Message : "+message);
//                        Log.i(TAG,"from_date : "+from_date);
//                        Log.i(TAG,"to_date : "+to_date);
//                        Log.e(TAG,"date : "+historyReplayResponse.getDataHistory().get(0).getDate());

//                        MySingleton.getInstance().setHistoryReplyResponse(historyReplayResponse);
//                        Log.e(TAG, "MySingleton.getInstance: " +  MySingleton.getInstance().getHistoryReplyResponse());

                            // TripDetailsView
//                        TripDetailsView.loadTripDetailResponse(historyReplayResponse);
//
//                        HistoryReplyResponse responseData = new HistoryReplyResponse();
//                        Log.e(TAG,"historyReplayResponse.getDataHistory()size : "+historyReplayResponse.getDataHistory().size());
//                        responseData.setDataHistory(historyReplayResponse.getDataHistory()); // set List<HistoryData>
//                        responseData.setFromDate(from_date);
//                        responseData.setToDate(to_date);
//                        responseData.setMessage(message);

//                        Intent intent = new Intent(mContext, HistoryReplayMap2.class);
                            Intent intent = new Intent(mContext, TripDetailsFragment.class);
                            intent.putExtra("data1", data1);
                            intent.putExtra("sTime", sTime);
                            intent.putExtra("eTime", eTime);
                            intent.putExtra("vehicle_id", vehicle_id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            finish();
                        }else {
                            Log.e(TAG, "Status Code :"+status);
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,""+e.toString());
                }
            }

            @Override
            public void onFailure(Call<HistoryReplyResponse> call, Throwable t) {
                pd.dismiss();
//                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }



}
