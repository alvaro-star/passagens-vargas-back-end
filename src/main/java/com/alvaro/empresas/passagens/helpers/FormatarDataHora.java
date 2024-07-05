package com.alvaro.empresas.passagens.helpers;

import com.alvaro.empresas.passagens.helpers.dtos.DataHoraFormatada;

import java.time.LocalDateTime;

public class FormatarDataHora {


    public static DataHoraFormatada getDataHoraToString(LocalDateTime dataHora) {
        String data = "", hora = "";
        if (dataHora.getDayOfMonth() < 10)
            data = data + "0" + dataHora.getDayOfMonth();
        else
            data = data + dataHora.getDayOfMonth();
        data = data + "/";
        if (dataHora.getMonthValue() < 10)
            data = data + "0" + dataHora.getMonthValue();
        else
            data = data + dataHora.getMonthValue();
        data = data + "/" + dataHora.getYear();

        if (dataHora.getHour() < 10)
            hora = hora + "0" + dataHora.getHour();
        else
            hora = hora + dataHora.getHour();
        data = data + ":";
        if (dataHora.getMinute() < 10)
            hora = hora + "0" + dataHora.getMinute();
        else
            hora = hora + dataHora.getMinute();
        return new DataHoraFormatada(data, hora);

    }
}


