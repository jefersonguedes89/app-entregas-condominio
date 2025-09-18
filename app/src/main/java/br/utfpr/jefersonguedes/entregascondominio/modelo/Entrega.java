package br.utfpr.jefersonguedes.entregascondominio.modelo;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Comparator;

@Entity(foreignKeys = {@ForeignKey(entity = Morador.class, parentColumns = "id", childColumns = "idMorador", onDelete = CASCADE)})
public class Entrega {

    public static Comparator<Entrega> ordenacaoDescrescente = new Comparator<Entrega>() {
        @Override
        public int compare(Entrega o1, Entrega o2) {
            return -1 * o1.getDiaHoraChegada().compareTo(o2.getDiaHoraChegada());
        }
    };

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long idMorador;

    @NonNull
    @ColumnInfo(index = true)
    private LocalDateTime diaHoraChegada;

    private LocalDateTime diaHoraRetirada;

    @NonNull
    private String descricao;


    public Entrega(long idMorador, @NonNull LocalDateTime diaHoraChegada, LocalDateTime diaHoraRetirada, @NonNull String descricao) {
        this.idMorador = idMorador;
        this.diaHoraChegada = diaHoraChegada;
        this.diaHoraRetirada = diaHoraRetirada;
        this.descricao = descricao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdMorador() {
        return idMorador;
    }

    public void setIdMorador(long idMorador) {
        this.idMorador = idMorador;
    }

    @NonNull
    public LocalDateTime getDiaHoraChegada() {
        return diaHoraChegada;
    }

    public void setDiaHoraChegada(@NonNull LocalDateTime diaHoraChegada) {
        this.diaHoraChegada = diaHoraChegada;
    }

    public LocalDateTime getDiaHoraRetirada() {
        return diaHoraRetirada;
    }

    public void setDiaHoraRetirada(LocalDateTime diaHoraRetirada) {
        this.diaHoraRetirada = diaHoraRetirada;
    }

    @NonNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NonNull String descricao) {
        this.descricao = descricao;
    }
}
