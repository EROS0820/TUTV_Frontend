package com.backstagesupporters.fasttrack.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.EngineStatusResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.activity.LiveTrackingActivity;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.backstagesupporters.fasttrack.utils.MySingleton;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.backstagesupporters.fasttrack.utils.vehicle.MyLocationAddress;
import com.backstagesupporters.fasttrack.utils.vehicle.VehicleTypesAndColors;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VehicleHomeAdapter extends RecyclerView.Adapter<VehicleHomeAdapter.MyViewHolder> implements Filterable {
//        private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private List<Vehicle> dataList =new ArrayList<Vehicle>();
    private ArrayList<Vehicle> arrayList;
    private int lastPosition = -1;
    private ItemClickListener clickListener;
    //=Interface Declaration
    ProgressDialog pd;
    private double lat,lng;
    private int mEngineStatus;

    public VehicleHomeAdapter(Context mContext, List<Vehicle> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.arrayList = new ArrayList<Vehicle>();
        this.arrayList.addAll(dataList);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_vehicle_home, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
//        setAnimation(holder.itemView, position);

        // find list card_view
        final Vehicle modelList = dataList.get(position);
        String vehicle_id = String.valueOf(modelList.getVehicleId());
        String vehicle_name = modelList.getVehicleName();
        String vehicle_no = modelList.getVehicleNo(); // vehicle_no
        String vehicle_type = modelList.getVehicleType();
        String vehicle_color = modelList.getColor();
        String vehicle_image_url = modelList.getVehicleImage(); // vehicle_image
//        String positionStatus = modelList.getPositionStatus();
        String positionStatus2 = modelList.getPositions();
        String driver_number = modelList.getDriverNumber();
        String emergency_number = modelList.getEmergencyNumber();
        mEngineStatus = modelList.getEngineStatus();
        String speed = modelList.getSpeed();
        String speedLimit = modelList.getSpeedLimit();
        String Latitude = modelList.getLatitude();
        String Longitude = modelList.getLongitude();

        Log.w(TAG,"vehicle_type: "+vehicle_type);
//        Log.v(TAG,"My vehicle_id: "+ vehicle_id);
        Log.w(TAG,"vehicle_no: "+vehicle_no);
//        Log.w(TAG,"vehicle_image URL : "+vehicle_image_url);
//        Log.w(TAG,"vehicle_name: "+vehicle_name);
//        Log.w(TAG,"vehicle_name: "+vehicle_name);
//        Log.w(TAG,"positionStatus2: "+positionStatus2);
        Log.e(TAG,"speed: "+speed);
        Log.w(TAG,"speedLimit: "+speedLimit);
        Log.w(TAG,"Latitude: "+Latitude);
        Log.w(TAG,"Longitude: "+Longitude);
        Log.w(TAG,"mEngineStatus: "+mEngineStatus);

        VehicleTypesAndColors typesAndColors = new VehicleTypesAndColors(mContext);
        holder.iv_car.setBackgroundResource(typesAndColors.getWhiteIcon(vehicle_type));

        holder.tv_positionStatus.setText(positionStatus2);
        holder.setIsRecyclable(false);
        holder.tv_vId.setText(vehicle_id);
        holder.tv_name_vehicle.setText(vehicle_no);

        String dInput = modelList.getCreatedAt();
        String uDate = dInput.substring(0,11); // 2020-03-03 16:49:04
        holder.tv_date.setText(uDate);

        holder.tv_speedLimit.setText(mContext.getString(R.string.speedLimit)+": "+speedLimit+"(KM/H)");
//        holder.tv_status.setText(mContext.getString(R.string.status)+"/n"+"STOP");
        holder.tv_speed.setText(mContext.getString(R.string.speed)+"(KM/H)"+"\n"+speed);
        holder.tv_track.setText(mContext.getString(R.string.track)+"\n");

        //Address

        lat  = Double.parseDouble(Latitude);
        lng  = Double.parseDouble(Longitude);
        holder.tv_address.setText(modelList.getAddress());



        // holder.vehicleBg.setBackgroundColor();
        if (vehicle_color.equalsIgnoreCase("green")){
            holder.rl_car_bg.setBackgroundResource(R.drawable.ic_circle_green);
            holder.tv_status.setText(mContext.getString(R.string.status)+"\n"+"Running");
        }else if (vehicle_color.equalsIgnoreCase("red")){
            holder.rl_car_bg.setBackgroundResource(R.drawable.ic_circle_red);
            holder.tv_status.setText(mContext.getString(R.string.status)+"\n"+"Stop");
        }else if (vehicle_color.equalsIgnoreCase("yellow")){
            holder.rl_car_bg.setBackgroundResource(R.drawable.ic_circle_yellow);
            holder.tv_status.setText(mContext.getString(R.string.status)+"\n"+"Ready");
        }else if (vehicle_color.equalsIgnoreCase("blue")){
            holder.rl_car_bg.setBackgroundResource(R.drawable.ic_circle_blue);
            holder.tv_status.setText(mContext.getString(R.string.status)+"\n"+"Not Connect");
        }else if (vehicle_color.equalsIgnoreCase("grey")){
            holder.rl_car_bg.setBackgroundResource(R.drawable.ic_circle_gery);
            holder.tv_status.setText(vehicle_no);
            holder.tv_status.setText(mContext.getString(R.string.status)+"\n"+"No Status");
        }

        //  holder.rl_engine  rl_engine_bg
        if (mEngineStatus ==1){
            holder.rl_engine_bg.setBackgroundResource(R.drawable.circle_white);
            holder.iv_engine.setBackgroundResource(R.drawable.engine_b);
            holder.tv_engine.setText(mContext.getString(R.string.engine_on));
        }else if(mEngineStatus==0){
            holder.rl_engine_bg.setBackgroundResource(R.drawable.ic_circle_red);
            holder.iv_engine.setBackgroundResource(R.drawable.engine_w);
            holder.tv_engine.setText(mContext.getString(R.string.engine_off));
        }

        // engine_status
        holder.ll_engine_bg.setOnClickListener(v -> {
            String Vehicle_id_engine = holder.tv_vId.getText().toString();
            //engine_on ->1 , engine_off ->0
            if (mEngineStatus ==1){
                engineOnOffMethodDialog(mContext,Vehicle_id_engine,mContext.getString(R.string.do_you_want_engine_off),
                        mContext.getString(R.string.engine_off), mEngineStatus, holder);
            }else {
                engineOnOffMethodDialog(mContext,Vehicle_id_engine,mContext.getString(R.string.do_you_want_engine_on),
                        mContext.getString(R.string.engine_on), mEngineStatus, holder);
            }
        });



//        Glide.with(mContext)
//                .load(vehicle_image_url)
//                .placeholder(R.drawable.ic_car_white)
//                .error(R.drawable.ic_car_white)  // any image in case of error
//                .override(50, 50)
//                .centerCrop()
//                .into(holder.iv_car);

         /*if (vehicle_color.equalsIgnoreCase("green")){
            holder.vehicleBg.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        }else if (vehicle_color.equalsIgnoreCase("red")){
            holder.vehicleBg.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        }else if (vehicle_color.equalsIgnoreCase("grey")){
            holder.vehicleBg.setBackgroundColor(mContext.getResources().getColor(R.color.colorGrey));
        }else if (vehicle_color.equalsIgnoreCase("yellow")){
            holder.vehicleBg.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
        }else if (vehicle_color.equalsIgnoreCase("blue")){
            holder.vehicleBg.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
        }*/




        holder.ll_track.setOnClickListener(v -> {

            AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_ID,vehicle_id);
            AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_NAME,vehicle_name);
            AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_NUMBER,vehicle_no);
            AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_TYPE, vehicle_type);
            AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_COLOR, vehicle_color);
            AppPreferences.savePreferences(mContext,VariablesConstant.DRIVER_CONTACT_NUMBER, driver_number);
            AppPreferences.savePreferences(mContext,VariablesConstant.EMERGENCY_CONTACT_NUMBER, emergency_number);

            //MySingleton
//            MySingleton.getInstance().setVehicle(modelList);

            // Intent
            Intent intent = new Intent(mContext, LiveTrackingActivity.class);
            intent.putExtra(VariablesConstant.VEHICLE_ID, vehicle_id);
            intent.putExtra(VariablesConstant.VEHICLE_NAME, vehicle_name);
            intent.putExtra(VariablesConstant.VEHICLE_NUMBER, vehicle_no);
            intent.putExtra(VariablesConstant.VEHICLE_TYPE, vehicle_type);
            intent.putExtra(VariablesConstant.VEHICLE_COLOR, vehicle_color);
            intent.putExtra(VariablesConstant.DRIVER_CONTACT_NUMBER, driver_number);
            intent.putExtra(VariablesConstant.EMERGENCY_CONTACT_NUMBER, emergency_number);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            mContext.startActivity(intent);

        });

    }


    //   getEngineStatusCall(token,vehicle_id);
    private void engineOnOffMethodDialog(Context context,String vehicle_id2 ,String massage, String title, int engStatus, MyViewHolder holder ) {
       String token = AppPreferences.loadPreferences(context,VariablesConstant.TOKEN);
        String engine_status_msg="";

        engine_status_msg = mContext.getString(R.string.do_you_want_change_engine_status);

        new FancyGifDialog.Builder((Activity)mContext)
                .setTitle(mContext.getString(R.string.engine_status))
                .setMessage(engine_status_msg)
                .setNegativeBtnBackground("#000000")
                .setNegativeBtnText(mContext.getString(R.string.engine_on_dialog))  // Cancel
                .setPositiveBtnBackground("#000000")
                .setPositiveBtnText(mContext.getString(R.string.engine_off_dialog))
                .setGifResource(R.drawable.engine_b)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        // ======= API Call ==========
                        if (CheckNetwork.isNetworkAvailable(mContext)) {
                            getEngineStatusCall(token,vehicle_id2, 0, holder);
                        } else {
                            Toasty.warning(mContext, mContext.getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        // ======= API Call ==========
                        if (CheckNetwork.isNetworkAvailable(mContext)) {
                            getEngineStatusCall(token,vehicle_id2, 1, holder);
                        } else {
                            Toasty.warning(mContext, mContext.getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();
        //Creating dialog box
    }


    @Override
    public int getItemCount() {
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

    @Override
    public Filter getFilter() {
        return vehicleFilter;
    }


    /**  // TODO: 5/3/2020
     * Search view for Filter
     *
     *  // exampleList -> dataList
     *     // exampleListFull -> arrayList
     */
    private Filter vehicleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Vehicle> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Vehicle item : arrayList) {
                    if (item.getVehicleNo().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataList.clear();
            dataList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        dataList.clear();
        if (charText.length() == 0) {
            dataList.addAll(arrayList);
        } else {
            for (Vehicle item : arrayList) {
                if (item.getVehicleNo().toLowerCase(Locale.getDefault()).contains(charText)) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }



    // inner Holder class
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_vId,tv_date,tv_track,tv_speedLimit,
                tv_name_vehicle,tv_status,tv_engine, tv_speed,tv_address,tv_positionStatus;
        ImageView iv_engine, iv_speed,iv_track,iv_car;
//        LinearLayout vehicleBg;
        RelativeLayout rl_car_bg,rl_track, rl_engine_bg;
        LinearLayout ll_track, ll_engine_bg;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_vId = itemView.findViewById(R.id.tv_vId);
            tv_name_vehicle =  itemView.findViewById(R.id.tv_name_vehicle);
//            vehicleBg = itemView.findViewById(R.id.vehicleBg);
            tv_positionStatus = itemView.findViewById(R.id.tv_positionStatus);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_speedLimit = itemView.findViewById(R.id.tv_speedLimit);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_engine = itemView.findViewById(R.id.tv_engine);
            tv_speed = itemView.findViewById(R.id.tv_speed);
            tv_track = itemView.findViewById(R.id.tv_track);
            tv_address = itemView.findViewById(R.id.tv_address1);
            iv_engine = itemView.findViewById(R.id.iv_engine);
            iv_speed = itemView.findViewById(R.id.iv_speed);
            iv_track = itemView.findViewById(R.id.iv_track);
            iv_car = itemView.findViewById(R.id.iv_car);
            rl_car_bg = itemView.findViewById(R.id.rl_car_bg);
            ll_track = itemView.findViewById(R.id.ll_track);
            ll_engine_bg = itemView.findViewById(R.id.ll_engine_bg);
            rl_engine_bg = itemView.findViewById(R.id.rl_engine_bg);
            cardView = itemView.findViewById(R.id.cardView);

            //// bind the listener- OnItemClick
//            itemView.setOnClickListener(this);

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


    // engine_status
    private void getEngineStatusCall(String token, String vehicle_id2, int engStatus,  MyViewHolder holder) {
//        Log.w(TAG, "vehicle_id2 :" + vehicle_id2);
//        Log.w(TAG, "engStatus :" + engStatus);
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<EngineStatusResponse> call = apiInterface.engineStatus(vehicleId,token);
        Call<EngineStatusResponse> call = apiInterface.engineStatus(vehicle_id2, String.valueOf(engStatus), token);
        call.enqueue(new Callback<EngineStatusResponse>() {
            @Override
            public void onResponse(Call<EngineStatusResponse> call, Response<EngineStatusResponse> response) {
               if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getEngineStatusCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);

                if (vehicle_id2.equalsIgnoreCase("1")){
                    playSound1();
                }else if (vehicle_id2.equalsIgnoreCase("0")) {
                    playSound2();
                }

                try {
                    if (response.isSuccessful()){
                        EngineStatusResponse engineStatusResponse = response.body();
                        assert engineStatusResponse != null;
                        int status = Integer.parseInt(engineStatusResponse.getStatus());
                        String message = engineStatusResponse.getMessage();
                        int engine_status = Integer.parseInt(engineStatusResponse.getEngineStatus());

                        Log.w(TAG, "status:" + status);
                        Log.w(TAG, "message:" + message);
                        Log.w(TAG, "engine_status:" + engine_status);

                        /**
                         *  1->On , 0 -> Off
                         * PS : true = 1 and false = 0
                         */
//                        playSound2();

                        if ( status==200){

                            if (engine_status ==1){
                                holder.rl_engine_bg.setBackgroundResource(R.drawable.circle_white);
                                holder.iv_engine.setBackgroundResource(R.drawable.engine_b);
                                holder.tv_engine.setText(mContext.getString(R.string.engine)+"\n"+"ON");
                            }else if(engine_status==0){
                                holder.rl_engine_bg.setBackgroundResource(R.drawable.ic_circle_red);
                                holder.iv_engine.setBackgroundResource(R.drawable.engine_w);
                                holder.tv_engine.setText(mContext.getString(R.string.engine)+"\n"+"OFF");
                            }

//                            Toasty.success(mContext,  message, Toast.LENGTH_SHORT).show();
                        }else {
                            Log.e(TAG, "status:" + status);
                        }

                    }else {
//                        Log.e(TAG, "responseCode :" + responseCode);
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG," Massage :"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<EngineStatusResponse> call, Throwable t) {
                if (pd!=null)pd.dismiss();
                Log.e(TAG,"getEngineStatus onFailure: "+t.toString());
            }
        });

    }

    private void playSound() {
        try {
            // define sound URI
            Uri alarmSound = Uri.parse(AppPreferences.loadPreferences(mContext, VariablesConstant.NOTIFICATION_URI_RINGTONE));
//            Log.w(TAG,"alarmSound : "+alarmSound);
            Uri alarmSound2 = Uri.parse("android.resource://"+mContext.getPackageName()+R.raw.alarm2);
//            Uri path = Uri.parse("android.resource://"+mContext.getPackageName()+"/raw/alarm1.mp3");
//            Log.e(TAG,"playSound alarmSound2 :"+alarmSound2);

            // the sound to be played when there's a notification
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound2);
            r.play();
        }
        catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG,"playSound Massage :"+e.toString());
        }

    }

    private void playSound1() {
        final MediaPlayer mp = MediaPlayer.create(mContext, R.raw.car_lock);
        new CountDownTimer(3000, 1000){
            int counter=0;
            public void onTick(long millisUntilFinished){
//                Log.e(TAG,"playSound counter :"+counter);
                counter++;
                mp.start();
            }
            public  void onFinish(){
                mp.stop();
//                Log.e(TAG,"FINISH");
            }
        }.start();
    }

    private void playSound2() {
        final MediaPlayer mp = MediaPlayer.create(mContext, R.raw.alarm1);
        new CountDownTimer(5000, 1000){
            int counter=0;
            public void onTick(long millisUntilFinished){
//                Log.e(TAG,"playSound counter :"+counter);
                counter++;
                mp.start();
            }
            public  void onFinish(){
                mp.stop();
//                Log.e(TAG,"FINISH");
            }
        }.start();
    }

}
