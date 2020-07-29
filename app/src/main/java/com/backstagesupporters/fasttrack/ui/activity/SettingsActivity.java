package com.backstagesupporters.fasttrack.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;


import com.backstagesupporters.fasttrack.BaseActivity;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.R;

import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;


public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private ImageView iv_tool_back_left;
    private TextView tv_tool_title;
    private TextView tv_volume,tv_notification_sound,tv_notification_ringtone, tv_buzzer_interval,tv_buzzer_ringtone, tv_select_map_display,tv_fresh_interval,
            tv_notification_ringtone2,tv_select_map_display2,tv_buzzer_ringtone2, tv_buzzer_interval2,  tv_fresh_interval2;

    private CheckedTextView check_box_volume, notificationCb;
    private SwitchCompat playRingtoneSwitch, switch_notification_sound;
    private SwitchCompat defaultSwitch,silentSwitch;
    private LinearLayout ll_tv_buzzer_interval,ll_tv_notification_ringtone,ll_tv_buzzer_ringtone,
            ll_tv_select_map_display,ll_tv_fresh_interval;

    private String notification_sound_uri,buzzer_sound_uri,
            buzzer_interval, map_view_type, Refresh_Interval;

    private Uri mCurrentSelectedUri;
    int position = 0;
    static Boolean isTouched = false;


    private View.OnClickListener mCheckBoxClickListener = v -> {
        if (v instanceof CheckedTextView) {
            CheckedTextView checkedTextView = (CheckedTextView) v;
            checkedTextView.setChecked(!checkedTextView.isChecked());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mContext=SettingsActivity.this;

        findViewById();

        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.settings));
        iv_tool_back_left.setOnClickListener(this);
        ll_tv_notification_ringtone.setOnClickListener(this);
        ll_tv_buzzer_ringtone.setOnClickListener(this);
        ll_tv_select_map_display.setOnClickListener(this);
        ll_tv_fresh_interval.setOnClickListener(this);
        ll_tv_buzzer_interval.setOnClickListener(this);


        // NOTIFICATION_URI_RINGTONE
        notification_sound_uri = AppPreferences.loadPreferences(mContext, VariablesConstant.NOTIFICATION_URI_RINGTONE);
//        Log.i(TAG,"NOTIFICATION_URI_RINGTONE : "+notification_sound_uri);
        tv_notification_ringtone2.setText("RINGTONE: "+AppPreferences.loadPreferences(mContext, VariablesConstant.RINGTONE_SOUND_NAME));

        // buzzer_ringtone
        buzzer_sound_uri = AppPreferences.loadPreferences(mContext, VariablesConstant.Buzzer_URI_RINGTONE);
//        Log.i(TAG,"Buzzer_URI_RINGTONE: "+buzzer_sound_uri);
        tv_buzzer_ringtone2.setText("RINGTONE: "+AppPreferences.loadPreferences(mContext, VariablesConstant.Buzzer_SOUND_NAME));

        // buzzer_interval
        buzzer_interval = AppPreferences.loadPreferences(mContext, VariablesConstant.Buzzer_Interval);
//        Log.i(TAG,"buzzer_interval : "+buzzer_interval);
        tv_buzzer_interval2.setText("Buzzer Interval: "+buzzer_interval);

        //map_view_type
        map_view_type = AppPreferences.loadPreferences(mContext, VariablesConstant.MapView_Type);
//        Log.i(TAG,"map_view_type : "+map_view_type);
        tv_select_map_display2.setText("Mapview: "+map_view_type);

        // Refresh_Interval
        Refresh_Interval = AppPreferences.loadPreferences(mContext, VariablesConstant.REFRESH_INTERVAL);
//        Log.i(TAG,"Refresh_Interval : "+Refresh_Interval);
        tv_fresh_interval2.setText("Mapview: "+Refresh_Interval);

    }


    private void findViewById() {
        iv_tool_back_left = findViewById(R.id.iv_tool_back_left);
        tv_tool_title = findViewById(R.id.tv_tool_title);

        tv_notification_ringtone = findViewById(R.id.tv_notification_ringtone);
        tv_notification_ringtone2 = findViewById(R.id.tv_notification_ringtone2);
        tv_buzzer_interval = findViewById(R.id.tv_buzzer_interval);
        tv_buzzer_interval2 = findViewById(R.id.tv_buzzer_interval2);
        tv_buzzer_ringtone = findViewById(R.id.tv_buzzer_ringtone);
        tv_buzzer_ringtone2 = findViewById(R.id.tv_buzzer_ringtone2);
        tv_select_map_display = findViewById(R.id.tv_select_map_display);
        tv_select_map_display2 = findViewById(R.id.tv_select_map_display2);
        tv_fresh_interval = findViewById(R.id.tv_fresh_interval);
        tv_fresh_interval2 = findViewById(R.id.fresh_interval2);
        ll_tv_buzzer_interval = findViewById(R.id.ll_tv_buzzer_interval);
        ll_tv_notification_ringtone = findViewById(R.id.ll_tv_notification_ringtone);
        ll_tv_buzzer_ringtone = findViewById(R.id.ll_tv_buzzer_ringtone);
        ll_tv_select_map_display = findViewById(R.id.ll_tv_select_map_display);
        ll_tv_fresh_interval = findViewById(R.id.ll_tv_fresh_interval);

        switch_notification_sound = findViewById(R.id.switch_notification_sound);
        playRingtoneSwitch = findViewById(R.id.switch_play_ringtone);
        defaultSwitch = findViewById(R.id.switch_default_ringtone);
        silentSwitch = findViewById(R.id.switch_silent_ringtone);

        check_box_volume = findViewById(R.id.check_box_volume);
        notificationCb = findViewById(R.id.notificationCb);
        switch_notification_sound.setOnClickListener(mCheckBoxClickListener);
        check_box_volume.setOnClickListener(mCheckBoxClickListener);
        notificationCb.setOnClickListener(mCheckBoxClickListener);


        // TODO: 1/31/2020
        //check_box_notification_sound - NOTIFICATION_CHECK
        String notification_check = AppPreferences.loadPreferences(mContext, VariablesConstant.NOTIFICATION_CHECK);
//        Log.i(TAG,"NOTIFICATION_CHECK: "+notification_check);
        if (notification_check.equalsIgnoreCase("true")){
//            Log.i(TAG,"switch_notification_sound is Checked");
            switch_notification_sound.setChecked(true);
        }else {
            switch_notification_sound.setChecked(false);
//            Log.i(TAG,"switch_notification_sound is not Checked");
        }

        //switch_play_ringtone - VOLUME_CHECK
        String ringtone_check = AppPreferences.loadPreferences(mContext, VariablesConstant.VOLUME_CHECK);
//        Log.i(TAG,"NOTIFICATION_CHECK: "+ringtone_check);
        if (ringtone_check.equalsIgnoreCase("true")){
            playRingtoneSwitch.setChecked(true);
//            Log.i(TAG,"playRingtoneSwitch is Checked");
        }else {
            playRingtoneSwitch.setChecked(false);
//            Log.i(TAG,"playRingtoneSwitch is not Checked ");
        }


        // playRingtoneSwitch
        playRingtoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                Log.i(TAG,"switch_play_ringtone is Checked");
                AppPreferences.savePreferences(mContext, VariablesConstant.VOLUME_CHECK, String.valueOf(true));
            } else {
//                Log.i(TAG,"switch_play_ringtone is not Checked");
                AppPreferences.savePreferences(mContext, VariablesConstant.VOLUME_CHECK, String.valueOf(false));
            }
        });

        switch_notification_sound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                Log.i(TAG,"switch_notification_sound is Checked");
                AppPreferences.savePreferences(mContext, VariablesConstant.NOTIFICATION_CHECK, String.valueOf(true));
            } else {
//                Log.i(TAG,"switch_notification_sound is not Checked");
                AppPreferences.savePreferences(mContext, VariablesConstant.NOTIFICATION_CHECK, String.valueOf(false));
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_tool_back_left:
                onBackPressed();
                break;

            case R.id.ll_tv_notification_ringtone:
                setRingtone(tv_notification_ringtone2, getString(R.string.notification_ringtone));
                break;

            case R.id.ll_tv_buzzer_ringtone:
                setRingtone(tv_buzzer_ringtone2, getString(R.string.buzzer_ringtone));
                break;

            case R.id.ll_tv_buzzer_interval:
                myShowDialog_buzzer_interval(mContext, getString(R.string.buzzer_interval));
                break;

            case R.id.ll_tv_select_map_display:
                myShowDialog_select_map_display(mContext,getString(R.string.select_the_map_display));
                break;

            case R.id.ll_tv_fresh_interval:
                myShowDialog_fresh_interval(mContext, getString(R.string.fresh_interval));
                break;

        }
    }

    private void setRingtone(TextView textView,  String title) {
        //Validate if at least one ringtone type is selected.
        if (!notificationCb.isChecked()) {
            Toast.makeText(mContext,"error : No_ringtone_type", Toast.LENGTH_SHORT).show();
            return;
        }

        //Application needs read storage permission for Builder.TYPE_MUSIC .
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog
                    .Builder(mContext, getSupportFragmentManager())

                    //Set title of the dialog.
                    //If set null, no title will be displayed.
//                    .setTitle("Select ringtone")
                    .setTitle(title)

                    //set the currently selected uri, to mark that ringtone as checked by default.
                    //If no ringtone is currently selected, pass null.
                    .setCurrentRingtoneUri(mCurrentSelectedUri)

                    //Allow user to select default ringtone set in phone settings.
                    .displayDefaultRingtone(defaultSwitch.isChecked())

                    //Allow user to select silent (i.e. No ringtone.).
                    .displaySilentRingtone(silentSwitch.isChecked())

                    //set the text to display of the positive (ok) button.
                    //If not set OK will be the default text.
                    .setPositiveButtonText("SET RINGTONE")

                    //set text to display as negative button.
                    //If set null, negative button will not be displayed.
                    .setCancelButtonText("CANCEL")

                    //Set flag true if you want to play the sample of the clicked tone.
                    .setPlaySampleWhileSelection(playRingtoneSwitch.isChecked())

                    //Set the callback listener.
                    .setListener(new RingtonePickerListener() {
                        @Override
                        public void OnRingtoneSelected(@NonNull String ringtoneName, Uri ringtoneUri) {
                            mCurrentSelectedUri = ringtoneUri;
                            textView.setText(String.format("Ringtone: %s", ringtoneName));
//                            textView.setText(String.format("Ringtone : %s\nUri : %s", ringtoneName, ringtoneUri));

                            if (title.equalsIgnoreCase( getString(R.string.notification_ringtone))){
                                AppPreferences.savePreferences(mContext, VariablesConstant.NOTIFICATION_URI_RINGTONE, String.valueOf(ringtoneUri));
                                AppPreferences.savePreferences(mContext, VariablesConstant.RINGTONE_SOUND_NAME, ringtoneName);
//                                Log.i(TAG,getString(R.string.notification_ringtone)+" "+ringtoneUri);
                            }else  if (title.equalsIgnoreCase( getString(R.string.buzzer_ringtone))){
                                AppPreferences.savePreferences(mContext, VariablesConstant.Buzzer_URI_RINGTONE,  String.valueOf(ringtoneUri));
                                AppPreferences.savePreferences(mContext, VariablesConstant.Buzzer_SOUND_NAME,  ringtoneName);
                            }
                        }
                    });


            //Add the desirable ringtone types.
            if (notificationCb.isChecked()){
                ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
                AppPreferences.savePreferences(mContext, VariablesConstant.RINGTONE_CHECK, String.valueOf(true));
            }

            // TODO: 1/31/2020
            if (switch_notification_sound.isChecked()){
                ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
                AppPreferences.savePreferences(mContext, VariablesConstant.TYPE_NOTIFICATION, String.valueOf(RingtonePickerDialog.Builder.TYPE_NOTIFICATION));
                AppPreferences.savePreferences(mContext, VariablesConstant.NOTIFICATION_CHECK, String.valueOf(true));
            }


          /*  if (musicCb.isChecked())
                ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_MUSIC);
            if (ringtoneCb.isChecked())
                ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);
            if (alarmCb.isChecked())
                ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);
*/
            //Display the dialog.
            ringtonePickerBuilder.show();
        } else {
            ActivityCompat.requestPermissions(SettingsActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }

    }

    private void myShowDialog_buzzer_interval(Context activity, String title) {
        String[] listItems = {getString(R.string.second_15),getString(R.string.second_20),getString(R.string.second_25)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);

        int checkedItem = 0; //this will checked the item when user open the dialog
        builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                tv_buzzer_interval2.setText("Buzzer Interval: " + listItems[which]);
                AppPreferences.savePreferences(mContext, VariablesConstant. Buzzer_Interval, listItems[which]);
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void myShowDialog_select_map_display(Context activity, String title) {
        String[] listItems = {getString(R.string.satelliteView),getString(R.string.normalView),getString(R.string.terranView)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);

        int checkedItem = 0; //this will checked the item when user open the dialog

        builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_select_map_display2.setText("Mapview: " + listItems[which]);
                AppPreferences.savePreferences(mContext, VariablesConstant. MapView_Type, listItems[which]);
//                Log.i(TAG," Buzzer_Interval: "+AppPreferences.loadPreferences(mContext, VariablesConstant. MapView_Type));
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    // refresh_intervalList
    private void myShowDialog_fresh_interval(Context activity, String title) {
        String[] listItems = {getString(R.string.second_15),getString(R.string.second_30),getString(R.string.second_45),getString(R.string.minute_1),getString(R.string.minute_2),getString(R.string.minute_3)};
        int checkedItem = 0; //this will checked the item when user open the dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_fresh_interval2.setText("Value: " + listItems[which]);
                AppPreferences.savePreferences(mContext, VariablesConstant. REFRESH_INTERVAL, listItems[which]);
//                Log.i(TAG," Buzzer_Interval: "+AppPreferences.loadPreferences(mContext, VariablesConstant.REFRESH_INTERVAL));

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }



}