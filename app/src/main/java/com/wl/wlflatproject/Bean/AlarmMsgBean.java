package com.wl.wlflatproject.Bean;

/**
 * @Author zhuobaolian
 * @Date 14:47
 */
public class AlarmMsgBean {
    /**
     * [{"alarmDescribe":"人体侦测报警",
     * "alarmId":"6615e08f2f525c31ec00f039",
     * "alarmType":"infra",
     * "date":1712709772000,
     * "deviceId":"271f46556701313a56c26bed07a94a96",
     * "eventId":"3f7c9ec49f23493eaa9fef7040790f60",
     * "messageType":"1",
     * "picPath":"https://video.topband-cloud.com/image/cover/DDC3C0D067264EA3B3EADE55AE24346F-6-2.png?auth_key=1712713318-0-0-1ba5dc9b140bef2fb51eaa24d78a6d3c",
     * "showDate":"2024年04月10日",
     * "timestamp":1712709772000,
     * "vendorName":"TopBand",
     * "videoPath":"https://video.topband-cloud.com/image/cover/DDC3C0D067264EA3B3EADE55AE24346F-6-2.png?auth_key=1712713318-0-0-1ba5dc9b140bef2fb51eaa24d78a6d3c",
     * "week":"周三"}]
     */

    private String alarmDescribe;
    private String alarmId;
    private String alarmType;
    private long date;
    private String deviceId;
    private String eventId;
    private int messageType;
    private String picPath;
    private String showDate;
    private long timestamp;
    private String vendorName;
    private String videoPath;
    private String week;

    public String getAlarmDescribe() {
        return alarmDescribe;
    }

    public void setAlarmDescribe(String alarmDescribe) {
        this.alarmDescribe = alarmDescribe;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
