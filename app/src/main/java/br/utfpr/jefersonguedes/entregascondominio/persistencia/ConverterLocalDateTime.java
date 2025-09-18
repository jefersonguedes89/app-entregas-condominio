package br.utfpr.jefersonguedes.entregascondominio.persistencia;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ConverterLocalDateTime {
    @TypeConverter
    public static Long fromLocalDateTimeToLong(LocalDateTime dateTime){
        if(dateTime == null){
            return null;
        }

        return dateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

    }

    @TypeConverter
    public static LocalDateTime fromLongToLocalDateTime(Long epochMili){
        if(epochMili == null){
            return null;
        }

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMili), ZoneOffset.UTC);
    }

}
