package com.backstagesupporters.fasttrack.ui.activity;

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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.MyLocation;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.notification.Config;
import com.backstagesupporters.fasttrack.notification.NotificationID;
import com.backstagesupporters.fasttrack.responseClass.CarParkingResponse;
import com.backstagesupporters.fasttrack.responseClass.EngineStatusResponse;
import com.backstagesupporters.fasttrack.responseClass.ShowLocationResponse;
import com.backstagesupporters.fasttrack.responseClass.TickerResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.myMap.LatLngInterpolator;
import com.backstagesupporters.fasttrack.ui.myMap.MyHomeMapActivity;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

import static com.backstagesupporters.fasttrack.ui.myMap.MapUtils.getBearing;
import static com.google.android.gms.maps.model.JointType.ROUND;

public class LiveTrackingAllActivity extends BaseActivity implements  OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private Toolbar toolbar;
    private ImageView  iv_tool_back_left;
    public  TextView tv_title;
    private Marker previousMarker = null;

    private float defaultZoom=15.0f;
    private MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int REQUEST_LOCATION = 199;

    private TextView tv_location;
    private String  mAddress;
    Polyline line; //added
    private ArrayList<LatLng> points; //added

    private ImageView iv_plus,iv_minus;
    private TextView tv_speed,tv_ticket,tv_MarkerInfo;
    private RelativeLayout ll_ticket;
    boolean iMarkerInfo =false;
    private String myStringGoogleMap;

    private String vehicle_id, vehicle_name,vehicle_type,vehicle_no,vehicle_color;
    private String userId ="";

    private static final long DELAY = 60*1000;
    private static final long ANIMATION_TIME_PER_ROUTE = 3000;
    private PolylineOptions polylineOptions;
    private Polyline greyPolyLine;
    private Handler handlerLocation;
    private Marker carMarker;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
    private List<LatLng> polyLineList = new ArrayList<>();
    private double lat, lng;

    // Give your Server URL here >> where you get car location update
    private boolean isFirstPosition = true;

    private Double startLatitude=0.0;
    private Double startLongitude=0.0;
    String sLatitude="",sLongitude="";

    //=Interface Declaration
    private ProgressDialog pd;
    private ApiInterface apiInterface;
    MyLocationAddress mLocationAddress;
    VehicleTypesAndColors mVehicleTypesAndColors;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_track_all);
        mContext = LiveTrackingAllActivity.this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        Intent intentGetData = getIntent();
        if (intentGetData !=null){
            vehicle_id = intentGetData.getStringExtra(VariablesConstant.VEHICLE_ID);
            vehicle_name = intentGetData.getStringExtra(VariablesConstant.VEHICLE_NAME);
            Log.e(TAG,"Intent-- vehicle_id: "+vehicle_id);
            Log.e(TAG,"Intent-- vehicle_name: "+vehicle_name);
        }


        findViewById();

        tv_title.setText(vehicle_name);
        iv_tool_back_left.setBackgroundResource(R.drawable.icon_back_blue);
        iv_tool_back_left.setOnClickListener(v -> {
            onBackPressed();
        });
        LinearLayout h1 = findViewById(R.id.h1);
        h1.setOnClickListener(v -> {
            onBackPressed();
        });

       /* toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));

        iv_signal.setVisibility(View.GONE);
        iv_refresh.setVisibility(View.GONE);
        iv_notification.setVisibility(View.GONE);
        iv_allMarkers.setVisibility(View.GONE);

        iv_refresh.setOnClickListener(this);
        iv_notification.setOnClickListener(this);*/

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
        mLocationAddress = new MyLocationAddress(mContext);
        mVehicleTypesAndColors = new VehicleTypesAndColors(mContext);

    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.w(TAG,"onStart");
        String token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);

        FloatingActionButton fabMarkerInfo = findViewById(R.id.fabMarkerInfo);
        ll_ticket.setVisibility(View.GONE);
        tv_MarkerInfo.setVisibility(View.GONE);

        tv_MarkerInfo.setOnClickListener(v -> {
            tv_MarkerInfo.setVisibility(View.GONE);
            fabMarkerInfo.setVisibility(View.VISIBLE);
        });

        fabMarkerInfo.setOnClickListener(view -> {
            tv_MarkerInfo.setVisibility(View.VISIBLE);
            fabMarkerInfo.setVisibility(View.GONE);

       /*Snackbar.make(view, myStringGoogleMap, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
//            if (iMarkerInfo){
//                tv_MarkerInfo.setVisibility(View.VISIBLE);
//                iMarkerInfo =false;
//            }else {
//                iMarkerInfo =true;
//                tv_MarkerInfo.setVisibility(View.INVISIBLE);
//            }
        });

        // ======= API Call ==========
//        if (CheckNetwork.isNetworkAvailable(mContext)) {
////            getTicketCall(token);
//            getTicketCall(userId);
//        } else {
//            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
//        }

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
//        Log.w(TAG,"onDestroy");
//       AppPreferences.loadPreferences(mContext, "");

        // stop the Handler
        stopRepeatingTaskRefresh();

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }



    private void findViewById() {
        toolbar = findViewById(R.id.toolbar);
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_title = findViewById(R.id.tv_tool_title);
        tv_location =findViewById(R.id.tv_location);
        iv_plus =findViewById(R.id.iv_plus);
        iv_minus =findViewById(R.id.iv_minus);
        tv_speed =findViewById(R.id.tv_speed);
        iv_plus =findViewById(R.id.iv_plus);
        tv_ticket=findViewById(R.id.tv_ticket);
        ll_ticket=findViewById(R.id.ll_ticket);
        tv_MarkerInfo=findViewById(R.id.tv_MarkerInfo);

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


//    private void getDriverLocationUpdate(String token, String vehicleId) {
    private void getDriverLocationUpdate(String user_id, String vehicleId) {
//        Log.e(TAG, "getDriverLocationUpdate >>> userId :" + userId +", vehicleId :" + vehicleId);

//        Call<ShowLocationResponse> call = apiInterface.showLocation(token,vehicleId);
        Call<ShowLocationResponse> call = apiInterface.showLocation1(user_id,vehicleId);
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
                        ShowLocationResponse locationResponse = response.body();
                        int status = locationResponse.getStatus();

                        if (status == 200) {
//                            publishProgress(status);

                            List<MyLocation> locationResponseList = locationResponse.getLocation();
//                            Log.w(TAG,"Response Count"+locationResponseList.size());
                            int vId = Integer.parseInt(vehicleId);

                            //Checking the Size Of Array Returned from server
                            if( vId ==0 && locationResponseList.size() > 0 )
                            {
                                //Initially Clearing the map
                                googleMap.clear();

                                //Initializing the Loop
                                for(int i=0; i<locationResponseList.size(); i++){
                                    MyLocation myLocation=locationResponseList.get(i);
//                                    Log.e(TAG,"Response  Count i: "+i);

                                    double tempLat = Double.parseDouble(myLocation.getLatitude());
                                    double tempLong = Double.parseDouble(myLocation.getLongitude());
                                    Log.w("If Current Lat long",""+tempLat+" lng "+tempLong);
                                    LatLng getCurrentLatLng = new LatLng(tempLat,tempLong);

                                    String car_position = myLocation.getPosition();          // "position": "Is Parked",- car position
                                    String position_color = myLocation.getPositionColor();   // pointer color
                                    String colorVehicle = myLocation.getColor();                // car color
                                    String vehicleType = myLocation.getType();
                                    Log.i(TAG, "JUNAID - car_position : " + car_position);
                                    Log.i(TAG, "JUNAID - position_color : " + position_color);
                                    Log.i(TAG,"JUNAID - colorVehicle: "+colorVehicle);
                                    Log.i(TAG,"JUNAID - vehicleType: "+vehicleType);

                                    String speed = myLocation.getSpeed();
                                    Log.e(TAG,"JUNAID - speed : "+speed);
                                    // tv_speed.setText(speed);

//                                    carMarker = googleMap.addMarker(new MarkerOptions().position(getCurrentLatLng).flat(true).icon(BitmapDescriptorFactory.fromResource(getMarkerColor(myLocation.getColor()))));

//                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(tempLat, tempLong)).title("Dalton Hall");
//                                    googleMap.addMarker(marker);

                                    carMarker = googleMap.addMarker(new MarkerOptions()
                                            .position(getCurrentLatLng).flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(mVehicleTypesAndColors.setCarColor(colorVehicle, vehicleType))));
                                    carMarker.setAnchor(0.5f, 0.5f);



                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng,5.0f));

//                                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
//                                            .target(getCurrentLatLng)
//                                            .zoom(defaultZoom)
//                                            .build()));

//                                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                                        @Override
//                                        public void onInfoWindowClick(Marker marker) {
//                                            //get locAddress
//                                            String Address = mLocationAddress.getAddress(marker.getPosition().latitude, marker.getPosition().longitude);
//
//                                            myStringGoogleMap = "Status : "+car_position+"\n"
//                                                    +"Vehicle Number: "+""+"\n"
//                                                    +"Add: "+Address;
//                                            Toast.makeText(mContext, myStringGoogleMap, Toast.LENGTH_SHORT).show();
//                                        }
//                                    });

//                                    googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//                                        @Override
//                                        public void onMapLongClick (LatLng latLng){

//                                        }
//                                    });

                                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            //get locAddress
                                            String Address = mLocationAddress.getAddress(marker.getPosition().latitude, marker.getPosition().longitude);
                                            Log.e(TAG,"JUNAID - getAddress Address :"+marker.getPosition().latitude+", "+ marker.getPosition().longitude);
                                            Log.e(TAG,"JUNAID - getAddress Address :"+Address);

                                            myStringGoogleMap = "Status : "+car_position+"\n"
                                                    +"Vehicle Number: "+""+"\n"
                                                    +"Add: "+Address;
                                            tv_MarkerInfo.setText(myStringGoogleMap);

//                                            Toast.makeText(mContext, myStringGoogleMap, Toast.LENGTH_SHORT).show();

                                            return true;
                                        }
                                    });


                                }
                            }
                            else
                                {
                                    // "vehicle_type": "car",
//                                    vehicle_type  = locationResponse.getVehicleType();
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall vehicle_type : "+vehicle_type);

                                    String colorCar = locationResponseList.get(0).getColor();                // "color": "red",
                                    String car_positionMsg = locationResponseList.get(0).getPosition();          //"position": "Is Stopped since 8 min",
                                    String position_color = locationResponseList.get(0).getPositionColor();  // "position_color": "red",
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall colorCar : "+colorCar);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall car_positionMsg : "+car_positionMsg);
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall position_color : "+position_color);
//                                    setCarPositionMsg(car_positionMsg);
//                                    setCarPositionMsgColor(position_color);

                                    String parking_status_bg = locationResponseList.get(0).getParkingStatus();  //"parking_status": "0",
                                    String engine_status_bg = locationResponseList.get(0).getEngineStatus();    // "engine_status": "0"
                                    String signal_strength = locationResponseList.get(0).getSignalStrength();    // "signal_strength": 5,
                                    AppPreferences.savePreferences(mContext, VariablesConstant.PARKING_STATUS_BG, parking_status_bg);
                                    AppPreferences.savePreferences(mContext, VariablesConstant.ENGINE_STATUS_BG, engine_status_bg);
                                    AppPreferences.savePreferences(mContext, VariablesConstant.SIGNAL_STRENGTH, signal_strength);

                                    String soccermomLatitude = locationResponseList.get(0).getLatitude();
                                    String soccermomLongitude = locationResponseList.get(0).getLongitude();
                                    if (soccermomLatitude != null){
                                        startLatitude = Double.valueOf(soccermomLatitude);
                                        startLongitude = Double.valueOf(soccermomLongitude);
                                    }
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall soccermomLatitude : " + soccermomLatitude);
//                                    Log.i(TAG, "JUNAID - Else getLocationShowCall soccermomLongitude : " + soccermomLongitude);

                                    //String sLatitude="",sLongitude="";
                                    sLatitude = soccermomLatitude;
                                    sLongitude = soccermomLongitude;
//                                    AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_LATITUDE, soccermomLatitude);
//                                    AppPreferences.savePreferences(mContext, VariablesConstant.DEFAULT_LONGITUDE, soccermomLongitude);

                                    String speed = locationResponseList.get(0).getSpeed();
//                                    Log.i(TAG,"JUNAID - Else getLocationShowCall speed : "+speed);

                                    tv_speed.setText(speed);
                                    mAddress = mLocationAddress.getAddress(startLatitude, startLongitude);
//                                        Log.w(TAG,"getAddress Address :"+mAddress);
                                    tv_location.setText(mAddress);

                                    if (isFirstPosition) {
                                        googleMap.clear();

                                        startPosition = new LatLng(startLatitude, startLongitude);
//                                        Log.d(TAG, "JUNAID1 -"+startLatitude + "--" + startLongitude);

//                                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).flat(true).icon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar))));
                                        carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                                                flat(true).icon(BitmapDescriptorFactory.fromResource(
                                                mVehicleTypesAndColors.setCarColor(colorCar, vehicle_type))));  // setCarColor(colorCar)
                                        carMarker.setAnchor(0.5f, 0.5f);
                                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                .target(startPosition)
                                                .zoom(defaultZoom)
                                                .build()));

                                        isFirstPosition = false;

                                    } else {
                                        endPosition = new LatLng(startLatitude, startLongitude);
//                                        Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude
//                                                + "--JUNAID2 -  Check --" + startPosition.longitude + "--" + endPosition.longitude);

                                        if ((startPosition.latitude != endPosition.latitude) ||
                                                (startPosition.longitude != endPosition.longitude)) {
//                                            Log.e(TAG, "NOT SAME");
                                            startBikeAnimation(startPosition, endPosition, colorCar, speed);
                                        } else {
                                            Log.i(TAG, "SAME");
                                        }
                                    }
                                }


                          /*  Img_btn_fullmap.setOnClickListener(v -> {
                                Intent intentMap = new Intent(mContext, MyHomeMapActivity.class);
                                intentMap.putExtra(VariablesConstant.VEHICLE_ID,vehicle_id);
                                intentMap.putExtra(VariablesConstant.VEHICLE_NUMBER,vehicle_no);
                                intentMap.putExtra(VariablesConstant.VEHICLE_TYPE, vehicle_type);
                                intentMap.putExtra(VariablesConstant.DEFAULT_LATITUDE,sLatitude);
                                intentMap.putExtra(VariablesConstant.DEFAULT_LONGITUDE,sLongitude);
                                startActivity(intentMap);
                                finish();

                                // stop the Handler
                                stopRepeatingTask();
                                stopRepeatingTaskRefresh();

                            });*/

                        }
                        else {
                            Log.e(TAG, "Response code :" + status);
                        }
                    } else {
                        // error case  responseCode
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Massage :" + e.toString());
                }

            }
            @Override
            public void onFailure(Call<ShowLocationResponse> call, Throwable t) {
                Log.e(TAG," onFailure: "+t.toString());
            }
        });

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
        gpsENABLE();

        // Zoom in, animating the camera. - iv_plus,iv_minus
        iv_plus.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());
            String zoomIn = CameraUpdateFactory.zoomIn().toString();
                Log.i(TAG,"googleMap zoomIn : "+zoomIn);
        });

        iv_minus.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.zoomOut());
            String zoomOut = CameraUpdateFactory.zoomOut().toString();
                Log.i(TAG,"googleMap zoomIn: "+zoomOut);
        });


        // LocationUpdate
//        getDriverLocationUpdate(token,vehicle_id);
        startGettingOnlineDataFromCar();
//        dynamicPolyLine();
        getDriverLocationUpdate(userId,vehicle_id);

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


    private void autoSignalHandlerRefresh() {
//        Log.i(TAG, "Handler - autoSignalHandlerRefresh");
        handlerLocation = new Handler();
//        mStatusCheckerLocation.run();
    }



    // stop the mStatusCheckerLocation
    void stopRepeatingTaskRefresh() {
        // stop the Handler
//        Log.i(TAG, "Handler - stopRepeatingTaskRefresh");
        handlerLocation.removeCallbacks(mStatusCheckerLocation);
//        handlerLocation.removeMessages(0);
        handlerLocation.removeCallbacksAndMessages(null);
    }

    void startGettingOnlineDataFromCar() {
        handlerLocation.post(mStatusCheckerLocation);
    }


    public Runnable mStatusCheckerLocation = new Runnable() {
        @Override
        public void run() {
            try {
//                getDriverLocationUpdate(token,vehicle_id);
                getDriverLocationUpdate(userId,vehicle_id);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            handlerLocation.postDelayed(mStatusCheckerLocation, DELAY);
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
                carMarker.setPosition(newPos);
                carMarker.setAnchor(0.5f, 0.5f);
                //carMarker.setRotation(getBearing(start, end));

//                carMarker.setIcon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar)));
                carMarker.setIcon(BitmapDescriptorFactory.fromResource(
                        mVehicleTypesAndColors.setCarColor(vColor, vehicle_type)));
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

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
                                status.startResolutionForResult(LiveTrackingAllActivity.this, 1000);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }


}