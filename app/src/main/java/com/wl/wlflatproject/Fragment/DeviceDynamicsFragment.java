package com.wl.wlflatproject.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.wl.wlflatproject.MUtils.DpUtils;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 设备动态
 */
public class DeviceDynamicsFragment extends Fragment {
    @BindView(R.id.device_tablayout)
    TabLayout tabLayout;
    private Unbinder unbinder;
    private Fragment[] fragments = new Fragment[2];
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_dynamic_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        TabLayout.Tab tab = tabLayout.newTab();
        tabLayout.addTab(tab.setText("开门记录"));
        tab = tabLayout.newTab();
        tabLayout.addTab(tab.setText("告警消息"));
        fragments[0]=new OpenRecordFragment();
        fragments[1]=new AlarmMsgFragment();
        showFragment();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initData();
        return view;
    }

    public void initData(){
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
    private void  showFragment(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        int pos = tabLayout.getSelectedTabPosition();
        fragmentTransaction.replace(R.id.device_main_framelayout, fragments[pos]);
        fragmentTransaction.commit();
    }
}
