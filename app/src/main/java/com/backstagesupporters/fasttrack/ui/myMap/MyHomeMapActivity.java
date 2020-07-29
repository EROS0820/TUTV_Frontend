package com.backstagesupporters.fasttrack.ui.myMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.ui.activity.LiveTrackingActivity;
import com.backstagesupporters.fasttrack.models.MyLocation;
import com.backstagesupporters.fasttrack.responseClass.ShowLocationResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.fragment.VilleyDriverLocation;
import com.backstagesupporters.fasttrack.utils.CommonMap;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.backstagesupporters.fasttrack.utils.vehicle.MyLocationAddress;
import com.backstagesupporters.fasttrack.utils.vehicle.VehicleTypesAndColors;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import io.gloxey.gnm.parser.GloxeyJsonParser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.backstagesupporters.fasttrack.ui.myMap.MapUtils.getBearing;
import static com.google.android.gms.maps.model.JointType.ROUND;


public class MyHomeMapActivity extends BaseActivity implements OnMapReadyCallback {
        private String TAG= getClass().getSimpleName();
//    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private ImageView imageViewScreen;
    private static final long DELAY = 4500;
    private static final long ANIMATION_TIME_PER_ROUTE = 3000;
    GoogleMap googleMap;
    private PolylineOptions polylineOptions;
    private Polyline greyPolyLine;
    private SupportMapFragment mapFragment;
    private Handler handlerLocation;
    private Marker carMarker;
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
    List<LatLng> polyLineList;
    private double lat, lng;

    Polyline line; //added
    private ArrayList<LatLng> points; //added

    String sLatitude="",sLongitude="";
    double latitude = 0.0;
    double longitude = 0.0;
    private float defaultZoom=17.0f;

    // Give your Server URL here >> where you get car location update
    private boolean isFirstPosition = true;
    private Double startLatitude;
    private Double startLongitude;
    private TextView tv_your_car_is_stopped,tv_speed, tv_location;
    private TextView  tv_avg_speed,tv_duration,tv_max_speed,tv_total_distance,tv_total_trip,tv_vehicleNumber,tv_date;
    private Geocoder geocoder;
    private List<Address> addressList;
    private String bookAddress;

    //http://3.135.158.46/api/login?email=tajinder&password=asd12345
    private String speed, gps_length,satellites,direction,alarm, signal_strength, position_color;
    private String colorCar="", car_position="",  mAddress;

    //=Interface Declaration
    private ProgressDialog pd;
    private ApiInterface apiInterface;
    private String token, vehicle_id,vehicle_number,vehicle_type,vehicle_color;
    private String userId ="";
    MyLocationAddress mLocationAddress;
    VehicleTypesAndColors mVehicleTypesAndColors;
    private LatLng startposi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullmap);
        if(googleMap!=null)
            googleMap.clear();
        mContext = MyHomeMapActivity.this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fmap);
        mapFragment.getMapAsync(this);
        points = new ArrayList<LatLng>();

        Intent intent = getIntent();
        if (intent!=null){
            vehicle_id = intent.getStringExtra(VariablesConstant.VEHICLE_ID);
            vehicle_number = intent.getStringExtra(VariablesConstant.VEHICLE_NUMBER);
            vehicle_color = intent.getStringExtra(VariablesConstant.VEHICLE_COLOR);
            vehicle_type = intent.getStringExtra(VariablesConstant.VEHICLE_TYPE);
            sLatitude = intent.getStringExtra(VariablesConstant.DEFAULT_LATITUDE);
            sLongitude = intent.getStringExtra(VariablesConstant.DEFAULT_LONGITUDE);
            Log.d(TAG, "intent vehicle_id : " + vehicle_id);
            Log.d(TAG, "intent vehicle_number : " + vehicle_number);
            Log.d(TAG, "intent mVehicleType : " + vehicle_type);
            Log.d(TAG, "intent sLatitude : " + sLatitude);
            Log.d(TAG, "intent sLongitude : " + sLongitude);
        }

//        staticPolyLine();

        initView();

        // TODO: 5/14/2020  
        mLocationAddress = new MyLocationAddress(mContext);
        mVehicleTypesAndColors = new VehicleTypesAndColors(mContext);
        tv_vehicleNumber.setText(vehicle_number);

        imageViewScreen.setOnClickListener(v -> {
            startActivity(new Intent(mContext, LiveTrackingActivity.class));
            finish();
        });

        iv_tool_back_left.setOnClickListener(v -> {
            onBackPressed();
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * reFresh();
         * Call the HAndler
         */
        autoSignalHandlerRefresh();
        startGettingOnlineDataFromCar();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.w(TAG,"onStop");

    }


    private void initView() {
        tv_avg_speed = findViewById(R.id.tv_avg_speed);
        tv_duration = findViewById(R.id.tv_duration);
        tv_max_speed = findViewById(R.id.tv_max_speed);
        tv_total_distance = findViewById(R.id.tv_total_distance);
        tv_total_trip = findViewById(R.id.tv_total_trip);
        tv_location = findViewById(R.id.tv_location);
        tv_speed = findViewById(R.id.tv_speed);
        tv_vehicleNumber = findViewById(R.id.tv_vehicleNumber);
        tv_date = findViewById(R.id.tv_date);
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        imageViewScreen = findViewById(R.id.imageViewScreen);
    }



    // TODO: 3/5/2020
    void startGettingOnlineDataFromCar() {
        handlerLocation.post(mStatusCheckerLocation);
    }

    private void autoSignalHandlerRefresh() {
//        Log.i(TAG, "Handler - autoSignalHandlerRefresh");
        handlerLocation = new Handler();
        mStatusCheckerLocation.run();

    }

    // stop the mStatusCheckerLocation
    void stopRepeatingTaskRefresh() {
//        Log.i(TAG, "Handler - stopRepeatingTaskRefresh");
        // stop the Handler
        if (mStatusCheckerLocation != null) {
            handlerLocation.removeCallbacks(mStatusCheckerLocation);
//            handlerLocation.removeMessages(0);
//            Log.i(TAG, "stopRepeatingTaskRefresh");
        }

        handlerLocation.removeCallbacks(mStatusCheckerLocation);
//        handlerLocation.removeMessages(0);
        handlerLocation.removeCallbacksAndMessages(null);

    }






    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.setPadding(0, 0, 0, 800);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);
        //googleMap.getUiSettings().setZoomControlsEnabled(true);

        // TODO: 5/31/2020
//        sLatitude = AppPreferences.loadPreferences(mContext, VariablesConstant.DEFAULT_LATITUDE);
//        sLongitude = AppPreferences.loadPreferences(mContext, VariablesConstant.DEFAULT_LONGITUDE);
        if ( sLatitude!=null ){
            latitude = Double.parseDouble(sLatitude);
            longitude = Double.parseDouble(sLongitude);
        }

//        Log.d(TAG, "intent sLatitude : " + sLatitude);
//        Log.d(TAG, "intent sLongitude : " + sLongitude);

        startPosition = new LatLng(latitude, longitude);

//        googleMap.addMarker(new MarkerOptions().position(startPosition).
//                flat(true).icon(BitmapDescriptorFactory.fromResource(
//                        mVehicleTypesAndColors.setCarColor(colorCar, mVehicleType))));
//        carMarker.setAnchor(0.5f, 0.5f);
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                                .target(startPosition)
                                .zoom(defaultZoom)
                                .build()));

//        dynamicPolyLine();
    }





    private void getDriverLocationUpdate2(String user_id, String vehicle_id) {
//        Log.e(TAG, "getDriverLocationUpdate >>> userId :" + userId +", vehicleId :" + vehicle_id);

        try {
            String URL = "http://3.135.158.46/api/show_location4?fullscreen=1&user_id="+user_id+"&vehicle_id="+vehicle_id;
            Log.e(TAG, "getDriverLocationUpdate URL :"+ URL);
            String GloxeyJsonParser_response = VilleyDriverLocation.CallVehicles(mContext,URL);
            Log.e(TAG, "GloxeyJsonParser_response :"+ GloxeyJsonParser_response);
            ShowLocationResponse locationResponse = GloxeyJsonParser.getInstance().parse(GloxeyJsonParser_response, ShowLocationResponse.class);
            int status = locationResponse.getStatus();

            // TODO: 5/14/2020
                tv_total_trip.setText("Total Trip"+locationResponse.getTotalTrip());
            tv_total_distance.setText("Total Distance\n" + "(km)"+"\n"+locationResponse.getTotalDistance());

//                        mVehicleType = locationResponse.getVehicleType();
//                        Log.e(TAG, "Junaid mVehicleType :" + mVehicleType);
            if (status == 200) {
                List<MyLocation> locationResponseList = locationResponse.getLocation();
                String soccermomLatitude = locationResponseList.get(0).getLatitude();
                String soccermomLongitude = locationResponseList.get(0).getLongitude();
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

//                speed = locationResponseList.get(0).getSpeed();
//                gps_length = locationResponseList.get(0).getGpsLength();
//                satellites = locationResponseList.get(0).getSatellites();
//                direction = locationResponseList.get(0).getDirection();
//                alarm = locationResponseList.get(0).getAlarm();
                car_position = locationResponseList.get(0).getPosition();          // "position": "Is Parked",- car position
                position_color = locationResponseList.get(0).getPositionColor();   // pointer color
                colorCar = locationResponseList.get(0).getColor();        // car color
//                            Log.i(TAG, "signal_strength : " + signal_strength);
//                            Log.i(TAG, "soccermomLatitude : " + soccermomLatitude);
//                            Log.i(TAG, "soccermomLongitude : " + soccermomLongitude);

                // TODO: 5/14/2020
                String average_speed, max_speed;
                average_speed = locationResponseList.get(0).getAverageSpeed();
                max_speed = locationResponseList.get(0).getMaxSpeed();
                Log.i(TAG, "Junaid average_speed : " + average_speed);
                Log.i(TAG, "Junaid max_speed : " + max_speed);

                tv_avg_speed.setText("Avg. Speed \n" + "(km/h)"+"\n"+average_speed);
                tv_max_speed.setText("Max. Speed \n" + "(km/h)"+"\n"+max_speed);
//              tv_duration.setText("Duration \n" + "00 Day(s), hh:mm:ss"+locationResponseList.get(0).getTodaysdistance());

                String location_time = locationResponseList.get(0).getLocationTime();
                String location_date = locationResponseList.get(0).getLocationDate();
                String duration = locationResponseList.get(0).getStatusFrom();
                tv_duration.setText("Duration \n" + "00 Day(s), hh:mm:ss"+"\n"+duration);
                tv_date.setText(location_date);
                speed = locationResponseList.get(0).getSpeed();
                // TODO: 5/31/2020
                if (soccermomLatitude!=null){
                    startLatitude = Double.valueOf(soccermomLatitude);
                    startLongitude = Double.valueOf(soccermomLongitude);

                    tv_speed.setText(speed);
                    mAddress = mLocationAddress.getAddress(startLatitude, startLongitude);
//                            Log.w(TAG,"getAddress Address :"+mAddress);
                    tv_location.setText(locationResponseList.get(0).getAddress());


                    if (isFirstPosition)
                    {
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

                    }
                    else {
                        endPosition = new LatLng(startLatitude, startLongitude);
//                                Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--JUNAID Check --" + startPosition.longitude + "--" + endPosition.longitude);
                        if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {

                            Log.i(TAG, "NOT SAME");
                            startBikeAnimation(startPosition, endPosition,colorCar,speed);
                        } else {
                            Log.i(TAG, "SAME");
                        }
                    }

                  /*   liveLocation = new LatLng(soccermomLatitude, soccermomLongitude);
                            if (count == 1) {
                                StartPoint = new LatLng(soccermomLatitude, soccermomLongitude);
                                count++;
                            }
                            getAddress(soccermomLatitude, soccermomLongitude);

                            startNavigation(soccermomLatitude, soccermomLongitude);*/


                   /* if (!speed.equals("0")){
                        LatLng latLng = new LatLng(startLatitude,startLongitude);
                        points.add(latLng);
                        redrawLine(); //added
                    }*/

                }






            } else {
                Log.e(TAG, "Get Location Response code :" + status);
            }

        } catch (Exception e) {
            e.printStackTrace();
//                    Log.e(TAG, "Massage :" + e.toString());
        }

    }


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
                carMarker.setRotation(getBearing(start, end));

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
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 300);
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


    private void startBikeAnimation2(final LatLng start, final LatLng end,String vColor, String mSpeed) {
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

                // TODO: 3/29/2020
                //getBearing - SphericalUtil.computeHeading
                int speed = Integer.parseInt(mSpeed);
                if (vColor.equalsIgnoreCase("green") && speed > 0){
                    carMarker.setRotation(getBearing(start, end));
                }

//                carMarker.setIcon(BitmapDescriptorFactory.fromResource(getMarkerColor(colorCar)));
                carMarker.setIcon(BitmapDescriptorFactory.fromResource(mVehicleTypesAndColors.setCarColor(colorCar, vehicle_type)));

                // todo : Junaid > i can delay here
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(newPos)
                        .zoom(defaultZoom)
                        .build()));

                startPosition = carMarker.getPosition();
            }
        });
        valueAnimator.start();
    }


    Runnable mStatusCheckerLocation = new Runnable() {
        @Override
        public void run() {
            try {
//                getDriverLocationUpdate();
//                getDriverLocationUpdate(token,vehicle_id);
                // TODO: 2/26/2020  
//                getDriverLocationUpdate(userId,vehicle_id);
                getDriverLocationUpdate2(userId,vehicle_id);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            handlerLocation.postDelayed(mStatusCheckerLocation, DELAY);
        }
    };



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


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.i(TAG, "onDestroy");
        // stop the Handler

        stopRepeatingTaskRefresh();
    }

    // TODO: 4/9/2020
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Log.i(TAG, "onBackPressed");
        Intent intent = new Intent(mContext, LiveTrackingActivity.class);
        intent.putExtra(VariablesConstant.VEHICLE_ID,vehicle_id);
        intent.putExtra(VariablesConstant.VEHICLE_NUMBER,vehicle_number);
        intent.putExtra(VariablesConstant.VEHICLE_TYPE, vehicle_type);
        intent.putExtra(VariablesConstant.VEHICLE_COLOR, vehicle_color);
        startActivity(intent);
        finish();

        // stop the Handler
        stopRepeatingTaskRefresh();

    }
}

