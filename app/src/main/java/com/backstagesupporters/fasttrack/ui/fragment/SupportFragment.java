package com.backstagesupporters.fasttrack.ui.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.backstagesupporters.fasttrack.MyApplication;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.HomeActivity;
import com.backstagesupporters.fasttrack.utils.language.Utility;
import com.backstagesupporters.fasttrack.models.Support;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiClient;
import com.backstagesupporters.fasttrack.retrofitAPI.ApiInterface;
import com.backstagesupporters.fasttrack.retrofitAPI.ErrorUtils;
import com.backstagesupporters.fasttrack.shared_pref.AppPreferences;
import com.backstagesupporters.fasttrack.utils.CheckNetwork;
import com.backstagesupporters.fasttrack.utils.VariablesConstant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends Fragment implements View.OnClickListener {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    private TextInputEditText edt_name,edt_mobileno,edit_support;
    TextView tv_submit;

    String name,phone_number,support;
    ApiInterface apiInterface;
    ProgressDialog pd;
    String loginStatus="",userType ="",token="";


    public SupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MyApplication.localeManager.onAttach(context);
        Utility.resetActivityTitle(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        mContext = getActivity();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        findViewById(view);
        tv_submit.setOnClickListener(this);
        token = AppPreferences.loadPreferences(mContext, VariablesConstant.TOKEN);
        userType = AppPreferences.loadPreferences(mContext, VariablesConstant.USER_TYPE);

        return view;
    }


    private void findViewById(View view) {
        edt_name = view .findViewById(R.id.edt_name);
        edt_mobileno = view .findViewById(R.id.edt_mobileno);
        edit_support = view .findViewById(R.id.edit_support);
        tv_submit = view .findViewById(R.id.tv_submit);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        int id =view.getId();
        if ( id== R.id.tv_submit) {
            doValidation();


//            case R.id.iv_tool_back_left:
//                onBackPressed();
//                break;
        }
    }


    private void doValidation() {
        name = edt_name.getText().toString().trim();
        phone_number = edt_mobileno.getText().toString().trim();
        support = edit_support.getText().toString().trim();

        if (name.equalsIgnoreCase("")) {
            edt_name.setError(getString(R.string.Please_Enter_driver_name));
            edt_name.requestFocus();
        }else if (support.isEmpty()){
            edit_support.setError(getString(R.string.support1));
            edit_support.requestFocus();
        }else if (phone_number.isEmpty()){
            edt_mobileno.setError(getString(R.string.err_msg_mobile));
            edt_mobileno.requestFocus();
        }else if (edt_mobileno.length() < 9) {
            edt_mobileno.setError(getString(R.string.err_msg_mobile_too_short));
            edt_mobileno.requestFocus();
        } else if (!Patterns.PHONE.matcher(phone_number).matches()){
            edt_mobileno.setError(getString(R.string.err_msg_mobile_invalid));
            edt_mobileno.requestFocus();
        }else if (phone_number.length()>12){
            edt_mobileno.setError(getString(R.string.err_msg_mobile_too_long));
            edt_mobileno.requestFocus();
        } else {
            // ========== Api Call =============
            if (CheckNetwork.isNetworkAvailable(mContext)) {
                getSupportCall(token,name,phone_number,support);

            } else {
                Toast.makeText(mContext, "Check Internet Connection !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getSupportCall(String token, String name, String phone_number, String support) {
        Support supportModel = new Support(token,support, name, phone_number);
//        Call<LoginResponse> call = apiInterface.supportQuery2(token,support, name, phone_number);
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(true);
        pd.show();

        Call<JsonElement> call =  apiInterface.support(supportModel);
//        Call<JsonElement> call =  apiInterface.complaint(supportModel);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
//                Log.e(TAG, "Response >>>>" + str_response);
                int  responseCode  = response.code();
//                Log.e(TAG, "responseCode :" + responseCode);
                try {
                    if (response.isSuccessful()&& responseCode == 200) {
                        JSONObject jsonObject = new JSONObject(str_response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("200")){
//                            JSONObject data = jsonObject.getJSONObject("driver");
//                            String driver_name = data.getString("driver_name");
//                            String driver_dob = data.getString("driver_dob");
//                            Log.e(TAG,"driver_name- "+driver_name);
//                            Log.e(TAG,"driver_dob- "+driver_dob);

                            startActivity(new Intent(mContext, HomeActivity.class));

                        }

//                        Log.e(TAG,"JSONObject message-"+message);
                        Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();

                    } else {
                        // error case
                        ErrorUtils.apiResponseErrorHandle(TAG,responseCode, mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,""+e.toString());
//                    Toasty.error(mContext, "We cant find an account with this credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                pd.dismiss();
                Log.e(TAG,"onFailure"+t.toString());
            }
        });
    }


}
