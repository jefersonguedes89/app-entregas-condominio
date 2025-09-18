package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import androidx.room.TypeConverter;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Genero;

public class ConverterGenero {
    public static Genero[] generos = Genero.values();
    @TypeConverter
    public static int fromEnumToInt(Genero genero){
        if(genero == null){
            return -1;
        }
        return genero.ordinal();
    }


    @TypeConverter
    public static Genero fromIntToEnum(int ordinal){
        if(ordinal < 0){
            return null;
        }

        return generos[ordinal];

    }

}
