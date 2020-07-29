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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.AddDeviceModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddDeviceAdapter extends RecyclerView.Adapter<AddDeviceAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<AddDeviceModel> dataList =new ArrayList<>();
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private int lastPosition = -1;
    private ItemClickListener clickListener;


    //    private MovieResponse[] mMovies;
//    private  LanguageModel myLanguageList[];

    public AddDeviceAdapter(Context mContext, List<AddDeviceModel> language) {
        this.dataList = language;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }

    public AddDeviceAdapter(final List<AddDeviceModel> data) {
        this.dataList = data;
        for (int i = 0; i < data.size(); i++) {
            expandState.append(i, false);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_add_device, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);
        // find list card_view
        final AddDeviceModel modelList = dataList.get(position);
//        Log.i(TAG,"getNameVehicle-"+modelList.getName());

        holder.setIsRecyclable(false);
        holder.tv_name_addDevice.setText(modelList.getName());
        holder.tv_details_addDevice.setText(modelList.getDetails());
        holder.cv_thumbnail.setImageResource(modelList.getImage_id());

//        holder.itemView.setBackgroundResource(R.drawable.shape_button_blue_light);
        holder.expandableLayout.setInRecyclerView(true);
//        holder.expandableLayout.setBackgroundColor(ContextCompat.getColor(mContext, item.colorId2));
//        holder.expandableLayout.setBackgroundResource(R.drawable.shape_button_blue10);
//        holder.expandableLayout.setInterpolator(modelList.interpolator);
//        holder.expandableLayout.setInterpolator(modelList.getInterpolator());
        holder.expandableLayout.setExpanded(expandState.get(position));
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
//                createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                createRotateAnimator(holder.buttonLayout, 0f, 90f).start();
                expandState.put(position, true);

                // call item
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
        holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
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
        return dataList.size() == 0 ? 0 : dataList.size();
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
        RelativeLayout buttonLayout;
        ImageView ivUpDown;
        TextView tv_name_addDevice,tv_details_addDevice;
        /**
         * You must use the ExpandableLinearLayout in the recycler view.
         * The ExpandableRelativeLayout doesn't work.
         */
        public ExpandableLinearLayout expandableLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_addDevice = itemView.findViewById(R.id.tv_name_addDevice);
            tv_details_addDevice = itemView.findViewById(R.id.tv_details_addDevice);
            ivUpDown = itemView.findViewById(R.id.ivUpDown);
            cv_thumbnail = itemView.findViewById(R.id.cv_thumbnail_addDevice);
            buttonLayout =  itemView.findViewById(R.id.btnUpDown);
            expandableLayout =  itemView.findViewById(R.id.expandableLayout_addDevice);


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
