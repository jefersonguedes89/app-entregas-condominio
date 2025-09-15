package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Morador;

@Dao
public interface MoradorDao {

    @Insert
    long insert(Morador morador);

    @Delete
    int delete(Morador morador);

    @Update
    int update(Morador morador);

    @Query("SELECT * FROM morador WHERE id =:id")
    Morador queryForId(long id);

    @Query("SELECT * FROM morador order by nome ASC")
    List<Morador> queryAllAscending();

    @Query("SELECT * FROM morador order by nome DESC")
    List<Morador> queryAllDownward();

}
