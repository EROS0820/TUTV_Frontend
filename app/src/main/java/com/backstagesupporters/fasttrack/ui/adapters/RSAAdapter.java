package com.backstagesupporters.fasttrack.ui.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.RSAModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class RSAAdapter extends RecyclerView.Adapter<RSAAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<RSAModel> dataList =new ArrayList<RSAModel>();
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private int lastPosition = -1;
    private ItemClickListener clickListener;


    public RSAAdapter(Context mContext, List<RSAModel> deviceModels) {
        this.dataList = deviceModels;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_rsa, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);

        // find list card_view
        final RSAModel modelList = dataList.get(position);
//        Log.i(TAG,"getNameVehicle-"+modelList.getName());
        holder.tv_name.setText(modelList.getName());
        holder.tv_phone.setText(modelList.getPhone());

        holder.ll_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone =  modelList.getPhone();
//                Log.e(TAG,"phone : "+phone);
                callByDialorMethod(phone);
            }
        });
    }


    private void callByDialorMethod(String phone) {
//        Log.e(TAG,"phone :"+phone);

        // Use format with "tel:" and phone number to create phoneNumber.
        if (phone!=null){
            Intent intent = new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse("tel:0123456789"));
            intent.setData(Uri.parse("tel:"+phone));
            mContext.startActivity(intent);
        }else {
            Toast.makeText(mContext, "Number not found", Toast.LENGTH_SHORT).show();
        }

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
        TextView tv_phone, tv_name;
        RelativeLayout ll_phone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_phone = itemView.findViewById(R.id.ll_phone);
            tv_phone =  itemView.findViewById(R.id.tv_phone);
            tv_name =  itemView.findViewById(R.id.tv_name);

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
