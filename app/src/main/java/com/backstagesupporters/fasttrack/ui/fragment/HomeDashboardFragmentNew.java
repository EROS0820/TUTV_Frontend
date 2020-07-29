package com.backstagesupporters.fasttrack.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.backstagesupporters.fasttrack.utils.DirectionsParser;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.activity.AllActivitiesActivity;
import com.backstagesupporters.fasttrack.service.LocationUpdatesService;
import com.backstagesupporters.fasttrack.models.MyLocation;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.models.VehicleDetails;
import com.backstagesupporters.fasttrack.ui.myMap.MapUtils;
import com.backstagesupporters.fasttrack.ui.myMap.MyHomeMapActivity;
import com.backstagesupporters.fasttrack.responseClass.CarDetailsResponse;
import com.backstagesupporters.fasttrack.responseClass.CarParkingResponse;
import com.backstagesupporters.fasttrack.responseClass.EngineStatusResponse;
import com.backstagesupporters.fasttrack.responseClass.ShowLocationResponse;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.github.aakira.expandablelayout.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.maps.model.JointType.ROUND;


public class HomeDashboardFragmentNew extends Fragment implements   View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{ // , LocationListener
    private Polyline mPolyline;
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;

    private  Double Origin,Desti;
    private ArrayList<LatLng> points; //added
    Polyline line; //added
    private View mapViewView;
    private MapView mapView;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng current_LatLang;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
//    private Marker marker;
    int count = 1;
    ArrayList<LatLng> listpoint;
    private float defaultZoom=17.0f;

    private LatLng liveLocation = new LatLng(0, 0);
    private LatLng StartPoint = new LatLng(0,0);
    private Geocoder geocoder;
    private ArrayList markerPoints = new ArrayList();
    private List<Address> addressList;
    private  List<Address> ori;
    private  List<Address> Dest;
    private double current_latitude, current_longitude;
    private TextView tv_location;
    private LinearLayout ll_location_address;
    private String lati, longi, bookAddress;

    private LinearLayout ll_call_driver,ll_all_activity,ll_engine_on_off,ll_car_parking,ll_sos;
    private ImageView iv_call_driver,iv_all_activity,iv_engine_on_off,iv_car_parking,iv_sos,
            iv_carStatusColor,iv_zoom;
    private ImageView iv_plus,iv_minus;
    private TextView tv_your_car_is_stopped,tv_speed;
    private Spinner vehicleSpinner;
    private String call_driver,all_activity, car_parked,engine_on_off,car_parking,sos;
    private String vehicle_id, vehicle_brand,vehicle_name,vehicle_type,vehicle_no,vehicle_image;
    private String owner_id,sos_number,driver_number,driver_name, car_signal_strength,
            car_parking_msg="", engine_status_msg="", myVehicle_id;

    public  ImageView iv_signal,iv_refresh,iv_notification,Img_btn_fullmap;

    private String colorCar="", car_color="",car_position="";
    private String speed, gps_length,satellites,direction,alarm, signal_strength, position_color;
    private boolean iscColorSos = false,iscColorcall_driver = false;

    private List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
    private ArrayList myVehicleListSpinner = new ArrayList();
    private String token,getParking_status="",getEngine_status="";

    // TODO: 1/4/2020
    private static final long DELAY = 4500;
    private static final long ANIMATION_TIME_PER_ROUTE = 3000;
    String polyLine = "q`epCakwfP_@EMvBEv@iSmBq@GeGg@}C]mBS{@KTiDRyCiBS";
    GoogleMap googleMap;
    private PolylineOptions polylineOptions;
    private Polyline greyPolyLine;
    private SupportMapFragment mapFragment;
    private Handler handler;
    private Marker carMarker;
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
    List<LatLng> polyLineList;
    private double lat, lng;

    // Give your Server URL here >> where you get car location update
    private boolean isFirstPosition = true;
    private Double startLatitude;
    private Double startLongitude;


    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    //=Interface Declaration
    private ProgressDialog pd;
    private ApiInterface apiInterface;



    public HomeDashboardFragmentNew() {
        // Required empty public constructor
        Log.i(TAG,"-- constructor");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_home_dashboard, container, false);
        View view = inflater.inflate(R.layout.fragment_home_dashboard, container, false);
        myReceiver = new MyReceiver();
        mContext = getActivity();
        points = new ArrayList<LatLng>();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(getActivity(),VariablesConstant.TOKEN);

        findViewById(view);
        listpoint = new ArrayList<>();
//        btn_live_tracking.setOnClickListener(this);
        ll_call_driver.setOnClickListener(this);
        ll_all_activity.setOnClickListener(this);
        ll_engine_on_off.setOnClickListener(this);
        ll_car_parking.setOnClickListener(this);
        ll_sos.setOnClickListener(this);

        //        Log.e(TAG,"token-"+token);
        if (vehicleArrayList.isEmpty()){
            Log.e(TAG,"vehicleArrayList Size: "+vehicleArrayList.size());
            Log.e(TAG,"API Call");
            // ======= API Call ==========
            if (CheckNetwork.isNetworkAvailable(mContext)) {
                getVehicleCall(token);
            } else {
                Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }


//        buildGoogleApiClient();//Init Google API Client
        myMapView(view);
//        myMapView();
        gpsENABLE();
        CheckPermissions();
        turnGPSOn();

        // Gets to GoogleMap from the MapView and does initialization stuff
//        mMap = mapView.getMap();
//        mMap = mapView.getMapAsync(this);
//        mapView = (MapView) view.findViewById(R.id.mapView);
//        mapView.getMapAsync(this);

//        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
//        MapsInitializer.initialize(this.getActivity());


        // TODO: 1/4/2020
        vehicle_id = AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_ID);
        Log.d(TAG, "intent vehicle_id : " + vehicle_id);
//        staticPolyLine();
        handler = new Handler();

        ll_location_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isNetworkAvailable(mContext)) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build((Activity) mContext);
                        startActivityForResult(intent, 1);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });


        Img_btn_fullmap = view.findViewById(R.id.Img_btn_fullmap);
        Img_btn_fullmap.setEnabled(false);
        Img_btn_fullmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), Fullmap.class);
                Intent intent = new Intent(getActivity(), MyHomeMapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("token","hh");
                bundle.putString("vehicle_id",vehicle_id);
                bundle.putDouble("Ori",Origin);
                bundle.putDouble("Dest",Desti);
                intent.putExtras(bundle);
                intent.putExtra("Token",token);
                startActivity(intent);
            }
        });


        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myReceiver);
        mapView.onDestroy();
        // set null when activity destry - SIGNAL_STRENGTH
        AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, "0");
    }



    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        buildGoogleApiClient();
        mGoogleApiClient.connect();

//        getCarDetailsCall(token,vehicle_id);

    }


    // TODO: 1/4/2020
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
                                            .zoom(15.5f)
                                            .build()));


                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 5000);

        }
    };

    void stopRepeatingTask() {

        if (staticCarRunnable != null) {
            handler.removeCallbacks(staticCarRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRepeatingTask();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.btn_live_tracking:
                //replaceFragment(new HomeLiveFragment());
                replaceFragment(new LiveTrackingFragment());
                Log.i(TAG,"called LiveTrackingFragment");
                break;*/

            case R.id.ll_call_driver:
                callByDialorMethod();
                break;

            case R.id.ll_sos:
                Log.i(TAG,"called ll_sos");
                sendSosMethod();

                break;

            case R.id.ll_all_activity:
                startActivity(new Intent(mContext, AllActivitiesActivity.class));
                Log.i(TAG,"called ll_all_activity");
                break;
            case R.id.ll_engine_on_off:

                if (getEngine_status.equalsIgnoreCase("")){
                    engine_status_msg = getString(R.string.do_you_want_engine_off);
                    engineOnOffMethodDialog();
                }else if (getEngine_status.equalsIgnoreCase("0")){
                    engine_status_msg = getString(R.string.do_you_want_engine_on);
                    engineOnOffMethodDialog();
                }else {
                    engine_status_msg = getString(R.string.do_you_want_engine_off);
                    engineOnOffMethodDialog();
                }

                break;

            case R.id.ll_car_parking:
                Log.i(TAG,"called ll_car_parking");
                //"parking_status":0,"parking_status":1
                getParking_status = AppPreferences.loadPreferences(mContext, VariablesConstant.PARKING_STATUS);
                Log.e(TAG, "ll_car_parking getParking_status :" + getParking_status);
                if (getParking_status.equalsIgnoreCase("")){
                    car_parking_msg = getString(R.string.do_you_want_car);
                    parkingMethodDialog();
                }else if (getParking_status.equalsIgnoreCase("0")){
                    car_parking_msg = getString(R.string.do_you_want_car_not);
                    parkingMethodDialog();
                }else {
                    car_parking_msg = getString(R.string.do_you_want_car);
                    parkingMethodDialog();
                }
                break;

        }
    }


    private void callByDialorMethod() {
        Log.e(TAG,"callDriverMethod driver_number :"+driver_number);

        // Use format with "tel:" and phone number to create phoneNumber.
        if (driver_number!=null){
            Intent intent = new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse("tel:0123456789"));
            intent.setData(Uri.parse("tel:" + driver_number));
            startActivity(intent);
        }else {
            Toast.makeText(mContext, "Number not found", Toast.LENGTH_SHORT).show();
        }

    }




    // owner_id,sos_number,driver_number,driver_name,car_color,
    // sendSMS(Context context, String phoneNo, String msg)
    private void  sendSosMethod() {
        Log.i(TAG, "getCarDetailsCall2 sos_number :" + sos_number);
        String SosMessage = driver_name + " triggered the Emergency button in his/her Fast Track app. As " + driver_name + " Emergency contact, we suggest you to call " + driver_name + " now or the local police";
        Log.i(TAG, "getCarDetailsCall2 Sos Message :" + SosMessage);
//      sendSms(SosMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Send Sms");
        builder.setMessage("Are you sure, You want send sms");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                sendSMSByIntent(SosMessage);

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

    private void sendSMSByIntent(String smsMessage) {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", new String(sos_number));
        smsIntent.putExtra("sms_body", smsMessage);

        try {
            startActivity(smsIntent);

            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }




    private void engineOnOffMethodDialog(){

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
                               Log.i(TAG,"User are not Valid");
//                                Toasty.error(mContext,"User are not Valid", Toast.LENGTH_SHORT).show();
                            }else if (vehicle_id.isEmpty()){
//                                Toasty.error(mContext,"Please select the Vehicle", Toast.LENGTH_SHORT).show();
                                Log.i(TAG," Please select the Vehicle");
                            }else {
                                Log.e(TAG, "engineOnOffMethodDialog token :" + token);
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
        Log.e(TAG, "parkingMethodDialog car_parking_msg :" + car_parking_msg);
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
                                Log.i(TAG,"User are not Valid ");
//                                Toasty.error(mContext,"User are not Valid", Toast.LENGTH_SHORT).show();
                            }else if (vehicle_id.isEmpty()){
                                Log.i(TAG," Please select the Vehicle");
//                                Toasty.error(mContext,"Please select the Vehicle", Toast.LENGTH_SHORT).show();
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

//        mapViewView = view.findViewById(R.id.mapViewview);
        vehicleSpinner =view.findViewById(R.id.vehicleSpinner);
        ll_call_driver =view.findViewById(R.id.ll_call_driver);
        ll_all_activity =view.findViewById(R.id.ll_all_activity);
        ll_engine_on_off =view.findViewById(R.id.ll_engine_on_off);
        ll_car_parking =view.findViewById(R.id.ll_car_parking);
        ll_sos =view.findViewById(R.id.ll_sos);
        ll_location_address =view.findViewById(R.id.ll_location_address);
        tv_location =view.findViewById(R.id.tv_location);
        iv_carStatusColor =view.findViewById(R.id.iv_carStatusColor);
        tv_your_car_is_stopped =view.findViewById(R.id.tv_your_car_is_stopped);
        iv_engine_on_off =view.findViewById(R.id.iv_engine_on_off);
        iv_call_driver =view.findViewById(R.id.iv_call_driver);
        iv_all_activity =view.findViewById(R.id.iv_all_activity);
        iv_car_parking =view.findViewById(R.id.iv_car_parking);
        iv_sos =view.findViewById(R.id.iv_sos);
        iv_zoom =view.findViewById(R.id.iv_zoom);
        iv_plus =view.findViewById(R.id.iv_plus);
        iv_minus =view.findViewById(R.id.iv_minus);
        tv_speed =view.findViewById(R.id.tv_speed);
        iv_plus =view.findViewById(R.id.iv_plus);
        iv_minus =view.findViewById(R.id.iv_minus);
    }


    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
        fragmentTransaction.commit();
    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }


    private void setVehicleSpinner() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext,R.layout.spinner_item, myVehicleListSpinner);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
        vehicleSpinner.setAdapter(spinnerArrayAdapter);

        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                int mySelectedVehicleId;
                if(selectedItem.equals(getString(R.string.please_select_vehicle_type))) {
                    Log.i(TAG,"selectedItem "+selectedItem);
                }else {
                    vehicle_name = parent.getItemAtPosition(position).toString();
                    mySelectedVehicleId = parent.getSelectedItemPosition() +1;

                    vehicle_id = String.valueOf(vehicleArrayList.get(position).getVehicleId());

                    Log.i(TAG,"selectedItem myId "+mySelectedVehicleId);
                    Log.i(TAG,"vehicle_id : "+vehicle_id);
                    Log.i(TAG,"selectedItem vehicle_name "+vehicle_name);
                    AppPreferences.savePreferences(mContext, VariablesConstant.VEHICLE_ID, vehicle_id);

                    getCarDetailsCall(token,vehicle_id);

                    selectSpinnerValue(vehicleSpinner, vehicle_id);

                    String myvehicle_id=  AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_ID);
                    Log.i(TAG,"myvehicle_id "+myvehicle_id);
                   /* if (!myvehicle_id.isEmpty()){
                        int vId = Integer.parseInt(myvehicle_id);
                        vehicleSpinner.setSelection(vId);
                    }*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG,getString(R.string.please_select_vehicle_type)+" : "+parent.toString());
            }
        });
    }



    private void selectSpinnerValue(Spinner spinner, String myString) {
        Log.i(TAG,"myvehicle_id "+myString);
        int index = 0;
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(myString)){
                spinner.setSelection(i);
                break;
            }
        }
    }

    //================ Api Call ============================
    // showVehicles
    private void getVehicleCall(final String token) {
      /*  pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();*/
        /**
         * GET
         * http://3.135.158.46/api/show_vehicles?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8zLjEzNS4xNTguNDZcL2FwaVwvbG9naW4iLCJpYXQiOjE1NzYyMjg5MDgsImV4cCI6MTU3NjIzMjUwOCwibmJmIjoxNTc2MjI4OTA4LCJqdGkiOiI5TW1ybFdlRGl6aTdTUTY3Iiwic3ViIjoxLCJwcnYiOiI4N2UwYWYxZWY5ZmQxNTgxMmZkZWM5NzE1M2ExNGUwYjA0NzU0NmFhIn0.eTTp34zaQ08FBJHjtIhGtwQ68lfRuakN7ti3uuVQFkA
         */

        Call<VehiclesResponse> call = apiInterface.showVehicles(token);
        call.enqueue(new Callback<VehiclesResponse>() {
            @Override
            public void onResponse(Call<VehiclesResponse> call, Response<VehiclesResponse> response) {
//                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e(TAG, "getVehicleCall Response >>>>" + str_response);
                int  responseCode  = response.code();
                Log.e(TAG, "responseCode :" + responseCode);

                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            VehiclesResponse vehiclesResponse = response.body();
                            assert vehiclesResponse != null;
                            int status = vehiclesResponse.getStatus();
                            if (status== 200) {
                                vehicleArrayList  = vehiclesResponse.getVehicles();
                                Log.e(TAG, "VehiclesResponse-listVehicle size :" +vehicleArrayList.size() );
                                for (int i=0; i<vehicleArrayList.size();i++){
                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
//                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleName() +" "+ vehicleArrayList.get(i).getVehicleNo());
                                }
                                //vehicleSpinner
                                setVehicleSpinner();
                            } else {
                                Log.e(TAG, "Get Vehicle Status Code :"+status);
//                                Toasty.error(mContext, "Get Vehicle Status Code :"+status, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
//                                Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                                break;
                            case 404:
//                                Toasty.error(mContext, getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
//                                Toasty.error(mContext, getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
//                                Toasty.error(mContext, getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
//                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Massage"+e.toString());
//                    Toasty.error(mContext, "Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
//                pd.dismiss();
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }

    // engine_status
    private void getEngineStatusCall(String token, String vehicle_id) {
//        pd = new ProgressDialog(mContext);
//        pd.setMessage("Loading Please Wait...");
//        pd.setCancelable(false);
//        pd.show();

        /**
         * GET
         *http://3.135.158.46//api/engine_status?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8xMzguMTk3LjIxMC4zNlwvYXBpXC9sb2dpbiIsImlhdCI6MTU3NDc1MzUxNiwiZXhwIjoxNTc0NzU3MTE2LCJuYmYiOjE1NzQ3NTM1MTYsImp0aSI6IkJQcXZUSGpQdjVMcG9NV20iLCJzdWIiOjEsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.8Cs0UJlaelZs-CpcZEdLnz8mF95avfWY8kCk3xG0PhM&vehicle_id=1
         *
         */
        Call<EngineStatusResponse> call = apiInterface.engineStatus(vehicle_id,token);
        call.enqueue(new Callback<EngineStatusResponse>() {
            @Override
            public void onResponse(Call<EngineStatusResponse> call, Response<EngineStatusResponse> response) {
//                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e(TAG, "getEngineStatusCall Response >>>>" + str_response);
                int  responseCode  = response.code();
                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            EngineStatusResponse engineStatusResponse = response.body();
                            assert engineStatusResponse != null;
                            int status = Integer.parseInt(engineStatusResponse.getStatus());
                            String message = engineStatusResponse.getMessage();
                            //0 -> Off 1->On
                            int engine_status = Integer.parseInt(engineStatusResponse.getEngineStatus());
                            AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS, String.valueOf(engine_status));
                            getEngine_status = AppPreferences.loadPreferences(mContext, VariablesConstant.ENGINE_STATUS);
                            Log.e(TAG, "getEngine_status :" + getEngine_status);
                            Log.e(TAG,"engine_status"+engine_status);
                            Log.i(TAG,"message :"+message);

                            if (engine_status ==0){
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
//                                Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                                break;
                            case 404:
//                                Toasty.error(mContext, getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
//                                Toasty.error(mContext, getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
//                                Toasty.error(mContext, getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
//                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"getEngineStatus Massage :"+e.toString());
                    // err_unauthorized_token_expired
//                    Toasty.error(mContext, "getEngineStatus Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EngineStatusResponse> call, Throwable t) {
//                pd.dismiss();
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"getEngineStatus onFailure"+t.toString());
            }
        });

    }

    // car_parking
    private void getCarParkingCall(String token, String vehicle_id) {
//        pd = new ProgressDialog(mContext);
//        pd.setMessage("Loading Please Wait...");
//        pd.setCancelable(false);
//        pd.show();

        /**
         * GET
         *http://3.135.158.46//api/car_parking?vehicle_id=1&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8xOC4yMTYuMTc0LjIxMVwvYXBpXC9sb2dpbiIsImlhdCI6MTU3NTM2NjAzNSwiZXhwIjoxNTc1MzY5NjM1LCJuYmYiOjE1NzUzNjYwMzUsImp0aSI6Ikxnd1lDS0dDbW5RTnd1TkoiLCJzdWIiOjEsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.XmqNrrRGb2Pxp0YbmJ3iNm4aG2ydMGAFBOxqv5U7wm8
         *
         */
        Call<CarParkingResponse> call = apiInterface.carParking(vehicle_id,token);
        call.enqueue(new Callback<CarParkingResponse>() {
            @Override
            public void onResponse(Call<CarParkingResponse> call, Response<CarParkingResponse> response) {
//                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e(TAG, "getCarParkingCall Response >>>>" + str_response);
                int  responseCode  = response.code();
                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        //"parking_status":0,"parking_status":1
                        if (response.isSuccessful()) {
                            CarParkingResponse carParkingResponse = response.body();
                            assert carParkingResponse != null;
                            int status = carParkingResponse.getStatus();
                            if (status==200) {
                                String message = carParkingResponse.getMessage();
                                //0 -> Off 1->On
                                int parking_status = carParkingResponse.getParkingStatus();

                                AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS, String.valueOf(parking_status));
                                getParking_status = AppPreferences.loadPreferences(mContext, VariablesConstant.PARKING_STATUS);
                                Log.e(TAG, "getParking_status :" + getParking_status);
                                Log.i(TAG,"parking_status :"+parking_status);
                                Log.i(TAG,"message :"+message);
                                if (parking_status ==0){
//                                iv_car_parking.setBackgroundResource(R.drawable.car_parked_red);
                                    iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked_red));
                                }else {
//                                iv_engine_on_off.setBackgroundResource(R.drawable.engine);
                                    iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked));
                                }
                                //Toasty.success(mContext, "Message : "+message, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Get Car Parking Status Code :"+status);
//                                Toasty.error(mContext, "Get Car Parking Status Code :"+status, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
//                                Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                                break;
                            case 404:
//                                Toasty.error(mContext, getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
//                                Toasty.error(mContext, getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
//                                Toasty.error(mContext, getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
//                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"getCarParking Massage :"+e.toString());
//                    Toasty.error(mContext, "getCarParking Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CarParkingResponse> call, Throwable t) {
//                pd.dismiss();
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
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

        /**
         * GET
         * car_details
         *http://3.135.158.46/api/car_details?vehicle_id=1&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8zLjEzNS4xNTguNDZcL2FwaVwvbG9naW4iLCJpYXQiOjE1NzYyMTkxMjYsImV4cCI6MTU3NjIyMjcyNiwibmJmIjoxNTc2MjE5MTI2LCJqdGkiOiJrMnAzcVdObU5aV0VXTlJZIiwic3ViIjoxLCJwcnYiOiI4N2UwYWYxZWY5ZmQxNTgxMmZkZWM5NzE1M2ExNGUwYjA0NzU0NmFhIn0.QXpIJ-eFN7wu8R-PEYCV4Wu02uovtLsnLCeGVT7aOx0
         *
         */
        Call<CarDetailsResponse> call = apiInterface.carDetails(vehicle_id,token);
        call.enqueue(new Callback<CarDetailsResponse>() {
            @Override
            public void onResponse(Call<CarDetailsResponse> call, Response<CarDetailsResponse> response) {
//                pd.dismiss();
                Log.e(TAG, "getCarDetailsCall Response >>>>" + response.toString());
                int  responseCode  = response.code();
                Log.e(TAG, "responseCode :" + responseCode);

                try {
                    if (responseCode ==200){
                        CarDetailsResponse carDetailsResponse = response.body();
                        assert carDetailsResponse != null;
                        int status =  carDetailsResponse.getStatus();
                        if (status==200) {
                            // String vehicle_name,vehicle_type,vehicle_no,vehicle_image,
                            // owner_id,sos_number,driver_number,driver_name,car_color;
                            String  message = carDetailsResponse.getMessage();
                            VehicleDetails vehicleDetails = carDetailsResponse.getVehicleDetails();
                            owner_id = vehicleDetails.getOwnerId();
                            sos_number = vehicleDetails.getSosNumber();
                            driver_number = vehicleDetails.getDriverNumber();
                            driver_name = vehicleDetails.getDriverName();
                            car_color = vehicleDetails.getCarColor();
                            vehicle_no = vehicleDetails.getVehicleNo();
                            car_signal_strength = vehicleDetails.getSignalStrength();
                            Log.i(TAG, "car_color :" + car_color);
                            Log.i(TAG, "car_signal_strength :" + car_signal_strength);
                            Log.i(TAG, "message :" + message);
                            Log.e(TAG, "getCarDetailsCall sos_number :" + sos_number);
                            Log.e(TAG, "getCarDetailsCall driver_number :" + driver_number);
                            Log.e(TAG, "getCarDetailsCall driver_name :" + driver_name);
                            Log.e(TAG, "getCarDetailsCall vehicle_no :" + vehicle_no);

//                            sendData(signal_strength);
//                            HomeActivity.setsignal_strength(signal_strength);
//                                Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Get Car Details Response code :"+status);
//                            Toasty.error(mContext, "Get Car Details Response code :"+status, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
//                                Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                                break;
                            case 404:
//                                Toasty.error(mContext, getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
//                                Toasty.error(mContext, getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
//                                Toasty.error(mContext, getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
//                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "getCarDetails Massage :"+e.toString());
//                    Toasty.error(mContext, "getCarDetails Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CarDetailsResponse> call, Throwable t) {
//                pd.dismiss();
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"getCarDetails onFailure"+t.toString());
            }
        });

    }


        //==============================Google Map ================================================================

    private void myMapView(View view) {
        mapView = view.findViewById(R.id.mapView); // map
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();

            mapView.getMapAsync(this);
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @TargetApi(Build.VERSION_CODES.P)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onMapReady(final GoogleMap mMap) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(false);

                }

                if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                    // Get the button view
//                    View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    ImageView locationButton = (ImageView) ((View)
                            mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    locationButton.setImageResource(R.drawable.ic_action_mylocation);
                    // and next place it, on bottom right (as Google Maps app)
//                    locationButton.setMinimumWidth(26);
//                    locationButton.setMinimumHeight(26);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

                    // my position button
                    View myLocationParent = ((View) getView().findViewById(Integer.parseInt("1")).getParent());
                    View myLocationParentParent = ((View) myLocationParent.getParent());
                    int positionWidth = myLocationParent.getLayoutParams().width;
                    int positionHeight = myLocationParent.getLayoutParams().height;

                    // lay out position button
                    FrameLayout.LayoutParams positionParams = new FrameLayout.LayoutParams(positionWidth, positionHeight);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                    layoutParams.setMargins(0, 0, 60, 20);
                    layoutParams.setMargins(0, 0, -20, -60);

                    locationButton.setBackgroundColor(R.color.transparent);
                    locationButton.setClipToOutline(false);
                    locationButton.setBackgroundResource(R.drawable.shape_circle);
                    locationButton.setElevation(0);
                    locationButton.setPadding(5, 5, 5, 5);
                    myLocationParent.setLayoutParams(positionParams);

                    // click locationButton
                    locationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isGPSEnabled(getActivity())){
                                gpsENABLE();
//                                getCurrentLocation();
                            }else {
                                Toast.makeText(getActivity(), "Please Active the Location", Toast.LENGTH_SHORT).show();
                            }
                            Log.i(TAG,"locationButton.setOnClickListener");
                        }
                    });
                }
            }
        });

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


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

    public boolean isGPSEnabled(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public void turnGPSOn(){
        String provider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getActivity().sendBroadcast(poke);
        }
    }

    public void gpsENABLE() {
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
            mMap.setMyLocationEnabled(false);
        }

        String mMapView = AppPreferences.loadPreferences(mContext, VariablesConstant.MapView_Type);
        Log.i(TAG,"map_view_type : "+mMapView);
         int mapInt = setMyMapType(mMapView);
         Log.i(TAG,"map_view_type : "+mapInt);

         // MAP_TYPE_NORMAL
        if (mapInt==0){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }else {
            mMap.setMapType(mapInt);
        }

        /**
         public static final int MAP_TYPE_NONE = 0;
         public static final int MAP_TYPE_NORMAL = 1;
         public static final int MAP_TYPE_SATELLITE = 2;
         public static final int MAP_TYPE_TERRAIN = 3;
         public static final int MAP_TYPE_HYBRID = 4;
         */

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        //googleMap.getUiSettings().setZoomControlsEnabled(true);

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUpMap();

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));

        // TODO: 1/4/2020
        this.googleMap = googleMap;
//        dynamicPolyLine();
//        startGettingOnlineDataFromCar();


       /* mCurrLocationMarker = mMap.addMarker(new MarkerOptions().position(current_LatLang)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));*/
//        mCurrLocationMarker =  mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.green))
//                .position(current_LatLang)
//                .flat(false));


        // Zooming the map iv_plus,iv_minus
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_LatLang));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_LatLang, 11));

//        LatLng current_LatLang = new LatLng(current_latitude, current_longitude);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_LatLang, defaultZoom));

        // Zoom in, animating the camera. - iv_plus,iv_minus
        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                String zoomIn = CameraUpdateFactory.zoomIn().toString();
                Log.e(TAG,"mMap zoomIn : "+zoomIn);
            }
        });

        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                String zoomOut = CameraUpdateFactory.zoomOut().toString();
                Log.e(TAG,"mMap zoomIn: "+zoomOut);
            }
        });



//        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        // Set a preference for minimum and maximum zoom.
//        mMap.setMinZoomPreference(6.0f);
//        mMap.setMaxZoomPreference(19.0f);

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
       // mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
       /* CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(MOUNTAIN_VIEW)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

    }


    // TODO: 1/4/2020
    private void getDriverLocationUpdate(String token, String vehicle_id) {

        Call<ShowLocationResponse> call = apiInterface.showLocation(token,vehicle_id);
        call.enqueue(new Callback<ShowLocationResponse>() {
            @Override
            public void onResponse(Call<ShowLocationResponse> call, retrofit2.Response<ShowLocationResponse> response) {
                Log.d("Manish", response.toString());
                // pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e(TAG, "getLocationShowCall Response >>>>" + str_response);
                int responseCode = response.code();
                Log.e(TAG, "responseCode :" + responseCode);

                try {
                    if (response.isSuccessful()) {
//                        int  responseCode  = response.code();
                        ShowLocationResponse locationResponse = response.body();
                        int status = locationResponse.getStatus();
                        Log.e(TAG, "status :" + status);
                        if (status == 200) {
                            Log.d("Junaid", locationResponse.getMessage());

                            List<MyLocation> locationResponseList = locationResponse.getLocation();
                            String soccermomLatitude = locationResponseList.get(0).getLatitude();
                            String soccermomLongitude = locationResponseList.get(0).getLongitude();
                            speed = locationResponseList.get(0).getSpeed();
                            gps_length = locationResponseList.get(0).getGpsLength();
                            satellites = locationResponseList.get(0).getSatellites();
                            direction = locationResponseList.get(0).getDirection();
                            alarm = locationResponseList.get(0).getAlarm();
                            car_position = locationResponseList.get(0).getPosition();          // "position": "Is Parked",- car position
                            position_color = locationResponseList.get(0).getPositionColor();   // pointer color
                            colorCar = locationResponseList.get(0).getColor();        // car color
                            String parking_status_bg = String.valueOf(locationResponseList.get(0).getParkingStatus());        // car color
                            String engine_status_bg = String.valueOf(locationResponseList.get(0).getEngineStatus());        // car color

                            Log.i(TAG, "signal_strength : " + signal_strength);

                            // TODO: 1/1/2020
                            startLatitude = Double.valueOf(soccermomLatitude);
                            startLongitude = Double.valueOf(soccermomLongitude);

                            tv_speed.setText(speed);
                            getAddress(startLatitude, startLongitude);
                            Log.d(TAG, "JUNAID: "+startLatitude + "--" + startLongitude);

//                            LatLng latLng = new LatLng(startLatitude,startLongitude);
//                            points.add(latLng);
//                            redrawLine(); //added

                            if (isFirstPosition) {
                                startPosition = new LatLng(startLatitude, startLongitude);

                                googleMap.clear();
                                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                                        flat(true).icon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar))));
                                carMarker.setAnchor(0.5f, 0.5f);

                                googleMap.moveCamera(CameraUpdateFactory
                                        .newCameraPosition
                                                (new CameraPosition.Builder()
                                                        .target(startPosition)
                                                        .zoom(defaultZoom)
                                                        .build()));

                                isFirstPosition = false;

                            } else {
                                endPosition = new LatLng(startLatitude, startLongitude);

                                Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);

                                if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {

                                    Log.e(TAG, "NOT SAME");
                                    startBikeAnimation(startPosition, endPosition);

                                } else {

                                    Log.e(TAG, "SAME");
                                }
                            }





                         /*   liveLocation = new LatLng(soccermomLatitude, soccermomLongitude);
                            if (count == 1) {
                                StartPoint = new LatLng(soccermomLatitude, soccermomLongitude);
                                count++;
                            }
                            getAddress(soccermomLatitude, soccermomLongitude);

                            startNavigation(soccermomLatitude, soccermomLongitude);*/

                        } else {
//                                Toasty.error(mContext, "Get Location Response code :"+status, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Get Location Response code :" + status);
                        }

                    } else {
                        // error case
                        switch (responseCode) {
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
//                                Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                                break;
                            case 404:
//                                Toasty.error(mContext, getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
//                                Toasty.error(mContext, getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
//                                Toasty.error(mContext, getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
//                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Massage :" + e.toString());
//                    Toasty.error(mContext, "Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<ShowLocationResponse> call, Throwable t) {
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

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

                // todo : Shihab > i can delay here
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

    Runnable mStatusChecker = new Runnable() {
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







    // TODO: 12/29/2019
    private void animateMarkerNew(final Location destination, final Marker marker) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(defaultZoom)
                                .build()));

                        marker.setRotation(getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }

    // TODO: 12/29/2019
    //Method for finding bearing between two points
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    // TODO: 12/29/2019
    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements HomeDashboardFragmentNew.LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    // TODO: 12/29/2019
    private void rotateMarker(final Marker marker, final float toRotation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

    }

    // TODO: 12/29/2019
    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


//    startNavigation(soccermomLatitude,soccermomLongitude)
    public void startNavigation(double mLatitude, double mLongitude) {
        String m =Double.toString(mLatitude);

        Log.d("ManishTest",m );

        try {
            Origin = mLatitude;
            Desti = mLongitude;
            mMap.clear();
            LatLng p1,p2     ;

            p1 =new LatLng(28.643698, 77.283979);
            p2 = new LatLng(28.643112, 77.284680);
//            String url = getRequestUrl(p1,p2);
//            TaskRequestDirrection taskRequestDirrection = new TaskRequestDirrection();
//            taskRequestDirrection.execute(url);

            // full screen click active
            Img_btn_fullmap.setEnabled(true);

            LatLng latLng = new LatLng(mLatitude,mLongitude);
            points.add(latLng);
//            redrawLine(); //added


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));

            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));


//             mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
//                            .fromResource(carImage))
//                    .position(liveLocation)
//                    .flat(true));

//            mMap.addCircle(new CircleOptions().center(liveLocation).radius(500.0).strokeWidth(3f).strokeColor(Color.RED));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(liveLocation ));

//            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(defaultZoom));
            List<LatLng> snappedPoints = new ArrayList<>();

            Location targetLocation = new Location(LocationManager.GPS_PROVIDER);
          //  Location targetLocation = new Location("");//provider name is unnecessary
            targetLocation.setLatitude(mLatitude);//your coords of course
            targetLocation.setLongitude(mLongitude);
//            float distanceInMeters =  targetLocation.distanceTo(myLocation);

            if (latLng != null) {
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                LatLng latLng = new LatLng(latitude, longitude);
                int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
                if (status == ConnectionResult.SUCCESS) {
                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar)))
                            .rotation(targetLocation.getBearing()).flat(true).anchor(0.5f, 0.5f)
                            .alpha((float) 0.91));
                } else {
                    GooglePlayServicesUtil.getErrorDialog(status, getActivity(), status);
                }
            }



            // TODO: 12/29/2019
           /*
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker arg0) {

//                    final LatLng startPosition = marker.getPosition();
//                    final LatLng finalPosition = new LatLng(12.7801569, 77.4148528);
                    final Handler handler = new Handler();
                    final long start = SystemClock.uptimeMillis();
                    final Interpolator interpolator = new AccelerateDecelerateInterpolator();
                    final float durationInMs = 3000;
                    final boolean hideMarker = false;
                    double bearing = bearingBetweenLocations(StartPoint,liveLocation);
//                    bearingBetweenLocations(SomePos,finalPosition);
                    rotateMarker(mCurrLocationMarker, (float) bearing);

                    handler.post(new Runnable() {
                        long elapsed;
                        float t;
                        float v;

                        @Override
                        public void run() {
                            // Calculate progress using interpolator
                            elapsed = SystemClock.uptimeMillis() - start;
                            t = elapsed / durationInMs;

                            LatLng currentPosition = new LatLng(
                                    StartPoint.latitude * (1 - t) + liveLocation.latitude * t,
                                    StartPoint.longitude * (1 - t) + liveLocation.longitude * t);

                            mCurrLocationMarker.setPosition(currentPosition);

                            // Repeat till progress is complete.
                            if (t < 1) {
                                // Post again 16ms later.
                                handler.postDelayed(this, 16);
                            } else {
                                if (hideMarker) {
                                    mCurrLocationMarker.setVisible(false);
                                } else {
                                    mCurrLocationMarker.setVisible(true);
                                }
                            }
                        }
                    });

                    return true;
                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void  redrawLine(){
        mMap.clear();

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for(int i=0; i<points.size(); i++){
            LatLng point = points.get(i);
            options.add(point);
        }
        line = mMap.addPolyline(options);

    }


    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin="+origin.latitude+","+origin.longitude;
        String str_des = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String mode ="mode-driving";
        String key = "key="+"AIzaSyCOMJanUWPuOAp067zCfs2_lR4Wxw6uPeQ";
        String param =str_org+"&"+str_des+"&"+sensor+"&"+mode+"&"+key;
        String output ="json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;
        return url;

    }


    private String requestDirection(String reqUrl) throws IOException {
        String responseString ="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection =(HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader  = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line ="";
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);

            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                    inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirrection extends AsyncTask<String ,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String responseString ="";
            try {
                responseString = requestDirection(strings[0]);
            }catch (IOException e){
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public  class TaskParser extends AsyncTask<String ,Void,List<List<HashMap<String,String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String,String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points  = null;
            PolylineOptions polylineOptions  = null;
            for(List<HashMap<String ,String >> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String,String> point : path){
                    double lat =Double.parseDouble(point.get("lat"));
                    double lon =Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);

            }
            if(polylineOptions != null){
                mMap.addPolyline(polylineOptions);
            }else {
//                Toast.makeText(mContext, "Direction not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAddress(double latitude, double longitude) {
        Log.e(TAG, "getAddress latitude:" + latitude);
        Log.e(TAG, "getAddress longitude :" + longitude);
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
                Log.e(TAG,"getAddress Address :"+bookAddress);
                tv_location.setText(bookAddress);
                AppPreferences.savePreferences(mContext, "ADDRESS_LOCATION", bookAddress);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


  /*  @Override
    public void onLocationChanged(Location location) {
//        mLocation = location;
        double latitude, longitude;

       *//* if( mListener != null ){
            mListener.onLocationChanged( location );

            //Move the camera to the user's location and zoom in!
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
        }*//*


        current_LatLang = new LatLng(location.getLatitude(), location.getLongitude());
//        latitude = mLocation.getLatitude();
        latitude = location.getLatitude();
//        longitude = mLocation.getLongitude();
        longitude = location.getLongitude();
        Log.d("latitude", latitude + "");
        Log.d("longitude", longitude + "");
        Log.d("LatLang current_LatLang", current_LatLang + "");

        getAddress(latitude, longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Toast.makeText(mContext,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(mContext,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            current_latitude = location.getLatitude();
            current_longitude = location.getLongitude();

            LatLng latLng = new LatLng(current_latitude, current_longitude);
            markerPoints.add(0, latLng);

            onMapReady(mMap); //refresh map

//            getAddress(current_latitude, current_longitude);

        }



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                lati = String.valueOf(place.getLatLng().latitude);
                longi = String.valueOf(place.getLatLng().longitude);

                getAddress(Double.parseDouble(lati), Double.parseDouble(longi));

                //============== Show location2 from PlaceAutoCompleteFragment ==================
//                current_latitude = place.getLatLng().latitude;
//                current_longitude = place.getLatLng().longitude;
//
//                onMapReady(mMap); //refresh Map
            }
        }
    }


    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG,"Receiver extends BroadcastReceiver");
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
//                updateLocation(token,String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),vehicle_id);
                getDriverShowCall(token,vehicle_id);

//                getLocationShowCall(token,vehicle_id);
            }
        }
    }
    private void getDriverShowCall(String token, String vehicle_id) {
        Log.d("sds","hf");
        Call<ShowLocationResponse> call = apiInterface.showLocation(token,vehicle_id);

        call.enqueue(new Callback<ShowLocationResponse>() {
            @Override
            public void onResponse(Call<ShowLocationResponse> call, Response<ShowLocationResponse> response) {
                Log.d("Manish",response.toString());
               // pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e(TAG, "getLocationShowCall Response >>>>" + str_response);
                int responseCode  = response.code();
                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (response.isSuccessful()) {
//                        int  responseCode  = response.code();
                        ShowLocationResponse locationResponse = response.body();
                        int status = locationResponse.getStatus();
                        Log.e(TAG, "status :" + status);
                        if (status==200 ) {
                            // mapView Visibility
//                            mapViewView.setVisibility(View.GONE);
//                            mapView.setVisibility(View.VISIBLE);
                            List<MyLocation> locationResponseList = locationResponse.getLocation();
                            double soccermomLatitude = Double.parseDouble(locationResponseList.get(0).getLatitude());
                            double soccermomLongitude = Double.parseDouble(locationResponseList.get(0).getLongitude());
                            speed = locationResponseList.get(0).getSpeed();
                            gps_length = locationResponseList.get(0).getGpsLength();
                            satellites = locationResponseList.get(0).getSatellites();
                            direction = locationResponseList.get(0).getDirection();
                            alarm = locationResponseList.get(0).getAlarm();
                            car_position = locationResponseList.get(0).getPosition();          // "position": "Is Parked",- car position
                            position_color = locationResponseList.get(0).getPositionColor();   // pointer color
                            colorCar = locationResponseList.get(0).getColor();        // car color
                            String parking_status_bg = String.valueOf(locationResponseList.get(0).getParkingStatus());        // car color
                            String engine_status_bg = String.valueOf(locationResponseList.get(0).getEngineStatus());        // car color

                            signal_strength = String.valueOf(locationResponseList.get(0).getSignalStrength());
                            AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, signal_strength);

                            AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS_BG, parking_status_bg);
                            AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS_BG, engine_status_bg);
                            String tokensignal = AppPreferences.loadPreferences(mContext, VariablesConstant.SIGNAL_STRENGTH);

                            Log.i(TAG, "signal_strength : " + signal_strength);
                            Log.i(TAG, "parking_status_bg : " + parking_status_bg);
                            Log.i(TAG, "engine_status_bg : " + engine_status_bg);

                            setCarParkingBg(parking_status_bg);
                            setEngineStatusBg(engine_status_bg);


                            tv_speed.setText(speed);
                            if (car_position.isEmpty()){

//                                tv_your_car_is_stopped.setText(getString(R.string.your_car_is_stopped));
                            }else {
                                tv_your_car_is_stopped.setText(car_position);
                            }
                              if (!position_color.equalsIgnoreCase("")){
                                if(position_color.equalsIgnoreCase("green")){
                                    iv_carStatusColor.setImageResource(R.drawable.point_green);
                                }else  if(position_color.equalsIgnoreCase("yellow")){
                                    iv_carStatusColor.setImageResource(R.drawable.point_yellow);
                                }else  if(position_color.equalsIgnoreCase("red")){
                                    iv_carStatusColor.setImageResource(R.drawable.point_red);
                                }

                            liveLocation = new LatLng(soccermomLatitude,soccermomLongitude);
                           if(count == 1){
                               StartPoint = new LatLng(soccermomLatitude,soccermomLongitude);
                               count++;
                           }
                            getAddress(soccermomLatitude,soccermomLongitude);

                            startNavigation(soccermomLatitude,soccermomLongitude);


                           /*       // TODO: 12/30/2019

                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(soccermomLatitude);
                            location.setLongitude(soccermomLongitude);
                            //calling method to animate marker
                            animateMarkerNew(location, mCurrLocationMarker);*/

                            } else {
//                                Toasty.error(mContext, "Get Location Response code :"+status, Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"Get Location Response code :"+status);
                            }
                        }
                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
//                                Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                                break;
                            case 404:
//                                Toasty.error(mContext, getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
//                                Toasty.error(mContext, getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
//                                Toasty.error(mContext, getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
//                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Massage :"+e.toString());
//                    Toasty.error(mContext, "Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShowLocationResponse> call, Throwable t) {
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }

    // engine_status_bg
    private void setEngineStatusBg(String engine_status_bg) {
        int my_getEngine_status = Integer.parseInt(engine_status_bg);
        if (my_getEngine_status ==1){
//                                iv_engine_on_off.setBackgroundResource(R.drawable.engine_red);
            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_red));
        }else {
//                                iv_engine_on_off.setBackgroundResource(R.drawable.engine);
            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine));
        }
    }



    //parking_status_bg
    private void setCarParkingBg(String parking_status_bg) {

        int my_getParking_status = Integer.parseInt(parking_status_bg);
        if (my_getParking_status ==1){
//                                iv_car_parking.setBackgroundResource(R.drawable.car_parked_red);
            iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked_red));
        }else {
//                                iv_engine_on_off.setBackgroundResource(R.drawable.engine);
            iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked));
        }

    }

    private void updateLocation(String token, String latitude, String longitude, String vehicle_id) {

        /**
         * POST
         * http://138.197.210.36/api/update_info
         */

        Call<ShowLocationResponse> call = apiInterface.updateLocation(token,latitude,longitude,vehicle_id);
        call.enqueue(new Callback<ShowLocationResponse>() {
            @Override
            public void onResponse(Call<ShowLocationResponse> call, Response<ShowLocationResponse> response) {
                Log.e(TAG, "updateLocation Response >>>>" + response.toString());
                int  responseCode  = response.code();
                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){

                    }else {
                        // error case
                        switch (responseCode){
                            case 401:
                                Log.e(TAG, getString(R.string.err_unauthorized_token_expired));
//                                Toasty.error(mContext, getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                                break;
                            case 404:
//                                Toasty.error(mContext, getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, getString(R.string.err_not_found));
                                break;
                            case 429:
                                Log.e(TAG, getString(R.string.err_Too_Many_Requests));
//                                Toasty.error(mContext, getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Log.e(TAG, getString(R.string.err_server_broken));
//                                Toasty.error(mContext, getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e(TAG, getString(R.string.err_unknown_error));
//                                Toasty.error(mContext, getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Massage"+e.toString());
//                    Toasty.error(mContext, "Massage :"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShowLocationResponse> call, Throwable t) {
//                Toasty.error(mContext, getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });

    }




    private int getMarkerColor(String colorCar){
        Log.e(TAG," getMarkerColor colorCar : "+colorCar);
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



}
