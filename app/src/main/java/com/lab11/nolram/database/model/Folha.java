package com.lab11.nolram.database.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by nolram on 24/08/15.
 */
public class Folha {
    private long id;
    private String local_folha;
    private String titulo;
    private String data;
    private String dataBanco;
    private long fk_caderno;
    private List<Tag> tags;
    private int contador;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public long getFk_caderno() {
        return fk_caderno;
    }

    public void setFk_caderno(long fk_caderno) {
        this.fk_caderno = fk_caderno;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocal_folha() {
        return local_folha;
    }

    public void setLocal_folha(String local_folha) {
        this.local_folha = local_folha;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        setDataBanco(data);
        DateTime dt = new DateTime(data);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        this.data = dtf.print(dt);
    }



    public String getDataBanco() {
        return dataBanco;
    }

    public void setDataBanco(String dataBanco) {
        this.dataBanco = dataBanco;
    }

    public String toString() {
        return titulo;
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

}
