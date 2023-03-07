package com.example.polyOder.model;

public class Message {
    public static String SEND_BY_ME = "me";
    public static String SEND_BY_BOT = "bot";

    private String message;
    private String sendBy;


    public Message(String message, String sendBy) {
        this.message = message;
        this.sendBy = sendBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }
}
