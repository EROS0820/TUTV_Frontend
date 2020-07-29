package com.backstagesupporters.fasttrack.retrofitAPI;




import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;

import java.io.IOException;
import java.lang.annotation.Annotation;


import es.dmoral.toasty.Toasty;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;


public class ErrorUtils {

    //Setting message manually and performing action on button click
    public static void alertDialogMsg(Context context, String title, String massage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(massage)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setTitle(title);
       // alert.setTitle("Unauthorized user");
        alert.show();
    }

    // apiResponseErrorHandle(TAG,responseCode,error,"LOGIN_USER", mContext);
    public static void apiResponseErrorHandle(String TAG, int responseCode,String massageError,String massageAPI, Context context) {
        String msgTitle = "";
        switch (responseCode) {
            case 401:
                msgTitle = context.getString(R.string.err_unauthorized_token_expired);
                Log.e(TAG,msgTitle);
//                Toasty.error(context, context.getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                if (massageAPI.equalsIgnoreCase(VariablesConstant.LOGIN_USER)){
                    alertDialogMsg(context,VariablesConstant.LOGIN_USER, massageError);
                }else if (massageAPI.equalsIgnoreCase(VariablesConstant.SHOW_VEHICLE_LIST)){
                    alertDialogMsg(context,VariablesConstant.SHOW_VEHICLE_LIST, massageError);
                }

                break;
            case 404:
                msgTitle = context.getString(R.string.err_not_found);
                Log.e(TAG,msgTitle);
                if (massageAPI.equalsIgnoreCase("LOGIN_USER")){
                    alertDialogMsg(context,msgTitle, massageError);
                }else if (massageAPI.equalsIgnoreCase(VariablesConstant.SHOW_VEHICLE_LIST)){
                    alertDialogMsg(context,msgTitle, massageError);
                }
//                Toasty.error(context, context.getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                break;
            case 429:
                msgTitle = context.getString(R.string.err_Too_Many_Requests);
                if (massageAPI.equalsIgnoreCase("LOGIN_USER")){
                    alertDialogMsg(context,msgTitle, massageError);
                }else if (massageAPI.equalsIgnoreCase(VariablesConstant.SHOW_VEHICLE_LIST)){
                    alertDialogMsg(context,msgTitle, massageError);
                }
                Log.e(TAG,msgTitle );
//                Toasty.error(context, context.getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                break;
            case 500:
                msgTitle = context.getString(R.string.err_server_broken);
                if (massageAPI.equalsIgnoreCase("LOGIN_USER")){
                    alertDialogMsg(context,msgTitle, massageError);
                }else if (massageAPI.equalsIgnoreCase(VariablesConstant.SHOW_VEHICLE_LIST)){
                    alertDialogMsg(context,msgTitle, massageError);
                }
                Log.e(TAG,msgTitle );
//                Toasty.error(context, context.getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                break;
            default:
                msgTitle = context.getString(R.string.err_unknown_error);
                if (massageAPI.equalsIgnoreCase("LOGIN_USER")){
                    alertDialogMsg(context,msgTitle, massageError);
                }else if (massageAPI.equalsIgnoreCase(VariablesConstant.SHOW_VEHICLE_LIST)){
                    alertDialogMsg(context,msgTitle, massageError);
                }
                Log.e(TAG,msgTitle );
//                Toasty.error(context, context.getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                break;
        }
    }



    public static void apiResponseErrorHandle(String TAG, int responseCode, Context context) {
        switch (responseCode) {
            case 401:
                Log.e(TAG, context.getString(R.string.err_unauthorized_token_expired));
//                Toasty.error(context, context.getString(R.string.err_unauthorized_token_expired), Toast.LENGTH_SHORT).show();
                break;
            case 404:
                Log.e(TAG, context.getString(R.string.err_not_found));
//                Toasty.error(context, context.getString(R.string.err_not_found), Toast.LENGTH_SHORT).show();
                break;
            case 429:
                Log.e(TAG, context.getString(R.string.err_Too_Many_Requests));
//                Toasty.error(context, context.getString(R.string.err_Too_Many_Requests), Toast.LENGTH_SHORT).show();
                break;
            case 500:
                Log.e(TAG, context.getString(R.string.err_server_broken));
//                Toasty.error(context, context.getString(R.string.err_server_broken), Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.e(TAG, context.getString(R.string.err_unknown_error));
//                Toasty.error(context, context.getString(R.string.err_unknown_error), Toast.LENGTH_SHORT).show();
                break;
        }
    }



}