package com.alvaro.empresas.passagens.dtos.viagens;

import com.alvaro.empresas.passagens.configuracoes.validations.groups.IClientCommonUser;
import com.alvaro.empresas.passagens.configuracoes.validations.groups.IEmpresaUser;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ViagemSolicitacaoDTO(
        @NotNull(groups = {IEmpresaUser.class, IClientCommonUser.class})
        Integer idCidadeSaida,
        @NotNull(groups = IClientCommonUser.class)
        Integer idCidadeDestino,
        @NotNull(groups = {IEmpresaUser.class, IClientCommonUser.class})
        @FutureOrPresent(groups = IClientCommonUser.class)
        LocalDate dataSaida) {
}
