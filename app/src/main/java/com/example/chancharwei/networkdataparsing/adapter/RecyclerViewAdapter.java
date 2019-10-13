package com.example.chancharwei.networkdataparsing.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chancharwei.networkdataparsing.R;
import com.example.chancharwei.networkdataparsing.networkInfo.NetworkData;
import com.example.chancharwei.networkdataparsing.threadUse.Request;
import com.example.chancharwei.networkdataparsing.threadUse.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName()+"[ByronLog]";
    private final int MAX_DATA_NUMBER = 5000;
    private final int POSITON_DATA_COUNT = 4;
    private List<NetworkData[]> mNetworkData;
    private Context mContext;
    private Fragment mFragment;
    private MyHandler mHandler;
    private Map<Integer,Bitmap> bitMaps;
    private boolean[] loadPositionRecord;
    private Service service;
    private int modifyWidth = 0;
    public RecyclerViewAdapter(Context context, Fragment fragment) {
        mContext = context;
        mFragment = fragment;
        bitMaps = new HashMap<>();
        loadPositionRecord = new boolean[MAX_DATA_NUMBER/POSITON_DATA_COUNT];
        for(int i=0;i<loadPositionRecord.length;i++) {
            loadPositionRecord[i] = false;
        }
        service = new Service();
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.i(TAG,"onCreateViewHolder E");
        int layoutIdForListItem = R.layout.recyclerview_list_item;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        Log.i(TAG,"onCreateViewHolder X "+recyclerViewHolder);
        return recyclerViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        Log.i(TAG, "onBindVIewHolder ("+ position+") holder is "+holder);
        NetworkData[] eachData = mNetworkData.get(position);
        for(int i=0; i<holder.constraintLayoutsGroup.length;i++) {
            holder.constraintLayoutsGroup[i].setBackgroundColor(Color.WHITE);
            TextView textViewID = (TextView)holder.constraintLayoutsGroup[i].findViewById(R.id.id);
            TextView textViewTitle = (TextView)holder.constraintLayoutsGroup[i].findViewById(R.id.title);
            textViewID.setText(Integer.toString(eachData[i].getId()));
            textViewTitle.setText(eachData[i].getTitle());

            if(bitMaps != null && bitMaps.containsKey(eachData[i].getId())) {
                Log.i(TAG,"getImage done id is "+eachData[i].getId());
                Drawable drawable = new BitmapDrawable(bitMaps.get(eachData[i].getId()));
                holder.constraintLayoutsGroup[i].setBackgroundDrawable(drawable);
            }else {
                setBackGroundColor(holder.constraintLayoutsGroup[i],eachData[i].getId(),eachData[i].getThumbnailUrl());
            }

        }
    }

    @Override
    public int getItemCount() {
        if(mNetworkData != null) {
            return mNetworkData.size();
        }else {
            return 0;
        }
    }

    public void setData(List<NetworkData[]> networkData) {
        Log.i(TAG,"setData data size "+networkData.size());
        mNetworkData = networkData;
        preDownloadImage();
    }

    private void preDownloadImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(final NetworkData[] dataPerGroup : mNetworkData) {
                    Request request = new Request() {
                        @Override
                        public void execute() {
                            try {
                                HttpURLConnection connection;
                                for(NetworkData data : dataPerGroup) {
                                    if(bitMaps.containsKey(data.getId())) continue;
                                    Log.i(TAG,"predownload id = "+data.getId());
                                    //Log.i(TAG,"preDownloadImage2 id = "+data.getId());
                                    connection = (HttpURLConnection) new URL(data.getThumbnailUrl()).openConnection();
                                    connection.connect();
                                    InputStream inputStream = connection.getInputStream();
                                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                                    Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                                    bitMaps.put(data.getId(),bmp);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    service.handleRequest(request);
                }
            }
        }).start();
    }

    public void setBackGroundColor(ConstraintLayout constraintLayout,int id,String url) {
        try {
            downloadImage(id,constraintLayout,new URL(url),true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mHandler = new MyHandler();

    }

    private void downloadImage(final int id,final ConstraintLayout constraintLayout,final URL imageURL,final boolean updateView) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection;
                try {
                    connection = (HttpURLConnection) imageURL.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                    //Log.i(TAG,"id = "+id+" constraintLayout = "+constraintLayout);
                    //Log.i(TAG,"id = "+id+" bitmap = "+bmp);
                    bitMaps.put(id,bmp);
                    if(!updateView) return;
                    Contact contact = new Contact(bmp,constraintLayout,id);
                    Message message = new Message();
                    message.obj = contact;
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout constraintLayout1,constraintLayout2,constraintLayout3,constraintLayout4;
        final ConstraintLayout[] constraintLayoutsGroup;
        final LinearLayout linearLayout;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.recyclerview_list_item);
            constraintLayoutsGroup = new ConstraintLayout[4];
            constraintLayout1 = itemView.findViewById(R.id.layoutView1);
            constraintLayoutsGroup[0] = constraintLayout1;

            constraintLayout2 = itemView.findViewById(R.id.layoutView2);
            constraintLayoutsGroup[1] = constraintLayout2;

            constraintLayout3 = itemView.findViewById(R.id.layoutView3);
            constraintLayoutsGroup[2] = constraintLayout3;

            constraintLayout4 = itemView.findViewById(R.id.layoutView4);
            constraintLayoutsGroup[3] = constraintLayout4;

            if(modifyWidth != 0) {
                constraintLayout1.setLayoutParams(new LinearLayout.LayoutParams(modifyWidth, modifyWidth));
                constraintLayout2.setLayoutParams(new LinearLayout.LayoutParams(modifyWidth, modifyWidth));
                constraintLayout3.setLayoutParams(new LinearLayout.LayoutParams(modifyWidth, modifyWidth));
                constraintLayout4.setLayoutParams(new LinearLayout.LayoutParams(modifyWidth, modifyWidth));
            }else {
                monitorViewChange(itemView);
            }

        }
        void monitorViewChange(final View itemView) {
            //If view change,we can get correct width and set height with width value
            final ViewTreeObserver observer = constraintLayout1.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = constraintLayout1.getWidth();
                    modifyWidth = width;
                    String id = ((TextView)constraintLayout1.findViewById(R.id.id)).getText().toString();
                    Log.i(TAG,"get id = "+id+"width = "+width+" itemView = "+itemView);
                    //modify height as same as width
                    constraintLayout1.setLayoutParams(new LinearLayout.LayoutParams(width,width));
                    constraintLayout2.setLayoutParams(new LinearLayout.LayoutParams(width,width));
                    constraintLayout3.setLayoutParams(new LinearLayout.LayoutParams(width,width));
                    constraintLayout4.setLayoutParams(new LinearLayout.LayoutParams(width,width));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        constraintLayout1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        constraintLayout1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
    }


    public void clearBackGroundWorkingThread() {
        service.cleanAllRequest();
    }

    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if(msg != null && msg.obj != null) {
                ConstraintLayout constraintLayout = ((Contact)msg.obj).constraintLayout;
                int id = ((Contact)msg.obj).id;
                int checkId = Integer.parseInt(((TextView)(constraintLayout.findViewById(R.id.id))).getText().toString());
                if(checkId != id) {
                    Log.i(TAG,"id not matched !!!!! id = "+id+" checkId = "+checkId);
                    return;
                }
                Bitmap bitmap = ((Contact) msg.obj).bitmap;
                //Log.i(TAG,"transfer id = "+id+" constraintLayout = "+constraintLayout);
                //Log.i(TAG,"transfer id = "+id+" bitmap = "+bitmap);
                Drawable drawable = new BitmapDrawable(bitmap);
                constraintLayout.setBackgroundDrawable(drawable);
            }
        }
    }

    static class Contact implements Parcelable {
        private Bitmap bitmap;
        private ConstraintLayout constraintLayout;
        private int id;

        private Contact() {

        }
        public Contact(Bitmap bmp, ConstraintLayout constraintLayout, int id) {
            this.bitmap = bmp;
            this.constraintLayout = constraintLayout;
            this.id = id;
        }

        public static final Parcelable.Creator<Contact> CREATOR = new Creator<Contact>() {
            @Override
            public Contact createFromParcel(Parcel source) {
                Contact contact = new Contact();
                contact.readFromParcel(source);
                return contact;
            }

            @Override
            public Contact[] newArray(int i) {
                return new Contact[0];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            if (bitmap != null) {
                try {
                    Parcelable p = (Parcelable)bitmap;
                    parcel.writeInt(1);
                    parcel.writeParcelable(p,flags);

                } catch (ClassCastException e) {
                    throw new RuntimeException(
                            "Can't marshal non-Parcelable objects across processes.");
                }
            } else {
                parcel.writeInt(0);
            }
            if (constraintLayout != null) {
                try {
                    Parcelable p = (Parcelable)constraintLayout;
                    parcel.writeInt(1);
                    parcel.writeParcelable(p,flags);

                } catch (ClassCastException e) {
                    throw new RuntimeException(
                            "Can't marshal non-Parcelable objects across processes.");
                }
            } else {
                parcel.writeInt(0);
            }
            parcel.writeInt(id);
        }

        private void readFromParcel(Parcel source) {
            if (source.readInt() != 0) {
                bitmap = source.readParcelable(getClass().getClassLoader());
            }
            if (source.readInt() != 0) {
                constraintLayout = source.readParcelable(getClass().getClassLoader());
            }
            id = source.readInt();
        }

    }

}
