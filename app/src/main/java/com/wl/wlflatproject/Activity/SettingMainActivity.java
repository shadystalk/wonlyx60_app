package com.wl.wlflatproject.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qtimes.service.wonly.client.QtimesServiceManager;
import com.wl.wlflatproject.Adapter.SettingGuideAdapter;
import com.wl.wlflatproject.MUtils.DateUtils;
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
    private String[] title = {"设备信息", "网络设置", "设备绑定", "设备动态", "系统设置", "开门机设置", "售后服务","防夹激活","智能防夹", "工程模式","设备重启"};
    /**
     * tag默认key
     */
    public static final String POSITION_PARAM_KEY = "POSITION";

    private int[] titleIcon = {R.mipmap.ic_device_info, R.mipmap.ic_net_coin, R.mipmap.ic_device_bind
            , R.mipmap.ic_device_state, R.mipmap.ic_sys_setting,
            R.mipmap.ic_door_opener,R.mipmap.ic_after_sales,R.mipmap.fangjia, R.mipmap.fangjia, R.mipmap.ic_sys_setting,R.mipmap.ic_device_state};


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
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
        ((SettingGuideAdapter.GuideBean) baseQuickAdapter.getData().get(tabPosition)).setSelect(false);
        baseQuickAdapter.notifyItemChanged(tabPosition);
        ((SettingGuideAdapter.GuideBean) baseQuickAdapter.getData().get(position)).setSelect(true);
        baseQuickAdapter.notifyItemChanged(position);
        tabPosition = position;
        switch (position){
            case 0:
                Intent intent=new Intent(SettingMainActivity.this,DeviceInfoActivity.class);
                startActivity(intent);
                break;
            case 1:
                Intent intent1=new Intent(SettingMainActivity.this,SystemNetActivity.class);
                startActivity(intent1);
                break;
            case 2:
                Intent intent2=new Intent(SettingMainActivity.this,BindActivity.class);
                startActivity(intent2);
                break;
            case 3:
                Intent intent3=new Intent(SettingMainActivity.this,DeviceDynamicsActivity.class);
                startActivity(intent3);
                break;
            case 4:
                Intent intent4=new Intent(SettingMainActivity.this,SystemSettingActivity.class);
                startActivity(intent4);
                break;
            case 5:
                Intent intent5=new Intent(SettingMainActivity.this,OpenMachineActivity.class);
                startActivity(intent5);
                break;
            case 6:
                Intent intent6=new Intent(SettingMainActivity.this,AfterSaleActivity.class);
                startActivity(intent6);
                break;
            case 7:
                if (!QtimesServiceManager.instance().isServerActive()) {
                    QtimesServiceManager.instance().connect(SettingMainActivity.this);
                }
                Intent intent7 = new Intent();
                intent7.setClassName("com.qtimes.wonly", "com.qtimes.wonly.activity.device.DevACActivity");
                startActivity(intent7);
                break;
            case 8:
                boolean a = QtimesServiceManager.instance().getAntiPinchStatus();
                if (normalDialog == null)
                    normalDialog = new NormalDialog(this, R.style.mDialog);
                normalDialog.show();
                normalDialog.setTitleText("防夹");
                if(a){
                    normalDialog.setContentText("点击关闭防夹");
                }else {
                    normalDialog.setContentText("点击开启防夹");
                }
                normalDialog.getConfirmTv().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        normalDialog.dismiss();
                        if (!QtimesServiceManager.instance().isServerActive()) {
                            QtimesServiceManager.instance().connect(SettingMainActivity.this);
                        }
                        boolean b = QtimesServiceManager.instance().resetAntiPinch();
                    }
                });
                break;
            case 9:
                boolean a1 = QtimesServiceManager.instance().getAntiPinchStatus();
                if(!a1){
                    ToastUtils.showShort("智能防夹已关闭");
                    return;
                }
                try {
                    if (!QtimesServiceManager.instance().isServerActive()) {
                        QtimesServiceManager.instance().connect(this);
                    }
                    QtimesServiceManager instance = QtimesServiceManager.instance();
                    instance.recoveryMode();
                } catch (Exception e) {
                    String s = e.toString();
                    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                }
                break;
            case 10:
                if (normalDialog == null)
                    normalDialog = new NormalDialog(this, R.style.mDialog);
                normalDialog.show();
                normalDialog.setTitleText("系统重启");
                normalDialog.setContentText("点击确定系统重启");
                normalDialog.getConfirmTv().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        normalDialog.dismiss();
                        if (!QtimesServiceManager.instance().isServerActive()) {
                            QtimesServiceManager.instance().connect(SettingMainActivity.this);
                        }
                        QtimesServiceManager.instance().reboot();
                    }
                });
                break;

        }
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
        hideBottomUIMenu();
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
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 16) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN //hide statusBar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; //hide navigationBar
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }
}