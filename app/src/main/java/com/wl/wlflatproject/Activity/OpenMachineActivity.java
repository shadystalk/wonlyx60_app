package com.wl.wlflatproject.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.wl.wlflatproject.MUtils.SerialPortUtil;
import com.wl.wlflatproject.MView.SetDialog;
import com.wl.wlflatproject.MView.WaitDialogTime;
import com.wl.wlflatproject.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpenMachineActivity extends AppCompatActivity {

    @BindView(R.id.open_degree_radio_group)
    RadioGroup openDegreeGroup;
    @BindView(R.id.open_speed_group)
    RadioGroup openSpeedGroup;
    @BindView(R.id.close_speed_group)
    RadioGroup closeSpeedGroup;
    @BindView(R.id.close_time_cl)
    ConstraintLayout closeTimeCl;
    @BindView(R.id.open_degree_repair_cl)
    ConstraintLayout openDegreeRepairCl;
    @BindView(R.id.open_degree_repair_tv)
    TextView openDegreeRepairTv;
    @BindView(R.id.close_time_tv)
    TextView closeTimeTv;
    @BindView(R.id.back_iv)
    View backIv;
    private int open_degree = 90;
    private int open_speed = 8;
    private int close_speed = 4;
    private String closeTime = "5秒";
    private String openDegreeRepair = "0°";
    private SerialPortUtil serialPort;
    private SerialPortUtil.DataListener dataListener;
    private WaitDialogTime dialogTime;
    private SetDialog setDialog;
    private SetDialog.ResultListener listener;
    public static String showString = "";
    private MyHandler myHandler;

    @SuppressLint("HandlerLeak")
    static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            OpenMachineActivity activity = (OpenMachineActivity) mActivity.get();
            if (activity != null &&activity.dataListener!=null) {
                activity.hideBottomUIMenu();
                switch (msg.what) {
                    case 0:
                        activity.dialogTime.dismiss();
                        ToastUtils.showShort(showString, Toast.LENGTH_LONG);
                        break;
                    case 1://开门角度
                        activity.initOpenDegree();
                        break;
                    case 2://开门等待时间
                        activity.closeTimeTv.setText(activity.closeTime);
                        break;
                    case 3://开门速度
                        activity.initOpenSpeed();
                        break;
                    case 4://关门速度
                        activity.initCloseSpeed();
                        break;
                    case 17://开门角度修复值
                        activity.openDegreeRepairTv.setText(activity.openDegreeRepair);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_machine_fragment);
        ButterKnife.bind(this);
        initData();
    }
    public void initData() {
        myHandler = new MyHandler(this);
        serialPort = SerialPortUtil.getInstance();
        dialogTime = new WaitDialogTime(this, android.R.style.Theme_Translucent_NoTitleBar);
        initOpenDegree();
        initOpenSpeed();
        initCloseSpeed();
        initListener();
        listener = new SetDialog.ResultListener() {
            @Override
            public void onResult(String value, int flag) {
                switch (flag) {
                    case 4:
                        closeTime = value;
                        String[] split1 = closeTime.split("秒");
                        serialPort.sendDate(("+OPENWAITTIME:" + split1[0] + "\r\n").getBytes());
                        closeTimeTv.setText(value);
                        break;
                    case 12:
                        openDegreeRepair = value;
                        String[] split = openDegreeRepair.split("°");
                        serialPort.sendDate(("+ANGLEREPAIR:" + split[0] + "\r\n").getBytes());
                        openDegreeRepairTv.setText(value);
                        break;
                    default:
                        break;
                }
                dialogTime.show();
            }
        };
        dataListener = new SerialPortUtil.DataListener() {
            @Override
            public void getData(String data) {
                try {
                    if (data.contains("AT+DEFAULT=")) {
                        String[] s = data.split("=");
                        String[] split = s[1].split(",");
                        switch (Integer.parseInt(split[0])) {
                            case 1://代表开门角度
                                open_degree = Integer.parseInt(split[1]);
                                myHandler.sendEmptyMessage(1);
                                break;
                            case 2://开门等待时间
                                closeTime = split[1] + "秒";
                                myHandler.sendEmptyMessage(2);
                                break;
                            case 3://开门速度
                                open_speed = Integer.parseInt(split[1]);
                                myHandler.sendEmptyMessage(3);
                                break;
                            case 4://关门速度
                                close_speed = Integer.parseInt(split[1]);
                                myHandler.sendEmptyMessage(4);
                                break;
                            case 17://开门角度修复值
                                openDegreeRepair = split[1] + "°";
                                myHandler.sendEmptyMessage(17);
                                break;
                            default:
                                break;
                        }
                    } else if (data.contains("AT+LEFTANGLEREPAIR=1")) {
                        showString = "左角度修复值设置成功";
                        myHandler.sendEmptyMessage(0);
                    } else if (data.contains("AT+RIGHTANGLEREPAIR=1")) {
                        showString = "右角度修复值设置成功";
                        myHandler.sendEmptyMessage(0);
                    } else if (data.contains("AT+ANGLEREPAIR=1")) {
                        showString = "开门角度修复值设置成功";
                        myHandler.sendEmptyMessage(0);
                    } else if (data.contains("AT+OPENANGLE=1")) {
                        showString = "开门角度设置成功";
                        myHandler.sendEmptyMessage(0);
                    } else if (data.contains("AT+OPENWAITTIME=1")) {
                        myHandler.sendEmptyMessage(0);
                        showString = "等待时间设置成功";
                    } else if (data.contains("AT+OPENSPEED=1")) {
                        showString = "开门速度设置成功";
                        myHandler.sendEmptyMessage(0);
                    } else if (data.contains("AT+CLOSESPEED=1")) {
                        showString = "关门速度设置成功";
                        myHandler.sendEmptyMessage(0);
                    }
                } catch (Exception e) {
                    Log.e("串口错误", e.toString());
                }
            }
        };
        serialPort.addListener(dataListener);
        serialPort.sendDate("+DATATOPAD\r\n".getBytes());
    }

    private void initListener() {
        int count = openDegreeGroup.getChildCount();
        for (int i=0;i<count;i++) {
            View o = openDegreeGroup.getChildAt(i);
            o.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogTime.show();
                    switch (v.getId()) {
                        case R.id.open_degree_90:
                            serialPort.sendDate(("+OPENANGLE:90\r\n").getBytes());
                            break;
                        case R.id.open_degree_100:
                            serialPort.sendDate(("+OPENANGLE:100\r\n").getBytes());
                            break;
                        case R.id.open_degree_110:
                            serialPort.sendDate(("+OPENANGLE:110\r\n").getBytes());
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        int count1 = openSpeedGroup.getChildCount();
        for (int i=0;i<count1;i++) {
            View o = openSpeedGroup.getChildAt(i);
            o.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogTime.show();
                    switch (v.getId()) {
                        case R.id.open_speed_low:
                            serialPort.sendDate(("+OPENSPEED:4\r\n").getBytes());
                            break;
                        case R.id.open_speed_mid:
                            serialPort.sendDate(("+OPENSPEED:8\r\n").getBytes());
                            break;
                        case R.id.open_speed_high:
                            serialPort.sendDate(("+OPENSPEED:12\r\n").getBytes());
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        int count2 = closeSpeedGroup.getChildCount();
        for (int i=0;i<count2;i++) {
            View o = closeSpeedGroup.getChildAt(i);
            o.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogTime.show();
                    switch (v.getId()) {
                        case R.id.close_speed_low:
                            serialPort.sendDate(("+CLOSESPEED:4\r\n").getBytes());
                            break;
                        case R.id.close_speed_mid:
                            serialPort.sendDate(("+CLOSESPEED:8\r\n").getBytes());
                            break;
                        case R.id.close_speed_high:
                            serialPort.sendDate(("+CLOSESPEED:12\r\n").getBytes());
                            break;
                        default:
                            break;
                    }
                }
            });
        }


        openDegreeRepairCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setDialog == null) {
                    setDialog = new SetDialog(OpenMachineActivity.this, R.style.mDialog);
                    setDialog.setListener(listener);
                }
                setDialog.show(12, openDegreeRepair);
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        closeTimeCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setDialog == null) {
                    setDialog = new SetDialog(OpenMachineActivity.this, R.style.mDialog);
                    setDialog.setListener(listener);
                }
                setDialog.show(4, closeTime);
            }
        });
    }

    /**
     * 开门角度
     */
    private void initOpenDegree() {
        switch (open_degree) {
            case 90:
                openDegreeGroup.check(R.id.open_degree_90);
                break;
            case 100:
                openDegreeGroup.check(R.id.open_degree_100);
                break;
            case 110:
                openDegreeGroup.check(R.id.open_degree_110);
                break;
            default:
                open_degree = 90;
                openDegreeGroup.check(R.id.open_degree_90);
                break;
        }
    }

    /**
     * 开门速度
     */
    private void initOpenSpeed() {
        if (open_speed < 8) {   //4~16拆分 高中低
            open_speed = 4;
        } else if (open_speed < 12) {
            open_speed = 8;
        } else {
            open_speed = 12;
        }
        switch (open_speed) {
            case 4:
                openSpeedGroup.check(R.id.open_speed_low);
                break;
            case 8:
                openSpeedGroup.check(R.id.open_speed_mid);
                break;
            case 12:
                openSpeedGroup.check(R.id.open_speed_high);
                break;
            default:
                open_speed = 8;
                openSpeedGroup.check(R.id.open_speed_mid);
                break;
        }

    }

    /**
     * 关门速度
     */
    private void initCloseSpeed() {
        if (close_speed < 8) {   //4~16拆分 高中低
            close_speed = 4;
        } else if (close_speed < 12) {
            close_speed = 8;
        } else {
            close_speed = 12;
        }
        switch (close_speed) {
            case 4:
                closeSpeedGroup.check(R.id.close_speed_low);
                break;
            case 8:
                closeSpeedGroup.check(R.id.close_speed_mid);
                break;
            case 12:
                closeSpeedGroup.check(R.id.close_speed_high);
                break;
            default:
                close_speed = 8;
                closeSpeedGroup.check(R.id.close_speed_mid);
                break;
        }

    }


    @Override
    public void onDestroy() {
        serialPort.removeListener(dataListener);
        dataListener=null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    public void hideBottomUIMenu() {
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
