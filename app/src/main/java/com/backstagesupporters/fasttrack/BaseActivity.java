package com.backstagesupporters.fasttrack;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;


import com.backstagesupporters.fasttrack.utils.language.Utility;
import com.backstagesupporters.fasttrack.utils.language.LanguageHelper;


public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "fasttrack";

    @Override
    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(MyApplication.localeManager.setLocale(base));
        super.attachBaseContext(LanguageHelper.onAttach(base));
//        Log.e(TAG,"attachBaseContext: "+LanguageHelper.getLanguage(base));
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate");
        Utility.resetActivityTitle(this);
    }







}