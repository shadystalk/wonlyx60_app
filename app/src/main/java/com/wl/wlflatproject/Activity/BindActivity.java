package com.wl.wlflatproject.Activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wl.wlflatproject.Bean.InfoBean;
import com.wl.wlflatproject.Constant.Constant;
import com.wl.wlflatproject.MUtils.ApiSrevice;
import com.wl.wlflatproject.MUtils.DpUtils;
import com.wl.wlflatproject.MUtils.GsonUtils;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BindActivity extends AppCompatActivity {
    @BindView(R.id.code_view)
    ImageView codeView;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.bind_num)
    TextView bindNum;
    @BindView(R.id.back_iv)
    View backIv;
    @BindView(R.id.bind_ll)
    LinearLayout bindLL;
    private String devId;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_fragment);
        ButterKnife.bind(this);
        initData();
    }

    public void initData() {
        EventBus.getDefault().register(this);
        devId = SPUtil.getInstance(this).getSettingParam(Constant.DEVID, "");
        String devType = SPUtil.getInstance(this).getSettingParam(Constant.DEVTYPE, "");
        Bitmap code = DpUtils.getTowCode(this, devType + "-" + devId);
        codeView.setImageBitmap(code);
        getInfo();
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InfoBean bean) {
        if(bean.getCode()==1){
            getInfo();
            bindVisi(true);
        }else{
            bindVisi(false);
        }
    }
    public void getInfo() {
        if(TextUtils.isEmpty(devId)){
            return;
        }
        OkGo.<String>get(ApiSrevice.baseUrl+ApiSrevice.searchInfo).tag(this).headers(ApiSrevice.getHeads(this)).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String s = response.body().toString();
                InfoBean infoBean = GsonUtils.GsonToBean(s, InfoBean.class);
                if(infoBean.getCode()==200  &&  infoBean.getData()!=null &&bindNum!=null){
                    bindNum.setText(infoBean.getData().getPhone());
                    bindVisi(true);
                }else{

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    public void bindVisi(boolean isBind){
        if(isBind){
            codeView.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
            tv1.setVisibility(View.VISIBLE);
            bindLL.setVisibility(View.VISIBLE);
        }else{
            codeView.setVisibility(View.VISIBLE);
            tv.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.GONE);
            bindLL.setVisibility(View.GONE);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        OkGo.getInstance().cancelTag(this);
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
