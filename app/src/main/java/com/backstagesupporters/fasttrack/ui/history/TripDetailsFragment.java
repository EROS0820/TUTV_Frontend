package com.backstagesupporters.fasttrack.ui.history;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.History;
import com.backstagesupporters.fasttrack.models.HistoryData;
import com.backstagesupporters.fasttrack.responseClass.HistoryReplyResponse;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.myMap.LatLngInterpolator;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
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
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import static com.backstagesupporters.fasttrack.ui.myMap.MapUtils.getBearing;


public class TripDetailsFragment extends BaseActivity implements OnMapReadyCallback, TripDetailsView, View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private AppCompatTextView textViewDate,textViewSpeed,textViewSpeedMax,textViewDistance,
            textViewAvgSpeed,textViewNumber,textViewStartTime,textViewEndTime,
            textViewStartPoint,textViewEndPoints, textViewAlerts;
    private TextView tv_speed;
    private AppCompatImageView imageViewBack, imageViewPreviews,imageViewNext;
    private AppCompatImageView imageViewPlay,imageViewPause, imageViewFullScreen;
    private CardView cardViewCar;
    private NestedScrollView scrollBottom;

    private String mVehicleType, vehicle_name,vehicle_no, distance,avg_speed,max_speed,date;;
    private String message,total,myDate,to_date;

    private List<LatLng> latLngsList = new ArrayList<>();
    private boolean callApi = true;
    private Polyline polyline;
    private SupportMapFragment mapFragment;
    private GoogleMap gmap;
    private PolylineOptions polylineOptions, polylineOptions2;
    private Handler handler;
    private Runnable runnable;
    private Runnable mStatusCheckerRunnable;
    private Handler handlerLocation;
    private static final long DELAY = 300;
    private static final long ANIMATION_TIME_PER_ROUTE = 300;
    private boolean isFirstPosition = true;
    private int TRIP_SIZE = 0;
    private int INDEX_SIZE = 0;

    private LatLng newPos;
    private int index=0, next=0;
    private LatLng startPosition, endPosition, prevLatLng;
    private float lastHead = 0;
    private float defaultZoom=18.0f;
    private Marker carMarker;
    private List<History> trips2 = new ArrayList<History>();
    private Marker myMarker;
    private  boolean markPause = true;

    private List<History> trips;
    private History trip;
    private List<History> historyList= new ArrayList<>();
    private VehicleTypesAndColors mVehicleTypesAndColors;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_details_fragment);
        mContext = TripDetailsFragment.this;

        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        initView();
        mVehicleTypesAndColors = new VehicleTypesAndColors(mContext);

        // List<History> trips
        trips = new ArrayList<>();

        String strHistoryReplyResponseAppPreferences = AppPreferences.loadPreferences(mContext, VariablesConstant.HISTORY_REPLAY_FILE);
//        Log.w(TAG,"strHistoryReplyResponseAppPreferences: "+strHistoryReplyResponseAppPreferences);
        HistoryReplyResponse historyReplayResponse = new Gson().fromJson(strHistoryReplyResponseAppPreferences, HistoryReplyResponse.class);
        if (strHistoryReplyResponseAppPreferences !=null && historyReplayResponse !=null){
//            Log.w(TAG,"strHistoryReplyResponseAppPreferences size: "+historyReplayResponse.getDataHistory().get(0).getHistoryList().size());
        }

        // MySingleton
//        Log.w(TAG, "MySingleton.getInstance().getHistoryReplyResponse : " +  MySingleton.getInstance().getHistoryReplyResponse());
//        HistoryReplyResponse responseData = MySingleton.getInstance().getHistoryReplyResponse();
        loadTripDetailResponse(historyReplayResponse);
//        Log.e(TAG,"MySingleton size: "+responseData.getDataHistory().get(0).getHistoryList().size());

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            String data1 = bundle.getString("data1");
            String sTime = bundle.getString("sTime");
            String  eTime = bundle.getString("eTime");
            String  vehicle_id = bundle.getString("vehicle_id");
//            Log.e(TAG,"My data1: "+data1);
            textViewDate.setText(data1);
            textViewStartTime.setText(sTime);
            textViewEndTime.setText(eTime);
//            textViewTime.setText();
        }else {
            Log.e(TAG,"Bundle of data is empty");
        }


        // set List DataHistory
        List<HistoryData> dataList = historyReplayResponse.getDataHistory();
        if (dataList !=null){
//            Log.e(TAG,"List<HistoryData> size: "+dataList.size());
            loadTripDetailData(dataList);
        }

        if(latLngsList != null){
            latLngsList.clear();
        }

        TRIP_SIZE = trips.size();
//        Log.e(TAG,"TRIP_SIZE :"+TRIP_SIZE);
        for(int i= 0; i<TRIP_SIZE-1; i++){
//            latLngs.add(new LatLng(Double.parseDouble(latlongList[i].split(",")[0]),Double.parseDouble(latlongList[i].split(",")[1])));
            latLngsList.add(new LatLng(Double.parseDouble(trips.get(i).getLatitude()),Double.parseDouble(trips.get(i).getLongitude())));
            //tv_speed.setText(trips.get(i).getSpeed());
        }



        if(gmap == null) {
            ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map_trip_details))
                    .getMapAsync(googleMap -> {
                        gmap = googleMap;
                        gmap.getUiSettings().setMapToolbarEnabled(false);
                        gmap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {

                                // add the pin and draw the path
                                if (latLngsList!=null){
                                    addPinOnMap(latLngsList);
                                }
                            }
                        });
                    });
        }else {
            if(trip != null){
                loadTripDetail(trip);
            }
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_trip_details);
        mapFragment.getMapAsync(this);

    }


    private void initView() {
        textViewDate = findViewById(R.id.textViewDate);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewSpeed = findViewById(R.id.textViewSpeed);
        textViewAvgSpeed = findViewById(R.id.textViewAvgSpeed);
        textViewStartPoint = findViewById(R.id.textViewStartPoint);
        textViewSpeedMax = findViewById(R.id.textViewSpeedMax);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewNumber = findViewById(R.id.textViewNumber);
        textViewEndTime = findViewById(R.id.textViewEndTime);
        textViewEndPoints = findViewById(R.id.textViewEndPoints);
        textViewAlerts = findViewById(R.id.textViewAlerts);
        tv_speed = findViewById(R.id.tv_speed);

        imageViewPreviews = findViewById(R.id.imageViewPreviews);
        imageViewNext = findViewById(R.id.imageViewNext);
        cardViewCar = findViewById(R.id.cardViewCar);
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewPlay = findViewById(R.id.imageViewPlay);
        imageViewPause = findViewById(R.id.imageViewPause);
        imageViewFullScreen = findViewById(R.id.imageViewFullScreen);
        scrollBottom = findViewById(R.id.scrollBottom);

        // setOnClickListener
        imageViewPlay.setOnClickListener(this);
        imageViewPause.setOnClickListener(this);
        imageViewFullScreen.setOnClickListener(this);
        imageViewPreviews.setOnClickListener(this);
        imageViewNext.setOnClickListener(this);

        imageViewBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , "");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gmap.setMyLocationEnabled(false);
            gmap.setTrafficEnabled(false);
            gmap.setIndoorEnabled(false);
            gmap.setBuildingsEnabled(false);
            gmap.getUiSettings().setMapToolbarEnabled(false);
            gmap.getUiSettings().setZoomControlsEnabled(false);

            gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        trips2 = trips;
        // add the pin and draw the path
        if (latLngsList!=null)
            addPinOnMap(latLngsList);
    }


    private void addPinOnMap(List<LatLng> latLngs) {
        if(latLngs != null && latLngs.size() > 0) {
            gmap.clear();
            if (polyline != null)
                polyline.remove();

//            gmap.addMarker(new MarkerOptions()
//                    .position(latLngs.get(0))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_starting_map_icon)));
//
//            MarkerOptions markerOptionsNew = new MarkerOptions()
//                    .position(latLngs.get(latLngs.size() - 1))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ending_map_icon));
//            gmap.addMarker(markerOptionsNew);


            // TODO: 1/21/2020
            MarkerOptions markerOptionsNew = new MarkerOptions()
                    .position(latLngs.get(latLngs.size() - 1))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ending_map_icon));

            gmap.addMarker(new MarkerOptions()
                    .position(latLngs.get(0))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_starting_map_icon)));

            gmap.addMarker(markerOptionsNew);

            gmap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng t:latLngs) {
                        builder.include(t);
                    }
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);

                    polylineOptions = new PolylineOptions();
                    polylineOptions.geodesic(true);
                    polylineOptions.addAll(latLngs);
                    polylineOptions.width(5);
//            polylineOptions.color(ContextCompat.getColor(MyApplication.getContext(), R.color.black));
                    polylineOptions.color(Color.BLACK);
                    polylineOptions.geodesic(true);

                  /*  MarkerOptions marker = new MarkerOptions().position(latLngs.get(0));
                    marker.icon(getMarkerBitmapFromView());
                    gmap.addMarker(marker);*/

                    polyline = gmap.addPolyline(polylineOptions); // TODO: 1/11/2020
//                    greyPolyLine = gmap.addPolyline(polylineOptions);

                    gmap.animateCamera(cu);
                }
            });

        }
    }



    private void startGettingOnlineDataFromCar() {
        handlerLocation.post(mStatusCheckerRunnable);
    }

    private void startGettingOnlineDataFromCar1() {
        handlerLocation.post(mStatusCheckerRunnable);

        MarkerOptions  markerOptionsNew1 = new MarkerOptions()
                .position(polylineOptions.getPoints().get(0))
                .icon(BitmapDescriptorFactory.fromResource(
                        mVehicleTypesAndColors.setCarColor("green", mVehicleType)));
        myMarker = gmap.addMarker(markerOptionsNew1);
//        moveCar( myMarker, latLngsList);
    }

    // private void getDriverLocationUpdate2(Marker carMarker, List<LatLng> points) {
//    private void getDriverLocationUpdate1(List<History> tripsList) {
    private void getDriverLocationUpdate1(Marker carMarker, List<LatLng> points, List<History> tripsList) {
//        Log.w(TAG,"getDriverLocationUpdate1 points SIZE :"+points.size());
//        Log.w(TAG,"getDriverLocationUpdate1 tripsList SIZE :"+tripsList.size());
//        TRIP_SIZE = trips.size();
//        Log.w(TAG,"TRIP_SIZE :"+trips.size());
//        latLngsList_SIZE = latLngsList.size();
//        Log.w(TAG,"latLngsList_SIZE size:"+latLngsList.size());

        mStatusCheckerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i=0; i< tripsList.size()-1; i++) {
                        INDEX_SIZE++;

                        Double startLatitude = points.get(i).latitude;
                        Double startLongitude =points.get(i).longitude;
                        Double startLatitude1 = Double.valueOf(tripsList.get(i).getLatitude());
                        Double startLongitude1 = Double.valueOf(tripsList.get(i).getLongitude());
                        String speed = tripsList.get(i).getSpeed();

//                        Log.i(TAG, "startLatitude Latitude : " + startLatitude);
//                        Log.i(TAG, "startLongitude Latitude : " + startLongitude);
//                        Log.i(TAG, "speed Latitude : " + speed);
//                        Log.i(TAG, "INDEX_SIZE Latitude : " + INDEX_SIZE);
                        tv_speed.setText(speed);


                        AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , String.valueOf(INDEX_SIZE));

                        getDriverLocationMove(startLatitude,startLongitude);
                        //   getDriverLocationMove(latLngsList.get(finalI).latitude,latLngsList.get(finalI).longitude);

                        if (trips.size()==1){
                            if (mStatusCheckerRunnable != null && handlerLocation != null) {

                                handlerLocation.removeCallbacks(mStatusCheckerRunnable);

                                handlerLocation.postDelayed(mStatusCheckerRunnable, 0);
                            }
                        }

                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

                handlerLocation.postDelayed(mStatusCheckerRunnable, DELAY);
            }
        };

    }





    private LatLng startPosition1, endPosition1;
    private double lat, lng;
    private float v;
    private void getDriverLocationMove(Double startLatitude,Double startLongitude) {
//        Log.d(TAG, "JUNAID: "+startLatitude + "--" + startLongitude);

        if (isFirstPosition) {
            startPosition1 = new LatLng(startLatitude, startLongitude);
           // gmap.clear();

//                  carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition1).flat(true).icon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar))));
            carMarker = gmap.addMarker(new MarkerOptions().position(startPosition1).
                    flat(true).icon(BitmapDescriptorFactory.fromResource(mVehicleTypesAndColors.setCarColor("green", mVehicleType))));  // setCarColor(colorCar)
            carMarker.setAnchor(0.5f, 0.5f);
            gmap.moveCamera(CameraUpdateFactory
                    .newCameraPosition
                            (new CameraPosition.Builder()
                                    .target(startPosition1)
                                    .zoom(defaultZoom)
                                    .build()));

            isFirstPosition = false;

        } else {
            endPosition1 = new LatLng(startLatitude, startLongitude);

//            Log.d(TAG, startPosition1.latitude + "--" + endPosition1.latitude + "--Check --" + startPosition1.longitude + "--" + endPosition1.longitude);

            if ((startPosition1.latitude != endPosition1.latitude) || (startPosition1.longitude != endPosition1.longitude)) {
                Log.e(TAG, "NOT SAME");
                startBikeAnimation(startPosition1, endPosition1);
            } else {
                Log.e(TAG, "SAME");
            }
        }
    }

    private void startBikeAnimation(final LatLng start, final LatLng end) {
//        Log.i(TAG, "startBikeAnimation called...");

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

//                carMarker.setIcon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar)));
                carMarker.setIcon(BitmapDescriptorFactory.fromResource(mVehicleTypesAndColors.setCarColor("green", mVehicleType)));
                // todo : Junaid > i can delay here
                gmap.moveCamera(CameraUpdateFactory
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





    @Override
    public void onClick(View mView) {
        switch (mView.getId()) {
            case R.id.imageViewFullScreen:
                cardViewCar.setVisibility(mView.isSelected() ? View.VISIBLE : View.GONE);
                scrollBottom.setVisibility(mView.isSelected() ? View.VISIBLE : View.GONE);
//                  textViewBreakPoint.setVisibility(mView.isSelected() ? View.GONE : View.VISIBLE);

                ((AppCompatImageView) mView).setImageResource(mView.isSelected() ? R.drawable.full_out : R.drawable.full_in);

                mView.setSelected(!mView.isSelected());

                break;

            case R.id.imageViewPlay:
                handlerLocation = new Handler();
                imageViewPlay.setVisibility(View.GONE);
                imageViewPause.setVisibility(View.VISIBLE);
                //latLngsList2 =latLngsList;
                // trips2 = trips;


                String mSize = AppPreferences.loadPreferences(mContext,VariablesConstant.INDEX_SIZE);
                if (!mSize.equalsIgnoreCase("") ){
                    int mCounter = Integer.parseInt(mSize);
//                    Log.e(TAG,"TRIP_SIZE mCounter size :"+mCounter);
                    latLngsList.remove(mCounter);
                    trips2.remove(mCounter);
                    trips.remove(mCounter);
                }

             /*   int m= 1;
                latLngsList.remove(m);
                trips2.remove(m);
                trips.remove(m);*/
                AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , "");

                // Marker
                MarkerOptions  markerOptionsNew ;
                polylineOptions2 = new PolylineOptions();
                polylineOptions2.geodesic(true);
                polylineOptions2.addAll(latLngsList);

                if (trips!=null && trips.size()-1> 0 ){
                    if (markPause){
                        markPause = false;
//                        getDriverLocationUpdate1(trips);
//                        getDriverLocationUpdate1(carMarker, polylineOptions.getPoints(), trips);
//                        startGettingOnlineDataFromCar();
//                        startGettingOnlineDataFromCar1();

                        //  setCarColor(String colorCar, String vehicleType)
                        markerOptionsNew = new MarkerOptions()
                                .position(polylineOptions.getPoints().get(0))
                                .icon(BitmapDescriptorFactory.fromResource(
                                        mVehicleTypesAndColors.setCarColor("green", mVehicleType)));

                        carMarker = gmap.addMarker(markerOptionsNew);
                        moveCar( carMarker, polylineOptions.getPoints(), trips);
                    }else {
//                        getDriverLocationUpdate1(carMarker, polylineOptions2.getPoints(), trips);
//                        startGettingOnlineDataFromCar();
                        //  moveCar(carMarker, polylineOptions.getPoints());
                    /*    if (myMarker !=null){
                            myMarker.remove();
                            carMarker.remove();
                        }*/

//                        markerOptionsNew = new MarkerOptions().position(newPos)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_green));

                        //  setCarColor(String colorCar, String vehicleType)
                        // polylineOptions.getPoints().get(0)
                        markerOptionsNew = new MarkerOptions()
                                .position(newPos)
                                .icon(BitmapDescriptorFactory.fromResource(
                                        mVehicleTypesAndColors.setCarColor("green", mVehicleType)));

                        carMarker = gmap.addMarker(markerOptionsNew);

                        moveCar(carMarker, polylineOptions2.getPoints(), trips);

                    }

//                    Log.e(TAG,"TRIP_SIZE trips :"+trips.size());
//                    Log.e(TAG,"TRIP_SIZE trips2 :"+trips2.size());
//                    Log.e(TAG,"TRIP_SIZE latLngsList :"+latLngsList.size());
//                    Log.e(TAG,"TRIP_SIZE polylineOptions2 :"+polylineOptions2.getPoints().size());

//                voidViewPlay();
                }


                break;


            case R.id.imageViewPause:
                if (trips2 != null) {
                    imageViewPlay.setVisibility(View.VISIBLE);
                    imageViewPause.setVisibility(View.GONE);

//                    Log.e(TAG, "markPause22: "+markPause);
                    if (trips !=null)
//                        Log.e(TAG,"imageViewPause trips Sise:"+ trips.size());
//                        Log.e(TAG,"imageViewPause trips2 Sise:"+ trips2.size());

                    if(handlerLocation != null && mStatusCheckerRunnable != null){
                        handlerLocation.removeCallbacks(mStatusCheckerRunnable);
                        mStatusCheckerRunnable = null;
                        handlerLocation = null;
                    }
                    if(handler != null && runnable != null){
                        handler.removeCallbacks(runnable);
                        runnable = null;
                        handler = null;
                    }

                    if (myMarker!=null) myMarker.remove();

                }
                break;

        }
    }

    private void voidViewPlay() {
        if (polylineOptions != null) {
//                    if(imageViewPause != null && imageViewPause.getVisibility() == View.GONE){
//                        imageViewPause.setVisibility(View.VISIBLE);
//                    }

            MarkerOptions markerOptionsNew;


            if (polylineOptions.getPoints().size() >0)
//                Log.w(TAG,"imageViewPlay polylineOptions size:"+  polylineOptions.getPoints().size());

                /*    if(imageViewPlay != null && imageViewPlay.getVisibility() == View.GONE){
                        markPause =false;
                    }

                    if(imageViewPause != null && imageViewPause.getVisibility() == View.VISIBLE){
                        markPause =false;
                    }*/

            if (!markPause){
                if (myMarker !=null){
                    myMarker.remove();
                }
                markPause =false;
//                Log.e(TAG, "markPause11: "+markPause);

                // /*R.drawable.home_new_car*/
                if (mVehicleType ==null){
                    markerOptionsNew = new MarkerOptions()
                            .position(polylineOptions.getPoints().get(0))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_green));
                }else {
                    //  setCarColor(String colorCar, String vehicleType)
                    markerOptionsNew = new MarkerOptions()
                            .position(polylineOptions.getPoints().get(0))
                            .icon(BitmapDescriptorFactory.fromResource(
                                    mVehicleTypesAndColors.setCarColor("green", mVehicleType)));
                }

                // Marker
                carMarker = gmap.addMarker(markerOptionsNew);
                //carMarker.setIcon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar)));

                // moveCar
              //  moveCar(carMarker, polylineOptions.getPoints());
            }else if (markPause){
                if (myMarker !=null){
                    myMarker.remove();
                }

                // /*R.drawable.home_new_car*/
                if (mVehicleType ==null){
                    markerOptionsNew = new MarkerOptions()
                            .position(polylineOptions.getPoints().get(next))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_green));
                }else {
                    //  setCarColor(String colorCar, String vehicleType)
                    markerOptionsNew = new MarkerOptions()
                            .position(polylineOptions.getPoints().get(next))
                            .icon(BitmapDescriptorFactory.fromResource(
                                    mVehicleTypesAndColors.setCarColor("green", mVehicleType)));
                }

                // add Marker
                carMarker = gmap.addMarker(markerOptionsNew);
                //carMarker.setIcon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar)));

//               moveCar(carMarker, latLngsList2); // polyLineList22
            }

        }
    }


    @Override
    public void loadTripDetail(History data) {
        if(trip == null)
            trip = data;



       /* textViewName.setText(data.getCarModel());
        textViewNumber.setText(data.getRegistrationNumber());
        textViewDate.setText(Common.convertDate(data.getDate()));

        int hours = (int) (data.getTime() / 60);
        int minutes = (int) (data.getTime() % 60);
        textViewTime.setText(getString(R.string.estimated_time,hours,minutes)); // speed

        textViewDisance.setText(data.getDistance() + " Kms");
        textViewUserName.setText(data.getUsername());

        textViewStartPoint.setText(data.getStartPoint());
        textViewEndPoint.setText(data.getEndPoint());

        textViewSpeed.setText(data.getMaxSpeed()+ " Km/h");
        textViewAlerts.setText(data.getAlertCounts());

          if(data.getDistance() <=0 || data.getTime()<= 0){
              textViewAugSpeed.setText(0+" km/h");
          }else {
              textViewAugSpeed.setText(String.format("%.2f",(data.getDistance() /(data.getTime()/60)))+" km/h");
          }*/

    }

    @Override
    public void loadTripDetailData(List<HistoryData> historyData) {
        // List<History> historyList
        for(int i=0; i<historyData.size(); i++){
//            Log.e(TAG,"historyData getHistoryList size:"+historyData.get(i).getHistoryList().size() );
            for (int j=0; j<historyData.get(i).getHistoryList().size(); j++){

                loadTripDetail(historyData.get(i).getHistoryList().get(j));
                trips.add(historyData.get(i).getHistoryList().get(j));
            }

            historyList = historyData.get(i).getHistoryList();
//            Log.e(TAG,"List<History> historyList size:"+historyList.size() );

            vehicle_no =   historyData.get(i).getVehicleNo();
            date =  historyData.get(i).getDate();
//            Log.i(TAG, "vehicle_no : " + vehicle_no);
//            Log.i(TAG, "date : " + date);
            textViewNumber.setText(vehicle_no);
        }
    }

    @Override
    public void loadTripDetailResponse(HistoryReplyResponse response) {
        message = response.getMessage();
        mVehicleType = response.getVehicleType();
        to_date = response.getToDate();

//        Log.i(TAG,"message:"+message);
//        Log.i(TAG,"mVehicleType:"+mVehicleType);
//        Log.i(TAG,"to_date:"+to_date);
//        textViewSpeed.setText(response.getSpeed());
        textViewDistance.setText(response.getDistance());
        textViewSpeedMax.setText(response.getMaxSpeed());
        textViewAvgSpeed.setText( response.getAvgSpeed());
    }


    @Override
    protected void onStop() {
        super.onStop();
//        Log.e(TAG,"onStop");
        AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , "");

        if (runnable != null && handler != null) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 0);
        }

        if (mStatusCheckerRunnable != null && handlerLocation != null) {
            handlerLocation.removeCallbacks(mStatusCheckerRunnable);
            handlerLocation.postDelayed(mStatusCheckerRunnable, 0);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, HistoryReplayActivity.class));
        finish();
    }

//    public void moveCar(Marker marker, List<LatLng> polyLineList) {
    public void moveCar2(Marker marker, List<LatLng> polyLineList, List<History> tripsList) {
//        Log.i(TAG, "Car Animation Started...");

        mStatusCheckerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // if (!isAdded())return;
                    if (index == polyLineList.size() - 1) {
//                        if (!isAdded())return;
                        imageViewPlay.setVisibility(View.VISIBLE);
                        marker.remove();
                        return;
                    }

                    if (index < polyLineList.size() - 1) {
                        index++;
                        next = index + 1;
                    }

                    if (index < polyLineList.size() - 1) {
                        prevLatLng = polyLineList.get(index > 0 ? index - 1 : 0);
                        startPosition = polyLineList.get(index);
                        endPosition = polyLineList.get(next);
                    }

                    AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , String.valueOf(next));
//                    Log.e(TAG,"index size: "+next);

                    String speed = tripsList.get(index).getSpeed();
//                    Log.i(TAG, "speed Latitude : " + speed);
                    tv_speed.setText(speed);

//                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                    ValueAnimator valueAnimator = ValueAnimator.ofObject(new LatLngInterpolator.LatLngEvaluator(), startPosition, endPosition);
                    valueAnimator.setDuration(300);
                    valueAnimator.setInterpolator(new LinearInterpolator());

                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {

                            LatLng newPos = (LatLng) valueAnimator.getAnimatedValue();// new LatLng(lat, lng);
                            marker.setPosition(newPos);
                            marker.setAnchor(0.5f, 0.5f);
                            gmap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition
                                            (new CameraPosition.Builder()
                                                    .target(newPos)
                                                    .zoom(defaultZoom)
                                                    .build()));
                        }
                    });


                    //mStatusCheckerRunnable , handlerLocation
                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mStatusCheckerRunnable != null && handlerLocation != null) {
                                AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , "");

                                handlerLocation.removeCallbacks(mStatusCheckerRunnable);

                                handlerLocation.postDelayed(mStatusCheckerRunnable, 0);
                            }
                        }
                    });

                    valueAnimator.start();

                    //bearingTo
                    final float bearingTo = (float) SphericalUtil.computeHeading(startPosition, endPosition);
                    ValueAnimator bearingAnimator = ValueAnimator.ofFloat(lastHead, bearingTo);
                    bearingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    bearingAnimator.setDuration(10);
                    bearingAnimator.addUpdateListener(valueAnimator2 -> {
                        float value = (float) valueAnimator2.getAnimatedValue();
                        marker.setRotation(value);
                    });
                    bearingAnimator.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            lastHead = bearingTo;
                        }
                    });
                    bearingAnimator.start();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

                handlerLocation.postDelayed(mStatusCheckerRunnable, DELAY);
            }
        };




    }

   public void moveCar(Marker marker, List<LatLng> polyLineList, List<History> tripsList) {
//       Log.i(TAG, "Car Animation Started...");
        try {
            int duration = 70000;
            runnable = new Runnable() {
                @Override
                public void run() {

//                    if (!isAdded())return;
                    if (index == polyLineList.size() - 1) {
//                        if (!isAdded())return;
                        imageViewPlay.setVisibility(View.VISIBLE);
                        marker.remove();
                        AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , "");
                        return;
                    }

                    // TODO: 3/18/2020
                    if (index < polyLineList.size() - 1) {
                        index++;
                        next = index + 1;
                    }

                    if (index < polyLineList.size() - 1) {
                        prevLatLng = polyLineList.get(index > 0 ? index - 1 : 0);
                        startPosition = polyLineList.get(index);
                        endPosition = polyLineList.get(next);
                    }

                    AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , String.valueOf(next));
//                    Log.e(TAG,"index size: "+next);

                    String speed = tripsList.get(index).getSpeed();
//                    Log.i(TAG, "speed Latitude : " + speed);
                    tv_speed.setText(speed);

                    // TODO: 3/19/2020
//                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                    ValueAnimator valueAnimator = ValueAnimator.ofObject(new LatLngInterpolator.LatLngEvaluator(), startPosition, endPosition);
                    valueAnimator.setDuration(300);
                    valueAnimator.setInterpolator(new LinearInterpolator());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            newPos = (LatLng) valueAnimator.getAnimatedValue();// new LatLng(lat, lng);
                            marker.setPosition(newPos);
                            marker.setAnchor(0.5f, 0.5f);
                          /*  marker.setIcon(BitmapDescriptorFactory.fromResource(
                                    mVehicleTypesAndColors.setCarColor("green", mVehicleType)));*/

                            gmap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition
                                            (new CameraPosition.Builder()
                                                    .target(newPos)
                                                    .zoom(defaultZoom)
                                                    .build()));
                            myMarker = marker;
                        }
                    });

                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (runnable != null && handler != null) {

                                handler.removeCallbacks(runnable);

                                handler.postDelayed(runnable, 0);

                                AppPreferences.savePreferences(mContext,VariablesConstant.INDEX_SIZE , "");
                            }
                        }
                    });
                    valueAnimator.start();


                    //bearingTo
                    final float bearingTo = (float) SphericalUtil.computeHeading(startPosition, endPosition);
                    ValueAnimator bearingAnimator = ValueAnimator.ofFloat(lastHead, bearingTo);
                    bearingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    bearingAnimator.setDuration(10);
                    bearingAnimator.addUpdateListener(valueAnimator2 -> {
                        float value = (float) valueAnimator2.getAnimatedValue();
                        marker.setRotation(value);
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

            };


            handler = new Handler();
            index = -1;
            next = 1;

            handler.postDelayed(runnable, duration / polyLineList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
