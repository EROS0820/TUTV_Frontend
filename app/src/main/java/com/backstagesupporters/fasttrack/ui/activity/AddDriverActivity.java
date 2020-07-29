package com.backstagesupporters.fasttrack.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.models.Driver;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.VehiclesResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ImageUtils;
import com.backstagesupporters.fasttrack.utils.RoundedImageView;
import com.backstagesupporters.fasttrack.utils.UserStatusAccount;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.backstagesupporters.fasttrack.utils.vehicle.SoftKeyBoard;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

public class AddDriverActivity extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private TextInputEditText edt_name,edt_mobileno,edt_email,edt_pincode,edit_pan_no,edit_adhaar_number, edit_license_number;
    private TextView tv_id_prof_image,tv_pan_card_image,tv_adhaar_card_image,tv_driving_licence_image,tv_dob,tv_add;
    private RelativeLayout rl_id_prof_image,rl_pan_card_image,rl_adhaar_card_image,rl_driving_licence_image;
    private Spinner genderSpinner,spinner_city,spinnerCountry,spinnerState;
    private CircleImageView user_profile_pic;
    private RoundedImageView imageProfilePic;
    private ImageView  imageViewChange;
    public static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath = "";

    private Spinner vehicleSpinner;
    String vehicle_id, vehicle_brand,vehicle_name,vehicle_type,vehicle_no,vehicle_image;
    List<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
    private ArrayList myVehicleListSpinner = new ArrayList();
    private MultiSpinnerSearch searchMultiSpinnerUnlimited;

    int mYear, mMonth, mDay;
    String name,phone_number,dob,email,city,pincode,gender,pan_no,adhaar_number,license_number,country,state,
            id_prof_image,pan_card_image,adhaar_card_image,driving_licence_image;
    String profile_pic;
    String token, userId;

    //=Interface Declaration
    private ProgressDialog pd;
    ApiInterface apiInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);
        mContext=AddDriverActivity.this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userId = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_ID);
//        Log.e(TAG, "token :" + token);

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.driverAdd));
        iv_tool_back_left.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        imageViewChange.setOnClickListener(this);
        rl_id_prof_image.setOnClickListener(this);
        rl_pan_card_image.setOnClickListener(this);
        rl_adhaar_card_image.setOnClickListener(this);
        rl_driving_licence_image.setOnClickListener(this);
        imageViewChange.setOnClickListener(this);
        tv_dob.setOnClickListener(this);

        //genderSpinner,
        setSpinnerGender();
        setSpinner_city();
        setSpinner_state();
        setSpinner_country();

        // ======= API Call ==========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            getVehicleCall(token, userId);
        } else {
            Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
        }


//        SoftKeyBoard.hideKeyboard(this);

    }


    @Override
    protected void onStart() {
        super.onStart();

        /**
         * "account_status": "active",
         *  "account_status": "deactive",
         *  // TODO: 4/1/2020
         */
        UserStatusAccount.checkUserStatus(this,TAG);
    }

    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        edt_name = findViewById(R.id.edt_name);
        edt_mobileno = findViewById(R.id.edt_mobileno);
        tv_dob = findViewById(R.id.tv_dob);
        edt_email = findViewById(R.id.edt_email);
        edt_pincode = findViewById(R.id.edt_pincode);
        edit_pan_no = findViewById(R.id.edit_pan_no);
        edit_adhaar_number = findViewById(R.id.edit_adhaar_number);
        edit_adhaar_number = findViewById(R.id.edit_adhaar_number);
        edit_license_number = findViewById(R.id.edit_license_number);
        tv_id_prof_image = findViewById(R.id.tv_id_prof_image);
        tv_pan_card_image = findViewById(R.id.tv_pan_card_image);
        tv_adhaar_card_image = findViewById(R.id.tv_adhaar_card_image);
        tv_driving_licence_image = findViewById(R.id.tv_driving_licence_image);
        spinner_city = findViewById(R.id.spinner_city);
        genderSpinner = findViewById(R.id.genderSpinner);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinnerState = findViewById(R.id.spinnerState);
        user_profile_pic = findViewById(R.id.imageViewUser_profile_pic);
        imageProfilePic = findViewById(R.id.imageProfilePic);
        rl_id_prof_image = findViewById(R.id.rl_id_prof_image);
        rl_pan_card_image = findViewById(R.id.rl_pan_card_image);
        rl_adhaar_card_image = findViewById(R.id.rl_adhaar_card_image);
        rl_driving_licence_image = findViewById(R.id.rl_driving_licence_image);
        imageViewChange = findViewById(R.id.imageView2);
        tv_add = findViewById(R.id.tv_add);
        vehicleSpinner = findViewById(R.id.vehicleSpinner);
        searchMultiSpinnerUnlimited = findViewById(R.id.searchMultiSpinnerUnlimited);

        // TODO: 1/29/2020
        user_profile_pic.setOnLongClickListener(v -> {
            CheckPermissions();
            showDialogImage();
            return true;
        });

        imageProfilePic.setOnLongClickListener(v -> {
            CheckPermissions();
            showDialogImage();
            return true;
        });

    }




    // TODO: 12/29/2019
    private void CheckPermissions() {
        RxPermissions.getInstance(mContext)
                .request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
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

    private void showDialogImage() {
        ImageButton btn_camera,btn_gallery;

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.custom_dialog_image_pick); //layout
        dialog.setTitle(getString(R.string.album_check_album_little));

        // set the custom dialog components - text, image and button
        btn_gallery =  dialog.findViewById(R.id.btn_gallery);
        btn_camera =   dialog.findViewById(R.id.btn_camera);

        // if button is clicked, close the custom dialog
        btn_gallery.setOnClickListener(v -> {
            imageProfilePic.setVisibility(View.VISIBLE);
            user_profile_pic.setVisibility(View.GONE);
            gallery();

            dialog.dismiss();
        });

        btn_camera.setOnClickListener(v -> {
            imageProfilePic.setVisibility(View.GONE);
            user_profile_pic.setVisibility(View.VISIBLE);
            takePicture2();
            dialog.dismiss();
        });
        dialog.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                String urlPic = data.toString();
//                Log.w(TAG,"urlPic : "+urlPic);
                selectedImagePath = getAbsolutePath(data.getData());
//                Log.e(TAG,"selectedImagePath : "+selectedImagePath);
//                imageProfilePic.setImageBitmap(decodeFile(selectedImagePath));
                Glide.with(mContext)
                        .load(selectedImagePath)
                        .placeholder(R.drawable.profile_default)
                        .into(imageProfilePic);
            }
        }
       /* if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Log.w(TAG, String.valueOf(bitmap));
                    imageProfilePic.setImageBitmap(bitmap);
//                    imageProfilePic2.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_add:
                doValidation();
                break;

            case R.id.iv_tool_back_left:
                onBackPressed();
                break;

            case R.id.tv_dob:
                getDobDate();
                break;

            case R.id.imageView2:
                // user_profile_pic imageViewChange
                takePicture2();
                break;

            case R.id.rl_driving_licence_image:
                takePictureDriving_licence();

                break;
            case R.id.rl_id_prof_image:
                takePicture_id_prof();
                //loadPic(profile_picResult);
                break;
            case R.id.rl_pan_card_image:
                takePicture_pan_card();
                break;
            case R.id.rl_adhaar_card_image:
                takePicture_adhaar_card();
                break;
        }
    }


    // Select Gender
    private void setSpinnerGender() {
        String[] genderArray = {getString(R.string.selectGender), getString(R.string.male), getString(R.string.female)};
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, genderArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        genderSpinner.setAdapter(spinnerArrayAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals(getString(R.string.selectGender))) {
//                    Toast.makeText(mContext, getString(R.string.selectGender), Toast.LENGTH_SHORT).show();
//                    Log.i(TAG,"gender selectedItem "+selectedItem);
                }else {
                    gender = parent.getItemAtPosition(position).toString();
//                    Log.i(TAG,"gender "+gender);
//                    Log.i(TAG,"gender selectedItem "+selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Log.i(TAG,"gender parent "+parent.toString());
            }
        });
    }


    private void setSpinner_city() {
        String[] cityArray = {getString(R.string.selectCity), "Delhi", "New Delhi","Noida","Gurgaon","Meerut"};

        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, cityArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_city.setAdapter(spinnerArrayAdapter);

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals(getString(R.string.selectGender))) {
//                    Toasty.error(mContext, getString(R.string.selectCity), Toast.LENGTH_SHORT).show();
//                    Log.i(TAG,"city "+selectedItem);
                }else {
                    city = parent.getItemAtPosition(position).toString();
//                    Log.i(TAG,"city "+city);
//                    Log.i(TAG,"city selectedItem "+selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Log.i(TAG,"city parent "+parent.toString());
            }
        });

    }


    private void setSpinner_state() {
        String[] stateArray = {getString(R.string.selectState), "Delhi", "Hariyana","UP","Punjab","Bihar"};

        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, stateArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerState.setAdapter(spinnerArrayAdapter);

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals(getString(R.string.selectState))) {
//                    Toasty.error(mContext, getString(R.string.selectState), Toast.LENGTH_SHORT).show();
//                    Log.i(TAG,"selectedItem State "+selectedItem);
                }else {
                    state = parent.getItemAtPosition(position).toString();
//                    Log.i(TAG,"State "+state);
//                    Log.i(TAG,"selectedItem State "+selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Log.i(TAG,"State parent "+parent.toString());
            }
        });

    }


    private void setSpinner_country() {
        String[] countryArray ={getString(R.string.select_country),"India"};

        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, countryArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerCountry.setAdapter(spinnerArrayAdapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals(getString(R.string.select_country))) {
//                    Toast.makeText(mContext, getString(R.string.select_country), Toast.LENGTH_SHORT).show();
//                    Log.i(TAG," selectedItem country "+selectedItem);
                }else {
                    country = parent.getItemAtPosition(position).toString();
//                    Log.i(TAG,"country "+country);
//                    Log.i(TAG," selectedItem country "+selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Log.i(TAG,"country parent "+parent.toString());
            }
        });

    }

    // driver_dob": "4/7/2000"
    public void getDobDate(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                tv_dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                tv_dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                Log.w(TAG, "myDob :" + tv_dob.getText().toString());
//                        tv_dob.setText(myDob);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void doValidation() {
        name =edt_name.getText().toString().trim();
        phone_number =edt_mobileno.getText().toString().trim();
        dob =tv_dob.getText().toString().trim();
        email =edt_email.getText().toString().trim();
        pincode =edt_pincode.getText().toString().trim();
        pan_no =edit_pan_no.getText().toString().trim();
        adhaar_number =edit_adhaar_number.getText().toString().trim();
        license_number =edit_license_number.getText().toString().trim();
        id_prof_image =tv_id_prof_image.getText().toString().trim();
        pan_card_image =tv_pan_card_image.getText().toString().trim();
        adhaar_card_image =tv_adhaar_card_image.getText().toString().trim();
        driving_licence_image =tv_driving_licence_image.getText().toString().trim();
//        AppPreferences.savePreferences(mContext, "EMAIL", email);

         if (name.equalsIgnoreCase("")) {
             edt_name.setError(getString(R.string.Please_Enter_driver_name));
             edt_name.requestFocus();
        }else if (dob.isEmpty()){
             tv_dob.setError(getString(R.string.selectDOB));
             tv_dob.requestFocus();
         }else if (phone_number.isEmpty()){
             edt_mobileno.setError(getString(R.string.err_msg_mobile));
             edt_mobileno.requestFocus();
         }else if (edt_mobileno.length() < 9) {
             edt_mobileno.setError(getString(R.string.err_msg_mobile_too_short));
             edt_mobileno.requestFocus();
         } else if (!Patterns.PHONE.matcher(phone_number).matches()){
             edt_mobileno.setError(getString(R.string.err_msg_mobile_invalid));
             edt_mobileno.requestFocus();
         }else if (phone_number.length()>12){
             edt_mobileno.setError(getString(R.string.err_msg_mobile_too_long));
             edt_mobileno.requestFocus();
         }else if (email.equalsIgnoreCase("")) {
             edt_email.setError(getString(R.string.err_msg_email));
             edt_email.requestFocus();
         } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
             edt_email.setError(getString(R.string.err_msg_email_invalid));
             edt_email.requestFocus();
         }
         else if (spinner_city.getSelectedItem().toString().trim() == getString(R.string.selectCity)) {
            Toast.makeText(mContext, "Error : "+getString(R.string.selectCity), Toast.LENGTH_SHORT).show();
        } else if (genderSpinner.getSelectedItem().toString().trim() == getString(R.string.selectGender)) {
             Toast.makeText(mContext, "Error : "+getString(R.string.selectGender), Toast.LENGTH_SHORT).show();
         } else if (spinnerCountry.getSelectedItem().toString().trim() == getString(R.string.select_country)) {
             Toast.makeText(mContext, "Error : "+getString(R.string.select_country), Toast.LENGTH_SHORT).show();
         } else if (spinnerState.getSelectedItem().toString().trim() == getString(R.string.selectState)) {
             Toast.makeText(mContext, "Error : "+getString(R.string.selectState), Toast.LENGTH_SHORT).show();
         }
         else if (license_number.equalsIgnoreCase("")) {
            edit_license_number.setError(getString(R.string.Please_Enter_license_number));
            edit_license_number.requestFocus();
        } else if (pincode.equalsIgnoreCase("")) {
             edt_pincode.setError(getString(R.string.pleaseEnterPincode));
             edt_pincode.requestFocus();
         } else if (pan_no.equalsIgnoreCase("")) {
             edit_pan_no.setError(getString(R.string.pleaseEnterPan_no));
             edit_pan_no.requestFocus();
         } else if (adhaar_number.equalsIgnoreCase("")) {
             edit_adhaar_number.setError(getString(R.string.pleaseEnterAdhaar_number));
             edit_adhaar_number.requestFocus();
         }
        /* else if (id_prof_image.equalsIgnoreCase("")) {
             tv_id_prof_image.setError(getString(R.string.pleaseEnterId_prof_image));
             tv_id_prof_image.requestFocus();
         } else if (pan_card_image.equalsIgnoreCase("")) {
             tv_pan_card_image.setError(getString(R.string.pleaseEnterPan_card_image));
             tv_pan_card_image.requestFocus();
         } else if (adhaar_card_image.equalsIgnoreCase("")) {
             tv_adhaar_card_image.setError(getString(R.string.pleaseEnterAdhaar_card_image));
             tv_adhaar_card_image.requestFocus();
         } else if (driving_licence_image.equalsIgnoreCase("")) {
             tv_driving_licence_image.setError(getString(R.string.pleaseEnterDriver_licence_number));
             tv_driving_licence_image.requestFocus();
         } */
         else {
             city =spinner_city.getSelectedItem().toString().trim();
             gender =genderSpinner.getSelectedItem().toString().trim();
             country =spinnerCountry.getSelectedItem().toString().trim();
             state =spinnerState.getSelectedItem().toString().trim();
            //   deviceSrNumber,license_number,driver_name,phone_number,device_no,sim_no;
            // ========== Api Call =============
            if (CheckNetwork.isNetworkAvailable(mContext)) {
//                getAddDriverResponseCall(name, phone_number, email, gender, state, city, dob, country,pincode, pan_no,license_number, adhaar_number, token);
                getAddDriverResponseCall(name, phone_number, email, gender, state, city, dob, country,pincode, pan_no,license_number, adhaar_number,vehicle_id, token);

            } else {
                Toasty.warning(mContext, getString(R.string.err_msg_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadPic(String profilePic){
        if (profilePic!=null){
            Glide.with(mContext)
                    .load(profilePic)
                    .into(user_profile_pic);
        }else {
            Glide.with(mContext)
                    .load(profilePic)
                    .placeholder(R.drawable.profile_default)
                    .into(user_profile_pic);
        }
    }


    private void takePictureDriving_licence() {
        Album.camera(this)
                .image()
//                .filePath()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) { ;
                        tv_driving_licence_image.setText(result);
//                        Log.w(TAG,"result Image : "+result);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(mContext, R.string.canceledCamera, Toast.LENGTH_LONG).show();
                    }
                })
                .start();

    }

    private void takePicture_id_prof() {
        Album.camera(this)
                .image()
//                .filePath()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) { ;
                        tv_id_prof_image.setText(result);
//                        Log.i(TAG,"result Image : "+result);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(mContext, R.string.canceledCamera, Toast.LENGTH_LONG).show();
                    }
                })
                .start();

    }

    private void takePicture_pan_card() {
        Album.camera(this)
                .image()
//                .filePath()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) { ;
                        tv_pan_card_image.setText(result);
//                        Log.i(TAG,"result Image : "+result);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(mContext, R.string.canceledCamera, Toast.LENGTH_LONG).show();
                    }
                })
                .start();

    }
    private void takePicture_adhaar_card() {
        Album.camera(this)
                .image()
//                .filePath()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) { ;
                        tv_adhaar_card_image.setText(result);
//                        Log.i(TAG,"result Image : "+result);
                        // loadPic(result);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(mContext, R.string.canceledCamera, Toast.LENGTH_LONG).show();
                    }
                })
                .start();

    }


    private void gallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE_REQUEST);

       /* Intent intent = new Intent();
        intent.setType("image/jpg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        startActivityForResult(intent, PICK_IMAGE);*/
    }

    private void takePicture2() {
        Album.camera(this)
                .image()
//                .filePath()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) { ;

//                        Log.i(TAG,"result profile_picResult"+result);
                        Album.getAlbumConfig()
                                .getAlbumLoader()
                                .load(user_profile_pic, result);
                        Bitmap bitmap = ImageUtils.StringToBitMap(result);
//                        uploadImage(token, bitmap);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(mContext, R.string.canceledCamera, Toast.LENGTH_LONG).show();
                    }
                })
                .start();

    }



    private void getAddDriverResponseCall(String driverName, String driverPhone, String driverEmail, String driverGander, String driverState, String driverCity, String driverDob,
                                          String driverCountry, String driverPincode, String driverPanNo, String driverDriveLicenceNo, String driverAadharNo,String vId, String token) {

      Driver registrationDriver1 = new Driver(driverName, driverPhone, driverEmail, driverGander, driverState, driverCity, driverDob, driverCountry, driverPincode, driverPanNo, driverDriveLicenceNo, driverAadharNo, token);
      // add - vehicle_id
      Driver registrationDriver = new Driver(driverName, driverPhone, driverEmail, driverGander, driverState, driverCity,
              driverDob, driverCountry, driverPincode, driverPanNo, driverDriveLicenceNo, driverAadharNo,vId, token);

        Driver addDriver= new Driver();
        addDriver.setDriverName(driverName);
        addDriver.setDriverPhone(driverPhone);
        addDriver.setDriverEmail(driverEmail);
        addDriver.setDriverGander(driverGander);
        addDriver.setDriverState(driverState);
        addDriver.setDriverCity(driverCity);
        addDriver.setDriverPanNo(driverPincode);
        addDriver.setDriverCountry(driverCountry);
        addDriver.setDriverPanNo(driverPanNo);
        addDriver.setDriverAadharNo(driverAadharNo);
        addDriver.setDriverDriveLicenceNo(driverDriveLicenceNo);
//        addDriver.setDriverIdproofImage(id_prof_image);
//        addDriver.setDriverPanImage(pan_card_image);
//        addDriver.setDriverAadharImage(adhaar_card_image);
//        addDriver.setDriverDriveLicenceImage(driving_licence_image);
//        addDriver.setDriverPhoto(selectedImagePath);
        addDriver.setDriverDob(driverDob);
        addDriver.setToken(token);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();

        /**
         * POST  - http://3.135.158.46/api/add_driver
         * register Driver
         */
        Call<JsonElement> call =  apiInterface.addDriver(registrationDriver);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "getAddDriverResponseCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200) {
                        JSONObject jsonObject = new JSONObject(str_response);
                        int  status = Integer.parseInt(jsonObject.getString("status"));
                        if (response.isSuccessful()) {
//                            String message = jsonObject.getString("message");
                            if (status==200){
//                                JSONObject data = jsonObject.getJSONObject("driver");
//                                String driver_name = data.getString("driver_name");
//                                String driver_dob = data.getString("driver_dob");
//                                Log.e(TAG,"driver_name- "+driver_name);
//                                Log.e(TAG,"driver_dob- "+driver_dob);
                                startActivity(new Intent(mContext, HomeActivity.class));
                                finish();
//                                Log.e(TAG,"JSONObject message-"+message);
//                                Toasty.success(mContext, "Message : "+message, Toast.LENGTH_SHORT).show();
                            }else {
                                Log.e(TAG,"Response code :"+status);
//                                Toasty.error(mContext, "Response code :"+status, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (pd!=null)pd.dismiss();
//                Log.e(TAG,"onFailure"+t.toString());
            }
        });


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
//                    Log.i(TAG,"selectedItem "+selectedItem);
                }else {
                    vehicle_name = parent.getItemAtPosition(position).toString();
                    mySelectedVehicleId = parent.getSelectedItemPosition() +1;

                    vehicle_id = String.valueOf(vehicleArrayList.get(position).getVehicleId());
//                    Log.i(TAG,"selectedItem myId "+mySelectedVehicleId);
//                    Log.i(TAG,"vehicle_id : "+vehicle_id);
//                    Log.i(TAG,"selectedItem vehicle_name "+vehicle_name);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Log.i(TAG,"parent "+parent.toString());
            }
        });
    }


    private void setSearchMultiSpinnerUnlimited() {

//        final List<String> list = Arrays.asList(getResources().getStringArray(R.array.sports_array));

        final List<KeyPairBoolData> listArray0 = new ArrayList<>();

        for (int i = 0; i < vehicleArrayList.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(vehicleArrayList.get(i).getVehicleId());
            h.setName(vehicleArrayList.get(i).getVehicleName());
            h.setSelected(false);
            listArray0.add(h);
        }

        searchMultiSpinnerUnlimited.setEmptyTitle("Data Found!");
        searchMultiSpinnerUnlimited.setSearchHint("Find Data");

        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });
    }

    // showVehicles
    private void getVehicleCall(final String token, String user_id) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

//        Call<VehiclesResponse> call = apiInterface.showVehicles(token);
        Call<VehiclesResponse> call = apiInterface.showVehiclesUserId(user_id);
        call.enqueue(new Callback<VehiclesResponse>() {
            @Override
            public void onResponse(Call<VehiclesResponse> call, Response<VehiclesResponse> response) {
                if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.w(TAG, "getVehicleCall Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (responseCode ==200){
                        if (response.isSuccessful()) {
                            VehiclesResponse vehiclesResponse = response.body();
                            assert vehiclesResponse != null;
                            int status = vehiclesResponse.getStatus();
                            if (status== 200) {
                                vehicleArrayList  = vehiclesResponse.getVehicles();
                                for (int i=0; i<vehicleArrayList.size();i++){
                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleNo());
//                                    myVehicleListSpinner.add(vehicleArrayList.get(i).getVehicleName() +" "+ vehicleArrayList.get(i).getVehicleNo());
                                }
                                //vehicleSpinner
                                setVehicleSpinner();
//                                setSearchMultiSpinnerUnlimited();
                            } else {
                                Toasty.error(mContext, "Status Code :"+status, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,""+e.toString());
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
                if (pd!=null)pd.dismiss();
                t.getStackTrace();
//                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }



}
