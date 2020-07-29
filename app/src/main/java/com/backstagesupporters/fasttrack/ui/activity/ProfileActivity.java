package com.backstagesupporters.fasttrack.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.responseClass.UpdateProfilePicture;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.user.LoginsActivity;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.ImageUtils;
import com.backstagesupporters.fasttrack.utils.language.LanguageHelper;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.bumptech.glide.Glide;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private Context mContext;
    private TextView tv_change_password,tv_edit_profile,tv_changeLanguage,tv_logout;
    private TextInputEditText edt_password;
    private TextView tv_username,tv_dob,tv_gender,tv_mobileno,tv_email,tv_address;
    private CircleImageView user_profile_pic;
    private ImageView  imageViewChange;

    ApiInterface apiInterface;
    private String token, profilePic;
    private ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext=ProfileActivity.this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        findViewById();
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.viewProfile));

        iv_tool_back_left.setOnClickListener(this);
        imageViewChange.setOnClickListener(this);
        tv_change_password.setOnClickListener(this);
        tv_edit_profile.setOnClickListener(this);
        tv_changeLanguage.setOnClickListener(this);
        tv_logout.setOnClickListener(this);


        //set user info
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        String userName = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_NAME);
        String password = AppPreferences.loadPreferences(mContext, VariablesConstant.PASSWORD);
        String userEmail = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_EMAIL);
        String email = AppPreferences.loadPreferences(mContext, VariablesConstant.EMAIL);
        String DOB = AppPreferences.loadPreferences(mContext, VariablesConstant.DOB);
        String mobile = AppPreferences.loadPreferences(mContext, VariablesConstant.MOBILE);
        String address = AppPreferences.loadPreferences(mContext, VariablesConstant.ADDRESS);
        String gender = AppPreferences.loadPreferences(mContext, VariablesConstant.GENDER);
        profilePic = AppPreferences.loadPreferences(mContext,  VariablesConstant.PROFILE_PIC);
//        Log.e(TAG, "token :" + token);
//        Log.e(TAG, "profilePic :" + profilePic);
//        Log.e(TAG, "User :" + userName+"-"+userEmail+"-"+mobile+"-");

        tv_username.setText(userName);
        tv_mobileno.setText(mobile);
        tv_address.setText(address);  //address
        tv_gender.setText(gender);  //address
        if (DOB !=null){
            tv_dob.setText(DOB);             // DOB
        }else {
            tv_dob.setText("User's DOB not find");
        }
        if (!password.isEmpty()){
            edt_password.setText(password);
        }
        if (userEmail.isEmpty()){
            tv_email.setText(email);
        }else {
            tv_email.setText(userEmail);
        }

        // profile_pic
        String image_url = VariablesConstant.URL_PICTURE + profilePic;
//        Log.w(TAG, "URL_PICTURE :" + image_url);
        Glide.with(mContext)
                .load(image_url)
                .placeholder(R.drawable.profile_default)
                .into(user_profile_pic);

    }


    @Override
    protected void onStart() {
        super.onStart();

        tv_changeLanguage.setOnClickListener(v -> {
            showDialogForLanguage();
        });
    }


    int selected;
    private void showDialogForLanguage() {
        final String languageItem[] ={"English","हिंदी"};
        // or whatever you want

        String index = AppPreferences.loadPreferences(mContext,VariablesConstant.SELECTED_LANGUAGE_POSITION );
        if (!index.isEmpty()){
            selected = Integer.parseInt(index);
//            Log.w(TAG, "selected Index: "+selected);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("choose Language");
        builder.setSingleChoiceItems(languageItem, selected, (dialog, position) -> {

            AppPreferences.savePreferences(mContext,VariablesConstant.SELECTED_LANGUAGE_POSITION , String.valueOf(position));
            if (position==0){
//                Log.w(TAG, "English");
                // English
                LanguageHelper.setLocale(mContext, VariablesConstant.ENGLISH_LOCALE);
                recreate();

//                MyApplication.localeManager.setNewLocale(this, VariablesConstant.ENGLISH_LOCALE);
//                setNewLocale(VariablesConstant.ENGLISH_LOCALE, false);
//                setNewLocale(VariablesConstant.ENGLISH_LOCALE, true);
//                LanguageHelper.onAttach(mContext, VariablesConstant.ENGLISH_LOCALE);
                AppPreferences.savePreferences(mContext,VariablesConstant.LOCALE_KEY , VariablesConstant.ENGLISH_LOCALE);

            }else if (position==1){
//                Log.w(TAG, "हिंदी");
                // हिंदी
//                MyApplication.localeManager.setNewLocale(this, VariablesConstant.HINDI_LOCALE);
                LanguageHelper.setLocale(mContext, VariablesConstant.HINDI_LOCALE);
                recreate();

//                setNewLocale(VariablesConstant.HINDI_LOCALE, false);
//                setNewLocale(VariablesConstant.HINDI_LOCALE, true);
//                LanguageHelper.onAttach(mContext, VariablesConstant.HINDI_LOCALE);
                AppPreferences.savePreferences(mContext,VariablesConstant.LOCALE_KEY , VariablesConstant.HINDI_LOCALE);

            }
            dialog.dismiss();

        });

        AlertDialog mDialog = builder.create();
        mDialog.show();
    }




    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);
        tv_logout =findViewById(R.id.tv_logout);
        tv_edit_profile =findViewById(R.id.tv_edit_profile);
        tv_change_password =findViewById(R.id.tv_change_password);
        tv_changeLanguage =findViewById(R.id.tv_changeLanguage);

        tv_username =findViewById(R.id.tv_username);
        tv_mobileno =findViewById(R.id.tv_mobileno);
        tv_dob =findViewById(R.id.tv_dob);
        tv_gender =findViewById(R.id.tv_gender);
        tv_email =findViewById(R.id.tv_email);
        tv_address =findViewById(R.id.tv_address);
        edt_password =findViewById(R.id.edt_password);
        user_profile_pic = findViewById(R.id.imageViewUser_profile_pic);
        imageViewChange = findViewById(R.id.imageView2);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.tv_logout:
                logOut();
                break;

            case R.id.tv_change_password:
                startActivity(new Intent(mContext,ChangePasswordActivity.class));
                finish();
                break;

            case R.id.tv_edit_profile:
                startActivity(new Intent(mContext,ProfileEditActivity.class));
                finish();
                break;

            case R.id.imageView2:
                takePicture();
                break;

            case R.id.iv_tool_back_left:
                onBackPressed();
                break;

        }
    }




    private void takePicture() {
        Album.camera(this)
                .image()
//                .filePath()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
//                        Log.d(TAG,"imageString :"+result);
                        Album.getAlbumConfig()
                                .getAlbumLoader()
                                .load(user_profile_pic, result);
                        Bitmap bitmap = ImageUtils.StringToBitMap(result);
                        uploadImage(token, bitmap);
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

    private void uploadImage(String token, Bitmap bitmap) {
        //========== Api Call ===========
        if (CheckNetwork.isNetworkAvailable(mContext)) {
            updateProfilePicture(token, profilePic);
        } else {
            Toasty.warning(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }


    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.app_logout, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        TextView btn_cancel = dialogView.findViewById(R.id.tv_cancel);
        TextView btn_yes = dialogView.findViewById(R.id.tv_logout);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                disconnectFromFacebook();

               /* SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.apply();*/
                AppPreferences.savePreferences(mContext, VariablesConstant.TOKEN, "");
                AppPreferences.savePreferences(mContext, VariablesConstant.LOGIN_STATUS, "0");

                Intent intent = new Intent(mContext, LoginsActivity.class);
//                Intent intent = new Intent(mContext, WelcomeActivity.class);

                //    Works with API >= 11
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                alertDialog.dismiss();

//                Toasty.success(mContext, "You have logout!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }



    //========== Api Call updateProfilePicture ===========
    private void updateProfilePicture(String token, String profilePic) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        Call<UpdateProfilePicture> call = apiInterface.updateProfilePicture(token, profilePic);
        call.enqueue(new Callback<UpdateProfilePicture>() {
            @Override
            public void onResponse(Call<UpdateProfilePicture> call, Response<UpdateProfilePicture> response) {
                pd.dismiss();
                String str = new Gson().toJson(response.body());
//                Log.w(TAG, "Response >>>>" + str);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (response.isSuccessful()) {
                        UpdateProfilePicture updateProfileResponse = response.body();
                        String status = updateProfileResponse.getStatus();
                        String message = updateProfileResponse.getMessage();

//                        Toasty.success(mContext, "" + message, Toast.LENGTH_SHORT).show();
                    } else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UpdateProfilePicture> call, Throwable t) {
                pd.dismiss();
//                Toasty.error(mContext, "" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }



}
