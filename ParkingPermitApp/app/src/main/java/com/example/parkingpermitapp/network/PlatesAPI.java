package com.example.parkingpermitapp.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Header;
public interface PlatesAPI {
    @GET("lp")
    Call<DriverInfo> queryLicensePlate(@Query("state") String state, @Query("plate") String licensePlate, @Header("api-key") String api_key);
}
