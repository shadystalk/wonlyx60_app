package com.rockchip.gpadc.demo;

import static com.rockchip.gpadc.demo.rga.HALDefine.CAMERA_PREVIEW_HEIGHT;
import static com.rockchip.gpadc.demo.rga.HALDefine.CAMERA_PREVIEW_WIDTH;
import static com.rockchip.gpadc.demo.rga.HALDefine.IM_HAL_TRANSFORM_FLIP_H;
import static com.rockchip.gpadc.demo.rga.HALDefine.RK_FORMAT_RGBA_8888;
import static com.rockchip.gpadc.demo.rga.HALDefine.RK_FORMAT_YCrCb_420_SP;
import static com.rockchip.gpadc.demo.yolo.PostProcess.INPUT_CHANNEL;
import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchip.gpadc.demo.rga.RGA;
import com.rockchip.gpadc.demo.utils.SerialPortUtil;
import com.rockchip.gpadc.demo.yolo.InferenceWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CameraPreviewActivity extends Activity implements Camera.PreviewCallback {

    private final String TAG = "rkyolo";
    private static final int MAGIC_TEXTURE_ID = 10;


    private Camera mCamera0 = null;
    private TextureView textureView = null;
    public SurfaceTexture mSurfaceTexture = null;
    private SurfaceHolder mSurfaceHolder = null;
    public int flip = -1;    // for CAMERA_FACING_BACK(camera comes with RK3588 using this mode),
    // we do not need flip, using -1, or we need using
    // IM_HAL_TRANSFORM_FLIP_H

    private boolean mIsCameraOpened = false;
    private int mCameraId = -1;
    public byte textureBuffer[];

    // for inference
    private String mModelName = "yolov5s.rknn";
    private String platform = "rk3588";
    private InferenceWrapper mInferenceWrapper;
    private String fileDirPath;     // file dir to store model cache
    private ImageBufferQueue mImageBufferQueue;    // intermedia between camera thread and  inference thread
    private InferenceResult mInferenceResult = new InferenceResult();  // detection result
    private int mWidth;    //surface width
    private int mHeight;    //surface height
    private volatile boolean mStopInference = false;

    //draw result
    private TextView mFpsNum1;
    private TextView mFpsNum2;
    private TextView mFpsNum3;
    private TextView mFpsNum4;
    private ImageView mTrackResultView;
    private Bitmap mTrackResultBitmap = null;
    private Canvas mTrackResultCanvas = null;
    private Paint mTrackResultPaint = null;
    private Paint mTrackResultTextPaint = null;
    private View up;
    private View left;
    private View right;
    private View down;
    private ResizableRectangleView rectangleView;
    private View up1;
    private View left1;
    private View right1;
    private View down1;
    private Button switchRct;
    private int flag=0;//矩形切换标识
    private SerialPortUtil serialPort;
    private Rect cropRect;
    private Rect cropRect1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerapreview);
        ImageView black = findViewById(R.id.black);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mFpsNum1 = (TextView) findViewById(R.id.fps_num1);
        mFpsNum2 = (TextView) findViewById(R.id.fps_num2);
        mFpsNum3 = (TextView) findViewById(R.id.fps_num3);
        mFpsNum4 = (TextView) findViewById(R.id.fps_num4);
        up = findViewById(R.id.up_to_up);
        left = findViewById(R.id.left_to_left);
        right = findViewById(R.id.right_to_right);
        down = findViewById(R.id.down_to_down);
        up1 = findViewById(R.id.up_to_down);
        left1 = findViewById(R.id.left_to_right);
        right1 = findViewById(R.id.right_to_left);
        down1 = findViewById(R.id.down_to_up);
        switchRct = findViewById(R.id.switch_rect);
        textureView = findViewById(R.id.surfaceViewCamera1);
        // 获取裁剪区域
        rectangleView = findViewById(R.id.ResizableRectangleView);
        rectangleView.setView(up,left,down,right,up1,left1,down1,right1);
        mTrackResultView = (ImageView) findViewById(R.id.canvasView);
        fileDirPath = getCacheDir().getAbsolutePath();
        platform = getPlatform();
        Log.d(TAG, "get soc platform:" + platform);

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
        serialPort = SerialPortUtil.getInstance();
        mInferenceWrapper = new InferenceWrapper();
        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        switchRct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (flag){
                    case 0:
                        flag=1;
                        switchRct.setText("矩形2");
                        break;
                    case 1:
                        flag=0;
                        switchRct.setText("矩形1");
                        break;
                }
                rectangleView.setSwitchRect(flag);
            }
        });

        rectangleView.setSwitchRect(flag);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if(cropRect!=null){
            SPUtil.getInstance(this).setSettingParam("rectLeft",cropRect.left);
            SPUtil.getInstance(this).setSettingParam("rectTop",cropRect.top);
            SPUtil.getInstance(this).setSettingParam("rectRight",cropRect.right);
            SPUtil.getInstance(this).setSettingParam("rectBottom",cropRect.bottom);
            SPUtil.getInstance(this).setSettingParam("rectLeft1",cropRect1.left);
            SPUtil.getInstance(this).setSettingParam("rectTop1",cropRect1.top);
            SPUtil.getInstance(this).setSettingParam("rectRight1",cropRect1.right);
            SPUtil.getInstance(this).setSettingParam("rectBottom1",cropRect1.bottom);
            SPUtil.getInstance(this).setSettingParam("haveRect",true);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        stopTrack();
        stopCamera();
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        createPreviewView();
//        startCamera();
//        startTrack();
        super.onResume();


    }




    public static byte[] cropAndFill(byte[] data, int width, int height, Rect cropRect,Rect cropRect1) {
        byte[] filledData = new byte[width * height * 3 / 2]; // YUV420格式，Y占总像素的一半，UV各占四分之一
        // 先复制整个Y分量
        System.arraycopy(data, 0, filledData, 0, width * height);
        // 填充Y分量之外的区域为黑色
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(((x<cropRect1.left&&y<cropRect.top)
                        ||(x>cropRect1.right&&y<cropRect.top)
                        || (x<cropRect1.left&&y>cropRect.bottom)
                        ||(x>cropRect1.right&&y>cropRect.bottom)
                        ||(x<cropRect1.left&&x<cropRect.left)
                        ||(x>cropRect1.right&&x>cropRect.right)
                        ||(y<cropRect1.top&&y<cropRect.top)
                        ||(y>cropRect1.bottom&&y>cropRect.bottom)
                )){
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
        // 填充UV分量之外的区域为黑色
        for (int y = 0; y < uvHeight; y++) {
            for (int x = 0; x < uvWidth; x++) {
                if (((x * 2 < cropRect1.left && y * 2 < cropRect.top) ||
                        (x * 2 > cropRect1.right && y * 2 < cropRect.top) ||
                        (x * 2 < cropRect1.left && y * 2 > cropRect.bottom) ||
                        (x * 2 > cropRect1.right && y * 2 > cropRect.bottom) ||
                        (x * 2 < cropRect1.left && x * 2 < cropRect.left) ||
                        (x * 2 > cropRect1.right && x * 2 > cropRect.right) ||
                        (y * 2 < cropRect1.top && y * 2 < cropRect.top) ||
                        (y * 2 > cropRect1.bottom && y * 2 > cropRect.bottom))) {
                    filledData[uvStartIndex + (y * uvWidth + x) * 2] = (byte) 128; // U分量
                    filledData[uvStartIndex + (y * uvWidth + x) * 2 + 1] = (byte) 128; // V分量
                }
            }
        }
        return filledData;
    }



    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(cropRect==null){
            cropRect = rectangleView.getRect();
            cropRect1 = rectangleView.getRect1();
            boolean haveRect = SPUtil.getInstance(this).getSettingParam("haveRect", false);
            if(haveRect){
                int rectLeft = SPUtil.getInstance(this).getSettingParam("rectLeft", 0);
                int rectTop = SPUtil.getInstance(this).getSettingParam("rectTop", 0);
                int rectRight = SPUtil.getInstance(this).getSettingParam("rectRight", 0);
                int rectBottom = SPUtil.getInstance(this).getSettingParam("rectBottom", 0);
                int rectLeft1 = SPUtil.getInstance(this).getSettingParam("rectLeft1", 0);
                int rectTop1 = SPUtil.getInstance(this).getSettingParam("rectTop1", 0);
                int rectRight1 = SPUtil.getInstance(this).getSettingParam("rectRight1", 0);
                int rectBottom1 = SPUtil.getInstance(this).getSettingParam("rectBottom1", 0);
                rectangleView.oneLeft=rectLeft;
                rectangleView.oneTop=rectTop;
                rectangleView.oneRight=rectRight;
                rectangleView.oneBottom=rectBottom;
                rectangleView.verticalStartX=rectLeft1;
                rectangleView.verticalStartY=rectTop1;
                rectangleView.verticalEndX=rectRight1;
                rectangleView.verticalEndY=rectBottom1;
                cropRect.set(rectLeft, rectTop, rectRight, rectBottom);
                cropRect1.set(rectLeft1, rectTop1, rectRight1, rectBottom1);
                rectangleView.invalidate();
            }
        }
        byte[] filledData = cropAndFill(data, CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT, cropRect, cropRect1);

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

//
//            YuvImage yuvimage = new YuvImage(filledData, ImageFormat.NV21, 1280, 720, null);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            yuvimage.compressToJpeg(new Rect(0, 0, 1280, 720), 80, baos);
//            byte[] jdata = baos.toByteArray();
//
//            Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
//            mTrackResultView.setImageBitmap(bmp);
    }



    private boolean startCamera() {
        if (mIsCameraOpened) {
            return true;
        }

        //(Camera.CameraInfo.CAMERA_FACING_BACK);
        int num = Camera.getNumberOfCameras();
        if (num > 1){
            mCameraId = 1;
        }else{
           return false;
        }
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
//            mCamera0.setPreviewDisplay(mSurfaceHolder);
            int BUFFER_SIZE0 = CAMERA_PREVIEW_WIDTH * CAMERA_PREVIEW_HEIGHT * 3 / 2; // NV21
            byte[][] mPreviewData0 = new byte[][]{new byte[BUFFER_SIZE0], new byte[BUFFER_SIZE0],new byte[BUFFER_SIZE0]};
//            //================================
            for (byte[] buffer : mPreviewData0)
                mCamera0.addCallbackBuffer(buffer);
            mCamera0.setPreviewCallbackWithBuffer(this);
            mCamera0.setPreviewTexture(mSurfaceTexture);
            //==================================
            mCamera0.startPreview();
        } catch (Exception e) {
            mCamera0.release();
            return false;
        }

        mIsCameraOpened = true;

        return true;
    }

    private void stopCamera() {
        if (mIsCameraOpened) {
            mCamera0.setPreviewCallback(null);
            mCamera0.stopPreview();
            mCamera0.release();
            mCamera0 = null;
            mIsCameraOpened = false;
        }

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

    private void startTrack() {
        mInferenceResult.reset();
        mImageBufferQueue = new ImageBufferQueue(3, CAMERA_PREVIEW_WIDTH, CAMERA_PREVIEW_HEIGHT);
        mStopInference = false;
        mInferenceThread = new Thread(mInferenceRunnable);
        mInferenceThread.start();
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

    private Thread mInferenceThread;
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
                    updateMainUI(0, fps);

                }

                updateMainUI(1, 0);
            }

            mInferenceWrapper.deinit();
        }
    };

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

                Log.d(TAG, "Create " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //    新加的
    private long lastDetectionTime = 0;
    private static final long DETECTION_TIMEOUT = 2000; // 2秒超时

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

    // UI线程，用于更新处理结果
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (msg.what == 0) {
                float fps = (float) msg.obj;
                DecimalFormat decimalFormat = new DecimalFormat("00.00");
                String fpsStr = decimalFormat.format(fps);
                mFpsNum1.setText(String.valueOf(fpsStr.charAt(0)));
                mFpsNum2.setText(String.valueOf(fpsStr.charAt(1)));
                mFpsNum3.setText(String.valueOf(fpsStr.charAt(3)));
                mFpsNum4.setText(String.valueOf(fpsStr.charAt(4)));
            } else {
                showTrackSelectResults();
            }
        }
    };

    private void updateMainUI(int type, Object data) {
        Message msg = mHandler.obtainMessage();
        msg.what = type;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }

    public static int sp2px(float spValue) {
        Resources r = Resources.getSystem();
        final float scale = r.getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }


    private void showTrackSelectResults() {
        int width = CAMERA_PREVIEW_WIDTH;
        int height = CAMERA_PREVIEW_HEIGHT;
        if (mTrackResultBitmap == null) {
            mTrackResultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mTrackResultCanvas = new Canvas(mTrackResultBitmap);
            initPaints();
        }
        // Clear canvas
        clearCanvas();
        ArrayList<InferenceResult.Recognition> recognitions = mInferenceResult.getResult(mInferenceWrapper);
        boolean detectedInterestedObject = false;
        for (InferenceResult.Recognition rego : recognitions) {
            int id = rego.getId();
            if (id == 0 || id == 15 || id == 16 || id == 56 || id == 14) {  // Specific categories
                detectedInterestedObject = true;
                Log.e("Activity防夹--","有人");
                drawDetection(rego, width, height);
            }
        }

        // Set GPIO based on detection
        handleGpio(detectedInterestedObject);
        // Update the displayed image
        mTrackResultView.setImageBitmap(mTrackResultBitmap);
    }

    private void initPaints() {
        mTrackResultPaint = new Paint();
        mTrackResultPaint.setColor(0xff06ebff);
        mTrackResultPaint.setStrokeJoin(Paint.Join.ROUND);
        mTrackResultPaint.setStrokeCap(Paint.Cap.ROUND);
        mTrackResultPaint.setStrokeWidth(4);
        mTrackResultPaint.setStyle(Paint.Style.STROKE);
        mTrackResultPaint.setTextSize(sp2px(10));
        mTrackResultPaint.setTypeface(Typeface.SANS_SERIF);

        mTrackResultTextPaint = new Paint();
        mTrackResultTextPaint.setColor(0xff06ebff);
        mTrackResultTextPaint.setStrokeWidth(2);
        mTrackResultTextPaint.setTextSize(sp2px(12));
        mTrackResultTextPaint.setTypeface(Typeface.SANS_SERIF);
    }

    private void clearCanvas() {
        mTrackResultPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mTrackResultCanvas.drawPaint(mTrackResultPaint);
        mTrackResultPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    private void drawDetection(InferenceResult.Recognition rego, int width, int height) {
        RectF detection = new RectF(
                width-rego.getLocation().right * width,
                rego.getLocation().top * height,
                width-rego.getLocation().left * width,
                rego.getLocation().bottom * height
        );
        mTrackResultCanvas.drawRect(detection, mTrackResultPaint);
        mTrackResultCanvas.drawText(
                rego.getTrackId() + " - " + mInferenceResult.mPostProcess.getLabelTitle(rego.getId()),
                detection.left + 5, detection.bottom - 5, mTrackResultTextPaint);
    }

    private void handleGpio(boolean detectedInterestedObject) {
        if (detectedInterestedObject) {
            long currentTimeMillis = System.currentTimeMillis();
            if(currentTimeMillis-lastDetectionTime>2000){
                serialPort.sendDate("+COPEN:1\r\n".getBytes());
                Log.e("Activity防夹--","发送开门");
                lastDetectionTime = System.currentTimeMillis();
            }
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



    public boolean createPreviewView() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                CameraPreviewActivity.this.mSurfaceTexture =surfaceTexture;
                Matrix matrix = textureView.getTransform(new Matrix());
                matrix.setScale(1, -1);
                int height = textureView.getHeight();
                matrix.postTranslate(0, height);
                textureView.setTransform(matrix);
                startCamera();
                startTrack();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
        return true;
    }

}
