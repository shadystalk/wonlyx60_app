package com.rockchip.gpadc.demo;

import static com.rockchip.gpadc.demo.rga.HALDefine.CAMERA_PREVIEW_HEIGHT;
import static com.rockchip.gpadc.demo.rga.HALDefine.CAMERA_PREVIEW_WIDTH;
import static com.rockchip.gpadc.demo.rga.HALDefine.IM_HAL_TRANSFORM_FLIP_H;
import static com.rockchip.gpadc.demo.rga.HALDefine.RK_FORMAT_RGBA_8888;
import static com.rockchip.gpadc.demo.rga.HALDefine.RK_FORMAT_YCrCb_420_SP;
import static com.rockchip.gpadc.demo.yolo.PostProcess.INPUT_CHANNEL;
import static java.lang.Thread.sleep;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.widget.Toast;
import com.rockchip.gpadc.demo.rga.RGA;
import com.rockchip.gpadc.demo.yolo.InferenceWrapper;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ComputerServices extends Service implements Camera.PreviewCallback{
    private String mModelName = "yolov5s.rknn";
    private String platform = "rk3588";
    private String fileDirPath;
    private InferenceWrapper mInferenceWrapper;
    private InferenceResult mInferenceResult = new InferenceResult();  // detection result
    private ImageBufferQueue mImageBufferQueue;
    private volatile boolean mStopInference = false;
    private Thread mInferenceThread;
    String TAG="com.rockchip.gpadc.demo.ComputerServices";
    private SurfaceTexture mSurfaceTexture;
    private int previewFormat;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTrack();
        stopCamera();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setGpioDirection();
        fileDirPath = getCacheDir().getAbsolutePath();

        platform = getPlatform();

        if (platform.equals("rk3588")) {
            createFile(mModelName, R.raw.yolov5s_rk3588);
        } else if (platform.equals("rk356x")) {
            createFile(mModelName, R.raw.yolov5s_rk3566);
        } else if (platform.equals("rk3562")) {
            createFile(mModelName, R.raw.yolov5s_rk3562);
        } else {
            Toast toast = Toast.makeText(this, "Can not get platform use RK3588 instead.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            createFile(mModelName, R.raw.yolov5s_rk3588);
        }

        try {
            mInferenceResult.init(getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mInferenceWrapper = new InferenceWrapper();
        startCamera();
        startTrack();
        return super.onStartCommand(intent, flags, startId);
    }
    private void setGpioDirection() {
        try {
            // Execute the command to set the GPIO direction
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            // Write the command to set the GPIO direction
            os.writeBytes("echo out > /sys/class/gpio/gpio33/direction\n");
            os.writeBytes("exit\n");
            os.flush();

            // Close the stream and wait for the process to complete
            os.close();
            process.waitFor();
//            XzjhSystemManager mManager = (XzjhSystemManager)getSystemService("xzjh_server");
//            mManager.xzjhSetGpioDirction(4, 1);
        } catch (IOException | InterruptedException e) {
        }
    }

    private String getPlatform()//取平台版本
    {
        String platform = null;
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            platform = (String) getMethod.invoke(classType, new Object[]{"ro.board.platform"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return platform;
    }


    private void createFile(String fileName, int id) {
        String filePath = fileDirPath + "/" + fileName;
        try {
            File dir = new File(fileDirPath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 目录存在，则将apk中raw中的需要的文档复制到该目录下
            File file = new File(filePath);

            if (!file.exists() || isFirstRun()) {

                InputStream ins = getResources().openRawResource(id);// 通过raw得到数据资源
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[8192];
                int count = 0;

                while ((count = ins.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                ins.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isFirstRun() {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }

        return isFirstRun;
    }

    private void startTrack() {
        mInferenceResult.reset();
        mImageBufferQueue = new ImageBufferQueue(3, CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT);
        mStopInference = false;
        mInferenceThread = new Thread(mInferenceRunnable);
        mInferenceThread.start();
    }

    private Runnable mInferenceRunnable = new Runnable() {
        public void run() {

            int count = 0;
            long oldTime = System.currentTimeMillis();
            long currentTime;
            updateMainUI(1, 0);
            String paramPath = fileDirPath + "/" + mModelName;

            try {
                mInferenceWrapper.initModel(CAMERA_PREVIEW_HEIGHT, CAMERA_PREVIEW_WIDTH, INPUT_CHANNEL, paramPath);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }


            while (!mStopInference) {
                ImageBufferQueue.ImageBuffer buffer = mImageBufferQueue.getReadyBuffer();

                if (buffer == null) {
                    try {
//                        Log.w(TAG, "buffer is null.");
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                InferenceResult.OutputBuffer outputs = mInferenceWrapper.run(buffer.mImage);

                mInferenceResult.setResult(outputs);

                mImageBufferQueue.releaseBuffer(buffer);

                if (++count >= 30) {
                    currentTime = System.currentTimeMillis();

                    float fps = count * 1000.f / (currentTime - oldTime);

//                    Log.d(TAG, "current fps = " + fps);


                    oldTime = currentTime;
                    count = 0;

                }
                updateMainUI(1, 0);
            }

            mInferenceWrapper.deinit();
        }
    };
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
                showTrackSelectResults();
        }
    };
    private Bitmap mTrackResultBitmap = null;
    private Canvas mTrackResultCanvas = null;
    private long lastDetectionTime = 0;
    private static final long DETECTION_TIMEOUT = 2000; // 2秒超时
    private void showTrackSelectResults() {
        int width = CAMERA_PREVIEW_WIDTH;
        int height = CAMERA_PREVIEW_HEIGHT;


        ArrayList<InferenceResult.Recognition> recognitions = mInferenceResult.getResult(mInferenceWrapper);
        boolean detectedInterestedObject = false;

        for (InferenceResult.Recognition rego : recognitions) {
            int id = rego.getId();
            if (id == 0 || id == 15 || id == 16 || id == 56 || id == 14) {  // Specific categories
                detectedInterestedObject = true;
                drawDetection(rego, width, height);
                Log.e("防夹--","有人");
            }
        }

        // Set GPIO based on detection
        handleGpio(detectedInterestedObject);
    }

    private void handleGpio(boolean detectedInterestedObject) {
        if (detectedInterestedObject) {
            setGpioHigh();
            lastDetectionTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - lastDetectionTime > DETECTION_TIMEOUT) {
            setGpioLow();
        }
    }
    private void setGpioHigh() {
//        writeToFile("/sys/class/gpio/gpio33/value", "1");
//        XzjhSystemManager mManager = (XzjhSystemManager)getSystemService("xzjh_server");
//        mManager.xzjhSetGpioValue(4, 1);
//        Log.d(TAG, "GPIO set to High");
    }

    private void setGpioLow() {
//        writeToFile("/sys/class/gpio/gpio33/value", "0");
//        XzjhSystemManager mManager = (XzjhSystemManager)getSystemService("xzjh_server");
//        mManager.xzjhSetGpioValue(4, 0);
//        Log.d(TAG, "GPIO set to Low");
    }

    private void writeToFile(String path, String value) {
        try {
            // Start a process with superuser privileges
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            // Write the command to set the GPIO value
            os.writeBytes("echo " + value + " > " + path + "\n");
            os.writeBytes("exit\n");
            os.flush();

            // Close the stream and wait for the process to complete
            os.close();
            process.waitFor();
        } catch (IOException e) {
            Log.e(TAG, "Error in writing to file: " + path, e);
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted exception", e);
        }
    }
    private void drawDetection(InferenceResult.Recognition rego, int width, int height) {

    }
    private void updateMainUI(int type, Object data) {
        Message msg = mHandler.obtainMessage();
        msg.what = type;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }
    private void stopTrack() {

        mStopInference = true;
        try {
            if (mInferenceThread != null) {
                mInferenceThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mImageBufferQueue != null) {
            mImageBufferQueue.release();
            mImageBufferQueue = null;
        }
    }
    private SurfaceHolder mSurfaceHolder = null;
    private int mCameraId = -1;
    private boolean mIsCameraOpened = false;
    private Camera mCamera0 = null;
    public int flip = -1;
    int previewWidth=1280;
    int previewHeight=720;
    private int  previewDataSize = 2;
    private boolean startCamera() {
        mSurfaceTexture = new SurfaceTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        if (mIsCameraOpened) {
            return true;
        }

        //(Camera.CameraInfo.CAMERA_FACING_BACK);
        int num = Camera.getNumberOfCameras();
//        if (num > 1){
            mCameraId = 0;
//        } else{
//            return false;
//        }
        Log.d(TAG, "mCameraId = " + mCameraId);
        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(mCameraId, camInfo);
            if (mCameraId != -1) {
                mCamera0 = Camera.open(mCameraId);
            } else {
                mCamera0 = Camera.open();
            }
            Log.d(TAG, "mCamera0 = " + mCamera0);
            Log.d(TAG, "camera facing: " + camInfo.facing);
            if (Camera.CameraInfo.CAMERA_FACING_FRONT == camInfo.facing) {
                this.flip = IM_HAL_TRANSFORM_FLIP_H;
            }

        } catch (RuntimeException e) {
            Log.w(TAG, "Unable to open camera!");
            Toast toast = Toast.makeText(this, "Unable to open camera!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }

        setCameraParameters();

        try {
            Camera.Parameters parameters = mCamera0.getParameters();
            mCamera0.setPreviewTexture(mSurfaceTexture);
            previewFormat = parameters.getPreviewFormat();
            this.previewFormat = setCameraPreviewFormat(mCamera0, this.previewFormat);
            int minFps = 30000;
            int maxFps = 30000;
            setCameraPreviewFpsRange(mCamera0, minFps, maxFps);
            int[] hasSetPreviewSize = setCameraPreviewSize(mCamera0, previewWidth, previewHeight);
            if (hasSetPreviewSize != null && hasSetPreviewSize.length > 1) {
                previewWidth = hasSetPreviewSize[0];
                previewHeight = hasSetPreviewSize[1];
            }
            setCameraPictureSize(mCamera0, previewWidth, previewHeight);
            mCamera0.setPreviewCallback(mCameraCallbacks);
            mCamera0.startPreview();
        } catch (IOException localIOException) {
            mCamera0.release();
        }
        mIsCameraOpened = true;
        return true;
    }

    private void setCameraParameters() {
        Camera.Parameters parameters;
        boolean checkWH = false;
        parameters = mCamera0.getParameters();
        int nearest_width_index = 0;
        int nearest_width_value = 1920;

        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);

            if (Math.abs(size.width-CAMERA_PREVIEW_WIDTH) < nearest_width_value ) {
                nearest_width_value = Math.abs(size.width-CAMERA_PREVIEW_WIDTH);
                nearest_width_index = i;
            }

            if ( (size.width == CAMERA_PREVIEW_WIDTH) && (size.height == CAMERA_PREVIEW_HEIGHT)) {
                checkWH = true;
            }

            Log.v(TAG, "Camera Supported Preview Size = " + size.width + "x" + size.height);
        }
        if (!checkWH) {
            Log.e(TAG, "Camera don't support this preview Size = " + CAMERA_PREVIEW_WIDTH + "x" + CAMERA_PREVIEW_HEIGHT);
            CAMERA_PREVIEW_WIDTH = sizes.get(nearest_width_index).width;
            CAMERA_PREVIEW_HEIGHT = sizes.get(nearest_width_index).height;
        }

        Log.w(TAG, "Use preview Size = " + CAMERA_PREVIEW_WIDTH + "x" + CAMERA_PREVIEW_HEIGHT);

        parameters.setPreviewSize(CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT);

        if (parameters.isZoomSupported()) {
            parameters.setZoom(0);
        }
        mCamera0.setParameters(parameters);
        Log.i(TAG, "mCamera0 set parameters success.");
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        ResizableRectangleView resizableRectangleView = new ResizableRectangleView(this);
        Rect cropRect = resizableRectangleView.getRect();
        byte[] filledData = cropAndFill(data, CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT, cropRect);

        mCamera0.addCallbackBuffer(data);
        ImageBufferQueue.ImageBuffer imageBuffer = mImageBufferQueue.getFreeBuffer();


        if (imageBuffer != null) {
            // RK_FORMAT_YCrCb_420_SP -> RK_FORMAT_RGBA_8888
            // flip for CAMERA_FACING_FRONT
            RGA.colorConvertAndFlip(filledData, RK_FORMAT_YCrCb_420_SP,
                    imageBuffer.mImage, RK_FORMAT_RGBA_8888,
                    CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT, this.flip);

            mImageBufferQueue.postBuffer(imageBuffer);
        }
    }


    public static byte[] cropAndFill(byte[] data, int width, int height, Rect cropRect) {
        byte[] filledData = new byte[width * height * 3 / 2]; // YUV420格式，Y占总像素的一半，UV各占四分之一

        // 先复制整个Y分量
        System.arraycopy(data, 0, filledData, 0, width * height);

        // 计算十字形区域
        int verticalWidth = cropRect.width() / 2;
        int horizontalHeight = cropRect.height() / 2;
        int verticalStartX = cropRect.centerX() - verticalWidth / 2;
        int verticalEndX = cropRect.centerX() + verticalWidth / 2;
        int horizontalStartY = cropRect.centerY() - horizontalHeight / 2;
        int horizontalEndY = cropRect.centerY() + horizontalHeight / 2;

        // 填充Y分量之外的区域为黑色
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!(x >= verticalStartX && x <= verticalEndX) && !(y >= horizontalStartY && y <= horizontalEndY)) {
                    filledData[y * width + x] = 0; // 裁剪区域外填充黑色
                }
            }
        }

        // UV分量的开始索引
        int uvStartIndex = width * height;
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        // 复制整个UV分量
        System.arraycopy(data, uvStartIndex, filledData, uvStartIndex, uvWidth * uvHeight * 2);

        // 填充UV分量之外的区域为中性色
        for (int y = 0; y < uvHeight; y++) {
            for (int x = 0; x < uvWidth; x++) {
                if (!(x >= (verticalStartX / 2) && x <= (verticalEndX / 2)) && !(y >= (horizontalStartY / 2) && y <= (horizontalEndY / 2))) {
                    filledData[uvStartIndex + (y * uvWidth + x) * 2] = (byte) 128; // U分量
                    filledData[uvStartIndex + (y * uvWidth + x) * 2 + 1] = (byte) 128; // V分量
                }
            }
        }

        return filledData;
    }


    private int setCameraPreviewFormat(Camera camera, int previewFormat) {
        Camera.Parameters parameters = camera.getParameters();
        // 默认预览格式
        int defaultPreviewFormat = parameters.getPreviewFormat();

        // 设置的预览图像格式是否是相机支持的
        boolean isSetFormatSuit = false;
        boolean isSupportYV12Format = false;
        int supportedFirstFormat = ImageFormat.NV21;
        List<Integer> pfa = parameters.getSupportedPreviewFormats();
        // 列出所有支持的格式
        for (int index = 0; index < pfa.size(); index++) {
            if (index == 0) {
                supportedFirstFormat = pfa.get(index);
            }
            if (previewFormat == pfa.get(index)) {
                isSetFormatSuit = true;
            }
            if (ImageFormat.YV12 == pfa.get(index)) {
                isSupportYV12Format = true;
            }
        }

        if (!isSetFormatSuit) {
        }
        if (isSetFormatSuit) {
            parameters.setPreviewFormat(previewFormat);
        } else if (isSupportYV12Format) {
            parameters.setPreviewFormat(ImageFormat.YV12);
        } else {
            parameters.setPreviewFormat(supportedFirstFormat);
        }
        // 设置预览格式
        camera.setParameters(parameters);

        return camera.getParameters().getPreviewFormat();
    }
    private void setCameraPreviewFpsRange(Camera camera, int minFps, int maxFps) {
        int min = 30000;
        int max = 30000;
        Camera.Parameters parameters = camera.getParameters();
        int[] defaultPreviewFps = new int[2];
        parameters.getPreviewFpsRange(defaultPreviewFps);
        List<int[]> supportedFpsRangeArray = parameters.getSupportedPreviewFpsRange();
        boolean isSupportSetFps = false;
        for (int index = 0; index < supportedFpsRangeArray.size(); index++) {
            if (index >= supportedFpsRangeArray.size() - 1) {
                min = supportedFpsRangeArray.get(index)[0];
                max = supportedFpsRangeArray.get(index)[1];
            }
            if (supportedFpsRangeArray.get(index)[0] == minFps && supportedFpsRangeArray.get(index)[1] == maxFps) {
                isSupportSetFps = true;
            }
        }
        if (isSupportSetFps) {
            min = minFps;
            max = maxFps;
        }
        parameters.setPreviewFpsRange(min, max);
        camera.setParameters(parameters);

        int[] afterFps = new int[2];
        camera.getParameters().getPreviewFpsRange(afterFps);
        boolean isSupportZoom = parameters.isZoomSupported();
        Log.i(
                TAG, "surfaceCreated defaultPreviewFps, min = "
                        + defaultPreviewFps[0] + ", max = " + defaultPreviewFps[1] + ", zoom support=" + isSupportZoom
                        + ", after set fps min=" + afterFps[0] + ", max=" + afterFps[1]
        );
    }


    private int[] setCameraPictureSize(Camera camera, int width, int height) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size defaultPictureSize = parameters.getPictureSize();
        // set picture size
        List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
        Camera.Size expected = sizes.get(sizes.size() - 1);
        boolean gotIt = false;
        for (Camera.Size size : sizes) {
            if (size.width == width && size.height == height) {
                expected = size;
                gotIt = true;
                Log.i(TAG, "setCameraPictureSize width,height is supported");
                break;
            }
        }
        int resultWidth = expected.width;
        int resultHeight = expected.height;
        if (!gotIt) {
            resultWidth = width;
            resultHeight = height;
            Log.i(TAG, "setCameraPictureSize width,height is not supported");
        }
        parameters.setPictureSize(resultWidth, resultHeight);
        camera.setParameters(parameters);
        Log.i(
                "TAG",
                "setCameraPictureSize defaultPictureSize width=" + defaultPictureSize.width + ", height=" + defaultPictureSize.height
                        + ", after set picture size width=" + camera.getParameters().getPictureSize().width + ", height=" + camera.getParameters().getPictureSize().height
        );

        return new int[]{resultWidth, resultHeight};
    }
    private int[] setCameraPreviewSize(Camera camera, int width, int height) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size defaultPreviewSize = parameters.getPreviewSize();
        // set preview size
        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size expected = sizes.get(sizes.size() - 1);
        boolean gotIt = false;
        for (Camera.Size size : sizes) {
            if (size.width == width && size.height == height) {
                expected = size;
                gotIt = true;
                Log.i(TAG, "setCameraPreviewSize width,height is supported");
                break;
            }
        }
        int resultWidth = expected.width;
        int resultHeight = expected.height;
        if (!gotIt) {
            resultWidth = width;
            resultHeight = height;
            Log.i(TAG, "setCameraPreviewSize width,height is not supported");
        }
        parameters.setPreviewSize(resultWidth, resultHeight);
        camera.setParameters(parameters);
        Log.i(
                "TAG",
                "setCameraPreviewSize defaultPreviewSize width=" + defaultPreviewSize.width + ", height=" + defaultPreviewSize.height
                        + ", after set preview size width=" + camera.getParameters().getPreviewSize().width + ", height=" + camera.getParameters().getPreviewSize().height
        );

        return new int[]{resultWidth, resultHeight};
    }
    private Camera.PreviewCallback mCameraCallbacks = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
//            YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, 1280, 720, null);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            yuvimage.compressToJpeg(new Rect(0, 0, 1280, 720), 80, baos);
//            byte[] jdata = baos.toByteArray();
//
//            Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
//            countNumBinder.setBmp(bmp);


            // 获取裁剪区域
            ResizableRectangleView rectangleView = new ResizableRectangleView(ComputerServices.this);
            Rect cropRect = rectangleView.getRect();


            byte[] filledData = cropAndFill(data, CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT, cropRect);

            mCamera0.addCallbackBuffer(data);
            ImageBufferQueue.ImageBuffer imageBuffer = mImageBufferQueue.getFreeBuffer();


            if (imageBuffer != null) {
                // RK_FORMAT_YCrCb_420_SP -> RK_FORMAT_RGBA_8888
                // flip for CAMERA_FACING_FRONT
                RGA.colorConvertAndFlip(filledData, RK_FORMAT_YCrCb_420_SP,
                        imageBuffer.mImage, RK_FORMAT_RGBA_8888,
                        CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT, ComputerServices.this.flip);

                mImageBufferQueue.postBuffer(imageBuffer);
            }
        }
    };


    private void stopCamera() {
        if (mIsCameraOpened) {
            mCamera0.setPreviewCallback(null);
            mCamera0.stopPreview();
            mCamera0.release();
            mCamera0 = null;
            mIsCameraOpened = false;
        }

    }
}
