package br.utfpr.jefersonguedes.entregascondominio.modelo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Objects;

@Entity
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

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(index = true)
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    public Object clone() throws CloneNotSupportedException {



        return super.clone();
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Morador morador = (Morador) o;
        return numApt == morador.numApt
                && aptAlugado == morador.aptAlugado
                && bloco == morador.bloco
                && nome.equals(morador.nome)
                && genero == morador.genero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, numApt, aptAlugado, bloco, genero);
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
