package com.backstagesupporters.fasttrack.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.backstagesupporters.fasttrack.R;

public class MyShowDialog {
    private AlertDialog dialog;
    private Activity activity;
    private Context context;
    private ProgressDialog pd;

    public MyShowDialog(Activity activity) {
        this.activity = activity;
        this.context = activity;
        this.dialog = getProgressDialog();
    }

    public void showProgress() {
        try {
            if (dialog != null) {
                dialog.show();
            } else {
                this.dialog = getProgressDialog();
                dialog.show();
            }
        } catch (Exception e) {
        }
    }

    public void hideProgress() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
        }
    }



    private AlertDialog getProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        View view = activity.getLayoutInflater().inflate(R.layout.progress_bar, null);
        dialog.setView(view);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        return dialog;
    }




}
