package com.lab11.nolram.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.Folha;

import org.joda.time.DateTime;

/**
 * Created by nolram on 24/08/15.
 */
public class FolhaDataSource {
    private SQLiteDatabase database;
    private Database dbHelper;
    private String[] allColumns = { Database.FOLHA_ID,
            Database.FOLHA_LOCAL_IMAGEM, Database.FOLHA_FK_CADERNO, Database.FOLHA_DATA};

    public FolhaDataSource(Context context) {
        dbHelper = new Database(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Folha criarFolhaERetornar(String local_imagem, long fk_caderno){
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.FOLHA_LOCAL_IMAGEM, local_imagem);
        values.put(Database.FOLHA_FK_CADERNO, fk_caderno);
        values.put(Database.FOLHA_DATA, now.toString());
        long dbInsert = database.insert(Database.TABLE_FOLHA, null, values);

    }

    private Folha cursorToFolha(Cursor cursor) {
        Folha folha = new Folha();
        folha.setId(cursor.getLong(0));
        folha.setLocal_folha(cursor.getString(1));
        folha.setBadge(cursor.getString(2));
        caderno.setDescricao(cursor.getString(3));
        caderno.setDataAdicionado(cursor.getString(4));
        caderno.setUltimaModificao(cursor.getString(5));
        return caderno;
    }
}
