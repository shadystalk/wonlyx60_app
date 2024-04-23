package com.wl.wlflatproject.Fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ScreenUtils;
import com.qtimes.service.wonly.client.QtimesServiceManager;
import com.wl.wlflatproject.Activity.MainActivity;
import com.wl.wlflatproject.Activity.SettingActivity;
import com.wl.wlflatproject.Bean.MainMsgBean;
import com.wl.wlflatproject.Bean.SetMsgBean;
import com.wl.wlflatproject.MUtils.SerialPortUtil;
import com.wl.wlflatproject.MView.CodeDialog;
import com.wl.wlflatproject.MView.SetDialog;
import com.wl.wlflatproject.MView.WaitDialogTime;
import com.wl.wlflatproject.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OpenMachineFragment extends Fragment {
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
    private int open_degree=90;
    private int open_speed=8;
    private int close_speed=4;
    private String closeTime="5秒";
    private String openDegreeRepair="0°";
    private SerialPortUtil serialPort;
    private SerialPortUtil.DataListener dataListener;
    private WaitDialogTime dialogTime;
    private Unbinder unbinder;
    private SetDialog setDialog;
    private SetDialog.ResultListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.open_machine_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    public void initData(){
        serialPort = SerialPortUtil.getInstance();
        if (dialogTime == null)
            dialogTime = new WaitDialogTime(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        initOpenDegree();
        initOpenSpeed();
        initCloseSpeed();
        initListener();
        listener = new SetDialog.ResultListener() {
            @Override
            public void onResult(String value, int flag) {
                switch (flag){
                    case 4:
                        serialPort.sendDate(("+OPENWAITTIME:" + value + "\r\n").getBytes());
                        closeTime=value;
                        closeTimeTv.setText(value);
                        break;
                    case 12:
                        openDegreeRepair=value;
                        serialPort.sendDate(("+ANGLEREPAIR:" + value + "\r\n").getBytes());
                        openDegreeRepairTv.setText(value);
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
                                initOpenDegree();
                                break;
                            case 2://开门等待时间
                                closeTime = split[1]+"秒";
                                closeTimeTv.setText(split[1]+"秒");
                                break;
                            case 3://开门速度
                                open_speed = Integer.parseInt(split[1]);
                                initOpenSpeed();
                                break;
                            case 4://关门速度
                                close_speed = Integer.parseInt(split[1]);
                                initCloseSpeed();
                                break;
                            case 9://关门力度
//                                closePower = split[1];
                                break;
                            case 10://开门角度修复值
                                openDegreeRepair = split[1]+"°";
                                openDegreeRepairTv.setText(split[1]+"°");
                                break;
                        }
                    }else if (data.contains("AT+LEFTANGLEREPAIR=1")) {
                        if(dialogTime!=null&&  dialogTime.isShowing()){
                            dialogTime.dismiss();
                        }
                        Toast.makeText(getContext(),"左角度修复值设置成功",Toast.LENGTH_SHORT).show();
                    } else if (data.contains("AT+RIGHTANGLEREPAIR=1")) {
                        if(dialogTime!=null&&  dialogTime.isShowing()){
                            dialogTime.dismiss();
                        }
                        Toast.makeText(getContext(),"右角度修复值设置成功",Toast.LENGTH_SHORT).show();
                    } else if (data.contains("AT+ANGLEREPAIR=1")) {
                        if(dialogTime!=null&&  dialogTime.isShowing()){
                            dialogTime.dismiss();
                        }
                        Toast.makeText(getContext(),"开门角度修复值设置成功",Toast.LENGTH_SHORT).show();
                    } else if (data.contains("AT+OPENANGLE=1")) {
                        if(dialogTime!=null&&  dialogTime.isShowing()){
                            dialogTime.dismiss();
                        }
                        Toast.makeText(getContext(),"开门角度设置成功",Toast.LENGTH_SHORT).show();
                    } else if (data.contains("AT+OPENWAITTIME=1")) {
                        if(dialogTime!=null&&  dialogTime.isShowing()){
                            dialogTime.dismiss();
                        }
                        Toast.makeText(getContext(),"等待时间设置成功",Toast.LENGTH_SHORT).show();
                    } else if (data.contains("AT+OPENSPEED=1")) {
                        if(dialogTime!=null&&  dialogTime.isShowing()){
                            dialogTime.dismiss();
                        }
                        Toast.makeText(getContext(),"开门速度设置成功",Toast.LENGTH_SHORT).show();
                    } else if (data.contains("AT+CLOSESPEED=1")) {
                        if(dialogTime!=null&&  dialogTime.isShowing()){
                            dialogTime.dismiss();
                        }
                        Toast.makeText(getContext(),"关门速度设置成功",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        };
        serialPort.addListener(dataListener);
    }

    private void initListener() {

        openDegreeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            dialogTime.show();
            switch (checkedId) {
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
        });


        openSpeedGroup.setOnCheckedChangeListener((group, checkedId) -> {
            dialogTime.show();
            switch (checkedId) {
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
        });


        closeSpeedGroup.setOnCheckedChangeListener((group, checkedId) -> {
            dialogTime.show();
            switch (checkedId) {
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
        });


        openDegreeRepairCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setDialog == null) {
                    setDialog = new SetDialog(getContext(), R.style.mDialog);
                    setDialog.setListener(listener);
                }
                setDialog.show(12,openDegreeRepair);
            }
        });


        closeTimeCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setDialog == null) {
                    setDialog = new SetDialog(getContext(), R.style.mDialog);
                    setDialog.setListener(listener);
                }
                setDialog.show(4,closeTime);
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
                open_degree=90;
                openDegreeGroup.check(R.id.open_degree_90);
                break;
        }
    }
    /**
     * 开门速度
     */
    private void initOpenSpeed() {
        if(open_speed<8){   //4~16拆分 高中低
            open_speed=4;
        }else if(open_speed<12){
            open_speed=8;
        }else{
            open_speed=12;
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
                open_speed=8;
                openSpeedGroup.check(R.id.open_speed_mid);
                break;
        }

    }
    /**
     * 关门速度
     */
    private void initCloseSpeed() {
        if(close_speed<8){   //4~16拆分 高中低
            close_speed=4;
        }else if(close_speed<12){
            close_speed=8;
        }else{
            close_speed=12;
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
                close_speed=8;
                closeSpeedGroup.check(R.id.close_speed_mid);
                break;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        serialPort.removeListener(dataListener);
    }
}
