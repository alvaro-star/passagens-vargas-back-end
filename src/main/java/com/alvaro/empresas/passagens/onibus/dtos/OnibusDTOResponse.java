package com.alvaro.empresas.passagens.onibus.dtos;

import com.alvaro.empresas.passagens.onibus.models.OnibusModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record OnibusDTOResponse(
        UUID id,
        String placa,
        Boolean habilitado,
        BigDecimal valorViagensDinheiro,
        BigDecimal valorViagensNaoWeb,
        BigDecimal valorViagensWeb,
        UUID idEmpresa,
        List<PisoResponseDTO> pisos) {

    public OnibusDTOResponse(
            OnibusModel modelo,
            BigDecimal valorViagensDinheiro,
            BigDecimal valorViagensNaoWeb,
            BigDecimal valorViagensWeb,
            List<PisoResponseDTO> pisos
    ) {
        this(modelo.getId(),
                modelo.getPlaca(),
                modelo.isEnabled(),
                valorViagensDinheiro,
                valorViagensNaoWeb,
                valorViagensWeb,
                modelo.getEmpresaId(),
                pisos);
    }

    public OnibusDTOResponse(OnibusModel modelo, List<PisoResponseDTO> pisos) {
        this(modelo, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, pisos);
    }

    public OnibusDTOResponse(OnibusModel modelo) {
        this(modelo, new ArrayList<>());
    }
}