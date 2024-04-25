package com.wl.wlflatproject.MUtils;

import android.util.Log;

import com.wl.wlflatproject.Activity.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android_serialport_api.SerialPort;
import ru.sir.ymodem.YModem;

public class SerialPortUtil {
    public SerialPort mSerialPort;
    public BufferedReader inputStream;
    public OutputStream outputStream;
    private static SerialPortUtil mSerialPortUtil = null;
    public boolean flag=true;
    public ExecutorService threads;
    private List<DataListener> dataListenerList=new ArrayList<>();


    public static SerialPortUtil getInstance() {
        Log.e("串口；","getInstance");
        if (mSerialPortUtil == null) {
            mSerialPortUtil = new SerialPortUtil();
        } else {
            return mSerialPortUtil;
        }
        return mSerialPortUtil;
    }


    //链接串口
    public SerialPortUtil() {
        startSerialPortUtil();
    }

    private void init() {
        Log.e("串口；","初始化");
        if (mSerialPort == null) {
            String path;
            path = "/dev/ttyS9";//串口地址
            int baurate = 9600;
            try {
                Log.e("串口；",path);
                mSerialPort = new SerialPort(new File(path), baurate, 0);
                inputStream=new BufferedReader(new InputStreamReader(mSerialPort.getInputStream()));
                outputStream = mSerialPort.getOutputStream();
                Log.e("串口；","创建成功");
            } catch (IOException e) {
                Log.e("串口；","报错");
                e.printStackTrace();
            }
        }
    }

    public void startSerialPortUtil(){
        init();
    }


    //发送数据给串口
    public void sendDate(byte[] writeBytes) {
        String s = new String(writeBytes);
        Log.e("send；",s);
        if (outputStream != null) {
            try {
                outputStream.write(writeBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void setThread(ExecutorService threads){
        this.threads=threads;
    }
    public void addListener(DataListener listener){
        dataListenerList.add(listener);
    }
    public void removeListener(DataListener listener){
        dataListenerList.remove(listener);
    }
    public void readCode(DataListener listener) {
        if (inputStream != null) {
            dataListenerList.add(listener);
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    Log.e("串口；","监听");
                    while (flag) {
                        try {
                            String s = inputStream.readLine();
                            if (s.length() > 0 && listener != null) {//有数据返回
                                Log.e("buffer", s);
                                if(dataListenerList.size()>1){
                                    for(int x=0;x<dataListenerList.size();x++){
                                        dataListenerList.get(x).getData(s);
                                    }
                                }else{
                                    listener.getData(s);
                                }

                            }
                        } catch (Exception e) {
                            Log.e("串口错误", e.toString());
                            String s = e.toString();
                            close();
                        }
                    }
                }
            });
        }
    }
    public void close(){
        if(mSerialPort!=null){
            try {
                flag=false;
                inputStream.close();
                outputStream.close();
                mSerialPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSerialPort=null;
            inputStream=null;
            outputStream=null;
            mSerialPortUtil=null;
            Log.e("串口；",(mSerialPortUtil==null)+"");
        }
    }
    public interface DataListener{
        void getData(String data);
    }

}
