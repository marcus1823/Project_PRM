package com.example.menaccessoriesshop;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
// 123
public class APIClient {
    private static String baseURL = "https://67df923a7635238f9aa9ee49.mockapi.io/";
    private static Retrofit retrofit;

    public static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
