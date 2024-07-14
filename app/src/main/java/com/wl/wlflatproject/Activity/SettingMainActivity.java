package com.wl.wlflatproject.Activity;

import static com.wl.wlflatproject.MUtils.HandlerCode.STOP_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.rockchip.gpadc.demo.CameraPreviewActivity;
import com.rockchip.gpadc.demo.ComputerServices;
import com.wl.wlflatproject.Adapter.SettingGuideAdapter;
import com.wl.wlflatproject.Bean.MainMsgBean;
import com.wl.wlflatproject.Fragment.AfterSaleFragment;
import com.wl.wlflatproject.Fragment.BindFragment;
import com.wl.wlflatproject.Fragment.DeviceDynamicsFragment;
import com.wl.wlflatproject.Fragment.DeviceInfoFragment;
import com.wl.wlflatproject.Fragment.OpenMachineFragment;
import com.wl.wlflatproject.Fragment.SystemNetFragment;
import com.wl.wlflatproject.Fragment.SystemSettingFragment;
import com.wl.wlflatproject.Fragment.SystemUpdateFragment;
import com.wl.wlflatproject.MUtils.DateUtils;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.MView.NormalDialog;
import com.wl.wlflatproject.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    View backIv;
    private TimeReceiver timeReceiver;

    private SettingGuideAdapter mGuideAdapter;
    private String[] title = {"设备信息", "网络设置", "设备绑定", "设备动态", "系统设置", "开门机设置", "售后服务", "智能防夹","工程模式","系统升级"};
    /**
     * tag默认key
     */
    public static final String POSITION_PARAM_KEY = "POSITION";

    private int[] titleIcon = {R.mipmap.ic_device_info, R.mipmap.ic_net_coin, R.mipmap.ic_device_bind
            , R.mipmap.ic_device_state, R.mipmap.ic_sys_setting,
            R.mipmap.ic_door_opener, R.mipmap.ic_after_sales,R.mipmap.fangjia_iv,R.mipmap.ic_sys_setting,R.mipmap.ic_sys_setting};
    private Fragment[] fragments = new Fragment[title.length];

    private int tabPosition = 0;
    private NormalDialog normalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        timeReceiver = new TimeReceiver();
        setTime();
        initData();
        backIv.setOnClickListener(v -> finish());
    }


    private void initData() {
        tabPosition = getIntent().getIntExtra(POSITION_PARAM_KEY, 0);
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
        //设置默认打开的tag
        switchFragment(tabPosition);
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
        //注册时间变化广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //广播解绑
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
                    fragment = new SystemNetFragment();
                    fragments[position] = fragment;
                    break;
                case 2:
                    // 设备绑定
                    fragment = new BindFragment();
                    fragments[position] = fragment;
                    break;
                case 3:
                    // 设备动态
                    fragment = new DeviceDynamicsFragment();
                    fragments[position] = fragment;
                    break;
                case 4:
                    // 系统设置
                    fragment = new SystemSettingFragment();
                    fragments[position] = fragment;
                    break;
                case 5:
                    // 开门机设置
                    fragment = new OpenMachineFragment();
                    fragments[position] = fragment;
                    break;
                case 6:
                    // 售后服务
                    fragment = new AfterSaleFragment();
                    fragments[position] = fragment;
                    break;
                case 7:
                    // 智能防夹
                    if (normalDialog == null)
                        normalDialog = new NormalDialog(this, R.style.mDialog);
                    normalDialog.show();
                    normalDialog.setTitleText("智能防夹");
                    if(!ComputerServices.mStopInference){
                        normalDialog.setContentText("关闭防夹");
                    }else{
                        normalDialog.setContentText("打开防夹");
                    }
                    normalDialog.getConfirmTv().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!ComputerServices.mStopInference){
                                EventBus.getDefault().post(new MainMsgBean(23));
                            }else{
                                EventBus.getDefault().post(new MainMsgBean(24));
                            }
                            normalDialog.dismiss();
                        }
                    });
                    break;
                case 8:
                    // 工程模式
                    if (normalDialog == null)
                        normalDialog = new NormalDialog(this, R.style.mDialog);
                    normalDialog.show();
                    normalDialog.setTitleText("工程模式");
                    normalDialog.setContentText("点击确定进入工程模式");
                    normalDialog.getConfirmTv().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EventBus.getDefault().post(new MainMsgBean(STOP_SERVICE));
                            normalDialog.dismiss();
                            Intent intent3=new Intent(SettingMainActivity.this, CameraPreviewActivity.class);
                            startActivityForResult(intent3,400);
                        }
                    });
                    break;
                case 9:
                    //系统升级
                    fragment = new SystemUpdateFragment();
                    fragments[position] = fragment;
                    break;
                default:
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TimeEvent bean) {
        setTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static class TimeReceiver extends BroadcastReceiver {
        public TimeReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_TIME_CHANGED.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                    || Intent.ACTION_TIME_TICK.equals(action)) {
                // 处理时间变化的逻辑
                EventBus.getDefault().post(new TimeEvent());
            }
        }
    }

    public static class TimeEvent {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==400){
            boolean fangJiaIsStop = SPUtil.getInstance(this).getSettingParam("fangJiaIsStop", true);
            if(!fangJiaIsStop){
                EventBus.getDefault().post(new MainMsgBean(24));
            }
        }
    }
}