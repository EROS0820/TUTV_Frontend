package com.backstagesupporters.fasttrack.ui.adapters;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.NotificationModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<NotificationModel> dataList =new ArrayList<NotificationModel>();
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private int lastPosition = -1;
    private ItemClickListener clickListener;

    String title, message;

    public NotificationsAdapter(Context mContext, List<NotificationModel> deviceModels) {
        this.dataList = deviceModels;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_notification, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);

        // find list card_view
        final NotificationModel modelList = dataList.get(position);
        title = modelList.getTitle();
        message = modelList.getBody();
        holder.titleView.setText(title);
        holder.messageView.setText(message);
//        Log.i(TAG,"title-"+title);

    }



    private void callTollMethod(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
//        callIntent.setData(Uri.parse("tel:18001213201"));
        callIntent.setData(Uri.parse("tel:"+phone));
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mContext.startActivity(callIntent);
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
        TextView titleView,messageView;
        LinearLayout ll_phone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

//            ll_phone = itemView.findViewById(R.id.ll_phone);
            titleView =  itemView.findViewById(R.id.titleView);
            messageView =  itemView.findViewById(R.id.messageView);

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


}
