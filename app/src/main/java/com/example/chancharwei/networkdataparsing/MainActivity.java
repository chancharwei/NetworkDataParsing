package com.example.chancharwei.networkdataparsing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.chancharwei.networkdataparsing.fragments.MainFragment;
import com.example.chancharwei.networkdataparsing.fragments.NetworkFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener{
    private static final String TAG = MainActivity.class.getSimpleName()+"[ByronLog]";
    private FragmentManager mFragmentManager;
    private static final String MAIN = "MAIN";
    private static final String NETWORK = "Network";

    private Fragment mMainFragment,mNetworkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate "+this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        if(savedInstanceState == null) {
            initialFragment();
        }else {
            mMainFragment = mFragmentManager.findFragmentByTag(MAIN);
            mNetworkFragment = mFragmentManager.findFragmentByTag(NETWORK);
        }

    }

    private void initialFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mMainFragment = MainFragment.newInstance();
        fragmentTransaction.add(R.id.container,mMainFragment, MAIN);
        fragmentTransaction.commit();
    }

    private void startNetworkFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if(mNetworkFragment == null) mNetworkFragment = NetworkFragment.newInstance();
        if(!mNetworkFragment.isAdded()) {
            fragmentTransaction.replace(R.id.container,mNetworkFragment, MAIN);
        }else {
            //fragmentTransaction.remove(mMainFragment);
            fragmentTransaction.show(mNetworkFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction() {
        startNetworkFragment();
    }
}
