package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketsConnectLocal extends AsyncTask<Void, Void, StompClient> {

    private Context context;

    public WebSocketsConnectLocal(Context context) {
        this.context = context;
    }

    private StompClient mStompClient;

    @SuppressLint("CheckResult")
    @Override
    protected StompClient doInBackground(Void... voids) {
        try {
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.168.8:8050/room/websocket");
            mStompClient.connect();
//            mStompClient.topic("/test"+"-User"+mStompClient.getTopicId("/spring-security-mvc-socket/hello-msg-mapping")).subscribe();

//            Log.d(TAG, "Stomp "+mStompClient.getTopicId("/spring-security-mvc-socket/hello-msg-mapping"));
            return mStompClient;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
