package com.backstagesupporters.fasttrack.ui.adapters;


import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.SubUser;
import com.backstagesupporters.fasttrack.models.SubUserDeleteListner;
import com.backstagesupporters.fasttrack.responseClass.SubUserStatusResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.github.aakira.expandablelayout.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubUserAdapter extends RecyclerView.Adapter<SubUserAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<SubUser> dataList =new ArrayList<SubUser>();
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private int lastPosition = -1;
    private ItemClickListener clickListener;
    private String account_status, accountStatus;
    private int sub_user_status;

    ApiInterface apiInterface;
    ProgressDialog pd;
    SubUserDeleteListner listner;

    public SubUserAdapter(Context mContext, List<SubUser> deviceModels, SubUserDeleteListner listner) {
        this.dataList = deviceModels;
        this.mContext = mContext;
        this.listner = listner;
//        Log.i(TAG,"-- constructor");
    }

    public SubUserAdapter(final List<SubUser> data) {
        this.dataList = data;
        for (int i = 0; i < data.size(); i++) {
            expandState.append(i, false);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_sub_user, viewGroup, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);
        // find list card_view
        final SubUser modelList = dataList.get(position);
        account_status = modelList.getAccountStatus();
//        Log.i(TAG,"user_id-"+modelList.getId());
//        Log.i(TAG,"account_status-"+account_status);

        holder.tv_name.setText(mContext.getString(R.string.name)+" : "+ modelList.getName());
        holder.tv_mobileno.setText(mContext.getString(R.string.mobileno)+" : "+modelList.getMobile());
        holder.tv_email.setText(mContext.getString(R.string.email)+" : "+modelList.getEmail());
        holder.tv_subUserId.setText(Integer.toString(modelList.getId()));

        if (account_status.equalsIgnoreCase("active")){
            holder.checkbox.setChecked(true);
        }else {
            holder.checkbox.setChecked(false);
        }


        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
                String UId = holder.tv_subUserId.getText().toString().trim();

                if(((CheckBox)v).isChecked()){
                    v.setSelected(true);
//                    Log.e(TAG,"setOnClickListener checkbox is Checked ");
                    // ======= API Call ==========
                    if (CheckNetwork.isNetworkAvailable(mContext)) {
//                        getSubUSerStatusCall(token,uId);
                        accountStatus = "active";
                        getSubUSerStatusCall(token,UId,accountStatus);
                    } else {
                        Toasty.warning(mContext, mContext.getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    v.setSelected(false);
//                    Log.e(TAG,"setOnClickListener Uncheckbox is Checked ");

                    // ======= API Call ==========
                    if (CheckNetwork.isNetworkAvailable(mContext)) {

                        accountStatus = "deactive";
                        getSubUSerStatusCall(token,UId,accountStatus);
                    } else {
                        Toasty.warning(mContext, mContext.getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
                String UId = holder.tv_subUserId.getText().toString().trim();

                if (CheckNetwork.isNetworkAvailable(mContext)) {
                    deleteSubUser(token, UId);

                } else {
                    Toasty.warning(mContext, mContext.getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // API sub_user_status
    private void getSubUSerStatusCall( String token,String user_id, String mStatus) {
//        Log.w(TAG,"getSubUSerStatusCall user_id: "+user_id);
//        Log.d(TAG,"getSubUSerStatusCall accountStatus: "+mStatus);
//        Log.d(TAG,"getSubUSerStatusCall token-"+token);

        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        // getSubUSerStatusCall(token,uId,accountStatus);
//        Call<SubUserStatusResponse> call = apiInterface.sub_user_status(token, user_id);
        Call<SubUserStatusResponse> call = apiInterface.sub_user_status(token,user_id,mStatus);

        call.enqueue(new Callback<SubUserStatusResponse>() {
            @Override
            public void onResponse(Call<SubUserStatusResponse> call, Response<SubUserStatusResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.w(TAG, " Response >>>>" + str_response);
                int responseCode  =response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            SubUserStatusResponse sub_userResponse = response.body();
                            int status = sub_userResponse.getStatus();
                            String massage = sub_userResponse.getMessage();
//                            Log.e(TAG, "success :" + status);
//                            Log.e(TAG, "massage :" + massage);
                            if (status==200) {

                                sub_user_status = sub_userResponse.getSubUserStatus();
//                                Log.w(TAG, "sub_user_status : " + sub_user_status);

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
                Log.e(TAG,"onFailure: "+t.toString());
            }
        });
    }

    // API delete sub user
    private void deleteSubUser( String token,String user_id) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SubUserStatusResponse> call = apiInterface.delete_sub_user(token,user_id);

        call.enqueue(new Callback<SubUserStatusResponse>() {
            @Override
            public void onResponse(Call<SubUserStatusResponse> call, Response<SubUserStatusResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                int responseCode  = response.code();
                try {
                    if (responseCode == 200){
                        if (response.isSuccessful()) {
                            SubUserStatusResponse sub_userResponse = response.body();
                            int status = sub_userResponse.getStatus();
                            if (status == 200) {
//                                sub_user_status = sub_userResponse.getSubUserStatus();
                                listner.delete();
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
                Log.e(TAG,"onFailure: "+t.toString());
            }
        });
    }


    @Override
    public int getItemCount() {
//        return dataList.size() == 0 ? 0 : dataList.size();
        return dataList.size() ;
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
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name,tv_mobileno,tv_email, tv_subUserId;
        CheckBox checkbox;
        TextView btn_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_subUserId = itemView.findViewById(R.id.tv_subUserId);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mobileno =  itemView.findViewById(R.id.tv_mobileno);
            tv_email =  itemView.findViewById(R.id.tv_email);
            checkbox =  itemView.findViewById(R.id.checkbox);
            btn_delete = itemView.findViewById(R.id.btn_user_delete);
            itemView.setOnClickListener(this); // bind the listener

        }

        @Override
        public void onClick(View view) {
            // call the onClick in the OnItemClickListener
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

}