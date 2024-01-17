package com.example.plantpilot.Services.Api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PlantApi{
    @POST("identification")
    Call<PlantApiResponse> identify(@Body PlantApiRequest request, @Header("Api-Key") String apiKey);
}