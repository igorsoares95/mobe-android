package com.example.guilherme.mobe.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();

    SharedPreferences pref;

    Editor editor;
    Context _context;


    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "ManutencaoVeicularLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager (Context context) {

        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();

    }

    public void setLogin (boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN,isLoggedIn);
        editor.commit();
        Log.d(TAG,"Sessão de Login do Usuario modificada");

    }

    public boolean isLoggedIn() { return pref.getBoolean(KEY_IS_LOGGEDIN,false); }

}