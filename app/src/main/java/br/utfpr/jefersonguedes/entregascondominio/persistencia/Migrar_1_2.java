package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrar_1_2 extends Migration {

    public Migrar_1_2(){
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Morador ADD COLUMN dataNascimento INTEGER");
    }
}
