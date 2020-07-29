package com.backstagesupporters.fasttrack.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.ui.fragment.VehicleFragment;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.google.android.material.textfield.TextInputEditText;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;

import es.dmoral.toasty.Toasty;

public class AddVehicleActivity extends BaseActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private ImageView imageViewCar;
    TextView tv_take_image,tv_add;
    TextInputEditText edit_vehicleBrand,edit_vehicleName,edit_vehicleType,edit_vehicleNumber,edit_OwnerName;
    String vehicleBrand,vehicleName,vehicleType,vehicleNumber,OwnerName;

    private ProgressDialog pd;
    //=Interface Declaration
    ApiInterface apiInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        mContext=AddVehicleActivity.this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }


        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.vehicleAdd));
        iv_tool_back_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tv_add.setClickable(false);
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doValidation();
                Log.i(TAG,"called tv_add");
            }
        });

        tv_take_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });


    }


    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        imageViewCar = findViewById(R.id.iv_carPic);

        edit_vehicleBrand = findViewById(R.id.edit_vehicleBrand);
        edit_vehicleName = findViewById(R.id.edit_vehicleName);
        edit_vehicleType = findViewById(R.id.edit_vehicleType);
        edit_vehicleNumber = findViewById(R.id.edit_vehicleNumber);
        edit_OwnerName = findViewById(R.id.edit_OwnerName);

        tv_take_image = findViewById(R.id.tv_take_image);
        tv_add = findViewById(R.id.tv_add);
    }


    private void doValidation() {
        vehicleBrand =edit_vehicleBrand.getText().toString().trim();
        vehicleName =edit_vehicleName.getText().toString().trim();
        vehicleType =edit_vehicleType.getText().toString().trim();
        vehicleNumber =edit_vehicleNumber.getText().toString().trim();
        OwnerName =edit_OwnerName.getText().toString().trim();

//        AppPreferences.savePreferences(mContext, "EMAIL", email);

        if (vehicleBrand.equalsIgnoreCase("")) {
            edit_vehicleBrand.setError(getString(R.string.Please_Enter_deviceSrNumber));
            edit_vehicleBrand.requestFocus();
        } else if (vehicleName.equalsIgnoreCase("")) {
            edit_vehicleName.setError(getString(R.string.Please_Enter_license_number));
            edit_vehicleName.requestFocus();
        } else if (vehicleType.equalsIgnoreCase("")) {
            edit_vehicleType.setError(getString(R.string.Please_Enter_driver_name));
            edit_vehicleType.requestFocus();
        }else if (vehicleNumber.isEmpty()){
            edit_vehicleNumber.setError(getString(R.string.err_msg_mobile));
            edit_vehicleNumber.requestFocus();
        }else if (OwnerName.isEmpty()) {
            edit_OwnerName.setError(getString(R.string.err_msg_mobile_too_short));
            edit_OwnerName.requestFocus();
        }  else {

            //   deviceSrNumber,license_number,driver_name,phone_number,device_no,sim_no;
            // ========== Api Call =============
            if (CheckNetwork.isNetworkAvailable(mContext)) {
//                getAddVehicleResponseCall(vehicleBrand,vehicleName,vehicleType,vehicleNumber,OwnerName);
                Toasty.success(mContext,"Successfully !!", Toast.LENGTH_SHORT).show();

//                replaceFragment(new HomeDashboardFragment());
                new VehicleFragment();

            } else {
                Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();

            }
        }
    }

    // takePicture
    private void takePicture() {
        Album.camera(this)
                .image()
//                .filePath()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Log.d(TAG,"imageString :"+result);
                        Album.getAlbumConfig()
                                .getAlbumLoader()
                                .load(imageViewCar, result);
//                        bitmap = StringToBitMap(result);
//                        uploadImage(token, bitmap);

                        // make Clickable
                        if (result!=null){
                            tv_add.setClickable(false);
                        }
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toasty.error(mContext, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();

    }

    private void getAddVehicleResponseCall(String vehicleBrand, String vehicleName, String vehicleType, String vehicleNumber, String ownerName) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }



}
