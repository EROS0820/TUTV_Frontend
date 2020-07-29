package com.backstagesupporters.fasttrack.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import com.backstagesupporters.fasttrack.ui.adapters.NotificationsAdapter;
import com.backstagesupporters.fasttrack.models.NotificationModel;
import com.backstagesupporters.fasttrack.responseClass.NotificationsResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends BaseActivity implements ItemClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title, tv_noData;

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    List<NotificationModel> notificationModelList = new ArrayList<>();

    ApiInterface apiInterface;
    ProgressDialog pd;
    String token="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mContext=NotificationActivity.this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext,"TOKEN");

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
//        tv_tool_title.setText(getString(R.string.add_device));
        tv_tool_title.setText(getString(R.string.notification));

        iv_tool_back_left.setOnClickListener(v -> {
            onBackPressed();
        });

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getNotificationCall(token);
        } else {
            Toasty.warning(mContext, mContext.getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }


        // set Adapter
        setAdapter();


       /* Intent intent = getIntent();
        title = intent.getStringExtra("title");
        message = intent.getStringExtra("message");
        titleView.setText("Title: \n"+title);
        messageView.setText("Message: \n"+message);*/

    }


    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);

        if (!notificationModelList.isEmpty()){
//            Log.e(TAG,"notificationModelList Size :"+notificationModelList.size());

            adapter = new NotificationsAdapter(mContext, notificationModelList);
//        adapter = new DeviceAdapter(mContext,getDummyData());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setClickListener(this); // Bind the listener
        }
    }


    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        recyclerView = findViewById(R.id.recyclerView_notifications);
        tv_noData = findViewById(R.id.tv_noData);
    }


 /*

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        title = intent.getStringExtra("title");
        message = intent.getStringExtra("message");

        titleView.setText("Title: \n"+title);
        messageView.setText("Message: \n"+message);
    }*/


   // call API
   private void getNotificationCall(String token) {

       pd = new ProgressDialog(mContext);
       pd.setMessage("Loading Please Wait...");
       pd.setCancelable(false);
       pd.show();

       Call<NotificationsResponse> call = apiInterface.notifications(token);

       call.enqueue(new Callback<NotificationsResponse>() {
           @Override
           public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
               if (pd!=null)pd.dismiss();
               String str_response = new Gson().toJson(response.body());
//               Log.e(TAG, "Response >>>>" + str_response);
               int responseCode  =  response.code();
//               Log.e(TAG, "responseCode :" + responseCode);

               try {
                   if (response.isSuccessful()) {
                       NotificationsResponse notificationsResponse = response.body();
                       int status = notificationsResponse.getStatus();
                       String getMessage = notificationsResponse.getMessage();
//                       Log.e(TAG, "success :" + status);
//                       Log.e(TAG, "getMessage :" + getMessage);
                       if (status==200) {
                           notificationModelList  = notificationsResponse.getNotifications();
//                           Log.e(TAG, "notificationModelList size :" + notificationModelList.size());

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
           public void onFailure(Call<NotificationsResponse> call, Throwable t) {
               pd.dismiss();
               Log.e(TAG,"onFailure"+t.toString());
           }
       });

   }


    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
        NotificationModel model = notificationModelList.get(position);
//        Log.i(TAG,"Welcome - "+model.getTitle());
//        Intent intent = new Intent(mContext, HomeActivity.class);
////        i.putExtra("city", model.name);
////        i.putExtra("desc", model.description);
////        i.putExtra("image", model.imageName);
//        startActivity(intent);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!notificationModelList.isEmpty()){
            notificationModelList.clear();
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(mContext, HomeActivity.class));
        startActivity(new Intent(mContext, LiveTrackingActivity.class));
        finish();
    }



}



/**
 * http://androidrace.com/2017/01/05/how-to-add-push-notification-in-android-application-from-android-studio-android-developer-part-2-working-with-server/
 * https://github.com/anjanapai2508/PushPlugin
 * https://github.com/rana01645/android-push-notification
 *
 *  https://stackoverflow.com/questions/25509802/android-java-cannot-resolve-notificationreceiver
 */