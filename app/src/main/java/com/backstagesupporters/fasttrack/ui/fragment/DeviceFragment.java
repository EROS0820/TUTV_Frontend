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

import com.backstagesupporters.fasttrack.MyApplication;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.AddDeviceActivity;
import com.backstagesupporters.fasttrack.ui.adapters.DeviceAdapter;
import com.backstagesupporters.fasttrack.utils.language.Utility;
import com.backstagesupporters.fasttrack.models.DeviceShow;
import com.backstagesupporters.fasttrack.responseClass.ShowDeviceResponse;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends Fragment implements ItemClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private RecyclerView recyclerView;
    private TextView tv_add_device;
    private DeviceAdapter adapter;

    private List<DeviceShow> deviceShowList = new ArrayList<>();

    private ApiInterface apiInterface;
    private ProgressDialog pd;
    private String userType ="",token="";



    public DeviceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        mContext = getActivity();

        findViewById(view);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getDriverShowCall(token);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }
    }


    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);

//        Log.e(TAG,"List sise :"+deviceShowList.size());
        if (!deviceShowList.isEmpty()){
            adapter = new DeviceAdapter(mContext,deviceShowList);
//        adapter = new DeviceAdapter(mContext,getDummyData());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setClickListener(this); // Bind the listener
        }else {
            Toasty.error(mContext, getString(R.string.err_data_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void findViewById(View view) {
        recyclerView =view.findViewById(R.id.recyclerView_device);
        tv_add_device =view.findViewById(R.id.tv_add_device);

        tv_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AddDeviceActivity.class));
                getActivity().finish();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!deviceShowList.isEmpty()){
            deviceShowList.clear();
        }
    }

    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
        DeviceShow model = deviceShowList.get(position);
        Toasty.success(mContext,"Welcome - "+model.getDeviceNo(), Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(mContext, HomeActivity.class);
////        i.putExtra("city", model.name);
////        i.putExtra("desc", model.description);
////        i.putExtra("image", model.imageName);
//        startActivity(intent);
    }

    //================ Api Call ============================
    private void getDriverShowCall(final String token) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        /**
         * GET
         * http://3.135.158.46/api/show_devices?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8zLjEzNS4xNTguNDZcL2FwaVwvbG9naW4iLCJpYXQiOjE1NzYyMjg5MDgsImV4cCI6MTU3NjIzMjUwOCwibmJmIjoxNTc2MjI4OTA4LCJqdGkiOiI5TW1ybFdlRGl6aTdTUTY3Iiwic3ViIjoxLCJwcnYiOiI4N2UwYWYxZWY5ZmQxNTgxMmZkZWM5NzE1M2ExNGUwYjA0NzU0NmFhIn0.eTTp34zaQ08FBJHjtIhGtwQ68lfRuakN7ti3uuVQFkA
         *
         */
        Call<ShowDeviceResponse> call = apiInterface.showDevices(token);

        call.enqueue(new Callback<ShowDeviceResponse>() {
            @Override
            public void onResponse(Call<ShowDeviceResponse> call, Response<ShowDeviceResponse> response) {
                if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);

                try {
                    if (response.isSuccessful() && responseCode ==200){
                        ShowDeviceResponse deviceResponse = response.body();
                        int status = deviceResponse.getStatus();
//                        Log.e(TAG, "success :" + status);
                        if (status==200) {

                            deviceShowList  = deviceResponse.getDevices();
//                            Log.e(TAG, "listVehicle :" + deviceShowList.toString());

                            // set Adapter
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
//                    Log.e(TAG, "Massage :"+e.toString());
//                    Toasty.error(mContext, "Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShowDeviceResponse> call, Throwable t) {
                if (pd!=null)pd.dismiss();
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }



}
