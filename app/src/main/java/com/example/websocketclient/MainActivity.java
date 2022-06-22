package com.example.websocketclient;

import static android.content.ContentValues.TAG;

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

import org.json.JSONException;
import org.json.JSONObject;

import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///////////////////////////////////////////////////////////////////

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
                if (str[0].equals("false")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Вы ввели не коректные данные", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Intent myIntent = new Intent(MainActivity.this, FindUser.class);
                    startActivity(myIntent);
                }
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


