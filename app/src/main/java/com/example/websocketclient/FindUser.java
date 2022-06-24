package com.example.websocketclient;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.websocketclient.DB.SqLiteDatabase;
import com.example.websocketclient.Entity.MsgEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
            Button btnPlus = findViewById(R.id.btnPlus);

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

            //Открытие галереии
            btnPlus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //Camera
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(Intent.createChooser(takePicture, "Select Picture"), 0);//zero can be replaced with any action code
                    ///Photo
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhoto.setType("image/*");
                    pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//Выбор несколько фото (разрешение ставить более 1 чекпоинта)
                    //pickPhoto.setAction(Intent.ACTION_GET_CONTENT);//установка стандарта выбора фото из ФМ
                    startActivityForResult(Intent.createChooser(pickPhoto, "Выбор фото"), 1);//one can be replaced with any action code
                }
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

    //получение списка с uri путями
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(this).execute();

        List<String> listURI = new ArrayList<>();

        switch (requestCode) {
            case 1:
            case 0:
                if (resultCode == RESULT_OK) {
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            PhotoRealPath realPath = new PhotoRealPath(this);
                            String path = realPath.getRealPathFromUri(this, imageUri);
                            Bitmap bm = BitmapFactory.decodeFile(path);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                            byte[] b = baos.toByteArray();
                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

//                            //////////////////////////////////////
//                            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
//                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                            ImageView tv1 = (ImageView) findViewById(R.id.iv);
//                            tv1.setImageBitmap(decodedByte);
//                            //////////////////////////////////////


                            try{
                                EditText userFrom = findViewById(R.id.userFrom);
                                JSONObject student = new JSONObject();

                                student.put("userTO", getPass());
                                student.put("userFrom", userFrom.getText());
                                student.put("msg", "img@"+encodedImage);


                                mStompClient.get().send("/spring-security-mvc-socket/SendMsg", String.valueOf(student)).subscribe();
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
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

                    String msg =cursor.getString(5);

                    if(msg.contains("img@")){
                        //////////////////////////////////////
                        byte[] decodedString = Base64.decode(msg.replace("img@",""), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ImageView tv1 = (ImageView) findViewById(R.id.iv);
                        tv1.setImageBitmap(decodedByte);
                        cw.addView(tv1);
                        //////////////////////////////////////

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
                        layoutParams.gravity=Gravity.RIGHT;
                    }

//                    cw.addView(tv);

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