package com.wl.wlflatproject.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.wl.wlflatproject.Activity.MainActivity;
import com.wl.wlflatproject.Bean.UpdataJsonBean;
import com.wl.wlflatproject.Bean.UpdateAppBean;
import com.wl.wlflatproject.Constant.Constant;
import com.wl.wlflatproject.MUtils.GsonUtils;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.MUtils.UpdateManager;
import com.wl.wlflatproject.MUtils.VersionUtils;
import com.wl.wlflatproject.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SystemUpdateFragment extends Fragment {

    private TextView check;
    private String mOtaFileSavePath;
    private String TAG="SystemUpdateFragment";
    private ProgressBar mProgress;
    private AlertDialog mDownloadDialog;
    private String downUrl;

    public SystemUpdateFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_update, container, false);
        check = view.findViewById(R.id.check);
        checkUpdate();
        initListener();
        return view;

    }

    private void initListener() {
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtaFileSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "update.zip";
                downloadApp(downUrl);
            }
        });
    }


    private void startInstall() {
        new Thread() {
            @Override
            public void run() {
                UpdateManager.getInstance(getActivity()).startOTAUpdate( mOtaFileSavePath, new UpdateManager.OTAUpdateReadyListener() {
                    @Override
                    public void onReady() {

                    }
                });
            }
        }.start();
    }


    protected void checkUpdate() {
        requestAppUpdate( new MainActivity.DataRequestListener<UpdateAppBean>() {
            @Override
            public void success(UpdateAppBean data) {
                check.setEnabled(true);
                check.setText("发现新版本点击下载升级");
                downUrl = data.getPUS().getBody().getUrl();
            }

            @Override
            public void fail(String msg) {

            }
        });
    }

    public boolean compareVersions(String currentVersion, String latestVersion) {
        String[] currentVersionSplit = currentVersion.split(".");
        int currentData = Integer.parseInt(currentVersionSplit[1]);
        int currentDataTime = Integer.parseInt(currentVersionSplit[2]);
        String[] latestVersionSplit = latestVersion.split(".");
        int latestData = Integer.parseInt(latestVersionSplit[1]);
        int latestDataTime = Integer.parseInt(latestVersionSplit[2]);
        if(currentData<latestData){
            return true;
        }

        if(currentData>latestData){
            return false;
        }

        if(currentDataTime<latestDataTime){
            return true;
        }
        return false;
    }
    /**
     * apk升级
     *
     * @param listener
     */
    private void requestAppUpdate(final MainActivity.DataRequestListener<UpdateAppBean> listener) {
        UpdataJsonBean updataJsonBean = new UpdataJsonBean();
        UpdataJsonBean.PUSBean pusBean = new UpdataJsonBean.PUSBean();
        UpdataJsonBean.PUSBean.BodyBean bodyBean = new UpdataJsonBean.PUSBean.BodyBean();
        UpdataJsonBean.PUSBean.HeaderBean headerBean = new UpdataJsonBean.PUSBean.HeaderBean();

        bodyBean.setToken("");
        bodyBean.setVendor_name("general");
        bodyBean.setPlatform("android");

        bodyBean.setEndpoint_type("X50 Pro_System");

        bodyBean.setCurrent_version("1");

        headerBean.setApi_version("1.0");
        headerBean.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        headerBean.setSeq_id("1");

        pusBean.setBody(bodyBean);
        pusBean.setHeader(headerBean);
        updataJsonBean.setPUS(pusBean);

        String s = GsonUtils.GsonString(updataJsonBean);
        String path = "https://pus.wonlycloud.com:10400";
        OkGo.<String>post(path).tag(this).upJson(s).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String s = response.body();
                Gson gson = new Gson();
                try {
                    String incremental = Build.VERSION.INCREMENTAL;
                    UpdateAppBean updateAppBean = gson.fromJson(s, UpdateAppBean.class);
                    if (compareVersions(incremental,updateAppBean.getPUS().getBody().getNew_version())) {
                        listener.success(updateAppBean);
                    }
                } catch (Exception e) {
                    Log.e("升级接口报错", e.toString());
                }
            }

            @Override
            public void onError(Response<String> response) {
                listener.fail("服务器连接失败");
            }
        });
    }

    private void downloadApp(String apk_url) {
        OkGo.<File>get(apk_url).tag(this).execute(new FileCallback(Environment.getExternalStorageDirectory().getAbsolutePath(),"update.zip") {
            @Override
            public void onError(Response<File> response) {
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                    mDownloadDialog = null;
                }
            }

            @Override
            public void onSuccess(Response<File> response) {
//                if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
//                    mDownloadDialog.dismiss();
//                    mDownloadDialog = null;
//                }
                startInstall();
            }

            @Override
            public void downloadProgress(Progress progress) {
                if (mDownloadDialog == null) {
                    // 构造软件下载对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("正在更新");
                    // 给下载对话框增加进度条
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    View v = inflater.inflate(R.layout.item_progress, null);
                    mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
                    builder.setView(v);
                    mDownloadDialog = builder.create();
                    mDownloadDialog.show();
                }
                mProgress.setProgress((int) (progress.fraction * 100));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}