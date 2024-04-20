package com.wl.wlflatproject.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wl.wlflatproject.R;

public class DeviceInfoFragment extends Fragment {
    /**
     * 产品型号，MAC地址，软件版本，前板版本，后板版本
     */
    private TextView mModelTv, mMacTv, mSoftwareTv, mPreviousTv,mAfterTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dev_info_fragment, container, false);
        // Find views and set listeners
        mModelTv = view.findViewById(R.id.model_text);
        mMacTv = view.findViewById(R.id.mac_text);
        mSoftwareTv = view.findViewById(R.id.software_version_text);
        mPreviousTv = view.findViewById(R.id.previous_version_text);
        mAfterTv = view.findViewById(R.id.after_version_text);
        return view;
    }
}
