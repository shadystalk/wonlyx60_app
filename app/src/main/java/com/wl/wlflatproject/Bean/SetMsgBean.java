package com.wl.wlflatproject.Bean;

public class SetMsgBean {

    private String msg;
    private int flag;
    public  SetMsgBean(){

    }
    public  SetMsgBean(int flat){
        this.flag=flat;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
