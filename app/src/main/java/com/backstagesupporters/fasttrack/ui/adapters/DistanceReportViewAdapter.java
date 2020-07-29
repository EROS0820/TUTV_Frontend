package com.backstagesupporters.fasttrack.ui.adapters;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.DistanceReport;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.github.aakira.expandablelayout.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class DistanceReportViewAdapter extends RecyclerView.Adapter<DistanceReportViewAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<DistanceReport> dataList =new ArrayList<DistanceReport>();
    private int lastPosition = -1;
    private ItemClickListener clickListener;
    private String vehicle_no, date, distance;

    // RoundingMode
    private static DecimalFormat decimalFormat = new DecimalFormat("##.####");


    public DistanceReportViewAdapter(Context mContext, List<DistanceReport> deviceModels) {
        this.dataList = deviceModels;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_distance_report, viewGroup, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);
        // find list card_view
        final DistanceReport modelList = dataList.get(position);
        vehicle_no =  modelList.getVehicleNo();
        date =  modelList.getDate();
        distance =  modelList.getDistance();
//        Log.i(TAG,"vehicle_no-"+vehicle_no);

        holder.tv_vehicle_number.setText(vehicle_no);
        holder.tv_date.setText(date);

        // System.out.println("double : " + df2.format(input));    //3.14
        float mDistance = Float.parseFloat(distance);
        String myDistance = decimalFormat.format(mDistance);
//        Log.w(TAG,"Rounding myDistance: "+myDistance);

        holder.tv_distance.setText(myDistance +" K.M.");

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
        TextView tv_vehicle_number,tv_date,tv_distance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_vehicle_number = itemView.findViewById(R.id.tv_vehicle_number);
            tv_date =  itemView.findViewById(R.id.tv_date);
            tv_distance =  itemView.findViewById(R.id.tv_distance);

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
