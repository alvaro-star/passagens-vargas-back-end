package com.alvaro.empresas.passagens.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@Component
public class DateAuxiliarFunctions {
    @Value("${spring.jackson.time-zone}")
    private String timeZone;

    public LocalDateTime getDateWithFirstDayOfMonth(Date date) {
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

    public LocalDateTime getDateWithLastDayOfMonth(Date date) {
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
}
