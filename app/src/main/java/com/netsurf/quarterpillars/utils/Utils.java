package com.netsurf.quarterpillars.utils;

import com.netsurf.quarterpillars.api.QuarterpillarsApi;
import com.netsurf.quarterpillars.retrofit.RetrofitClient;

public class Utils {
    private static final String BASE_URL = "https://quarterpillars.com/mobile_app/test/";

    public static QuarterpillarsApi getApi(){
        return RetrofitClient.getClient(BASE_URL).create(QuarterpillarsApi.class);
    }
}
