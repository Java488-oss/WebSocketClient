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

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {


    String addr = "";

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


            mStompClient.get().topic("/user/11/queue/updates").subscribe(topicMessage -> {
                Log.d(TAG, "Stomp MSG " + topicMessage.getPayload());
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
                        mStompClient.get().send("/spring-security-mvc-socket/hello-msg-mapping", String.valueOf(textSend())).subscribe();
                        addr = Objects.requireNonNull(textSend()).getString("pass");
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

    private JSONObject textSend() {
        try {
            EditText etText = findViewById(R.id.etText);
            EditText etName = findViewById(R.id.etName);
            JSONObject student1 = new JSONObject();

            student1.put("user", etName.getText());
            student1.put("pass", etText.getText());

            return student1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}


