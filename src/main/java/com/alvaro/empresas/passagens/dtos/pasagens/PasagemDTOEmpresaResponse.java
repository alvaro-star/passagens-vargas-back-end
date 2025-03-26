package com.alvaro.empresas.passagens.dtos.pasagens;

import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.util.Date;
import java.util.UUID;

public record PasagemDTOEmpresaResponse(
        UUID id,
        String carnet,
        String nombre,
        Date nascimento,
        Boolean rembolsado,
        Boolean pagado,
        Integer nSilla,
        ParadaDTOComplete salida,
        ParadaDTOComplete destino
) {
    public PasagemDTOEmpresaResponse(PassagemModel model) {
        this(
                model.getId(),
                model.getDocumento(),
                model.getNome(),
                model.getNascimento(),
                model.getFaturaReembolsoId() != null,
                model.getEstaPago(),
                model.getNumeroAssento(),
                new ParadaDTOComplete(model.getSaida()),
                new ParadaDTOComplete(model.getDestino()));
    }
}
