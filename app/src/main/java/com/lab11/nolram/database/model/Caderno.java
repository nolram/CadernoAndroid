package com.lab11.nolram.database.model;

/**
 * Created by nolram on 24/08/15.
 */
public class Caderno {
    private long id;
    private String titulo;
    private String badge;
    private String descricao;
    private String dataAdicionado;
    private String ultimaModificacao;
    private String corPrincipal;
    private String corSecundaria;

    public String getCorSecundaria() {
        return corSecundaria;
    }

    public void setCorSecundaria(String corSecundaria) {
        this.corSecundaria = corSecundaria;
    }

    public void setUltimaModificacao(String ultimaModificacao) {
        this.ultimaModificacao = ultimaModificacao;
    }

    public String getCorPrincipal() {
        return corPrincipal;
    }

    public void setCorPrincipal(String corPrincipal) {
        this.corPrincipal = corPrincipal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataAdicionado() {
        return dataAdicionado;
    }

    public void setDataAdicionado(String dataAdicionado) {
        this.dataAdicionado = dataAdicionado;
    }

    public String getUltimaModificacao() {
        return ultimaModificacao;
    }

    public void setUltimaModificao(String ultimaModificacao){
        this.ultimaModificacao = ultimaModificacao;
    }

    @Override
    public String toString() {
        return titulo;
    }

}
