package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketsConnect extends AsyncTask<String, Void, String> {

    private Context context;

    public WebSocketsConnect(Context context) {
        this.context = context;
    }

    private StompClient mStompClient;

    private MainActivity mainActivity;
    private int i=0;

    @SuppressLint("CheckResult")
    @Override
    protected String doInBackground(String... voids) {

        try{
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.1.9:8050/example-endpoint/websocket");
            mStompClient.connect();

            String token =null;
            token= mStompClient.getTopicId("/topic/greetings");
            if(token!=null) {

                //получение сообщения с сервера на клиет
                mStompClient.topic("/topic/greetings").subscribe(topicMessage -> {
                    onPostExecute(topicMessage.getPayload());
//                            Log.d(TAG, "json4 "+mStompClient.getTopicId("/topic/greetings"));
                });

                Log.d(TAG, "json token " + mStompClient.getTopicId("/topic/greetings"));


                mStompClient.send("/topic/hello-msg-mapping", voids[0]).subscribe();
            }


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
                Log.d(TAG, "json error "+e.getMessage());
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);

            mainActivity = (MainActivity) context;
            TextView textView = (TextView)mainActivity.findViewById(R.id.textView);
            textView.setText(""+aBoolean);
            Log.d(TAG, "" +
                    " result "+aBoolean);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
