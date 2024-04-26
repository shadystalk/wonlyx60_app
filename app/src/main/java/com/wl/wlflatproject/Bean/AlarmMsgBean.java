package com.wl.wlflatproject.Bean;

import java.util.List;

/**
 * 告警消息实体
 * @Author zhuobaolian
 * @Date 14:47
 */
public class AlarmMsgBean {

    /**
     * 状态码 200 成功
     */
    private int code;
    /**
     * 告警消息实际数据
     */
    private List<AlarmMsgDataDTO> data;
    /**
     * 请求失败时候的消息内容
     */
    private String msg;
    private String note;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<AlarmMsgDataDTO> getData() {
        return data;
    }

    public void setData(List<AlarmMsgDataDTO> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static class AlarmMsgDataDTO {
        private List<AlarmMsgListDTO> alarmMsgList;
        private String showDate;
        private Long timestamp;

        public List<AlarmMsgListDTO> getAlarmMsgList() {
            return alarmMsgList;
        }

        public void setAlarmMsgList(List<AlarmMsgListDTO> alarmMsgList) {
            this.alarmMsgList = alarmMsgList;
        }

        public String getShowDate() {
            return showDate;
        }

        public void setShowDate(String showDate) {
            this.showDate = showDate;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public static class AlarmMsgListDTO {
            /**
             * 告警内容
             */
            private String alarmDescribe;
            private String alarmId;
            private String alarmType;
            private String date;
            private String deviceId;
            private String eventId;
            private String imgOrientation;
            private String messageType;
            /**
             * 图片地址
             */
            private String picPath;
            /**
             * 日期
             */
            private String showDate;
            /**
             * 时间
             */
            private String showTime;
            private Long timestamp;
            private String vendorName;
            /**
             * 视频地址
             */
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

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
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

            public String getImgOrientation() {
                return imgOrientation;
            }

            public void setImgOrientation(String imgOrientation) {
                this.imgOrientation = imgOrientation;
            }

            public String getMessageType() {
                return messageType;
            }

            public void setMessageType(String messageType) {
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

            public String getShowTime() {
                return showTime;
            }

            public void setShowTime(String showTime) {
                this.showTime = showTime;
            }

            public Long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(Long timestamp) {
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
    }
}
