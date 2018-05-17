package com.example.vinhtruong.chatapp_team7.Models;

/**
 * Created by vinhtruong on 5/6/2018.
 */

public class Room {
    private String rommName;
    private String idCreator;

    public Room() {
    }

    public Room(String rommName, String idCreator) {
        this.rommName = rommName;
        this.idCreator = idCreator;
    }

    public String getRommName() {
        return rommName;
    }

    public void setRommName(String rommName) {
        this.rommName = rommName;
    }

    public String getIdCreator() {
        return idCreator;
    }

    public void setIdCreator(String idCreator) {
        this.idCreator = idCreator;
    }
}
