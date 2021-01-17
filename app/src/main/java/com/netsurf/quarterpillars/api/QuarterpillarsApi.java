package com.netsurf.quarterpillars.api;

import com.netsurf.quarterpillars.response.Response;

import io.reactivex.Single;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface QuarterpillarsApi {

    @POST("Influencer_login.php")
    Single<Response> login(@Query("email") String email,
                           @Query("password") String password);

    @POST("registration.php")
    Single<Response> registration(@Query("name") String name,
                                  @Query("phone") String phone,
                                  @Query("email") String email,
                                  @Query("password") String password);
}
