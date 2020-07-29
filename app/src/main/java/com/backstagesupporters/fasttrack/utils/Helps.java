package com.backstagesupporters.fasttrack.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


//  https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android

public class Helps  {

    public static void sendSMS(Context context, String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context,ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public  void showDailog(Context context){
        final CharSequence[] options = {"SMS Send", "Call","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("SMS Send")) {
                    dialog.dismiss();

                } else if (options[item].equals("Call")) {
                    dialog.dismiss();

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public  void showDialog(Context context){
        final CharSequence[] options = {"SMS Send", "Call","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("SMS Send")) {
                    dialog.dismiss();

                } else if (options[item].equals("Call")) {
                    dialog.dismiss();

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    /**
     private void callSosMethod2() {
     Log.e(TAG,"callSosMethod2 sos_number :"+sos_number);
     if (!iscColorSos){
     Log.i(TAG,"if iscColorSos: "+iscColorSos);
     iscColorSos =true;
     //            iv_sos.setBackgroundResource(R.drawable.circle_red);
     iv_sos.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.sos_b_red));
     }else {
     Log.i(TAG,"else iscColorSos: "+iscColorSos);
     iscColorSos =false;
     iv_sos.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.sos_b));
     //            ll_sos.setBackgroundResource(R.drawable.circle_white);
     }

     if (sos_number!=null){
     Log.e(TAG,"sos_number : "+sos_number);
     //                int sosPhon = Integer.parseInt(sos_number);
     //                callIntent.setData(Uri.parse("tel:+" + phoneTV.getText().toString().trim()));
     Intent callIntent = new Intent(Intent.ACTION_CALL);
     callIntent.setData(Uri.parse("tel:+" + sos_number));
     if (ActivityCompat.checkSelfPermission(mContext,
     Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
     return;
     }
     startActivity(callIntent);
     }else {
     Intent callIntent = new Intent(Intent.ACTION_CALL);
     callIntent.setData(Uri.parse("tel:1800 121 3201"));
     if (ActivityCompat.checkSelfPermission(mContext,
     Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
     return;
     }
     startActivity(callIntent);
     }

     }

     */

    /**
     // Send DATA FROM FRAGMENT
     private void sendData(String sData1,String sData2) {
     Intent i = new Intent(getActivity().getBaseContext(), HomeActivity.class);
     //PACK DATA
     i.putExtra("SENDER_KEY1", sData1);
     i.putExtra("SENDER_KEY2", sData2);

     //RESET WIDGETS
     //        nameFragTxt.setText("");
     //        launchYearSpinner.setSelection(0);

     //START ACTIVITY
     getActivity().startActivity(i);
     }

     */


}
