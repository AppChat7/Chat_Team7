package com.example.vinhtruong.chatapp_team7.Models;

/**
 * Created by vinhtruong on 5/14/2018.
 */

public class History {
    private int id;
    private String status;
    private String date;


    public History(int id, String status, String date) {
        this.id = id;
        this.status = status;
        this.date = date;
    }

    public History(String status, String date) {
        this.status = status;
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
