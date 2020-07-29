package com.backstagesupporters.fasttrack.ui.adapters;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<Vehicle> dataList =new ArrayList<Vehicle>();
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private int lastPosition = -1;
    private ItemClickListener clickListener;
    private String vehicle_brand,vehicle_name,vehicle_type, vehicle_no, vehicle_image_url,owner_id;


    public VehicleAdapter(Context mContext, List<Vehicle> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
//        Log.i(TAG,"-- constructor");
//        Log.e(TAG,"List sise :"+ this.dataList.size());
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_vehicle, viewGroup, false);
//        Log.i(TAG,"-- onCreateViewHolder");
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);

        /**
         * "vehicle_brand": "Volkswagen",
         *             "vehicle_name": "Vento",
         *             "vehicle_type": "Motor Car",
         *             "vehicle_no": "DL 14CD0210",
         *             "vehicle_image": null,
         *             "owner_id": "Abhishek",
         */
        // find list card_view
        final Vehicle modelList = dataList.get(position);
//        vehicle_name = modelList.getVehicleName();
        vehicle_type = modelList.getVehicleType();
        vehicle_no = modelList.getVehicleNo();
        vehicle_brand = modelList.getVehicleNo();
        owner_id = modelList.getOwnerId();
        vehicle_image_url = modelList.getVehicleImage(); // vehicle_image

        holder.setIsRecyclable(false);
        holder.tv_vehicle_name.setText(vehicle_no);
        holder.tv_vehicle_type.setText("Vehicle type : "+vehicle_type);
        holder.tv_vehicle_brand.setText("Vehicle brand : "+vehicle_brand);
        holder.tv_vehicle_no.setText("Vehicle No : "+vehicle_no);
        holder.tv_owner_id.setText("Owner id : "+owner_id);

//        Log.e(TAG,"vehicle_image URL : "+vehicle_image_url);
//        holder.cv_thumbnail.setImageResource(modelList.getVehicleName());
        Glide.with(mContext)
                .load(vehicle_image_url)
                .placeholder(R.drawable.ic_car_white)
                .error(R.drawable.ic_car_white)  // any image in case of error
                .override(50, 50)
                .centerCrop()
                .into(holder.cv_thumbnail);



        // https://github.com/bumptech/glide
       /*
       Glide.with(this)
        .load("url here") // image url
        .placeholder(R.drawable.placeholder) // any placeholder to load at start
        .error(R.drawable.imagenotfound)  // any image in case of error
        .override(200, 200); // resizing
        .centerCrop();
        .into(imageView);  // imageview object
                */


        holder.expandableLayout.setInRecyclerView(true);
//        holder.expandableLayout.setInterpolator(modelList.getInterpolator());
        holder.expandableLayout.setInterpolator(Utils.createInterpolator(Utils.ACCELERATE_INTERPOLATOR));
        holder.expandableLayout.setExpanded(expandState.get(position));
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
//                createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                createRotateAnimator(holder.buttonLayout, 0f, 90f).start();
                expandState.put(position, true);

                holder.btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toasty.success(mContext,"Development Mode", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toasty.success(mContext,"Development Mode", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onPreClose() {
//                createRotateAnimator(holder.buttonLayout, 180f, 0f).start();
                createRotateAnimator(holder.buttonLayout, 90f, 0f).start();
                expandState.put(position, false);
            }
        });

//        holder.buttonLayout.setRotation(expandState.get(position) ? 180f : 0f);
        holder.buttonLayout.setRotation(expandState.get(position) ? 90f : 0f);
        holder.relative_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickButton(holder.expandableLayout);
            }
        });

    }


    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    @Override
    public int getItemCount() {
//        return dataList.size() == 0 ? 0 : dataList.size();
        return dataList.size();
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
        CircleImageView cv_thumbnail;
        RelativeLayout buttonLayout,relative_vehicle;
        ImageView ivUpDown;
        TextView tv_vehicle_name,tv_vehicle_type,tv_vehicle_no,tv_owner_id,tv_vehicle_brand,btn_call,btn_edit;
        /**
         * You must use the ExpandableLinearLayout in the recycler view.
         * The ExpandableRelativeLayout doesn't work.
         */
        public ExpandableLinearLayout expandableLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_vehicle_name = itemView.findViewById(R.id.tv_name_vehicle);
            tv_vehicle_type =  itemView.findViewById(R.id.tv_vehicle_type);
            tv_vehicle_no =  itemView.findViewById(R.id.tv_vehicle_no);
            tv_vehicle_brand =  itemView.findViewById(R.id.tv_vehicle_brand);
            tv_owner_id = itemView.findViewById(R.id.tv_owner_id);
            ivUpDown = itemView.findViewById(R.id.ivUpDown);
            cv_thumbnail = itemView.findViewById(R.id.cv_thumbnail_vehicle);
            buttonLayout =  itemView.findViewById(R.id.btnUpDown);
            relative_vehicle =  itemView.findViewById(R.id.relative_vehicle);
            expandableLayout =  itemView.findViewById(R.id.expandableLayout_vehicle);
            btn_call =  itemView.findViewById(R.id.btn_call);
            btn_edit =  itemView.findViewById(R.id.btn_edit);

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
