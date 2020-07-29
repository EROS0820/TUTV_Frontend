package com.backstagesupporters.fasttrack.utils.vehicle;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.utils.CommonMap;

// TODO: 3/5/2020
public class VehicleTypesAndColors {
    private String TAG= getClass().getSimpleName();
    private Context context;

    public VehicleTypesAndColors(Context mContext) {
        this.context = mContext;
    }

    // colorVehicle, vehicleType
    public int setCarColor(String colorCar, String vehicleType) {
        //Log.w(TAG,"setCarColor1 VehicleType: "+vehicleType);
        //Log.w(TAG,"setCarColor2 colorCar : "+colorCar);
        if (colorCar != null)
        switch (colorCar) {
            case "green":
                return getGreenIcon(vehicleType);

            case "red":
                return getRedIcon(vehicleType);

            case "yellow":
                return getYellowIcon(vehicleType);

            case "blue":
                return getBlueIcon(vehicleType);

            default:
                return getBlueIcon(vehicleType);
        }
        else
            return getBlueIcon(vehicleType);
    }



    // return type colorIcon
    public int getWhiteIcon(String vehicleType){
//        Log.e(TAG,"getGreenIcon vehicleType : "+vehicleType);
        if (vehicleType != null)
        switch (vehicleType){
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_white;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.CAR:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_white;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.BUS:
                return R.drawable.ic_car_white;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_white;

            default:
                return R.drawable.ic_car_white;
        }
        else
            return R.drawable.ic_car_white;
    }

    private int getGreenIcon(String vehicleType){
//        Log.e(TAG,"getGreenIcon vehicleType : "+vehicleType);
        switch (vehicleType){
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.scooter_green;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_green;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.chota_hathi_green;

            case CommonMap.VehicleType.CAR:
                return R.drawable.car_green;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.crane_green;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.rickshaw_green;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_green;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.taxi_green;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.tractor_green;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ambulance_green;

            case CommonMap.VehicleType.BUS:
                return R.drawable.bus_green;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_green;
//                return R.drawable.test2;

            default:
                return R.drawable.car_green;
        }
    }

    private int getRedIcon(String vehicleType){
//        Log.e(TAG," getRedIcon vehicleType : "+vehicleType);
        switch (vehicleType){
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.scooter_red;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_red;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.chota_hathi_red;

            case CommonMap.VehicleType.CAR:
                return R.drawable.car_red;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.crane_red;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.rickshaw_red;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_red;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.taxi_red;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.tractor_red;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ambulance_red;

            case CommonMap.VehicleType.BUS:
                return R.drawable.bus_red;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_red;

            default:
                return R.drawable.car_red;
        }
    }

    private int getYellowIcon(String vehicleType){
//        Log.e(TAG," getYellowIcon type : "+vehicleType);
        switch (vehicleType){
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.scooter_yellow;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_yellow;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.chota_hathi_yellow;

            case CommonMap.VehicleType.CAR:
                return R.drawable.car_yellow;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.crane_yellow;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.rickshaw_yellow;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_yellow;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.taxi_yellow;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.tractor_yellow;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ambulance_yellow;

            case CommonMap.VehicleType.BUS:
                return R.drawable.bus_yellow;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_yellow;

            default:
                return R.drawable.car_yellow;
        }
    }

    private int getBlueIcon(String vehicleType){
        Log.e(TAG,"getBlueIcon vehicleType : "+vehicleType);

        if (vehicleType != null){
        switch (vehicleType) {
            case CommonMap.VehicleType.SCOOTER:
                return R.drawable.scooter_blue;

            case CommonMap.VehicleType.BIKE:
                return R.drawable.bike_blue;

            case CommonMap.VehicleType.CHOTA_HATHI:
                return R.drawable.chota_hathi_blue;

            case CommonMap.VehicleType.CAR:
                return R.drawable.car_blue;

            case CommonMap.VehicleType.CRANE:
                return R.drawable.crane_blue;

            case CommonMap.VehicleType.ERICKSHAW:
                return R.drawable.rickshaw_blue;

            case CommonMap.VehicleType.JCB:
                return R.drawable.jcb_blue;

            case CommonMap.VehicleType.TAXI:
                return R.drawable.taxi_blue;

            case CommonMap.VehicleType.TRACTOR:
                return R.drawable.tractor_blue;

            case CommonMap.VehicleType.AMBULANCE:
                return R.drawable.ambulance_blue;

            case CommonMap.VehicleType.BUS:
                return R.drawable.bus_blue;

            case CommonMap.VehicleType.TRUCK:
                return R.drawable.truck_blue;

            default:
                return R.drawable.car_blue;
        }

        }
        else
            return R.drawable.car_blue;
    }


}
