package com.backstagesupporters.fasttrack.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.FloatRange;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.ui.user.LoginsActivity;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;


import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

public class OnboardingActivity extends MaterialIntroActivity {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_onboarding);
        mContext = OnboardingActivity.this;
        AppPreferences.savePreferences(mContext, VariablesConstant.ONBOARDING_COMPLETE, "1");


        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.transparent)
                .buttonsColor(R.color.second_slide_buttons)
                .title(getString(R.string.gps))
                .description(getString(R.string.gps_details))
                .image(R.drawable.gps1)
                .build());


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.transparent)
                .buttonsColor(R.color.second_slide_buttons)
                .title(getString(R.string.fastags))
                .description(getString(R.string.fastags_details))
                .image(R.drawable.gps2)
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.transparent)
                .buttonsColor(R.color.second_slide_buttons)
                .title(getString(R.string.insurance))
                .description(getString(R.string.insurance_details))
                .image(R.drawable.gps3)
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.transparent)
                .buttonsColor(R.color.second_slide_buttons)
                .title(getString(R.string.saving))
                .description(getString(R.string.saving_details))
                .image(R.drawable.gps4)
                .build());


//        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard(getString(R.string.gps), getString(R.string.gps_details), R.drawable.gps1);
//        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard(getString(R.string.fastags),getString(R.string.fastags_details), R.drawable.gps2);
//        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard(getString(R.string.insurance),getString(R.string.gps_details), R.drawable.gps3);
//        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard(getString(R.string.saving),getString(R.string.saving_details), R.drawable.gps4);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, LoginsActivity.class));
        finish();
    }


}
