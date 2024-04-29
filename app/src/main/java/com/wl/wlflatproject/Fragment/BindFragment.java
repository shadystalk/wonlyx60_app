package com.wl.wlflatproject.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ToastUtils;
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
public class BindFragment extends Fragment {
    @BindView(R.id.code_view)
    ImageView codeView;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.bind_num)
    TextView bindNum;
    @BindView(R.id.bind_ll)
    LinearLayout bindLL;
    private Unbinder unbinder;
    private String devId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bind_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    public void initData() {
        EventBus.getDefault().register(this);
        devId = SPUtil.getInstance(getContext()).getSettingParam(Constant.DEVID, "");
        String devType = SPUtil.getInstance(getContext()).getSettingParam(Constant.DEVTYPE, "");
        Bitmap code = DpUtils.getTowCode(getContext(), devType + "-" + devId);
        codeView.setImageBitmap(code);
        getInfo();
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
        OkGo.<String>get(ApiSrevice.searchInfo).headers(ApiSrevice.getHeads(getContext())).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String s = response.body().toString();
                InfoBean infoBean = GsonUtils.GsonToBean(s, InfoBean.class);
                if(infoBean.getCode()==200  &&  infoBean.getData()!=null){
                    bindNum.setText(infoBean.getData().getPhone());
                    bindVisi(true);
                }else{

                }
            }
        });
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
        unbinder.unbind();
    }
}
