package com.example.websocketclient.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.websocketclient.Entity.MsgEntity;
import com.example.websocketclient.Entity.UserEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SqLiteDatabase extends SQLiteOpenHelper {
    private static String DB_PATH;// = "/data/data/com.kazzinc.checklist/databases/";
    private static String DB_NAME = "WebSockets.db";
    private static final int SCHEMA = 3;

    public SQLiteDatabase database;
    private Context myContext;

    private int qIndex = 0;

    public SqLiteDatabase(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.myContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {

    }

    public void create_db(Context context) throws IOException {
        InputStream myInput = null;
        OutputStream myOutput = null;

        DB_PATH = context.getDatabasePath(DB_NAME).getPath();

        try {
            //File file = new File(DB_PATH + DB_NAME);
            File file = new File(DB_PATH);
            if (!file.exists()) {

                this.getReadableDatabase();
                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                //String outFileName = DB_PATH + DB_NAME;
                String outFileName = DB_PATH;

                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        }
        catch(IOException ex){
            Toast.makeText(myContext.getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            throw new IOException();
        }
    }

    //открытие БД
    public void open(Context context) throws SQLException {

        DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        //String path = DB_PATH + DB_NAME;
        String path = DB_PATH;
        database = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE);
    }

    //закрытие БД
    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    public void insertUser(UserEntity userEntity) throws Exception {
        ContentValues values = new ContentValues();
        values.put("UserLogin", userEntity.getUser());
        values.put("UserPassword", userEntity.getPass());


        database.insert("User", null, values);
    }

    public void insertMSg(MsgEntity msgEntity) throws Exception {
        ContentValues values = new ContentValues();
        values.put("NameTo", msgEntity.getNameTo());
        values.put("TabTo", msgEntity.getTabTo());
        values.put("NameFrom", msgEntity.getNameFrom());
        values.put("TabFrom", msgEntity.getTabFrom());
        values.put("Msg", msgEntity.getMsg());
        values.put("Status", msgEntity.getStatus());



        database.insert("MSG", null, values);
    }

}
