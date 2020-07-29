package com.backstagesupporters.fasttrack.utils;

public class MyUtility {

    //// PS : true = 1 and false = 0
    /**
     * {getString(R.string.second_15),getString(R.string.second_30),getString(R.string.second_45),
     * getString(R.string.minute_1),getString(R.string.minute_2),getString(R.string.minute_3)};
     * 1 Second -> 1000 miliSeconds;
     */
    public static int getRefreshTime(String time) {
        int mTime = Integer.parseInt(time);
        int miliSeconds = 1000;
        int myTime = mTime*miliSeconds;
//        Log.e(TAG,"getRefreshTime myTime: "+myTime);
        return myTime;
    }







}
