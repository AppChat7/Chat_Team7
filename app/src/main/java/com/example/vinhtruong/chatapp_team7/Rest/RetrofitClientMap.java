package com.example.vinhtruong.chatapp_team7.Rest;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huong.tx on 1/27/2018.
 */

public class RetrofitClientMap {
    public static final String BASE_MAP = "https://maps.googleapis.com/maps/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String base_url) {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create()))
                    .build();
        }
        return retrofit;
    }
}
