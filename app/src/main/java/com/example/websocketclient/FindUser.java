package com.example.websocketclient;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.websocketclient.DB.SqLiteDatabase;
import com.example.websocketclient.Entity.MsgEntity;

import org.json.JSONException;
import org.json.JSONObject;

import ua.naiksoftware.stomp.StompClient;

public class FindUser extends AppCompatActivity {

    private final String[] str = new String[1];
    private SqLiteDatabase sqlLiteDatabase = new SqLiteDatabase(this);


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(this).execute();
        try {

            inboxMg();
            ScrollView svCont = findViewById(R.id.svCont);

            svCont.post(new Runnable() {
                @Override
                public void run() {
                    svCont.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

            Button btnSendMsg = findViewById(R.id.btnSendMsg);

            mStompClient.get().topic("/user/" + getPass() + "/queue/updates").subscribe(topicMessage -> {
                str[0] = topicMessage.getPayload();
                JSONObject jsonObject = new JSONObject(str[0]);
                sqlLiteDatabase.open(FindUser.this);
                sqlLiteDatabase.insertMSg(new MsgEntity(jsonObject.getString("userTO"), Integer.parseInt(jsonObject.getString("userTO")), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), jsonObject.getString("msg"), 0));
                sqlLiteDatabase.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout linearLayout = findViewById(R.id.llCont);
                        linearLayout.removeAllViews();
                        inboxMg();
                        ScrollView svCont = findViewById(R.id.svCont);

                        svCont.post(new Runnable() {
                            @Override
                            public void run() {
                                svCont.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }
                });
            });

            String finalUserTo = getPass();
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

    private void inboxMg(){

        try {
            int id = 1;
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT * FROM MSG WhERE TabTo="+getPass() + " OR TabFrom="+getPass();
            //String selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum;
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            cursor.getCount();
            if (cursor.moveToFirst()) {
                do {
                    final CardView cw = new CardView(this);
                    cw.setId(id);
                    cw.setClipToOutline(true);

                    LinearLayout llCont = findViewById(R.id.llCont);

                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(15, 15, 15, 0);

                    TextView tv = new TextView(this);
                    tv.setLayoutParams(llp);
                    tv.setTextSize(20);
                    tv.setTextColor(Color.parseColor("#E1E2E5"));
                    tv.setText(cursor.getString(5));
                    cw.setBackgroundResource(R.drawable.layout_bg_gray);

                    if(cursor.getString(2).equals(getPass())){
                        cw.setBackgroundResource(R.drawable.layout_bg_blue);
                        layoutParams.gravity=Gravity.RIGHT;
                    }

                    cw.addView(tv);

                    LinearLayout ll1 = new LinearLayout(this);

                    ll1.addView(cw);

                    llCont.addView(ll1, layoutParams);

                } while (cursor.moveToNext());
            }

            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JSONObject textSend() {
        try {
            EditText userFrom = findViewById(R.id.userFrom);
            EditText etSendMsg = findViewById(R.id.etSendMsg);
            JSONObject student = new JSONObject();

            student.put("userTO", getPass());
            student.put("userFrom", userFrom.getText());
            student.put("msg", etSendMsg.getText());

            return student;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPass(){
        //////////////
        // Получаем пароль пользователя из бд
        sqlLiteDatabase.open(this);
        String select = "SELECT UserPassword FROM USER WHERE UserID = 1";
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