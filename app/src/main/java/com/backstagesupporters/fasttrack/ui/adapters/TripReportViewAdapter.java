package com.backstagesupporters.fasttrack.ui.adapters;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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
import com.backstagesupporters.fasttrack.models.TripModel;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.github.aakira.expandablelayout.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TripReportViewAdapter extends RecyclerView.Adapter<TripReportViewAdapter.MyViewHolder> {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<TripModel> dataList =new ArrayList<TripModel>();
    private int lastPosition = -1;
    private ItemClickListener clickListener;
    private String vehicle_no,distance,trip, startAddress, startTime,endAddress, endTime,
            startLat, startLong, endLat, endLong,start_date,end_date;

    // RoundingMode
    private static DecimalFormat decimalFormat = new DecimalFormat("##.####");



    public TripReportViewAdapter(Context mContext, List<TripModel> deviceModels) {
        this.dataList = deviceModels;
        this.mContext = mContext;
//        Log.i(TAG,"-- constructor");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_trip_report, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
//        setAnimation(holder.itemView, position);
        // find list card_view
        final TripModel modelList = dataList.get(position);
//        vehicle_no =  modelList.getVehicleNo();
        trip = modelList.getTripNo();
        distance = modelList.getDistance();
        startTime = modelList.getStartTime();
        start_date = modelList.getStartDate();
        endTime = modelList.getEndTime();
        end_date = modelList.getEndDate();

        startLat = modelList.getStartLatitude();
        startLong = modelList.getStartLongitude();
        endLat = modelList.getEndLatitude();
        endLong = modelList.getEndLongitude();

//        Log.i(TAG,"trip : "+trip);
//        Log.i(TAG,"distance : "+distance);
//        Log.i(TAG,"start_date : "+start_date);
//        Log.i(TAG,"startTime : "+startTime);
//        Log.i(TAG,"endTime : "+endTime);
//        Log.i(TAG,"end_date : "+end_date);
//        Log.i(TAG,"startLat : "+startLat);
//        Log.i(TAG,"startLong : "+startLong);

        if (startLat != null){
            if (startLong != null)
                startAddress = getStartAddressLocation(startLat,startLong);
        }

        if (endLat != null){
            if (endLong != null)
                endAddress = getEndAddressLocation(endLat,endLong);
        }

        holder.tv_startAddress.setText(startAddress);
        holder.tv_startTime.setText(startTime+"  HH:mm:ss");
        holder.tv_startDate.setText(start_date +"  YYYY-MM-DD");
        holder.tv_trip.setText("Trip : "+trip);
        holder.tv_endAddress.setText(endAddress);
        holder.tv_endTime.setText(endTime +"  HH:mm:ss");
        holder.tv_endDate.setText(end_date +"  YYYY-MM-DD");

        // System.out.println("double : " + df2.format(input));    //3.14
        float mDistance = Float.parseFloat(distance);
        String myDistance = decimalFormat.format(mDistance);
//        Log.w(TAG,"Rounding mDistance: "+mDistance);
        holder.tv_distance.setText(myDistance +" K.M.");

    }


    private String getStartAddressLocation(String startLat, String startLong) {
        double latitude = Double.parseDouble(startLat);
        double longitude = Double.parseDouble(startLong);
        String address = null;
        try {
           address = getAddress(latitude,longitude);
//            Log.e(TAG,"address Complete : "+address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private String getEndAddressLocation(String endLat, String endLong) {
        double latitude = Double.parseDouble(endLat);
        double longitude = Double.parseDouble(endLong);
        String address = null;
        try {
            address = getAddress(latitude,longitude);
//            Log.e(TAG,"address Complete : "+address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private String getAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mContext, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        String addressComplete =  address + " "+city + " "+ state +  " "+postalCode;

//        Log.e(TAG,"knownName : "+knownName);

        String Locality = addresses.get(0).getLocality();
//        Log.e(TAG,"Locality : "+Locality);

        return addressComplete;
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
        TextView tv_distance, tv_trip, tv_startAddress, tv_startTime,tv_endAddress, tv_endTime,
                tv_startDate, tv_endDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_startAddress = itemView.findViewById(R.id.tv_startAddress);
            tv_startTime =  itemView.findViewById(R.id.tv_startTime);
            tv_distance =  itemView.findViewById(R.id.tv_distance);
            tv_trip =  itemView.findViewById(R.id.tv_trip);
            tv_endAddress =  itemView.findViewById(R.id.tv_endAddress);
            tv_endTime =  itemView.findViewById(R.id.tv_endTime);
            tv_startDate =  itemView.findViewById(R.id.tv_startDate);
            tv_endDate =  itemView.findViewById(R.id.tv_endDate);

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


/**
 * https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
 * https://javapapers.com/android/android-get-address-with-street-name-city-for-location-with-geocoding/
 *
 */