package com.backstagesupporters.fasttrack.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.backstagesupporters.fasttrack.MyApplication;
import com.backstagesupporters.fasttrack.R;
import com.backstagesupporters.fasttrack.utils.language.Utility;
import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemoFragment extends Fragment {
    //    private String TAG= getClass().getSimpleName();
    private static final String TAG = "fasttrack";
    private Context mContext;
    TextInputEditText ed_name,ed_email_address,ed_phone_number,ed_date_of_demo,ed_time_slot;
    EditText ed_massage;
    TextView btn_send_massage;


    public DemoFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_demo, container, false);
        mContext = getActivity();

        findViewById(view);
//        setAdapter();

        btn_send_massage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(mContext, "Welcome", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    private void findViewById(View view) {
        ed_name = view.findViewById(R.id.ed_name);
        ed_email_address = view.findViewById(R.id.ed_email_address);
        ed_phone_number = view.findViewById(R.id.ed_phone_number);
        ed_date_of_demo = view.findViewById(R.id.ed_date_of_demo);
        ed_time_slot = view.findViewById(R.id.ed_time_slot);
        ed_massage = view.findViewById(R.id.ed_massage);
        btn_send_massage = view.findViewById(R.id.btn_send_massage);
//         = view.findViewById(R.id.);

    }

}
