package com.example.chancharwei.networkdataparsing.networkInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NetworkAPI {
    @GET("/photos")
    Call<List<NetworkData>> getNetworkData();
}
