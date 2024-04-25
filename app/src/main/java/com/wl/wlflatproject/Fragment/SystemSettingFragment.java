package com.wl.wlflatproject.Fragment;

import static android.media.AudioManager.STREAM_MUSIC;
import static com.wl.wlflatproject.Constant.Constant.RINGTONES_KEY;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.VolumeUtils;
import com.wl.wlflatproject.Constant.Constant;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.MUtils.SerialPortUtil;
import com.wl.wlflatproject.MView.PasswardDialog;
import com.wl.wlflatproject.MView.SetDialog;
import com.wl.wlflatproject.MView.WaitDialogTime;
import com.wl.wlflatproject.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 系统设置
 */
public class SystemSettingFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.brightness_value)
    TextView brightnessTv;
    @BindView(R.id.advanced_settings)
    TextView advancedSettings;
    @BindView(R.id.advanced_view)
    View advancedView;
    @BindView(R.id.general_settings)
    TextView generalSettings;
    @BindView(R.id.stop_power_tv)
    TextView stopPowerTv;

    @BindView(R.id.brightness_bar)
    AppCompatSeekBar brightnessSeekBar;

    @BindView(R.id.screen_off_time_radio_group)
    RadioGroup timeGroup;
    @BindView(R.id.open_power_group)
    RadioGroup openPowerGroup;
    @BindView(R.id.volume_radio_group)
    RadioGroup volumeGroup;
    @BindView(R.id.ringtones_radio_group)
    RadioGroup ringtonesGroup;

    MediaPlayer mediaPlayer;
    @BindView(R.id.general_settings_layout)
    ConstraintLayout generalSettingsLayout;
    @BindView(R.id.general_settings_ll)
    LinearLayout generalSettingsLl;
    @BindView(R.id.stop_power_cl)
    ConstraintLayout stopPowerCl;
    private PasswardDialog passwardDialog;
    private int openPower = 1;
    private String stopPower = "1";
    private WaitDialogTime dialogTime;
    private SerialPortUtil.DataListener dataListener;
    private static String showString;
    private SetDialog setDialog;
    private SetDialog.ResultListener selectListener;
    private SerialPortUtil serialPort;
    private MyHandler myHandler;

    static class MyHandler extends Handler {
        private final WeakReference<Fragment> mFragment;

        MyHandler(Fragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            SystemSettingFragment fragment = (SystemSettingFragment) mFragment.get();
            if (fragment != null&&fragment.dataListener!=null) {
                switch (msg.what){
                    case 0:
                        fragment.dialogTime.dismiss();
                        Toast.makeText(fragment.getContext(),showString,Toast.LENGTH_LONG).show();
                        break;
                    case 9:
                        fragment.initOpenPower();
                        break;
                    case 11:
                        fragment.stopPowerTv.setText(fragment.stopPower);
                        break;
                }
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sys_setting_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        myHandler = new MyHandler(this);
        serialPort = SerialPortUtil.getInstance();
        passwardDialog = new PasswardDialog(getContext(), R.style.mDialog);
        initBrightness();
        initDisplay();
        initVolume();
        initRingtones();
        initOpenPower();

        initListener();
        return view;
    }

    private void initOpenPower() {
        switch (openPower) {
            case 1:
                openPowerGroup.check(R.id.open_power_1);
                break;
            case 2:
                openPowerGroup.check(R.id.open_power_2);
                break;
            case 3:
                openPowerGroup.check(R.id.open_power_3);
                break;
            case 4:
                openPowerGroup.check(R.id.open_power_4);
                break;
            case 5:
                openPowerGroup.check(R.id.open_power_5);
                break;
        }
    }

    private void initListener() {
        passwardDialog.setListener(new PasswardDialog.OnResultListener() {
            @Override
            public void setOnResultListener(String password) {
                if (password.equals("605268")) {
                    setSelect(true);
                    passwardDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        advancedSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogTime == null){
                    dialogTime = new WaitDialogTime(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
                }

                passwardDialog.setEdit("");
                passwardDialog.show();
            }
        });
        generalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelect(false);
            }
        });

        stopPowerCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setDialog == null) {
                    setDialog = new SetDialog(getContext(), R.style.mDialog);
                    setDialog.setListener(selectListener);
                }
                setDialog.show(22,stopPower);
            }
        });
        int count = openPowerGroup.getChildCount();
        for (int i=0;i<count;i++) {
            View o = openPowerGroup.getChildAt(i);
            o.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogTime.show();
                    switch (v.getId()) {
                        case R.id.open_power_1:
                            serialPort.sendDate(("+CLOSESTRENGTH:1\r\n").getBytes());
                            break;
                        case R.id.open_power_2:
                            serialPort.sendDate(("+CLOSESTRENGTH:2\r\n").getBytes());
                            break;
                        case R.id.open_power_3:
                            serialPort.sendDate(("+CLOSESTRENGTH:3\r\n").getBytes());
                            break;
                        case R.id.open_power_4:
                            serialPort.sendDate(("+CLOSESTRENGTH:4\r\n").getBytes());
                            break;
                        case R.id.open_power_5:
                            serialPort.sendDate(("+CLOSESTRENGTH:5\r\n").getBytes());
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        selectListener = new SetDialog.ResultListener() {
            @Override
            public void onResult(String value, int flag) {
                switch (flag){
                    case 22:
                        serialPort.sendDate(("+FGSTRENGTH:" + value + "\r\n").getBytes());
                        stopPower=value;
                        stopPowerTv.setText(value);
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
                            case 9://开关门力度
                                openPower = Integer.parseInt(split[1]);
                                myHandler.sendEmptyMessage(9);
                                break;
                            case 11://防夹力度
                                stopPower = split[1];
                                myHandler.sendEmptyMessage(11);
                                break;
                        }
                    } else if (data.contains("AT+CLOSESTRENGTH=1")) {
                        showString = "关门力道设置成功";
                        myHandler.sendEmptyMessage(0);
                    } else if (data.contains("AT+FGSTRENGTH=1")) {
                        showString = "防夹力度设置成功";
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


    /**
     * 初始化亮度
     */
    private void initBrightness() {
        int currentBrightness = BrightnessUtils.getBrightness();
        int maxBrightness = 255;
        int progress = (int) ((float) currentBrightness / maxBrightness * 100);
        brightnessSeekBar.setProgress(progress);
        brightnessTv.setText(String.valueOf(progress));

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    BrightnessUtils.setBrightness((int) ((float) progress / 100 * maxBrightness));
                    brightnessTv.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void setSelect(boolean flag) {
        if (flag) {
            advancedSettings.setTextColor(Color.parseColor("#ffffff"));
            advancedView.setBackgroundResource(R.drawable.set_unlock);
            generalSettings.setTextColor(Color.parseColor("#878787"));
            generalSettingsLayout.setVisibility(View.GONE);
            generalSettingsLl.setVisibility(View.VISIBLE);
        } else {
            advancedSettings.setTextColor(Color.parseColor("#878787"));
            advancedView.setBackgroundResource(R.drawable.lock_gray);
            generalSettings.setTextColor(Color.parseColor("#ffffff"));
            generalSettingsLayout.setVisibility(View.VISIBLE);
            generalSettingsLl.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化休眠
     */
    private void initDisplay() {
        int sleepTime = ScreenUtils.getSleepDuration();
        switch (sleepTime) {
            case 15 * 1000:
                timeGroup.check(R.id.time_seconds_15);
                break;
            case 30 * 1000:
                timeGroup.check(R.id.time_seconds_30);
                break;
            case 180 * 1000:
                timeGroup.check(R.id.time_minute_3);
                break;
            case 600 * 1000:
                timeGroup.check(R.id.time_minute_10);
                break;
            case 1800 * 1000:
                timeGroup.check(R.id.time_minute_30);
                break;
            default:
        }

        timeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.time_seconds_15:
                    ScreenUtils.setSleepDuration(15 * 1000);
                    break;
                case R.id.time_seconds_30:
                    ScreenUtils.setSleepDuration(30 * 1000);
                    break;
                case R.id.time_minute_3:
                    ScreenUtils.setSleepDuration(180 * 1000);
                    break;
                case R.id.time_minute_10:
                    ScreenUtils.setSleepDuration(600 * 1000);
                    break;
                case R.id.time_minute_30:
                    ScreenUtils.setSleepDuration(1800 * 1000);
                    break;
                default:
            }
        });
    }

    /**
     * 初始化音量
     */
    private void initVolume() {
        //当前音量
        int currentVolume = VolumeUtils.getVolume(STREAM_MUSIC);
        //最大音量
        int maxVolume = VolumeUtils.getMaxVolume(STREAM_MUSIC);
        //最小音量
        int minVolume = VolumeUtils.getMinVolume(STREAM_MUSIC);

        Log.e("音量", currentVolume + "--" + maxVolume + "--" + minVolume);
        int level = maxVolume / 3;
        if (currentVolume == minVolume) {
            volumeGroup.check(R.id.silent_rb);
        } else if (currentVolume <= level) {
            volumeGroup.check(R.id.low_rb);
        } else if (currentVolume <= 2 * level) {
            volumeGroup.check(R.id.middle_rb);
        } else if (currentVolume <= maxVolume) {
            volumeGroup.check(R.id.high_rb);
        }

        volumeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.silent_rb:
                    VolumeUtils.setVolume(STREAM_MUSIC, minVolume, 0);
                    break;
                case R.id.low_rb:
                    VolumeUtils.setVolume(STREAM_MUSIC, level, 0);
                    break;
                case R.id.middle_rb:
                    VolumeUtils.setVolume(STREAM_MUSIC, 2 * level, 0);
                    break;
                case R.id.high_rb:
                    VolumeUtils.setVolume(STREAM_MUSIC, maxVolume, 0);
                    break;
            }
        });

    }

    /**
     * 铃声初始化
     */
    private void initRingtones() {
        mediaPlayer = new MediaPlayer();
        // 创建 AudioAttributes 对象，并设置音频属性
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) // 设置音频内容类型为音乐
                .setUsage(AudioAttributes.USAGE_MEDIA) // 设置音频用途为媒体播放
                .build();
        mediaPlayer.setAudioAttributes(audioAttributes);

        int select = SPUtil.getInstance(getContext()).getSettingParam(RINGTONES_KEY, 0);
        switch (select) {
            case 0:
                ringtonesGroup.check(R.id.ringtones_rb);
                break;
            case 1:
                ringtonesGroup.check(R.id.ringtones1_rb);
                break;
            case 2:
                ringtonesGroup.check(R.id.ringtones2_rb);
                break;
        }
        ringtonesGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.ringtones_rb:
                    SPUtil.getInstance(getContext()).setSettingParam(RINGTONES_KEY, 0);
                    playAudio(Constant.RINGTONES_RES[0]);
                    break;
                case R.id.ringtones1_rb:
                    SPUtil.getInstance(getContext()).setSettingParam(RINGTONES_KEY, 1);
                    playAudio(Constant.RINGTONES_RES[1]);
                    break;
                case R.id.ringtones2_rb:
                    SPUtil.getInstance(getContext()).setSettingParam(RINGTONES_KEY, 2);
                    playAudio(Constant.RINGTONES_RES[2]);
                    break;
            }
        });
    }

    private void playAudio(@RawRes int audioResId) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getResources().openRawResourceFd(audioResId));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        serialPort.removeListener(dataListener);
        dataListener=null;
        super.onDestroyView();
    }
}
