package com.lab11.nolram.database.controller;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import org.joda.time.DateTime;

import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.model.Caderno;

/**
 * Created by nolram on 24/08/15.
 */
public class CadernoDataSource {
    private SQLiteDatabase database;
    private Database dbHelper;
    private String[] allColumns = { Database.CADERNO_ID,
            Database.CADERNO_TITULO, Database.CADERNO_BADGE,
            Database.CADERNO_DESCRICAO, Database.CADERNO_DATA, Database.CADERNO_ULTIMA_MODIFICACAO };

    public CadernoDataSource(Context context) {
        dbHelper = new Database(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Caderno criarCadernoERetornar(String titulo, String descricao){
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.CADERNO_TITULO, titulo);
        values.put(Database.CADERNO_DESCRICAO, descricao);
        values.put(Database.CADERNO_DATA, now.toString());
        values.put(Database.CADERNO_ULTIMA_MODIFICACAO, now.toString());

        long dbInsert = database.insert(Database.TABLE_CADERNO, null, values);
        Cursor cursor = database.query(Database.TABLE_CADERNO, allColumns, Database.CADERNO_ID + " = " + dbInsert,
                null, null, null, null);
        cursor.moveToFirst();
        Caderno caderno = cursorToCaderno(cursor);
        cursor.close();
        return caderno;
    }

    public void criarCaderno(String titulo, String descricao){
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.CADERNO_TITULO, titulo);
        values.put(Database.CADERNO_DESCRICAO, descricao);
        values.put(Database.CADERNO_DATA, now.toString());
        values.put(Database.CADERNO_ULTIMA_MODIFICACAO, now.toString());

        long dbInsert = database.insert(Database.TABLE_CADERNO, null, values);
    }

    public void deleteCaderno(Caderno caderno) {
        long id = caderno.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(Database.TABLE_CADERNO, Database.CADERNO_ID
                + " = " + id, null);
    }

    public List<Caderno> getAllCadernos() {
        List<Caderno> cadernos = new ArrayList<Caderno>();

        Cursor cursor = database.query(Database.TABLE_CADERNO,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Caderno caderno = cursorToCaderno(cursor);
            cadernos.add(caderno);
            cursor.moveToNext();
        }
        cursor.close();
        return cadernos;
    }

    private Caderno cursorToCaderno(Cursor cursor) {
        Caderno caderno = new Caderno();
        caderno.setId(cursor.getLong(0));
        caderno.setTitulo(cursor.getString(1));
        caderno.setBadge(cursor.getString(2));
        caderno.setDescricao(cursor.getString(3));
        caderno.setDataAdicionado(cursor.getString(4));
        caderno.setUltimaModificao(cursor.getString(5));
        return caderno;
    }
}
