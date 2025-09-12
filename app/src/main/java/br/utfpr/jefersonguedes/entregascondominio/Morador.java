package br.utfpr.jefersonguedes.entregascondominio;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class Morador implements Cloneable{

    public static Comparator <Morador> ordenacaoCrescente = new Comparator<Morador> () {

        @Override
        public int compare(Morador morador1, Morador morador2) {
            return morador1.getNome().compareToIgnoreCase(morador2.getNome());
        }
    };

    public static Comparator <Morador> ordenacaoDecrescente = new Comparator<Morador> () {

        @Override
        public int compare(Morador morador1, Morador morador2) {
            return -1 * morador1.getNome().compareToIgnoreCase(morador2.getNome());
        }
    };

    private String nome;

    private int numApt;

    private boolean aptAlugado;

    private int bloco;

    private Genero genero;


    public Morador(String nome, int numApt, boolean aptAlugado, int bloco, Genero genero) {
        this.nome = nome;
        this.numApt = numApt;
        this.aptAlugado = aptAlugado;
        this.bloco = bloco;
        this.genero = genero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNumApt() {
        return numApt;
    }

    public void setNumApt(int numApt) {
        this.numApt = numApt;
    }

    public boolean isAptAlugado() {
        return aptAlugado;
    }

    public void setAptAlugado(boolean aptAlugado) {
        this.aptAlugado = aptAlugado;
    }

    public int getBloco() {
        return bloco;
    }

    public void setBloco(int bloco) {
        this.bloco = bloco;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {



        return super.clone();
    }

    @Override
    public String toString() {
        return  nome + "\n" +
                numApt + "\n" +
                aptAlugado + "\n" +
                bloco + "\n" +
                genero;
    }
}
