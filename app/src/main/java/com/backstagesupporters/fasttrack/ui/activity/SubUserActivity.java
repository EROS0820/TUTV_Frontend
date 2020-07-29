package com.backstagesupporters.fasttrack.ui.activity;


import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.SubUserDeleteListner;
import com.backstagesupporters.fasttrack.ui.adapters.SubUserAdapter;
import com.backstagesupporters.fasttrack.models.SubUser;
import com.backstagesupporters.fasttrack.responseClass.ShowSubUserResponse;
import com.backstagesupporters.fasttrack.responseClass.SubUserStatusResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.backstagesupporters.fasttrack.utils.UserStatusAccount;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.github.aakira.expandablelayout.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class SubUserActivity extends BaseActivity implements ItemClickListener, SubUserDeleteListner {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title,tv_noData;
    RecyclerView recyclerView;
    ListView listView_sub_user;
    private TextView tv_add_sub_user;
    private SubUserAdapter adapter;
    SubUserAdapter3 adapter2;

    public static int count = 0;
    private static boolean isNotAdded = true;
    public CheckBox checkBox_header;
    SparseBooleanArray mChecked = new SparseBooleanArray();

    List<SubUser> subUSerList = new ArrayList<>();

    ApiInterface apiInterface;
    ProgressDialog pd;
    String token="";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_user);
        mContext = SubUserActivity.this;

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.sub_user));

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);

        // ======= API Call ==========
        if (subUSerList.isEmpty()){
            if (CheckNetwork.isNetworkAvailable(mContext)) {
                getShowSubUsersCall(token);
            } else {
                Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }

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

    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);
//        Log.e(TAG, "VehiclesResponse-listVehicle size :" + subUSerList.size());
        if (!subUSerList.isEmpty()){
            adapter = new SubUserAdapter(mContext,subUSerList, this);
//        adapter = new DeviceAdapter(mContext,getDummyData());
            recyclerView.setAdapter(adapter);
            adapter.setClickListener(this); // Bind the listener

            adapter.notifyDataSetChanged();
        }
        if (subUSerList.isEmpty()){
//                tv_noData.setText("Sub User can't add in database");
            tv_noData.setText("No Sub User");
            tv_noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            tv_noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    // listView_sub_user
    private void setAdapterListView(){

        adapter2 = new SubUserAdapter3(mContext, subUSerList);

        //  To avoid adding multiple times
        if (isNotAdded) {
            // mListView (ListView) //DO NOT ADD `NULL` here.
            final View headerView = getLayoutInflater().inflate(R.layout.custom_list_view_header, listView_sub_user, false);

            checkBox_header = (CheckBox) headerView.findViewById(R.id.checkBox_header);

            //Select All / None DO NOT USE "setOnCheckedChangeListener" here.
            checkBox_header.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Set all the checkbox to True/False

                    for (int i = 0; i < count; i++) {
                        mChecked.put(i, checkBox_header.isChecked());
                    }

                    //Update View
                    adapter2.notifyDataSetChanged();

                }
            });

            // Add Header to ListView
            listView_sub_user.addHeaderView(headerView);

            isNotAdded = false;
        }


        /**
         * Set Data base Item into listview
         */
        listView_sub_user.setAdapter(adapter2);

        // go to next activity for detail image
        listView_sub_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Use "if else" only if header is added
                if (position == 0) {
                    Log.d(TAG,checkBox_header.getId() + "\n" + checkBox_header.isChecked());
                } else {
                    position = position - 1; // "-1" If Header is Added
                   Log.e(TAG,subUSerList.get(position) + "\n" + mChecked.get(position));
                }
            }
        });

    }

    private void findViewById() {
        iv_tool_back_left =findViewById(R.id.iv_tool_back_left);
        tv_tool_title =findViewById(R.id.tv_tool_title);
        tv_noData =findViewById(R.id.tv_noData);
        recyclerView =findViewById(R.id.recyclerView_sub_user);
        listView_sub_user =findViewById(R.id.listView_sub_user);
        tv_add_sub_user =findViewById(R.id.tv_add_sub_user);

        tv_add_sub_user.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AddSubUser.class));
            finish();
        });

        iv_tool_back_left.setOnClickListener(v -> {
            onBackPressed();
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subUSerList.isEmpty()){
            subUSerList.clear();
        }
    }

    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
//        SubUser model = subUSerList.get(position);
//        Toasty.success(mContext,"Welcome - "+model.getName(), Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(mContext, HomeActivity.class);
////        i.putExtra("city", model.name);
////        i.putExtra("desc", model.description);
////        i.putExtra("image", model.imageName);
//        startActivity(intent);
    }

    //================ Api Call ============================
    private void getShowSubUsersCall(final String token) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<ShowSubUserResponse> call = apiInterface.showSubUsers(token);

        call.enqueue(new Callback<ShowSubUserResponse>() {
            @Override
            public void onResponse(Call<ShowSubUserResponse> call, Response<ShowSubUserResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "Response >>>>" + str_response);
                int responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (response.isSuccessful()) {
                        ShowSubUserResponse sub_userResponse = response.body();
                        int status = sub_userResponse.getStatus();
//                        Log.e(TAG, "success :" + status);
                        if (status==200) {

                            subUSerList  = sub_userResponse.getUsers();
                            setAdapter();
//                            setAdapterListView();
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
            public void onFailure(Call<ShowSubUserResponse> call, Throwable t) {
                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }

    @Override
    public void delete() {
        getShowSubUsersCall(token);
    }


    class SubUserAdapter3 extends BaseAdapter {
        private String TAG= getClass().getSimpleName();
        private Context mContext;
        private List<SubUser> dataList =new ArrayList<SubUser>();
        private int lastPosition = -1;
        private ItemClickListener clickListener;
        private String account_status;
        private int user_id,sub_user_status;

        ApiInterface apiInterface;
        ProgressDialog pd;
        String token="";


        public SubUserAdapter3(Context mContext, List<SubUser> deviceModels) {
            this.dataList = deviceModels;
            this.mContext = mContext;

        }


        // API sub_user_status
        private void getSubUSerStatusCall(final String token, String user_id) {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Loading Please Wait...");
            pd.setCancelable(false);
            pd.show();

            Call<SubUserStatusResponse> call = apiInterface.sub_user_status(token, user_id);

            call.enqueue(new Callback<SubUserStatusResponse>() {
                @Override
                public void onResponse(Call<SubUserStatusResponse> call, Response<SubUserStatusResponse> response) {
                    pd.dismiss();
                    String str_response = new Gson().toJson(response.body());
//                    Log.e(TAG, "getSubUSerStatusCall Response >>>>" + str_response);
                    int responseCode  =response.code();
//                    Log.e(TAG, "responseCode :" + responseCode);
                    try {
                        if (responseCode ==200){
                            if (response.isSuccessful()) {
                                SubUserStatusResponse sub_userResponse = response.body();
                                int status = sub_userResponse.getStatus();
                                String massage = sub_userResponse.getMessage();
//                                Log.e(TAG, "success :" + status);
//                                Log.e(TAG, "massage :" + massage);
                                if (status==200) {

                                    sub_user_status = sub_userResponse.getSubUserStatus();
//                                    Log.e(TAG, "sub_user_status : " + sub_user_status);

                                } else {
                                    Log.e(TAG, "status Error "+status);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SubUserStatusResponse> call, Throwable t) {
                    pd.dismiss();
                    Log.e(TAG,"onFailure"+t.toString());
                }
            });
        }


        @Override
        public int getCount() {
            return dataList.size() ;
        }

        @Override
        public Object getItem(int position) {
            ///Current Item
            return position;
        }

        @Override
        public long getItemId(int position) {
            //Current Item's ID
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            MyViewHolder holder = null;
            if (view == null) {
                holder = new MyViewHolder();

                // LayoutInflater
//            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                final LayoutInflater sInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // Inflate Custom List View
                view = sInflater.inflate(R.layout.cardview_sub_user, null, false);

//            view = inflater.inflate(layoutResourceId, parent,false);
                holder.tv_name = view.findViewById(R.id.tv_name);
                holder.tv_mobileno = view.findViewById(R.id.tv_mobileno);
                holder.tv_email = view.findViewById(R.id.tv_email);
                holder.checkbox = view.findViewById(R.id.checkbox);
                view.setTag(holder);
            }else {
                holder = (MyViewHolder) view.getTag();
            }

            // access data to respect of position
            // find list card_view
            final SubUser modelList = dataList.get(position);
            account_status = modelList.getAccountStatus();
            user_id = modelList.getId();

//            Log.i(TAG,"user_id-"+user_id);
//            Log.i(TAG,"account_status-"+account_status);

            holder.tv_name.setText(modelList.getName());
            holder.tv_mobileno.setText(modelList.getMobile());
            holder.tv_email.setText(modelList.getEmail());



            if (account_status.equalsIgnoreCase("active")){
                holder.checkbox.setChecked(true);
            }else {
                holder.checkbox.setChecked(false);
            }

            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            token = AppPreferences.loadPreferences(mContext,"TOKEN");
//            Log.e(TAG,"token-"+token);


            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(((CheckBox)v).isChecked()){
                        v.setSelected(true);
                        String uId = String.valueOf(user_id);
//                        Log.e(TAG,"setOnClickListener checkbox is Checked ");

                        // ======= API Call ==========
                        if (CheckNetwork.isNetworkAvailable(mContext)) {
                            getSubUSerStatusCall(token,uId);
                        } else {
                            Toasty.warning(mContext, mContext.getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        v.setSelected(false);
//                        Log.e(TAG,"setOnClickListener Uncheckbox is Checked ");
                    }
                }
            });

            // TODO: 1/2/2020
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Saving Checked Position
                        mChecked.put(position, isChecked);

                        // Find if all the check boxes are true
                        if (isAllValuesChecked()) {

                            // set HeaderCheck box to true
                            checkBox_header.setChecked(isChecked);
                        }

                    } else {
                        // Removed UnChecked Position
                        mChecked.delete(position);

                        // Remove Checked in Header
//                        checkBox_header.setChecked(isChecked);
                    }
                }
            });

            //  Set CheckBox "TRUE" or "FALSE" if mChecked == true
            holder.checkbox.setChecked((mChecked.get(position) == true ? true : false));


            return view;
        }

        @Override
        public int getItemViewType(int position) {
//        return position % 4;
            return position;
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }


        // inner Holder class
        class MyViewHolder {
            TextView tv_name,tv_mobileno,tv_email;
            CheckBox checkbox;

        }

        private void setAnimation(View viewToAnimate, int position) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

        public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
            animator.setDuration(300);
            animator.setInterpolator(com.github.aakira.expandablelayout.Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
            return animator;
        }

    }


    /*
     * Find if all values are checked.
     */
    protected boolean isAllValuesChecked() {

        for (int i = 0; i < count; i++) {
            if (!mChecked.get(i)) {
                return false;
            }
        }

        return true;
    }



}
