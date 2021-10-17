package com.deeplabstudio.tolki.Adapter.ChatList;

public class Users {
    private String zero,one,channelId;

    public Users(){}

    public Users(String zero, String one, String channelId) {
        this.zero = zero;
        this.one = one;
        this.channelId = channelId;
    }

    public String getZero() {
        return zero;
    }

    public String getOne() {
        return one;
    }

    public String getChannelId() {
        return channelId;
    }
}
