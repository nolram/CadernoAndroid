package com.lab11.nolram.database.model;

/**
 * Created by nolram on 24/08/15.
 */
public class Tag {
    private long id;
    private String tag;
    private String tag_min;
    private String data_adicionado;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag_min() {
        return tag_min;
    }

    public void setTag_min(String tag_min) {
        this.tag_min = tag_min;
    }

    public String getData_adicionado() {
        return data_adicionado;
    }

    public void setData_adicionado(String data_adicionado) {
        this.data_adicionado = data_adicionado;
    }

    @Override
    public String toString() {
        return tag;
    }
}
