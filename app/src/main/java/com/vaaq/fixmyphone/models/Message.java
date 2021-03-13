package com.vaaq.fixmyphone.models;

public class Message {

    private String  senderName;
    private String  senderId;
    private String  message;
    private String  url;
    private long    time;
    private boolean isImage;

    public Message() {
    }

    public Message(String senderName, String senderId, String message, String url, long time, boolean isImage) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.url = url;
        this.time = time;
        this.isImage = isImage;
    }


    public String getSenderName() {
        return senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public long getTime() {
        return time;
    }

    public boolean isImage() {
        return isImage;
    }
}
