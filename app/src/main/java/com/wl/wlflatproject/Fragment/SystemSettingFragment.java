package com.wl.wlflatproject.Fragment;

import static android.media.AudioManager.STREAM_MUSIC;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.VolumeUtils;
import com.wl.wlflatproject.R;

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

    @BindView(R.id.brightness_bar)
    AppCompatSeekBar brightnessSeekBar;

    @BindView(R.id.screen_off_time_radio_group)
    RadioGroup timeGroup;
    @BindView(R.id.volume_radio_group)
    RadioGroup volumeGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sys_setting_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initBrightness();
        initDisplay();
        initVolume();
        initRingtones();
        return view;
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


    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
