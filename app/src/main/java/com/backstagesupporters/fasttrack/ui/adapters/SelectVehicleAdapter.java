package com.backstagesupporters.fasttrack.ui.adapters;


import android.content.Context;
import android.util.Log;
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
import com.backstagesupporters.fasttrack.models.SelectVehicleModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SelectVehicleAdapter extends RecyclerView.Adapter<SelectVehicleAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private List<SelectVehicleModel> languageList =new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;
    protected List<?> dataList = new ArrayList<>();
    private ItemClickListener clickListener;

    //    private MovieResponse[] mMovies;
//    private  LanguageModel myLanguageList[];

    public SelectVehicleAdapter(Context mContext, List<SelectVehicleModel> language) {
        this.languageList = language;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }
    public SelectVehicleAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_select_vehicle, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {
        setAnimation(viewHolder.itemView, position);
        // find list card_view
        final SelectVehicleModel modelList = languageList.get(position);
//        Log.i(TAG,"getNumberSelectVehicle-"+modelList.getNumberSelectVehicle());
//        viewHolder.imgPhoto.setChecked(list.get(position).isSelected());
//        viewHolder.nameLanguage.setText(languageList.get(position).getNameLanguage());
//        viewHolder.imgPhoto.setImageResource(languageList.get(position).getImage_id());
        viewHolder.tvNumberSelectVehicle.setText(modelList.getNumberSelectVehicle());
//        viewHolder.imgPhoto.setImageResource(modelList.getImage_id());
        viewHolder.relativeLayoutCard.setBackgroundResource(modelList.getImage_id());
        viewHolder.cardView.setBackgroundResource(modelList.getImage_id());
        viewHolder.cardView.setPreventCornerOverlap(false); //it is very important
        viewHolder.cardView.setUseCompatPadding(false); //it is very important

//        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toasty.success(mContext,"Welcome - "+modelList.getNameLanguage(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(mContext, VehicleTypesActivity.class);
////                intent.putExtra(OrderDetailActivity.ARG_ORDER_ID, modelList.getNameLanguage());
//                view.getContext().startActivity(intent);
//            }
//        });

    }


    @Override
    public int getItemCount() {
//        return dataList.size() == 0 ? 0 : dataList.size();
        return languageList.size() == 0 ? 0 : languageList.size();
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
        ImageView imgPhoto;
        TextView tvNumberSelectVehicle;
        MaterialCardView cardView;
        RelativeLayout relativeLayoutCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumberSelectVehicle = (TextView) itemView.findViewById(R.id.tv_titleSelect);
            imgPhoto = (ImageView) itemView.findViewById(R.id.iv_thumbnailLanguage);
            cardView = (MaterialCardView) itemView.findViewById(R.id.card_view);
            relativeLayoutCard = (RelativeLayout) itemView.findViewById(R.id.rl_card);

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
