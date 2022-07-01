package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.example.websocketclient.DB.SqLiteDatabase;
import com.example.websocketclient.Entity.MsgEntity;
import com.example.websocketclient.Entity.UserEntity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketsConnectLocal extends AsyncTask<Void, Void, StompClient> {

    private Context context;

    public WebSocketsConnectLocal(Context context) {
        this.context = context;
    }

    private StompClient mStompClient;

    private SqLiteDatabase sqlLiteDatabase = new SqLiteDatabase(context);

    private final String[] str = new String[1];

    @SuppressLint("CheckResult")
    @Override
    protected StompClient doInBackground(Void... voids) {

        try {
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.164.126:8050/room/websocket");
//            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.164.5:8050/room/websocket");
//            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.1.9:8050/room/websocket");
            mStompClient.connect();
            mStompClient.send("/spring-security-mvc-socket/GetUser", getPass()).subscribe();
            //Получение списка пользователей для чата из внешней бд
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    sqlLiteDatabase.open(context);
                    try{

                        mStompClient.topic("/user/" + getPass() + "/queue/state").subscribe(topicMessage -> {
                            str[0] = topicMessage.getPayload();

                            JSONObject student = new JSONObject();

                            student.put("pass", getPass());
                            student.put("state", "true");

                            mStompClient.send("/spring-security-mvc-socket/isOnline", String.valueOf(student)).subscribe();
                            Log.d(TAG, "Timer to server "+str[0]);
                        });

                        mStompClient.topic("/user/" + getPass() + "/queue/offline").subscribe(topicMessage -> {
                            str[0] = topicMessage.getPayload();
                            JSONObject jsonObject = new JSONObject(str[0]);

                            JSONObject student = new JSONObject();

                            student.put("Date", jsonObject.getString("Date"));
                            student.put("state", "true");

                            if(jsonObject.getString("msg").equals("img$")){
                                byte[] decodedString = Base64.decode(jsonObject.getString("msg").replace("img$", ""), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                File f = new File("/storage/emulated/0", "test.jpg");
                                f.createNewFile();
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                decodedByte.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                                byte[] bitmapdata = bos.toByteArray();

                                FileOutputStream fos = new FileOutputStream(f);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();
                            }


                            mStompClient.send("/spring-security-mvc-socket/isSend", String.valueOf(student)).subscribe();

                            sqlLiteDatabase.open(context);
                            sqlLiteDatabase.insertMSg(new MsgEntity(jsonObject.getString("userTO"), Integer.parseInt(jsonObject.getString("userTO")), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), jsonObject.getString("msg"), 0, jsonObject.getString("Date")));
                            sqlLiteDatabase.close();
                        });

//                            mStompClient.topic("/user/" + getPass() + "/queue/updates").subscribe(topicMessage -> {
//                                str[0] = topicMessage.getPayload();
//                                JSONObject jsonObject = new JSONObject(str[0]);
//
//                                JSONObject student = new JSONObject();
//
//                                student.put("Date", jsonObject.getString("Date"));
//                                student.put("state", "true");
//
//                                mStompClient.send("/spring-security-mvc-socket/isSend", String.valueOf(student)).subscribe();
//
//                                sqlLiteDatabase.open(context);
//                                sqlLiteDatabase.insertMSg(new MsgEntity(jsonObject.getString("userTO"), Integer.parseInt(jsonObject.getString("userTO")), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), jsonObject.getString("msg"), 0, jsonObject.getString("Date")));
//                                sqlLiteDatabase.close();
//                            });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return mStompClient;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getPass(){
        //////////////
        // Получаем пароль пользователя из бд
        sqlLiteDatabase.open(context);
        String select = "SELECT UserPassword FROM User WHERE UserID = 1";
        Cursor cursor = sqlLiteDatabase.database.rawQuery(select, null);
        String userTo = "";
        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(0);
            } while (cursor.moveToNext());
        }
        sqlLiteDatabase.close();
        //////////////
        return "";
    }



}
