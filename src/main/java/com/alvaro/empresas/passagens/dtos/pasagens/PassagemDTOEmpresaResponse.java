package com.alvaro.empresas.passagens.dtos.pasagens;

import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record PassagemDTOEmpresaResponse(
        UUID id,
        String cpf,
        String nome,
        LocalDate nascimento,
        Boolean estaRempolsado,
        Boolean estaPago,
        Integer nSilla,
        ParadaResponseDTO saida,
        ParadaResponseDTO destino) {
    public PassagemDTOEmpresaResponse(PassagemModel model) {
        this(
                model.getId(),
                model.getCpf(),
                model.getNome(),
                model.getNascimento(),
                model.getFaturaReembolsoId() != null,
                model.getEstaPago(),
                model.getNAssento(),
                new ParadaResponseDTO(model.getSaida()),
                new ParadaResponseDTO(model.getDestino()));
    }
}
