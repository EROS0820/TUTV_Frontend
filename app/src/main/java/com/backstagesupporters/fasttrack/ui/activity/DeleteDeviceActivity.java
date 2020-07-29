package com.backstagesupporters.fasttrack.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.adapters.DeleteDeviceAdapter;
import com.backstagesupporters.fasttrack.models.DeleteDeviceModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.github.aakira.expandablelayout.Utils;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class DeleteDeviceActivity extends BaseActivity implements ItemClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title, tv_noData;
    RecyclerView recyclerView;
    private DeleteDeviceAdapter adapter;
    List<DeleteDeviceModel> list = new ArrayList<DeleteDeviceModel>();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);
        mContext = DeleteDeviceActivity.this;
        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.delete_device));
//        iv_tool_back_left.setImageResource(R.drawable.ic_arrow_back_white);
        iv_tool_back_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        setAdapter();

        if (list.isEmpty()){
            tv_noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            tv_noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }


    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);
//        adapter = VehicleAdapter(mContext,List);
        adapter = new DeleteDeviceAdapter(mContext,getDummyData());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setClickListener(this); // Bind the listener
    }

    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        recyclerView = findViewById(R.id.recyclerView_deleteDevice);
        tv_noData = findViewById(R.id.tv_noData);

    }

    /**
     *         this.phone = phone;
     *         this.image_id = image_id;
     *         this.interpolator = interpolator;
     * @return
     */
    // DeleteDeviceModel(String deviceName, String deviceSrNumber, String licenseNumber, String driver, String phone, int image_id, TimeInterpolator interpolator)
    private List<DeleteDeviceModel> getDummyData() {
        list.add(new DeleteDeviceModel("Device1","Device Sr. Number","License Number","Mukesh","8802587111", R.drawable.ic_device_serial,Utils.createInterpolator(Utils.ACCELERATE_DECELERATE_INTERPOLATOR) ));
        list.add(new DeleteDeviceModel("Device2","Device Sr. Number","License Number","Mukesh","8802587111", R.drawable.ic_device_serial,Utils.createInterpolator(Utils.ACCELERATE_INTERPOLATOR) ));
        list.add(new DeleteDeviceModel("Device3","Device Sr. Number","License Number","Mukesh","8802587111", R.drawable.ic_device_serial,Utils.createInterpolator(Utils.BOUNCE_INTERPOLATOR) ));
        list.add(new DeleteDeviceModel("Device4","Device Sr. Number","License Number","Mukesh","8802587111", R.drawable.ic_device_serial,Utils.createInterpolator(Utils.DECELERATE_INTERPOLATOR) ));

        return list;
    }



    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
        DeleteDeviceModel model = list.get(position);
        Toasty.success(mContext,"Welcome - "+model.getDeviceName(), Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(mContext, HomeAdminActivity.class);
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
