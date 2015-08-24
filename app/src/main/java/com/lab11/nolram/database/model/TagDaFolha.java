package com.lab11.nolram.database.model;

/**
 * Created by nolram on 24/08/15.
 */
public class TagDaFolha {
    private Tag tag;
    private Folha folha;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Folha getFolha() {
        return folha;
    }

    public void setFolha(Folha folha) {
        this.folha = folha;
    }

    @Override
    public String toString() {
        return "TagDaFolha{" +
                "tag=" + tag +
                ", folha=" + folha +
                '}';
    }
}
