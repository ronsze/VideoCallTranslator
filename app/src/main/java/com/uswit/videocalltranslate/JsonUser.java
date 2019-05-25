package com.uswit.videocalltranslate;

public class JsonUser {
    String roomId;
    String roomName;
    boolean isFaivorite;

    JsonUser(String id, String name, boolean isFaivo){
        this.roomId = id;
        this.roomName = name;
        this.isFaivorite = isFaivo;
    }
}
