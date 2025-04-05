package com.alvaro.empresas.passagens.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;


public class DateTimeUtils {
    private static final String formatDate = "HH:mm - dd/MM/yyyy";

    public static LocalDateTime getFirstDayOfMonth(Integer[] anoMes) {
        var primeiroDiaMes = LocalDate.of(anoMes[0], anoMes[1], 1);
        return primeiroDiaMes.atTime(0, 0, 0, 0);
    }

    public static LocalDateTime getLastDayOfMonth(Integer[] anoMes) {
        anoMes[1] += 1;
        var primeiroDiaMes = getFirstDayOfMonth(anoMes);
        return primeiroDiaMes.minusNanos(1);
    }

    public static Integer[] splitAnoMonth(String mesAnalise) {
        String regex = "^(\\d{4})-(0[1-9]|1[0-2])$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mesAnalise);
        if (!matcher.matches())
            throw new RestRuntimeException(HttpStatus.BAD_REQUEST, "O mes precisa seguir o seguinte formato YYYY-MM");
        String[] mesAnaliseSplit = mesAnalise.split("-");
        Integer[] anoMes = new Integer[2];
        for (int i = 0; i < mesAnaliseSplit.length; i++) {
            anoMes[i] = Integer.valueOf(mesAnaliseSplit[i]);
        }
        return anoMes;

    }

    public static String formatLocalDateTime(LocalDateTime dataHora) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern(formatDate);
        return dataHora.format(formato);
    }

    public static LocalDateTime copyLocalTimeInLocalDate(LocalDate dataLocal, LocalDateTime dataHora) {
        return dataLocal.atTime(dataHora.getHour(), dataHora.getMinute(), dataHora.getSecond(), dataHora.getNano());
    }
}