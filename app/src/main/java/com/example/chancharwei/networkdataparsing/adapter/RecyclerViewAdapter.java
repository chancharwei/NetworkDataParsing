package com.example.chancharwei.networkdataparsing.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chancharwei.networkdataparsing.MainActivity;
import com.example.chancharwei.networkdataparsing.R;
import com.example.chancharwei.networkdataparsing.fragments.NetworkFragment;
import com.example.chancharwei.networkdataparsing.networkInfo.NetworkData;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.LogRecord;

import retrofit2.http.HTTP;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName()+"[ByronLog]";
    private List<NetworkData[]> mNetworkData;
    private Context mContext;
    private Fragment mFragment;
    private Myhandler mHandler;

    public RecyclerViewAdapter(Context context, Fragment fragment) {
        mContext = context;
        mFragment = fragment;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder");
        int layoutIdForListItem = R.layout.recyclerview_list_item;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Log.i(TAG, "onBindVIewHolder ("+ position+")");
        NetworkData[] eachData = mNetworkData.get(position);
        for(int i=0; i<holder.constraintLayoutsGroup.length;i++) {
            TextView textViewID = (TextView)holder.constraintLayoutsGroup[i].findViewById(R.id.id);
            TextView textViewTitle = (TextView)holder.constraintLayoutsGroup[i].findViewById(R.id.title);
            textViewID.setText(Integer.toString(eachData[i].getId()));
            textViewTitle.setText(eachData[i].getTitle());
            setBackGroundColor(holder.constraintLayoutsGroup[i],eachData[i].getId(),eachData[i].getThumbnailUrl());
        }
    }

    @Override
    public int getItemCount() {
        if(mNetworkData != null) {
            Log.i(TAG,"data size "+mNetworkData.size());
            return mNetworkData.size();
        }else {
            return 0;
        }
    }

    public void setData(List<NetworkData[]> networkData) {
        Log.i(TAG,"setData data size "+networkData.size());
        mNetworkData = networkData;
    }

    public void setBackGroundColor(ConstraintLayout constraintLayout,int id,String url) {
        try {
            downloadImage(id,constraintLayout,new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mHandler = new Myhandler();

    }

    private void downloadImage(final int id,final ConstraintLayout constraintLayout,final URL imageURL) {

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
                    Contact contact = new Contact(bmp,constraintLayout,id);
                    //Log.i(TAG,"id = "+id+" constraintLayout = "+constraintLayout);
                    //Log.i(TAG,"id = "+id+" bitmap = "+bmp);
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

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayoutsGroup = new ConstraintLayout[4];
            constraintLayout1 = itemView.findViewById(R.id.layoutView1);
            constraintLayoutsGroup[0] = constraintLayout1;

            constraintLayout2 = itemView.findViewById(R.id.layoutView2);
            constraintLayoutsGroup[1] = constraintLayout2;

            constraintLayout3 = itemView.findViewById(R.id.layoutView3);
            constraintLayoutsGroup[2] = constraintLayout3;

            constraintLayout4 = itemView.findViewById(R.id.layoutView4);
            constraintLayoutsGroup[3] = constraintLayout4;

        }
    }

    static class Myhandler extends Handler {
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
                    parcel.writeParcelable(p, flags);

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
                    parcel.writeParcelable(p, flags);

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
