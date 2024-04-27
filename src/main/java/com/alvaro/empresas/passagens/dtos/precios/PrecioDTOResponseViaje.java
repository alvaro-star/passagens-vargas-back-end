package com.alvaro.empresas.passagens.dtos.precios;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.models.PrecioModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PrecioDTOResponseViaje(
        UUID id,
        BigDecimal precio,
        Integer nPiso,
        Boolean lleno,
        Integer nSillasDisponibles,
        PisoDTOResponse piso,
        List<Integer> sillasOcupadas
) {
    public PrecioDTOResponseViaje(PrecioModel model, PisoDTOResponse piso, List<Integer> sillasOcupadas) {

        this(model.getId(), model.getPrecio(), model.getNPiso(), model.getLleno(), model.getNSillasDisponibles(), piso, sillasOcupadas);
    }
}
