package com.example.parkingpermitapp.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface PlatesAPI {
    @GET("lp")
    Call<DriverInfo> queryLicensePlate(@Query("state") String state, @Query("plate") String licensePlate);
}
