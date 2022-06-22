package com.example.websocketclient;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import ua.naiksoftware.stomp.StompClient;

public class FindUser extends AppCompatActivity {

    private final String userTo = "13";
    private final String[] str = new String[1];

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(this).execute();
        try {

            Button btnSendMsg = findViewById(R.id.btnSendMsg);

            mStompClient.get().topic("/user/" + userTo + "/queue/updates").subscribe(topicMessage -> {
                str[0] = topicMessage.getPayload();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FindUser.this, "MSG " + str[0], Toast.LENGTH_LONG).show();

                    }
                });

            });

            btnSendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mStompClient.get().send("/spring-security-mvc-socket/SendMsg", String.valueOf(textSend())).subscribe();
                        EditText userFrom = findViewById(R.id.userFrom);
                        EditText etSendMsg = findViewById(R.id.etSendMsg);
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