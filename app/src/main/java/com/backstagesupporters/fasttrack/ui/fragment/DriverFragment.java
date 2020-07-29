package com.backstagesupporters.fasttrack.ui.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.MyApplication;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.AddDriverActivity;
import com.backstagesupporters.fasttrack.ui.adapters.DriverAdapter;
import com.backstagesupporters.fasttrack.utils.MyProgressDialog;
import com.backstagesupporters.fasttrack.utils.language.Utility;
import com.backstagesupporters.fasttrack.models.DriverShow;
import com.backstagesupporters.fasttrack.responseClass.DriverResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriverFragment extends Fragment implements ItemClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private RecyclerView recyclerView;
    private TextView tv_addDiver, tv_noData;
    private DriverAdapter adapter;
    String driver_photo, driver_name,driver_phone,driver_email, driver_id;
    private List<DriverShow> driverShowList = new ArrayList<DriverShow>();

    private ApiInterface apiInterface;
    private ProgressDialog pd;
    private String userType ="",token="";
    private String str_response;


    public DriverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MyApplication.localeManager.onAttach(context);
        Utility.resetActivityTitle(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        findViewById(view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);

        FloatingActionButton floatingActionButton = ((HomeActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.hide();
        }

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getDriverShowCall(token);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        HomeActivity.tv_title.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        // set title toolbar
        HomeActivity.tv_title.setText(getString(R.string.driver));
    }

    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);
        if (!driverShowList.isEmpty()){
//            Log.e(TAG,"List sise :"+driverShowList.size());
            adapter = new DriverAdapter(mContext,driverShowList);
//        adapter = new DriverAdapter(mContext,getDummyData());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setClickListener(this); // Bind the listener
        }else {
            if (driverShowList.isEmpty()){
                tv_noData.setText("No Driver");
                tv_noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                tv_noData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

    }

    private void findViewById(View view) {
        recyclerView =view.findViewById(R.id.recyclerView_driver);
        tv_addDiver =view.findViewById(R.id.tv_addDiver);
        tv_noData =view.findViewById(R.id.tv_noData);

        userType = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_TYPE);
        if (userType.equalsIgnoreCase("admin")){
            tv_addDiver.setVisibility(View.VISIBLE);
        }else if (userType.equalsIgnoreCase("user")){
            tv_addDiver.setVisibility(View.VISIBLE);
        }else if (userType.equalsIgnoreCase("sub")){
            tv_addDiver.setVisibility(View.GONE);
        }

        tv_addDiver.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AddDriverActivity.class));
            getActivity().finish();
        });
    }



    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!driverShowList.isEmpty()){
            driverShowList.clear();
        }
    }

    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
        DriverShow model = driverShowList.get(position);
        driver_id = String.valueOf(model.getDriverId());
        driver_name = model.getDriverName();
//        Log.e(TAG,"driver_id :"+driver_id);
//        Log.i(TAG,"driver_name :"+driver_name);
    }


    //================ Api Call ============================
    private void getDriverShowCall(final String token) {
        MyProgressDialog.showProgress(mContext);

        Call<DriverResponse> call = apiInterface.showDrivers(token);
        call.enqueue(new Callback<DriverResponse>() {
            @Override
            public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {
                MyProgressDialog.hideProgress();
                str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getDriverShowCall >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (response.isSuccessful() && responseCode ==200){
                        DriverResponse driverResponse = response.body();
                        assert driverResponse != null;
                        int status = driverResponse.getStatus();
//                        Log.e(TAG, "status :" + status);
                        if (status==200 ) {
                            driverShowList  = driverResponse.getDrivers();
//                            Log.i(TAG, "istVehicle size:" + driverShowList.size());
                            //set Adapter
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
            public void onFailure(Call<DriverResponse> call, Throwable t) {
                MyProgressDialog.hideProgress();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }





}
