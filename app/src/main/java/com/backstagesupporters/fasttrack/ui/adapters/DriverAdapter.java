package com.backstagesupporters.fasttrack.ui.adapters;


import android.animation.ObjectAnimator;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.EditDriverActivity;
import com.backstagesupporters.fasttrack.models.DriverShow;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.backstagesupporters.fasttrack.utils.MySingleton;
import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<DriverShow> dataList =new ArrayList<>();
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private int lastPosition = -1;
    private ItemClickListener clickListener;
    String driver_photo, driver_name,driver_phone,driver_email, driver_id;

    //    private MovieResponse[] mMovies;
//    private  LanguageModel myLanguageList[];

    public DriverAdapter(Context mContext, List<DriverShow> data) {
        this.dataList = data;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }

    public DriverAdapter(final List<DriverShow> data) {
        this.dataList = data;
        for (int i = 0; i < data.size(); i++) {
            expandState.append(i, false);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_driver, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);

        /**
         *  {
         *             "driver_id": 9,
         *             "driver_name": "test2",
         *             "driver_phone": "123456",
         *             "driver_email": "test2@t.com",
         *             "driver_gander": "m",
         *             "driver_state": "dl",
         *             "driver_city": "dl",
         *             "driver_pincode": "11",
         *             "driver_country": "in",
         *             "driver_pan_no": "pa5654",
         *             "driver_aadhar_no": "4556446556",
         *             "driver_drive_licence_no": "Dl56554",
         *             "driver_idproof_image": null,
         *             "driver_pan_image": null,
         *             "driver_aadhar_image": null,
         *             "driver_drive_licence_image": null,
         *             "driver_photo": null,
         *             "driver_dob": "4/7/2000",
         *             "user_id": "1"
         *         }
         */

        // find list card_view
        final DriverShow modelList = dataList.get(position);
        driver_id = String.valueOf(modelList.getDriverId());
        driver_name = modelList.getDriverName();
        driver_phone = modelList.getDriverPhone();

        holder.setIsRecyclable(false);
        holder.tv_name.setText(driver_name);
        holder.tv_licence_number.setText(modelList.getDriverDriveLicenceNo());
        holder.tv_phone_number.setText(driver_phone);
        driver_photo = modelList.getDriverPhoto();
//        holder.cv_thumbnail.setImageResource(modelList.getDriverPhoto());
        Glide.with(mContext)
                .load(driver_photo)
                .placeholder(R.drawable.profile_default)
                .into(holder.cv_thumbnail);

        holder.expandableLayout.setInRecyclerView(true);
//        holder.expandableLayout.setInterpolator(modelList.interpolator);
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
                        String phone = holder.tv_phone_number.getText().toString().trim();

//                        Toasty.success(mContext,"Call the driver :"+phone, Toast.LENGTH_SHORT).show();
                        callByDialorMethod(phone);
                    }
                });

                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Using the Singleton and set data
                        DriverShow driverList = dataList.get(position);
                        MySingleton.getInstance().setDriverShow(driverList);
//                        Log.i(TAG, "MySingleton getDriverShow : " +  MySingleton.getInstance().getDriverShow().toString());
                        // startActivity
                        Intent intent = new Intent(mContext,EditDriverActivity.class);
                        mContext.startActivity(intent);
                    }
                });

                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
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
        // relative_driver
        holder.relative_driver.setOnClickListener(new View.OnClickListener() {
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


    // inner Holder class
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView cv_thumbnail;
        RelativeLayout buttonLayout, relative_driver;
        ImageView ivUpDown;
        TextView tv_name,tv_licence_number,tv_phone_number;
        TextView  btn_call, btn_edit, btn_delete;
        /**
         * You must use the ExpandableLinearLayout in the recycler view.
         * The ExpandableRelativeLayout doesn't work.
         */
        public ExpandableLinearLayout expandableLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_driver_name);
            tv_licence_number =  itemView.findViewById(R.id.tv_driver_licence_number);
            tv_phone_number = itemView.findViewById(R.id.tv_phone_number);
            ivUpDown = itemView.findViewById(R.id.ivUpDown);
            cv_thumbnail = itemView.findViewById(R.id.cv_thumbnail_driver);
            buttonLayout =  itemView.findViewById(R.id.btnUpDown);
            relative_driver =  itemView.findViewById(R.id.relative_driver);
            expandableLayout =  itemView.findViewById(R.id.expandableLayout_driver);
            btn_call =  itemView.findViewById(R.id.btn_call);
            btn_edit =  itemView.findViewById(R.id.btn_edit);
            btn_delete =  itemView.findViewById(R.id.btn_delete);

            itemView.setOnClickListener(this); // bind the listener

        }

        @Override
        public void onClick(View view) {
            // call the onClick in the OnItemClickListener
            if (clickListener != null)
                clickListener.onClick(view, getAdapterPosition());
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
