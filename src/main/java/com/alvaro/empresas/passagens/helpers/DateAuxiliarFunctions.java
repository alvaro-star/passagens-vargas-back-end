package com.alvaro.empresas.passagens.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alvaro.empresas.passagens.helpers.dtos.DataHoraFormatada;

@Component
public class DateAuxiliarFunctions {
    @Value("${spring.jackson.time-zone}")
    private String fusoHorario;

    public LocalDateTime getFirstDayOfMonth(LocalDate dataLocal) {
        var primeiroDiaMes = LocalDate.of(dataLocal.getYear(), dataLocal.getMonthValue(), 1);
        return primeiroDiaMes.atTime(0, 0, 0, 0);
    }

    public LocalDateTime getLastDayOfMonth(LocalDate dataLocal) {
        var primeiroDiaMes = getFirstDayOfMonth(dataLocal.plusMonths(1));
        return primeiroDiaMes.minusNanos(1);
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