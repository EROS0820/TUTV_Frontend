package com.backstagesupporters.fasttrack.utils;


import android.app.ProgressDialog;
import android.content.Context;

// TODO: 3/5/2020
public class MyProgressDialog {
    private static ProgressDialog pd;

    public static void showProgress(Context context) {
        pd = new ProgressDialog(context);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();
    }

    public static void hideProgress() {
        if (pd!=null){
            pd.dismiss();
        }
    }


}
