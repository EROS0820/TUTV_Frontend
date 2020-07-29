package com.backstagesupporters.fasttrack;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.androidnetworking.AndroidNetworking;
import com.backstagesupporters.fasttrack.utils.language.Utility;
import com.backstagesupporters.fasttrack.utils.language.LanguageHelper;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

public class MyApplication extends Application {
    private String TAG= getClass().getSimpleName();
//    public static LocaleManager localeManager;
    public static LanguageHelper localeManager;
    private static MyApplication instance;

    // getInstance
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Utility.bypassHiddenApiRestrictions();
        AndroidNetworking.initialize(getApplicationContext());

        // Album
        if (instance == null) {
            instance = this;

            // Album
            Album.initialize(AlbumConfig.newBuilder(this)
                    .setAlbumLoader(new MediaLoader())
                    .setLocale(Locale.getDefault())
                    .build()
            );
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageHelper.getLanguage(this);
//        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
    }


    // locale settings in your application - your own language
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(LanguageHelper.onAttach(base, "en"));
//    }

    // override the base context of application to update default locale for the application
    @Override
    protected void attachBaseContext(Context base) {
//        localeManager = new LocaleManager(base);
        super.attachBaseContext(LanguageHelper.onAttach(base));
//        super.attachBaseContext(localeManager.setLocale(base));
        LanguageHelper.onAttach(base, LanguageHelper.getLanguage(base));
//        Log.d(TAG, "attachBaseContext");
    }


}
