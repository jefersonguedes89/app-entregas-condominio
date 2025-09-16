package br.utfpr.jefersonguedes.entregascondominio.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class UtilsLocalDate {
    private UtilsLocalDate(){

    }

    public static String formatLocalDate(LocalDate date){
        if(date == null){
            return null;
        }
//        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
//        return date.format(formatter);

        String formatPattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT,
                null,
                IsoChronology.INSTANCE,
                Locale.getDefault());

        formatPattern = formatPattern.replaceAll("\\byy\\b", "yyyy");

        formatPattern = formatPattern.replaceAll("\\bM\\b", "MM");

        formatPattern = formatPattern.replaceAll("\\bd\\b", "dd");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault());

        return date.format(formatter);

    }

    public static long toMillissegundos(LocalDate date){
        if(date == null){
            return 0;
        }
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    public static int diferencaEmAnos(LocalDate date1, LocalDate date2){
        if(date1 == null || date2 == null){
            return 0;
        }
        Period periodo = Period.between(date1, date2);

        return periodo.getYears();

    }


    public static int diferencaEmAnosParaHoje(LocalDate date){
        return diferencaEmAnos(date, LocalDate.now());
    }


}
