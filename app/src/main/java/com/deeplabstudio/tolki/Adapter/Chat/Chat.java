package com.deeplabstudio.tolki.Adapter.Chat;

import com.google.firebase.Timestamp;

public class Chat {
    private String channelId, audio, uid,messageUUID;
    private boolean read,effect;
    private Timestamp timestamp;

    public Chat(){}

    public Chat(String channelId, String audio, String uid, String messageUUID, boolean read, boolean effect, Timestamp timestamp) {
        this.channelId = channelId;
        this.audio = audio;
        this.uid = uid;
        this.messageUUID = messageUUID;
        this.read = read;
        this.effect = effect;
        this.timestamp = timestamp;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getAudio() {
        return audio;
    }

    public String getUid() {
        return uid;
    }

    public String getMessageUUID() {
        return messageUUID;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isEffect() {
        return effect;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
