package com.wl.wlflatproject.MUtils;

import android.content.Context;

import com.lzy.okgo.model.HttpHeaders;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;

public class ApiSrevice {
    public static String baseUrl = "https://ag-micro.wonlycloud.com:10301";

    public static String searchInfo = baseUrl + "/api/aigang/ten/screen/bind/info";    //查询设备绑定用户信息
    /**
     * 开门记录
     */
    public static String queryUnlockRecord = baseUrl + "/api/aigang/ten/screen/queryUnlockRecord";

    /**
     * 告警消息
     */
    public static String queryAlarmMsg = baseUrl + "//api/aigang/ten/screen/queryAlarmMsg";
    public static HttpHeaders getHeads(Context context) {
        String devId = SPUtil.getInstance(context).getSettingParam("devId", "");
        devId="BE:00:00:00:00:00:DB:BE";
        String versionName = VersionUtils.getVersionName(context);
        HttpHeaders headers = new HttpHeaders();
        headers.put("clientId", devId);
        headers.put("appId", "wonly_screen_10");
        headers.put("appVersion", versionName);
        long l = System.currentTimeMillis();
        headers.put("timestamp", l + "");
        String trim = calculateHmacMD5(devId, l).trim();
        headers.put("token", trim);
        return headers;
    }


    public static String calculateHmacMD5(String devId,long timestamp) {
        try {
            String key = devId+"_ag_"+timestamp;
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(devId.getBytes());
            String s = Base64.encodeToString(hmacBytes, Base64.DEFAULT);
            return s;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
    }
}
