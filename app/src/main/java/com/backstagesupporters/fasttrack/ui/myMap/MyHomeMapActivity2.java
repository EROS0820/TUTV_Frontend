package com.backstagesupporters.fasttrack.ui.myMap;

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
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.MyLocation;
import com.backstagesupporters.fasttrack.responseClass.ShowLocationResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import retrofit2.Call;
import retrofit2.Callback;

import static com.backstagesupporters.fasttrack.ui.myMap.MapUtils.getBearing;
import static com.google.android.gms.maps.model.JointType.ROUND;



/**
 * A demonstration about car movement on google map
       by @Shihab Uddin

 TO RUN -> GIVE YOUR GOOGLE API KEY to >  google_maps_api.xml file
        -> GIVE YOUR SERVER URL TO FETCH LOCATION UPDATE

*/

public class MyHomeMapActivity2 extends AppCompatActivity implements OnMapReadyCallback {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
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
    // banani
    double latitude = 23.7877649;
    double longitude = 90.4007049;
    private float defaultZoom=17.0f;

    private Geocoder geocoder;
    private List<Address> addressList;
    private TextView tv_your_car_is_stopped,tv_speed, tv_location;
    private LinearLayout ll_location_address;
    private String bookAddress;

    // Give your Server URL here >> where you get car location update
    private boolean isFirstPosition = true;
    private Double startLatitude;
    private Double startLongitude;

    //http://3.135.158.46/api/login?email=tajinder&password=asd12345
    private String speed, gps_length,satellites,direction,alarm, signal_strength, position_color;
    private String colorCar="", car_color="",car_position="";
//    private String email="tajinder", password="asd12345" , mobile="8802587111", device_type="", device_toke="";
//    private String email="lucky15", password="lucky15" , mobile="8802587111", device_type="", device_toke="";

    //=Interface Declaration
    private ProgressDialog pd;
    private ApiInterface apiInterface;
    String token, vehicle_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullmap);
        mContext = MyHomeMapActivity2.this;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fmap);
        mapFragment.getMapAsync(this);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        vehicle_id = AppPreferences.loadPreferences(mContext, VariablesConstant.VEHICLE_ID);

        Intent intent = getIntent();
        String vehicle_id=intent.getStringExtra("vehicle_id");
//        Log.d(TAG, "intent vehicle_id : " + vehicle_id);

//        staticPolyLine();

        handler = new Handler();

        initView();
    }

    private void initView() {
        ll_location_address = findViewById(R.id.ll_location_address);
        tv_location = findViewById(R.id.tv_location);
        tv_speed = findViewById(R.id.tv_speed);
    }


    Runnable staticCarRunnable = new Runnable() {
        @Override
        public void run() {
//            Log.i(TAG, "staticCarRunnable handler called...");
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

    private void startCarAnimation(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

//        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.new_car_small)));
        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
                .flat(true).icon(BitmapDescriptorFactory.fromResource(getMapIcon(colorCar))));

        index = -1;
        next = 1;
        handler.postDelayed(staticCarRunnable, 3000);
    }

    void stopRepeatingTask() {

        if (staticCarRunnable != null) {
            handler.removeCallbacks(staticCarRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);
        //googleMap.getUiSettings().setZoomControlsEnabled(true);

        dynamicPolyLine();
        startGettingOnlineDataFromCar();

    }


    private void getDriverLocationUpdate(String token, String vehicle_id) {
//        Log.d(TAG, "token : " + token);
//        Log.d(TAG, "vehicle_id : " + vehicle_id);

        Call<ShowLocationResponse> call = apiInterface.showLocation(token,vehicle_id);
        call.enqueue(new Callback<ShowLocationResponse>() {
            @Override
            public void onResponse(Call<ShowLocationResponse> call, retrofit2.Response<ShowLocationResponse> response) {
//                Log.d("", response.toString());
                // pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, " Response >>>>" + str_response);
                int responseCode = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);

                try {
                    if (response.isSuccessful()) {
//                        int  responseCode  = response.code();
                        ShowLocationResponse locationResponse = response.body();
                        int status = locationResponse.getStatus();
//                        Log.e(TAG, "status :" + status);
                        if (status == 200) {
//                            Log.d("Junaid", locationResponse.getMessage());

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
//                            Log.i(TAG, "signal_strength : " + signal_strength);
//                            Log.i(TAG, "soccermomLatitude : " + soccermomLatitude);
//                            Log.i(TAG, "soccermomLongitude : " + soccermomLongitude);

                            // TODO: 1/1/2020
                            startLatitude = Double.valueOf(soccermomLatitude);
                            startLongitude = Double.valueOf(soccermomLongitude);

//                            Log.d(TAG, "JUNAID: "+startLatitude + "--" + startLongitude);
                            tv_speed.setText(speed);
                            getAddress(startLatitude, startLongitude);

                            // strt from animation and direction
                            if (isFirstPosition) {
                                startPosition = new LatLng(startLatitude, startLongitude);

//                                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).flat(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.new_car_small)));
                                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                                        flat(true).icon(BitmapDescriptorFactory.fromResource(getMapIcon(colorCar))));
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

//                                Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);

                                if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {

                                    Log.e(TAG, "NOT SAME");
                                    startBikeAnimation(startPosition, endPosition);

                                } else {

                                    Log.e(TAG, "SAMME");
                                }
                            }


                         /*   liveLocation = new LatLng(soccermomLatitude, soccermomLongitude);
                            if (count == 1) {
                                StartPoint = new LatLng(soccermomLatitude, soccermomLongitude);
                                count++;
                            }
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
//                    Log.e(TAG, "Massage :" + e.toString());
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

    private void getAddress(double latitude, double longitude) {
//        Log.e(TAG, "getAddress latitude:" + latitude);
//        Log.e(TAG, "getAddress longitude :" + longitude);
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
//                Log.e(TAG,"getAddress Address :"+bookAddress);
                tv_location.setText(bookAddress);
                AppPreferences.savePreferences(mContext, "ADDRESS_LOCATION", bookAddress);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private int getMapIcon(String colorCar){
        switch (colorCar) {
            case "green":
                return  R.drawable.car_grey;

            case "red":
                return  R.drawable.car_red;

            case "grey":
                return  R.drawable.car_grey;

            case "yellow":
                return R.drawable.car_yellow;

            default:
                return  R.drawable.car_green;
        }
    }




}

