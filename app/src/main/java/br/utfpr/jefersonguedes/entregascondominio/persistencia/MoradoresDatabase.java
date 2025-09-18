package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Entrega;
import br.utfpr.jefersonguedes.entregascondominio.modelo.Morador;

@Database(entities = {Morador.class, Entrega.class}, version = 3)
@TypeConverters({ConverterGenero.class, ConverterLocalDate.class, ConverterLocalDateTime.class})
public abstract class MoradoresDatabase extends RoomDatabase {
    public abstract MoradorDao getMoradorDao();

    public abstract EntregaDao getEntregaDao();

    private static MoradoresDatabase INSTANCE;

    public static MoradoresDatabase getInstance(final Context context) {
        if (INSTANCE == null) {  // <-- corrige aqui
            synchronized (MoradoresDatabase.class) {
                if (INSTANCE == null) {  // <-- corrige aqui também
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                                    MoradoresDatabase.class,
//                                    "moradores.db")
//                            .allowMainThreadQueries()
//                            .build();

                    Builder builder = Room.databaseBuilder(context, MoradoresDatabase.class, "moradores.db");
                    builder.allowMainThreadQueries();
                    builder.addMigrations(new Migrar_1_2());

                    builder.addMigrations(new Migrar_2_3());
                    INSTANCE = (MoradoresDatabase) builder.build();


                }
            }
        }
        return INSTANCE;
    }

}
