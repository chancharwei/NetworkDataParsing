package com.example.chancharwei.networkdataparsing.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chancharwei.networkdataparsing.R;
import com.example.chancharwei.networkdataparsing.adapter.NetworkAdapter;
import com.example.chancharwei.networkdataparsing.adapter.RecyclerViewAdapter;
import com.example.chancharwei.networkdataparsing.networkInfo.NetworkData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NetworkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetworkFragment extends Fragment {
    private static final String TAG = NetworkFragment.class.getSimpleName()+"ByronLog";
    private NetworkAdapter networkAdapter;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
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
        recyclerView = root.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(),this);
        networkAdapter = new NetworkAdapter(this);
        networkAdapter.networkSearch();
        return root;
    }

    @Override
    public void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        Log.i(TAG,"onAttach "+context);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.i(TAG,"onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void getDataFromNetwork(List<NetworkData> networkData) {
        recyclerViewAdapter.setData(reArrangeData(networkData));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private List<NetworkData[]> reArrangeData(List<NetworkData> networkData) {
        List<NetworkData[]> outputNetworkDataList = new ArrayList<>();

        int dataSize = networkData.size();
        final int dataPerGroup = 4;
        for(int i=0;i<dataSize;i+=dataPerGroup) {
            NetworkData[] networkDataArray = new NetworkData[4];
            networkDataArray[0] = networkData.get(i);
            networkDataArray[1] = networkData.get(i+1);
            networkDataArray[2] = networkData.get(i+2);
            networkDataArray[3] = networkData.get(i+3);
            outputNetworkDataList.add(networkDataArray);
        }
        Log.i(TAG,"outputNetworkDataList size "+outputNetworkDataList.size());
        return outputNetworkDataList;
    }

    public Bitmap downloadImage(String imageURL) {
        return networkAdapter.downloadImage(imageURL);
    }
}
