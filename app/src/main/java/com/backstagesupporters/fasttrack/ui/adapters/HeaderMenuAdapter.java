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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.HeadMenuModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.ArrayList;
import java.util.List;


public class HeaderMenuAdapter extends RecyclerView.Adapter<HeaderMenuAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<HeadMenuModel> dataList =new ArrayList<>();
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private int lastPosition = -1;
    private ItemClickListener clickListener;
    String profilePic;



    public HeaderMenuAdapter(Context mContext, List<HeadMenuModel> data) {
        this.dataList = data;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }

    public HeaderMenuAdapter(final List<HeadMenuModel> data) {
        this.dataList = data;
        for (int i = 0; i < data.size(); i++) {
            expandState.append(i, false);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_menu_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        setAnimation(holder.itemView, position);

        // find list card_view
        final HeadMenuModel modelList = dataList.get(position);
//        Log.i(TAG,"title :"+modelList.getTitle());
        holder.setIsRecyclable(false);
        holder.tv_report_head1.setText(modelList.getTitle());
        holder.cv_thumbnail1.setImageResource(modelList.getImage_id());

        holder.expandableLayout.setInRecyclerView(true);
        holder.expandableLayout.setInterpolator(modelList.getInterpolator());
        holder.expandableLayout.setExpanded(expandState.get(position));
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
//                createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                createRotateAnimator(holder.buttonLayout, 0f, 90f).start();
                expandState.put(position, true);

                holder.relative_report1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, modelList.getTitle(), Toast.LENGTH_SHORT).show();
//
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
        ImageView cv_thumbnail,cv_thumbnail1,ivUpDown;
        RelativeLayout buttonLayout,relative_report;
        LinearLayout relative_report1;
        TextView tv_report_head,tv_report_head1;

        public ExpandableLinearLayout expandableLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            tv_report_head = itemView.findViewById(R.id.tv_report_head);
//            relative_report = itemView.findViewById(R.id.relative_report);
//            buttonLayout =  itemView.findViewById(R.id.btnUpDown_head);
//            ivUpDown = itemView.findViewById(R.id.ivUpDown);

            cv_thumbnail1 = itemView.findViewById(R.id.cv_thumbnail_head1);
            tv_report_head1 =  itemView.findViewById(R.id.tv_report_head1);
            relative_report1 =  itemView.findViewById(R.id.relative_report1);
//            expandableLayout =  itemView.findViewById(R.id.expandableLayout_head);

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
