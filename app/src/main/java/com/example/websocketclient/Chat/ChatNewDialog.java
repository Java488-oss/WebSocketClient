package com.example.websocketclient.Chat;

import static ua.naiksoftware.stomp.provider.OkHttpConnectionProvider.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.websocketclient.DB.SqLiteDatabase;
import com.example.websocketclient.R;

public class ChatNewDialog extends AppCompatActivity {

    private SqLiteDatabase sqlLiteDatabase = new SqLiteDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new_dialog);
        getSupportActionBar().setTitle("Новый диалог");

        LinearLayout rContainer = (LinearLayout) findViewById(R.id.LLContCND);


        try {
            String selectQuery;

            sqlLiteDatabase.open(this);
            selectQuery = "SELECT DISTINCT UserLogin, UserPassword From User WHERE NOT like(UserPassword,'"+getPass()+"');";

            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int chatUserTabNum = cursor.getInt(1);

                    String chatUserName = cursor.getString(0);

                    final CardView cw = new CardView(this);
                    LinearLayout linearLayout2 = new LinearLayout(this);
                    cw.setCardBackgroundColor(Color.parseColor("#444446"));

                    cw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ChatDialog.class);
                            intent.putExtra("userName", chatUserName);
                            intent.putExtra("chatUserTabNum", chatUserTabNum);
                            finish();
                            startActivity(intent);
                        }
                    });

                    LinearLayout ll = new LinearLayout(this);

                    LinearLayout.LayoutParams layoutParamsTV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);

                    linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout2.setLayoutParams(layoutParams1);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(20,20,20,20);

                    layoutParams.setMargins(0, 10, 0, 0);

                    TextView tvName = new TextView(this);
                    tvName.setText(Html.fromHtml("<font color='#E1E2E5'> " + chatUserName + "</font>"), TextView.BufferType.SPANNABLE);
                    tvName.setTextSize(21);
                    tvName.setLayoutParams(layoutParamsTV);
                    linearLayout2.addView(tvName);

                    ll.addView(linearLayout2 );

                    cw.addView(ll);

                    rContainer.addView(cw, layoutParams);

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d(TAG,"Error ChatNewDialog: "+e.getMessage());
        }

    }

    private String getPass(){
        //////////////
        // Получаем пароль пользователя из бд
        sqlLiteDatabase.open(this);
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