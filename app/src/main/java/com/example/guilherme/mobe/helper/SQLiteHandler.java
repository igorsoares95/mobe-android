package com.example.guilherme.mobe.helper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;


public class SQLiteHandler extends SQLiteOpenHelper {

    public static final String TAG = SQLiteHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "manutencao_veicular";

    private static final String TABLE_USER = "tb_usuario";

    private static final String KEY_ID = "ID";
    private static final String KEY_NOME = "S_NOME";
    private static final String KEY_ID_USUARIO = "ID_USUARIO";
    private static final String KEY_EMAIL = "S_EMAIL";
    private static final String KEY_TELEFONE = "N_TELEFONE";

    public SQLiteHandler(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_ID_USUARIO + " INTEGER," + KEY_TELEFONE + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG,"Tabela criada");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);

    }

    public void addUser(String nome, String email, int id_usuario, String telefone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOME, nome);
        values.put(KEY_EMAIL, email);
        values.put(KEY_ID_USUARIO, id_usuario);
        values.put(KEY_TELEFONE, telefone);
        long id = db.insert(TABLE_USER, null, values);
        db.close();

        Log.d(TAG,"Usuario inserido no SQLite: " + id);

    }

    public void updateUser(String nome, String email, int id_usuario, String telefone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOME, nome);
        values.put(KEY_EMAIL, email);
        values.put(KEY_ID_USUARIO, id_usuario);
        values.put(KEY_TELEFONE, telefone);
        long id = db.update(TABLE_USER,values,"id = 1",null);

        Log.d(TAG,"Usuario atualizado no SQLite: " + id);

    }

    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(cursor.getCount() > 0) {

            user.put("S_NOME", cursor.getString(1));
            user.put("S_EMAIL", cursor.getString(2));
            user.put("ID_USUARIO", cursor.getString(3));
            user.put("N_TELEFONE", cursor.getString(4));

        }
        cursor.close();
        db.close();

        Log.d(TAG, "Associando dados do SQLite: " + user.toString());

        return user;

    }

    public void deleteUsers() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER,null,null);
        db.close();

        Log.d(TAG, "Informacoes do usuario excluidas");

    }

}