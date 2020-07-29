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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

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


public class TripDetailsFragment2 extends BaseActivity implements OnMapReadyCallback, TripDetailsView, View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private AppCompatTextView textViewDate,textViewSpeed,textViewSpeedMax,textViewDistance,
            textViewAvgSpeed,textViewName,textViewNumber,textViewStartTime,textViewEndTime,
            textViewStartPoint,textViewEndPoints, textViewAlerts;
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
    private PolylineOptions polylineOptions;
    private Handler handler;
    private Runnable runnable;
    private int index, next;
    private LatLng startPosition, endPosition, prevLatLng;
    private float lastHead = 0;
    private int page = 1;
    private float defaultZoom=17.0f;
    Marker carMarker;
    private  List<LatLng> latLngsList2 = new ArrayList<>();
    private Marker myMarker;
    private  boolean markPause = false;

    private int selected = 0;
    private List<History> trips;
    private History trip;
    private List<History> historyList= new ArrayList<>();
    private VehicleTypesAndColors mVehicleTypesAndColors;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_details_fragment);
        mContext = TripDetailsFragment2.this;
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

        // HISTORY_REPLAY_TEXT
//        String strHistoryReplyResponseJson = UtilityReadWrite.readJsonFromFile(mContext, AppConstantFile.HISTORY_REPLAY_TEXT);
//        HistoryReplyResponse historyReplayResponse = new Gson().fromJson(strHistoryReplyResponseJson, HistoryReplyResponse.class);
//        Log.w(TAG,"strHistoryReplyResponseJson: "+strHistoryReplyResponseJson);
//        Log.e(TAG,"strHistoryReplyResponseJson size: "+historyReplayResponse.getDataHistory().get(0).getHistoryList().size());

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

//        Log.e(TAG,"dataList trips size :"+trips.size());
        for(int i= 0; i<trips.size()-1; i++){
//            latLngs.add(new LatLng(Double.parseDouble(latlongList[i].split(",")[0]),Double.parseDouble(latlongList[i].split(",")[1])));
            latLngsList.add(new LatLng(Double.parseDouble(trips.get(i).getLatitude()),Double.parseDouble(trips.get(i).getLongitude())));
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
                                addPinOnMap(latLngsList);
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

        // add the pin and draw the path
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



  /*  int pos =1;
    public void pauseAnimation(ValueAnimator mAnimator, Marker marker) {

        imageViewPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markPause){
                    markPause =false;
                    Log.i(TAG, "next Position: "+ next);

                    // moveCar
//                            moveCar2( polyLineList22);
//                            handler.postDelayed(runnable, 0);

                    if (mAnimator != null  && mAnimator.isStarted()) {
                        mAnimator.pause();
                        pos = 2;
                        Log.i(TAG, "pauseAnimation-1 ");
                        imageViewPause.setVisibility(View.GONE);
                        imageViewPlay.setVisibility(View.VISIBLE);
//                marker.remove();
                    }
                }
            }
        });

//        if (!markPause && pos ==2){
//            if (mAnimator != null  && mAnimator.isPaused()){
//                pos =1;
//                mAnimator.start();
//            }
//        }

    }

*/
    public void moveCar(Marker marker, List<LatLng> polyLineList) {
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
                        return;
                    }

                    // TODO: 3/18/2020
                    latLngsList2 =polyLineList;

                    if (index < polyLineList.size() - 1) {
                        index++;
                        next = index + 1;
                    }

                    if (index < polyLineList.size() - 1) {
                        prevLatLng = polyLineList.get(index > 0 ? index - 1 : 0);
                        startPosition = polyLineList.get(index);
                        endPosition = polyLineList.get(next);

//                        Log.i(TAG, "staticCarRunnable handler called...");
                        latLngsList2.remove(next);
                    }


                    // TODO: 3/19/2020
//                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                    ValueAnimator valueAnimator = ValueAnimator.ofObject(new LatLngInterpolator.LatLngEvaluator(), startPosition, endPosition);
                    valueAnimator.setDuration(300);
                    valueAnimator.setInterpolator(new LinearInterpolator());

//                    valueAnimator.addUpdateListener(valueAnimator1 -> {
//                        LatLng newPos = (LatLng) valueAnimator1.getAnimatedValue();// new LatLng(lat, lng);
//                        marker.setPosition(newPos);
//                        marker.setAnchor(0.5f, 0.5f);
//
//                    });

                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                            Log.i(TAG, "Car Animation Started...");

                            LatLng newPos = (LatLng) valueAnimator.getAnimatedValue();// new LatLng(lat, lng);
                            marker.setPosition(newPos);
                            marker.setAnchor(0.5f, 0.5f);
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

//                        @Override
//                        public void onAnimationPause(Animator animation) {
//                            super.onAnimationPause(animation);
//                            if (markPause){
//                                Log.i(TAG, "onAnimationPause ");
//                                imageViewPause.setVisibility(View.GONE);
//                                imageViewPlay.setVisibility(View.VISIBLE);
//                            }
//                        }

//                        @Override
//                        public void onAnimationResume(Animator animation) {
//                            super.onAnimationResume(animation);
//                            if (markPause){
//                                Log.i(TAG, "onAnimationResume ");
//                                imageViewPause.setVisibility(View.VISIBLE);
//                                imageViewPlay.setVisibility(View.GONE);
//                            }
//                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (runnable != null && handler != null) {

                                handler.removeCallbacks(runnable);

                                handler.postDelayed(runnable, 0);
                            }
                        }
                    });


                    valueAnimator.start();
//                    pauseAnimation(valueAnimator, marker);

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
//                    pauseAnimation(bearingAnimator);
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
                if (polylineOptions != null) {
//                    if(imageViewPause != null && imageViewPause.getVisibility() == View.GONE){
//                        imageViewPause.setVisibility(View.VISIBLE);
//                    }


                    imageViewPlay.setVisibility(View.GONE);
                    imageViewPause.setVisibility(View.VISIBLE);

                    MarkerOptions markerOptionsNew;


                    if (polylineOptions.getPoints().size() >0)
//                        Log.w(TAG,"imageViewPlay polylineOptions size:"+  polylineOptions.getPoints().size());

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
//                        Log.e(TAG, "markPause11: "+markPause);

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
                        moveCar(carMarker, polylineOptions.getPoints());
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


                        if (latLngsList2 !=null) Log.e(TAG,"imageViewPlay latLngsList2 Sise:"+latLngsList2.size());
//                        Log.e(TAG, "markPause12: "+markPause);
                        //  polylineOptions.addAll(latLngsList);
//                        polylineOptions.addAll(latLngsList2);

                        moveCar(carMarker, latLngsList2); // polyLineList22
                    }

                  /*  if (markPause){
                        if (myMarker !=null){
                            myMarker.remove();
                        }

                        Log.e(TAG, "markPause2: "+markPause);
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

                        // moveCar
                        if (polyLineList22 !=null){
                            Log.e(TAG,"imageViewPlay polyLineList22 Sise:"+polyLineList22.size());
                            moveCar2(gmap.addMarker(markerOptionsNew), polyLineList22);
                        }

                    }*/
                }
                break;


            case R.id.imageViewPause:
                if (polylineOptions != null) {
//                    Log.e(TAG, "markPause21: "+markPause);
                    markPause =true;
                    imageViewPlay.setVisibility(View.VISIBLE);
                    imageViewPause.setVisibility(View.GONE);

//                    Log.e(TAG, "markPause22: "+markPause);

                    if (latLngsList2 !=null)
//                    Log.e(TAG,"imageViewPause polyLineList2 Sise:"+ latLngsList2.size());
                    if(handler != null && runnable != null){
                        imageViewPlay.setVisibility(View.VISIBLE);
                        handler.removeCallbacks(runnable);
                        runnable = null;
                        handler = null;
                    }
                }
                break;

            case R.id.imageViewPreviews:
                if(imageViewPlay != null && imageViewPlay.getVisibility() == View.GONE){
                    imageViewPlay.setVisibility(View.VISIBLE);
                    imageViewPause.setVisibility(View.GONE);
                }

                if (latLngsList2 !=null)
//                    Log.e(TAG,"imageViewPreviews polyLineList22 Sise:"+ latLngsList2.size());
                if(handler != null && runnable != null){
                    imageViewPlay.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(runnable);
                    runnable = null;
                    handler = null;
                }

                if (selected == 0) {
                    if (page > 1) {
                        page--;
                        //     presenter.loadTripList(page, vehicleId);
                    } else {
                        return;
                    }
                }else {
                    selected--;
                    loadTripDetail(trips.get(selected));
                }

                break;

            case R.id.imageViewNext:
                if(imageViewPlay != null && imageViewPlay.getVisibility() == View.GONE){
                    imageViewPlay.setVisibility(View.VISIBLE);
                    imageViewPause.setVisibility(View.GONE);
                }
                if (latLngsList2 !=null)
//                    Log.e(TAG,"imageViewNext polyLineList22 Sise:"+ latLngsList2.size());
                if(imageViewPlay != null && handler != null && runnable != null){
                    imageViewPlay.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(runnable);
                    runnable = null;
                    handler = null;
                }
                if((trips != null && trips.size() > 0)) {
                    if (selected != trips.size()-1) {
                        selected++;
                        loadTripDetail(trips.get(selected));
                    }else if(selected != trips.size() && callApi){
                        page++;
                        //        presenter.loadTripList(page, vehicleId);
                    }else {
                        return;
                    }
                }

                break;
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
        AppPreferences.savePreferences(mContext,VariablesConstant.HISTORY_REPLAY_FILE , "");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.e(TAG,"onRestart");
        AppPreferences.savePreferences(mContext,VariablesConstant.HISTORY_REPLAY_FILE , "");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, HistoryReplayActivity.class));
        finish();
    }



}
