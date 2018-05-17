package com.example.vinhtruong.chatapp_team7.Models;

import java.io.Serializable;

/**
 * Created by CR7 on 3/9/2018.
 */

public class Conversation implements Serializable{
    private String image;
    private String name;
    private String time;
    private Double lat;
    private Double lon;
    private long isOnline;

    public Conversation() {
    }

    public Conversation(String image, String name, String time) {
        this.image = image;
        this.name = name;
        this.time = time;
    }

    public Conversation(String image, String name, String time, Double lat, Double lon, long isOnline) {
        this.image = image;
        this.name = name;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
        this.isOnline = isOnline;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public long isOnline() {
        return isOnline;
    }

    public void setOnline(long online) {
        isOnline = online;
    }
}
