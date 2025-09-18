package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Entrega;

@Dao
public interface EntregaDao {

    @Insert
    long insert(Entrega entrega);

    @Delete
    int delete (Entrega entrega);

    @Update
    int update(Entrega entrega);

    @Query("SELECT * FROM Entrega WHERE id = :id")
    Entrega queryForId(long id);

    @Query("SELECT * FROM Entrega WHERE idMorador = :idMorador ORDER BY diaHoraChegada DESC")
    List<Entrega> queryForIdMorador(long idMorador);

    @Query("SELECT count(*) FROM Entrega WHERE idMorador = :idMorador")
    int totalIdMorador(long idMorador);

}
