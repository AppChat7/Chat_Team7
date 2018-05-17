package com.example.vinhtruong.chatapp_team7.Models;

/**
 * Created by vinhtruong on 4/22/2018.
 */

public class Message {
    private String message, type, from, date;


    public Message() {
    }

    public Message(String message, String type, String from) {
        this.message = message;
        this.type = type;
        this.from = from;
    }

    public Message(String message, String type, String from, String date) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}