package com.lab11.nolram.database.model;

/**
 * Created by nolram on 24/08/15.
 */
public class Tag {
    private long id;
    private String tag;
    private String tagMin;
    private int contador;

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

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

    public String getTagMin() {
        return tagMin;
    }

    public void setTagMin(String tagMin) {
        this.tagMin = tagMin;
    }

    @Override
    public String toString() {
        return tag;
    }
}
