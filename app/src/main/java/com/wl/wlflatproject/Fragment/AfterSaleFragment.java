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

public class AfterSaleFragment extends Fragment {
    private TextView mModelTv, mMacTv, mSoftwareTv, mPreviousTv,mAfterTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.after_sale_fragment, container, false);

        initData();
        return view;
    }

    public void initData(){


    }
}
