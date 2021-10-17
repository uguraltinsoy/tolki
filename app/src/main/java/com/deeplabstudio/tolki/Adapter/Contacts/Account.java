package com.deeplabstudio.tolki.Adapter.Contacts;

public class Account {
    private String uid,name,image;

    public Account(String uid, String name, String image) {
        this.uid = uid;
        this.name = name;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
