package com.backstagesupporters.fasttrack.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.adapters.LanguageAdapter;
import com.backstagesupporters.fasttrack.models.LanguageModel;
import com.backstagesupporters.fasttrack.ui.user.LoginsActivity;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends BaseActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private TextView tv_noData;
    private RecyclerView  recyclerView;
    private TextView tv_select_language;

    private LanguageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        mContext = LanguageActivity.this;

        recyclerView = findViewById(R.id.recyclerView_language);
        tv_select_language = findViewById(R.id.tv_select_your_language);
        tv_noData = findViewById(R.id.tv_noData);


        if (getDummyData().isEmpty()){
            tv_noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            tv_noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        setAdapter();

    }

    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);
//        adapter = LanguageAdapter(mContext,List);
        adapter = new LanguageAdapter(mContext,getDummyData());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//                AppPreferences.savePreferences(mContext, VariablesConstant.LANGUAGE, "en");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }



    private List<LanguageModel> getDummyData() {
        List<LanguageModel> list = new ArrayList<LanguageModel>();
        list.add(new LanguageModel("Hindi",R.drawable.hindi));
        list.add(new LanguageModel("English",R.drawable.english));
        list.add(new LanguageModel("Tamil",R.drawable.tamil));
        list.add(new LanguageModel("Malayalam",R.drawable.malayalam));

        list.add(new LanguageModel("Kannad",R.drawable.bg_circle_b));
        list.add(new LanguageModel("Urdu",R.drawable.bg_circle_r));

        return list;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, LoginsActivity.class));
        finish();
    }


}
