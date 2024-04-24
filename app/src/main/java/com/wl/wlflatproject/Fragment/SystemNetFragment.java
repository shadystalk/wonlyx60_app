package com.wl.wlflatproject.Fragment;

import static com.blankj.utilcode.util.NetworkUtils.NetworkType.NETWORK_NO;
import static com.blankj.utilcode.util.NetworkUtils.getWifiEnabled;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.XPopup;
import com.wl.wlflatproject.Adapter.WifiAdapter;
import com.wl.wlflatproject.Bean.AccessPoint;
import com.wl.wlflatproject.MView.WifiInfoPopup;
import com.wl.wlflatproject.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 网络设置
 */
@SuppressLint("MissingPermission")
public class SystemNetFragment extends Fragment implements Utils.Consumer<NetworkUtils.WifiScanResults>, BaseQuickAdapter.OnItemClickListener {
    private Unbinder unbinder;

    @BindView(R.id.wifi_list)
    RecyclerView wifiRv;

    @BindView(R.id.net_switch)
    Switch mNetSwitch;
    /**
     * 当前连接WiFi布局
     */
    @BindView(R.id.current_wifi_cl)
    ConstraintLayout mCurrentWifiCl;
    /**
     * 已连接WiFi名
     */
    @BindView(R.id.wifi_name)
    TextView mWifiName;

    @BindView(R.id.wifi_icon)
    ImageView mConnectIcon;
    @BindView(R.id.links_pb)
    ProgressBar linksPb;
    /**
     * 当前WiFi信号状态
     */
    @BindView(R.id.wifi_state_iv)
    ImageView wifiStateIv;
    /**
     * WiFi列表组合
     * WiFi关闭时隐藏
     */
    @BindView(R.id.wifi_list_group)
    Group wifiListGroup;

    @BindView(R.id.wifi_refresh_pb)
    ProgressBar wifiRefreshPb;
    @BindView(R.id.wifi_list_refresh)
    ImageView wifiRefreshView;
    private WifiAdapter wifiAdapter;

    private final List<AccessPoint> lastAccessPoints = new ArrayList<>();
    //wiif列表 系统的
    private List<ScanResult> scanResults;

    private WifiManager wifiManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sys_net_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initView();
        return view;
    }

    private void initView() {
        //当前连接WiFi
        mCurrentWifiCl.setVisibility(NetworkUtils.isConnected() ? View.VISIBLE : View.GONE);
        if (NetworkUtils.isConnected()) {
            WifiInfo info = wifiManager.getConnectionInfo();
            mWifiName.setText(removeQuotes(info.getSSID()));
        }
        wifiAdapter = new WifiAdapter(new ArrayList<>());
        wifiAdapter.setOnItemClickListener(this);
        wifiRv.setLayoutManager(new LinearLayoutManager(getContext()));
        wifiRv.setAdapter(wifiAdapter);


        mNetSwitch.setChecked(getWifiEnabled());
        mNetSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NetworkUtils.setWifiEnabled(isChecked);
            if (isChecked) {
                //打开
                wifiListGroup.setVisibility(View.VISIBLE);
                updateWifiList();
            } else {
                //关闭
                wifiListGroup.setVisibility(View.GONE);
            }
        });

        NetworkUtils.addOnWifiChangedConsumer(this);
        sendReceiver();
    }


    @OnClick({R.id.wifi_list_refresh, R.id.wifi_info_iv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.wifi_list_refresh:
                //刷新
                updateWifiList();
                break;
            case R.id.wifi_info_iv:
                //WiFi信息
                new XPopup.Builder(getContext())
                        .dismissOnTouchOutside(true)
                        .asCustom(new WifiInfoPopup(getContext(), wifiManager.getConnectionInfo(), mCapabilities, ssid -> {
                            forgetWifiNetwork(removeQuotes(ssid));
                            wifiManager.disconnect();
//                            forgetNetwork(wifiManager, wifiManager.getConnectionInfo().getNetworkId());
                            mCurrentWifiCl.setVisibility(View.GONE);
                            updateWifiList();
                        }))
                        .show();
                break;
        }
    }

    /**
     * 更新WiFi列表
     */
    private void updateWifiList() {
        refreshView(true);
        NetworkUtils.WifiScanResults results = NetworkUtils.getWifiScanResult();
        List<ScanResult> list = results.getFilterResults();
        WifiInfo info = wifiManager.getConnectionInfo();
        //WiFi列表移除当前连接的WiFi
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).SSID.equals(removeQuotes(info.getSSID()))) {
                list.remove(i);
                break;
            }
        }
        if (wifiAdapter != null) {
            wifiAdapter.setNewData(list);
        }
        refreshView(false);
    }

    /**
     * 刷新时显示视图
     *
     * @param isRefresh
     */
    private void refreshView(boolean isRefresh) {
        Log.e("refreshView", isRefresh + "");
        wifiRefreshPb.setVisibility(isRefresh ? View.VISIBLE : View.GONE);
        wifiRefreshView.setVisibility(isRefresh ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        NetworkUtils.removeOnWifiChangedConsumer(this);
        stopReceiver();
        super.onDestroyView();
    }

    private String mCapabilities = "[WPA2-PSK-CCMP][ESS][WPS]";

    @Override
    public void accept(NetworkUtils.WifiScanResults wifiScanResults) {
        List<ScanResult> list = wifiScanResults.getFilterResults();
        WifiInfo info = wifiManager.getConnectionInfo();
        //WiFi列表移除当前连接的WiFi
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).SSID.equals(removeQuotes(info.getSSID()))) {
                mCapabilities = list.get(i).capabilities;
                list.remove(i);
                break;
            }
        }
        wifiAdapter.setNewData(list);
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        //点击WiFi，未连接弹窗，已连接连接
        ScanResult scanResult = wifiAdapter.getData().get(i);
        WifiConfiguration configuration = everConnected(scanResult.SSID);
        if (configuration != null) {
            //发起连接
            wifiManager.enableNetwork(configuration.networkId, true);//直接连接wifi
            wifiAdapter.remove(i);
        } else {
            if (isSecured(scanResult)) {
                //弹窗输入密码

            } else {
                connectToWifi(scanResult, "");
                wifiAdapter.remove(i);
            }

        }
    }

    // 检查是否已经连接过
    public WifiConfiguration everConnected(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs == null || existingConfigs.isEmpty()) {
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }


    // 连接到指定的 WiFi
    public void connectToWifi(ScanResult scanResult, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + scanResult.SSID + "\"";

        // 检查是否需要密码
        if (scanResult.capabilities.contains("WPA") || scanResult.capabilities.contains("WEP")) {
            wifiConfig.preSharedKey = "\"" + password + "\"";
        } else {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    // 检查WiFi是否加密
    public static boolean isSecured(ScanResult result) {
        return result.capabilities.contains("WEP") || result.capabilities.contains("PSK") || result.capabilities.contains("EAP");
    }

    WifiStateReceiver wifiStateReceiver;

    private void sendReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        filter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiStateReceiver = new WifiStateReceiver();
        getContext().registerReceiver(wifiStateReceiver, filter);
    }

    private void stopReceiver() {
        if (wifiStateReceiver != null) {
            getContext().unregisterReceiver(wifiStateReceiver);
            wifiStateReceiver = null;
        }
    }

    /**
     * wifi监听广播
     */
    private class WifiStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
                        // 最好用的还是这个监听。
                        // wifi如果打开，关闭，以及连接上可用的连接都会接到监听。
                        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适

                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        Log.e("WifiResult", "----wifi info:" + wifiInfo.getSSID() + "," + wifiInfo.getIpAddress());
                        if (!TextUtils.isEmpty(wifiInfo.getSSID())) {
                            //获取到连接的信息
                            if (wifiInfo.getSSID().contains("unknown")) {
                                //setWifiLableHide();
                                mCurrentWifiCl.setVisibility(View.GONE);
                            } else {
                                mCurrentWifiCl.setVisibility(View.VISIBLE);
                                mWifiName.setText(removeQuotes(wifiInfo.getSSID()));

                                if ((wifiInfo.getIpAddress() == 0) && (isNetworkAvailable(getContext()) == 2)) {
                                    //正在连接
                                    linksPb.setVisibility(View.VISIBLE);
                                    mConnectIcon.setVisibility(View.GONE);
                                } else {
                                    if (isNetworkAvailable(getContext()) == 2) {
                                        //已连接
                                        mConnectIcon.setVisibility(View.VISIBLE);
                                        linksPb.setVisibility(View.GONE);
//                                        if (wifiPwdPop != null) {
//                                            wifiPwdPop.dismiss();
//                                            wifiPwdPop = null;
//                                        }
                                    }
                                }
                            }
                        } else {
                            mCurrentWifiCl.setVisibility(View.GONE);
                        }

                        break;

                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    case "android.net.wifi.CONFIGURED_NETWORKS_CHANGE":
                    case "android.net.wifi.LINK_CONFIGURATION_CHANGED":
//                        updateAccessPoints();//获取wifi列表加入到wifi显示新的列表
//                        KLog.d("tangshang=111111" + intent.getAction());
                        break;
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:

//                        updateAccessPoints();//获取wifi列表加入到wifi显示新的列表
//                        wifiInfoResult();
//                        KLog.d("tangshang=22222" + intent.getAction());
                        break;
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                        if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                            //密码错误
//                            if (wifiPwdPop != null) {
//                                wifiPwdPop.stopScanner();
//                                ToastUtils.showShort("连接失败,请检测密码后重试");
//                                KLog.a("tangshang==" + connectNetId);
//                            }
                            forgetWifiNetwork(wifiManager.getConnectionInfo().getSSID());
                        }
                        break;
                    case WifiManager.WIFI_STATE_CHANGED_ACTION:

                        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                        switch (wifiState) {
                            case WifiManager.WIFI_STATE_DISABLED://关闭
                                mNetSwitch.setChecked(false);
                                break;
                            case WifiManager.WIFI_STATE_DISABLING:

                                break;
                            case WifiManager.WIFI_STATE_ENABLED://打开
                                mNetSwitch.setChecked(true);
                                if (wifiManager.getConnectionInfo().getIpAddress() == 0) {
                                    //没连接wifi
                                    mCurrentWifiCl.setVisibility(View.GONE);
                                }

                                break;
                            case WifiManager.WIFI_STATE_ENABLING:
                                break;
                            case WifiManager.WIFI_STATE_UNKNOWN:
                                break;
                            default:
                                break;
                        }

                        break;
                    default:
                        break;
                }
            }

        }
    }

    // 删除指定 SSID 的 WiFi 配置
    public boolean forgetWifiNetwork(String ssid) {
        boolean result = false;
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (configurations != null) {
            for (WifiConfiguration config : configurations) {
                if (config.SSID.equals("\"" + ssid + "\"")) {
                    result = wifiManager.removeNetwork(config.networkId);
                    return wifiManager.saveConfiguration();
                }
            }
        }
        return result;
    }

    /**
     * 取消连接
     *
     * @param manager
     * @param networkId
     */
    public void forgetNetwork(WifiManager manager, int networkId) {
        if (manager == null) {
            return;
        }
        try {
            Method forget = manager.getClass().getDeclaredMethod("forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (forget != null) {
                forget.setAccessible(true);
                forget.invoke(manager, networkId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int isNetworkAvailable(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            return 1;
        } else if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * utf-8编码的ssid会被双引号包裹，此处移除双引号
     *
     * @param ssid
     * @return
     */
    private String removeQuotes(String ssid) {
        if (ssid.startsWith("\"") && ssid.endsWith("\"") && ssid.length() >= 2) {
            return ssid.substring(1, ssid.length() - 1);
        } else {
            return ssid;
        }
    }

}
