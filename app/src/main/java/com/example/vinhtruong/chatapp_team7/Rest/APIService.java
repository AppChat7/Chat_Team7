package com.example.vinhtruong.chatapp_team7.Rest;


import com.example.vinhtruong.chatapp_team7.Models.use_map.Directions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by huong.tx on 1/27/2018.
 */

public interface APIService {

 @GET("api/directions/json?key=AIzaSyDomAp4eyEtU9PeMEnro1m8dD9S0SccLIM")
 Call<Directions> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);

}
