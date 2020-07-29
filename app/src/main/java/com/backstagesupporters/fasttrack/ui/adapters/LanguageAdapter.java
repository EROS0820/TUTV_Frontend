package com.backstagesupporters.fasttrack.ui.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.backstagesupporters.fasttrack.models.LanguageModel;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private List<LanguageModel> languageList =new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;
    protected List<?> dataList = new ArrayList<>();
    String languageTitle;

    //    private MovieResponse[] mMovies;
//    private  LanguageModel myLanguageList[];

    public LanguageAdapter(Context mContext, List<LanguageModel> language) {
        this.languageList = language;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }
    public LanguageAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_language, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {
        setAnimation(viewHolder.itemView, position);
//        Log.e(TAG,"myLanguageList-"+languageList.toString());
        // find list card_view
        final LanguageModel modelList = languageList.get(position);
        languageTitle = modelList.getNameLanguage();
//        Log.i(TAG,"Language title-"+languageTitle);
//        viewHolder.imgPhoto.setChecked(list.get(position).isSelected());
//        viewHolder.nameLanguage.setText(languageList.get(position).getNameLanguage());
//        viewHolder.imgPhoto.setImageResource(languageList.get(position).getImage_id());
        if (languageTitle.equalsIgnoreCase("Kannad") || languageTitle.equalsIgnoreCase("Urdu")){
            viewHolder.nameLanguage.setText(modelList.getNameLanguage());
            //        viewHolder.imgPhoto.setImageResource(modelList.getImage_id());
//            viewHolder.relativeLayoutCard.setBackgroundResource(modelList.getImage_id());
            viewHolder.cardView.setBackgroundResource(modelList.getImage_id());
            viewHolder.cardView.setPreventCornerOverlap(false); //it is very important
            viewHolder.cardView.setUseCompatPadding(false); //it is very important
        }{
            viewHolder.relativeLayoutCard.setBackgroundResource(modelList.getImage_id());
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.success(mContext,"Welcome - "+modelList.getNameLanguage(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(mContext, VehicleTypesActivity.class);
////                intent.putExtra(OrderDetailActivity.ARG_ORDER_ID, modelList.getNameLanguage());
//                view.getContext().startActivity(intent);
//                AppPreferences.savePreferences(mContext, VariablesConstant.LANGUAGE, modelList.getNameLanguage());
            }
        });

    }


    @Override
    public int getItemCount() {
//        return dataList.size() == 0 ? 0 : dataList.size();
        return languageList.size();
    }


    @Override
    public int getItemViewType(int position) {
//        return position % 4;
        return position;
    }


    public void add(int position, String item) {
//        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
//        values.remove(position);
        notifyItemRemoved(position);
    }

    // inner Holder class
    class MyViewHolder extends RecyclerView.ViewHolder {
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

        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }


}
