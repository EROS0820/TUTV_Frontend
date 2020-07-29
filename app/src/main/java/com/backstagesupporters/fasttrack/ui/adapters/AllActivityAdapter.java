package com.backstagesupporters.fasttrack.ui.adapters;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.CarAllActivity;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class AllActivityAdapter extends RecyclerView.Adapter<AllActivityAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<CarAllActivity> dataList =new ArrayList<CarAllActivity>();
    private int lastPosition = -1;
    private ItemClickListener clickListener;

    public AllActivityAdapter(Context mContext, List<CarAllActivity> dataModels) {
        this.mContext = mContext;
        this.dataList = dataModels;
//        Log.i(TAG,"-- dataList size: "+dataList.size());
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
        String vehicle_id,title,sos, description,created_at, activity_type;
        CarAllActivity  modelList = dataList.get(position);
//        add5H30M()
        if (modelList !=null){
            vehicle_id = modelList.getVehicleId();
            title = modelList.getTitle();
            sos = modelList.getSos();
            activity_type = modelList.getActivityType();
//            Log.e(TAG,"vehicle_id-"+vehicle_id);
//            Log.i(TAG,"title-"+title);

            if (!vehicle_id.isEmpty()) {
                holder.titleView.setText(title);
                holder.messageView.setText(modelList.getDescription());
                holder.tv_created_at.setText(modelList.getCreatedAt());
                holder.tv_activity_type.setText(activity_type);


                //  "activity_type": "engine",parking
                if (activity_type.equalsIgnoreCase("parking")) {
                    holder.imageViewTitle.setBackgroundResource(R.drawable.car_parked2);
                    holder.imageViewActivityType.setBackgroundResource(R.drawable.car_parked2);
                }else if (activity_type.equalsIgnoreCase("engine")){
                    holder.imageViewTitle.setBackgroundResource(R.drawable.engine_g);
                    holder.imageViewActivityType.setBackgroundResource(R.drawable.engine_g);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // inner Holder class
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleView,messageView,tv_activity_type,tv_created_at;
        ImageView imageViewActivityType, imageViewTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView =  itemView.findViewById(R.id.titleView);
            messageView =  itemView.findViewById(R.id.messageView);
            tv_created_at = itemView.findViewById(R.id.tv_created_at);
            tv_activity_type = itemView.findViewById(R.id.tv_activity_type);
            imageViewTitle = itemView.findViewById(R.id.imageViewTitle);
            imageViewActivityType = itemView.findViewById(R.id.imageViewActivityType);

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
