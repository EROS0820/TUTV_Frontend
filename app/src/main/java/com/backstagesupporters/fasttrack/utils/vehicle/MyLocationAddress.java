package com.backstagesupporters.fasttrack.utils.vehicle;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyLocationAddress {
    private Context mContext;

    public MyLocationAddress(Context context) {
        this.mContext = context;
    }


    public String getAddress(double latitude, double longitude) {
        String  mAddress ="";

        //Geocoder
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            //List<Address>
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                mAddress = addressList.get(0).getAddressLine(0);
                String city = addressList.get(0).getLocality();
                String state = addressList.get(0).getAdminArea();
                String subArea = addressList.get(0).getSubAdminArea();
                String country = addressList.get(0).getCountryName();
                String postalCode = addressList.get(0).getPostalCode();
                String knownName = addressList.get(0).getFeatureName();
//                Log.w(TAG,"getAddress Address :"+mAddress);
//                tv_location.setText(mAddress);
//                AppPreferences.savePreferences(mContext, VariablesConstant.ADDRESS_LOCATION, mAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  mAddress;
    }


}
