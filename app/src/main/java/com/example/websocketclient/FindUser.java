package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.websocketclient.DB.SqLiteDatabase;
import com.example.websocketclient.Entity.MsgEntity;

import org.json.JSONException;
import org.json.JSONObject;

import ua.naiksoftware.stomp.StompClient;

public class FindUser extends AppCompatActivity {

    private final String userTo = "1";
    private final String[] str = new String[1];
    private SqLiteDatabase sqlLiteDatabase = new SqLiteDatabase(this);


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(this).execute();
        try {


            //////////////
            // Получаем пароль пользователя из бд
            sqlLiteDatabase.open(this);
            String select = "SELECT UserPassword FROM USER WHERE UserID = 1";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(select, null);
            String userTo = "";
            if (cursor.moveToFirst()) {
                do {
                    userTo = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
            //////////////


            Log.d(TAG, "Stomp Log in " + userTo);

            Button btnSendMsg = findViewById(R.id.btnSendMsg);

            mStompClient.get().topic("/user/" + userTo + "/queue/updates").subscribe(topicMessage -> {
                str[0] = topicMessage.getPayload();

                Log.d(TAG, "Stomp json to server " + str[0]);

                JSONObject jsonObject = new JSONObject(str[0]);
                sqlLiteDatabase.open(FindUser.this);
                sqlLiteDatabase.insertMSg(new MsgEntity(jsonObject.getString("userTO"), Integer.parseInt(jsonObject.getString("userTO")), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), jsonObject.getString("msg"), 0));
                sqlLiteDatabase.close();

            });

            String finalUserTo = userTo;
            btnSendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mStompClient.get().send("/spring-security-mvc-socket/SendMsg", String.valueOf(textSend())).subscribe();
                        EditText userFrom = findViewById(R.id.userFrom);
                        EditText etSendMsg = findViewById(R.id.etSendMsg);

                        sqlLiteDatabase.open(FindUser.this);
                        sqlLiteDatabase.insertMSg(new MsgEntity(finalUserTo, Integer.parseInt(finalUserTo), String.valueOf(userFrom.getText()), Integer.parseInt(String.valueOf(userFrom.getText())), String.valueOf(etSendMsg.getText()), 0));
                        sqlLiteDatabase.close();

                        userFrom.getText().clear();
                        etSendMsg.getText().clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject textSend() {
        try {
            EditText userFrom = findViewById(R.id.userFrom);
            EditText etSendMsg = findViewById(R.id.etSendMsg);
            JSONObject student = new JSONObject();
            //////////////
            // Получаем пароль пользователя из бд
            sqlLiteDatabase.open(this);
            String select = "SELECT UserPassword FROM USER WHERE UserID = 1";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(select, null);
            String userTo = "";
            if (cursor.moveToFirst()) {
                do {
                    userTo = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
            //////////////

            student.put("userTO", userTo);
            student.put("userFrom", userFrom.getText());
            student.put("msg", etSendMsg.getText());

            return student;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}