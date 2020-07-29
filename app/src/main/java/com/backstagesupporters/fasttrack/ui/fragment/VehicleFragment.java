package com.backstagesupporters.fasttrack.ui.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.MyApplication;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.LiveTrackingActivity;
import com.backstagesupporters.fasttrack.ui.activity.LiveTrackingAllActivity;
import com.backstagesupporters.fasttrack.ui.adapters.VehicleHomeAdapter;
import com.backstagesupporters.fasttrack.utils.MySingleton;
import com.backstagesupporters.fasttrack.utils.MyUtility;
import com.backstagesupporters.fasttrack.utils.language.Utility;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.backstagesupporters.fasttrack.utils.MyShowDialog;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VehicleFragment extends Fragment implements ItemClickListener {
        private String TAG= getClass().getSimpleName();
//    private static final String TAG = "fasttrack";
    private Context mContext;
    private TextView tv_noData, tv_noNetwork;
    private TextInputEditText tv_search_view;
    private RecyclerView recyclerView;
    private VehicleHomeAdapter adapter;
    private List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
    private List<Vehicle> vehicleArrayList2 = new ArrayList<Vehicle>();

    String vehicleId="",vehicleNo="";

    private ApiInterface apiInterface;
    private String userId="";
    private ProgressDialog pd;
    private MyShowDialog myShowDialog;
    private Handler mHandlerFragment;
    SwipeRefreshLayout mSwipeRefreshLayout;
    HomeActivity homeActivity;

    public VehicleFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MyApplication.localeManager.onAttach(context);
        Utility.resetActivityTitle(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.activity_vehicle_home, container, false);
        mContext = getActivity();

        homeActivity = new HomeActivity();
        HomeActivity.tv_title.setText(getString(R.string.vehicle_list));

        findViewById(view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.blue, R.color.green);

        myShowDialog = new MyShowDialog(getActivity());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);

        // TODO: 5/31/2020
        String strVResponse2 = AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_LIST);
        Log.d(TAG,"strVResponse2: "+strVResponse2);
        VehiclesResponse vResponse2  = new Gson().fromJson(strVResponse2, VehiclesResponse.class);
        if (vResponse2 !=null ){
            Log.d(TAG,"strVResponse2 size: "+vResponse2.getVehicles().size());
            int status = vResponse2.getStatus();
            if (status == 200){
                vehicleArrayList  = vResponse2.getVehicles();
                // setAdapter
                setAdapter();

              /*  if (CheckNetwork.isNetworkAvailable(mContext)) {
                    vehicleArrayList  = vResponse2.getVehicles();
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_noNetwork.setVisibility(View.GONE);
                    // setAdapter
                    setAdapter();
                } else {
                    tv_noNetwork.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }*/
            }

        }

            //   tv_search_view.setVisibility(View.GONE);
            //   searchView.setVisibility(View.VISIBLE);
            tv_search_view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // filter recycler view when query submitted
                   // adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // filter recycler view when query submitted
                    adapter.getFilter().filter(s);
                }
            });


            // listening to search query text change
          /*
           SearchView searchView =  = view.findViewById(R.id. searchView);
          searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(vehicleArrayList.contains(query)){
                        // filter recycler view when query submitted
                        adapter.getFilter().filter(query);

                        //  adapter.filter(query);   // https://abhiandroid.com/ui/searchview
                    }else{
                        Toast.makeText(mContext, "No Match found", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    *//**
                     * https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/
                     * https://www.tutorialspoint.com/how-to-filter-a-recyclerview-with-a-searchview-on-android
                     * https://www.tutorialspoint.com/how-to-use-searchview-in-android
                     *//*
                    // filter recycler view when text is changed
                    adapter.getFilter().filter(query);
                    return false;
                }
            });
*/
       return view;
    }


    @Override
    public void onStart() {
        super.onStart();
       /* Log.w(TAG,"MyLife onStart");*/
        AppPreferences.savePreferences(mContext,VariablesConstant.VehicleFragment_1 , "");

        // ======= API Call
        getVehicleAPI();
       // myShowDialog.hideProgress();

        // RefreshFragment
        autoRefreshFragment();


        // allMarkers
        ImageView iv_allMarkers = ((HomeActivity) getActivity()).getImageViewAllMarkers();
        iv_allMarkers.setOnClickListener(view -> {
            Log.d(TAG, "click iv_allMarkers");

            // "vehicle_id":0
            Intent intent = new Intent(mContext, LiveTrackingAllActivity.class);
//                intent.putExtra(VariablesConstant.VEHICLE_ID,vehicleId);
            intent.putExtra(VariablesConstant.VEHICLE_ID,"0");
            intent.putExtra(VariablesConstant.VEHICLE_NAME,getString(R.string.all_Vehicle));
            startActivity(intent);

            Log.d(TAG, "click iv_allMarkers size: "+ vehicleArrayList2.size());
//            if (vehicleArrayList2 != null){
//                for (Vehicle item : vehicleArrayList2) {
//                    vehicleNo =  item.getVehicleNo();
//                    Log.d(TAG, "vehicleId: "+ vehicleId);
//                }
//            }

        });

        // show FloatingActionButton
        FloatingActionButton floatingActionButton = ((HomeActivity) getActivity()).getFloatingActionButton();
//        if (floatingActionButton != null) {
//            floatingActionButton.show();
//        }

        // hide FloatingActionButton
        if (floatingActionButton != null) {
            floatingActionButton.hide();
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // ======= API Call
                        getVehicleAPI();
                    }
                }, 5000);
            }
        });

    }

    // ======= API Call ==========
    private void getVehicleAPI() {
        getVehicleCall(userId);

//        if (CheckNetwork.isNetworkAvailable(mContext)) {
//            recyclerView.setVisibility(View.VISIBLE);
//            tv_noNetwork.setVisibility(View.GONE);
//            getVehicleCall(userId);
//        } else {
//            tv_noNetwork.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppPreferences.savePreferences(mContext,VariablesConstant.VehicleFragment , "1");

        // set title toolbar
//        HomeActivity.tv_title.setText(getString(R.string.vehicle_list));
    }

    @Override
    public void onStop() {
        super.onStop();
        AppPreferences.savePreferences(mContext,VariablesConstant.VehicleFragment_1 , "");
        stopRepeatingTaskRefresh();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopRepeatingTaskRefresh();
    }


    private void autoRefreshFragment() {
        mHandlerFragment = new Handler();
        m_RunnableFragment.run();
    }

    // stop the mStatusCheckerLocation
    void stopRepeatingTaskRefresh() {
        mHandlerFragment.removeCallbacks(m_RunnableFragment);

        // stop the Handler
        if (m_RunnableFragment != null) {
            mHandlerFragment.removeCallbacks(m_RunnableFragment);
            mHandlerFragment.removeMessages(0);
        }
    }

    private final Runnable m_RunnableFragment = new Runnable() {
        public void run() {
            String refreshTime = AppPreferences.loadPreferences(mContext, VariablesConstant.REFRESH_INTERVAL);
//            Log.d(TAG, "Refresh Interval Time :" + refreshTime);
            if (!refreshTime.isEmpty()){
                int REFRESH_TIME = MyUtility.getRefreshTime(refreshTime);
                Log.d(TAG, "Refresh REFRESH_TIME:" + REFRESH_TIME);
                // ======= API Call
                getVehicleAPI();

                mHandlerFragment.postDelayed(m_RunnableFragment,REFRESH_TIME);
            }
        }
    };



    private void setAdapter() {
        Log.d(TAG,"vehicleArrayList: "+vehicleArrayList.size());
        if (!vehicleArrayList.isEmpty()){
            if (vehicleArrayList2 !=null){
                vehicleArrayList2.clear();
            }

            /**
             * remove  vehicle id=0 from vehicleList
             * and all items in vehicleArrayList2
             */
            for (Vehicle item : vehicleArrayList) {
                if (!item.getVehicleNo().equalsIgnoreCase("All Vehicle")) {
                    vehicleArrayList2.add(item);
                }
            }

            // TODO: 5/31/2020
            if (!vehicleArrayList2.isEmpty()){
                Log.d(TAG,"vehicleArrayList2: "+vehicleArrayList2.size());

                LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(verticalManager);
                adapter = new VehicleHomeAdapter(mContext,vehicleArrayList2);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.setClickListener(this); // Bind the listener

                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void findViewById(View view) {
        recyclerView = view.findViewById(R.id.recyclerView_vehicle);
        tv_noData = view.findViewById(R.id.tv_noData);
        tv_noNetwork = view.findViewById(R.id.tv_noNetwork);
        tv_search_view = view.findViewById(R.id. tv_search_view);
    }



    @Override
    public void onClick(View view, int position) {
        String vehicle_id, vehicle_name,vehicle_type, vehicle_no, vehicle_color, driver_number, emergency_number;
/*
        // The onClick implementation of the RecyclerView item click
        Vehicle modelVehicle = vehicleArrayList2.get(position);
        vehicle_id = String.valueOf(modelVehicle.getVehicleId());
        vehicle_name = modelVehicle.getVehicleName();
        vehicle_no = modelVehicle.getVehicleNo();
        vehicle_type = modelVehicle.getVehicleType();
        vehicle_color = modelVehicle.getColor();
        driver_number = modelVehicle.getDriverNumber();
        emergency_number = modelVehicle.getEmergencyNumber();
        Log.d(TAG,"modelVehicle vName: "+vehicle_name);
        Log.d(TAG,"modelVehicle vehicle_id: "+vehicle_id);
        Log.d(TAG,"modelVehicle vNumber: "+vehicle_no);

        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_ID,vehicle_id);
        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_NAME,vehicle_name);
        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_NUMBER,vehicle_no);
        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_TYPE, vehicle_type);
        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_COLOR, vehicle_color);
        AppPreferences.savePreferences(mContext,VariablesConstant.DRIVER_CONTACT_NUMBER, driver_number);
        AppPreferences.savePreferences(mContext,VariablesConstant.EMERGENCY_CONTACT_NUMBER, emergency_number);

        // Singleton
        MySingleton.getInstance().setVehicle(modelVehicle);

        Intent intent = new Intent(mContext, LiveTrackingActivity.class);
        intent.putExtra(VariablesConstant.VEHICLE_ID, vehicle_id);
        intent.putExtra(VariablesConstant.VEHICLE_NAME, vehicle_name);
        intent.putExtra(VariablesConstant.VEHICLE_NUMBER, vehicle_no);
        intent.putExtra(VariablesConstant.VEHICLE_TYPE, vehicle_type);
        intent.putExtra(VariablesConstant.VEHICLE_COLOR, vehicle_color);
        intent.putExtra(VariablesConstant.DRIVER_CONTACT_NUMBER, driver_number);
        intent.putExtra(VariablesConstant.EMERGENCY_CONTACT_NUMBER, emergency_number);
        startActivity(intent);*/
    }


    //================ Api Call ============================
//    private void getVehicleCall(final String token) {
    private void getVehicleCall( String user_id) {
//        Log.e(TAG, "Junaid userId :" + user_id);
//        myShowDialog.showProgress();
//        pd = new ProgressDialog(mContext);
//        pd.setMessage("Loading Please Wait...");
//        pd.setCancelable(true);
//        pd.show();

//        Call<VehiclesResponse> call = apiInterface.showVehicles(token);
        Call<VehiclesResponse> call = apiInterface.showVehiclesUserId(user_id);
        call.enqueue(new Callback<VehiclesResponse>() {
            @Override
            public void onResponse(Call<VehiclesResponse> call, Response<VehiclesResponse> response) {
//                myShowDialog.hideProgress();
//                if (pd!=null)pd.dismiss();

                String str_response = new Gson().toJson(response.body());
                Log.d(TAG, " Response >>>>" + str_response);
                int  responseCode  = response.code();
                try {
                    if (response.isSuccessful() && responseCode ==200){
                        VehiclesResponse vehiclesResponse = response.body();
                        assert vehiclesResponse != null;
                        int status = vehiclesResponse.getStatus();

                        if (status== 200) {

                            // TODO: 5/31/2020
                            if (vehiclesResponse.getVehicles().get(0).getVehicleType() !=null){
                                vehicleArrayList  = vehiclesResponse.getVehicles();

                                AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_LIST , str_response);
                                String strVehiclesResponse2 = AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_LIST);
                                Log.d(TAG,"strVehiclesResponse2: "+strVehiclesResponse2);

                                // setAdapter
                                setAdapter();
                            }


                        } else {
                            Log.e(TAG, "status Error "+status);
                            Log.e(TAG, "Response code :" + status);
                            if (status==401){
                                String user_id = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);
                                homeActivity.logOutApiCall(user_id,TAG);
                            }
                        }
                    }else {
//                        Log.e(TAG, "Response code :" + responseCode);
                        if (responseCode==401){
                            String user_id = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);
                            homeActivity.logOutApiCall(user_id,TAG);
                        }

                        // error case
//                        try {
//                            // :{"success":false,"error":"We cant find an account with this credentials."}
//                            String jsonString = response.errorBody().string();
//                            JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);
//                            // convertedObject.get("name").getAsString().equals("Baeldung")
//                            String success =  convertedObject.get("success").toString();
//                            String error =  convertedObject.get("error").toString();
////                            Log.d(TAG, "response.success :" + success);
////                            Log.d(TAG, "response.error :" + error);
//
//                            //ErrorUtils.alertDialogMsg(mContext,response.message(),"LOGIN_USER");
//                            ErrorUtils.apiResponseErrorHandle(TAG,responseCode,error,VariablesConstant.SHOW_VEHICLE_LIST, mContext);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception: "+e.toString());
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
//                myShowDialog.hideProgress();
                Log.e(TAG,"onFailure: "+t.toString());
            }
        });

    }


}
