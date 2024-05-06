package com.wl.wlflatproject.Activity;

import static com.wl.wlflatproject.Activity.SettingMainActivity.POSITION_PARAM_KEY;
import static com.wl.wlflatproject.Constant.Constant.RINGTONES_KEY;
import static com.wl.wlflatproject.MUtils.HandlerCode.CAMERA_INIT;
import static com.wl.wlflatproject.MUtils.HandlerCode.DOWN_LOAD_APK;
import static com.wl.wlflatproject.MUtils.HandlerCode.GET_DOOR_INFO;
import static com.wl.wlflatproject.MUtils.HandlerCode.HEARTBEAT;
import static com.wl.wlflatproject.MUtils.HandlerCode.LEAVE;
import static com.wl.wlflatproject.MUtils.HandlerCode.PERMISSION;
import static com.wl.wlflatproject.MUtils.HandlerCode.TIME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.herohan.uvcapp.CameraHelper;
import com.herohan.uvcapp.ICameraHelper;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.qtimes.service.wonly.client.QtimesServiceManager;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.IButtonCallback;
import com.serenegiant.usb.IStatusCallback;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.widget.AspectRatioSurfaceView;
import com.serenegiant.widget.AspectRatioTextureView;
import com.wl.wlflatproject.Adapter.AlarmMsgParentViewAdapter;
import com.wl.wlflatproject.Bean.AlarmMsgBean;
import com.wl.wlflatproject.Bean.BaseBean;
import com.wl.wlflatproject.Bean.CalendarParam;
import com.wl.wlflatproject.Bean.GDFutureWeatherBean;
import com.wl.wlflatproject.Bean.GDNowWeatherBean;
import com.wl.wlflatproject.Bean.InfoBean;
import com.wl.wlflatproject.Bean.OpenTvBean;
import com.wl.wlflatproject.Bean.SetMsgBean;
import com.wl.wlflatproject.Bean.StateBean;
import com.wl.wlflatproject.Bean.UpdataJsonBean;
import com.wl.wlflatproject.Bean.UpdateAppBean;
import com.wl.wlflatproject.Bean.WeatherBean;
import com.wl.wlflatproject.Constant.Constant;
import com.wl.wlflatproject.MUtils.ApiSrevice;
import com.wl.wlflatproject.MUtils.CMDUtils;
import com.wl.wlflatproject.MUtils.DateUtils;
import com.wl.wlflatproject.MUtils.GsonUtils;
import com.wl.wlflatproject.MUtils.LocationUtils;
import com.wl.wlflatproject.MUtils.LunarUtils;
import com.wl.wlflatproject.MUtils.RbMqUtils;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.MUtils.SerialPortUtil;
import com.wl.wlflatproject.MUtils.VersionUtils;
import com.wl.wlflatproject.MView.SimpleUVCCameraTextureView;
import com.wl.wlflatproject.MView.WaitDialogTime;
import com.wl.wlflatproject.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.sir.ymodem.YModem;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.video_play_view)
    AspectRatioSurfaceView videoPlayView;
    @BindView(R.id.lock_bt)
    LinearLayout lockBt;
    @BindView(R.id.video_iv)
    LinearLayout videoIv;
    @BindView(R.id.full_screen)
    ConstraintLayout fullScreen;
    @BindView(R.id.video_play_view_cl)
    ConstraintLayout videoPlayViewCl;
    @BindView(R.id.location_tv)
    TextView locationTv;
    @BindView(R.id.today_weather_view)
    View todayWeatherView;
    @BindView(R.id.today_temp_tv)
    TextView todayTempTv;
    @BindView(R.id.today_weather_tv)
    TextView todayWeatherTv;
    @BindView(R.id.second_day_view)
    View secondDayView;
    @BindView(R.id.second_day_tv)
    TextView secondDayTv;
    @BindView(R.id.wifi_state)
    ImageView wifi_state;
    @BindView(R.id.third_day_view)
    View thirdDayView;
    @BindView(R.id.third_day_tv)
    TextView thirdDayTv;
    @BindView(R.id.weather_ll)
    LinearLayout weatherLl;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.message_date)
    TextView messageDate;
    @BindView(R.id.changKai)
    LinearLayout changKai;
    @BindView(R.id.close_video)
    LinearLayout closeVideo;
    @BindView(R.id.calendar_cn_tv)
    TextView calendarCnTv;

    @BindView(R.id.today_extent_tv)
    TextView todayExtentTv;
    @BindView(R.id.second_weather_tv)
    TextView secondWeatherTv;
    @BindView(R.id.third_weather_tv)
    TextView thirdWeatherTv;
    @BindView(R.id.today_temp_ll)
    LinearLayout todayTempLl;
    @BindView(R.id.door_select_ll)
    LinearLayout doorSelectLl;
    @BindView(R.id.message_edit)
    EditText messageEdit;
    @BindView(R.id.message_tv)
    TextView messageTv;
//    @BindView(R.id.bg)
//    ImageView bg;

    @BindView(R.id.view_next)
    ImageView msgReminderNext;

    @BindView(R.id.recycler_msg)
    RecyclerView msgRecyclerView;
    int countN=0;
    private int version;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private AlertDialog mDownloadDialog;
    private RbMqUtils rbmq;
    public SerialPortUtil serialPort;
    private StateBean bean = new StateBean();
    private WaitDialogTime dialogTime;
    private int changkaiFlag = 3;
    public static String videoWIfi;
    public static String videOldWIfi;
    private boolean watherClick = false;
    private long lastClickTime;
    private long mWorkerThreadID = -1;
    private ICameraHelper mCameraHelper;
    private ICameraHelper.StateCallback mStateListener;
    private Surface mPreviewSurface;
    Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HEARTBEAT://心跳包
                    try {
                        if (wifiManager == null)
                            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        String ssid = wifiInfo.getSSID();
                        if (!TextUtils.isEmpty(ssid) && !ssid.equals("<unknown ssid>")) {
                            bean.setTime(System.currentTimeMillis() / 1000);
                            stateJson = GsonUtils.GsonString(bean);
                            rbmq.pushMsg(id + "#" + stateJson);
                        } else {
                            Toast.makeText(MainActivity.this, "WIFI不可用", Toast.LENGTH_SHORT).show();
                            wifi_state.setBackgroundResource(R.drawable.wifi_disconnect);
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "心跳包报错：" + e.toString(), Toast.LENGTH_SHORT).show();
                    } finally {
                        sendEmptyMessageDelayed(0, 60000);
                    }
                    break;
                case LEAVE: //有人离开
                    releaseCamera();
                    Log.e("有人离开停止视频", "..");
                    break;
                case DOWN_LOAD_APK://下载apk
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                        mDownloadDialog = null;
                    }
                    OkGo.getInstance().cancelTag(MainActivity.this);
                    requestPermission();
                    handler.sendEmptyMessageDelayed(DOWN_LOAD_APK, 24 * 60 * 60 * 1000);
                    break;
                case TIME://时间更新
                    String s = dateUtils.dateFormat6(System.currentTimeMillis());
                    time.setText(s);
                    handler.sendEmptyMessageDelayed(TIME, 1000);
                    break;
                case GET_DOOR_INFO://获取开门机信息
                    serialPort.sendDate("+DATATOPAD\r\n".getBytes());
                    break;
                case PERMISSION://请求权限
                    requestPermission();
                    break;
//                case 15:
//                    serialPort.flag = true;
//                    serialPort.readCode(dataListener);
//                    break;
                case CAMERA_INIT:
                    mWorkerThreadID = handler.getLooper().getThread().getId();
                    initSerialPort();
                    releaseCamera();
                    break;
                case 13:
                    countN++;
                    count.setText("开们:"+countN);
                    closeVideo.setVisibility(View.VISIBLE);
                    break;
//                case 16:
//                    //打开视频
//                    Log.e("usb++","deviceList"+deviceList.size());
//                    if(deviceList.size()==0){
//                        deviceList = QtimesServiceManager.getCameraList(MainActivity.this, QtimesServiceManager.DoorEyeCamera);
//                    }
//                    if (deviceList.size() == 0) {
//                        Toast.makeText(MainActivity.this, "未检测到摄像头", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    mUSBMonitor.requestPermission(deviceList.get(0));
//                    sendEmptyMessageDelayed(18,3000);
//                    break;
//                case 18:
//                    releaseCamera();
//                    sendEmptyMessageDelayed(16,1000);
//                    break;
                default:
                    break;
            }
        }
    };
    private String id;
    private String stateJson;
    private WifiManager wifiManager;
    private NetStatusReceiver receiver;
    private AMapLocation mAMapLocation;
    private long mExitTime;
    public boolean isStart = false;
    private FileOutputStream fout;
    private PrintWriter printWriter;
    private DateUtils dateUtils;
    private String mTodayCode = "";
    private String mSecondCode = "";
    private String mThirdCode = "";
    private ExecutorService threads;
    private SerialPortUtil.DataListener dataListener;
    private YModem yModem;
    private File downLoadFile;
    private String fileUrl;
    private IntentFilter intentFilter;
    private float plankVersionCode;
    private USBMonitor mUSBMonitor;
    private boolean isPlaying = false;
    private UVCCamera camera;
    private HashMap<Integer,UsbDevice> deviceList;
    private MediaPlayer mediaplayer;
    private List<DeviceFilter> filter;
    private PopupWindow clearPopupWindow;
    private String devType;
    private Runnable runnable;
    UsbDevice device=null;
    private PowerManager.WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initCalendar();
        hideBottomUIMenu();
        Sync();
    }

    @SuppressLint("InvalidWakeLockTag")
    private void initData() {
        SPUtil.getInstance(MainActivity.this).setSettingParam(Constant.DEVID,"");
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
        wakeLock.acquire();
        mediaplayer = MediaPlayer.create(this, R.raw.alarm);
//        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
//        mUSBMonitor.register();
        filter = DeviceFilter.getDeviceFilters(this,
                com.serenegiant.uvccamera.R.xml.device_filter);
        deviceList = QtimesServiceManager.getCameraList(MainActivity.this, QtimesServiceManager.DoorEyeCamera);
        if (deviceList==null||deviceList.size() < 1) {
            Toast.makeText(MainActivity.this, "未检测到摄像头", Toast.LENGTH_SHORT).show();
        }
        threads = Executors.newFixedThreadPool(4);
        handler.sendEmptyMessageDelayed(HEARTBEAT, 1000);
        dateUtils = DateUtils.getInstance();
        handler.removeMessages(DOWN_LOAD_APK);
        handler.removeMessages(TIME);
        if (dialogTime == null)
            dialogTime = new WaitDialogTime(this, android.R.style.Theme_Translucent_NoTitleBar);
        handler.sendEmptyMessageDelayed(PERMISSION,10000);
        Log.e("获得Mac地址", id + "");
        rbmq = new RbMqUtils();
        bean.setAck(0);
        bean.setCmd(0x46);
        bean.setDevType(devType);
        bean.setDevId(id);
        bean.setSeqId(1);
        bean.setTime(System.currentTimeMillis() / 1000);
        bean.setVendor("general");
        stateJson = GsonUtils.GsonString(bean);
        receiver = new NetStatusReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);
        handler.sendEmptyMessageDelayed(DOWN_LOAD_APK, 24 * 60 * 60 * 1000);
        handler.sendEmptyMessage(TIME);
        handler.sendEmptyMessageDelayed(CAMERA_INIT, 1000);
        messageEdit.setCursorVisible(false);
        initListener();
        String messageS=SPUtil.getInstance(MainActivity.this).getSettingParam(Constant.MESSAGE,"");
        String messageDateS=SPUtil.getInstance(MainActivity.this).getSettingParam(Constant.MESSAGE_DATE,"");
        if(!TextUtils.isEmpty(messageDateS)){
            messageEdit.setText(messageS);
            messageDate.setText(messageDateS);
        }
        initMsgData();
        handler.sendEmptyMessageDelayed(16,5000);



        //UVC摄像头状态回调
        mStateListener = new ICameraHelper.StateCallback() {
            //插入UVC设备
            @Override
            public void onAttach(UsbDevice device) {
                //设置为当前设备（如果没有权限，会显示授权对话框）
//                mCameraHelper.selectDevice(device);
                MainActivity.this.device=device;
            }

            //打开UVC设备成功（也就是已经获取到UVC设备的权限）
            @Override
            public void onDeviceOpen(UsbDevice device, boolean isFirstOpen) {
                //打开UVC摄像头
                mCameraHelper.openCamera();
            }

            //打开摄像头成功
            @Override
            public void onCameraOpen(UsbDevice device) {
                //开始预览
                mCameraHelper.startPreview();

                //获取预览使用的Size（包括帧格式、宽度、高度、FPS）
                Size size = mCameraHelper.getPreviewSize();
                if (size != null) {
                    int width = size.width;
                    int height = size.height;
                    //需要自适应摄像头分辨率的话，设置新的宽高比
                    videoPlayView.setAspectRatio(width, height);
                }

                //添加预览Surface
                mCameraHelper.addSurface(videoPlayView.getHolder().getSurface(), false);
                isPlaying=true;
                closeVideo.setVisibility(View.VISIBLE);
            }

            //关闭摄像头成功
            @Override
            public void onCameraClose(UsbDevice device) {
                if (mCameraHelper != null) {
                    //移除预览Surface
                    mCameraHelper.removeSurface(videoPlayView.getHolder().getSurface());
                }
            }

            //关闭UVC设备成功
            @Override
            public void onDeviceClose(UsbDevice device) {
                isPlaying=false;
                closeVideo.setVisibility(View.INVISIBLE);
            }

            //断开UVC设备
            @Override
            public void onDetach(UsbDevice device) {
                int a=0;
            }

            //用户没有授予访问UVC设备的权限
            @Override
            public void onCancel(UsbDevice device) {
            }

        };

        //设置SurfaceView的Surface监听回调
        videoPlayView.getHolder().addCallback(new SurfaceHolder.Callback() {

            //创建了新的Surface
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (mCameraHelper != null) {
                    //添加预览Surface
                    mCameraHelper.addSurface(holder.getSurface(), false);
                }
            }

            //Surface发生了改变
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            //销毁了原来的Surface
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (mCameraHelper != null) {
                    //移除预览Surface
                    mCameraHelper.removeSurface(holder.getSurface());
                }
            }
        });

        mCameraHelper = new CameraHelper();
        //设置UVC摄像头状态回调
        mCameraHelper.setStateCallback(mStateListener);



    }

    /**
     * 请求首页告警消息记录
     */
    public void initMsgData(){
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("vendorName","wja");
            //最多展示10條
            requestBody.put("pageSize","10");
        } catch (Exception e) {
          e.printStackTrace();
        }
        OkGo.<String>post(ApiSrevice.queryAlarmMsg).headers(ApiSrevice.getHeads(this)).upJson(requestBody).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String s = response.body();
                AlarmMsgBean infoBean = GsonUtils.GsonToBean(s, AlarmMsgBean.class);
                if (infoBean.getCode() == Constant.SUCCESS_CODE && infoBean.getData() != null) {
                    List<AlarmMsgBean.AlarmMsgDataDTO> data = infoBean.getData();
                    if (data != null) {
                        // 创建主RecyclerView的适配器
                        AlarmMsgParentViewAdapter adapter = new AlarmMsgParentViewAdapter(MainActivity.this, data){
                            @Override
                            public void onBindViewHolder(ViewHolder holder, int position) {
                                super.onBindViewHolder(holder, position);
                                //适配器跟设置的设备动态页面是共用的，但是首页这边的有个偏移，特殊处理
                                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)  holder.dateTv.getLayoutParams();
                                layoutParams.leftMargin=10;
                                holder.dateTv.setLayoutParams(layoutParams);
                            }
                        };

                        // 设置主RecyclerView的布局管理器，这里是为了二级的recycle能够正常展示做的适配
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this) {
                            @Override
                            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            }
                        };
                        layoutManager.setAutoMeasureEnabled(true);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        msgRecyclerView.setLayoutManager(layoutManager);
                        // 设置主RecyclerView的适配器
                        msgRecyclerView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(MainActivity.this, infoBean.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }
        });

    }

    private void initListener() {
        messageTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(clearPopupWindow==null){
                    View inflate = View.inflate(MainActivity.this, R.layout.message_clear, null);
                    clearPopupWindow = new PopupWindow(inflate, 150,65, true);
                    inflate.findViewById(R.id.clear_message).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            messageEdit.setText("");
                            messageDate.setText("");
                            clearPopupWindow.dismiss();
                        }
                    });
                }
                clearPopupWindow.showAsDropDown(changKai,40,-22);

                return false;
            }
        });
        messageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(messageEdit.getText().toString().length()!=0){
                    String s = DateUtils.getInstance().dateFormat11(System.currentTimeMillis());
                    messageDate.setText(s);
                    SPUtil.getInstance(MainActivity.this).setSettingParam(Constant.MESSAGE,messageEdit.getText().toString());
                    SPUtil.getInstance(MainActivity.this).setSettingParam(Constant.MESSAGE_DATE,s);
                    if(messageEdit.getText().toString().length()==55){
                        ToastUtils.showShort("字数超限制");
                    }
                }else{
                    messageDate.setText("");
                }
            }
        });
    }


    @OnClick({R.id.close_video, R.id.date_tv,R.id.calendar_cn_tv,R.id.changKai, R.id.setting, R.id.lock_bt,
            R.id.weather_ll,  R.id.video_iv,R.id.view_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting:
                 Intent intent = new Intent(MainActivity.this, SettingMainActivity.class);
                 startActivity(intent);
                break;
            case R.id.lock_bt://开门
                dialogTime.show();
                serialPort.sendDate("+COPEN:1\r\n".getBytes());
                break;
            case R.id.video_iv:
                if (!isFastClick()) {
                    return;
                }
                if (mCameraHelper != null) {
                    final List<UsbDevice> list = mCameraHelper.getDeviceList();
                    if (list != null && list.size() > 0) {
                        mCameraHelper.selectDevice(list.get(0));
                    }
                }
                break;
            case R.id.close_video:
                mCameraHelper.closeCamera();
                break;
            case R.id.changKai:
                if (changkaiFlag == 1) {
                    dialogTime.show();
                    serialPort.sendDate("+ALWAYSOPEN\r\n".getBytes());
                } else if (changkaiFlag == 2) {
                    dialogTime.show();
                    serialPort.sendDate("+CLOSEALWAYSOPEN\r\n".getBytes());
                }
                break;
//            case R.id.weather_ll:
//                ConnectivityManager connectivityManager
//                        = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//                if (info != null && info.isAvailable()) {
//                    watherClick = true;
//                    mLocationUtils.startLocation();
//
//                } else {
//                    Toast.makeText(this, "WiFi不可用或已断开", Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.date_tv:
            case R.id.calendar_cn_tv:
                //日历
                if (mAMapLocation == null) {
                    Toast.makeText(this, "数据获取中...", Toast.LENGTH_SHORT).show();
                    return;
                }
                CalendarParam calendarParam = new CalendarParam();
                calendarParam.temp = todayTempTv.getText().toString();
                calendarParam.weather = todayWeatherTv.getText().toString();
                String city = mAMapLocation.getCity();
                String district = mAMapLocation.getDistrict();
                calendarParam.location = district == null ? city : district;
                CalendarActivity.start(this, calendarParam);
                break;
            case R.id.view_next:
                intent = new Intent(MainActivity.this, SettingMainActivity.class);
                intent.putExtra(POSITION_PARAM_KEY,3);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 服务器socket
     */
    public void setMq() {
        //发送端
        rbmq.publishToAMPQ("");
        //接收端
        String s = id+"_robot" ;
        rbmq.subscribe(s);
        rbmq.setUpConnectionFactory();
        rbmq.setRbMsgListener(new RbMqUtils.OnRbMsgListener() {
            @Override
            public void AcceptMsg(String msg) {//服务器返回数据
                Log.e("服务器发给平板---", msg);
                BaseBean baseBean = null;
                try {
                    baseBean = GsonUtils.GsonToBean(msg, BaseBean.class);
                } catch (Exception e) {

                    e.printStackTrace();
                }

                if (baseBean != null) {
                    switch (baseBean.getCmd()) {
                        case 0x1001://通知小管家
                            break;
                        case 4103://绑定  解绑
                                InfoBean infoBean = new InfoBean();
                                infoBean.setCode(baseBean.getBind());
                                EventBus.getDefault().post(infoBean);
                            break;
                        default:
                            break;

                    }
                } else {
                    String s = "+MIPLWRITE:" + msg.length() + "," + msg + "\r\n";
                    serialPort.sendDate(s.getBytes());
                }
            }
        });
//        handler.removeMessages(HEARTBEAT);
//        handler.sendEmptyMessageDelayed(HEARTBEAT, 10000);
    }

    /**
     * 串口socket
     */
    private void initSerialPort() {
        Log.e("串口；", "initSerialPort");
        serialPort = SerialPortUtil.getInstance();
        serialPort.setThread(threads);
        handler.sendEmptyMessageDelayed(GET_DOOR_INFO, 2000);
        dataListener = new SerialPortUtil.DataListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
            @Override
            public void getData(String data) {//串口返回数据

                runOnUiThread(new Runnable() {

                    private SetMsgBean setMsgBean;

                    @Override
                    public void run() {
                        if (data.contains("AT+CDOOR=")) {
                            String[] split = data.split("=");
                            if(split.length<2){
                                return;
                            }
                            switch (split[1]) {
                                case "1"://表示故障1
                                    Toast.makeText(MainActivity.this, "故障1", Toast.LENGTH_SHORT).show();
                                    break;
                                case "2"://表示故障2
                                    Toast.makeText(MainActivity.this, "故障2", Toast.LENGTH_SHORT).show();
                                    break;
                                case "C"://表示故障3
                                    Toast.makeText(MainActivity.this, "故障3", Toast.LENGTH_SHORT).show();
                                    break;
                                case "D"://表示故障4
                                    Toast.makeText(MainActivity.this, "故障4", Toast.LENGTH_SHORT).show();
                                    break;
                                case "A"://表示报警1
                                    Toast.makeText(MainActivity.this, "报警1", Toast.LENGTH_SHORT).show();
                                    break;
                                case "B"://表示报警2
                                    Toast.makeText(MainActivity.this, "报警2", Toast.LENGTH_SHORT).show();
                                    break;
                                case "E"://表示假锁状态
                                    Toast.makeText(MainActivity.this, "假锁", Toast.LENGTH_SHORT).show();
                                    break;
                                case "8"://8表示开门成功
                                    //mq  通知小管家开门成功  并且关闭视频
                                    OpenTvBean bean = new OpenTvBean();
                                    bean.setCmd(0x1009);
                                    bean.setAck(0);
                                    bean.setDevType(devType);
                                    bean.setDevid(id);
                                    bean.setVendor("general");
                                    bean.setSeqid(1);
                                    rbmq.pushMsg(id + "#" + GsonUtils.GsonString(bean));
                                    if (dialogTime != null && dialogTime.isShowing())
                                        dialogTime.dismiss();
                                    ToastUtils.showShort("门已打开");
                                    break;
                                case "9"://表示关门成功
                                    if (dialogTime != null && dialogTime.isShowing())
                                        dialogTime.dismiss();
                                default:
                                    break;
                            }
                        } else if (data.contains("AT+DEFAULT=")) {
                            String[] s = data.split("=");
                            if(s.length<2){
                                return;
                            }
                            String[] split = s[1].split(",");
                            try {
                                switch (Integer.parseInt(split[0])) {
                                    case 7://已经常开
                                        if (changkaiFlag != 2) {
                                            changkaiFlag = 2;
                                            changKai.setBackgroundResource(R.drawable.cancel_changkai);
                                            if (QtimesServiceManager.instance().isServerActive()) {
                                                QtimesServiceManager.instance().setLongOpenState(true);
                                            }
                                        }
                                        break;
                                    case 8://没有开启常开
                                        if (changkaiFlag != 1) {
                                            changKai.setBackgroundResource(R.drawable.changkai);
                                            changkaiFlag = 1;
                                            if (QtimesServiceManager.instance().isServerActive()) {
                                                QtimesServiceManager.instance().setLongOpenState(false);
                                            }
                                        }
                                        break;
                                    case 13://唯一id1
                                        if(TextUtils.isEmpty(id)){
                                            id = split[1];
                                            bean.setDevId(id);
                                            SPUtil.getInstance(MainActivity.this).setSettingParam(Constant.DEVID, id);
                                            setMq();
                                        }
                                        break;
                                    case 14://前板版本号
                                        String fVer = split[1];
                                        SPUtil.getInstance(MainActivity.this).setSettingParam(Constant.FVER, fVer);
                                        break;
                                    case 15://后板版本号
                                        String bVer = split[1];
                                        SPUtil.getInstance(MainActivity.this).setSettingParam(Constant.BVER, bVer);
                                        break;
                                    case 16://设备型号
                                        devType = split[1];
                                        SPUtil.getInstance(MainActivity.this).setSettingParam(Constant.DEVTYPE, devType);
                                        break;
                                    default:
                                        break;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else if (data.contains("AT+ALWAYSOPEN=1")) {//常开
                            changkaiFlag = 2;
                            if (dialogTime != null && dialogTime.isShowing())
                            dialogTime.dismiss();
                            changKai.setBackgroundResource(R.drawable.cancel_changkai);
                            ToastUtils.showShort("门已常开");
                        } else if (data.contains("AT+CLOSEALWAYSOPEN=1")) {//取消常开
                            changkaiFlag = 1;
                            if (dialogTime != null && dialogTime.isShowing())
                            dialogTime.dismiss();
                            changKai.setBackgroundResource(R.drawable.changkai);
                            ToastUtils.showShort("门已取消常开");
                        } else if (data.contains("AT+CDWAKE=1")) {    //有人   但是不打开视频
                        } else if (data.contains("AT+CDBELL=1")) {   //门铃
                            Log.e("有人按门铃", "..");
                            int settingParam = SPUtil.getInstance(MainActivity.this).getSettingParam(RINGTONES_KEY, 0);
                            playAudio(Constant.RINGTONES_RES[settingParam]);
                            if (deviceList.size() == 0) {
                                return;
                            }
                            if (!isPlaying) {
                                if (!isFastClick()) {
                                    return;
                                }
                                Set<Integer> set = deviceList.keySet();
                                set.iterator().next();
                                mUSBMonitor.requestPermission(deviceList.get(set.iterator().next()));
                            }

                        } else if (data.contains("AT+CDECT=")) {
                            String[] split = data.split("=");
                            if(split.length<2){
                                return;
                            }
                            String[] split1 = split[1].split(",");
                            switch (split1[0]) {
                                case "0"://表示前板检测到遮挡  门外
                                    if ("0".equals(split1[1])) {//人离开

                                    } else {//人靠近
                                        Log.e("检测有人", "..");
                                        if (deviceList.size() == 0) {
                                            return;
                                        }
                                        if (!isPlaying) {
                                            if (!isFastClick()) {
                                                return;
                                            }
                                            Set<Integer> set = deviceList.keySet();
                                            set.iterator().next();
                                            mUSBMonitor.requestPermission(deviceList.get(set.iterator().next()));
                                        }
                                    }
                                    break;
                                case "1"://表示后板检测到遮挡 门内

                                    break;
                                default:
                                    break;
                            }
                        } else if (data.contains("AT+MIPLNOTIFY=")) {//加密消息上报给服务器
                            String[] split = data.split(",");
                            if(split.length<1){
                                return;
                            }
                            String s = split[split.length - 1];
                            String[] split1 = s.split("\r\n");
                            String s1 = id + "#" + split1[0];
                            rbmq.pushMsg(s1);
//                            handler.removeMessages(HEARTBEAT);
//                            handler.sendEmptyMessageDelayed(HEARTBEAT, 60000);
                        } else if (data.contains("AT+CGSN=1")) {//上传id
                            serialPort.sendDate(("+CGSN:" + id + "\r\n").getBytes());
                        } else if (data.contains("AT+CGATT?")) {//服务器是否链接
                            serialPort.sendDate((rbmq.isConnection() + "\r\n").getBytes());
                        } else if (data.contains("AT+CCLK?")) {//上报时间
                            serialPort.sendDate(("+CCLK:" + System.currentTimeMillis() + "\r\n").getBytes());
                        } else if (data.contains("AT+VIDEOSN=")) { //设置摄像头id
                        } else if (data.contains("AT+WIFISSID=")) { //设置摄像头wifi
                            Log.e("收到摄像头wifi", "----" + data);
                            try {
                                String[] split = data.split("=");
                                videOldWIfi = videoWIfi;
                                if (split.length > 1) {
                                    videoWIfi = split[1];
                                }
                            } catch (Exception e) {

                            }
                        } else if (data.contains("AT+VER=")) { //防夹版本号
                            String[] split = data.split("=V");
                            if(split.length<2){
                                return;
                            }
                            plankVersionCode = Float.parseFloat(split[1]);
                        } else if (data.contains("AT+REQUESTACK=1")) {
                            unregisterReceiver(receiver);
                            handler.removeMessages(HEARTBEAT);
                            threads.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(100);
                                        if (yModem == null) {
                                            yModem = new YModem();
                                        }
                                        yModem.send(downLoadFile, serialPort);
                                        EventBus.getDefault().post(new SetMsgBean(CMDUtils.UPDATE_SUCCESS));
                                    } catch (Exception e) {
                                        EventBus.getDefault().post(new SetMsgBean(CMDUtils.UPDATE_ERRO));
                                        Log.e("固件升级抛出错误---", e.toString());
                                    } finally {
                                        Log.e("固件升---", "结束");
                                        handler.sendEmptyMessage(15);
                                        handler.sendEmptyMessage(HEARTBEAT);
                                        registerReceiver(receiver, intentFilter);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        };

        serialPort.readCode(dataListener);
    }



    /**
     * 初始化日历
     */
    private void initCalendar() {
        DateUtils instance = DateUtils.getInstance();
        //日期
        String dayOrMonthOrYear = instance.getDayOrMonthOrYear1(System.currentTimeMillis());
        dateTv.setText(dayOrMonthOrYear+"  "+instance.getWeekday(System.currentTimeMillis(), true));
        //星期
        //农历
        //猪 贰零壹玖 润 六月 小廿八 己亥 辛未 戊辰
        Calendar calendar = Calendar.getInstance();
        String[] lunar = LunarUtils.getLunar(
                calendar.get(Calendar.YEAR) + "",
                (calendar.get(Calendar.MONTH) + 1) + "",
                calendar.get(Calendar.DAY_OF_MONTH) + "");
        calendarCnTv.setText(String.format("农历-%s月-%s", lunar[3], lunar[4]));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mLocationUtils.startLocation();
        String dayOrMonthOrYear = dateUtils.getDayOrMonthOrYear1(System.currentTimeMillis());
        String weekday = dateUtils.getWeekday(System.currentTimeMillis(), true);
        dateTv.setText(dayOrMonthOrYear+"  "+weekday);
        hideBottomUIMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mLocationUtils.stopLocation();
    }

    @Override
    protected void onDestroy() {
        try {
            if (fout != null) {
                fout.close();
                fout = null;
            }
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rbmq.flag = false;
        rbmq.mClose(false);
        handler.removeMessages(HEARTBEAT);
        handler.removeMessages(LEAVE);
        handler.removeMessages(DOWN_LOAD_APK);
        handler.removeMessages(TIME);
        Log.e("串口；", "ondestroy");
        serialPort.close();
        serialPort.flag = false;
        releaseCamera();
        mLocationUtils.destroyLocationClient();
        unregisterReceiver(receiver);
        if (camera != null) {
            camera.stopPreview();
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        mediaplayer.stop();
        mediaplayer.release();
        super.onDestroy();
    }


    public class NetStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    mLocationUtils.startLocation();
                    handler.removeMessages(HEARTBEAT);
                    handler.sendEmptyMessageDelayed(HEARTBEAT, 10000);
                    rbmq.clearQueue();
                    Log.d("hsl666", "onReceive: ==可用");
                    initCalendar();
                    wifi_state.setBackgroundResource(R.drawable.wifi);
                } else {
                    Toast.makeText(context, "WiFi不可用或已断开", Toast.LENGTH_SHORT).show();
                    Log.d("hsl666", "onReceive: ==不可用");
                    wifi_state.setBackgroundResource(R.drawable.wifi_disconnect);
                }
//
            }
        }
    }

    /**
     * 时间更新的广播
     */
    public class TimeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                DateUtils dateUtils = DateUtils.getInstance();
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour == 0) {
                    //日期
                    String dayOrMonthOrYear = dateUtils.getDayOrMonthOrYear1(System.currentTimeMillis());
                    dateTv.setText(dayOrMonthOrYear+"  "+dateUtils.getWeekday(System.currentTimeMillis(), true));
                    //星期
                    //农历
                    //农历
                    //猪 贰零壹玖 润 六月 小廿八 己亥 辛未 戊辰
                    String[] lunar = LunarUtils.getLunar(
                            calendar.get(Calendar.YEAR) + "",
                            (calendar.get(Calendar.MONTH) + 1) + "",
                            calendar.get(Calendar.DAY_OF_MONTH) + "");
                    calendarCnTv.setText(String.format("农历-%s月-%s", lunar[3], lunar[4]));
                }
            }
        }
    }

    /**
     * 定位工具类
     */
    private LocationUtils mLocationUtils = new LocationUtils() {

        @Override
        protected void onQueryNowWeatherResult(GDNowWeatherBean.LivesBean livesBean, AMapLocation aMapLocation) {
            mAMapLocation = aMapLocation;
            //地址
            StringBuffer locationBuffer = new StringBuffer();
            String city = aMapLocation.getCity();
            String district = aMapLocation.getDistrict();
            locationBuffer.append(city);
            if (district != null) {
                locationBuffer.append(district);
            }
            locationTv.setText(locationBuffer);
            //今日天气
            //ICON Text
            mTodayCode = setWeatherIcon(todayWeatherView, livesBean.getWeather());
            //实时时间
            String dayOrMonthOrYear = DateUtils.getInstance().getDayOrMonthOrYear(System.currentTimeMillis());
            setWeatherText(todayWeatherTv, livesBean.getWeather(), dayOrMonthOrYear, false);
            //体感温度
            todayTempTv.setText(livesBean.getTemperature());
            if (watherClick) {
                watherClick = false;
                WeatherBean bean = new WeatherBean();
                bean.setCmd(0x1002);
                bean.setAck(0);
                bean.setDevType("WonlyRangeHood");
                bean.setDevid(id);
                bean.setVendor("general");
                bean.setSeqid(1);
                bean.setAddress(locationBuffer.toString());
                bean.setWeather(livesBean.getWeather());
                bean.setHumidity(livesBean.getHumidity());
                bean.setTemperature(livesBean.getTemperature());
                long timeStamp = dateUtils.date2TimeStamp(livesBean.getReporttime(), "yyyy-MM-dd HH:mm:ss");
                bean.setTime((int) (timeStamp / 1000));
                rbmq.pushMsg(id + "#" + GsonUtils.GsonString(bean));


                Bundle bundle = new Bundle();
                bundle.putString("param", locationBuffer.toString());
                bundle.putString("param1", livesBean.getTemperature());
                bundle.putString("param2", todayWeatherTv.getText().toString());
                bundle.putString("param3", todayExtentTv.getText().toString());
                bundle.putString("param4", secondWeatherTv.getText().toString());
                bundle.putString("param5", secondDayTv.getText().toString());
                bundle.putString("param6", thirdWeatherTv.getText().toString());
                bundle.putString("param7", thirdDayTv.getText().toString());
                bundle.putString("param8", mTodayCode);
                bundle.putString("param9", mSecondCode);
                bundle.putString("param10", mThirdCode);
                Intent intent = new Intent(MainActivity.this, WeatherActivity1.class);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        }

        @Override
        protected void onQueryFutureWeatherResult(GDFutureWeatherBean.ForecastsBean forecastsBean, AMapLocation aMapLocation) {
            List<GDFutureWeatherBean.ForecastsBean.CastsBean> beanCasts = forecastsBean.getCasts();
            DateUtils dateUtils = DateUtils.getInstance();
            boolean night = dateUtils.isNight();
            //当前天气
            GDFutureWeatherBean.ForecastsBean.CastsBean todayWeather = beanCasts.get(0);
            todayExtentTv.setText("最高 "+todayWeather.getDaytemp() + "c°" + "   最低 " + todayWeather.getNighttemp() + "c°");

            //后两天天气
            GDFutureWeatherBean.ForecastsBean.CastsBean secondWeather = beanCasts.get(1);
            mSecondCode = setWeatherIcon(secondDayView, night ? secondWeather.getNightweather() : secondWeather.getDayweather());
            setWeatherText(secondDayTv,
                    secondWeatherTv,
                    "明天",
                    night ? secondWeather.getNightweather() : secondWeather.getDayweather(),
                    secondWeather.getDaytemp(),
                    secondWeather.getNighttemp());
            GDFutureWeatherBean.ForecastsBean.CastsBean thirdWeather = beanCasts.get(2);
            mThirdCode = setWeatherIcon(thirdDayView, night ? thirdWeather.getNightweather() : thirdWeather.getDayweather());
            setWeatherText(thirdDayTv,
                    thirdWeatherTv,
                    "后天",
                    night ? thirdWeather.getNightweather() : thirdWeather.getDayweather(),
                    thirdWeather.getDaytemp(),
                    thirdWeather.getNighttemp());
        }
    };

    /**
     * 设置天气ICON
     *
     * @param view
     * @param weather
     */
    private String setWeatherIcon(View view, String weather) {
        String code = LocationUtils.weatherCode(weather);
        switch (code) {
            case "1":
                view.setBackgroundResource(R.drawable.sun_icon);
                break;
            case "2":
                view.setBackgroundResource(R.drawable.cloud_icon);
                break;
            case "3":
                view.setBackgroundResource(R.drawable.rain_icon);
                break;
            case "4":
                view.setBackgroundResource(R.drawable.snow_icon);
                break;
            default:
                break;
        }
        return code;
    }

    /**
     * 设置天气文本
     *
     * @param tv
     * @param weather
     * @param showDate
     */
    private void setWeatherText(TextView tv, String weather, String date, boolean showDate) {
        String code = LocationUtils.weatherCode(weather);
        StringBuilder content = new StringBuilder();
        if (showDate) {
            String format11 = DateUtils.getInstance().dateFormat11(date);
            content.append(format11).append(" ");
        }
        switch (code) {
            case "1":
                content.append("晴天");
                break;
            case "2":
                content.append("阴天");
                break;
            case "3":
                content.append("雨天");
                break;
            case "4":
                content.append("雪");
                break;
            default:
                break;
        }
        tv.setText(content.toString());
    }

    /**
     * 设置天气文本
     *
     * @param tv
     * @param date
     * @param weather
     * @param dayTemp
     * @param nightTemp
     */
    private void setWeatherText(TextView tv, TextView tvW, String date, String weather, String dayTemp, String nightTemp) {
        String code = LocationUtils.weatherCode(weather);
        String w = "晴";
        switch (code) {
            case "1":
                w = "晴";
                break;
            case "2":
                w = "阴";
                break;
            case "3":
                w = "雨";
                break;
            case "4":
                w = "雪";
                break;
            default:
                break;
        }
        tvW.setText(date);
        tv.setText(w+"   "+dayTemp + "/" + nightTemp + "°c");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }

        return true;
    }



    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        checkUpdate();
                        if (mLocationUtils != null) {
                            mLocationUtils.startLocation();
                        }
                        initCalendar();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        checkUpdate();
                    }
                })
                .start();
    }

    protected void checkUpdate() {
        version = VersionUtils.getVersionCode(this);
        requestAppUpdate(version, new DataRequestListener<UpdateAppBean>() {
            @Override
            public void success(UpdateAppBean data) {
                downloadApp(data.getPUS().getBody().getUrl());
            }

            @Override
            public void fail(String msg) {

            }
        });
    }

    /**
     * apk升级
     *
     * @param version
     * @param listener
     */
    private void requestAppUpdate(int version, final DataRequestListener<UpdateAppBean> listener) {
        UpdataJsonBean updataJsonBean = new UpdataJsonBean();
        UpdataJsonBean.PUSBean pusBean = new UpdataJsonBean.PUSBean();
        UpdataJsonBean.PUSBean.BodyBean bodyBean = new UpdataJsonBean.PUSBean.BodyBean();
        UpdataJsonBean.PUSBean.HeaderBean headerBean = new UpdataJsonBean.PUSBean.HeaderBean();

        bodyBean.setToken("");
        bodyBean.setVendor_name("general");
        bodyBean.setPlatform("android");

        bodyBean.setEndpoint_type(devType);

        bodyBean.setCurrent_version(version + "");

        headerBean.setApi_version("1.0");
        headerBean.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        headerBean.setSeq_id("1");

        pusBean.setBody(bodyBean);
        pusBean.setHeader(headerBean);
        updataJsonBean.setPUS(pusBean);

        String s = GsonUtils.GsonString(updataJsonBean);
        String path = "https://pus.wonlycloud.com:10400";
        OkGo.<String>post(path).upJson(s).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String s = response.body();
                Gson gson = new Gson();
                try {
                    UpdateAppBean updateAppBean = gson.fromJson(s, UpdateAppBean.class);
                    if (Integer.parseInt(updateAppBean.getPUS().getBody().getNew_version()) > version) {
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


    /**
     * 固件升级
     */
    private void requestFileUpdate() {
        UpdataJsonBean updataJsonBean = new UpdataJsonBean();
        UpdataJsonBean.PUSBean pusBean = new UpdataJsonBean.PUSBean();
        UpdataJsonBean.PUSBean.BodyBean bodyBean = new UpdataJsonBean.PUSBean.BodyBean();
        UpdataJsonBean.PUSBean.HeaderBean headerBean = new UpdataJsonBean.PUSBean.HeaderBean();

        bodyBean.setToken("");
        bodyBean.setVendor_name("general");
        bodyBean.setPlatform("android");
        bodyBean.setEndpoint_type("haomibo");
        bodyBean.setCurrent_version(version + "");

        headerBean.setApi_version("1.0");
        headerBean.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        headerBean.setSeq_id("1");

        pusBean.setBody(bodyBean);
        pusBean.setHeader(headerBean);
        updataJsonBean.setPUS(pusBean);

        String s = GsonUtils.GsonString(updataJsonBean);
        String path = "";
        path = "https://pus.wonlycloud.com:10400";
        OkGo.<String>post(path).upJson(s).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String s = response.body();
                Gson gson = new Gson();
                try {
                    UpdateAppBean updateAppBean = gson.fromJson(s, UpdateAppBean.class);
                    if (Integer.parseInt(updateAppBean.getPUS().getBody().getNew_version()) > plankVersionCode) {
                        EventBus.getDefault().post(new SetMsgBean(CMDUtils.FIND_NEW_VERSION));
                        fileUrl = updateAppBean.getPUS().getBody().getUrl();
                    } else {
                        EventBus.getDefault().post(new SetMsgBean(CMDUtils.CURRENT_NEW_VERSION));
                    }
                } catch (Exception e) {
                    Log.e("升级接口报错", e.toString());
                }
            }

            @Override
            public void onError(Response<String> response) {
            }
        });
    }

    //下载apk文件并跳转(第二次请求，get)
    private void downloadApp(String apk_url) {
        OkGo.<File>get(apk_url).tag(this).execute(new FileCallback() {
            @Override
            public void onError(Response<File> response) {
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                    mDownloadDialog = null;
                }
            }

            @Override
            public void onSuccess(Response<File> response) {
                if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
                    mDownloadDialog.dismiss();
                    mDownloadDialog = null;
                }
                String filePath = response.body().getAbsolutePath();
//                if (!isSystem) {
//                    Intent intent = IntentUtil.getInstallAppIntent(MainActivity.this, filePath);
//                    startActivity(intent);
//                } else {
                boolean b = installApp(filePath);
//                }
            }

            @Override
            public void downloadProgress(Progress progress) {
                if (mDownloadDialog == null) {
                    // 构造软件下载对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("正在更新");
                    // 给下载对话框增加进度条
                    final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
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

    //下载固件
    private void downloadFile(String apk_url) {
        OkGo.<File>get(apk_url).tag(this).execute(new FileCallback() {
            @Override
            public void onError(Response<File> response) {
                Log.e("固件下载", "下载失败");
            }

            @Override
            public void onSuccess(Response<File> response) {
                String filePath = response.body().getAbsolutePath();
                downLoadFile = new File(filePath);
                Log.e("固件下载", "下载成功：" + filePath);
                if (downLoadFile.exists()) {
                    //暂停一下 别的 接收
                    serialPort.flag = false;
                    serialPort.sendDate(("+REQUESTACK" + "\r\n").getBytes());
                }
            }
        });
    }

    public interface DataRequestListener<T> {
        //请求成功
        void success(T data);

        //请求失败
        void fail(String msg);
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




    public boolean installApp(String apkPath) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = new ProcessBuilder("pm", "install", "-r", "-i", "com.wl.wlflatproject", apkPath).start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
            Log.e("静默安装报错", e.toString());
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {

            }
            if (process != null) {
                process.destroy();
            }
        }
        Log.e("result", "" + errorMsg.toString());
        return successMsg.toString().equalsIgnoreCase("success");
    }




    //防止断电回滚
    public void Sync() {
        try {
            Runtime.getRuntime().exec("sync");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= 500) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    //本地摄像头链接
//    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
//        @Override
//        public void onAttach(final UsbDevice device) {
////            Toast.makeText(MainActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
//            Log.e("usb++","connect");
//            queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    if(camera==null)
//                    camera = new UVCCamera();
//                    camera.setStatusCallback(new IStatusCallback() {
//                        @Override
//                        public void onStatus(final int statusClass, final int event, final int selector,
//                                             final int statusAttribute, final ByteBuffer data) {
//                            Toast.makeText(MainActivity.this, "视像头初始化失败，请检测摄像头是否链接", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                    int i = camera.open(ctrlBlock);
//                    Log.e("摄像头", "--state"+i);
//                    if (i != 0) {
//                        return;
//                    }
//                    if (mPreviewSurface != null) {
//                        mPreviewSurface.release();
//                        mPreviewSurface = null;
//                    }
//                    final SurfaceTexture st = videoPlayView.getSurfaceTexture();
//                    if (st != null) {
//                        mPreviewSurface = new Surface(st);
//                        camera.setPreviewDisplay(mPreviewSurface);
//                        camera.startPreview();
//                        Log.e("摄像头", "--start");
//                    }
//
//                    videoPlayView.setAspectRatio(742, 435);
//                    ViewGroup.LayoutParams params = videoPlayView.getLayoutParams();
//                    params.width = 435;
//                    params.height = 752;
//                    videoPlayView.setLayoutParams(params);
//                    videoPlayView.setRotation(-270f);
//                    isStart=true;
//                    isPlaying = true;
//                    handler.sendEmptyMessageDelayed(13,2000);
//                    bg.setVisibility(View.GONE);
//                    handler.removeMessages(LEAVE);
//                    handler.sendEmptyMessageDelayed(LEAVE, 120000);
//                }
//            }, 0);
//        }
//
//        @Override
//        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
//            // XXX you should check whether the coming device equal to camera device that currently using
//
//        }
//
//        @Override
//        public void onDettach(final UsbDevice device) {
//            Toast.makeText(MainActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onCancel(final UsbDevice device) {
//        }
//    };

    private synchronized void releaseCamera() {
        closeVideo.setVisibility(View.INVISIBLE);
//        bg.setVisibility(View.VISIBLE);
        if (!isPlaying) {
            return;
        }
        threads.execute(new Runnable() {
            @Override
            public void run() {
                if (camera != null) {
                    try {
                        camera.destroy();
                        camera.setStatusCallback(null);
                        camera.setButtonCallback(null);
                        camera = null;
                    } catch (final Exception e) {
                        Log.e("摄像头", "--关闭报错"+e.getMessage());
                    }
                }
                if (mPreviewSurface != null) {
                    mPreviewSurface.release();
                    mPreviewSurface = null;
                }
                isPlaying = false;
                Log.e("摄像头", "--关闭");
            }
        });
    }

    protected final synchronized void queueEvent(final Runnable task, final long delayMillis) {
        if ((task == null) || (handler == null)) return;
        try {
            handler.removeCallbacks(task);
            if (delayMillis > 0) {
                handler.postDelayed(task, delayMillis);
            } else if (mWorkerThreadID == Thread.currentThread().getId()) {
                task.run();
            } else {
                handler.post(task);
            }
        } catch (final Exception e) {
            // ignore
        }
    }
    private void playAudio(@RawRes int audioResId) {
        try {
            mediaplayer.reset();
            mediaplayer.setDataSource(getResources().openRawResourceFd(audioResId));
            mediaplayer.prepare();
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

}
