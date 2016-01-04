package com.lab11.nolram.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nolram on 21/08/15.
 */
public class Database extends SQLiteOpenHelper {
    public static final String TABLE_CADERNO = "caderno";
    public static final String CADERNO_ID = "_id";
    public static final String CADERNO_TITULO = "caderno_titulo";
    public static final String CADERNO_ARQUIVADO = "arquivado";
    public static final String CADERNO_BADGE = "badge";
    public static final String CADERNO_COR_PRINCIPAL = "cor";
    public static final String CADERNO_ID_COR_PRINCIPAL = "id_cor";
    public static final String CADERNO_COR_SECUNDARIA = "cor_secundaria";
    public static final String CADERNO_ID_COR_SECUNDARIA = "id_cor_secundaria";
    public static final String CADERNO_DESCRICAO = "descricao";
    public static final String CADERNO_DATA = "data_adicionado";
    public static final String CADERNO_ULTIMA_MODIFICACAO = "ultima_modificacao";
    public static final String TABLE_FOLHA = "folha";
    public static final String FOLHA_ID = "_id";
    public static final String FOLHA_TITULO = "folha_titulo";
    public static final String FOLHA_LOCAL_IMAGEM = "local_folha";
    public static final String FOLHA_DATA = "data_adicionado";
    public static final String FOLHA_FK_CADERNO = "fk_caderno";
    public static final String FOLHA_CONTADOR = "contador";
    public static final String TABLE_TAG = "tag";
    public static final String TAG_ID = "_id";
    public static final String TAG_TAG = "tag";
    public static final String TAG_MIN_TAG = "min_tag";
    public static final String TABLE_TAG_DA_FOLHA = "tag_da_folha";
    public static final String TAG_DA_FOLHA_ID = "_id";
    public static final String TAG_DA_FOLHA_ID_TAG = "fk_tag";
    public static final String TAG_DA_FOLHA_ID_FOLHA = "fk_folha";
    private static final String BANCO_DADOS = "CadernoDatabase.db";
    private static final String CREATE_CADERNO = "CREATE TABLE " + TABLE_CADERNO + "(" + CADERNO_ID + " INTEGER PRIMARY KEY," +
            CADERNO_TITULO + " TEXT," + CADERNO_BADGE + " TEXT NULL," + CADERNO_DESCRICAO + " TEXT NULL," +
            CADERNO_COR_PRINCIPAL + " TEXT NULL," + CADERNO_COR_SECUNDARIA + " TEXT NULL," +
            CADERNO_DATA + " DATE," + CADERNO_ULTIMA_MODIFICACAO + " DATE);";
    private static final String CREATE_FOLHA = "CREATE TABLE " + TABLE_FOLHA + "(" + FOLHA_ID + " INTEGER PRIMARY KEY, " +
            FOLHA_LOCAL_IMAGEM + " TEXT, " + FOLHA_DATA + " DATE, " + FOLHA_FK_CADERNO + " INTEGER, " +
            FOLHA_TITULO + " TEXT NULL, " + FOLHA_CONTADOR + " INTEGER, " +
            "FOREIGN KEY (" + FOLHA_FK_CADERNO + ") REFERENCES " + TABLE_CADERNO + "(" + CADERNO_ID + ") ON DELETE CASCADE);";
    private static final String CREATE_TAG = "CREATE TABLE " + TABLE_TAG + "(" + TAG_ID + " INTEGER PRIMARY KEY, " +
            TAG_TAG + " TEXT, " + TAG_MIN_TAG + " TEXT);";
    private static final String CREATE_TAG_DA_FOLHA = "CREATE TABLE " + TABLE_TAG_DA_FOLHA +
            "(" + TAG_DA_FOLHA_ID + " INTEGER PRIMARY KEY, " + TAG_DA_FOLHA_ID_TAG + " INTEGER, " + TAG_DA_FOLHA_ID_FOLHA + " INTEGER, " +
            " UNIQUE(" + TAG_DA_FOLHA_ID_TAG + " , " + TAG_DA_FOLHA_ID_FOLHA + ") ON CONFLICT REPLACE " +
            "FOREIGN KEY (" + TAG_DA_FOLHA_ID_FOLHA + ") REFERENCES " + TABLE_FOLHA + "(" + FOLHA_ID + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + TAG_DA_FOLHA_ID_TAG + ") REFERENCES " + TABLE_TAG + "(" + TAG_ID +
            ") ON DELETE CASCADE);";//, PRIMARY KEY("+TAG_DA_FOLHA_ID_TAG+","+TAG_DA_FOLHA_ID_FOLHA+"));";


    private static int VERSAO_DB = 2;

    public Database(Context context) {
        super(context, BANCO_DADOS, null, VERSAO_DB);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CADERNO);

        db.execSQL(CREATE_FOLHA);

        db.execSQL(CREATE_TAG);

        db.execSQL(CREATE_TAG_DA_FOLHA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                db.execSQL("ALTER TABLE "+TABLE_CADERNO+" ADD COLUMN "+ CADERNO_ARQUIVADO + " BOOLEAN NOT NULL DEFAULT 0");
        }
    }
}
