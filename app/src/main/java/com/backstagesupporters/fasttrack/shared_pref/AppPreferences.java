package com.backstagesupporters.fasttrack.shared_pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

public class AppPreferences {


    private static final String PREFS_NAME = "com.backstagesupporters.fasttrack";

    public static String savePreferences(Context context, String key, String value) {

        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();// edit() fun is used save the data into sharedPreferences
        editor.putString(key, value);
        editor.apply();

        return key;
    }

    //custom Method To Get the Data
    public static String loadPreferences(Context context, String key) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String value = sharedPreferences.getString(key, "");

        return value;
    }



}
