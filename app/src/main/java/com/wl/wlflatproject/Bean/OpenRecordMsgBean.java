package com.wl.wlflatproject.Bean;

import java.util.List;

/**
 * 开始记录实体
 * @Author zhuobaolian
 * @Date 15:23
 */
public class OpenRecordMsgBean {


    private int code;
    private List<OpenRecordMsgDataBean> data;
    private String msg;
    private String note;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<OpenRecordMsgDataBean> getData() {
        return data;
    }

    public void setData(List<OpenRecordMsgDataBean> data) {
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

    public static class OpenRecordMsgDataBean {
        private List<UnlockMsgListDTO> unlockMsgList;
        private String showDate;
        private int timestamp;

        public List<UnlockMsgListDTO> getUnlockMsgList() {
            return unlockMsgList;
        }

        public void setUnlockMsgList(List<UnlockMsgListDTO> unlockMsgList) {
            this.unlockMsgList = unlockMsgList;
        }

        public String getShowDate() {
            return showDate;
        }

        public void setShowDate(String showDate) {
            this.showDate = showDate;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public static class UnlockMsgListDTO {
            /**
             * 开锁类型
             */
            private int unlockMode;
            private String date;
            private String deviceId;
            private String imgOrientation;
            private String messageType;
            private String picPath;
            /**
             * 日期
             */
            private String showDate;
            /**
             * 时间
             */
            private String showTime;
            private int timestamp;
            private String unlockDescribe;
            private String unlockId;
            /**
             * 用户
             */
            private String userNote;
            private String vendorName;
            private String videoPath;
            private String week;

            public int getUnlockMode() {
                return unlockMode;
            }

            public void setUnlockMode(int unlockMode) {
                this.unlockMode = unlockMode;
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

            public int getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(int timestamp) {
                this.timestamp = timestamp;
            }

            public String getUnlockDescribe() {
                return unlockDescribe;
            }

            public void setUnlockDescribe(String unlockDescribe) {
                this.unlockDescribe = unlockDescribe;
            }

            public String getUnlockId() {
                return unlockId;
            }

            public void setUnlockId(String unlockId) {
                this.unlockId = unlockId;
            }

            public String getUserNote() {
                return userNote;
            }

            public void setUserNote(String userNote) {
                this.userNote = userNote;
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
