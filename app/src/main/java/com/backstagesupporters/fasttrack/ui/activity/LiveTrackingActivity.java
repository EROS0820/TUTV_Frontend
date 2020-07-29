package com.backstagesupporters.fasttrack.ui.activity;

import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.MyLocation;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.models.VehicleDetails;
import com.backstagesupporters.fasttrack.retrofitAPI.BaseApi;
import com.backstagesupporters.fasttrack.ui.fragment.VilleyDriverLocation;
import com.backstagesupporters.fasttrack.ui.myMap.LatLngInterpolator;
import com.backstagesupporters.fasttrack.ui.myMap.MyHomeMapActivity;
import com.backstagesupporters.fasttrack.notification.Config;
import com.backstagesupporters.fasttrack.notification.NotificationID;
import com.backstagesupporters.fasttrack.responseClass.CarDetailsResponse;
import com.backstagesupporters.fasttrack.responseClass.CarParkingResponse;
import com.backstagesupporters.fasttrack.responseClass.EngineStatusResponse;
import com.backstagesupporters.fasttrack.responseClass.ShowLocationResponse;
import com.backstagesupporters.fasttrack.responseClass.TickerResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.MySingleton;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.backstagesupporters.fasttrack.utils.vehicle.MyLocationAddress;
import com.backstagesupporters.fasttrack.utils.vehicle.VehicleTypesAndColors;
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
import com.google.maps.android.SphericalUtil;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.gloxey.gnm.parser.GloxeyJsonParser;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

import static com.backstagesupporters.fasttrack.ui.myMap.MapUtils.getBearing;
import static com.google.android.gms.maps.model.JointType.ROUND;

public class LiveTrackingActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String TAG= getClass().getSimpleName();
//    private static final String TAG = "fasttrack";
    private Context mContext;
    private Toolbar toolbar;
    private ImageView iv_signal,iv_refresh,iv_notification,iv_allMarkers, iv_tool_back_left;
    public  TextView tv_title;

    private float defaultZoom=17.0f;
    private RelativeLayout relativeLayoutView;
    private MapView mapView2;
    private MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int REQUEST_LOCATION = 199;

    private TextView tv_location;
    private String  mAddress;
    Polyline line; //added
    private ArrayList<LatLng> points=new ArrayList<>(); //added

    private LinearLayout ll_call_driver,ll_all_activity,ll_engine_on_off,ll_car_parking,ll_sos, ll_location_share,ll_speed, car_Status_msg;
    private CardView cv_location_address;
    RelativeLayout llFragmentHomeDashboardFullLayout,rl_iv_car,rl_engine_on_off;
    private ImageView iv_engine_on_off,iv_car_parking, iv_carStatusColor,Img_btn_fullmap,iv_plus,iv_minus, iv_car;
    private TextView tv_your_car_is_stopped,tv_speed,tv_ticket, tv_vehicleSpinner, tv_car_parking,tv_engine_on_off;
    private Spinner vehicleSpinner;
    private HorizontalScrollView bottom_layout;

    private String vehicle_id, vehicle_name,vehicle_type,vehicle_no,vehicle_color;
    private String sos_number,driver_number,driver_name;

    private List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>(); // VehicleList
    private ArrayList<String> myVehicleListSpinner = new ArrayList<>();          // VehicleListSpinner
    private ArrayList<String> mVehicleTypeList = new ArrayList<>();       // VehicleType
    private ArrayList<String> mVehicleColor = new ArrayList<>();       // VehicleColor
    private String token;
    private String userId ="";

    private static final long DELAY = 2000;
    private static final long ANIMATION_TIME_PER_ROUTE = 1000;
    private PolylineOptions polylineOptions;
    private Polyline greyPolyLine;
    private boolean stop = false;
    private Handler handlerLocation;
    private Handler signalHandlerRefresh;
    private Marker carMarker;
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
    private List<LatLng> polyLineList = new ArrayList<>();
    private double lat, lng;

    // Give your Server URL here >> where you get car location update
    private boolean isFirstPosition = true;
    private boolean isFirstPositionSpinner = true;
    private Double startLatitude=0.0;
    private Double startLongitude=0.0;
    String sLatitude="",sLongitude="";

    //=Interface Declaration
    private ProgressDialog pd;
    private ApiInterface apiInterface;
    MyLocationAddress mLocationAddress;
    VehicleTypesAndColors mVehicleTypesAndColors;
    HomeActivity homeActivity;
    private LatLng startposi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);
        mContext = LiveTrackingActivity.this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        // toolbar.setTitle("");       // Remove title in Toolbar
        //  tv_title.setText(getString(R.string.live_tracking));

        findViewById();
        iv_tool_back_left.setBackgroundResource(R.drawable.icon_back_blue);
        iv_allMarkers.setVisibility(View.GONE);

        ll_call_driver.setOnClickListener(this);
        ll_all_activity.setOnClickListener(this);
        ll_engine_on_off.setOnClickListener(this);
        ll_car_parking.setOnClickListener(this);
        ll_sos.setOnClickListener(this);
        ll_location_share.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        iv_notification.setOnClickListener(this);
        iv_tool_back_left.setOnClickListener(this);


        mapView2 = (MapView) findViewById(R.id.mapView2);
        mapView2.onCreate(savedInstanceState);

        // initializing Mapview
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this::onMapReady);

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * set marque
         * Set focus to the textview
         */
        tv_ticket.setSelected(true);

        // Permissions
        CheckPermissions();

        Img_btn_fullmap.setEnabled(false);


        // TODO: 3/5/2020
        mLocationAddress = new MyLocationAddress(mContext);
        mVehicleTypesAndColors = new VehicleTypesAndColors(mContext);
        homeActivity = new HomeActivity();

        Img_btn_fullmap.setOnClickListener(v -> {
            Intent intentMap = new Intent(mContext, MyHomeMapActivity.class);
            intentMap.putExtra(VariablesConstant.VEHICLE_ID,vehicle_id);
            intentMap.putExtra(VariablesConstant.VEHICLE_NUMBER,vehicle_no);
            intentMap.putExtra(VariablesConstant.VEHICLE_TYPE, vehicle_type);
            intentMap.putExtra(VariablesConstant.VEHICLE_COLOR, vehicle_color);
            intentMap.putExtra(VariablesConstant.DEFAULT_LATITUDE,sLatitude);
            intentMap.putExtra(VariablesConstant.DEFAULT_LONGITUDE,sLongitude);
            startActivity(intentMap);
            finish();

            // stop the Handler
            stopRepeatingTaskRefresh();

        });

    }


    private final Runnable signalRunnable = new Runnable() {
        public void run() {
            String tokensignal = AppPreferences.loadPreferences(mContext, VariablesConstant.SIGNAL_STRENGTH);
            if (!tokensignal.isEmpty())
                Log.i(TAG, "Refresh Signal :" + tokensignal);
            setsignal_strength(tokensignal);
            if (!stop){
                signalHandlerRefresh.postDelayed(signalRunnable,DELAY);
            }
        }
    };

    public void setsignal_strength(String signal_strength) {
     if (signal_strength.equals("1")){
//            iv_signal.setBackground(getResources().getDrawable(R.drawable.signal_1));
            iv_signal.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.signal_1));
        }else if (signal_strength.equals("2")){
//            iv_signal.setBackground(getResources().getDrawable(R.drawable.signal_2));
            iv_signal.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.signal_2));
        }else if (signal_strength.equals("3")){
//            iv_signal.setBackground(getResources().getDrawable(R.drawable.signal_3));
            iv_signal.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.signal_3));
        }else if (signal_strength.equals("4")){
//            iv_signal.setBackground(getResources().getDrawable(R.drawable.signal_4));
            iv_signal.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.signal_4));
        }else if (signal_strength.equals("5")){
//            iv_signal.setBackground(getResources().getDrawable(R.drawable.signal_5));
            iv_signal.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.signal_5));
        }else {
//         iv_signal.setBackground(getResources().getDrawable(R.drawable.signal_defalt));
         iv_signal.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.signal_defalt));
     }
    }


    @Override
    public void onStart() {
        super.onStart();
//        Log.w(TAG,"onStart");

        token = AppPreferences.loadPreferences(mContext,VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);

        Intent intentGetData = getIntent();
        if (intentGetData !=null){
            vehicle_id = intentGetData.getStringExtra(VariablesConstant.VEHICLE_ID);
            vehicle_name = intentGetData.getStringExtra(VariablesConstant.VEHICLE_NAME);
            vehicle_no = intentGetData.getStringExtra(VariablesConstant.VEHICLE_NUMBER);
            vehicle_type = intentGetData.getStringExtra(VariablesConstant.VEHICLE_TYPE);
            vehicle_color = intentGetData.getStringExtra(VariablesConstant.VEHICLE_COLOR);
            driver_number = intentGetData.getStringExtra(VariablesConstant.DRIVER_CONTACT_NUMBER);
            sos_number = intentGetData.getStringExtra(VariablesConstant.EMERGENCY_CONTACT_NUMBER);
            Log.e(TAG,"Intent-- vehicle_id: "+vehicle_id);
            Log.e(TAG,"Intent-- vehicle_name: "+vehicle_name);
            Log.e(TAG,"Intent-- vehicle_no: "+vehicle_no);
            Log.e(TAG,"Intent-- vehicle_type: "+vehicle_type);
            Log.e(TAG,"Intent-- vehicle_color: "+vehicle_color);
            Log.e(TAG,"Intent-- driver_number: "+driver_number);
            Log.e(TAG,"Intent-- emergency_number: "+sos_number);
        }else {
            vehicle_id =  AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_ID);
            vehicle_name = AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_NAME);
            vehicle_no = AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_NUMBER);
            vehicle_type = AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_TYPE);
            vehicle_color = AppPreferences.loadPreferences(mContext,VariablesConstant.VEHICLE_COLOR);
            driver_number = AppPreferences.loadPreferences(mContext,VariablesConstant.DRIVER_CONTACT_NUMBER);
            sos_number = AppPreferences.loadPreferences(mContext,VariablesConstant.EMERGENCY_CONTACT_NUMBER);

            Log.e(TAG,"modelVehicle-- vehicle_id: "+vehicle_id);
            Log.e(TAG,"modelVehicle-- vehicle_name: "+vehicle_name);
            Log.e(TAG,"modelVehicle-- vehicle_no: "+vehicle_no);
            Log.e(TAG,"modelVehicle-- vehicle_type: "+vehicle_type);
            Log.e(TAG,"modelVehicle-- driver_number: "+driver_number);
            Log.e(TAG,"modelVehicle-- emergency_number: "+sos_number);
        }

        if (vehicle_no !=null) {
//                tv_vehicleSpinner.setVisibility(View.VISIBLE);
            tv_vehicleSpinner.setText(vehicle_no);
            tv_title.setText(vehicle_no);
        }


        // TODO: 5/27/2020  WhiteIcon(vehicle_type)
        if (vehicle_type != null)
        iv_car.setBackgroundResource(mVehicleTypesAndColors.getWhiteIcon(vehicle_type));

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
//            getTicketCall(token);
            getTicketCall(userId);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

        /**
         * reFresh();
         * Call the HAndler
         */
        autoSignalHandlerRefresh();
        startGettingOnlineDataFromCar();
    }


    @Override
    public void onResume() {
        super.onResume();
//        Log.w(TAG,"onResume");
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.w(TAG,"onPause");
        mMapView.onPause();
//        stopRepeatingTask();
//        stopRepeatingTaskRefresh();
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.w(TAG,"onStop");

        // set null when activity destry - SIGNAL_STRENGTH
        AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, "0");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
//        Log.w(TAG,"onDestroy");
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_call_driver:
//                callByDialorMethod();
                callByDialorMethod(driver_number);
                break;

            case R.id.ll_sos:
                //sendSosMethod();
                sendSosMethod(driver_name,driver_number,sos_number);
                break;

            case R.id.ll_location_share:
              shareLocation();
//              shareLocationByIntent();
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

            case R.id.iv_notification:
                startActivity(new Intent(mContext, NotificationActivity.class));
                finish();
                break;

            case R.id.iv_refresh:
                Intent intent = new Intent(mContext, LiveTrackingActivity.class);
                intent.putExtra(VariablesConstant.VEHICLE_ID, vehicle_id);
                intent.putExtra(VariablesConstant.VEHICLE_NAME, vehicle_name);
                intent.putExtra(VariablesConstant.VEHICLE_NUMBER, vehicle_no);
                intent.putExtra(VariablesConstant.VEHICLE_TYPE, vehicle_type);
                intent.putExtra(VariablesConstant.VEHICLE_COLOR, vehicle_color);
                intent.putExtra(VariablesConstant.DRIVER_CONTACT_NUMBER, driver_number);
                intent.putExtra(VariablesConstant.EMERGENCY_CONTACT_NUMBER, sos_number);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                mContext.startActivity(intent);
                finish();
                break;

            case R.id.iv_tool_back_left:
                onBackPressed();
                break;

        }
    }



    private void shareLocation() {
//        Log.w(TAG,"shareLocation :"+sLatitude +", "+sLongitude);

        /**
         *  sLatitude = soccermomLatitude;
         *  sLongitude = soccermomLongitude;
         *  https://developers.google.com/maps/documentation/urls/android-intents
         *  https://stackoverflow.com/questions/42677389/android-how-to-pass-lat-long-route-info-to-google-maps-app
         *  https://stackoverflow.com/questions/12668551/share-location-with-share-intent-activity
         *  https://stackoverflow.com/questions/22704451/open-google-maps-through-intent-for-specific-location-in-android
         * https://www.wikihow.com/Get-Latitude-and-Longitude-from-Google-Maps
         *
         * https://www.google.com/maps/place/28%C2%B037'44.8%22N+77%C2%B016'39.1%22E
         * http://maps.google.com/maps?q=loc:28.629123333333332,77.27753
         * https://www.google.com/maps/place/28.629123333333332,77.27753
         */

        // Share via
//        if (!sLatitude.equals("") && !sLongitude.equals("")){
        if ( startLatitude != 0.0 &&  startLongitude !=0.0){
            String uriDirection = "http://maps.google.com/maps?saddr=" +sLatitude+","+sLongitude;
            // https://www.google.com/maps/?q=28.706805,77.034435
            String uri2 = "https://www.google.com/maps/?q=" +startLatitude+","+startLongitude;
            String uri = "http://maps.google.com/maps?q=loc:" +startLatitude+","+startLongitude;

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String ShareSub = vehicle_no+" vehicle location";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

//            Log.w(TAG,"shareLocation  ShareSub:"+ShareSub);
//            Log.w(TAG,"shareLocation  uri:"+uri);
        }

    }

    private void shareLocationByIntent() {
        Log.w(TAG,"shareLocation :"+sLatitude +", "+sLongitude);
        Log.i(TAG, "shareLocation startLatitude Latitude : " + startLatitude);
        Log.i(TAG, "shareLocation startLongitude Longitude : " + startLongitude);


        // Share via
//        if (!sLatitude.equals("") && !sLongitude.equals("")){
        if ( startLatitude != 0.0 &&  startLongitude !=0.0){
            // https://www.google.com/maps/?q=28.706805,77.034435
            String uri = "https://www.google.com/maps/?q=" +startLatitude+","+startLongitude;
           /* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String ShareSub = vehicle_no+" vehicle location";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);*/

//            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
//            Uri mapUri = Uri.parse("geo:0,0?q=lat,lng(label)");
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+startLatitude+","+startLongitude);
            Intent sharingIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            sharingIntent.setPackage("com.google.android.apps.maps");
//            startActivity(sharingIntent);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

            Log.w(TAG,"shareLocation  uri:"+uri);
        }

    }


    private void callByDialorMethod(String driverNumber) {
//        Log.w(TAG,"callByDialorMethod driver_number :"+driverNumber);
        // Use format with "tel:" and phone number to create phoneNumber.
        if (driverNumber!=null){
            Intent intent = new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse("tel:0123456789"));
            intent.setData(Uri.parse("tel:" + driverNumber));
            startActivity(intent);
        }else {
            Log.e(TAG,"Number not found ");
        }
    }


    private void  sendSosMethod(String driverName, String driverNumber, String sosNumber ) {
        Log.w(TAG,"sendSosMethod driverName :"+driverName);
        Log.w(TAG,"sendSosMethod driverNumber :"+driverNumber);
        Log.w(TAG,"sendSosMethod sosNumber :"+sosNumber);

        String SosMessage = driverName + " triggered the Emergency button in his/her Fast Track app. As "
                + driverName + " Emergency contact, we suggest you to call " + driverName + " now or the local police";
        String SosMessageByNumber = driverNumber + " triggered the Emergency button in his/her Fast Track app. As "
                + driverNumber + " Emergency contact, we suggest you to call " + driverNumber + " now or the local police";
//      sendSms(SosMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Send Sms");
        builder.setMessage("Are you sure, You want send sms");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                if (sosNumber !=null){

                }
                sendSMSByIntent(SosMessage,sosNumber);
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
//        Log.w(TAG, "sendSosMethod Sos Message :" + message);
//        Log.w(TAG, "sendSosMethod Sos phoneNumber  :" + phoneNumber);

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

        engine_status_msg = getString(R.string.do_you_want_change_engine_status);

        new FancyGifDialog.Builder(this)
                .setTitle(getString(R.string.engine_status))
                .setMessage(engine_status_msg)
                .setNegativeBtnBackground("#000000")
                .setNegativeBtnText(getString(R.string.engine_on_dialog))  // Cancel
                .setPositiveBtnBackground("#000000")
                .setPositiveBtnText(getString(R.string.engine_off_dialog))
                .setGifResource(R.drawable.engine_b)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        // ======= API Call ==========
                        if (CheckNetwork.isNetworkAvailable(mContext)) {
                            getEngineStatusCall(token,vehicle_id, "0");
                        } else {
                            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        // ======= API Call ==========
                        if (CheckNetwork.isNetworkAvailable(mContext)) {
                            getEngineStatusCall(token,vehicle_id, "1");
                        } else {
                            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();
    }

    private void parkingMethodDialog(){
        String car_parking_msg="";
        String getParking_status = AppPreferences.loadPreferences(mContext, VariablesConstant.PARKING_STATUS);
        String parking_status_bg = AppPreferences.loadPreferences(mContext, VariablesConstant.PARKING_STATUS_BG);
//        Log.e(TAG, "engineOnOffMethodDialog getEngine_status :" + getParking_status);
//        Log.e(TAG, "engineOnOffMethodDialog parking_status_bg :" + parking_status_bg);

        if (parking_status_bg.equalsIgnoreCase("1")){
            car_parking_msg = getString(R.string.do_you_want_car_not);
        }else if (parking_status_bg.equalsIgnoreCase("0")){
            car_parking_msg = getString(R.string.do_you_want_car);
        }
        new FancyGifDialog.Builder(this)
                .setTitle(getString(R.string.parking_the_car))
                .setMessage(car_parking_msg)
                .setNegativeBtnBackground("#000000")
                .setNegativeBtnText(getString(R.string.cancel))  // Cancel
                .setPositiveBtnBackground("#000000")
                .setPositiveBtnText(getString(R.string.ok))
//                .setGifResource(R.drawable.dash_car)   //Pass your Gif here
                .setGifResource(R.drawable.car_parked_b)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        // ======= API Call ==========
                        if (CheckNetwork.isNetworkAvailable(mContext)) {
                            getCarParkingCall(token,vehicle_id);
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

    private void findViewById() {
        toolbar = findViewById(R.id.toolbar);
        iv_signal = findViewById(R.id.iv_signal);
        iv_notification = findViewById(R.id.iv_notification);
        iv_refresh = findViewById(R.id.iv_refresh);
        iv_allMarkers = findViewById(R.id.iv_allMarkers);
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_title = findViewById(R.id.tv_title);


        Img_btn_fullmap = findViewById(R.id.Img_btn_fullmap);
        relativeLayoutView = findViewById(R.id.relativeLayoutView);
        vehicleSpinner =findViewById(R.id.vehicleSpinner);
        tv_vehicleSpinner =findViewById(R.id.tv_vehicleSpinner);
        ll_call_driver =findViewById(R.id.ll_call_driver);
        ll_all_activity =findViewById(R.id.ll_all_activity);
        ll_engine_on_off =findViewById(R.id.ll_engine_on_off);
        ll_car_parking =findViewById(R.id.ll_car_parking);
        ll_sos =findViewById(R.id.ll_sos);
        ll_location_share =findViewById(R.id.ll_location_share);
        tv_location =findViewById(R.id.tv_location);
        iv_carStatusColor =findViewById(R.id.iv_carStatusColor);
        tv_your_car_is_stopped =findViewById(R.id.tv_your_car_is_stopped);
        iv_engine_on_off =findViewById(R.id.iv_engine_on_off);
        rl_engine_on_off =findViewById(R.id.rl_engine_on_off);
        tv_engine_on_off =findViewById(R.id.tv_engine_on_off);
        iv_car_parking =findViewById(R.id.iv_car_parking);
        tv_car_parking =findViewById(R.id.tv_car_parking);
        iv_plus =findViewById(R.id.iv_plus);
        iv_minus =findViewById(R.id.iv_minus);
        tv_speed =findViewById(R.id.tv_speed);
        tv_ticket=findViewById(R.id.tv_ticket);
        iv_plus =findViewById(R.id.iv_plus);
        iv_minus = findViewById(R.id.iv_minus);
        bottom_layout=findViewById(R.id.bottom_layout);
        cv_location_address=findViewById(R.id.cv_location_address);
        ll_speed=findViewById(R.id.ll_speed);
        llFragmentHomeDashboardFullLayout=findViewById(R.id.llFragmentHomeDashboardFullLayout);
        car_Status_msg=findViewById(R.id.car_Status_msg);
        rl_iv_car=findViewById(R.id.rl_iv_car);
        iv_car=findViewById(R.id.iv_car);
    }



 //================ Api Call ===============================================================

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
                    if (response.isSuccessful() && responseCode ==200){
                        TickerResponse  tickerResponse = response.body();
                        int status = tickerResponse.getStatus();
                        if (status== 200) {
                            String ticker = tickerResponse.getTicker();
//                            Log.e(TAG, "ticker :"+ticker);
//                                Log.e(TAG, "ticker Message :"+ tickerResponse.getMessage());
//                                tv_ticket.setText(getString(R.string.token_message) + "  "+ ticker);

                            tv_ticket.setText(ticker);

                        } else {
                            Log.e(TAG, "Status Code :"+status);
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,"Massage"+e.toString());
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
        new CountDownTimer(2000, 1000){
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

    // engine_status
    private void getEngineStatusCall(String token, String vehicleId, String status) {
        String engine_status_bg = AppPreferences.loadPreferences(mContext, VariablesConstant.ENGINE_STATUS_BG);

        if (engine_status_bg.equalsIgnoreCase(status)){
            return;
        }
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<EngineStatusResponse> call = apiInterface.engineStatus(vehicleId,token);
        call.enqueue(new Callback<EngineStatusResponse>() {
            @Override
            public void onResponse(Call<EngineStatusResponse> call, Response<EngineStatusResponse> response) {
                if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getEngineStatusCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (response.isSuccessful() && responseCode ==200){
                        playSound2();
                        EngineStatusResponse engineStatusResponse = response.body();
                        assert engineStatusResponse != null;
                        int status = Integer.parseInt(engineStatusResponse.getStatus());
                        String message = engineStatusResponse.getMessage();
                        //0 -> Off 1->On
                        int engine_status = Integer.parseInt(engineStatusResponse.getEngineStatus());
                        AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS, String.valueOf(engine_status));
                        String getEngine_status = AppPreferences.loadPreferences(mContext, VariablesConstant.ENGINE_STATUS);
//                        Log.w(TAG, "getEngine_status :" + getEngine_status);

                        if (engine_status==1){
                            tv_engine_on_off.setText(getString(R.string.engine_on));
                            rl_engine_on_off.setBackgroundResource(R.drawable.shape_bg_white10);
                            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_b)); // R.drawable.engine
                        }else if (engine_status==0){
                            tv_engine_on_off.setText(getString(R.string.engine_off));
                            rl_engine_on_off.setBackgroundResource(R.drawable.shape_bg_red10);
                            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_w)); //  R.drawable.engine_redble.engine_red
                        }

                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,"getEngineStatus Massage :"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<EngineStatusResponse> call, Throwable t) {
                if (pd!=null) pd.dismiss();
                Log.e(TAG," onFailure"+t.toString());
            }
        });
    }

    // car_parking
    private void getCarParkingCall(String token, String vehicleId) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<CarParkingResponse> call = apiInterface.carParking(vehicleId,token);
        call.enqueue(new Callback<CarParkingResponse>() {
            @Override
            public void onResponse(Call<CarParkingResponse> call, Response<CarParkingResponse> response) {
                if (pd!=null) pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.w(TAG, "getCarParkingCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (response.isSuccessful() && responseCode ==200){

                        CarParkingResponse carParkingResponse = response.body();
                        assert carParkingResponse != null;
                        int status = carParkingResponse.getStatus();
                        if (status==200) {
                            /**
                             *  1->On , 0 -> Off
                             * PS : true = 1 and false = 0
                             */
                            playSound1();

                            String message = carParkingResponse.getMessage();
                            int parking_status = carParkingResponse.getParkingStatus();
                            AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS, String.valueOf(parking_status));

//                            Log.w(TAG,"parking_status :"+parking_status);
//                                Log.i(TAG,"message :"+message);
                         /*   if (parking_status ==0){
                                iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked));
                            }else {
                                iv_car_parking.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.car_parked_red));
                            }*/
                            if (parking_status ==1){
                                tv_car_parking.setText(getString(R.string.car_parking_on));
                                iv_car_parking.setBackground(ContextCompat.getDrawable(mContext, R.drawable.car_parked_b)); //car_parked_red
                            }else if (parking_status==0) {
                                tv_car_parking.setText(getString(R.string.car_parking_off));
                                iv_car_parking.setBackground(ContextCompat.getDrawable(mContext, R.drawable.car_parked2));
                            }
                        } else {
                            Log.e(TAG, "Status Code :"+status);
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,"getCarParking Massage :"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<CarParkingResponse> call, Throwable t) {
                if (pd!=null) pd.dismiss();
                Log.e(TAG," onFailure"+t.toString());
            }
        });
    }

    // car_details



//    private void getDriverLocationUpdate(String token, String vehicleId) {

    private void getDriverLocationUpdate3(String user_id, String vehicleId){
        //String URL1= http://3.135.158.46/api/show_location1?user_id=1102&vehicle_id=1246
        String URL1 = "http://3.135.158.46/api/show_location3?user_id=1102&vehicle_id=1249";

        try {
            // active the full map click button
            Img_btn_fullmap.setEnabled(true);

            String URL = "http://3.135.158.46/api/show_location3?user_id="+user_id+"&vehicle_id="+vehicleId;
            Log.e(TAG, "getDriverLocationUpdate URL :"+ URL);
            String GloxeyJsonParser_response = VilleyDriverLocation.CallVehicles(mContext,URL);
            Log.e(TAG, "GloxeyJsonParser_response :"+ GloxeyJsonParser_response);
            ShowLocationResponse locationResponse = GloxeyJsonParser.getInstance().parse(GloxeyJsonParser_response, ShowLocationResponse.class);
            int status = locationResponse.getStatus();
            if (status == 200) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // set mapView
                        mapView2.setVisibility(View.GONE);
                        relativeLayoutView.setVisibility(View.GONE);
                        mMapView.setVisibility(View.VISIBLE);
//                            publishProgress(status);
                        isFirstPositionSpinner = true;

                        bottom_layout.setVisibility(View.VISIBLE);
                        ll_speed.setVisibility(View.VISIBLE);
                        cv_location_address.setVisibility(View.VISIBLE);
                        llFragmentHomeDashboardFullLayout.setVisibility(View.VISIBLE);
//                                    car_Status_msg.setVisibility(View.VISIBLE);
//                                    tv_vehicleSpinner.setVisibility(View.VISIBLE);

                        List<MyLocation> locationResponseList = locationResponse.getLocation();
//

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for(int i=0;i<locationResponseList.size();i++)
                                {
                                    points.add(new LatLng(Double.parseDouble(locationResponseList.get(i).getLatitude()),Double.parseDouble(locationResponseList.get(i).getLongitude())));
                                }
                                if(points.size()>0)
                                    redrawLine();
                            }
                        },1500);

                        // "vehicle_type": "car",
//                                    vehicle_type  = locationResponse.getVehicleType();
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall vehicle_type : "+vehicle_type);

                        String colorCar = locationResponseList.get(0).getColor();                // "color": "red",
                        String car_positionMsg = locationResponseList.get(0).getPosition();          //"position": "Is Stopped since 8 min",
                        String position_color = locationResponseList.get(0).getPositionColor();  // "position_color": "red",
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall colorCar : "+colorCar);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall car_positionMsg : "+car_positionMsg);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall position_color : "+position_color);
                        setCarPositionMsg(car_positionMsg);
                        setCarPositionMsgColor(position_color);

                        String parking_status_bg = locationResponseList.get(0).getParkingStatus();  //"parking_status": "0",
                        String engine_status_bg = locationResponseList.get(0).getEngineStatus();    // "engine_status": "0"
                        String signal_strength = locationResponseList.get(0).getSignalStrength();    // "signal_strength": 5,
                        AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS_BG, parking_status_bg);
                        AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS_BG, engine_status_bg);
                        AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, signal_strength);

//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall parking_status_bg : " + parking_status_bg);
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall engine_status_bg : " + engine_status_bg);
//                                    Log.w(TAG, "JUNAID - Else getLocationShowCall signal_strength : " + signal_strength);
                        // TODO: 4/7/2020
                        if (parking_status_bg !=null || engine_status_bg !=null){
                            setCarParkingBg(parking_status_bg);
                            setEngineStatusBg(engine_status_bg);
                            parkingNotification(colorCar,parking_status_bg);
                        }

                        String soccermomLatitude = locationResponseList.get(0).getLatitude();
                        String soccermomLongitude = locationResponseList.get(0).getLongitude();
                        if (soccermomLatitude != null){
                            startLatitude = Double.valueOf(soccermomLatitude);
                            startLongitude = Double.valueOf(soccermomLongitude);
                        }
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall soccermom Latitude : " + soccermomLatitude);
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall soccermom Longitude : " + soccermomLongitude);

                        //String sLatitude="",sLongitude="";
                        sLatitude = soccermomLatitude;
                        sLongitude = soccermomLongitude;
                        AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_LATITUDE, soccermomLatitude);
                        AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_LONGITUDE, soccermomLongitude);

                        String speed = locationResponseList.get(0).getSpeed();
                        tv_speed.setText(speed);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall speed : "+speed);
                        // TODO: 1/1/2020

                        mAddress = mLocationAddress.getAddress(startLatitude, startLongitude);
                        tv_location.setText(locationResponseList.get(0).getAddress());
//                                        Log.w(TAG,"getAddress Address :"+mAddress);

                        if (isFirstPosition) {
                            startPosition = new LatLng(startLatitude, startLongitude);
                            startposi=startPosition;
                            Log.d(TAG, "JUNAID1 -"+startLatitude + "--" + startLongitude);
                            googleMap.addMarker(new MarkerOptions().position(startPosition).
                                    flat(true).icon(BitmapDescriptorFactory.defaultMarker()));// setCarColor(colorCar)
//                                Log.d(TAG, "JUNAID1: "+startLatitude + "--" + startLongitude);
//                                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).flat(true).icon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar))));
                            carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                                    flat(true).icon(BitmapDescriptorFactory.fromResource(
                                    mVehicleTypesAndColors.setCarColor(colorCar, vehicle_type))));
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
                            Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude
                                    + "--JUNAID2 -  Check --" + startPosition.longitude + "--" + endPosition.longitude);

                            if ((startPosition.latitude != endPosition.latitude) ||
                                    (startPosition.longitude != endPosition.longitude)) {
                                Log.e(TAG, "NOT SAME");
                                startBikeAnimation(startPosition, endPosition, colorCar, speed);
                            } else {
                                Log.i(TAG, "SAME");
                            }
                        }


                    }
                }); //end UIThread

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception Massage :" + e.toString());
        }

    }
    private void getDriverLocationUpdate2(String user_id, String vehicleId) {

        /**
         *  AndroidNetworking
         *  https://blog.mindorks.com/simple-and-fast-android-networking-19ed860d1455
         * https://github.com/amitshekhariitbhu/Fast-Android-Networking
         * http://3.135.158.46/api/show_location2?user_id=1102&vehicle_id=1246
         *  @POST(EndApi.UPDATE_LOCATION)
         *  GET http://3.135.158.46/api/show_location1?user_id=1102&vehicle_id=1249
         *     Call<ShowLocationResponse> updateLocation(@Query("token") String token,
         *                                    @Query("latitude") String latitude,
         *                                    @Query("longitude") String longitude,
         *                                    @Query("vehicle_id") String vehicle_id);
         */

        // Adding an Network Interceptor for Debugging purpose :
        AndroidNetworking.initialize(getApplicationContext());

        String url_api_method = BaseApi.BASE_URL+ "show_location1?";
        Log.w(TAG, "url_api_method: " + url_api_method);
//        AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
        AndroidNetworking.get( url_api_method)
                .addQueryParameter("user_id", user_id)
                .addQueryParameter("vehicle_id", vehicleId)
                .setTag(this)
                .setOkHttpClient(new OkHttpClient())
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsObject(ShowLocationResponse.class, new ParsedRequestListener<ShowLocationResponse>() {

                    @Override
                    public void onResponse(ShowLocationResponse locationResponse) {

                        // pd.dismiss();
                        String str_response = new Gson().toJson(locationResponse.toString());
                        Log.w(TAG, "getLocationShowCall Response >>>>" + str_response);

                        // active the full map click button
                        Img_btn_fullmap.setEnabled(true);

                        int status = locationResponse.getStatus();
                        if (status == 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    // set mapView
                                    mapView2.setVisibility(View.GONE);
                                    relativeLayoutView.setVisibility(View.GONE);
                                    mMapView.setVisibility(View.VISIBLE);
//                            publishProgress(status);
                                    isFirstPositionSpinner = true;

                                    bottom_layout.setVisibility(View.VISIBLE);
                                    ll_speed.setVisibility(View.VISIBLE);
                                    cv_location_address.setVisibility(View.VISIBLE);
                                    llFragmentHomeDashboardFullLayout.setVisibility(View.VISIBLE);
//                                    car_Status_msg.setVisibility(View.VISIBLE);
//                                    tv_vehicleSpinner.setVisibility(View.VISIBLE);

                                    List<MyLocation> locationResponseList = locationResponse.getLocation();
//
                                    // "vehicle_type": "car",
//                                    vehicle_type  = locationResponse.getVehicleType();
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall vehicle_type : "+vehicle_type);

                                    String colorCar = locationResponseList.get(0).getColor();                // "color": "red",
                                    String car_positionMsg = locationResponseList.get(0).getPosition();          //"position": "Is Stopped since 8 min",
                                    String position_color = locationResponseList.get(0).getPositionColor();  // "position_color": "red",
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall colorCar : "+colorCar);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall car_positionMsg : "+car_positionMsg);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall position_color : "+position_color);
                                    setCarPositionMsg(car_positionMsg);
                                    setCarPositionMsgColor(position_color);

                                    String parking_status_bg = locationResponseList.get(0).getParkingStatus();  //"parking_status": "0",
                                    String engine_status_bg = locationResponseList.get(0).getEngineStatus();    // "engine_status": "0"
                                    String signal_strength = locationResponseList.get(0).getSignalStrength();    // "signal_strength": 5,
                                    AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS_BG, parking_status_bg);
                                    AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS_BG, engine_status_bg);
                                    AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, signal_strength);

//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall parking_status_bg : " + parking_status_bg);
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall engine_status_bg : " + engine_status_bg);
//                                    Log.w(TAG, "JUNAID - Else getLocationShowCall signal_strength : " + signal_strength);
                                    // TODO: 4/7/2020
                                    if (parking_status_bg !=null || engine_status_bg !=null){
                                        setCarParkingBg(parking_status_bg);
                                        setEngineStatusBg(engine_status_bg);
                                        parkingNotification(colorCar,parking_status_bg);
                                    }

                                    String soccermomLatitude = locationResponseList.get(0).getLatitude();
                                    String soccermomLongitude = locationResponseList.get(0).getLongitude();
                                    if (soccermomLatitude != null){
                                        startLatitude = Double.valueOf(soccermomLatitude);
                                        startLongitude = Double.valueOf(soccermomLongitude);
                                    }
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall soccermom Latitude : " + soccermomLatitude);
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall soccermom Longitude : " + soccermomLongitude);

                                    //String sLatitude="",sLongitude="";
                                    sLatitude = soccermomLatitude;
                                    sLongitude = soccermomLongitude;
                                    AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_LATITUDE, soccermomLatitude);
                                    AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_LONGITUDE, soccermomLongitude);

                                    String speed = locationResponseList.get(0).getSpeed();
                                    tv_speed.setText(speed);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall speed : "+speed);
                                    // TODO: 1/1/2020
                                    mAddress = mLocationAddress.getAddress(startLatitude, startLongitude);
                                    tv_location.setText(mAddress);
//                                        Log.w(TAG,"getAddress Address :"+mAddress);

                                    if (isFirstPosition) {
                                        googleMap.clear();

                                        startPosition = new LatLng(startLatitude, startLongitude);
                                        Log.d(TAG, "JUNAID1 -"+startLatitude + "--" + startLongitude);

//                                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).flat(true).icon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar))));
                                        carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                                                flat(true).icon(BitmapDescriptorFactory.fromResource(
                                                mVehicleTypesAndColors.setCarColor(colorCar, vehicle_type))));  // setCarColor(colorCar)
                                        carMarker.setAnchor(0.5f, 0.5f);
//                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
//                                                .target(startPosition)
//                                                .zoom(defaultZoom)
//                                                .build()));

                                        isFirstPosition = false;

                                    } else {
                                        endPosition = new LatLng(startLatitude, startLongitude);
                                        Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude
                                                + "--JUNAID2 -  Check --" + startPosition.longitude + "--" + endPosition.longitude);

                                        if ((startPosition.latitude != endPosition.latitude) ||
                                                (startPosition.longitude != endPosition.longitude)) {
                                            Log.e(TAG, "NOT SAME");
                                            startBikeAnimation(startPosition, endPosition, colorCar, speed);
                                        } else {
                                            Log.i(TAG, "SAME");
                                        }
                                    }


                                }
                            }); //end UIThread

                        }

                    }

                    @Override
                    public void onError(ANError errorResponce) {
                        Log.e(TAG, "errorResponce: "+errorResponce.getErrorCode());
                        Log.e(TAG, errorResponce.toString());

                        // error case  responseCode
                        if (errorResponce.getErrorCode()==401){
                            String user_id = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);
                            homeActivity.logOutApiCall(user_id,TAG);
                        }
                        ErrorUtils.apiResponseErrorHandle(TAG,errorResponce.getErrorCode(), mContext);

                    }
                });




    }


    private void getDriverLocationUpdate(String user_id, String vehicleId) {
//        Log.e(TAG, "getDriverLocationUpdate >>> userId :" + userId +", vehicleId :" + vehicleId);

//        Call<ShowLocationResponse> call = apiInterface.showLocation(token,vehicleId);
        Call<ShowLocationResponse> call = apiInterface.showLocation1(user_id,vehicleId);
        call.enqueue(new Callback<ShowLocationResponse>() {
            @Override
            public void onResponse(Call<ShowLocationResponse> call, Response<ShowLocationResponse> response) {
                // pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.w(TAG, "getLocationShowCall Response >>>>" + str_response);
//                Log.w(TAG, "response.toString >>>>" + response.toString());
                int responseCode = response.code();
//                Log.w(TAG, "Junaid responseCode :" + responseCode);
                try {
                    if (response.isSuccessful()) {
                        // active the full map click button
                        Img_btn_fullmap.setEnabled(true);

//                        int  responseCode  = response.code();
                        ShowLocationResponse locationResponse = response.body();
                        int status = locationResponse.getStatus();
                        if (status == 200) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    // set mapView
                                    mapView2.setVisibility(View.GONE);
                                    relativeLayoutView.setVisibility(View.GONE);
                                    mMapView.setVisibility(View.VISIBLE);
//                            publishProgress(status);
                                    isFirstPositionSpinner = true;

                                    bottom_layout.setVisibility(View.VISIBLE);
                                    ll_speed.setVisibility(View.VISIBLE);
                                    cv_location_address.setVisibility(View.VISIBLE);
                                    llFragmentHomeDashboardFullLayout.setVisibility(View.VISIBLE);
//                                    car_Status_msg.setVisibility(View.VISIBLE);
//                                    tv_vehicleSpinner.setVisibility(View.VISIBLE);

                                    List<MyLocation> locationResponseList = locationResponse.getLocation();
//
                                    // "vehicle_type": "car",
//                                    vehicle_type  = locationResponse.getVehicleType();
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall vehicle_type : "+vehicle_type);

                                    String colorCar = locationResponseList.get(0).getColor();                // "color": "red",
                                    String car_positionMsg = locationResponseList.get(0).getPosition();          //"position": "Is Stopped since 8 min",
                                    String position_color = locationResponseList.get(0).getPositionColor();  // "position_color": "red",
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall colorCar : "+colorCar);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall car_positionMsg : "+car_positionMsg);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall position_color : "+position_color);
                                    setCarPositionMsg(car_positionMsg);
                                    setCarPositionMsgColor(position_color);

                                    String parking_status_bg = locationResponseList.get(0).getParkingStatus();  //"parking_status": "0",
                                    String engine_status_bg = locationResponseList.get(0).getEngineStatus();    // "engine_status": "0"
                                    String signal_strength = locationResponseList.get(0).getSignalStrength();    // "signal_strength": 5,
                                    AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS_BG, parking_status_bg);
                                    AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS_BG, engine_status_bg);
                                    AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, signal_strength);

//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall parking_status_bg : " + parking_status_bg);
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall engine_status_bg : " + engine_status_bg);
//                                    Log.w(TAG, "JUNAID - Else getLocationShowCall signal_strength : " + signal_strength);
                                    // TODO: 4/7/2020
                                    if (parking_status_bg !=null || engine_status_bg !=null){
                                        setCarParkingBg(parking_status_bg);
                                        setEngineStatusBg(engine_status_bg);
                                        parkingNotification(colorCar,parking_status_bg);
                                    }

                                    String soccermomLatitude = locationResponseList.get(0).getLatitude();
                                    String soccermomLongitude = locationResponseList.get(0).getLongitude();
                                    if (soccermomLatitude != null){
                                        startLatitude = Double.valueOf(soccermomLatitude);
                                        startLongitude = Double.valueOf(soccermomLongitude);
                                    }
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall soccermom Latitude : " + soccermomLatitude);
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall soccermom Longitude : " + soccermomLongitude);

                                    //String sLatitude="",sLongitude="";
                                    sLatitude = soccermomLatitude;
                                    sLongitude = soccermomLongitude;
                                    AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_LATITUDE, soccermomLatitude);
                                    AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_LONGITUDE, soccermomLongitude);

                                    String speed = locationResponseList.get(0).getSpeed();
                                    tv_speed.setText(speed);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall speed : "+speed);
                                    // TODO: 1/1/2020
                                    mAddress = mLocationAddress.getAddress(startLatitude, startLongitude);
                                    tv_location.setText(mAddress);
//                                        Log.w(TAG,"getAddress Address :"+mAddress);

                                    if (isFirstPosition) {
                                        startPosition = new LatLng(startLatitude, startLongitude);
                                        startposi=startPosition;
                                        Log.d(TAG, "JUNAID1 -"+startLatitude + "--" + startLongitude);
                                        googleMap.addMarker(new MarkerOptions().position(startPosition).
                                                flat(true).icon(BitmapDescriptorFactory.defaultMarker()));// setCarColor(colorCar)
//                                Log.d(TAG, "JUNAID1: "+startLatitude + "--" + startLongitude);
//                                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).flat(true).icon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar))));
                                        carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                                                flat(true).icon(BitmapDescriptorFactory.fromResource(
                                                mVehicleTypesAndColors.setCarColor(colorCar, vehicle_type))));
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
                                        Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude
                                                + "--JUNAID2 -  Check --" + startPosition.longitude + "--" + endPosition.longitude);

                                        if ((startPosition.latitude != endPosition.latitude) ||
                                                (startPosition.longitude != endPosition.longitude)) {
                                            Log.e(TAG, "NOT SAME");
                                            startBikeAnimation(startPosition, endPosition, colorCar, speed);
                                        } else {
                                            Log.i(TAG, "SAME");
                                        }
                                    }


                                }
                            }); //end UIThread

                        } else {
                            Log.e(TAG, "Response code :" + status);
                            if (status==401){
                                String user_id = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);
                                homeActivity.logOutApiCall(user_id,TAG);
                            }
                        }
                    } else {
                        // error case  responseCode
                        if (responseCode==401){
                            String user_id = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);
                            homeActivity.logOutApiCall(user_id,TAG);
                        }
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception Massage :" + e.toString());
                }

            }
            @Override
            public void onFailure(Call<ShowLocationResponse> call, Throwable t) {
                Log.e(TAG,"onFailure Massage: "+t.toString());
            }
        });

    }

    private void parkingNotification(String colorCar, String parkingStatus) {
        if (colorCar.equalsIgnoreCase("green") && parkingStatus.equalsIgnoreCase("1") ){
            // playSound
//            playSound1();
            Uri  alarmSound = Uri.parse(AppPreferences.loadPreferences(this, VariablesConstant.NOTIFICATION_URI_RINGTONE));

            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Log.i(TAG, "alarmSound : " + alarmSound);

           // Notification
            Intent intent = new Intent(Config.PUSH_NOTIFICATION);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            String message = "Vehicle is moving while parking is on!.";
            String title = "Parking is on!.";

            Spanned styledText = Html.fromHtml(message);
            String channelId = getString(R.string.default_notification_channel_id);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(styledText))
                            .setAutoCancel(true)
                            .setLights(Color.RED, 3000, 3000)
                            .setSound(alarmSound)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(NotificationID.getID() , notificationBuilder.build());
        }
    }


    private void setCarPositionMsgColor(String position_color) {
        if (!position_color.equalsIgnoreCase("")) {
            if (position_color.equalsIgnoreCase("green")) {
                iv_carStatusColor.setImageResource(R.drawable.point_green);
                rl_iv_car.setBackgroundResource(R.drawable.ic_circle_green);

            } else if (position_color.equalsIgnoreCase("yellow")) {
                iv_carStatusColor.setImageResource(R.drawable.point_yellow);
                rl_iv_car.setBackgroundResource(R.drawable.ic_circle_yellow);

            } else if (position_color.equalsIgnoreCase("red")) {
                iv_carStatusColor.setImageResource(R.drawable.point_red);
                rl_iv_car.setBackgroundResource(R.drawable.ic_circle_red);

            }else if (position_color.equalsIgnoreCase("blue")) {
                iv_carStatusColor.setImageResource(R.drawable.point_blue);
                rl_iv_car.setBackgroundResource(R.drawable.ic_circle_blue);
            }
        }else{
            Log.e(TAG, "position_color  is empty");
        }
    }

    private void setCarPositionMsg(String car_position) {
        if (car_position.isEmpty()){
            tv_your_car_is_stopped.setText(getString(R.string.your_car_is_stopped));
        }else {
            String vehicleString = getString(R.string.vehicleNumber)+" "+vehicle_no+" "+car_position+" (hh:mm:ss)";
            tv_your_car_is_stopped.setText(vehicleString);
        }
    }

    /**
     * 0 -> Off 1->On
     * PS : true = 1 and false = 0
     */
    // engine_status_bg
    private void setEngineStatusBg(String engine_status_bg) {
        int my_getEngine_status = Integer.parseInt(engine_status_bg);
     /*   if (my_getEngine_status ==0){
            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_g));
        }else {
            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_b));
        }*/
        if (my_getEngine_status==0){
            tv_engine_on_off.setText(getString(R.string.engine_on));
            rl_engine_on_off.setBackgroundResource(R.drawable.shape_bg_white10);
            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_b)); // R.drawable.engine
        }else if (my_getEngine_status==1){
            tv_engine_on_off.setText(getString(R.string.engine_off));
            rl_engine_on_off.setBackgroundResource(R.drawable.shape_bg_red10);
            iv_engine_on_off.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.engine_w)); //  R.drawable.engine_red
        }
    }

    //parking_status_bg
    private void setCarParkingBg(String parking_status_bg) {
        int my_getParking_status = Integer.parseInt(parking_status_bg);
        if (my_getParking_status ==1){
            tv_car_parking.setText(getString(R.string.car_parking_on));
            iv_car_parking.setBackground(ContextCompat.getDrawable(mContext, R.drawable.car_parked_b)); //car_parked_red
        }else if (my_getParking_status==0) {
            tv_car_parking.setText(getString(R.string.car_parking_off));
            iv_car_parking.setBackground(ContextCompat.getDrawable(mContext, R.drawable.car_parked2));
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        LatLng latLng = new LatLng(28.5503,77.2502);
        //  googleMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(BitmapDescriptorFactory.fromResource(getBlueIcon(colorCar))));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));
        buildGoogleApiClient();//Init Google API Client

        // TODO: 1/4/2020
        startGettingOnlineDataFromCar();
//        dynamicPolyLine();


        // Zoom in, animating the camera. - iv_plus,iv_minus
        iv_plus.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());
            String zoomIn = CameraUpdateFactory.zoomIn().toString();
//                Log.i(TAG,"googleMap zoomIn : "+zoomIn);
        });

        iv_minus.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.zoomOut());
            String zoomOut = CameraUpdateFactory.zoomOut().toString();
//                Log.i(TAG,"googleMap zoomIn: "+zoomOut);
        });


        // LocationUpdate
//        getDriverLocationUpdate(token,vehicle_id);
        // TODO: 2/26/2020
//        getDriverLocationUpdate(userId,vehicle_id);
//        getDriverLocationUpdate2(userId,vehicle_id);
        getDriverLocationUpdate3(userId,vehicle_id);

    }


    private void  redrawLine(){
        if(line!=null)
        line.remove();
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for(int i=0; i<points.size(); i++){
            LatLng point = points.get(i);
            options.add(point);
            options.color(Color.BLACK);
            options.width(7);
        }

        line = googleMap.addPolyline(options);


    }

    // CreatePolyLineOnly
    void dynamicPolyLine() {
        googleMap.clear();
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                polyLineList = MapUtils.decodePoly(polyLine);

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



    private void autoSignalHandlerRefresh() {
        handlerLocation = new Handler();
//        mStatusCheckerLocation.run();

        signalHandlerRefresh = new Handler();
//        signalRunnable.run();
    }



    // stop the mStatusCheckerLocation
    void stopRepeatingTaskRefresh() {
        // stop the Handler
//        Log.i(TAG, "Handler - stopRepeatingTaskRefresh");
        handlerLocation.removeCallbacks(mStatusCheckerLocation);

//        handlerLocation.removeCallbacksAndMessages(null);
//        handlerLocation.removeMessages(0);

        signalHandlerRefresh.removeCallbacks(signalRunnable);
//        signalHandlerRefresh.removeMessages(0);
//        signalHandlerRefresh.removeCallbacksAndMessages(null);
    }

    // TODO: 3/5/2020
    void startGettingOnlineDataFromCar() {
        handlerLocation.post(mStatusCheckerLocation);
        signalHandlerRefresh.post(signalRunnable);

    }


    public Runnable mStatusCheckerLocation = new Runnable() {
        @Override
        public void run() {
            try {
                Log.i(TAG, "mStatusCheckerLocation Refresh");
//                getDriverLocationUpdate(token,vehicle_id);
                // TODO: 2/26/2020
//                getDriverLocationUpdate(userId,vehicle_id);
//                getDriverLocationUpdate2(userId,vehicle_id);
                getDriverLocationUpdate3(userId,vehicle_id);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            if (!stop){
                handlerLocation.postDelayed(mStatusCheckerLocation, DELAY);
            }
        }
    };


    // startPosition, endPosition
    float lastHead = 0;
    private void startBikeAnimation(final LatLng start, final LatLng end, String vColor, String mSpeed) {
//        Log.i(TAG, "startBikeAnimation called...");

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new LatLngInterpolator.LatLngEvaluator(), start, end);
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
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
                if(carMarker.getPosition().latitude!=newPos.latitude&&carMarker.getPosition().longitude!=newPos.longitude) {
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f, 0.5f);
                }
                //carMarker.setRotation(getBearing(start, end));

//                carMarker.setIcon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar)));
                carMarker.setIcon(BitmapDescriptorFactory.fromResource(
                        mVehicleTypesAndColors.setCarColor(vColor, vehicle_type)));
                // todo : Junaid > i can delay here
//                googleMap.moveCamera(CameraUpdateFactory
//                        .newCameraPosition
//                                (new CameraPosition.Builder()
//                                        .target(newPos)
//                                        .zoom(defaultZoom)
//                                        .build()));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                builder.include(startposi);
                startPosition = carMarker.getPosition();


                builder.include(newPos);
                LatLngBounds bounds = builder.build();
                // 150=offset from edges of the map in pixels
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                googleMap.animateCamera(cameraUpdate);
            }
        });
        valueAnimator.start();


        // TODO: 3/29/2020
        //getBearing - SphericalUtil.computeHeading
        int speed = Integer.parseInt(mSpeed);
        if (vColor.equalsIgnoreCase("green") && speed > 0){
            final float bearingTo = (float) SphericalUtil.computeHeading(start, end);
            ValueAnimator bearingAnimator = ValueAnimator.ofFloat(lastHead, bearingTo);
            bearingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            bearingAnimator.setDuration(10);
            bearingAnimator.addUpdateListener(valueAnimator2 -> {
                float value = (float) valueAnimator2.getAnimatedValue();
                carMarker.setRotation(value);
            });
            bearingAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    lastHead = bearingTo;
                }
            });
            bearingAnimator.start();
        }


    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // TODO: 3/29/2020
        gpsENABLE();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("onActivityResult()", Integer.toString(resultCode));

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
            mGoogleApiClient = new GoogleApiClient.Builder(this)
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
                                status.startResolutionForResult(LiveTrackingActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialr.LAYOUTog.
                            break;
                    }
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.e(TAG,"Stop Handler");
        // stop the Handler
        stop = true;
        stopRepeatingTaskRefresh();

        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
//        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_ID,"");
//        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_NUMBER,"");
//        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_TYPE, "");
//        AppPreferences.savePreferences(mContext,VariablesConstant.VEHICLE_COLOR, "");

    }

}