package com.wl.wlflatproject.MUtils;

import android.app.Application;
import android.graphics.Color;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;


import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by rongqiang on 2019/5/8 10:23
 */
public class Utils {

    public static byte[] md5(String s) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes("UTF-8"));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (Exception e) {
        }
        return null;
    }

    private static final String toHex(byte hash[]) {
        if (hash == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    public static String hash(String s) {
        try {
            return new String(toHex(md5(s)).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    public static String hash(Map<String, String> map) {
        try {
            return new String(toHex(md5(getsign(map))).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }


    public static String getsign(Map<String, String> map) {
        String msign = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (msign.equals("")) {
                msign = msign + entry.getKey() + "=" + entry.getValue();
            } else {
                msign = msign + "&" + entry.getKey() + "=" + entry.getValue();
            }
            //  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        msign += "&secretKey=abc123456";

        return msign;
        //    String  sign=  "phone="+username.get()+"&timestamp="+timestamp+"&secretKey=abc123456";
    }

    @NonNull
    public static String string2md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    public static String gettimeString() {

        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(System.currentTimeMillis());

    }

    public static String gettimeString(String pattern, long timemillis) {

        return new SimpleDateFormat(pattern, Locale.ENGLISH).format(timemillis);

    }

    public static String gettimeString(long timemillis) {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(timemillis);

    }

    public static String hidePhone(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            return "";
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static int toint(String z) {
        int a = 0;
        try {
            a = Integer.parseInt(z);
        } catch (Exception e) {
        }
        return a;

    }

    public static long tolong(String z) {
        long a = 0;
        try {
            a = Long.parseLong(z);
        } catch (Exception e) {
        }
        return a;

    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xff);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toLowerCase());
        }
        return sb.toString();
    }

    /**
     * SHA-256 加密
     *
     * @param strSrc
     * @return
     */
    public static String SHAEncrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            strDes = parseByte2HexStr(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    /**
     * SHA-256  加密的后24位
     *
     * @param strSrc
     * @return
     */
    public static String SHAEncrypt24(String strSrc) {
        String strDes = SHAEncrypt(strSrc);
        if (strDes == null) {
            return null;
        }
        return strDes.substring(strDes.length() - 24);
    }

    /**
     * SHA-256 加密的后24位+时间戳前8位
     *
     * @param strSrc
     * @return
     */
    public static String SHAend24AndTime8(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            strDes = parseByte2HexStr(md.digest()); // to HexString

            strDes = strDes.substring(strDes.length() - 24) + System.currentTimeMillis() / 100000;
            strDes = SHAEncrypt(strDes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static double todouble(String z) {
        double a = 0;
        try {
            a = Double.parseDouble(z);
        } catch (Exception e) {
        }
        return a;

    }

    public static float lightnessOfColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[2];
    }


    /**
     * 解析Base64数据
     *
     * @param base64String
     * @return
     */
    public static String decodeBase64(String base64String) {
        if (TextUtils.isEmpty(base64String)) {
            return base64String;
        }
        String decodeStr = "";
        try {
            byte[] decode = Base64.decode(base64String, Base64.DEFAULT);
            decodeStr = new String(decode);
        } catch (Exception e) {
            decodeStr = "";
        }
        return decodeStr;
    }

    public static String getBase64String(String s) {
        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
    }



    public static void setSignJson(JSONObject jsonObject, Application application) {
        jsonObject.put("appId", "wonly_zk");
        jsonObject.put("clientId", getAndroidID(application));
        // map.put("env", RetrofitClient.ENV);
        jsonObject.put("timestamp", String.valueOf(System.currentTimeMillis()));
        jsonObject.put("i18n_language", getLanguage());
        jsonObject.put("sign", Utils.hashJson(jsonObject));
        jsonObject.put("signType", "md5");
        jsonObject.put("phoneType", "android");
    }

    public static String hashJson(JSONObject jsonObject) {
        try {
            return new String(toHex(md5(getsignJson(jsonObject))).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
    public static String getsignJson(JSONObject jsonObject) {

        String msign = "";
        for (Map.Entry entry : jsonObject.entrySet()) {
            if (msign.equals("")) {
                msign = msign + entry.getKey() + "=" + entry.getValue();
            } else {
                msign = msign + "&" + entry.getKey() + "=" + entry.getValue();
            }
            //  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        msign += "&secretKey=abc123456";

        return msign;
        //    String  sign=  "phone="+username.get()+"&timestamp="+timestamp+"&secretKey=abc123456";
    }
    public  static String getAndroidID(Application application) {
        String id = Settings.Secure.getString(application.getContentResolver(), "android_id");
        if ("9774d56d682e549c".equals(id)) {
            return "";
        } else {
            return id == null ? "" : id;
        }
    }
}
