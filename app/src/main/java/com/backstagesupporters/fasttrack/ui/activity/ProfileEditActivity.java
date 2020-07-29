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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;


import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.responseClass.UpdateProfilePicture;
import com.backstagesupporters.fasttrack.responseClass.UpdateProfileResponse;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.RoundedImageView;
import com.backstagesupporters.fasttrack.utils.UserStatusAccount;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.InputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import rx.functions.Action1;

public class ProfileEditActivity extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private ImageView iv_tool_back_left;

    private TextView tv_tool_title;
    private Context mContext;
    TextInputEditText ed_name,ed_mobileno,ed_profession, ed_email;
    private Spinner genderSpinner;
    private TextView btn_save, tv_dob;
    private String name,mobileno,dob,gender,profession, email;
    private ImageView  imageViewChange;
    private CircleImageView user_profile_pic;  // profile_default
    private RoundedImageView imageProfilePic;
    private int mYear, mMonth, mDay;
    ApiInterface apiInterface;
    private String token, profilePic;
    private Uri selectedImageUri;

    private ProgressDialog pd;
    public static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        mContext=ProfileEditActivity.this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        String userName = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_NAME);
        String userEmail = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_EMAIL);
        String email = AppPreferences.loadPreferences(mContext, VariablesConstant.EMAIL);
        String DOB = AppPreferences.loadPreferences(mContext, VariablesConstant.DOB);
        String mobile = AppPreferences.loadPreferences(mContext, VariablesConstant.MOBILE);
        String profession = AppPreferences.loadPreferences(mContext, VariablesConstant.PROFESSION);
        String gender = AppPreferences.loadPreferences(mContext, VariablesConstant.GENDER);
        profilePic = AppPreferences.loadPreferences(mContext,  VariablesConstant.PROFILE_PIC);

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.editProfile));

        iv_tool_back_left.setOnClickListener(this);
        imageViewChange.setOnClickListener(this);
        tv_dob.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
//        Log.w(TAG, "token :" + token);
        setSpinnerGender();

        // profilePic
        if (profilePic!=null){
            String vehicle_image_url = "http://3.135.158.46"+ profilePic;
//            Log.w(TAG, "vehicle_image_url :" + vehicle_image_url);

            Glide.with(mContext)
                    .load(vehicle_image_url)
                    .placeholder(R.drawable.profile_default)
                    .into(user_profile_pic);
        }

        ed_name.setText(userName);
        ed_mobileno.setText(mobile);
        ed_profession.setText(profession);
        ed_email.setText(email);
        tv_dob.setText(DOB);

        if (!mobile.isEmpty() && mobile.length() >2){
            btn_save.setText(getString(R.string.save));

            // set not editable -  setEditable and setFocusable false
          /*  ed_mobileno.setKeyListener(null);
            ed_mobileno.setCursorVisible(false);
            ed_mobileno.setPressed(false);
            ed_mobileno.setFocusable(false);
            ed_mobileno.setClickable(false);*/
        }

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
        ed_name =findViewById(R.id.ed_name);
        ed_mobileno =findViewById(R.id.ed_mobileno);
        ed_profession =findViewById(R.id.ed_profession);
        ed_email =findViewById(R.id.ed_email);
        tv_dob =findViewById(R.id.tv_dob);
        genderSpinner =findViewById(R.id.genderSpinner);
        user_profile_pic = findViewById(R.id.imageViewUser_profile_pic);
        imageProfilePic = findViewById(R.id.imageProfilePic);
        imageViewChange = findViewById(R.id.imageView2);
        btn_save = findViewById(R.id.btn_save);

        // TODO: 1/29/2020
//        user_profile_pic.setOnLongClickListener(v -> {
//            CheckPermissions();
//            showDialogImage();
//            return true;
//        });

        imageViewChange.setOnClickListener(v -> {
            CheckPermissions();
            showDialogImage();
        });


    }

    // TODO: 12/29/2019
    private void CheckPermissions() {
        RxPermissions.getInstance(mContext)
                .request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
            takePicture();
            dialog.dismiss();
        });
        dialog.show();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = data.getData();
//                Log.w(TAG,"selectedImageUri : "+selectedImageUri);

                profilePic = getAbsolutePath(data.getData());
//                Log.e(TAG,"profilePic : "+profilePic);

                 selectedImagePath = profilePic;
//                imageProfilePic.setImageBitmap(decodeFile(selectedImagePath));
                Glide.with(mContext)
                        .load(profilePic)
                        .placeholder(R.drawable.profile_default)
                        .into(imageProfilePic);

                profileCall(profilePic);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                doValidation();
                break;

            case R.id.iv_tool_back_left:
                onBackPressed();
                break;

            // imageViewChange
            case R.id.imageView2:
                CheckPermissions();
                showDialogImage();
                break;

            case R.id.tv_dob:
                getDobDate();
                break;
        }
    }


    // Select Gender
    private void setSpinnerGender() {
        String[] genderArray = { getString(R.string.selectGender), getString(R.string.male), getString(R.string.female)};
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, genderArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        genderSpinner.setAdapter(spinnerArrayAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals(getString(R.string.selectGender))) {
                    Toast.makeText(mContext, getString(R.string.selectGender), Toast.LENGTH_SHORT).show();
                }else {
                    gender = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Log.i(TAG,"parent "+parent.toString());
            }
        });
    }


    public void getDobDate(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tv_dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                        Log.e(TAG, "myDob :" + tv_dob.getText().toString());
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }



    private void takePicture() {
        Album.camera(this)
                .image()
//                .filePath()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
//                        Log.d(TAG,"imageString :"+result);
                        profilePic = result;
                        selectedImageUri = Uri.parse(result);

                        Album.getAlbumConfig()
                                .getAlbumLoader()
                                .load(user_profile_pic, result);

                        profileCall(result);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(mContext, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();

    }


    // name ,email,dob,gender,profession
    private void doValidation() {
//        AppPreferences.savePreferences(mContext, VariablesConstant.MOBILE, mobile);
        name = ed_name.getText().toString().trim();
        mobileno = ed_mobileno.getText().toString().trim();
        dob = tv_dob.getText().toString().trim();
        profession = ed_profession.getText().toString().trim();
        email = ed_email.getText().toString().trim();
//        Log.i(TAG,"gender "+gender);

        if (name.equalsIgnoreCase("")) {
            ed_name.setError(getString(R.string.pleaseEnterUserName));
            ed_name.requestFocus();
        }else if(mobileno.isEmpty()){
            ed_mobileno.setError(getString(R.string.err_msg_mobile));
            ed_mobileno.requestFocus();
        }else if(mobileno.length() < 9) {
            ed_mobileno.setError(getString(R.string.err_msg_mobile_too_short));
            ed_mobileno.requestFocus();
        } else if(!Patterns.PHONE.matcher(ed_mobileno.getText().toString()).matches()){
            ed_mobileno.setError(getString(R.string.err_msg_mobile_invalid));
            ed_mobileno.requestFocus();
        }else if(mobileno.length()>13){
            ed_mobileno.setError(getString(R.string.err_msg_mobile_too_long));
            ed_mobileno.requestFocus();
        }else if(dob.equalsIgnoreCase("")) {
            tv_dob.setError(getString(R.string.selectDOB));
            tv_dob.requestFocus();
        } else if (email.equalsIgnoreCase("")) {
//            ed_profession.setError(getString(R.string.pleaseEnterUserName));
            ed_email.setError(getString(R.string.email_user));
            ed_email.requestFocus();
        }else {
            if(gender==null || gender.equalsIgnoreCase(getString(R.string.selectGender))) {
                Toast.makeText(mContext, getString(R.string.selectGender), Toast.LENGTH_SHORT).show();
            } else {
                if (CheckNetwork.isNetworkAvailable(mContext)) {
                    // ======= API Call ==========
                    //calling the upload file method after choosing the file
                    updateProfileCall(token,name,mobileno,dob,gender,email,profilePic);
                } else {
                    Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void profileCall(String takePic){
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            // ======= API Call ==========
            if (profilePic!=null){
//                    uploadFile(profilePic, token);
//                    updateProfilePicture1(token, profilePic);
                updateProfilePicture(takePic, token);
            }else {
                Toast.makeText(mContext, getString(R.string.pleaseEnterTakeProfile_image), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    // Image to Base64
    private String getBase64String(String mCurrentPhotoPath) {
        // give your image file url in mCurrentPhotoPath
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // In case you want to compress your image, here it's at 40%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }


    private void decodeBase64AndSetImage(String completeImageData) {

        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);

        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

//        imageView.setImageBitmap(bitmap);
        imageProfilePic.setImageBitmap(bitmap);
    }

    //============================== Api Call ==============================

    private void updateProfileCall(String token, String name, String mobile, String dob, String gender, String email, String profile_pic) {
//        File file = new File(getRealPathFromURI(selectedImageUri));
//        Log.w(TAG, "file >>>>" + file);

        //creating a file
//        Log.w(TAG, "profile_pic >>>>" + profile_pic);
        Uri uriPic = Uri.parse(profile_pic);
//        Log.w(TAG, "uriPic >>>>" + uriPic);

        Drawable drawable = imageProfilePic.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)imageProfilePic.getDrawable()).getBitmap();

        String stringDataPic2 =  getEncoded64ImageStringFromBitmap(bitmap);
//        Log.w(TAG, "stringDataPic >>>>" + stringDataPic2);

        byte[] decodedString = Base64.decode(stringDataPic2.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

//        String stringDataPic = getBase64String(stringDataPic2);
//        Log.w(TAG, "Bitmap decodedByte >>>>" + decodedByte);

        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        /**
         * POST
         * http://138.197.210.36/api/update_info
         */
//        Call<UpdateProfileResponse> call = apiInterface.updateUserProfile(token,name,mobile,dob,gender,profession,profile_pic);
        Call<UpdateProfileResponse> call = apiInterface.updateUserProfile(token,name,mobile,dob,gender,email,profile_pic);
        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (pd!=null)pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                int  responseCode  = response.code();
//                Log.e(TAG, "Response >>>>" + str_response);
//                Log.e(TAG, "responseCode :" + responseCode);

                try {
                    if (response.isSuccessful()) {
                        UpdateProfileResponse  profileResponse = response.body();
                        int status = profileResponse.getStatus();
                        String message = profileResponse.getMessage();
//                        Log.e(TAG, "response-message :" + message);

                        if (status ==200) {
//                            Toasty.success(mContext, "SUCCESS : "+message, Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "message : "+message);
                            startActivity(new Intent(mContext, ProfileActivity.class));
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                        } else {
//                            Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
                            Log.w(TAG,message);
                        }
                    } else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
//                    Log.e(TAG,""+e.toString());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
//                Toast.makeText(mContext, "" + t, Toast.LENGTH_SHORT).show();
            }
        });

    }


    //profile_pic
    private void updateProfilePicture( String profilePic, String token) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        //Create a file object using file path
        File file = new File(profilePic);
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("profile_pic", file.getName(), requestBody);
//        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), imgname);
        RequestBody requestBodyToken = RequestBody.create(MediaType.parse("text/plain"), token);

//        Log.w(TAG,"updateProfilePicture file >> "+ file);
//        Log.w(TAG,"updateProfilePicture requestBo>> "+requestBody);

        Call<UpdateProfilePicture> call = apiInterface.uploadImage1(requestBodyToken, fileToUpload);

        call.enqueue(new Callback<UpdateProfilePicture>() {
            @Override
            public void onResponse(Call<UpdateProfilePicture> call, Response<UpdateProfilePicture> response) {
                if (pd!=null) pd.dismiss();

                String str = new Gson().toJson(response.body());
//                Log.e(TAG, "updateProfilePicture Response >>>>" + str);
                try {
                    if (response.isSuccessful()) {

                        UpdateProfilePicture updateProfileResponse = response.body();
                        String status = updateProfileResponse.getStatus();
                        String message = updateProfileResponse.getMessage();
//                        Log.w(TAG, "status:" + status);
//                        Log.w(TAG, "message:" + message);

                        Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(TAG,"Exception: "+e.toString());
                }
            }

            @Override
            public void onFailure(Call<UpdateProfilePicture> call, Throwable t) {
                if (pd!=null) pd.dismiss();
                Log.e(TAG,"onFailure: "+t.toString());
//                Toasty.error(mContext, "" + t, Toast.LENGTH_SHORT).show();
            }
        });

    }

    // uploadFile
    private void uploadFile(String path, String token) {
//        Log.w(TAG, "uploadFile token >>>>" + token);
        File file1 = new File(getRealPathFromURI(selectedImageUri));
//        Log.w(TAG, "uploadFile file1 >>>>" + file1);

        //  showpDialog();
        pd = new ProgressDialog(mContext);
        pd.setMessage("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        //Create a file object using file path
        File file = new File(path);
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("profile_pic", file.getName(), requestBody);
//        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), imgname);
        RequestBody requestBodyToken = RequestBody.create(MediaType.parse("text/plain"), token);

//        Log.e(TAG,"uploadFile file >> "+ file);
//        Log.e(TAG,"uploadFile requestBo>> "+requestBody);

        Call<String> call = apiInterface.uploadImage(requestBodyToken, fileToUpload);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                pd.dismiss();
                String str = new Gson().toJson(response.body());
//                Log.e(TAG,"uploadFile response >> "+ str);
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

//                    Log.e(TAG,"status >> "+ jsonObject.getString("status"));
//                    Log.e(TAG,"jsonObject >> "+ jsonObject.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Log.e(TAG,"JSONException: "+ e.toString());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                pd.dismiss();
                Log.e(TAG,"onFailure :"+ call.toString());
            }
        });

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, ProfileActivity.class));
        finish();
    }


}
