package com.lab11.nolram.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.model.Folha;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolram on 24/08/15.
 */
public class FolhaDataSource {
    private SQLiteDatabase database;
    private Database dbHelper;
    private String[] allColumns = { Database.FOLHA_ID,
            Database.FOLHA_LOCAL_IMAGEM, Database.FOLHA_FK_CADERNO, Database.FOLHA_DATA,
            Database.FOLHA_TITULO};

    public FolhaDataSource(Context context) {
        dbHelper = new Database(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Folha criarFolhaERetornar(String local_imagem, long fk_caderno, String titulo){
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.FOLHA_LOCAL_IMAGEM, local_imagem);
        values.put(Database.FOLHA_FK_CADERNO, fk_caderno);
        values.put(Database.FOLHA_DATA, now.toString());
        values.put(Database.FOLHA_TITULO, titulo);
        long dbInsert = database.insert(Database.TABLE_FOLHA, null, values);
        Cursor cursor = database.query(Database.TABLE_FOLHA, allColumns, Database.FOLHA_ID + " = " + dbInsert,
                null, null, null, null);
        cursor.moveToFirst();
        Folha folha = cursorToFolha(cursor);
        cursor.close();
        return folha;
    }

    public void criarFolha(String local_imagem, long fk_caderno, String titulo){
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.FOLHA_LOCAL_IMAGEM, local_imagem);
        values.put(Database.FOLHA_FK_CADERNO, fk_caderno);
        values.put(Database.FOLHA_DATA, now.toString());
        values.put(Database.FOLHA_TITULO, titulo);
        long dbInsert = database.insert(Database.TABLE_FOLHA, null, values);
    }

    public List<Folha> getAllFolhas(long fk_caderno) {
        List<Folha> folhas = new ArrayList<Folha>();

        Cursor cursor = database.query(Database.TABLE_FOLHA,
                allColumns, Database.FOLHA_FK_CADERNO + " = " + fk_caderno, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Folha folha = cursorToFolha(cursor);
            folhas.add(folha);
            cursor.moveToNext();
        }
        cursor.close();
        return folhas;
    }

    public void deleteFolha(Folha folha) {
        long id = folha.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(Database.TABLE_FOLHA, Database.FOLHA_ID
                + " = " + id, null);
    }

    private Folha cursorToFolha(Cursor cursor) {
        Folha folha = new Folha();
        folha.setId(cursor.getLong(0));
        folha.setLocal_folha(cursor.getString(1));
        folha.setFk_caderno(cursor.getLong(2));
        folha.setData_adicionado(cursor.getString(3));
        folha.setTitulo(cursor.getString(4));
        return folha;
    }
}
