package com.wl.wlflatproject.MUtils;



import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.RecoverySystem;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public final class UpdateManager {
    private static final String TAG = "UpdateManager";

    private Context mContext;

    private Object mLock = new byte[0];
    private boolean mHasOTAUpdate;

    private static volatile UpdateManager INSTANCE;

    private UpdateManager(Context context) {
        mContext = context;
    }

    public static UpdateManager getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (UpdateManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new UpdateManager(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    @SuppressLint("NewApi")
    public void startOTAUpdate(String otaPath, OTAUpdateReadyListener listener) {
        Log.d(TAG, "startOTAUpdate--->" + otaPath);
        File f = new File(otaPath);
        if (!f.exists()) {
            Log.e(TAG, "ota file not found");
            return;
        }

        if (!"update.zip".equals(f.getName())) {
            Log.e(TAG, "ota file name error");
            return;
        }

        if (!otaPath.contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            Log.e(TAG, "ota file path error");
            return;
        }

        try {
            RecoverySystem.verifyPackage(f, new RecoverySystem.ProgressListener() {
                @Override
                public void onProgress(int progress) {
                    Log.d(TAG, "verifyPackage progress--->" + progress);
                }
            }, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "ota升级包验证失败--->");
            e.printStackTrace();
            return;
        }

        String realPath = "/data/media/0/" + f.getName();

        synchronized (mLock) {
            if (mHasOTAUpdate) {
                Log.d(TAG, "OTA升级任务进行中");
                return;
            }
            mHasOTAUpdate = true;
        }

        if (null != listener) {
            listener.onReady();
            Log.w(TAG, "onReady.");
        }
        //不是A/B系统，直接使用此方法升级
        try {
            SPUtil.getInstance(mContext).setSettingParam("SystemUpDate",true);
            RecoverySystem.installPackage(mContext, new File(realPath));
        } catch (IOException e) {
            Log.w(TAG, "IO error while trying to install non AB update.", e);
            return;
        }


    }

    public interface OTAUpdateReadyListener {
        void onReady();
    }
}

