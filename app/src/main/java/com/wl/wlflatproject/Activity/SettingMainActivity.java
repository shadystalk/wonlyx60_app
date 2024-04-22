package com.wl.wlflatproject.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wl.wlflatproject.Adapter.SettingGuideAdapter;
import com.wl.wlflatproject.Fragment.DeviceInfoFragment;
import com.wl.wlflatproject.Fragment.SystemSettingFragment;
import com.wl.wlflatproject.MUtils.DateUtils;
import com.wl.wlflatproject.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingMainActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.guide_rv)
    RecyclerView guideRv;
    @BindView(R.id.back_iv)
    ImageView backIv;
    private TimeReceiver timeReceiver;

    private SettingGuideAdapter mGuideAdapter;
    private String[] title = {"设备信息", "网络设置", "设备绑定", "系统密码", "设备动态", "系统设置", "遥感设置", "开门机设置", "售后服务", "恢复出厂"};

    private int[] titleIcon = {R.mipmap.ic_device_info, R.mipmap.ic_net_coin, R.mipmap.ic_device_bind
            , R.mipmap.ic_sys_pwd, R.mipmap.ic_device_state, R.mipmap.ic_sys_setting, R.mipmap.ic_bluetooth,
            R.mipmap.ic_door_opener, R.mipmap.ic_after_sales, R.mipmap.ic_reset};

    private Fragment[] fragments = new Fragment[title.length];

    private int tabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        ButterKnife.bind(this);
        timeReceiver = new TimeReceiver();
        setTime();

        initData();
        backIv.setOnClickListener(v -> finish());
    }


    private void initData() {
        List<SettingGuideAdapter.GuideBean> guideList = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            SettingGuideAdapter.GuideBean guideBean = new SettingGuideAdapter.GuideBean(titleIcon[i], title[i]);
            if (i == tabPosition) {
                guideBean.setSelect(true);
            }
            guideList.add(guideBean);
        }
        mGuideAdapter = new SettingGuideAdapter(guideList);
        mGuideAdapter.setOnItemClickListener(this);
        RecyclerView.ItemAnimator itemAnimator = guideRv.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        guideRv.setAdapter(mGuideAdapter);
        guideRv.setLayoutManager(new LinearLayoutManager(this));

        // 默认选中第一个 Tab
        switchFragment(0);
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
        ((SettingGuideAdapter.GuideBean) baseQuickAdapter.getData().get(tabPosition)).setSelect(false);
        baseQuickAdapter.notifyItemChanged(tabPosition);
        ((SettingGuideAdapter.GuideBean) baseQuickAdapter.getData().get(position)).setSelect(true);
        baseQuickAdapter.notifyItemChanged(position);
        tabPosition = position;
        switchFragment(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(timeReceiver);
    }

    /**
     * 更新时间
     */
    private void setTime() {
        String timeString = DateUtils.getInstance().dateFormat6(System.currentTimeMillis());
        timeTv.setText(timeString);
    }

    /**
     * 切换fragment
     *
     * @param position
     */
    private void switchFragment(int position) {
        Fragment fragment = fragments[position];
        if (fragment == null) {
            switch (position) {
                case 0:
                    // 设备信息
                    fragment = new DeviceInfoFragment();
                    Bundle bundle = new Bundle();
                    fragment.setArguments(bundle);
                    fragments[position] = fragment;
                    break;
                case 1:
                    // 网络设置
                    fragment = new DeviceInfoFragment();
                    fragments[position] = fragment;
                    break;
                case 2:
                    // 设备绑定
                    fragment = new DeviceInfoFragment();
                    fragments[position] = fragment;
                    break;
                case 3:
                    // 系统密码
                    fragment = new DeviceInfoFragment();
                    fragments[position] = fragment;
                    break;
                case 4:
                    // 设备动态
                    fragment = new DeviceInfoFragment();
                    fragments[position] = fragment;
                    break;
                case 5:
                    // 系统设置
                    fragment = new SystemSettingFragment();
                    fragments[position] = fragment;
                    break;
                case 6:
                    // 遥感设置
                    fragment = new DeviceInfoFragment();
                    fragments[position] = fragment;
                    break;
                case 7:
                    // 开门机设置
                    fragment = new DeviceInfoFragment();
                    fragments[position] = fragment;
                    break;
                case 8:
                    // 售后服务
                    fragment = new DeviceInfoFragment();
                    fragments[position] = fragment;
                    break;
                case 9:
                    // 恢复出厂
                    fragment = new DeviceInfoFragment();
                    fragments[position] = fragment;
                    break;
            }
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.setting_content, fragment);
            fragmentTransaction.commit();
        }
    }

    public class TimeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_TIME_CHANGED.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                    || Intent.ACTION_TIME_TICK.equals(action)) {
                // 处理时间变化的逻辑
                setTime();
            }
        }
    }

}