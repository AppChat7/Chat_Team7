package com.example.vinhtruong.chatapp_team7.Models.use_map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Directions {
    @SerializedName("routes")
    @Expose
    private List<Route> routes = new ArrayList<>();

    /**
     * @return The routes
     */
    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
