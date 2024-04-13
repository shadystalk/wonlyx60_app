package com.wl.wlflatproject.Presenter;

import android.app.Application;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wl.wlflatproject.Activity.MainActivity;
import com.wl.wlflatproject.Bean.TimeBean;
import com.wl.wlflatproject.MUtils.GsonUtils;
public class WJAPlayPresenter{
    private MainActivity context;
    private ImageView bg;
    private ConstraintLayout mFunVideoView;
    private TextView time;


    public void initCamera(
            String mDeviceUid,
            String mVideoUid,
            Application application,
            MainActivity context,
            ImageView bg, ConstraintLayout mFunVideoView, TextView time
    ) {
        this.bg = bg;
        this.mFunVideoView = mFunVideoView;
        this.time = time;
        this.context = context;
        mFunVideoView.setVisibility(View.GONE);


    }







    public void getSystemTime() {
        String path = "https://ums-test.wonlycloud.com:10301/api/aigang/getTimeStamp";
        OkGo.<String>post(path).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                TimeBean bean=GsonUtils.GsonToBean(response.body(), TimeBean.class);
                SystemClock.setCurrentTimeMillis(bean.getData().getMillisecond());
            }

            @Override
            public void onError(Response<String> response) {
            }
        });
    }



}
