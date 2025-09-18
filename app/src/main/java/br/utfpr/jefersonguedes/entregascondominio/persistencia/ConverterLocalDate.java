package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class ConverterLocalDate {

    @TypeConverter
    public static Long fromLocalDateToLong(LocalDate localDate){
        if(localDate == null){
            return null;
        }
        return localDate.toEpochDay();
    }

    @TypeConverter
    public static LocalDate fromLongToLocalDate(Long epochDay){
        if(epochDay == null){
            return null;
        }
        return LocalDate.ofEpochDay(epochDay);
    }



}
