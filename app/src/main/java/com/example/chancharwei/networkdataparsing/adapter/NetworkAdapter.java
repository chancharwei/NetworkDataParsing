package com.example.chancharwei.networkdataparsing.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.chancharwei.networkdataparsing.RetrofitClient;
import com.example.chancharwei.networkdataparsing.fragments.NetworkFragment;
import com.example.chancharwei.networkdataparsing.networkInfo.NetworkAPI;
import com.example.chancharwei.networkdataparsing.networkInfo.NetworkData;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NetworkAdapter implements Callback<List<NetworkData>> {
    private static final String TAG = NetworkAdapter.class.getSimpleName();
    private Fragment fragment;
    public NetworkAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void networkSearch() {
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkAPI networkAPI = retrofit.create(NetworkAPI.class);
        Call<List<NetworkData>> networkCall = networkAPI.getNetworkData();
        networkCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<NetworkData>> call, Response<List<NetworkData>> response) {
        Log.i(TAG,"response code "+response.code());
        List<NetworkData> networkData = response.body();
        ((NetworkFragment)fragment).getDataFromNetwork(networkData);
    }

    @Override
    public void onFailure(Call<List<NetworkData>> call, Throwable t) {

    }

    public Bitmap downloadImage(String imageURL) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(imageURL).openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return BitmapFactory.decodeStream(bufferedInputStream);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
