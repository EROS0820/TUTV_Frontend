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

import static com.backstagesupporters.fasttrack.ui.myMap.MapUtils.getBearing;


public class TripDetailsFragment3 extends BaseActivity implements OnMapReadyCallback, TripDetailsView, View.OnClickListener {
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
    ValueAnimator valueAnimator, bearingAnimator;

    private List<LatLng> latLngsList = new ArrayList<>();
    private boolean callApi = true;
    private Polyline polyline;
    private SupportMapFragment mapFragment;
    private GoogleMap gmap;
    private PolylineOptions polylineOptions, polylineOptions2;
    private Handler handler;
    private Runnable runnable;
    private Thread mThread;
    private int index, next;
    private LatLng startPosition, endPosition, prevLatLng;
    private float lastHead = 0;
    private float defaultZoom=17.0f;
    Marker carMarker;
    private  List<LatLng> latLngsList2 = new ArrayList<>();
    private Marker myMarker;
    private  boolean markPause = false;
    private static final long ANIMATION_TIME_PER_ROUTE = 300;

    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    private List<History> trips;
    private History trip;
    private List<History> historyList= new ArrayList<>();
    private VehicleTypesAndColors mVehicleTypesAndColors;
    MyThread myThread;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_details_fragment);
        mContext = TripDetailsFragment3.this;
        initView();
        mVehicleTypesAndColors = new VehicleTypesAndColors(mContext);

        // List<History> trips
        trips = new ArrayList<>();

        String strHistoryReplyResponseAppPreferences = AppPreferences.loadPreferences(mContext, VariablesConstant.HISTORY_REPLAY_FILE);
        Log.w(TAG,"strHistoryReplyResponseAppPreferences: "+strHistoryReplyResponseAppPreferences);
        HistoryReplyResponse historyReplayResponse = new Gson().fromJson(strHistoryReplyResponseAppPreferences, HistoryReplyResponse.class);
        if (strHistoryReplyResponseAppPreferences !=null && historyReplayResponse !=null){
            Log.w(TAG,"strHistoryReplyResponseAppPreferences size: "+historyReplayResponse.getDataHistory().get(0).getHistoryList().size());
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            String data1 = bundle.getString("data1");
            String sTime = bundle.getString("sTime");
            String  eTime = bundle.getString("eTime");
            String  vehicle_id = bundle.getString("vehicle_id");
            Log.e(TAG,"My data1: "+data1);
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
            Log.e(TAG,"List<HistoryData> size: "+dataList.size());
            loadTripDetailData(dataList);
        }

        if(latLngsList != null){
            latLngsList.clear();
        }

        Log.e(TAG,"dataList trips size :"+trips.size());
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
                    polylineOptions.addAll(latLngs);;
                    polylineOptions.width(5);
//            polylineOptions.color(ContextCompat.getColor(MyApplication.getContext(), R.color.black));
                    polylineOptions.color(Color.BLACK);
                    polylineOptions.geodesic(true);

                  /*  MarkerOptions marker = new MarkerOptions().position(latLngs.get(0));
                    marker.icon(getMarkerBitmapFromView());
                    gmap.addMarker(marker);*/

                  // add for polyline
                    polyline = gmap.addPolyline(polylineOptions); // TODO: 1/11/2020  
//                    greyPolyLine = gmap.addPolyline(polylineOptions);

                    gmap.animateCamera(cu);
                }
            });

        }
    }

    public void moveCar(Marker marker, List<LatLng> polyLineList) {

        // Your code here


        handler = new Handler();
    }





    // TODO: 3/19/2020
    private void startBikeAnimation(LatLng startPosition, LatLng endPosition, Marker marker) {
        Log.i(TAG, "Car Animation Started...");
        valueAnimator = ValueAnimator.ofObject
                (new LatLngInterpolator.LatLngEvaluator(), startPosition, endPosition);
        valueAnimator.setDuration(ANIMATION_TIME_PER_ROUTE);  //300
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
                myMarker = marker;
            }
        });
        valueAnimator.start();

        //bearingTo
        final float bearingTo = (float) SphericalUtil.computeHeading(startPosition, endPosition);
        bearingAnimator = ValueAnimator.ofFloat(lastHead, bearingTo);
        bearingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        bearingAnimator.setDuration(ANIMATION_TIME_PER_ROUTE); //10
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


    private float v;
    private double lat, lng;
    private void startBikeAnimation2(final LatLng start, final LatLng end,Marker marker ) {
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
                marker.setPosition(newPos);
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(getBearing(start, end));

               // marker.setIcon(BitmapDescriptorFactory.fromResource(mVehicleTypesAndColors.setCarColor("green", mVehicleType)));
                // todo : Junaid > i can delay here
                gmap.moveCamera(CameraUpdateFactory
                        .newCameraPosition
                                (new CameraPosition.Builder()
                                        .target(newPos)
                                        .zoom(defaultZoom)
                                        .build()));

                startPosition = marker.getPosition();

            }

        });
        valueAnimator.start();
    }





    @Override
    public void onClick(View mView) {
        myThread = new MyThread();

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

                    if (myThread !=null){
                        myThread.run();
//                        myThread.resume();
                    }

                    imageViewPlay.setVisibility(View.GONE);
                    imageViewPause.setVisibility(View.VISIBLE);

                    markPause =false;
                    Log.e(TAG, "markPause22: "+markPause);

//                    if (myMarker !=null){
//                        myMarker.remove();
//                    }
                }
                break;


            case R.id.imageViewPause:
                imageViewPlay.setVisibility(View.VISIBLE);
                imageViewPause.setVisibility(View.GONE);
                markPause =false;
                Log.e(TAG, "markPause22: "+markPause);

                if (myThread !=null){
                    myThread.pause();

                    // myThread.stop();
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
            Log.e(TAG,"historyData getHistoryList size:"+historyData.get(i).getHistoryList().size() );
            for (int j=0; j<historyData.get(i).getHistoryList().size(); j++){

                loadTripDetail(historyData.get(i).getHistoryList().get(j));
                trips.add(historyData.get(i).getHistoryList().get(j));
            }

            historyList = historyData.get(i).getHistoryList();
            Log.e(TAG,"List<History> historyList size:"+historyList.size() );

            vehicle_no =   historyData.get(i).getVehicleNo();
            date =  historyData.get(i).getDate();
            Log.i(TAG, "vehicle_no : " + vehicle_no);
            Log.i(TAG, "date : " + date);
            textViewNumber.setText(vehicle_no);
        }
    }

    @Override
    public void loadTripDetailResponse(HistoryReplyResponse response) {
        message = response.getMessage();
        mVehicleType = response.getVehicleType();
        to_date = response.getToDate();

        Log.i(TAG,"message:"+message);
        Log.i(TAG,"mVehicleType:"+mVehicleType);
        Log.i(TAG,"to_date:"+to_date);
//        textViewSpeed.setText(response.getSpeed());
        textViewDistance.setText(response.getDistance());
        textViewSpeedMax.setText(response.getMaxSpeed());
        textViewAvgSpeed.setText( response.getAvgSpeed());
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"onStop");
        AppPreferences.savePreferences(mContext,VariablesConstant.HISTORY_REPLAY_FILE , "");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG,"onRestart");
        AppPreferences.savePreferences(mContext,VariablesConstant.HISTORY_REPLAY_FILE , "");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, HistoryReplayActivity.class));
        finish();
    }



    ///==========================

    public class MyThread implements Runnable {

        @Override
        public void run() {
            while (running) {
                synchronized (pauseLock) {
                    if (!running) { // may have changed while waiting to
                        // synchronize on pauseLock
                        break;
                    }
                    if (paused) {
                        try {
                            synchronized (pauseLock) {
                                pauseLock.wait(); // will cause this Thread to block until
                                // another thread calls pauseLock.notifyAll()
                                // Note that calling wait() will
                                // relinquish the synchronized lock that this
                                // thread holds on pauseLock so another thread
                                // can acquire the lock to call notifyAll()
                                // (link with explanation below this code)
                            }
                        } catch (InterruptedException ex) {
                            break;
                        }
                        if (!running) { // running might have changed since we paused
                            break;
                        }
                    }
                }

                //=========================================================
                // Your code here
//                    ((AppCompatImageView) mView).setImageResource(mView.isSelected() ? R.drawable.play : R.drawable.ic_pause);
//                    mView.setSelected(!mView.isSelected());

                MarkerOptions markerOptionsNew;
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
//                carMarker = gmap.addMarker(markerOptionsNew);
                //carMarker.setIcon(BitmapDescriptorFactory.fromResource(setCarColor(colorCar)));
                // moveCar
                // moveCar(carMarker, polylineOptions.getPoints());
                Marker marker = gmap.addMarker(markerOptionsNew);
                List<LatLng> polyLineList = polylineOptions.getPoints();

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

                Log.i(TAG, "polyLineList.get(next) : "+next);
                Log.i(TAG, "Car Runnable handler called...");

//                  startBikeAnimation(startPosition, endPosition, marker);
                startBikeAnimation2(startPosition, endPosition, marker);

            }
        }

        public void stop() {
            running = false;
            // you might also want to interrupt() the Thread that is
            // running this Runnable, too, or perhaps call:
            resume();
            // to unblock
        }

        public void pause() {
            // you may want to throw an IllegalStateException if !running
            paused = true;
        }

        public void resume() {
            synchronized (pauseLock) {
                paused = false;
                pauseLock.notifyAll(); // Unblocks thread
            }
        }

    }



}
