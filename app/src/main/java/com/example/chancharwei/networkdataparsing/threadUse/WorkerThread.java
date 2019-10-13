package com.example.chancharwei.networkdataparsing.threadUse;

public class WorkerThread extends Thread {
    private static final String TAG = WorkerThread.class.getSimpleName()+"[ByronLog]";
    private Request request;
    private boolean isContinue = true;
    boolean isIdle() {
        return request == null;
    }

    void setRequest(Request request) {
        synchronized (this) {
            if(isIdle()) {
                this.request = request;
                notify();
            }
        }
    }

    @Override
    public void run() {
        while(isContinue) {
            synchronized (this) {
                try {
                    wait();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                request.execute();
                request = null;
            }
        }
    }

    public void terminate() {
        isContinue = false;
    }
}
