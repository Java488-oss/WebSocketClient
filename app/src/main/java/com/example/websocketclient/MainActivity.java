package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {

//    private StompClient mStompClient;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///////////////////////////////////////////////////////////////////

        Button btnSend = findViewById(R.id.btnSend);

        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(MainActivity.this).execute();

        try {
            mStompClient.get().topic("/topic/greetings").subscribe();

            final String[] str = new String[1];
            //получение сообщения с сервера на клиет
            mStompClient.get().topic("/topic/greetings").subscribe(topicMessage -> {
                str[0] =topicMessage.getPayload();
                textOut(str[0]);
            });


        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mStompClient.get().send("/topic/hello-msg-mapping", String.valueOf(textSend())).subscribe();
                    EditText etText = findViewById(R.id.etText);
                    EditText etName = findViewById(R.id.etName);
                    etText.getText().clear();
                    etName.getText().clear();
                    Log.d("Sergey", "return AsyncTask token 1 " + mStompClient.get().getTopicId("/topic/greetings"));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ///////////////////////////////////////////////////////////////////
    }

//    public String textSend(){
//        EditText etText = findViewById(R.id.etText);
//        EditText etName = findViewById(R.id.etName);
//        return String.valueOf(etText.getText());
//    }

    public void textOut(String text){
        TextView textView = findViewById(R.id.textView);
        textView.setText(text);
        Log.d("Sergey", "return AsyncTask token 2 " + text);
    }


    private JSONObject textSend(){
        EditText etText = findViewById(R.id.etText);
        EditText etName = findViewById(R.id.etName);
        JSONObject student1 = new JSONObject();
        try {
            student1.put("name", etName.getText());
            student1.put("msg", etText.getText());

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


