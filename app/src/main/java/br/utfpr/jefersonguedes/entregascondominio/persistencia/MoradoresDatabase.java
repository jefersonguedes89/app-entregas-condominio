package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Morador;

@Database(entities = {Morador.class}, version = 1, exportSchema = false)
public abstract class MoradoresDatabase extends RoomDatabase {
    public abstract MoradorDao geMoradorDao();

    private static MoradoresDatabase INSTANCE;

    public static MoradoresDatabase getInstance(final Context context){
        if(INSTANCE != null){
            synchronized (MoradoresDatabase.class){
                if(INSTANCE != null){
                    INSTANCE = Room.databaseBuilder(context,
                                                    MoradoresDatabase.class,
                                              "moradores.db").allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

}
