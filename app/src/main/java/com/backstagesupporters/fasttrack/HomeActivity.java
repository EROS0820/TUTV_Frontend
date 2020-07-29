package com.backstagesupporters.fasttrack;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backstagesupporters.fasttrack.models.User;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.LoginResponse;
import com.backstagesupporters.fasttrack.responseClass.LogoutResponse;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.service.MyService;
import com.backstagesupporters.fasttrack.ui.activity.ComplaintActivity;
import com.backstagesupporters.fasttrack.ui.activity.DistanceReportActivity;
import com.backstagesupporters.fasttrack.ui.activity.LiveTrackingAllActivity;
import com.backstagesupporters.fasttrack.ui.history.HistoryReplayActivity;
import com.backstagesupporters.fasttrack.ui.activity.LiveTrackingActivity;
import com.backstagesupporters.fasttrack.ui.activity.ProfileActivity;
import com.backstagesupporters.fasttrack.ui.activity.RoadSideAssistanceActivity;
import com.backstagesupporters.fasttrack.ui.activity.SettingsActivity;
import com.backstagesupporters.fasttrack.ui.activity.TripReportActivity;
import com.backstagesupporters.fasttrack.ui.adapters.HeaderMenuAdapter;
import com.backstagesupporters.fasttrack.ui.fragment.DeviceFragment;
import com.backstagesupporters.fasttrack.ui.fragment.DriverFragment;
import com.backstagesupporters.fasttrack.ui.activity.SubUserActivity;
import com.backstagesupporters.fasttrack.ui.activity.SpeedLimitActivity;
import com.backstagesupporters.fasttrack.ui.fragment.SupportFragment;
import com.backstagesupporters.fasttrack.ui.fragment.VehicleFragment;
import com.backstagesupporters.fasttrack.models.HeadMenuModel;
import com.backstagesupporters.fasttrack.responseClass.ProfileInfoResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.user.LoginsActivity;
import com.backstagesupporters.fasttrack.utils.AppConstants;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ItemClickListener;
import com.backstagesupporters.fasttrack.utils.UserStatusAccount;
import com.backstagesupporters.fasttrack.utils.language.LanguageHelper;
import com.backstagesupporters.fasttrack.utils.OnResponse;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.BuildConfig;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements View.OnClickListener , ItemClickListener {
    private String TAG= getClass().getSimpleName();
    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navView;
    public static TextView tv_title;
    private Toolbar toolbar;
    private ImageView iv_refresh,iv_allMarkers;
    private LinearLayout ll_close, ll_nav_home,ll_nav_insurance,ll_nav_driver,ll_nav_vehicle,ll_nav_geoFencing,ll_nav_support,
            ll_nav_subUser, ll_nav_complaint,ll_nav_about,ll_nav_device, ll_nav_report,ll_nav_RSA,
            ll_nav_logout, ll_nav_history_replay,ll_nav_toll,ll_nav_distance_report,ll_nav_trip_report, ll_nav_speedLimit;
    private LinearLayout ll_header_profile_pic, ll_header_profile, ll_header_settings;
    private CircleImageView iv_profile_pic_head;
    private TextView tv_toll, tv_user_name_head, tv_mobile_number_head;
    public static FloatingActionButton fabReferesh;
    private RecyclerView recyclerView;
    private HeaderMenuAdapter adapter;
    private static List<HeadMenuModel> headMenuModelList = new ArrayList();
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private RelativeLayout buttonLayout,relative_report;
    private TextView tv_report_head;
    public ExpandableLinearLayout expandableLayout;

    private String user_name,user_email,dob,gender,mobile,profession,profile_pic,email_verification,mobile_verification;
    private int  responseCode;
    private ApiInterface apiInterface;
    private ProgressDialog pd;
    private String token;
    private Handler mHandlerFragment;
    String phone_insurance = "+918295084488";
    private final String REFRESH_INTERVAL = "10";



    //====== App logout ======
    private boolean dialogStatus = false, doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    @Override
    protected void attachBaseContext(Context newBase) {
//        Log.e(TAG,"LanguageHelper: "+ LanguageHelper.onAttach(newBase));
//        Log.e(TAG,"LanguageHelper: "+LanguageHelper.getLanguage(newBase));
        super.attachBaseContext(LanguageHelper.onAttach(newBase));

//        MyApplication.localeManager.setLocale(newBase);
//        Log.d(TAG, "attachBaseContext");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_home);
        mContext = HomeActivity.this;

        // TODO: 3/28/2020
        //active screen
//        final Window win= getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        findViewById();
        setClickListner();

        AppPreferences.savePreferences(mContext, VariablesConstant.REFRESH_INTERVAL,REFRESH_INTERVAL);
        AppConstants.ChangeStatusBarColor(HomeActivity.this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);

        // TODO: 4/1/2020  
        // userVisibility
        String userType = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_TYPE);
        userVisibility(userType);
//        Log.d(TAG, "userType: "+userType);

        // =================== Drawer Info == ActionBar - AppCompatActivity ======================
        setSupportActionBar(toolbar);
        toolbar.setTitle("");       // Remove title in Toolbar

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // for enable or disable home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // HomeButtonEnabled in Toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        // Remove title in Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // set DrawerToggle icon in Toolbar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.navigation_menu);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.home_g1);

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        checkPermissions();
        requestPermissions();

        ll_close.setOnClickListener(v -> onBackPressed());


        if (CheckNetwork.isNetworkAvailable(mContext)) {
            //========== Api Call ==============
            getProfileInfo(token);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }

        // ExpandableLinearLayout head
        setAdapter();

    }

    private void setClickListner() {
        ll_nav_toll.setOnClickListener(this);
        ll_nav_home.setOnClickListener(this);
        ll_nav_distance_report.setOnClickListener(this);
        ll_nav_history_replay.setOnClickListener(this);
        ll_nav_trip_report.setOnClickListener(this);
        ll_nav_driver.setOnClickListener(this);
        ll_nav_subUser.setOnClickListener(this);
        ll_nav_vehicle.setOnClickListener(this);
        ll_nav_geoFencing.setOnClickListener(this);
        ll_nav_support.setOnClickListener(this);
        ll_nav_complaint.setOnClickListener(this);
        ll_nav_about.setOnClickListener(this);
        ll_close.setOnClickListener(this);
        ll_nav_insurance.setOnClickListener(this);
        ll_nav_logout.setOnClickListener(this);
        ll_nav_device.setOnClickListener(this);
        ll_nav_report.setOnClickListener(this);
        ll_nav_RSA.setOnClickListener(this);
        ll_nav_speedLimit.setOnClickListener(this);

        ll_header_profile_pic.setOnClickListener(this);
        ll_header_profile.setOnClickListener(this);
        ll_header_settings.setOnClickListener(this);
    }

    // TODO: 3/5/2020  
    private void setUserProfile() {
        String userName = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_NAME);
        String userMobile = AppPreferences.loadPreferences(mContext, VariablesConstant.MOBILE);
        String userEmail = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_EMAIL);

        tv_user_name_head.setText(userName);   // user_name
        tv_mobile_number_head.setText(userMobile); // mobile
        //   tv_user_name1.setText(userEmail);
        // profile_pic
        String image_url = VariablesConstant.URL_PICTURE + profile_pic;
//        Log.w(TAG, "URL_PICTURE :" + image_url);
        Glide.with(mContext)
                .load(image_url)
                .placeholder(R.drawable.profile_default)
                .into(iv_profile_pic_head);
    }


    private void findViewById() {
        toolbar = findViewById(R.id.toolbar);

        iv_refresh = findViewById(R.id.iv_refresh);
        iv_allMarkers = findViewById(R.id.iv_allMarkers);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        ll_close = findViewById(R.id.ll_close);
        tv_title = findViewById(R.id.tv_title);
        fabReferesh = findViewById(R.id.fabReferesh);

        // nav_menu mDrawerLayout
        ll_nav_toll = mDrawerLayout.findViewById(R.id.ll_nav_toll);
        ll_nav_home = navView.findViewById(R.id.ll_nav_home);
        ll_nav_insurance = navView.findViewById(R.id.ll_nav_insurance);
        ll_nav_distance_report = findViewById(R.id.ll_nav_distance_report);
        ll_nav_history_replay = findViewById(R.id.ll_nav_history_replay);
        ll_nav_trip_report = findViewById(R.id.ll_nav_trip_report);
        ll_nav_driver = findViewById(R.id.ll_nav_driver);
        ll_nav_subUser = findViewById(R.id.ll_nav_subUser);
        ll_nav_vehicle = findViewById(R.id.ll_nav_vehicle);
        ll_nav_support = findViewById(R.id.ll_nav_support);
        ll_nav_complaint = findViewById(R.id.ll_nav_complaint);
        ll_nav_geoFencing = findViewById(R.id.ll_nav_geoFencing);
        ll_nav_about = findViewById(R.id.ll_nav_about);
        ll_nav_logout = findViewById(R.id.ll_nav_logout);
        ll_nav_device = findViewById(R.id.ll_nav_device);
        tv_toll = findViewById(R.id.tv_toll);
        ll_nav_report = findViewById(R.id.ll_nav_report);
        ll_nav_RSA = findViewById(R.id.ll_nav_RSA);
        ll_nav_speedLimit = findViewById(R.id.ll_nav_speedLimit);

        ll_header_profile_pic = findViewById(R.id.ll_header_profile_pic);
        ll_header_profile = findViewById(R.id.ll_header_profile);
        ll_header_settings = findViewById(R.id.ll_header_settings);
        iv_profile_pic_head = findViewById(R.id.iv_profile_pic_head);
        tv_user_name_head = findViewById(R.id.tv_user_name_head);
        tv_mobile_number_head = findViewById(R.id.tv_mobile_number_head);
        recyclerView = findViewById(R.id.recycler_nav_report);
        tv_report_head = findViewById(R.id.tv_report_head);
        relative_report = findViewById(R.id.relative_report);
        buttonLayout =  findViewById(R.id.btnUpDown_head);
        expandableLayout =  findViewById(R.id.expandableLayout_head);
    }


    // HeadMenuM Adapter
    private void setAdapter() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);
        headMenuModelList = getDummyData();
        adapter = new HeaderMenuAdapter(mContext,getDummyData());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setClickListener(this); // Bind the listener
    }

    // HeadMenuM list
    private List<HeadMenuModel> getDummyData() {
        List<HeadMenuModel> list = new ArrayList<HeadMenuModel>();
        list.add(new HeadMenuModel(getString(R.string.distance_report),R.drawable.news_feed_w, Utils.createInterpolator(Utils.ACCELERATE_DECELERATE_INTERPOLATOR)));
        list.add(new HeadMenuModel(getString(R.string.trip_report),R.drawable.ic_car_trip, Utils.createInterpolator(Utils.ACCELERATE_DECELERATE_INTERPOLATOR)));
        return list;
    }



    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "MyLife onStart");

        /**
         * "account_status": "active",
         *  "account_status": "deactive",
         *  // TODO: 4/1/2020
         */
        UserStatusAccount.checkUserStatus(this,TAG);

        setUserProfile();
        // RefreshFragment
//        autoRefreshFragment();

        // TODO: 1/31/2020
//        tv_title.setText(getString(R.string.dashboard));
//        replaceFragment(new HomeDashboardFragment());
//        replaceFragment(new HomeDashboardFragmentNew());
        tv_title.setText(getString(R.string.vehicle_list));
        replaceFragment(new VehicleFragment());

        mDrawerLayout.closeDrawer(GravityCompat.START);
        overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);

        //refresh();
        fabReferesh = findViewById(R.id.fabReferesh);
        fabReferesh.setOnClickListener(view -> {
            startActivity(new Intent(mContext, HomeActivity.class));
            finish();
        });

        iv_refresh.setOnClickListener(view -> {
            startActivity(new Intent(mContext, HomeActivity.class));
            finish();
        });


    }


    public ImageView getImageViewAllMarkers() {
        ImageView  iv_allMarkers = findViewById(R.id.iv_allMarkers);
        Log.d(TAG, "MyLife All Markers");
        return iv_allMarkers;
    }

    public FloatingActionButton getFloatingActionButton() {
        FloatingActionButton  fab= (FloatingActionButton)findViewById(R.id.fabReferesh);
        return fab;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "MyLife onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();

        // stop the Handler - Fragment  - TODO: 3/5/2020
//        mHandlerFragment.removeCallbacks(m_RunnableFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "MyLife onPause");
    }

    // TODO: 3/27/2020
    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.d(TAG, "MyLife onRestart");
        if (AppPreferences.loadPreferences(mContext,VariablesConstant.VehicleFragment).equalsIgnoreCase("1")){
            startActivity(new Intent(mContext, SplashActivity.class));
            finish();
        }
    }

    private void autoRefreshFragment() {
        mHandlerFragment = new Handler();
        m_RunnableFragment.run();
    }

    private final Runnable m_RunnableFragment = new Runnable() {
        public void run() {
            String refreshTime = AppPreferences.loadPreferences(mContext, VariablesConstant.REFRESH_INTERVAL);
//            Log.w(TAG, "Refresh Interval Time :" + refreshTime);
            if (!refreshTime.isEmpty()){
              int REFRESH_TIME = getRefreshTime(refreshTime);
//                Log.i(TAG, "Refresh Interval REFRESH_TIME :" + REFRESH_TIME);

                // Reload current fragment
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                if (currentFragment instanceof VehicleFragment) {
                    FragmentTransaction fragTransaction =   getSupportFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);
                    fragTransaction.commit();
                }

                mHandlerFragment.postDelayed(m_RunnableFragment,REFRESH_TIME);
            }
        }
    };

    /**
     * {getString(R.string.second_15),getString(R.string.second_30),getString(R.string.second_45),
     * getString(R.string.minute_1),getString(R.string.minute_2),getString(R.string.minute_3)};
     * 1 Second -> 1000 miliSeconds;
     */
    private int getRefreshTime(String time) {
        int mTime = Integer.parseInt(time);
        int miliSeconds = 1000;
        int myTime = mTime*miliSeconds;
//        Log.e(TAG,"getRefreshTime myTime: "+myTime);
        return myTime;
    }



    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }



    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {

            Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
//                mService.requestLocationUpdates();
//                Log.i(TAG, "User interaction was OK Permission.");
            } else {
                // Permission denied.

                Snackbar.make(
                        findViewById(R.id.drawer_layout),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }



    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
//        HeadMenuModel model = headMenuModelList.get(position);
//        Log.i(TAG,"Welcome: "+model.getTitle());
//        Intent intent = new Intent(mContext, HomeActivity.class);
////        i.putExtra("city", model.name);
////        i.putExtra("desc", model.description);
////        i.putExtra("image", model.imageName);
//        startActivity(intent);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_nav_home:
//                tv_title.setText(getString(R.string.live_tracking));
//                iv_setting.setVisibility(View.GONE);
//                replaceFragment(new HomeDashboardFragment());
//                replaceFragment(new HomeDashboardFragmentNew());
//                replaceFragment(new HomeDashboardFragmentMy());
                startActivity(new Intent(mContext, HomeActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_support:
                tv_title.setText(getString(R.string.support));
                replaceFragment(new SupportFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_trip_report:
//                tv_title.setText(getString(R.string.trip_report));
                startActivity(new Intent(mContext, TripReportActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                finish();
                break;

            case R.id.ll_nav_complaint:
                startActivity(new Intent(mContext, ComplaintActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
                break;

            case R.id.ll_nav_subUser:
//                tv_title.setText(getString(R.string.sub_user));
                startActivity(new Intent(mContext,SubUserActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                finish();
                break;

            case R.id.ll_nav_vehicle:
                tv_title.setText(getString(R.string.vehicle_list));
                replaceFragment(new VehicleFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_speedLimit:
                startActivity(new Intent(mContext,SpeedLimitActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_driver:
                tv_title.setText(getString(R.string.driver));
                replaceFragment(new DriverFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_about:
                tv_title.setText(getString(R.string.about));
//                replaceFragment(new HomeLiveFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_logout:
                String user_id = AppPreferences.loadPreferences(mContext,VariablesConstant.USER_ID);
                logOutApiCall(user_id,TAG);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.ll_nav_insurance:
                callTollMethod(phone_insurance);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.ll_nav_toll:
                String phone_toll = tv_toll.getText().toString().trim();
                callTollMethod(phone_toll);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.ll_nav_history_replay:
                startActivity(new Intent(mContext, HistoryReplayActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_distance_report:
                startActivity(new Intent(mContext, DistanceReportActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_device:
                tv_title.setText(getString(R.string.device));
                replaceFragment(new DeviceFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_nav_RSA:
                startActivity(new Intent(mContext, RoadSideAssistanceActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                break;

            case R.id.ll_header_profile:
                // replaceFragment(new ProfileFragment());
                startActivity(new Intent(mContext, ProfileActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                finish();
                break;

            case R.id.ll_header_settings:
                startActivity(new Intent(mContext, SettingsActivity.class));
                overridePendingTransition(R.anim.up_from_bottom, R.anim.up_from_bottom);
                finish();
                break;

            case R.id.ll_nav_report:
                int position =1;
                myExpandableLinearLayoutClick(position);
                onClickButton(expandableLayout);
                break;
        }
    }






    // TODO: 4/2/2020
    private void userVisibility(String userType) {
//        Log.i(TAG,"userVisibility userType : "+userType);
        if (userType.equalsIgnoreCase("admin"))
        {
//            Log.i(TAG,"userVisibility called admin");
            ll_nav_support.setVisibility(View.GONE);
            ll_nav_geoFencing.setVisibility(View.GONE);
            ll_nav_subUser.setVisibility(View.GONE);

            ll_nav_device.setVisibility(View.VISIBLE);
            ll_nav_driver.setVisibility(View.VISIBLE);
        }
        else if (userType.equalsIgnoreCase("user"))
        {
//            Log.i(TAG,"userVisibility called user");
            ll_nav_device.setVisibility(View.GONE);
            ll_nav_support.setVisibility(View.GONE);
            ll_nav_geoFencing.setVisibility(View.GONE);

            ll_nav_subUser.setVisibility(View.VISIBLE);
            ll_nav_driver.setVisibility(View.VISIBLE);
        }
        else if (userType.equalsIgnoreCase("sub")){
//            Log.i(TAG,"userVisibility called sub");
            ll_nav_device.setVisibility(View.GONE);
            ll_nav_support.setVisibility(View.GONE);
            ll_nav_geoFencing.setVisibility(View.GONE);
            ll_nav_subUser.setVisibility(View.GONE);
            ll_nav_driver.setVisibility(View.GONE);
        }
    }


    private void myExpandableLinearLayoutClick(int position) {
        expandableLayout.setInterpolator(Utils.createInterpolator(Utils.ACCELERATE_INTERPOLATOR));
        expandableLayout.setExpanded(expandState.get(position));
        expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                createRotateAnimator(buttonLayout, 0f, 90f).start();
                expandState.put(position, true);
            }

            @Override
            public void onPreClose() {
                createRotateAnimator(buttonLayout, 90f, 0f).start();
                expandState.put(position, false);
            }
        });

        buttonLayout.setRotation(expandState.get(position) ? 90f : 0f);
        buttonLayout.setOnClickListener(v -> {
            onClickButton(expandableLayout);
        });

    }


    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    private void callTollMethod(String phone) {
//        Log.e(TAG,"phone :"+phone);
        if (phone!=null){
            Intent intent = new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse("tel:0123456789"));
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }else {
            Log.e(TAG,"Number not found");
        }
    }


    public void logOutApiCall(String userId, String tag) {
        Call<LogoutResponse> call = apiInterface.logout(userId);
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                String str_response = new Gson().toJson(response.body());
                Log.w(tag, "Response >>>>" + str_response);
                try {
                    if (response.code() == 200) {
                        LogoutResponse loginResponse = response.body();

                        logOut(mContext);

//                            LoginResponse.Data data = loginResponse.getData();
//                            String token = data.getToken();
//                            User user = data.getUser();
//                            account_status = user.getAccountStatus();
//
//                            AppPreferences.savePreferences(mContext, "TOKEN", token);
//                            AppPreferences.savePreferences(mContext, "USER_STATUS", account_status);
//                            Log.w(TAG, "account_status : "+account_status);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
//                Toast.makeText(MyService.this, "" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logOut(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.app_logout, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        TextView btn_cancel = dialogView.findViewById(R.id.tv_cancel);
        TextView btn_yes = dialogView.findViewById(R.id.tv_logout);
        btn_yes.setOnClickListener(v -> {

            // FirebaseAuth.getInstance().signOut();
            AppPreferences.savePreferences(context, VariablesConstant.TOKEN, "");
            AppPreferences.savePreferences(context, VariablesConstant.LOGIN_STATUS, "0");
            AppPreferences.savePreferences(context, VariablesConstant.EMAIL, "");
            AppPreferences.savePreferences(context, VariablesConstant.PASSWORD, "");

            Intent intent = new Intent(context, LoginsActivity.class);
//                Intent intent = new Intent(mContext, WelcomeActivity.class);
            //    Works with API >= 11
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
            alertDialog.dismiss();

        });

        btn_cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

    }

    private void replaceFragment (Fragment fragment){
        String fragmentTag =  fragment.getClass().getName();
//        Log.e(TAG, "replaceFragment fragmentTag: " + fragmentTag);
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (fragmentTag, 0);
//        Log.e(TAG, "replaceFragment fragmentPopped: " + fragmentPopped);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_layout, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(fragmentTag);
            ft.commit();
        }
    }



    //================ Api Call ============================
    private void getProfileInfo(final String token) {
        /*pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();*/

        Call<ProfileInfoResponse> call = apiInterface.profileInfo(token);

        call.enqueue(new Callback<ProfileInfoResponse>() {
            @Override
            public void onResponse(Call<ProfileInfoResponse> call, Response<ProfileInfoResponse> response) {
              //  pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getProfileInfo Response >>>>" + str_response);
                responseCode  = response.code();
                try {
                    if (responseCode ==200 && response.isSuccessful()) {
                        ProfileInfoResponse profileInfoResponse = response.body();
                        assert profileInfoResponse != null;
                        int success = Integer.parseInt(profileInfoResponse.getStatus());
                        if (success==200) {
                            ProfileInfoResponse.Profile arrayList = profileInfoResponse.getProfile();
                            user_name = arrayList.getName();
                            user_email = arrayList.getEmail();
                            dob = arrayList.getDob();
                            gender = arrayList.getGender();
                            mobile = arrayList.getMobile();
                            profession = arrayList.getProfession();
                            profile_pic = arrayList.getProfile_pic();
                            email_verification = arrayList.getEmail_verification();
                            mobile_verification = arrayList.getMobile_verification();

                            AppPreferences.savePreferences(mContext, VariablesConstant.USER_NAME, user_name);
                            AppPreferences.savePreferences(mContext, VariablesConstant.USER_EMAIL, user_email);
                            AppPreferences.savePreferences(mContext, VariablesConstant.DOB, dob);
                            AppPreferences.savePreferences(mContext, VariablesConstant.GENDER, gender);
                            AppPreferences.savePreferences(mContext, VariablesConstant.MOBILE, mobile);
                            AppPreferences.savePreferences(mContext, VariablesConstant.PROFESSION, profession);
                            AppPreferences.savePreferences(mContext, VariablesConstant.PROFILE_PIC, profile_pic);
                            AppPreferences.savePreferences(mContext, VariablesConstant.EMAIL_VERIFICATION, email_verification);
                            AppPreferences.savePreferences(mContext, VariablesConstant.MOBILE_VERIFICATION, mobile_verification);
                        } else {
                            Log.d(TAG, "responseCode :" + responseCode);
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,"get Profile Info Massage :"+e.toString());
                }
            }

            @Override
            public void onFailure(Call<ProfileInfoResponse> call, Throwable t) {
//                if (pd!=null)
//                pd.dismiss();
//                Log.e(TAG,"onFailure"+t.toString());

                if (responseCode==401) {
                    AppConstants.getLoginResponseCall(mContext, new OnResponse() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>1){
            super.onBackPressed();
        }else{
//            finish();
            doubleBackPressLogic();
        }
    }


    //====End Double tab back press logic
    private void doubleBackPressLogic() {
        if (doubleBackToExitPressedOnce) {
            if (dialogStatus) {
                moveTaskToBack(true);
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toasty.info(mContext, "Please click back again to exit !!", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(mRunnable, 1000);

    }




}
