package com.wl.wlflatproject.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.wl.wlflatproject.Adapter.PagerAdapter;
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
    @BindView(R.id.view_pager)
    ViewPager2 viewPager;

    private Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_dynamic_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        TabLayout.Tab tab = tabLayout.newTab();
        tabLayout.addTab(tab.setText("开门记录"));
        tab = tabLayout.newTab();
        tabLayout.addTab(tab.setText("告警消息"));

        // 创建适配器
        PagerAdapter adapter = new PagerAdapter(getActivity());

        // 添加 Fragment 到适配器
        adapter.addFragment( new OpenRecordFragment());
        adapter.addFragment(new AlarmMsgFragment());
        // 设置适配器给 ViewPager
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tabLayout.getSelectedTabPosition();
                viewPager.setCurrentItem(pos);
//                showFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setUserInputEnabled(false);
        initData();
        return view;
    }

    public void initData(){
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPager.setCurrentItem(0);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
