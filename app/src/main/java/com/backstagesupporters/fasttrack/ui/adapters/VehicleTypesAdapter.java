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
import com.backstagesupporters.fasttrack.models.VehicleTypesModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class VehicleTypesAdapter extends RecyclerView.Adapter<VehicleTypesAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private List<VehicleTypesModel> languageList =new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;
    protected List<?> dataList = new ArrayList<>();
    private ItemClickListener clickListener;

    //    private MovieResponse[] mMovies;
//    private  LanguageModel myLanguageList[];

    public VehicleTypesAdapter(Context mContext, List<VehicleTypesModel> language) {
        this.languageList = language;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }
    public VehicleTypesAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_vehicle_type, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {
        setAnimation(viewHolder.itemView, position);

        // find list card_view
        final VehicleTypesModel modelList = languageList.get(position);
//        Log.i(TAG,"VehicleTypes-"+modelList.getName());
//        viewHolder.nameLanguage.setText(languageList.get(position).getNameLanguage());
//        viewHolder.imgPhoto.setImageResource(languageList.get(position).getImage_id());
        viewHolder.nameLanguage.setText(modelList.getName());
        viewHolder.imgPhoto.setImageResource(modelList.getImageIcon());
        viewHolder.relativeLayoutCard.setBackgroundResource(modelList.getImageBgId());
        viewHolder.cardView.setBackgroundResource(modelList.getImageBgId());

        viewHolder.cardView.setPreventCornerOverlap(false); //it is very important
        viewHolder.cardView.setUseCompatPadding(false); //it is very important

//        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toasty.success(mContext,"Welcome - "+modelList.getName(), Toast.LENGTH_SHORT).show();
////                Intent intent = new Intent(mContext, NumberOfVehicleActivity.class);
//////                intent.putExtra(OrderDetailActivity.ARG_ORDER_ID, modelList.getNameLanguage());
////                view.getContext().startActivity(intent);
//                Log.d(TAG, "><onCardClick(view=" + view + ", position=" + position + ", viewHolder=" + viewHolder + ")");
//                Log.i(TAG,"called  NumberOfVehicleActivity");
//            }
//        });

        if (getItemViewType(position) == 5) {
            viewHolder.imgPhoto.setVisibility(View.GONE);

        }

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
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgPhoto;
        TextView nameLanguage;
        MaterialCardView cardView;
        RelativeLayout relativeLayoutCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameLanguage = (TextView) itemView.findViewById(R.id.tv_titleLanguage);
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
