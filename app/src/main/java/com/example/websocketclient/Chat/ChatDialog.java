package com.example.websocketclient.Chat;

import static ua.naiksoftware.stomp.provider.OkHttpConnectionProvider.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.websocketclient.DB.SqLiteDatabase;
import com.example.websocketclient.Entity.MsgEntity;
import com.example.websocketclient.PhotoRealPath;
import com.example.websocketclient.R;
import com.example.websocketclient.WebSocketsConnectLocal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import ua.naiksoftware.stomp.StompClient;

public class ChatDialog extends AppCompatActivity {
    private String userName = "";
    private int chatUserTabNum;
    private SqLiteDatabase sqlLiteDatabase = new SqLiteDatabase(this);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final String[] str = new String[1];

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog);

        ///Устанавливаем title для активити и получаем данные для чата
        try {
            Bundle arguments = getIntent().getExtras();
            userName = arguments.get("userName").toString();
            chatUserTabNum = Integer.parseInt(String.valueOf(arguments.get("chatUserTabNum")));
            getSupportActionBar().setTitle("Чат с польз: "+userName);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow);// set drawable icon
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.d(TAG,"Error ChatDialog: "+ e.getMessage());
        }
        Button btnSendMsg = findViewById(R.id.btnSendMsg);
        Button btnPlus = findViewById(R.id.btnPlus);
        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(this).execute();
        updateLL();

        try{
            mStompClient.get().topic("/user/" + getPass() + "/queue/updates").subscribe(topicMessage -> {
                str[0] = topicMessage.getPayload();
                JSONObject jsonObject = new JSONObject(str[0]);

                JSONObject student = new JSONObject();

                student.put("Date", jsonObject.getString("Date"));
                student.put("state", "true");

                mStompClient.get().send("/spring-security-mvc-socket/isSend", String.valueOf(student)).subscribe();

                sqlLiteDatabase.open(this);
                sqlLiteDatabase.insertMSg(new MsgEntity(jsonObject.getString("userTO"), Integer.parseInt(jsonObject.getString("userTO")), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), jsonObject.getString("msg"), 0, jsonObject.getString("Date")));
                sqlLiteDatabase.close();
                updateLL();
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mStompClient.get().send("/spring-security-mvc-socket/SendMsg", String.valueOf(textSend())).subscribe();

                    JSONObject jsonObject = textSend();
                    EditText etSendMsg = findViewById(R.id.etSendMsg);

                    sqlLiteDatabase.open(ChatDialog.this);
                    String finalUserTo=getPass();
                    sqlLiteDatabase.insertMSg(new MsgEntity(finalUserTo, Integer.parseInt(finalUserTo), userName, chatUserTabNum, jsonObject.getString("msg"), 0,jsonObject.getString("Date")));
                    sqlLiteDatabase.close();

                    updateLL();
                    etSendMsg.getText().clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Открытие галереии
        btnPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setType("image/*");
                pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//Выбор несколько фото (разрешение ставить более 1 чекпоинта)
                //pickPhoto.setAction(Intent.ACTION_GET_CONTENT);//установка стандарта выбора фото из ФМ
                startActivityForResult(Intent.createChooser(pickPhoto, "Выбор фото"), 1);//one can be replaced with any action code
            }
        });

    }

    //получение списка с uri путями
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(this).execute();

        switch (requestCode) {
            case 1:
            case 0:
                if (resultCode == RESULT_OK) {
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            try{
                                Log.d(TAG, "Image "+data.getClipData().getItemAt(i).getUri());
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                PhotoRealPath realPath = new PhotoRealPath(this);

                                JSONObject jsonObject = textSend();
                                sqlLiteDatabase.open(this);

                                String path = realPath.getRealPathFromUri(this, imageUri);
                                Bitmap bm = BitmapFactory.decodeFile(path);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                                byte[] b = baos.toByteArray();
                                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                                sqlLiteDatabase.insertMSg(new MsgEntity(getPass(), Integer.parseInt(getPass()), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), "img@"+realPath.getRealPathFromUri(this, imageUri), 0,jsonObject.getString("Date")));
//                                sqlLiteDatabase.insertMSg(new MsgEntity(getPass(), Integer.parseInt(getPass()), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), "img$"+encodedImage, 0,jsonObject.getString("Date")));
                                sqlLiteDatabase.close();
                                updateLL();
                                JSONObject student = new JSONObject();

                                student.put("userTO", getPass());
                                student.put("userFrom", userName);
                                student.put("msg", "img$"+encodedImage);
                                student.put("Date", jsonObject.getString("Date"));
                                mStompClient.get().send("/spring-security-mvc-socket/SendMsg", String.valueOf(student)).subscribe();

                                updateLL();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
        }
    }



    private void inboxMg(){

        try {
            int id = 1;
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT * FROM MSG WhERE TabTo="+getPass() + " OR TabFrom="+getPass();
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

                    String msg =cursor.getString(5);

                    if(msg.contains("img@")){
//
                        ImageView tv1 = new ImageView(this);
                        tv1.setLayoutParams(new LinearLayout.LayoutParams(400,400));

                        tv1.setImageURI(Uri.parse(msg.replace("img@","")));
                        cw.addView(tv1);
                        //////////////////////////////////////

                    }else if(msg.contains("img$")) {
//
                        byte[] decodedString = Base64.decode(msg.replace("img$", ""), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ImageView tv1 = new ImageView(this);
                        tv1.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
                        tv1.setImageBitmap(decodedByte);
                        cw.addView(tv1);
                    }else {
                        TextView tv = new TextView(this);
                        tv.setLayoutParams(llp);
                        tv.setTextSize(20);
                        tv.setTextColor(Color.parseColor("#E1E2E5"));
                        tv.setText(cursor.getString(5));
                        cw.setBackgroundResource(R.drawable.layout_bg_gray);
                        cw.addView(tv);

                    }

                    if(cursor.getString(2).equals(getPass())){
                        cw.setBackgroundResource(R.drawable.layout_bg_blue);
                        layoutParams.gravity= Gravity.RIGHT;
                    }
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


    public void updateLL(){
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
    }

    private JSONObject textSend() {
        try {
            EditText etSendMsg = findViewById(R.id.etSendMsg);
            JSONObject student = new JSONObject();

            student.put("userTO", getPass());
            student.put("userFrom", userName);
            student.put("msg", etSendMsg.getText());
            student.put("Date", dateFormat.format(new Date()));

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                Intent intent = new Intent(getApplication(), ChatNewDialog.class);
                intent.putExtra("inputPage", "chat");
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}