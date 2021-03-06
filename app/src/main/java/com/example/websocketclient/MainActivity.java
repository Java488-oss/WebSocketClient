package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.CursorWindow;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.websocketclient.Chat.ChatNewDialog;
import com.example.websocketclient.DB.SqLiteDatabase;
import com.example.websocketclient.Entity.UserEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

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
        EditText etText = findViewById(R.id.etText);
        EditText etName = findViewById(R.id.etName);

        File fileDir = new File(getRootOfExternalStorage(2)+"/Photo");
        if(!fileDir.exists()){
            fileDir.mkdir();
        }

        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(MainActivity.this).execute();
        try {

            final String[] str = new String[1];

            mStompClient.get().topic("/spring-security-mvc-socket/Login").subscribe();

            String id = mStompClient.get().getTopicId("/spring-security-mvc-socket/Login");

            mStompClient.get().topic("/user/" + id + "/queue/Login").subscribe(topicMessage -> {
                str[0] = topicMessage.getPayload();
                JSONObject jsonObject = new JSONObject(str[0]);

                if (jsonObject.getString("result").equals("false")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "???? ?????????? ???? ?????????????????? ????????????", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    //////////////
                    // ???????????????????? ???????????? ?? ???????????????????????? ?? ????
                    sqlLiteDatabase.open(this);
                    String updateQuery = "UPDATE User SET UserLogin = " + jsonObject.getString("user") + ", UserPassword = " + jsonObject.getString("pass") + " WHERE UserID = 1";
                    sqlLiteDatabase.database.execSQL(updateQuery);
                    sqlLiteDatabase.close();
                    //////////////

                    Intent myIntent = new Intent(MainActivity.this, ChatNewDialog.class);
                    startActivity(myIntent);
                    mStompClient.get().disconnect();
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

    //////////////////////////////////////
    // ?????????????????? ?????????????????? ??????????
    public String getRootOfExternalStorage(int i) {
        File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(this, null);
        switch (i) {
            case 1:
                for (File file : externalStorageFiles) {
                    // ?????????????????? ???????????????????? ???????? /storage/emulated/0
                    return file.getAbsolutePath().replaceAll("/Android/data/" + getPackageName() + "/files", "");
                }
            case 2:
                for (File file : externalStorageFiles) {
                    // ?????????????????? ?????????????? ???????? ????????????????????  /storage/emulated/0/Android/data/app/files
                    return file.getAbsolutePath();
                }
        }
        return null;
    }

    ////////////////////////////////
}


