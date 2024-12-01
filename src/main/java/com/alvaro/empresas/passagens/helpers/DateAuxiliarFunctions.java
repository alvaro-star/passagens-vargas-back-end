package com.alvaro.empresas.passagens.helpers;

import com.alvaro.empresas.passagens.helpers.dtos.DataHoraFormatada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@Component
public class DateAuxiliarFunctions {
    @Value("${spring.jackson.time-zone}")
    private String timeZone;

    public LocalDateTime getFirstDayOfMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        ZoneId zoneId = ZoneId.of(timeZone);
        return calendar.getTime().toInstant().atZone(zoneId).toLocalDateTime();
    }

    public LocalDateTime getLastDayOfMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        ZoneId zoneId = ZoneId.of(timeZone);
        return calendar.getTime().toInstant().atZone(zoneId).toLocalDateTime();
    }

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

    public static LocalDateTime copyLocalTimeInLocalDate(LocalDate localDate, LocalDateTime dateTime) {
        return localDate.atTime(dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.getNano());
    }
}
