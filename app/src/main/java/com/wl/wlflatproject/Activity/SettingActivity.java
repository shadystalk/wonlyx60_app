package com.wl.wlflatproject.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wl.wlflatproject.Bean.MainMsgBean;
import com.wl.wlflatproject.Bean.SetMsgBean;
import com.wl.wlflatproject.MView.SetDialog;
import com.wl.wlflatproject.MView.WaitDialogTime;
import com.wl.wlflatproject.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.back)
    RelativeLayout back;
    @BindView(R.id.left_repair_degree)
    RelativeLayout leftRepairDegree;
    @BindView(R.id.right_repair_degree)
    RelativeLayout rightRepairDegree;
    @BindView(R.id.open_degree)
    RelativeLayout openDegree;
    @BindView(R.id.open_speed)
    RelativeLayout openSpeed;
    @BindView(R.id.close_power)
    RelativeLayout closePower;
    @BindView(R.id.close_speed)
    RelativeLayout closeSpeed;
    @BindView(R.id.left_repair_degree_tv)
    TextView leftRepairDegreeTv;
    @BindView(R.id.right_repair_degree_tv)
    TextView rightRepairDegreeTv;
    @BindView(R.id.open_degree_tv)
    TextView openDegreeTv;
    @BindView(R.id.open_speed_tv)
    TextView openSpeedTv;
    @BindView(R.id.close_speed_tv)
    TextView closeSpeedTv;
    @BindView(R.id.close_power_tv)
    TextView closePowerTv;
    @BindView(R.id.open_degree_repair_tv)
    TextView openDegreeRepairTv;
    @BindView(R.id.level_tv)
    TextView level_tv;
    private String value;
    private SetDialog setDialog;
    private SetDialog.ResultListener listener;
    private MainMsgBean mainMsgBean;
    private WaitDialogTime waitDialogTime;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        EventBus.getDefault().register(this);
        intent = getIntent();
        leftRepairDegreeTv.setText(intent.getStringExtra("leftDegreeRepair"));
        rightRepairDegreeTv.setText(intent.getStringExtra("rightDegreeRepair"));
        String openDegree = intent.getStringExtra("openDegree");
        switch (openDegree){
            case "72":
                openDegreeTv.setText("小");
                break;
            case "77":
                openDegreeTv.setText("适中");
                break;
            case "82":
                openDegreeTv.setText("大");
                break;
            case "87":
                openDegreeTv.setText("最大");
                break;
        }
        openSpeedTv.setText(intent.getStringExtra("openDoorSpeed"));
        closeSpeedTv.setText(intent.getStringExtra("closeDoorSpeed"));
        closePowerTv.setText(intent.getStringExtra("closePower"));
        openDegreeRepairTv.setText(intent.getStringExtra("openDegreeRepair"));
        level_tv.setText(intent.getStringExtra("level"));
        listener = new SetDialog.ResultListener() {
            @Override
            public void onResult(String value, int flag) {
                SettingActivity.this.value = value;
                if (mainMsgBean == null)
                    mainMsgBean = new MainMsgBean();
                mainMsgBean.setMsg(value);
                mainMsgBean.setFlag(flag);
                EventBus.getDefault().post(mainMsgBean);
                if (waitDialogTime == null)
                    waitDialogTime = new WaitDialogTime(SettingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                waitDialogTime.show();
            }
        };
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetMsgBean setMsgBean) {
        if (waitDialogTime != null & waitDialogTime.isShowing())
            waitDialogTime.dismiss();
        switch (setMsgBean.getFlag()) {
            case 1:
                Toast.makeText(SettingActivity.this, "设置左角度修复值成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(value))
                    leftRepairDegreeTv.setText(value);
                getIntent().putExtra("leftDegreeRepair", value);
                break;
            case 2:
                Toast.makeText(SettingActivity.this, "设置右角度修复值成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(value))
                    rightRepairDegreeTv.setText(value);
                getIntent().putExtra("rightDegreeRepair", value);
                break;
            case 3:
                Toast.makeText(SettingActivity.this, "设置开门角度成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(value)){
                    switch (value){
                        case "72":
                            value="72";
                            openDegreeTv.setText("小");
                            break;
                        case "77":
                            value="77";
                            openDegreeTv.setText("适中");
                            break;
                        case "82":
                            value="82";
                            openDegreeTv.setText("大");
                            break;
                        case "87":
                            value="87";
                            openDegreeTv.setText("最大");
                            break;
                    }
                }
                getIntent().putExtra("openDegree", value);
                break;
            case 5:
                Toast.makeText(SettingActivity.this, "设置开门速度成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(value))
                    openSpeedTv.setText(value);
                getIntent().putExtra("openDoorSpeed", value);
                break;
            case 6:
                Toast.makeText(SettingActivity.this, "设置关门速度成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(value))
                    closeSpeedTv.setText(value);
                getIntent().putExtra("closeDoorSpeed", value);
                break;
            case 7:
                Toast.makeText(SettingActivity.this, "开门成功", Toast.LENGTH_SHORT).show();
                if (waitDialogTime != null && waitDialogTime.isShowing())
                    waitDialogTime.dismiss();
                break;
            case 8:
                Toast.makeText(SettingActivity.this, "关门力度设置成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(value))
                    closePowerTv.setText(value);
                getIntent().putExtra("closePower", value);
                break;
            case 12:
                Toast.makeText(SettingActivity.this, "设置开门角度修复值成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(value))
                    openDegreeRepairTv.setText(value);
                getIntent().putExtra("openDegreeRepair", value);
                break;
            case 22:
                Toast.makeText(SettingActivity.this, "设置防夹等级成功", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(value))
                    level_tv.setText(value);
                getIntent().putExtra("level", value);
                break;

        }
        setResult(300, getIntent());
    }

    @Override
    protected void onResume() {
        hideBottomUIMenu();
        super.onResume();
    }

    @OnClick({R.id.level, R.id.open_degree_repair, R.id.back, R.id.left_repair_degree, R.id.right_repair_degree, R.id.open_degree, R.id.open_speed, R.id.close_power, R.id.close_speed})
    public void onViewClicked(View view) {
        if (setDialog == null) {
            setDialog = new SetDialog(SettingActivity.this, R.style.mDialog);
            setDialog.setListener(listener);
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.level:
                setDialog.show(22, intent.getStringExtra("level"));
                break;
            case R.id.close_power:
                setDialog.show(8, intent.getStringExtra("closePower"));
                break;
            case R.id.open_degree_repair:
                setDialog.show(12, intent.getStringExtra("openDegreeRepair"));
                break;
            case R.id.left_repair_degree:
                setDialog.show(1, intent.getStringExtra("leftDegreeRepair"));
                break;
            case R.id.right_repair_degree:
                setDialog.show(2, intent.getStringExtra("rightDegreeRepair"));
                break;
            case R.id.open_degree:
                String openDegree = intent.getStringExtra("openDegree");
                switch (openDegree){
                    case "72":
                        setDialog.show(3, "小");
                        break;
                    case "77":
                        setDialog.show(3, "适中");
                        break;
                    case "82":
                        setDialog.show(3, "大");
                        break;
                    case "87":
                        setDialog.show(3, "最大");
                        break;
                }
                break;
            case R.id.open_speed:
                setDialog.show(5, intent.getStringExtra("openDoorSpeed"));
                break;
            case R.id.close_speed:
                setDialog.show(6, intent.getStringExtra("closeDoorSpeed"));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
