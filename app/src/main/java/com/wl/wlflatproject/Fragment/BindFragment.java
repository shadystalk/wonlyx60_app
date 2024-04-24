package com.wl.wlflatproject.Fragment;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wl.wlflatproject.MUtils.DpUtils;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.MUtils.SerialPortUtil;
import com.wl.wlflatproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BindFragment extends Fragment {
    @BindView(R.id.code_view)
    ImageView codeView;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bind_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    public void initData(){
        String devId = SPUtil.getInstance(getContext()).getSettingParam("devId", "");
        String devType = SPUtil.getInstance(getContext()).getSettingParam("devType", "");
        Bitmap code = DpUtils.getTowCode(getContext(), devType+devId);
        codeView.setImageBitmap(code);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
