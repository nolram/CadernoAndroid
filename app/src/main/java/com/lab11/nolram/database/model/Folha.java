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
    private String data_adicionado;
    private long fk_caderno;
    private List<Tag> tags;

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

    public String getData_adicionado() {
        return data_adicionado;
    }

    public void setData_adicionado(String data_adicionado) {
        DateTime dt = new DateTime(data_adicionado);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss MM/dd/yyyy");
        this.data_adicionado = dtf.print(dt);
    }

    public String toString(){
        return local_folha;
    }
}
