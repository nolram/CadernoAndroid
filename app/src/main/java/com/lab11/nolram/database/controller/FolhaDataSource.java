package com.lab11.nolram.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.Folha;
import com.lab11.nolram.database.model.Tag;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nolram on 24/08/15.
 */
public class FolhaDataSource {
    private SQLiteDatabase database;
    private Database dbHelper;

    public final static String[] ALL_COLUMNS_FOLHA = { Database.FOLHA_ID,
            Database.FOLHA_LOCAL_IMAGEM, Database.FOLHA_FK_CADERNO, Database.FOLHA_DATA,
            Database.FOLHA_TITULO, Database.FOLHA_CONTADOR};
    public final static String[] ALL_COLUMNS_TAG = { Database.TAG_ID, Database.TAG_TAG,
            Database.TAG_MIN_TAG };
    public final static String[] ALL_TAGS_FOLHAS = {Database.TAG_DA_FOLHA_ID,
            Database.TAG_DA_FOLHA_ID_FOLHA, Database.TAG_DA_FOLHA_ID_TAG};

    public final static String[] ALL_COLORS = {Database.CADERNO_COR_PRINCIPAL,
            Database.CADERNO_COR_SECUNDARIA};

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
        values.put(Database.FOLHA_CONTADOR, getLastCont(fk_caderno)+1);
        long dbInsert = database.insert(Database.TABLE_FOLHA, null, values);
        Cursor cursor = database.query(Database.TABLE_FOLHA, ALL_COLUMNS_FOLHA,
                Database.FOLHA_ID + " = ?",new String[]{Long.toString(dbInsert)}, null, null, null);
        cursor.moveToFirst();
        Folha folha = cursorToFolha(cursor);
        cursor.close();
        return folha;
    }

    public void criarFolha(String local_imagem, long fk_caderno, String titulo, String[] tags){
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.FOLHA_LOCAL_IMAGEM, local_imagem);
        values.put(Database.FOLHA_FK_CADERNO, fk_caderno);
        values.put(Database.FOLHA_DATA, now.toString());
        values.put(Database.FOLHA_TITULO, titulo);
        values.put(Database.FOLHA_CONTADOR, getLastCont(fk_caderno)+1);
        long dbInsertFolha = database.insert(Database.TABLE_FOLHA, null, values);
        ContentValues cv = new ContentValues();
        cv.put(Database.CADERNO_ULTIMA_MODIFICACAO, now.toString());
        database.update(Database.TABLE_CADERNO, cv, Database.CADERNO_ID + "=" + fk_caderno, null);
        insertTags(tags, dbInsertFolha);
    }

    public int getLastCont(long fk_caderno){
        Cursor cursor = database.query(Database.TABLE_FOLHA, ALL_COLUMNS_FOLHA,
                Database.FOLHA_FK_CADERNO + " = " + fk_caderno,
                null, null, null, Database.FOLHA_CONTADOR + " DESC", "1");
        if(cursor != null && cursor.moveToFirst()){
            Folha folha = cursorToFolha(cursor);
            return folha.getContador();
        }
        return 0;
    }

    public void moveItem(Folha folhaDe, Folha folhaPara) {
        ContentValues valuesDe = new ContentValues();
        ContentValues valuesPara = new ContentValues();
        valuesDe.put(Database.FOLHA_CONTADOR, folhaPara.getContador());
        valuesPara.put(Database.FOLHA_CONTADOR, folhaDe.getContador());
        database.update(Database.TABLE_FOLHA, valuesDe, Database.FOLHA_ID + " = " + folhaDe.getId(),
                null);
        //Log.d("folhaDe", folhaDe.getTitulo() + "- Cont: "+folhaDe.getContador());
        database.update(Database.TABLE_FOLHA, valuesPara, Database.FOLHA_ID + " = " +
                folhaPara.getId(), null);
        //Log.d("folhaPara", folhaPara.getTitulo() + "- Cont: "+folhaPara.getContador());
    }


    public void editarFolha(String local_imagem, long id_folha, long fk_caderno, String titulo,
                            List<String> novas_tags, List<String> velhas_tags){
        DateTime now = new DateTime();
        ContentValues values = new ContentValues();
        values.put(Database.FOLHA_LOCAL_IMAGEM, local_imagem);
        values.put(Database.FOLHA_FK_CADERNO, fk_caderno);
        values.put(Database.FOLHA_DATA, now.toString());
        values.put(Database.FOLHA_TITULO, titulo);
        long dbUpdateFolha = database.update(Database.TABLE_FOLHA, values,
                Database.FOLHA_ID + "=" + id_folha, null);
        ContentValues cv = new ContentValues();
        cv.put(Database.CADERNO_ULTIMA_MODIFICACAO, now.toString());
        database.update(Database.TABLE_CADERNO, cv, Database.CADERNO_ID + "=" + fk_caderno, null);
        updateTags(novas_tags, velhas_tags, id_folha);
    }

    private void updateTags(List<String> novas_tags, List<String> velhas_tags,
                            long fk_folha){
        Tag tag;
        Log.d("Velhas Tags: ", velhas_tags.toString());
        Log.d("Novas Tags: ", novas_tags.toString());
        final Set<String> novasSet = new HashSet<>(novas_tags);
        final Set<String> velhasSet = new HashSet<>(velhas_tags);
        //final Set<String> intersection = intersection(novasSet, velhasSet);
        final Set<String> diferencaVelhas = diferencaVelhas(velhasSet, novasSet); // O que tem nas
        // novas e não tem nas velhas é deletado

        for(String t: diferencaVelhas){
            tag = getTag(t.toLowerCase());
            if(tag != null) {
                Log.d("Tag", tag.getTag());
                int log = database.delete(Database.TABLE_TAG_DA_FOLHA,
                        Database.TAG_DA_FOLHA_ID_FOLHA + " = " + fk_folha + " and " +
                                Database.TAG_DA_FOLHA_ID_TAG + " = " + tag.getId(), null);
                Log.d("deletado: ", String.valueOf(log));
            }else {
                Log.d("Tag not found", t.toLowerCase());
            }
        }
        insertTags(novasSet, fk_folha);
    }

    private void insertTags(String[] tags, long dbInsertFolha) {
        ContentValues values, values1, values2;
        Tag tag;
        long dbInsertTag;
        if(tags != null){
            //Log.d("tamanho", String.valueOf(tags.length));
            for (int i=0; i < tags.length; i++) {
                tag = getTag(tags[i].toLowerCase());
                if(tag == null) {

                    values = new ContentValues();
                    values.put(Database.TAG_TAG, tags[i]);
                    values.put(Database.TAG_MIN_TAG, tags[i].toLowerCase());
                    dbInsertTag = database.insert(Database.TABLE_TAG, null, values);

                    values1 = new ContentValues();
                    values1.put(Database.TAG_DA_FOLHA_ID_FOLHA, dbInsertFolha);
                    values1.put(Database.TAG_DA_FOLHA_ID_TAG, dbInsertTag);
                    database.insert(Database.TABLE_TAG_DA_FOLHA, null, values1);

                }else {

                    values2 = new ContentValues();
                    values2.put(Database.TAG_DA_FOLHA_ID_FOLHA, dbInsertFolha);
                    values2.put(Database.TAG_DA_FOLHA_ID_TAG, tag.getId());
                    database.insert(Database.TABLE_TAG_DA_FOLHA, null, values2);

                }

            }
        }
    }

    private void insertTags(final Set<String> tags, long dbInsertFolha) {
        ContentValues values, values1, values2;
        Tag tag;
        long dbInsertTag;
        if(tags != null){
            //Log.d("tamanho", String.valueOf(tags.length));
            for (String t: tags) {
                tag = getTag(t.toLowerCase());
                if(tag == null) {

                    values = new ContentValues();
                    values.put(Database.TAG_TAG, t);
                    values.put(Database.TAG_MIN_TAG, t.toLowerCase());
                    dbInsertTag = database.insert(Database.TABLE_TAG, null, values);

                    values1 = new ContentValues();
                    values1.put(Database.TAG_DA_FOLHA_ID_FOLHA, dbInsertFolha);
                    values1.put(Database.TAG_DA_FOLHA_ID_TAG, dbInsertTag);
                    database.insert(Database.TABLE_TAG_DA_FOLHA, null, values1);

                }else {

                    values2 = new ContentValues();
                    values2.put(Database.TAG_DA_FOLHA_ID_FOLHA, dbInsertFolha);
                    values2.put(Database.TAG_DA_FOLHA_ID_TAG, tag.getId());
                    database.insert(Database.TABLE_TAG_DA_FOLHA, null, values2);

                }

            }
        }
    }

    public static Set<String> diferencaVelhas(final Set<String> velhas, final Set<String> novas){
        Set<String> copy = new HashSet<>();
        for(String e: velhas){
            if(!novas.contains(e)){
                copy.add(e);
            }
        }
        return copy;
    }

    public static Set<String> intersection(final Set<String> first, final Set<String> second) {
        final Set<String> copy = new HashSet<>(first);
        copy.retainAll(second);
        return copy;
    }

    public Tag getTag(String min_tag){
        Tag tag = null;
        Cursor cursor = database.query(Database.TABLE_TAG,
                ALL_COLUMNS_TAG, Database.TAG_MIN_TAG + " = ?", new String[]{min_tag}, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            tag = cursorToTag(cursor);
        }
        cursor.close();
        return tag;
    }

    public List<Folha> getAllFolhas(long fk_caderno) {
        List<Folha> folhas = new ArrayList<Folha>();
        //log();
        Cursor cursor = database.query(Database.TABLE_FOLHA, ALL_COLUMNS_FOLHA,
                Database.FOLHA_FK_CADERNO + " = " + fk_caderno, null, null, null,
                Database.FOLHA_CONTADOR);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Folha folha = cursorToFolha(cursor);
            folhas.add(folha);
            cursor.moveToNext();
        }
        cursor.close();
        return folhas;
    }

    public Folha getFolha(long id_folha) {
        Folha folha = null;
        //log();
        Cursor cursor = database.query(Database.TABLE_FOLHA, ALL_COLUMNS_FOLHA,
                Database.FOLHA_ID + " = ?", new String[]{String.valueOf(id_folha)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            folha = cursorToFolha(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return folha;
    }

    public Caderno getCaderno(long fk_caderno) {
        Caderno caderno = null;
        //log();
        Cursor cursor = database.query(Database.TABLE_CADERNO, CadernoDataSource.allColumnsCaderno,
                Database.CADERNO_ID + " = ?", new String[]{String.valueOf(fk_caderno)},
                null, null, null);
        cursor.moveToFirst();
        caderno = CadernoDataSource.cursorToCaderno(cursor);
        cursor.close();
        return caderno;
    }

    public String[] getColor(long fk_caderno){
        String[] cores = new String[2];
        Cursor cursor = database.query(Database.TABLE_CADERNO, ALL_COLORS,
                Database.CADERNO_ID + " = ?", new String[]{String.valueOf(fk_caderno)},
                null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cores[0] = cursor.getString(0);
            cores[1] = cursor.getString(1);
            cursor.moveToNext();
        }
        cursor.close();
        return cores;
    }

    public List<Folha> getAllFolhasByTag(String query){
        List<Folha> folhas = new ArrayList<>();
        final String QUERY = "SELECT f."+Database.FOLHA_ID+", f."+Database.FOLHA_LOCAL_IMAGEM+
                ", f."+Database.FOLHA_FK_CADERNO+", f."+Database.FOLHA_DATA+", f."+Database.FOLHA_TITULO+
                ", f."+Database.FOLHA_CONTADOR+
                " FROM "+Database.TABLE_FOLHA+" f INNER JOIN "+Database.TABLE_TAG_DA_FOLHA+" tt INNER JOIN "+
                Database.TABLE_TAG+" t ON t."+Database.TAG_TAG+" LIKE ?"+" WHERE t."+
                Database.TAG_ID+"=tt."+Database.TAG_DA_FOLHA_ID_TAG+" AND f."+Database.FOLHA_ID+
                "=tt."+Database.TAG_DA_FOLHA_ID_FOLHA;
        Cursor cursor = database.rawQuery(QUERY, new String[]{query});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Folha folha = cursorToFolha(cursor);
            folhas.add(folha);
            cursor.moveToNext();
        }
        cursor.close();
        return folhas;
    }

    public List<Folha> getAllFolhasByTag(long id){
        List<Folha> folhas = new ArrayList<>();
        final String QUERY = "SELECT f."+Database.FOLHA_ID+", f."+Database.FOLHA_LOCAL_IMAGEM+
                ", f."+Database.FOLHA_FK_CADERNO+", f."+Database.FOLHA_DATA+", f."+Database.FOLHA_TITULO+
                ",f."+Database.FOLHA_CONTADOR+
                " FROM "+Database.TABLE_FOLHA+" f INNER JOIN "+Database.TABLE_TAG_DA_FOLHA+" tt INNER JOIN "+
                Database.TABLE_TAG+" t ON t."+Database.TAG_ID+" = ?"+" WHERE t."+
                Database.TAG_ID+"=tt."+Database.TAG_DA_FOLHA_ID_TAG+" AND f."+Database.FOLHA_ID+
                "=tt."+Database.TAG_DA_FOLHA_ID_FOLHA;
        Cursor cursor = database.rawQuery(QUERY, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Folha folha = cursorToFolha(cursor);
            folhas.add(folha);
            cursor.moveToNext();
        }
        cursor.close();
        return folhas;
    }

    public List<Tag> getAllTagsByFolha(long fk_folha){
        List<Tag> tags = new ArrayList<>();
        final String QUERY = "SELECT t."+Database.TAG_ID+", t."+Database.TAG_TAG+
                ", t."+Database.TAG_MIN_TAG+", tt."+Database.TAG_DA_FOLHA_ID+", tt."+Database.TAG_DA_FOLHA_ID_TAG+
                " FROM "+Database.TABLE_TAG+" t INNER JOIN "+
                Database.TABLE_TAG_DA_FOLHA+" tt ON tt."+Database.TAG_DA_FOLHA_ID_FOLHA+" = ?"+
                " WHERE t."+Database.TAG_ID+"=tt."+Database.TAG_DA_FOLHA_ID_TAG;
        //Log.d("query", QUERY);
        Cursor cursor = database.rawQuery(QUERY, new String[]{String.valueOf(fk_folha)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tag tag = cursorToTag(cursor);
            tags.add(tag);
            cursor.moveToNext();
        }
        cursor.close();
        return tags;
    }

    public void deleteFolha(Folha folha) {
        long id = folha.getId();
        //for(Tag t: folha.getTags()){
        //    database.delete(Database.TABLE_TAG_DA_FOLHA,
        //            Database.TAG_DA_FOLHA_ID_TAG + " = "+ t.getId(), null);
        //}
        //System.out.println("Comment deleted with id: " + id);
        database.execSQL("UPDATE " + Database.TABLE_FOLHA + " SET " + Database.FOLHA_CONTADOR + " = " +
                        Database.FOLHA_CONTADOR + " - 1 WHERE " + Database.FOLHA_CONTADOR + " > " +
                        folha.getContador() + " AND " + Database.FOLHA_FK_CADERNO + " = " +
                        folha.getFk_caderno()
        );
        database.delete(Database.TABLE_FOLHA, Database.FOLHA_ID + " = " + id, null);
    }

    public void deleteCaderno(long fk_caderno) {
        //System.out.println("Comment deleted with id: " + id);
        database.delete(Database.TABLE_CADERNO, Database.CADERNO_ID + " = " + fk_caderno, null);
    }

    public void log(){
        Cursor cursor = database.query(Database.TABLE_TAG_DA_FOLHA, ALL_TAGS_FOLHAS, null, null, null, null, null);
        while (cursor.moveToNext()){
            Log.d(Database.TABLE_TAG_DA_FOLHA, Database.TABLE_TAG_DA_FOLHA);
            Log.d(Database.TAG_DA_FOLHA_ID, String.valueOf(cursor.getLong(0)));
            Log.d(Database.TAG_DA_FOLHA_ID_FOLHA, String.valueOf(cursor.getLong(1)));
            Log.d(Database.TAG_DA_FOLHA_ID_TAG, String.valueOf(cursor.getLong(2)));
        }
        cursor.close();
    }

    public Folha cursorToFolha(Cursor cursor) {
        Folha folha = new Folha();
        folha.setId(cursor.getLong(0));
        folha.setLocal_folha(cursor.getString(1));
        folha.setFk_caderno(cursor.getLong(2));
        folha.setData(cursor.getString(3));
        folha.setTitulo(cursor.getString(4));
        folha.setTags(getAllTagsByFolha(folha.getId()));
        folha.setContador(cursor.getInt(5));
        return folha;
    }

    public static Folha cursorToFolhaSemTags(Cursor cursor) {
        Folha folha = new Folha();
        folha.setId(cursor.getLong(0));
        folha.setLocal_folha(cursor.getString(1));
        folha.setFk_caderno(cursor.getLong(2));
        folha.setData(cursor.getString(3));
        folha.setTitulo(cursor.getString(4));
        //folha.setTags(getAllTagsByFolha(folha.getId()));
        folha.setContador(cursor.getInt(5));
        return folha;
    }

    public static Tag cursorToTagGroupBy(Cursor cursor){
        Tag tag = new Tag();
        tag.setContador(cursor.getInt(0));
        tag.setTag(cursor.getString(1));
        tag.setTagMin(cursor.getString(2));
        tag.setId(cursor.getLong(3));
        return tag;
    }


    public static Tag cursorToTag(Cursor cursor){
        Tag tag = new Tag();
        tag.setId(cursor.getLong(0));
        tag.setTag(cursor.getString(1));
        tag.setTagMin(cursor.getString(2));
        return tag;
    }
}
