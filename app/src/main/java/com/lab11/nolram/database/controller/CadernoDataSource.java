package com.lab11.nolram.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.Folha;
import com.lab11.nolram.database.model.Tag;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolram on 24/08/15.
 */
public class CadernoDataSource {
    public static final String[] allColumnsCaderno = {Database.CADERNO_ID,
            Database.CADERNO_TITULO, Database.CADERNO_BADGE,
            Database.CADERNO_DESCRICAO, Database.CADERNO_DATA, Database.CADERNO_ULTIMA_MODIFICACAO,
            Database.CADERNO_COR_PRINCIPAL, Database.CADERNO_COR_SECUNDARIA};
    private SQLiteDatabase database;
    private Database dbHelper;

    public CadernoDataSource(Context context) {
        dbHelper = new Database(context);
    }

    public static Caderno cursorToCaderno(Cursor cursor) {
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

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Caderno criarCadernoERetornar(String titulo, String descricao) {
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.CADERNO_TITULO, titulo);
        values.put(Database.CADERNO_DESCRICAO, descricao);
        values.put(Database.CADERNO_DATA, now.toString());
        values.put(Database.CADERNO_ULTIMA_MODIFICACAO, now.toString());

        long dbInsert = database.insert(Database.TABLE_CADERNO, null, values);
        Cursor cursor = database.query(Database.TABLE_CADERNO, allColumnsCaderno, Database.CADERNO_ID + " = " + dbInsert,
                null, null, null, null);
        cursor.moveToFirst();
        Caderno caderno = cursorToCaderno(cursor);
        cursor.close();
        return caderno;
    }

    public void criarCaderno(String titulo, String descricao, String[] cor, String badge) {
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

    public void updateCaderno(String titulo, String descricao, String[] cor, String badge, long id) {
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.CADERNO_TITULO, titulo);
        values.put(Database.CADERNO_DESCRICAO, descricao);
        values.put(Database.CADERNO_DATA, now.toString());
        values.put(Database.CADERNO_ULTIMA_MODIFICACAO, now.toString());
        values.put(Database.CADERNO_COR_PRINCIPAL, cor[0]);
        values.put(Database.CADERNO_COR_SECUNDARIA, cor[1]);
        values.put(Database.CADERNO_BADGE, badge);

        long dbInsert = database.update(Database.TABLE_CADERNO, values,
                Database.CADERNO_ID + " = " + id, null);
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
                allColumnsCaderno, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Caderno caderno = cursorToCaderno(cursor);
            cadernos.add(caderno);
            cursor.moveToNext();
        }
        cursor.close();
        return cadernos;
    }

    public Caderno getCaderno(long fk_caderno) {
        Caderno caderno;
        Cursor cursor = database.query(Database.TABLE_CADERNO,
                allColumnsCaderno, Database.CADERNO_ID + " = " + fk_caderno, null, null, null, null);
        cursor.moveToFirst();
        caderno = cursorToCaderno(cursor);
        return caderno;
    }

    public List<Caderno> searchCadernos(String query) {
        List<Caderno> cadernos = new ArrayList<Caderno>();

        Cursor cursor = database.query(Database.TABLE_CADERNO,
                allColumnsCaderno, Database.CADERNO_TITULO + " LIKE ?",
                new String[]{"%" + query + "%"}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Caderno caderno = cursorToCaderno(cursor);
            cadernos.add(caderno);
            cursor.moveToNext();
        }
        cursor.close();
        return cadernos;
    }

    public List<Tag> searchTags(String query) {
        List<Tag> tags = new ArrayList<Tag>();

        Cursor cursor = database.query(Database.TABLE_TAG,
                FolhaDataSource.ALL_COLUMNS_TAG, Database.TAG_MIN_TAG + " LIKE ?",
                new String[]{"%" + query.toLowerCase() + "%"}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tag caderno = FolhaDataSource.cursorToTag(cursor);
            tags.add(caderno);
            cursor.moveToNext();
        }
        cursor.close();
        return tags;
    }

    public List<Folha> searchFolhas(String query) {
        List<Folha> folhas = new ArrayList<Folha>();

        Cursor cursor = database.query(Database.TABLE_FOLHA,
                FolhaDataSource.ALL_COLUMNS_FOLHA, Database.FOLHA_TITULO + " LIKE ?",
                new String[]{"%" + query + "%"}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Folha folha = FolhaDataSource.cursorToFolhaSemTags(cursor);
            folhas.add(folha);
            cursor.moveToNext();
        }
        cursor.close();
        return folhas;
    }

    public List<Tag> getAllTagsGroupBy() {
        List<Tag> tags = new ArrayList<>();
        final String QUERY = "SELECT COUNT(tt." + Database.TAG_DA_FOLHA_ID_TAG + "), t." + Database.TAG_TAG +
                ", t." + Database.TAG_MIN_TAG + ", t." + Database.TAG_ID + " FROM " + Database.TABLE_TAG + " t INNER JOIN " +
                Database.TABLE_TAG_DA_FOLHA + " tt ON tt." + Database.TAG_DA_FOLHA_ID_TAG + "=t." + Database.TAG_ID +
                " GROUP BY tt." + Database.TAG_DA_FOLHA_ID_TAG + " ORDER BY COUNT(tt." + Database.TAG_DA_FOLHA_ID_TAG + ") DESC";
        Cursor cursor = database.rawQuery(QUERY, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tag tag = cursorToTagGroupBy(cursor);
            tags.add(tag);
            cursor.moveToNext();
        }
        cursor.close();
        return tags;
    }

    public List<Tag> getAllTagsGroupByLimited() {
        List<Tag> tags = new ArrayList<>();
        final String QUERY = "SELECT COUNT(tt." + Database.TAG_DA_FOLHA_ID_TAG + "), t." + Database.TAG_TAG +
                ", t." + Database.TAG_MIN_TAG + ", t." + Database.TAG_ID + " FROM " + Database.TABLE_TAG + " t INNER JOIN " +
                Database.TABLE_TAG_DA_FOLHA + " tt ON tt." + Database.TAG_DA_FOLHA_ID_TAG + "=t." + Database.TAG_ID +
                " GROUP BY tt." + Database.TAG_DA_FOLHA_ID_TAG + " ORDER BY COUNT(tt." + Database.TAG_DA_FOLHA_ID_TAG + ") DESC" +
                " LIMIT 10";
        Cursor cursor = database.rawQuery(QUERY, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tag tag = cursorToTagGroupBy(cursor);
            tags.add(tag);
            cursor.moveToNext();
        }
        cursor.close();
        return tags;
    }

    private Tag cursorToTagGroupBy(Cursor cursor) {
        Tag tag = new Tag();
        tag.setContador(cursor.getInt(0));
        tag.setTag(cursor.getString(1));
        tag.setTagMin(cursor.getString(2));
        tag.setId(cursor.getLong(3));
        return tag;
    }
}
