package com.example.chancharwei.networkdataparsing.threadUse;

import android.util.Log;

public class Service {
    private static final String TAG = Service.class.getSimpleName()+"[ByronLog]";
    private WorkerThreadPool workerThreadPool;
    public Service() {
        workerThreadPool = new WorkerThreadPool();
    }

    public void handleRequest(Request request) {
        workerThreadPool.serviceHandleRequest(request);
    }
    public void cleanAllRequest() {
        Log.i(TAG,"cleanAllRequest E");
        workerThreadPool.cleanIdle();
    }
}
