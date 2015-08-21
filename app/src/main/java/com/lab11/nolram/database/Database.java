package com.lab11.nolram.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nolram on 21/08/15.
 */
public class Database extends SQLiteOpenHelper {
    private static final String BANCO_DADOS = "CadernoDatabase";
    private static int VERSAO = 1;

    public Database(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE caderno (_id INTEGER PRIMARY KEY," +
                "titulo TEXT, badge TEXT NULL, descricao TEXT NULL, data_adicionado DATE,");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
