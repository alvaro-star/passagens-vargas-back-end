package com.alvaro.empresas.passagens.onibus.dtos.onibus;

import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record OnibusDTOResponse(
        Integer id,
        String placa,
        Boolean habilitado,
        BigDecimal valorViagensDinheiro,
        BigDecimal valorViagensNaoWeb,
        BigDecimal valorViagensWeb,
        UUID idEmpresa,
        List<PisoDTOResponse> pisos
) {

    public OnibusDTOResponse(OnibusModel modelo,
                             BigDecimal valorViagensDinheiro,
                             BigDecimal valorViagensNaoWeb,
                             BigDecimal valorViagensWeb,
                             List<PisoDTOResponse> pisos) {
        this(modelo.getId(),
                modelo.getPlaca(),
                modelo.isEnable(),
                valorViagensDinheiro,
                valorViagensNaoWeb,
                valorViagensWeb,
                modelo.getEmpresaId(),
                pisos);
    }

    public OnibusDTOResponse(OnibusModel modelo, List<PisoDTOResponse> pisos) {
        this(modelo, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, pisos);
    }

    public OnibusDTOResponse(OnibusModel modelo) {
        this(modelo, new ArrayList<>());
    }
}