package com.lab11.nolram.database.model;

/**
 * Created by nolram on 24/08/15.
 */
public class Folha {
    private long id;
    private String local_folha;
    private String titulo;
    private String data_adicionado;
    private long fk_caderno;


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
        this.data_adicionado = data_adicionado;
    }

    public String toString(){
        return local_folha;
    }
}
