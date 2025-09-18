package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrar_2_3 extends Migration {

    public Migrar_2_3(){
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `Entrega` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `idMorador` INTEGER NOT NULL, `diaHoraChegada` INTEGER NOT NULL, `diaHoraRetirada` INTEGER, `descricao` TEXT NOT NULL, FOREIGN KEY(`idMorador`) REFERENCES `Morador`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Entrega_diaHoraChegada` ON `Entrega` (`diaHoraChegada`)");
    }
}
