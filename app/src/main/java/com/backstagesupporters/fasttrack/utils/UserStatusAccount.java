package com.backstagesupporters.fasttrack.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;


import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.user.LoginsActivity;

// TODO: 4/1/2020
public class UserStatusAccount {

    public static void checkUserStatus(Activity activity, String TAG)  {
        String USER_STATUS="";
        // USER_STATUS = AppPreferences.loadPreferences(mContext, "USER_STATUS");
        USER_STATUS = AppPreferences.loadPreferences(activity, VariablesConstant.USER_STATUS);
        Log.e(TAG, "USER_STATUS : "+USER_STATUS);
        if (USER_STATUS.equalsIgnoreCase("deactive") || USER_STATUS.equalsIgnoreCase("deactivate")){
            activity.startActivity(new Intent(activity, LoginsActivity.class));
            activity.finish();
        }
    }
}
