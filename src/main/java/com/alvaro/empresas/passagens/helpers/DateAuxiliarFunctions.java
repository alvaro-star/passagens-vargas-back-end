package com.alvaro.empresas.passagens.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.helpers.dtos.DataHoraFormatada;

@Component
public class DateAuxiliarFunctions {
    @Value("${spring.jackson.time-zone}")
    private String fusoHorario;

    public LocalDateTime getFirstDayOfMonth(Integer[] anoMes) {
        var primeiroDiaMes = LocalDate.of(anoMes[0], anoMes[1], 1);
        return primeiroDiaMes.atTime(0, 0, 0, 0);
    }

    public LocalDateTime getLastDayOfMonth(Integer[] anoMes) {
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

    public static DataHoraFormatada getDataHoraFromDateTime(LocalDateTime dataHora) {
        if (dataHora == null)
            throw new IllegalArgumentException("A data e hora não podem ser nulas.");

        StringBuilder construtorData = new StringBuilder();
        StringBuilder construtorHora = new StringBuilder();

        construtorData.append(String.format("%02d", dataHora.getDayOfMonth()))
                .append("/")
                .append(String.format("%02d", dataHora.getMonthValue()))
                .append("/")
                .append(dataHora.getYear());

        construtorHora.append(String.format("%02d", dataHora.getHour()))
                .append(":")
                .append(String.format("%02d", dataHora.getMinute()));

        return new DataHoraFormatada(construtorData.toString(), construtorHora.toString());
    }

    public static LocalDateTime copyLocalTimeInLocalDate(LocalDate dataLocal, LocalDateTime dataHora) {
        return dataLocal.atTime(dataHora.getHour(), dataHora.getMinute(), dataHora.getSecond(), dataHora.getNano());
    }
}