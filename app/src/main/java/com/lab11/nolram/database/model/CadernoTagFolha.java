package com.lab11.nolram.database.model;

/**
 * Created by nolram on 07/10/15.
 */
public class CadernoTagFolha {

    private Caderno caderno;
    private Folha folha;
    private Tag tag;

    public CadernoTagFolha(Caderno caderno){
        this.caderno = caderno;
        this.folha = null;
        this.tag = null;
    }
    public CadernoTagFolha(Folha folha){
        this.folha = folha;
        this.caderno = null;
        this.tag = null;
    }
    public CadernoTagFolha(Tag tag){
        this.tag = tag;
        this.folha = null;
        this.caderno = null;
    }

    public Caderno getCaderno() {
        return caderno;
    }

    public void setCaderno(Caderno caderno) {
        this.caderno = caderno;
    }

    public Folha getFolha() {
        return folha;
    }

    public void setFolha(Folha folha) {
        this.folha = folha;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
