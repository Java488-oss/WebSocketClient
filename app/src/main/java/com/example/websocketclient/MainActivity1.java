package com.example.websocketclient;//package com.example.websocketclient;
//
//import static android.content.ContentValues.TAG;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.concurrent.ExecutionException;
//
//import ua.naiksoftware.stomp.StompClient;
//
//public class MainActivity extends AppCompatActivity {
//
////    private StompClient mStompClient;
//
//    @SuppressLint("CheckResult")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        ///////////////////////////////////////////////////////////////////
//
//        Button btnSend = findViewById(R.id.btnSend);
//        Button btnRegister = findViewById(R.id.btnRegister);
//
//        AsyncTask<Void, Void, StompClient> mStompClient = new WebSocketsConnectLocal(MainActivity.this).execute();
//
//        try {
//            mStompClient.get().topic("/spring-security-mvc-socket/hello-msg-mapping").subscribe();
//            Log.d(TAG, "Stomp "+mStompClient.get().getTopicId("/spring-security-mvc-socket/hello-msg-mapping"));
//
//            final String[] str = new String[1];
//            //получение сообщения с сервера на клиет
//            mStompClient.get().topic("/test/greetings").subscribe(topicMessage -> {
//                str[0] = topicMessage.getPayload();
//
//                Log.d(TAG, "Stomp1 " + str[0]);
//
//                if (str[0].equals("false")) {
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "Вы ввели не коректные данные", Toast.LENGTH_LONG).show();
//                        }
//                    });
////
//                } else {
//                    Intent myIntent = new Intent(MainActivity.this, FindUser.class);
//                    startActivity(myIntent);
//                }
//            });
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
////        } catch (ExecutionException | InterruptedException e) {
////            e.printStackTrace();
////        }
//
//
//            btnSend.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//
////                        mStompClient.get().send("/test/hello-msg-mapping"+"-user"+mStompClient.get().getTopicId("/spring-security-mvc-socket/hello-msg-mapping"), String.valueOf(textSend())).subscribe();
//                        mStompClient.get().send("/spring-security-mvc-socket/hello-msg-mapping", String.valueOf(textSend())).subscribe();
////                    EditText etText = findViewById(R.id.etText);
////                    EditText etName = findViewById(R.id.etName);
////                    etText.getText().clear();
////                    etName.getText().clear();
//                    } catch (ExecutionException | InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            btnRegister.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try{
//                        mStompClient.get().send("/topic/register", String.valueOf(textSend())).subscribe();
//                        EditText etText = findViewById(R.id.etText);
//                        EditText etName = findViewById(R.id.etName);
//                        etText.getText().clear();
//                        etName.getText().clear();
//                    } catch (ExecutionException | InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            ///////////////////////////////////////////////////////////////////
//        }
//
//        private JSONObject textSend(){
//            EditText etText = findViewById(R.id.etText);
//            EditText etName = findViewById(R.id.etName);
//            JSONObject student1 = new JSONObject();
//            try {
//                student1.put("user", etName.getText());
//                student1.put("pass", etText.getText());
//
//                JSONArray jsonArray = new JSONArray();
//
//                jsonArray.put(student1);
//
//                JSONObject jsonObject = new JSONObject();
//
//                jsonObject.put("register", jsonArray);
//
//                return jsonObject;
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return student1;
//        }
//    }
//
//
