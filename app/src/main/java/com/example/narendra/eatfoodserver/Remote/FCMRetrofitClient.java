package com.example.narendra.eatfoodserver.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by narendra on 3/13/2018.
 */

public class FCMRetrofitClient {
    private static Retrofit retrofit=null;
    public static Retrofit getClient(String baseUrl){
        if (retrofit== null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
