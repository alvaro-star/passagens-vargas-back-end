package com.alvaro.empresas.passagens.dtos.pasagens;

import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;

import java.util.Date;
import java.util.UUID;

public record PassagemDTOEmpresaResponse(
        UUID id,
        String documento,
        String nome,
        Date nascimento,
        Boolean estaRempolsado,
        Boolean estaPago,
        Integer nSilla,
        ParadaResponseDTO saida,
        ParadaResponseDTO destino) {
    public PassagemDTOEmpresaResponse(PassagemModel model) {
        this(
                model.getId(),
                model.getDocumento(),
                model.getNome(),
                model.getNascimento(),
                model.getFaturaReembolsoId() != null,
                model.getEstaPago(),
                model.getNAssento(),
                new ParadaResponseDTO(model.getSaida()),
                new ParadaResponseDTO(model.getDestino()));
    }
}
