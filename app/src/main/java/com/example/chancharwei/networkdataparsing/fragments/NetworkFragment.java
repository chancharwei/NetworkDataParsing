package com.example.chancharwei.networkdataparsing.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.chancharwei.networkdataparsing.R;
import com.example.chancharwei.networkdataparsing.RetrofitClient;
import com.example.chancharwei.networkdataparsing.networkInfo.NetworkAPI;
import com.example.chancharwei.networkdataparsing.networkInfo.NetworkData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NetworkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetworkFragment extends Fragment implements Callback<List<NetworkData>> {
    private static final String TAG = NetworkFragment.class.getSimpleName()+"ByronLog";
    private NetworkAPI networkAPI;
    public NetworkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment NetworkFragment.
     */
    public static NetworkFragment newInstance() {
        NetworkFragment fragment = new NetworkFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_network, container, false);
        return root;
    }

    @Override
    public void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
        networkSearch();
    }

    private void networkSearch() {
        Retrofit retrofit = RetrofitClient.getInstance();
        networkAPI = retrofit.create(NetworkAPI.class);
        Call<List<NetworkData>> neetworkCall = networkAPI.getNetworkData();
        neetworkCall.enqueue(this);
    }


    @Override
    public void onAttach(Context context) {
        Log.i(TAG,"onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.i(TAG,"onDetach");
        super.onDetach();
    }

    @Override
    public void onResponse(Call<List<NetworkData>> call, Response<List<NetworkData>> response) {
        Log.i(TAG,"response code "+response.code());
        List<NetworkData> networkData = response.body();
        Log.i(TAG,"networkData length = "+networkData.size());
        for(NetworkData eachData : networkData) {
            //Log.i(TAG,"albumID = "+eachData.getId());
        }
    }

    @Override
    public void onFailure(Call<List<NetworkData>> call, Throwable t) {

    }
}
