package com.example.websocketclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {

    private StompClient mStompClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///////////////////////////////////////////////////////////////////
        Button btnSend = findViewById(R.id.btnSend);
        Button btnConnection = findViewById(R.id.btnConnection);
        EditText etText = findViewById(R.id.etText);

//        new WebSocketsConnect(MainActivity.this).execute();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WebSocketsConnect(MainActivity.this).execute(textSend());
                etText.getText().clear();
            }
        });


        ///////////////////////////////////////////////////////////////////
    }

    public String textSend(){
        EditText etText = findViewById(R.id.etText);
        return String.valueOf(etText.getText());
    }


    private JSONObject testJson(){
        JSONObject student1 = new JSONObject();
        try {
            student1.put("name", "nameS");
            student1.put("msg", "Msg ");

            JSONArray jsonArray = new JSONArray();

            jsonArray.put(student1);

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("test", jsonArray);

            return jsonObject;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return student1;
    }
}