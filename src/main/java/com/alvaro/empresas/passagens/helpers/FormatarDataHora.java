package com.alvaro.empresas.passagens.helpers;

import com.alvaro.empresas.passagens.helpers.dtos.DataHoraFormatada;

import java.time.LocalDateTime;

public class FormatarDataHora {


    public static DataHoraFormatada getDataHoraToString(LocalDateTime dataHora) {
        if (dataHora == null)
            throw new IllegalArgumentException("A data e hora não podem ser nulas.");

        StringBuilder dataBuilder = new StringBuilder();
        StringBuilder horaBuilder = new StringBuilder();

        dataBuilder.append(String.format("%02d", dataHora.getDayOfMonth()))
                .append("/")
                .append(String.format("%02d", dataHora.getMonthValue()))
                .append("/")
                .append(dataHora.getYear());
        
        horaBuilder.append(String.format("%02d", dataHora.getHour()))
                .append(":")
                .append(String.format("%02d", dataHora.getMinute()));

        return new DataHoraFormatada(dataBuilder.toString(), horaBuilder.toString());
    }

}


