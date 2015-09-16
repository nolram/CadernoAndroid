package com.lab11.nolram.database.controller;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.Tag;

/**
 * Created by nolram on 24/08/15.
 */
public class CadernoDataSource {
    private SQLiteDatabase database;
    private Database dbHelper;
    private String[] allColumns = { Database.CADERNO_ID,
            Database.CADERNO_TITULO, Database.CADERNO_BADGE,
            Database.CADERNO_DESCRICAO, Database.CADERNO_DATA, Database.CADERNO_ULTIMA_MODIFICACAO,
            Database.CADERNO_COR_PRINCIPAL, Database.CADERNO_COR_SECUNDARIA};

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

    public void criarCaderno(String titulo, String descricao, String[] cor, String badge){
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.CADERNO_TITULO, titulo);
        values.put(Database.CADERNO_DESCRICAO, descricao);
        values.put(Database.CADERNO_DATA, now.toString());
        values.put(Database.CADERNO_ULTIMA_MODIFICACAO, now.toString());
        values.put(Database.CADERNO_COR_PRINCIPAL, cor[0]);
        values.put(Database.CADERNO_COR_SECUNDARIA, cor[1]);
        values.put(Database.CADERNO_BADGE, badge);

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


    public List<Caderno> searchCadernos(String query) {
        List<Caderno> cadernos = new ArrayList<Caderno>();

        Cursor cursor = database.query(Database.TABLE_CADERNO,
                allColumns, Database.CADERNO_TITULO+" LIKE '%"+query+"%'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Caderno caderno = cursorToCaderno(cursor);
            cadernos.add(caderno);
            cursor.moveToNext();
        }
        cursor.close();
        return cadernos;
    }


    public List<Tag> getAllTagsGroupBy(){
        List<Tag> tags = new ArrayList<>();
        final String QUERY = "SELECT COUNT(tt."+Database.TAG_DA_FOLHA_ID_TAG+"), t."+Database.TAG_TAG+
                ", t."+Database.TAG_MIN_TAG+", t."+Database.TAG_ID+" FROM "+Database.TABLE_TAG+" t INNER JOIN "+
                Database.TABLE_TAG_DA_FOLHA+" tt ON tt."+Database.TAG_DA_FOLHA_ID_TAG+"=t."+Database.TAG_ID+
                " GROUP BY tt."+Database.TAG_DA_FOLHA_ID_TAG;
        Cursor cursor = database.rawQuery(QUERY, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Tag tag = cursorToTagGroupBy(cursor);
            tags.add(tag);
            cursor.moveToNext();
        }
        cursor.close();
        return tags;
    }

    private Tag cursorToTagGroupBy(Cursor cursor){
        Tag tag = new Tag();
        tag.setContador(cursor.getInt(0));
        tag.setTag(cursor.getString(1));
        tag.setTagMin(cursor.getString(2));
        tag.setId(cursor.getLong(3));
        return tag;
    }


    private Caderno cursorToCaderno(Cursor cursor) {
        Caderno caderno = new Caderno();
        caderno.setId(cursor.getLong(0));
        caderno.setTitulo(cursor.getString(1));
        caderno.setBadge(cursor.getString(2));
        caderno.setDescricao(cursor.getString(3));
        caderno.setDataAdicionado(cursor.getString(4));
        caderno.setUltimaModificao(cursor.getString(5));
        caderno.setCorPrincipal(cursor.getString(6));
        caderno.setCorSecundaria(cursor.getString(7));
        return caderno;
    }
}
