//package com.example.websocketclient;
//
//
//import static ua.naiksoftware.stomp.provider.OkHttpConnectionProvider.TAG;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.content.ContextCompat;
//
//import com.example.websocketclient.DB.SqLiteDatabase;
//import com.example.websocketclient.Entity.MsgEntity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.concurrent.ExecutionException;
//
//import ua.naiksoftware.stomp.StompClient;
//
//public class FindUser extends AppCompatActivity {
//
//    private final String[] str = new String[1];
//    private SqLiteDatabase sqlLiteDatabase = new SqLiteDatabase(this);
//
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//
//    @SuppressLint("CheckResult")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_find_user);
//
//        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(this).execute();
//        try {
//
//            inboxMg();
//            ScrollView svCont = findViewById(R.id.svCont);
//
//            svCont.post(new Runnable() {
//                @Override
//                public void run() {
//                    svCont.fullScroll(ScrollView.FOCUS_DOWN);
//                }
//            });
//
//            Button btnSendMsg = findViewById(R.id.btnSendMsg);
//            Button btnPlus = findViewById(R.id.btnPlus);
//
//            mStompClient.get().topic("/user/" + getPass() + "/queue/state").subscribe(topicMessage -> {
//                str[0] = topicMessage.getPayload();
//
//                JSONObject student = new JSONObject();
//
//                student.put("pass", getPass());
//                student.put("state", "true");
//
//                mStompClient.get().send("/spring-security-mvc-socket/isOnline", String.valueOf(student)).subscribe();
//                Log.d(TAG, "Timer to server "+str[0]);
//            });
//
//
//            mStompClient.get().topic("/user/" + getPass() + "/queue/updates").subscribe(topicMessage -> {
//                str[0] = topicMessage.getPayload();
//                JSONObject jsonObject = new JSONObject(str[0]);
//
//                JSONObject student = new JSONObject();
//
//                student.put("Date", jsonObject.getString("Date"));
//                student.put("state", "true");
//
//                mStompClient.get().send("/spring-security-mvc-socket/isSend", String.valueOf(student)).subscribe();
//
//                sqlLiteDatabase.open(FindUser.this);
//                sqlLiteDatabase.insertMSg(new MsgEntity(jsonObject.getString("userTO"), Integer.parseInt(jsonObject.getString("userTO")), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), jsonObject.getString("msg"), 0, jsonObject.getString("Date")));
//                sqlLiteDatabase.close();
//
//                updateLL();
//
//            });
//            //Открытие галереии
//            btnPlus.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//
//                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    pickPhoto.setType("image/*");
//                    pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//Выбор несколько фото (разрешение ставить более 1 чекпоинта)
//                    //pickPhoto.setAction(Intent.ACTION_GET_CONTENT);//установка стандарта выбора фото из ФМ
//                    startActivityForResult(Intent.createChooser(pickPhoto, "Выбор фото"), 1);//one can be replaced with any action code
//                }
//            });
//
//            String finalUserTo = getPass();
//            btnSendMsg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        mStompClient.get().send("/spring-security-mvc-socket/SendMsg", String.valueOf(textSend())).subscribe();
//                        EditText userFrom = findViewById(R.id.userFrom);
//                        EditText etSendMsg = findViewById(R.id.etSendMsg);
//
//                        sqlLiteDatabase.open(FindUser.this);
//                        sqlLiteDatabase.insertMSg(new MsgEntity(finalUserTo, Integer.parseInt(finalUserTo), String.valueOf(userFrom.getText()), Integer.parseInt(String.valueOf(userFrom.getText())), String.valueOf(etSendMsg.getText()), 0,dateFormat.format(new Date())));
//                        sqlLiteDatabase.close();
//
//                        updateLL();
//                        etSendMsg.getText().clear();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    //получение списка с uri путями
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(this).execute();
//
//        switch (requestCode) {
//            case 1:
//            case 0:
//                if (resultCode == RESULT_OK) {
//                    if (data.getClipData() != null) {
//                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
//                            try{
//                                Log.d(TAG, "Image "+data.getClipData().getItemAt(i).getUri());
//                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                                PhotoRealPath realPath = new PhotoRealPath(this);
//
//                                JSONObject jsonObject = textSend();
//                                sqlLiteDatabase.open(FindUser.this);
//                                sqlLiteDatabase.insertMSg(new MsgEntity(getPass(), Integer.parseInt(getPass()), jsonObject.getString("userFrom"), Integer.parseInt(jsonObject.getString("userFrom")), "img$"+realPath.getRealPathFromUri(this, imageUri), 0,jsonObject.getString("Date")));
//                                sqlLiteDatabase.close();
//                                updateLL();
//
//                                String path = realPath.getRealPathFromUri(this, imageUri);
//                                Bitmap bm = BitmapFactory.decodeFile(path);
//                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
//                                byte[] b = baos.toByteArray();
//                                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//
//                                EditText userFrom = findViewById(R.id.userFrom);
//                                JSONObject student = new JSONObject();
//
//                                student.put("userTO", getPass());
//                                student.put("userFrom", userFrom.getText());
//                                student.put("msg", "img@"+encodedImage);
//                                mStompClient.get().send("/spring-security-mvc-socket/SendMsg", String.valueOf(student)).subscribe();
//
//                                updateLL();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                }
//        }
//    }
//
//    private void inboxMg(){
//
//        try {
//            int id = 1;
//            sqlLiteDatabase.open(this);
//            String selectQuery = "SELECT * FROM MSG WhERE TabTo="+getPass() + " OR TabFrom="+getPass();
//            //String selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum;
//            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
//            cursor.getCount();
//            if (cursor.moveToFirst()) {
//                do {
//                    final CardView cw = new CardView(this);
//                    cw.setId(id);
//                    cw.setClipToOutline(true);
//
//                    LinearLayout llCont = findViewById(R.id.llCont);
//
//                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);
//
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    layoutParams.setMargins(15, 15, 15, 0);
//
//                    String msg =cursor.getString(5);
//
//                    if(msg.contains("img$")){
//
//                        ImageView tv1 = new ImageView(this);
//                        tv1.setLayoutParams(new LinearLayout.LayoutParams(400,400));
//
//                        tv1.setImageURI(Uri.parse(msg.replace("img$","")));
//                        cw.addView(tv1);
//                        //////////////////////////////////////
//
//                    }else if(msg.contains("img@")) {
//                        //////////////////////////////////////
//                        byte[] decodedString = Base64.decode(msg.replace("img@", ""), Base64.DEFAULT);
//                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                        ImageView tv1 = new ImageView(this);
//                        tv1.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
//                        tv1.setImageBitmap(decodedByte);
////                        tv1.setImageURI(Uri.parse(f.getAbsolutePath()));
//                        cw.addView(tv1);
//
////                        File f = new File(getRootOfExternalStorage(1), "test.jpg");
////                        f.createNewFile();
////
////                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////                        decodedByte.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
////                        byte[] bitmapdata = bos.toByteArray();
////
////                        FileOutputStream fos = new FileOutputStream(f);
////                        fos.write(bitmapdata);
////                        fos.flush();
////                        fos.close();
//
////                        tv1.setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                Intent intent = new Intent();
////                                intent.setAction(Intent.ACTION_VIEW);
////                                intent.setDataAndType(Uri.parse(f.getAbsolutePath()), "image/jpg");
////                                startActivity(intent);
////                            }
////
////                        });
//                        //////////////////////////////////////
//
//                    }else {
//                        TextView tv = new TextView(this);
//                        tv.setLayoutParams(llp);
//                        tv.setTextSize(20);
//                        tv.setTextColor(Color.parseColor("#E1E2E5"));
//                        tv.setText(cursor.getString(5));
//                        cw.setBackgroundResource(R.drawable.layout_bg_gray);
//                        cw.addView(tv);
//                    }
//
//                    if(cursor.getString(2).equals(getPass())){
//                        cw.setBackgroundResource(R.drawable.layout_bg_blue);
//                        layoutParams.gravity=Gravity.RIGHT;
//                    }
//
//                    LinearLayout ll1 = new LinearLayout(this);
//
//                    ll1.addView(cw);
//
//                    llCont.addView(ll1, layoutParams);
//
//                } while (cursor.moveToNext());
//            }
//
//            sqlLiteDatabase.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private JSONObject textSend() {
//        try {
//            EditText userFrom = findViewById(R.id.userFrom);
//            EditText etSendMsg = findViewById(R.id.etSendMsg);
//            JSONObject student = new JSONObject();
//
//            student.put("userTO", getPass());
//            student.put("userFrom", userFrom.getText());
//            student.put("msg", etSendMsg.getText());
//            student.put("Date", dateFormat.format(new Date()));
//
//            return student;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    //////////////////////////////////////
//    // Получение системных путей
//    public String getRootOfExternalStorage(int i) {
//        File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(this, null);
//        switch (i){
//            case 1:
//                for (File file : externalStorageFiles) {
//                    // получение системного пути /storage/emulated/0
//                    return file.getAbsolutePath().replaceAll("/Android/data/" + getPackageName() + "/files", "");
//                }
//            case 2:
//                for (File file : externalStorageFiles) {
//                    // Получение полного пути приложения  /storage/emulated/0/Android/data/com.example.sportapp/files
//                    return file.getAbsolutePath();
//                }
//        }
//        return null;
//    }
//
//    ////////////////////////////////
//    private void updateLL(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                LinearLayout linearLayout = findViewById(R.id.llCont);
//                linearLayout.removeAllViews();
//                inboxMg();
//                ScrollView svCont = findViewById(R.id.svCont);
//
//                svCont.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        svCont.fullScroll(ScrollView.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
//    }
//
//    private String getPass(){
//        //////////////
//        // Получаем пароль пользователя из бд
//        sqlLiteDatabase.open(this);
//        String select = "SELECT UserPassword FROM USER WHERE UserID = 1";
//        Cursor cursor = sqlLiteDatabase.database.rawQuery(select, null);
//        String userTo = "";
//        if (cursor.moveToFirst()) {
//            do {
//                return cursor.getString(0);
//            } while (cursor.moveToNext());
//        }
//        sqlLiteDatabase.close();
//        //////////////
//        return "";
//    }
//
//}