package com.example.chancharwei.networkdataparsing;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = RetrofitClient.class.getSimpleName()+"[ByronLog]";
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static Retrofit retrofitInstance;
    public static Retrofit getInstance() {
        if(retrofitInstance == null) {
            Log.i(TAG,"retrofitInstance create");
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }
    private RetrofitClient() {

    }
}
