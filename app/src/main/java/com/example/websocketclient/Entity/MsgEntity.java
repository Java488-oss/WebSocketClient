package com.example.websocketclient.Entity;

public class MsgEntity {

    private Long Id;
    private String NameTo;
    private int TabTo;
    private String NameFrom;
    private int TabFrom;
    private String Msg;
    private int Status;
    private String MsgDate;

    public MsgEntity() {
    }

    public MsgEntity(String nameTo, int tabTo, String nameFrom, int tabFrom, String msg, int status, String msgDate) {
        NameTo = nameTo;
        TabTo = tabTo;
        NameFrom = nameFrom;
        TabFrom = tabFrom;
        Msg = msg;
        Status = status;
        MsgDate = msgDate;
    }

    public String getNameTo() {
        return NameTo;
    }

    public void setNameTo(String nameTo) {
        NameTo = nameTo;
    }

    public int getTabTo() {
        return TabTo;
    }

    public void setTabTo(int tabTo) {
        TabTo = tabTo;
    }

    public String getNameFrom() {
        return NameFrom;
    }

    public void setNameFrom(String nameFrom) {
        NameFrom = nameFrom;
    }

    public int getTabFrom() {
        return TabFrom;
    }

    public void setTabFrom(int tabFrom) {
        TabFrom = tabFrom;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMsgDate() {
        return MsgDate;
    }

    public void setMsgDate(String msgDate) {
        MsgDate = msgDate;
    }
}
