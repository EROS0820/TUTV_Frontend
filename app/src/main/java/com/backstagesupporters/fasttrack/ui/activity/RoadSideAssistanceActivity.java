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

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.adapters.RSAAdapter;
import com.backstagesupporters.fasttrack.models.RSAModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class RoadSideAssistanceActivity extends BaseActivity implements ItemClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;

    private RecyclerView recyclerView;
    private RSAAdapter adapter;
    private List<RSAModel> rsaList ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_side_accistance);
        mContext=RoadSideAssistanceActivity.this;

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.roadSideAssistance));
        iv_tool_back_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // set Adapter
        setAdapter();

    }

    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);

        adapter = new RSAAdapter(mContext, getDummyData());
//        adapter = new DeviceAdapter(mContext,getDummyData());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setClickListener(this); // Bind the listener

        if (!rsaList.isEmpty()){
            Log.e(TAG,"RSA List Size :"+rsaList.size());
        }
    }

    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        recyclerView = findViewById(R.id.recyclerView_rsa);

    }


    private List<RSAModel> getDummyData() {
        rsaList = new ArrayList<RSAModel>();
        rsaList.add(new RSAModel("Volkswagen","18002090909"));
        rsaList.add(new RSAModel("maruti suzuki arena","18001021800"));
        rsaList.add(new RSAModel("maruti suzuki nexa","18001026392"));
        rsaList.add(new RSAModel("Mahindra","9015033888"));
        rsaList.add(new RSAModel("Nissan","1800 209 3456"));
        rsaList.add(new RSAModel("Hyundai","1800 102 4645"));
        rsaList.add(new RSAModel("Honda","1800 103 3121"));
        rsaList.add(new RSAModel("BMW","1800 208 8870"));
        rsaList.add(new RSAModel("Renault","1800 300 44444"));
        rsaList.add(new RSAModel("Toyota","1800 102 5001"));
        rsaList.add(new RSAModel("Ford","1800 103 7400"));
        rsaList.add(new RSAModel("TATA","1800 209 6688"));
        rsaList.add(new RSAModel("Audi","1800 103 6800"));
        rsaList.add(new RSAModel("Jeep","1800 266 5337"));
        rsaList.add(new RSAModel("Mercedes","00800 1 777 7777"));
        rsaList.add(new RSAModel("Mitsubishi","1800 102 2955"));
        rsaList.add(new RSAModel("Volvo","08006986586"));
        return rsaList;
    }

    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
        RSAModel model = rsaList.get(position);
//        Log.i(TAG,"Welcome - "+model.getName());
//        Intent intent = new Intent(mContext, HomeActivity.class);
////        i.putExtra("city", model.name);
////        i.putExtra("desc", model.description);
////        i.putExtra("image", model.imageName);
//        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rsaList.isEmpty()){
            rsaList.clear();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();

    }
}
