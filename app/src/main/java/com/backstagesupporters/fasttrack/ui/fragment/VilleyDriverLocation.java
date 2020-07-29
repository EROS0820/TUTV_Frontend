package com.backstagesupporters.fasttrack.ui.fragment;


import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;

import io.gloxey.gnm.interfaces.VolleyResponse;
import io.gloxey.gnm.managers.ConnectionManager;


public class VilleyDriverLocation  {

    private  static String myResponse="";
    public  static String CallVehicles(Context context, String url){


        /**
         * Method GET (without header)
         *  ConnectionManager.volleyStringRequest(context, isDialog, progressView, requestURL, volleyResponseInterface);
         *  https://github.com/adnanbinmustafa/Gloxey-Network-Manager#volley-stringrequest-1
          */
        ConnectionManager.volleyJSONRequest(context, false, null, url, new VolleyResponse() {
            @Override
            public void onResponse(String _response) {

                /**
                 * Handle Response
                 */
                myResponse = _response;
                Log.w("VilleyDriver myResponse",myResponse);

            }

            @Override
            public void onErrorResponse(VolleyError error) {

                /**
                 * handle Volley Error
                 */

                error.printStackTrace();
                error.getNetworkTimeMs();
            }

            @Override
            public void isNetwork(boolean connected) {

                /**
                 * True if internet is connected otherwise false
                 */
            }
        });
        return myResponse;
    }

}