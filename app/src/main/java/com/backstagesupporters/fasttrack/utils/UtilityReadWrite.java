package com.backstagesupporters.fasttrack.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class UtilityReadWrite {



    // add-write text into file
    public static void writeJsonToFile(Context context, String fileName, String data) {
        try {
            FileOutputStream fileout = context.openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(data);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // Read text from file
    public static String readJsonFromFile(Context context, String fileName) {
        String ret = "";
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            ret = sb.toString();
        } catch (FileNotFoundException e) {
            Log.e("HistoryReply activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("HistoryReply activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    public static String convertUTCToDateFormat(String strDate) {

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");

        Date d = null;
        try {
            d = input.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formatted = output.format(d);
        return formatted;
    }



}
/**
 * http://learningprogramming.net/mobile/android/read-write-and-delete-file-in-internal-storage/
 *
 * https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
 *
 *
 */