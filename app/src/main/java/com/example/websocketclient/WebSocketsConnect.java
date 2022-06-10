package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketsConnect extends AsyncTask<String, Void, Boolean> {

    private Context context;

    public WebSocketsConnect(Context context) {
        this.context = context;
    }

    private StompClient mStompClient;

    @SuppressLint("CheckResult")
    @Override
    protected Boolean doInBackground(String... voids) {

        try{
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.164.5:8050/example-endpoint/websocket");
            mStompClient.connect();


            //получение сообщения с сервера на клиет
            mStompClient.topic("/topic/greetings").subscribe(topicMessage -> {
                Log.d(TAG, "json "+topicMessage.getPayload());
            });


            String str = voids[0];

            mStompClient.send("/topic/hello-msg-mapping", str).subscribe();


            mStompClient.lifecycle().subscribe(lifecycleEvent -> {
                switch (lifecycleEvent.getType()) {

                    case OPENED:
                        Log.d(TAG, "Stomp connection opened123");
                        break;

                    case ERROR:
                        Log.e(TAG, "Stomp Error", lifecycleEvent.getException());
                        break;

                    case CLOSED:
                        Log.d(TAG, "Stomp connection closed");
                        break;
                }
            });

//            mStompClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
