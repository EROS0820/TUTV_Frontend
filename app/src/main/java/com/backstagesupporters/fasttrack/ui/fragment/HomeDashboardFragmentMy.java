package com.backstagesupporters.fasttrack.ui.fragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.backstagesupporters.fasttrack.MyApplication;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.AllActivitiesActivity;
import com.backstagesupporters.fasttrack.utils.language.Utility;
import com.backstagesupporters.fasttrack.models.MyLocation;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.models.VehicleDetails;
import com.backstagesupporters.fasttrack.ui.myMap.MapUtils;
import com.backstagesupporters.fasttrack.ui.myMap.MyHomeMapActivity;
import com.backstagesupporters.fasttrack.responseClass.CarDetailsResponse;
import com.backstagesupporters.fasttrack.responseClass.CarParkingResponse;
import com.backstagesupporters.fasttrack.responseClass.EngineStatusResponse;
import com.backstagesupporters.fasttrack.responseClass.ShowLocationResponse;
import com.backstagesupporters.fasttrack.responseClass.TickerResponse;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.CommonMap;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

import static com.backstagesupporters.fasttrack.ui.myMap.MapUtils.getBearing;
import static com.google.android.gms.maps.model.JointType.ROUND;


public class HomeDashboardFragmentMy extends Fragment implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private float defaultZoom=17.0f;
    private RelativeLayout relativeLayoutView;
    private MapView mapView2;
    private MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int REQUEST_LOCATION = 199;
    private Geocoder geocoder;
    private List<Address> addressList;
    private TextView tv_location;
    private String  bookAddress;
    Polyline line; //added
    private ArrayList<LatLng> points; //added

    private LinearLayout ll_call_driver,ll_all_activity,ll_engine_on_off,ll_car_parking,ll_sos,
            ll_speed,ll_location_address,llFragmentHomeDashboardFullLayout, car_Status_msg;
    private ImageView iv_engine_on_off,iv_car_parking, iv_carStatusColor,Img_btn_fullmap;
    private ImageView iv_plus,iv_minus;
    private TextView tv_your_car_is_stopped,tv_speed,tv_ticket;
    private Spinner vehicleSpinner;
    private TextView tv_vehicleSpinner;
    private RelativeLayout ll_spinner;
    private HorizontalScrollView bottom_layout;
    private String vehicle_id, vehicle_name,vehicle_type, vehicle_no_pref,vehicle_no,vehicle_color;
    private String sos_number,driver_number,driver_name, car_signal_strength;
    private String colorCar="", car_position="", mVehicleType="";
    private String speed, gps_length,satellites,direction,alarm, signal_strength, position_color;

    private List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>(); // VehicleList
    private ArrayList<String> myVehicleListSpinner = new ArrayList<>();          // VehicleListSpinner
    private ArrayList<String> mVehicleTypeList = new ArrayList<>();       // VehicleType
    private ArrayList<String> mVehicleColor = new ArrayList<>();       // VehicleColor
    private String token, userId;

    // TODO: 1/4/2020
    private static final long DELAY = 4500;
    private static final long ANIMATION_TIME_PER_ROUTE = 3000;
    private String polyLine = "q`epCakwfP_@EMvBEv@iSmBq@GeGg@}C]mBS{@KTiDRyCiBS";
    private PolylineOptions polylineOptions;
    private Polyline greyPolyLine;
    public Handler handler;
    private Marker carMarker;
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
    private List<LatLng> polyLineList;
    private double lat, lng;
    private double latitude = 23.7877649;
    private double longitude = 90.4007049;

    // Give your Server URL here >> where you get car location update
    private boolean isFirstPosition = true;
    private boolean isFirstPositionSpinner = true;
    private boolean OnTouch = true;
    private Double startLatitude;
    private Double startLongitude;

    //=Interface Declaration
    private ProgressDialog pd;
    private ApiInterface apiInterface;



    public HomeDashboardFragmentMy() {
        // Required empty public constructor
//        Log.i(TAG,"-- constructor");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MyApplication.localeManager.onAttach(context);
        Utility.resetActivityTitle(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_dashboard, container, false);
        mContext = getActivity();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        handler = new Handler();

        findViewById(view);
        ll_call_driver.setOnClickListener(this);
        ll_all_activity.setOnClickListener(this);
        ll_engine_on_off.setOnClickListener(this);
        ll_car_parking.setOnClickListener(this);
        ll_sos.setOnClickListener(this);
//        tv_vehicleSpinner.setOnClickListener(this);
//        ll_spinner.setOnClickListener(this);

        Img_btn_fullmap.setEnabled(false);
        Img_btn_fullmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), Fullmap.class);
                Intent intent = new Intent(getActivity(), MyHomeMapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("vehicle_id",vehicle_id);
//                bundle.putDouble("Ori",Origin);
//                bundle.putDouble("Dest",Desti);
                intent.putExtras(bundle);
                intent.putExtra("Token",token);
                startActivity(intent);
            }
        });


        // TODO: 2/4/2020
        // get data from activity
        vehicle_id = AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_ID);
        vehicle_no_pref = AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_NUMBER);
        mVehicleType = AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_TYPE);
        vehicle_color = AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_COLOR);

        //  tv_vehicleSpinner, ll_spinner
        if (!vehicle_no_pref.isEmpty()){
            ll_spinner.setVisibility(View.GONE);
            tv_vehicleSpinner.setVisibility(View.VISIBLE);
            tv_vehicleSpinner.setText(vehicle_no_pref);
            Log.e(TAG,"-- vehicle_id: "+vehicle_id);
//            Log.e(TAG,"-- vehicle_no_pref: "+vehicle_no_pref);
//            Log.v(TAG,"-- vehicle_type: "+mVehicleType);
//            Log.v(TAG,"-- vehicle_color: "+vehicle_color);
        }


        // TODO: 1/8/2020
        mapView2 = (MapView) view.findViewById(R.id.mapView2);
        mapView2.onCreate(savedInstanceState);

        // initializing Mapview
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this::onMapReady);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        token = AppPreferences.loadPreferences(getActivity(),VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);
        CheckPermissions();

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
//            getTicketCall(token);
            getTicketCall(userId);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

       /* if (vehicleArrayList.isEmpty()){
            if (CheckNetwork.isNetworkAvailable(mContext)) {
                getVehicleCall(token);
            } else {
                Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }*/


    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        token = AppPreferences.loadPreferences(mContext,VariablesConstant.TOKEN);
        spinnerClick();

        handler = new Handler();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        stopRepeatingTask();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"-- onStop");
        // stop the Handler
//        handler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"-- onDestroy");
        mMapView.onDestroy();
        // set null when activity destry - SIGNAL_STRENGTH
        AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, "0");

        if (!vehicleArrayList.isEmpty()){
            vehicleArrayList.clear();
        }
        if (!myVehicleListSpinner.isEmpty()){
            myVehicleListSpinner.clear();
        }
        if (!mVehicleTypeList.isEmpty()){
            mVehicleTypeList.clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"-- onDestroyView");
        // stop the Handler - location
        handler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void spinnerClick() {
        if (OnTouch){
            vehicleSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    OnTouch = false;
                    callVehicleList();
                    return false;
                }
            });

        }
    }

    private void callVehicleList() {
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            if (isFirstPositionSpinner){
                isFirstPositionSpinner = false;
                if (vehicleArrayList !=null){
                    vehicleArrayList.clear();
                }
                if (myVehicleListSpinner !=null){
                    myVehicleListSpinner.clear();
                }
                if (mVehicleTypeList !=null){
                    mVehicleTypeList.clear();
                }

                getVehicleCall2(token);
            }

        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_call_driver:
                callByDialorMethod();
                break;

            case R.id.ll_sos:
                sendSosMethod();
                break;

            case R.id.ll_all_activity:
                startActivity(new Intent(mContext, AllActivitiesActivity.class));
                break;
            case R.id.ll_engine_on_off:
                engineOnOffMethodDialog();
                break;

            case R.id.ll_car_parking:
                //"parking_status":0,"parking_status":1
                parkingMethodDialog();
                break;

            case R.id.tv_vehicleSpinner:
                Log.w(TAG,"tv_vehicleSpinner");
                ll_spinner.setVisibility(View.VISIBLE);
                tv_vehicleSpinner.setVisibility(View.GONE);
                callVehicleList();
                break;
        }
    }

    private void callByDialorMethod() {
        Log.w(TAG,"callByDialorMethod driver_number :"+driver_number);
        // Use format with "tel:" and phone number to create phoneNumber.
        if (driver_number!=null){
            Intent intent = new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse("tel:0123456789"));
            intent.setData(Uri.parse("tel:" + driver_number));
            startActivity(intent);
        }else {
            Log.e(TAG,"Number not found");
//            Toast.makeText(mContext, "Number not found", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: 1/28/2020
    private void  sendSosMethod() {
        String SosMessage = driver_name + " triggered the Emergency button in his/her Fast Track app. As " + driver_name + " Emergency contact, we suggest you to call " + driver_name + " now or the local police";
//      sendSms(SosMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Send Sms");
        builder.setMessage("Are you sure, You want send sms");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                if (sos_number !=null){

                }
                sendSMSByIntent(SosMessage,sos_number);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendSMSByIntent(String message, String phoneNumber) {
        Log.w(TAG, "sendSosMethod Sos Message :" + message);
        Log.w(TAG, "sendSosMethod Sos phoneNumber  :" + phoneNumber);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            //        Uri uri = Uri.parse("sms:" + phoneNumber);
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("smsto:"));
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", phoneNumber);
            smsIntent.putExtra("sms_body", message);

            try {
                startActivity(smsIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e(TAG,"sendSosMethod :"+ex.getMessage());
            }
        }else {

            Uri smsUri=  Uri.parse("smsto:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_SENDTO);
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("vnd.android-dir/mms-sms");
            intent.setData(smsUri);  // This ensures only SMS apps respond
            intent.putExtra("sms_body", message);
            intent.putExtra(Intent.EXTRA_STREAM, phoneNumber);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    private void engineOnOffMethodDialog(){
        String engine_status_msg="";
        String getEngine_status = AppPreferences.loadPreferences(mContext, VariablesConstant.ENGINE_STATUS);
        String engine_status_bg = AppPreferences.loadPreferences(mContext, VariablesConstant.ENGINE_STATUS_BG);

         if (engine_status_bg.equalsIgnoreCase("0")){
            engine_status_msg = getString(R.string.do_you_want_engine_off);
        }else if (engine_status_bg.equalsIgnoreCase("1")){
            engine_status_msg = getString(R.string.do_you_want_engine_on);
        }

        new FancyGifDialog.Builder(getActivity())
                .setTitle(getString(R.string.engine_status))
                .setMessage(engine_status_msg)
                .setNegativeBtnBackground("#000000")
                .setNegativeBtnText(getString(R.string.cancel))  // Cancel
                .setPositiveBtnBackground("#000000")
                .setPositiveBtnText(getString(R.string.ok))
                .setGifResource(R.drawable.engine)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        // ======= API Call ==========
                        if (CheckNetwork.isNetworkAvailable(mContext)) {
                            if (token ==null){
                               Log.e(TAG,"User are not Valid");
//                                Toasty.error(mContext,"User are not Valid", Toast.LENGTH_SHORT).show();
                            }else if (vehicle_id.isEmpty()){
//                                Toasty.error(mContext,"Please select the Vehicle", Toast.LENGTH_SHORT).show();
                                Log.e(TAG," Please select the Vehicle");
                            }else {
                                getEngineStatusCall(token,vehicle_id);
                            }
                        } else {
                            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .build();

    }

    private void parkingMethodDialog(){
        String car_parking_msg="";
        String getParking_status = AppPreferences.loadPreferences(mContext, VariablesConstant.PARKING_STATUS);
        String parking_status_bg = AppPreferences.loadPreferences(mContext, VariablesConstant.PARKING_STATUS_BG);
        Log.e(TAG, "engineOnOffMethodDialog getEngine_status :" + getParking_status);
        Log.e(TAG, "engineOnOffMethodDialog parking_status_bg :" + parking_status_bg);
        

        if (parking_status_bg.equalsIgnoreCase("0")){
            car_parking_msg = getString(R.string.do_you_want_car);
        }else if (parking_status_bg.equalsIgnoreCase("1")){
            car_parking_msg = getString(R.string.do_you_want_car_not);
        }
        Log.i(TAG, "engineOnOffMethodDialog car_parking_msg :" + car_parking_msg);


        new FancyGifDialog.Builder(getActivity())
                .setTitle(getString(R.string.parking_the_car))
                .setMessage(car_parking_msg)
                .setNegativeBtnBackground("#000000")
                .setNegativeBtnText(getString(R.string.cancel))  // Cancel
                .setPositiveBtnBackground("#000000")
                .setPositiveBtnText(getString(R.string.ok))
//                .setGifResource(R.drawable.dash_car)   //Pass your Gif here
                .setGifResource(R.drawable.car_parked2)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Log.e(TAG, "parkingMethodDialog token :" + token);
                        // ======= API Call ==========
                        if (CheckNetwork.isNetworkAvailable(mContext)) {
                            if (token ==null){
                                Log.e(TAG,"User are not Valid ");
                            }else if (vehicle_id.isEmpty()){
                                Log.e(TAG," Please select the Vehicle");
                            }else {
                                getCarParkingCall(token,vehicle_id);
                            }
                        } else {
                            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .build();
    }


    private void findViewById(View view) {
        Img_btn_fullmap = view.findViewById(R.id.Img_btn_fullmap);
        relativeLayoutView = view.findViewById(R.id.relativeLayoutView);
        vehicleSpinner =view.findViewById(R.id.vehicleSpinner);
        tv_vehicleSpinner =view.findViewById(R.id.tv_vehicleSpinner);
        ll_spinner =view.findViewById(R.id.ll_spinner);
        ll_call_driver =view.findViewById(R.id.ll_call_driver);
        ll_all_activity =view.findViewById(R.id.ll_all_activity);
        ll_engine_on_off =view.findViewById(R.id.ll_engine_on_off);
        ll_car_parking =view.findViewById(R.id.ll_car_parking);
        ll_sos =view.findViewById(R.id.ll_sos);
        tv_location =view.findViewById(R.id.tv_location);
        iv_carStatusColor =view.findViewById(R.id.iv_carStatusColor);
        tv_your_car_is_stopped =view.findViewById(R.id.tv_your_car_is_stopped);
        iv_engine_on_off =view.findViewById(R.id.iv_engine_on_off);
        iv_car_parking =view.findViewById(R.id.iv_car_parking);
        iv_plus =view.findViewById(R.id.iv_plus);
        iv_minus =view.findViewById(R.id.iv_minus);
        tv_speed =view.findViewById(R.id.tv_speed);
        tv_ticket=view.findViewById(R.id.tv_ticket);
        iv_plus =view.findViewById(R.id.iv_plus);
        iv_minus =view.findViewById(R.id.iv_minus);
        bottom_layout=view.findViewById(R.id.bottom_layout);
        ll_location_address=view.findViewById(R.id.ll_location_address);
        ll_speed=view.findViewById(R.id.ll_speed);
        llFragmentHomeDashboardFullLayout=view.findViewById(R.id.llFragmentHomeDashboardFullLayout);
        car_Status_msg=view.findViewById(R.id.car_Status_msg);
    }


    private void setVehicleSpinner2() {
        Log.w(TAG,"vehicleArray List Size: "+vehicleArrayList.size());
        Log.w(TAG,"VehicleListSpinner List Size: "+myVehicleListSpinner.size());

        // set selected spinner item
       /* String vehiclePosition = AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_POSITION);
        if (vehiclePosition.equalsIgnoreCase("") || vehiclePosition.isEmpty() ){
            Log.e(TAG,"selectedItem VEHICLE_POSITION vehiclePosition:"+vehiclePosition);
        }else {
            int vPosition = Integer.parseInt(vehiclePosition);
            Log.e(TAG,"selectedItem VEHICLE_POSITION vPosition:"+vPosition);
            vehicleSpinner.setSelection(vPosition);
            String vSelectedVehicleName = myVehicleListSpinner.get(vPosition).toString();
            Log.e(TAG,"selectedItem vSelectedVehicleName:"+vSelectedVehicleName);
        }*/

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext,R.layout.spinner_item, myVehicleListSpinner){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
//                String color = vehicleArrayList.get(position).getColor();
                String color = mVehicleColor.get(position);
                if (color.equalsIgnoreCase("green")){
                    tv.setTextColor(getResources().getColor(R.color.green));
                }else if (color.equalsIgnoreCase("red")){
                    tv.setTextColor(getResources().getColor(R.color.red));
                }else if (color.equalsIgnoreCase("grey")){
                    tv.setTextColor(getResources().getColor(R.color.colorGrey));
                }else if (color.equalsIgnoreCase("yellow")){
                    tv.setTextColor(getResources().getColor(R.color.yellow));
                }else if (color.equalsIgnoreCase("blue")){
                    tv.setTextColor(getResources().getColor(R.color.blue));
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
        vehicleSpinner.setAdapter(spinnerArrayAdapter);
        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                int mySelectedVehicleId;
                if(selectedItem.equals(getString(R.string.please_select_vehicle_type))) {
                    Log.e(TAG,"selectedItem "+selectedItem);
                }else {
                    vehicle_name = parent.getItemAtPosition(position).toString();
                    mySelectedVehicleId = parent.getSelectedItemPosition() +1;

                    // change for car marker color status
                    isFirstPosition =true;

                    AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS_BG, "");
                    AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS_BG, "");
//                    AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS, "");
//                    AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS, "");

                    vehicle_id = String.valueOf(vehicleArrayList.get(position).getVehicleId());
                    AppPreferences.savePreferences(mContext, VariablesConstant.VEHICLE_ID, vehicle_id);
//                    AppPreferences.savePreferences(mContext, VariablesConstant.VEHICLE_POSITION, String.valueOf(position));
//                    AppPreferences.savePreferences(mContext, VariablesConstant.VEHICLE_POSITION, String.valueOf(mySelectedVehicleId));

                    Log.i(TAG,"selectedItem position: "+position);
                    Log.e(TAG,"selectedItem myId: "+mySelectedVehicleId);
                    Log.e(TAG,"selectedItem vehicle_id: "+vehicle_id);
                    Log.d(TAG,"selectedItem vehicle_name: "+vehicle_name);

                    String mySelectedVehicleName = myVehicleListSpinner.get(position).toString(); //selected item name from VehicleList
                    mVehicleType =   mVehicleTypeList.get(position).toString();
                    AppPreferences.savePreferences(mContext, VariablesConstant.VEHICLE_TYPE, mVehicleType);
//                    String vPosition = AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_POSITION);
                    String vType = AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_TYPE);

                    Log.e(TAG,"selectedItem mySelectedVehicleType: "+mySelectedVehicleName);
                    Log.i(TAG,"selectedItem mVehicleType: "+mVehicleType);
                    Log.i(TAG,"selectedItem vType:"+vType);

                    getCarDetailsCall(token,vehicle_id);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG,getString(R.string.please_select_vehicle_type)+" : "+parent.toString());
            }
        });
    }




    //================ Api Call ===============================================================
    // showVehicles
    private void getVehicleCall(final String token) {
      /*  pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();
       */
        Call<VehiclesResponse> call = apiInterface.showVehicles(token);
        call.enqueue(new Callback<VehiclesResponse>() {
            @Override
            public void onResponse(Call<VehiclesResponse> call, Response<VehiclesResponse> response) {
//                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.i(TAG, "getVehicleCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            VehiclesResponse vehiclesResponse = response.body();
                            assert vehiclesResponse != null;
                            int status = vehiclesResponse.getStatus();
                            if (status== 200) {
                                vehicleArrayList  = vehiclesResponse.getVehicles();

                                for (int i=0; i<vehicleArrayList.size(); i++){
                                    mVehicleTypeList.add(vehicleArrayList.get(i).getVehicleType());
                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
                                    mVehicleColor.add(vehicleArrayList.get(i).getColor());
//                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleName() +" "+ vehicleArrayList.get(i).getVehicleNo());
                                }
                                //vehicleSpinner
                                setVehicleSpinner2();

                            } else {
                                Log.e(TAG, "Get Vehicle Status Code :"+status);
                            }
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
                                break;
                            case 404:
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Massage"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
//                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }

    private void getVehicleCall2(final String token) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();

        Call<VehiclesResponse> call = apiInterface.showVehicles(token);
        call.enqueue(new Callback<VehiclesResponse>() {
            @Override
            public void onResponse(Call<VehiclesResponse> call, Response<VehiclesResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.w(TAG, "getVehicleCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            OnTouch = true;

                            VehiclesResponse vehiclesResponse = response.body();
                            assert vehiclesResponse != null;
                            int status = vehiclesResponse.getStatus();
                            if (status== 200) {
                                if (mVehicleColor!=null){
                                    mVehicleColor.clear();
                                }
                                vehicleArrayList  = vehiclesResponse.getVehicles();

                                for (int i=0; i<vehicleArrayList.size(); i++){
                                    mVehicleTypeList.add(vehicleArrayList.get(i).getVehicleType());
                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
                                    mVehicleColor.add(vehicleArrayList.get(i).getColor());
//                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleName() +" "+ vehicleArrayList.get(i).getVehicleNo());
                                }
                                // set null when activity destry - SIGNAL_STRENGTH
                                AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, "0");

                                //vehicleSpinner
                                setVehicleSpinner2();
                            } else {
                                Log.e(TAG, "Get Vehicle Status Code :"+status);
                            }
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
                                break;
                            case 404:
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Massage"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
                if (pd!=null)
                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }

    private void getTicketCall(String token) {
         /*  pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();
         */

//        Call<TickerResponse> call = apiInterface.tickerMessage(token);
        Call<TickerResponse> call = apiInterface.tickerMessageUser(token);
        call.enqueue(new Callback<TickerResponse >() {
            @Override
            public void onResponse(Call<TickerResponse > call, Response<TickerResponse > response) {
//                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getTicketCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            TickerResponse  tickerResponse = response.body();
                            int status = tickerResponse.getStatus();
                            if (status== 200) {
                             String ticker = tickerResponse.getTicker();
                                Log.e(TAG, "ticker :"+ticker);
//                                Log.e(TAG, "ticker Message :"+ tickerResponse.getMessage());
//                                tv_ticket.setText(getString(R.string.token_message) + "  "+ ticker);
                                tv_ticket.setText(ticker);

                            } else {
                                Log.e(TAG, "Get Vehicle Status Code :"+status);
                            }
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
                                break;
                            case 404:
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Massage"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<TickerResponse > call, Throwable t) {
//                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }

    private void playSound1() {
        final MediaPlayer mp = MediaPlayer.create(mContext, R.raw.alarm1);
        new CountDownTimer(2000, 1000){
            int counter=0;
            public void onTick(long millisUntilFinished){
                Log.e(TAG,"playSound counter :"+counter);
                counter++;
                mp.start();
            }
            public  void onFinish(){
                mp.stop();
                Log.e(TAG,"FINISH");
            }
        }.start();
    }

    private void playSound2() {
        final MediaPlayer mp = MediaPlayer.create(mContext, R.raw.alarm1);
        new CountDownTimer(2000, 1000){
            int counter=0;
            public void onTick(long millisUntilFinished){
                Log.e(TAG,"playSound counter :"+counter);
                counter++;
                mp.start();
            }
            public  void onFinish(){
                mp.stop();
                Log.e(TAG,"FINISH");
            }
        }.start();
    }
    // engine_status
    private void getEngineStatusCall(String token, String vehicle_id) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<EngineStatusResponse> call = apiInterface.engineStatus(vehicle_id,token);
        call.enqueue(new Callback<EngineStatusResponse>() {
            @Override
            public void onResponse(Call<EngineStatusResponse> call, Response<EngineStatusResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e(TAG, "getEngineStatusCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            playSound2();
                            EngineStatusResponse engineStatusResponse = response.body();
                            assert engineStatusResponse != null;
                            String status = engineStatusResponse.getStatus();
                            String message = engineStatusResponse.getMessage();
                            //0 -> Off 1->On
                            String engine_status = engineStatusResponse.getEngineStatus();
                            AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS, String.valueOf(engine_status));
                            String getEngine_status = AppPreferences.loadPreferences(mContext, VariablesConstant.ENGINE_STATUS);
                            Log.w(TAG, "getEngine_status :" + getEngine_status);

                            if (engine_status.equalsIgnoreCase("0")){
//                                iv_engine_on_off.setBackgroundResource(R.drawable.engine_red);
                                iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_red));
                            }else {
//                                iv_engine_on_off.setBackgroundResource(R.drawable.engine);
                                iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine));
                            }
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
                                break;
                            case 404:
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"getEngineStatus Massage :"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<EngineStatusResponse> call, Throwable t) {
                if (pd!=null) pd.dismiss();
                Log.e(TAG,"getEngineStatus onFailure"+t.toString());
            }
        });

    }

    // car_parking
    private void getCarParkingCall(String token, String vehicle_id) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<CarParkingResponse> call = apiInterface.carParking(vehicle_id,token);
        call.enqueue(new Callback<CarParkingResponse>() {
            @Override
            public void onResponse(Call<CarParkingResponse> call, Response<CarParkingResponse> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.w(TAG, "getCarParkingCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        //"parking_status":0,"parking_status":1
                        if (response.isSuccessful()) {
                            playSound1();

                            CarParkingResponse carParkingResponse = response.body();
                            assert carParkingResponse != null;
                            int status = carParkingResponse.getStatus();
                            if (status==200) {
                                String message = carParkingResponse.getMessage();
                                //0 -> Off 1->On
                                int parking_status = carParkingResponse.getParkingStatus();
                                AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS, String.valueOf(parking_status));

                                Log.w(TAG,"parking_status :"+parking_status);
//                                Log.i(TAG,"message :"+message);
                                if (parking_status ==0){
                                    iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked_red));
                                }else {
                                    iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked));
                                }
                            } else {
                                Log.e(TAG, "Get Car Parking Status Code :"+status);
                            }
                        }

                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
                                break;
                            case 404:
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"getCarParking Massage :"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<CarParkingResponse> call, Throwable t) {
              if (pd!=null) pd.dismiss();
                Log.e(TAG,"getCarParking onFailure"+t.toString());
            }
        });

    }

    // car_details
    private void getCarDetailsCall(String token, String vehicle_id) {
//        Log.e(TAG,"getCarDetailsCall vehicle_id "+vehicle_id);
//        pd = new ProgressDialog(mContext);
//        pd.setMessage("Loading Please Wait...");
//        pd.setCancelable(false);
//        pd.show();

        Call<CarDetailsResponse> call = apiInterface.carDetails(vehicle_id,token);
        call.enqueue(new Callback<CarDetailsResponse>() {
            @Override
            public void onResponse(Call<CarDetailsResponse> call, Response<CarDetailsResponse> response) {
//                pd.dismiss();
//                Log.w(TAG, "getCarDetailsCall Response >>>>" + response.toString());
                int  responseCode  = response.code();
                Log.w(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        CarDetailsResponse carDetailsResponse = response.body();
                        assert carDetailsResponse != null;
                        int status =  carDetailsResponse.getStatus();
                        if (status==200) {
                            VehicleDetails vehicleDetails = carDetailsResponse.getVehicleDetails();
                            sos_number = vehicleDetails.getSosNumber();
                            driver_number = vehicleDetails.getDriverNumber();
                            driver_name = vehicleDetails.getDriverName();
                            car_signal_strength = vehicleDetails.getSignalStrength();
                            vehicle_no = vehicleDetails.getVehicleNo();
                            vehicle_type = vehicleDetails.getVehicleType();
//                            owner_id = vehicleDetails.getOwnerId();
//                           String car_color = vehicleDetails.getCarColor();
//                            Log.d(TAG, "car_color :" + car_color);
                            Log.w(TAG, "car_signal_strength :" + car_signal_strength);
                            Log.i(TAG, "getCarDetailsCall sos_number :" + sos_number);
                            Log.i(TAG, "getCarDetailsCall driver_number :" + driver_number);
                        } else {
                            Log.e(TAG, "Get Car Details Response code :"+status);
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
                                break;
                            case 404:
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "getCarDetails Massage :"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<CarDetailsResponse> call, Throwable t) {
//                pd.dismiss();ow();
                Log.e(TAG,"getCarDetails onFailure"+t.toString());
            }
        });

    }


    private void getDriverLocationUpdate(String token, String vehicle_id) {
        Call<ShowLocationResponse> call = apiInterface.showLocation(token,vehicle_id);
        call.enqueue(new Callback<ShowLocationResponse>() {
            @Override
            public void onResponse(Call<ShowLocationResponse> call, Response<ShowLocationResponse> response) {
                // pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.w(TAG, "getLocationShowCall Response >>>>" + str_response);
                int responseCode = response.code();
//                Log.w(TAG, "Junaid responseCode :" + responseCode);
                try {
                    if (response.isSuccessful()) {
                        // active the full map click button
                        Img_btn_fullmap.setEnabled(true);
//                        int  responseCode  = response.code();
                        ShowLocationResponse locationResponse = response.body();
                        int status = locationResponse.getStatus();
                        Log.e(TAG, "status :" + status);
                        if (status == 200) {

                            isFirstPositionSpinner = true;

                            List<MyLocation> locationResponseList = locationResponse.getLocation();
                            Log.w(TAG,"Response Count"+locationResponseList.size());
                            int vId = Integer.parseInt(vehicle_id);
                            //Checking the Size Of Array Returned from server
                            if( vId ==0 && locationResponseList.size() > 0 ){
                                //Initially Clearing the map
                                googleMap.clear();
                                bottom_layout.setVisibility(View.GONE);

                                ll_speed.setVisibility(View.GONE);
                                ll_location_address.setVisibility(View.GONE);
                                llFragmentHomeDashboardFullLayout.setVisibility(View.GONE);
                                car_Status_msg.setVisibility(View.GONE);
                                //Initializing the Loop
                                for(int i=0;i<locationResponseList.size();i++){
                                    MyLocation myLocation=locationResponseList.get(i);
                                    Log.e(TAG,"Response Count i: "+i);

                                    double tempLat= Double.parseDouble(myLocation.getLatitude());
                                    double tempLong= Double.parseDouble(myLocation.getLongitude());
                                    Log.w("Current Lat long",""+tempLat+" lng "+tempLong);
                                    LatLng getCurrentLatLng=new LatLng(tempLat,tempLong);
                                    String colorVehicle = myLocation.getColor();
                                    String vehicleType = myLocation.getType();
                                    Log.i(TAG,"colorVehicle : "+colorVehicle);
                                    Log.i(TAG,"vehicleType : "+vehicleType);
//                                    carMarker = googleMap.addMarker(new MarkerOptions().position(getCurrentLatLng).flat(true).icon(BitmapDescriptorFactory.fromResource(getMarkerColor(myLocation.getColor()))));
                                    carMarker = googleMap.addMarker(new MarkerOptions().position(getCurrentLatLng)
                                            .flat(true).icon(BitmapDescriptorFactory
                                                    .fromResource(setCarColor(colorVehicle, vehicleType))));
                                    carMarker.setAnchor(0.5f, 0.5f);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng,5.0f));
                                    startPosition = new LatLng(tempLat, tempLong);
                                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {

                                        }
                                    });
                                }
                            }else{

                                bottom_layout.setVisibility(View.VISIBLE);
                                ll_speed.setVisibility(View.VISIBLE);
                                ll_location_address.setVisibility(View.VISIBLE);
                                llFragmentHomeDashboardFullLayout.setVisibility(View.VISIBLE);
                                car_Status_msg.setVisibility(View.VISIBLE);

                                Log.e(TAG,"Response Count size: "+locationResponseList.size());
                                car_position = locationResponseList.get(0).getPosition();          //"position": "Is Stopped since 8 min",
                                position_color = locationResponseList.get(0).getPositionColor();  // "position_color": "red",
                                colorCar = locationResponseList.get(0).getColor();                // "color": "red",
                                setCarPositionMsg(car_position);
                                setCarPositionMsgColor(position_color);
                                setCarColor(colorCar);

                                String soccermomLatitude = locationResponseList.get(0).getLatitude();
                                String soccermomLongitude = locationResponseList.get(0).getLongitude();
                                speed = locationResponseList.get(0).getSpeed();
                                gps_length = locationResponseList.get(0).getGpsLength();
                                satellites = locationResponseList.get(0).getSatellites();
                                direction = locationResponseList.get(0).getDirection();
                                alarm = locationResponseList.get(0).getAlarm();
                                String parking_status_bg = String.valueOf(locationResponseList.get(0).getParkingStatus());        // car color
                                String engine_status_bg = String.valueOf(locationResponseList.get(0).getEngineStatus());        // car color

                                signal_strength = String.valueOf(locationResponseList.get(0).getSignalStrength());

                                AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, signal_strength);
                                AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS_BG, parking_status_bg);
                                AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS_BG, engine_status_bg);
                                Log.w(TAG, "signal_strength : " + signal_strength);
                                Log.i(TAG, "parking_status_bg : " + parking_status_bg);
                                Log.i(TAG, "engine_status_bg : " + engine_status_bg);
                                Log.w(TAG, "colorCar : " + colorCar);

                                setCarParkingBg(parking_status_bg);
                                setEngineStatusBg(engine_status_bg);
//                            startNavigation(soccermomLatitude,soccermomLongitude);

                                // TODO: 1/1/2020
                                startLatitude = Double.valueOf(soccermomLatitude);
                                startLongitude = Double.valueOf(soccermomLongitude);

                                tv_speed.setText(speed);
                                getAddress(startLatitude, startLongitude);
                                Log.d(TAG, "JUNAID: "+startLatitude + "--" + startLongitude);

//                            LatLng latLng = new LatLng(startLatitude, startLongitude);
//                            googleMap.addMarker(new MarkerOptions().position(latLng).
//                                    flat(true).icon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar))));
//                            LatLng latLng = new LatLng(startLatitude,startLongitude);
//                            points.add(latLng);
//                            redrawLine(); //added

                                if (isFirstPosition) {
                                    startPosition = new LatLng(startLatitude, startLongitude);
                                    Log.w(TAG, "startPosition");
                                    googleMap.clear();

//                                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).flat(true).icon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar))));
                                    carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                                            flat(true).icon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar))));
                                    carMarker.setAnchor(0.5f, 0.5f);

                                    googleMap.moveCamera(CameraUpdateFactory
                                            .newCameraPosition
                                                    (new CameraPosition.Builder()
                                                            .target(startPosition)
                                                            .zoom(defaultZoom)
                                                            .build()));

                                    isFirstPosition = false;

                                } else {
                                    Log.w(TAG, "endPosition");
                                    endPosition = new LatLng(startLatitude, startLongitude);

                                    Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);

                                    if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {

                                        Log.e(TAG, "NOT SAME");
                                        startBikeAnimation(startPosition, endPosition);

                                    } else {

                                        Log.e(TAG, "SAME");
                                    }
                                }

                            }

                        /*    googleMap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition
                                            (new CameraPosition.Builder()
                                                    .target(startPosition)
                                                    .zoom(defaultZoom)
                                                    .build()));*/

                            // set mapView
                            mapView2.setVisibility(View.GONE);
                            relativeLayoutView.setVisibility(View.GONE);
                            mMapView.setVisibility(View.VISIBLE);

                        } else {
                            Log.e(TAG, "Get Location Response code :" + status);
                        }

                    } else {
                        // error case
                        switch (responseCode) {
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
                                break;
                            case 404:
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Massage :" + e.toString());
                }

            }
            @Override
            public void onFailure(Call<ShowLocationResponse> call, Throwable t) {
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }



    private void setCarPositionMsgColor(String position_color) {
        if (!position_color.equalsIgnoreCase("")) {
            if (position_color.equalsIgnoreCase("green")) {
                iv_carStatusColor.setImageResource(R.drawable.point_green);
            } else if (position_color.equalsIgnoreCase("yellow")) {
                iv_carStatusColor.setImageResource(R.drawable.point_yellow);
            } else if (position_color.equalsIgnoreCase("red")) {
                iv_carStatusColor.setImageResource(R.drawable.point_red);
            }else if (position_color.equalsIgnoreCase("blue")) {
                iv_carStatusColor.setImageResource(R.drawable.point_blue);
            }
        }else{
            Log.e(TAG, "position_color  is empty");
        }
    }

    private void setCarPositionMsg(String car_position) {
        if (car_position.isEmpty()){
            tv_your_car_is_stopped.setText(getString(R.string.your_car_is_stopped));
        }else {
            tv_your_car_is_stopped.setText(car_position);
        }
    }

    // engine_status_bg
    private void setEngineStatusBg(String engine_status_bg) {
        int my_getEngine_status = Integer.parseInt(engine_status_bg);
        if (my_getEngine_status ==1){
            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_red));
        }else {
            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine));
        }
    }

    //parking_status_bg
    private void setCarParkingBg(String parking_status_bg) {
        int my_getParking_status = Integer.parseInt(parking_status_bg);
        if (my_getParking_status ==1){
            iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked_red));
        }else {
            iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked));
        }
    }



    //==============================Google Map ================================================================

    private void CheckPermissions() {
        RxPermissions.getInstance(mContext)
                .request(
                        Manifest.permission.CONTROL_LOCATION_UPDATES,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private void setUpMap(){
        // https://developers.google.com/android/reference/com/google/android/gms/maps/UiSettings
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setTrafficEnabled(false);
            googleMap.setIndoorEnabled(false);
            googleMap.setBuildingsEnabled(false);
            //googleMap.getUiSettings().setZoomControlsEnabled(true);
        }

        String mMapView = AppPreferences.loadPreferences(mContext, VariablesConstant.MapView_Type);
//        Log.i(TAG,"map_view_type : "+mMapView);
         int mapInt = setMyMapType(mMapView);
//         Log.i(TAG,"map_view_type : "+mapInt);

         // MAP_TYPE_NORMAL
        if (mapInt==0){
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }else {
            googleMap.setMapType(mapInt);
        }
    }

    private int setMyMapType(String mMapView) {
        int mapType = GoogleMap.MAP_TYPE_NONE;
        // {"Satellite View","Normal View","Terran View"}
        if (mMapView.equalsIgnoreCase("Satellite View")){
            mapType = GoogleMap.MAP_TYPE_SATELLITE;
        }else  if (mMapView.equalsIgnoreCase("Normal View")){
            mapType = GoogleMap.MAP_TYPE_NORMAL;
        }else  if (mMapView.equalsIgnoreCase("Terran View")){
            mapType = GoogleMap.MAP_TYPE_TERRAIN;
        }

        return mapType;
    }


    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        setUpMap();

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));

        LatLng latLng = new LatLng(28.5503,77.2502);
      //  googleMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(BitmapDescriptorFactory.fromResource(getBlueIcon(colorCar))));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));

        buildGoogleApiClient();//Init Google API Client

        // TODO: 1/4/2020
        startGettingOnlineDataFromCar();
//        dynamicPolyLine();

        // Zoom in, animating the camera. - iv_plus,iv_minus
        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                String zoomIn = CameraUpdateFactory.zoomIn().toString();
//                Log.i(TAG,"googleMap zoomIn : "+zoomIn);
            }
        });

        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.animateCamera(CameraUpdateFactory.zoomOut());
                String zoomOut = CameraUpdateFactory.zoomOut().toString();
//                Log.i(TAG,"googleMap zoomIn: "+zoomOut);
            }
        });

        // LocationUpdate
        getDriverLocationUpdate(token,vehicle_id);

    }


    void staticPolyLine() {

        googleMap.clear();

        polyLineList = MapUtils.decodePoly(polyLine);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        googleMap.animateCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(5);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = googleMap.addPolyline(polylineOptions);

        startCarAnimation(latitude, longitude);

    }



    Runnable staticCarRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "staticCarRunnable handler called...");
            if (index < (polyLineList.size() - 1)) {
                index++;
                next = index + 1;
            } else {
                index = -1;
                next = 1;
                stopRepeatingTask();
                return;
            }

            if (index < (polyLineList.size() - 1)) {
//                startPosition = polyLineList.get(index);
                startPosition = carMarker.getPosition();
                endPosition = polyLineList.get(next);
            }

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

//                    Log.i(TAG, "Car Animation Started...");

                    v = valueAnimator.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v)
                            * startPosition.longitude;
                    lat = v * endPosition.latitude + (1 - v)
                            * startPosition.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f, 0.5f);
                    carMarker.setRotation(getBearing(startPosition, newPos));
                    googleMap.moveCamera(CameraUpdateFactory
                            .newCameraPosition
                                    (new CameraPosition.Builder()
                                            .target(newPos)
                                            .zoom(defaultZoom)
                                            .build()));
                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 5000);

        }
    };

    private void startCarAnimation(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

//        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(BitmapDescriptorFactory.fromResource(getMapIcon(colorCar))));
//        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar))));
       // carMarker = googleMap.addMarker(new MarkerOptions().position(latLng).
         //       flat(true).icon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar))));


        index = -1;
        next = 1;
        handler.postDelayed(staticCarRunnable, 3000);
    }

    private void  redrawLine(){
        googleMap.clear();

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for(int i=0; i<points.size(); i++){
            LatLng point = points.get(i);
            options.add(point);
        }
        line = googleMap.addPolyline(options);

    }

    void stopRepeatingTask() {
        if (staticCarRunnable != null) {
            handler.removeCallbacks(staticCarRunnable);
        }
    }


    private void startBikeAnimation(final LatLng start, final LatLng end) {
        Log.i(TAG, "startBikeAnimation called...");

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(ANIMATION_TIME_PER_ROUTE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                //LogMe.i(TAG, "Car Animation Started...");
                v = valueAnimator.getAnimatedFraction();
                lng = v * end.longitude + (1 - v)
                        * start.longitude;
                lat = v * end.latitude + (1 - v)
                        * start.latitude;

                LatLng newPos = new LatLng(lat, lng);
                carMarker.setPosition(newPos);
                carMarker.setAnchor(0.5f, 0.5f);
                carMarker.setRotation(getBearing(start, end));

//                carMarker.setIcon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar)));
                carMarker.setIcon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar)));
                // todo : Junaid > i can delay here
                googleMap.moveCamera(CameraUpdateFactory
                        .newCameraPosition
                                (new CameraPosition.Builder()
                                        .target(newPos)
                                        .zoom(defaultZoom)
                                        .build()));

                startPosition = carMarker.getPosition();

            }

        });
        valueAnimator.start();
    }

    public Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
//                getDriverLocationUpdate();
                getDriverLocationUpdate(token,vehicle_id);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            handler.postDelayed(mStatusChecker, DELAY);

        }
    };

    void startGettingOnlineDataFromCar() {
        handler.post(mStatusChecker);
    }

    // CreatePolyLineOnly
    void dynamicPolyLine() {

        googleMap.clear();
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

        polyLineList = MapUtils.decodePoly(polyLine);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        googleMap.animateCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(5);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = googleMap.addPolyline(polylineOptions);
            }
        });
    }


    private void getAddress(double latitude, double longitude) {
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                bookAddress = addressList.get(0).getAddressLine(0);
                String city = addressList.get(0).getLocality();
                String state = addressList.get(0).getAdminArea();
                String subArea = addressList.get(0).getSubAdminArea();
                String country = addressList.get(0).getCountryName();
                String postalCode = addressList.get(0).getPostalCode();
                String knownName = addressList.get(0).getFeatureName();
                Log.w(TAG,"getAddress Address :"+bookAddress);
                tv_location.setText(bookAddress);
//                AppPreferences.savePreferences(mContext, VariablesConstant.ADDRESS_LOCATION, bookAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //colorCar - return marker color
    private int getMarkerColor(String colorCar){
//        Log.e(TAG,"getMarkerColor colorCar : "+colorCar);
        int carImage = R.drawable.car_blue;
        switch (colorCar) {
            case "green":
                carImage =  R.drawable.car_green;
                break;

            case "red":
                carImage = R.drawable.car_red;
                break;

            case "grey":
                carImage =  R.drawable.car_grey;
                break;

            case "yellow":
                carImage =   R.drawable.car_yellow;
                break;

            case "blue":
                carImage =   R.drawable.car_blue;
                break;
        }

        return carImage;
    }

    // colorVehicle, vehicleType
    private int setCarColor(String colorCar,String vehicleType) {
//        Log.w(TAG,"setCarColor VehicleType: "+vehicleType);
//        Log.w(TAG,"setCarColor colorCar : "+colorCar);
        switch (colorCar) {
            case "green":
                return getGreenIcon(vehicleType);

            case "red":
                return getRedIcon(vehicleType);

            case "yellow":
                return getYellowIcon(vehicleType);

            case "blue":
                return getBlueIcon(vehicleType);

            default:
                return getBlueIcon(vehicleType);
        }
    }

    private int setCarColor(String colorCar) {
//        Log.w(TAG,"setCarColor VehicleType: "+mVehicleType);
//        Log.w(TAG,"setCarColor colorCar : "+colorCar);

        switch (colorCar) {
            case "green":
                return getGreenIcon(mVehicleType);

            case "red":
                return getRedIcon(mVehicleType);

            case "yellow":
                return getYellowIcon(mVehicleType);

            case "blue":
                return getBlueIcon(mVehicleType);

            default:
                return getBlueIcon(mVehicleType);
        }
    }

    // return type colorIcon
    private int getGreenIcon(String vehicleType){
//        Log.e(TAG,"getGreenIcon vehicleType : "+vehicleType);
        switch (vehicleType){
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.scooter_green;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_green;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.chota_hathi_green;

            case CommonMap.VehicleType.CAR:
                return R.drawable.car_green;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.crane_green;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.rickshaw_green;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_green;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.taxi_green;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.tractor_green;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ambulance_green;

            case CommonMap.VehicleType.BUS:
                return R.drawable.bus_green;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_green;
//                return R.drawable.test2;

            default:
                return R.drawable.car_green;
        }
    }

    private int getRedIcon(String vehicleType){
//        Log.e(TAG," getRedIcon vehicleType : "+vehicleType);
        switch (vehicleType){
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.scooter_red;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_red;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.chota_hathi_red;

            case CommonMap.VehicleType.CAR:
                return R.drawable.car_red;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.crane_red;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.rickshaw_red;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_red;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.taxi_red;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.tractor_red;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ambulance_red;

            case CommonMap.VehicleType.BUS:
                return R.drawable.bus_red;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_red;

            default:
                return R.drawable.car_red;
        }
    }

    private int getYellowIcon(String vehicleType){
//        Log.e(TAG," getYellowIcon type : "+vehicleType);
        switch (vehicleType){
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.scooter_yellow;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_yellow;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.chota_hathi_yellow;

            case CommonMap.VehicleType.CAR:
                return R.drawable.car_yellow;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.crane_yellow;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.rickshaw_yellow;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_yellow;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.taxi_yellow;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.tractor_yellow;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ambulance_yellow;

            case CommonMap.VehicleType.BUS:
                return R.drawable.bus_yellow;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_yellow;

            default:
                return R.drawable.car_yellow;
        }
    }

    private int getBlueIcon(String vehicleType){
//        Log.e(TAG,"getBlueIcon vehicleType : "+vehicleType);
        switch (vehicleType){
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.scooter_blue;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_blue;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.chota_hathi_blue;

            case CommonMap.VehicleType.CAR:
                return R.drawable.car_blue;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.crane_blue;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.rickshaw_blue;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_blue;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.taxi_blue;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.tractor_blue;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ambulance_blue;

            case CommonMap.VehicleType.BUS:
                return R.drawable.bus_blue;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_blue;

            default:
                return R.drawable.car_blue;
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // https://stackoverflow.com/questions/4721449/how-can-i-enable-or-disable-the-gps-programmatically-on-android
        gpsENABLE();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Toast.makeText(mContext, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(mContext, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }



    private void gpsENABLE() {
        //buildGoogleApiClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location2 settings are satisfied. The client can initialize location2
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }


}