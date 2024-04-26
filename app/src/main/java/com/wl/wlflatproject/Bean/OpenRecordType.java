package com.wl.wlflatproject.Bean;

/**
 * 开始类型
 * @Author zhuobaolian
 * @Date 10:40
 */
public class OpenRecordType {
    /**
     *门开
     */
 public  final  static  int   DOOR_OPEN=0;

    /**
     * 密码开锁
     */
    public  final  static  int     PSW_OPEN=1;
    /**
     * 指纹开锁
     */
    public  final  static  int      FINGERPRINT_OPEN=3;
    /**
     * 人脸开锁
     */
    public  final  static  int     FACE_OPEN=5;
    /**
     * 遥感开锁
     */
    public  final  static  int    SENSING_OPEN=12;
    /**
     * 门内开锁
     */
    public  final  static  int     IN_DOOR_OPEN=13;
    /**
     * 远程开锁
     */
    public  final  static  int      REMOTE_OPEN=14;
    /**
     * 胁迫密码开锁
     */
    public  final  static  int     COERCION_PSW_OPEN=15;
    /**
     * 胁迫指纹开锁
     */
    public  final  static  int    COERCION_FINGERPRINT_OPEN=16;
    /**
     * 双人模式开锁
     */
    public  final  static  int     DOUBLE_MODE_OPEN=100;

}
