package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.websocketclient.DB.SqLiteDatabase;
import com.example.websocketclient.Entity.UserEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {

    private SqLiteDatabase sqlLiteDatabase = new SqLiteDatabase(this);
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        ///////////////////////////////////////////////////////////////////

        try {
            sqlLiteDatabase.create_db(this);
        } catch (IOException e) {
        }

        Button btnSend = findViewById(R.id.btnSend);
        Button btnRegister = findViewById(R.id.btnRegister);
        EditText etText = findViewById(R.id.etText);
        EditText etName = findViewById(R.id.etName);

        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(MainActivity.this).execute();
        try {

            final String[] str = new String[1];

            mStompClient.get().topic("/spring-security-mvc-socket/Login").subscribe();

            String id = mStompClient.get().getTopicId("/spring-security-mvc-socket/Login");

            mStompClient.get().topic("/user/" + id + "/queue/updates").subscribe(topicMessage -> {
                str[0] = topicMessage.getPayload();
                JSONObject jsonObject = new JSONObject(str[0]);

                if (jsonObject.getString("result").equals("false")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Вы ввели не коректные данные", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    //////////////
                    // Записываем данные о пользователе в бд
                    sqlLiteDatabase.open(this);
                    String updateQuery = "UPDATE User SET UserLogin = " + jsonObject.getString("user") + ", UserPassword = " + jsonObject.getString("pass") + " WHERE UserID = 1";
                    sqlLiteDatabase.database.execSQL(updateQuery);
                    sqlLiteDatabase.close();
                    //////////////


                    Intent myIntent = new Intent(MainActivity.this, FindUser.class);
                    startActivity(myIntent);
                }
            });


            mStompClient.get().topic("/user/13/queue/state").subscribe(topicMessage -> {
                str[0] = topicMessage.getPayload();

                Log.d(TAG, "Stomp json " + str[0]);
            });

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mStompClient.get().send("/spring-security-mvc-socket/Login", String.valueOf(textSend(id))).subscribe();
                        etName.getText().clear();
                        etText.getText().clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        ///////////////////////////////////////////////////////////////////
    }

    private JSONObject textSend(String id) {
        try {
            EditText etText = findViewById(R.id.etText);
            EditText etName = findViewById(R.id.etName);
            JSONObject student = new JSONObject();
            student.put("user", etName.getText());
            student.put("pass", etText.getText());
            student.put("id", id);

            return student;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}


