package com.example.chancharwei.networkdataparsing.threadUse;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Thread.sleep;

public class WorkerThreadPool {
    private static final String TAG = WorkerThreadPool.class.getSimpleName()+"[ByronLog]";
    private List<WorkerThread> workerThreads;
    private boolean isCleaning;
    private final Object lock = new Object();
    WorkerThreadPool() {
    workerThreads = new ArrayList<>();
    isCleaning = false;
    }

    synchronized void serviceHandleRequest(Request request) {
       final int limitedThreadCount = 100;
       boolean findWorkThread = false;
       if(workerThreads.size() == limitedThreadCount) {
           while(!findWorkThread) {
               for (WorkerThread workerThread : workerThreads) {
                   if(workerThread.isIdle()) {
                       workerThread.setRequest(request);
                       findWorkThread = true;
                       break;
                   } else {
                       try {
                           sleep(100);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
               }
           }
       }else {
           synchronized (lock) {
               if(!isCleaning) {
                   WorkerThread workerThread = createWorkThread();
                   Log.i(TAG,"workerThread = "+workerThread+" list size = "+workerThreads.size());
                   workerThread.setRequest(request);
               }
           }
       }
    }

    private WorkerThread createWorkThread() {
       WorkerThread workerThread = new WorkerThread();
       workerThread.start();
       workerThreads.add(workerThread);
       try {
           sleep(100);
       }catch (InterruptedException e){
           e.printStackTrace();
       }
       return workerThread;
    }

    synchronized void cleanIdle() {
       synchronized (lock) {
           isCleaning = true;
           while(workerThreads.size()!=0) {
               Log.i(TAG,"workerThreads size is = "+workerThreads.size());
               Iterator<WorkerThread> iterator = workerThreads.iterator();
               while (iterator.hasNext()) {
                   WorkerThread workerThread = iterator.next();
                   if(workerThread.isIdle()) {
                       iterator.remove();
                       workerThread.terminate();
                   }else {
                       try {
                           sleep(100);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
               }
           }
       }
    }
}
