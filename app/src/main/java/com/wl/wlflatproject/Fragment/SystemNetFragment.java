package com.wl.wlflatproject.Fragment;

import static com.blankj.utilcode.util.NetworkUtils.getWifiEnabled;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.NetworkUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.wl.wlflatproject.Adapter.WifiAdapter;
import com.wl.wlflatproject.MView.AlarmPopup;
import com.wl.wlflatproject.MView.WifiInfoPopup;
import com.wl.wlflatproject.MView.WifiInputPopup;
import com.wl.wlflatproject.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 网络设置
 */
@SuppressLint("MissingPermission")
public class SystemNetFragment extends Fragment implements BaseQuickAdapter.OnItemClickListener {
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
    @BindView(R.id.choose_wifi)
    TextView mChooseWifi;

    @BindView(R.id.wifi_icon)
    ImageView mConnectIcon;
    @BindView(R.id.links_pb)
    ProgressBar linksPb;
    /**
     * 当前WiFi信号状态
     */
    @BindView(R.id.wifi_state_iv)
    ImageView wifiStateIv;

    @BindView(R.id.wifi_refresh_pb)
    ProgressBar wifiRefreshPb;
    @BindView(R.id.wifi_list_refresh)
    ImageView wifiRefreshView;
    private WifiAdapter wifiAdapter;

    private WifiManager wifiManager;
    private BasePopupView basePopup;

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sys_net_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
        wifiRv.setLayoutManager(new LinearLayoutManager(mContext));
        wifiRv.setAdapter(wifiAdapter);

        sendReceiver();

        if (getWifiEnabled()) {
            mNetSwitch.setChecked(true);
            wifiGroupVisible(true);
            updateWifiList();
        } else {
            mNetSwitch.setChecked(false);
            wifiGroupVisible(false);
        }
        mNetSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NetworkUtils.setWifiEnabled(isChecked);
            wifiGroupVisible(isChecked);
            if (isChecked) {
                //打开
                updateWifiList();
            }
        });
    }

//    @OnLongClick(R.id.net_settings)
//    public boolean onLongClick(View view) {
//        //系统网络设置
//        NetworkUtils.openWirelessSettings();
//        return false;
//    }


    @OnClick({R.id.wifi_list_refresh, R.id.wifi_info_iv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.wifi_list_refresh:
                //刷新
                updateWifiList();
                break;
            case R.id.wifi_info_iv:
                //WiFi信息
                new XPopup.Builder(mContext)
                        .dismissOnTouchOutside(true)
                        .asCustom(new WifiInfoPopup(mContext, wifiManager.getConnectionInfo(), mCapabilities, ssid -> {
                            boolean remove = forgetWifiNetwork(removeQuotes(ssid));
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
        wifiManager.startScan();
        refreshView(true);
    }

    Handler handler = new Handler(msg -> {
        if (msg.what == 1001) {
            wifiRefreshPb.setVisibility(View.GONE);
            wifiRefreshView.setVisibility(View.VISIBLE);
        }
        return false;
    });


    /**
     * 刷新时显示视图
     *
     * @param isRefresh
     */
    private void refreshView(boolean isRefresh) {
        if (!isRefresh) {
            //延迟关闭
            handler.sendEmptyMessageDelayed(1001, 2000);
        } else {
            wifiRefreshPb.setVisibility(View.VISIBLE);
            wifiRefreshView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        stopReceiver();
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    private String mCapabilities = "[WPA2-PSK-CCMP][ESS][WPS]";

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
                new XPopup.Builder(mContext).asCustom(new WifiInputPopup(mContext, scanResult, (mScanResult, inputString) -> {
                    connectToWifi(mScanResult, inputString);
                    wifiAdapter.getData().remove(mScanResult);
                })).show();
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

    /**
     * 注册广播
     */
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
        mContext.registerReceiver(wifiStateReceiver, filter);
    }

    /**
     * 广播解绑
     */
    private void stopReceiver() {
        if (wifiStateReceiver != null) {
            mContext.unregisterReceiver(wifiStateReceiver);
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
                        updateWifiConnection();
                        break;

                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    case "android.net.wifi.CONFIGURED_NETWORKS_CHANGE":
                    case "android.net.wifi.LINK_CONFIGURATION_CHANGED":
                        scanWifiResult(wifiManager.getScanResults());
                        break;
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                        updateWifiConnection();
                        break;
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                        int supplicantError = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                        if (state == SupplicantState.ASSOCIATING && supplicantError == WifiManager.ERROR_AUTHENTICATING) {
                            // 密码错误
                            if (basePopup == null || basePopup.isDismiss()) {
                                basePopup = new XPopup.Builder(mContext)
                                        .dismissOnTouchOutside(false)
                                        .asCustom(new AlarmPopup(mContext, "密码错误", "知道了"));
                                basePopup.show();
                            }
                            forgetWifiNetwork(wifiManager.getConnectionInfo().getSSID());
                        } else if (state == SupplicantState.ASSOCIATING) {
                            // 正在尝试连接
                            wifiConnecting();
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

    /**
     * 删除指定 SSID 的 WiFi 配置
     * （系统应用才生效）
     */
    public boolean forgetWifiNetwork(String ssid) {
        boolean result = false;
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (configurations != null) {
            for (WifiConfiguration config : configurations) {
                if (config.SSID.equals("\"" + ssid + "\"")) {
                    return wifiManager.removeNetwork(config.networkId);
                }
            }
        }
        return result;
    }

    /**
     * wifi正在连接
     */
    private void wifiConnecting() {
        linksPb.setVisibility(View.VISIBLE);
        mConnectIcon.setVisibility(View.GONE);
    }

    /**
     * 更新Wi-Fi列表
     *
     * @param results
     */
    private synchronized void scanWifiResult(List<ScanResult> results) {
        WifiInfo info = wifiManager.getConnectionInfo();
        //WiFi列表移除当前连接的WiFi
        Iterator<ScanResult> resultIterator = results.iterator();
        while (resultIterator.hasNext()) {
            ScanResult result = resultIterator.next();
            if (TextUtils.isEmpty(result.SSID)) {
                resultIterator.remove();
            } else if (result.SSID.equals(removeQuotes(info.getSSID()))) {
                mCapabilities = result.capabilities;
                resultIterator.remove();
            }
        }
        wifiAdapter.setNewData(results);
        refreshView(false);
    }

    /**
     * 更新Wi-Fi连接状态
     */
    private void updateWifiConnection() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (!TextUtils.isEmpty(wifiInfo.getSSID())) {
            //获取到连接的信息
            if (wifiInfo.getSSID().contains("unknown")) {
                mCurrentWifiCl.setVisibility(View.GONE);
            } else {
                mCurrentWifiCl.setVisibility(View.VISIBLE);
                mWifiName.setText(removeQuotes(wifiInfo.getSSID()));

                if ((wifiInfo.getIpAddress() == 0) && (isNetworkAvailable(mContext) == 2)) {
                    //正在连接
                    linksPb.setVisibility(View.VISIBLE);
                    mConnectIcon.setVisibility(View.GONE);
                } else {
                    if (isNetworkAvailable(mContext) == 2) {
                        //已连接
                        mConnectIcon.setVisibility(View.VISIBLE);
                        linksPb.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            mCurrentWifiCl.setVisibility(View.GONE);
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

    /**
     * 布局集合
     * wifi关闭状态隐藏对应布局
     *
     * @param isVisible 是否可见
     */
    private void wifiGroupVisible(boolean isVisible) {
        if (!isVisible) {
            mCurrentWifiCl.setVisibility(View.GONE);
        }
        mChooseWifi.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        wifiRv.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        wifiRefreshView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

}
