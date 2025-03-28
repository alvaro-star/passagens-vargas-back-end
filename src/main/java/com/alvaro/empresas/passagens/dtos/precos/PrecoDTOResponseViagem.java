package com.alvaro.empresas.passagens.dtos.precos;

import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.models.PrecoModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PrecoDTOResponseViagem(
        UUID id,
        BigDecimal preco,
        Integer nPiso,
        Boolean cheio,
        Integer nAssentosDisponiveis,
        PisoDTOResponse piso,
        List<Integer> assentosOcupados) {
    public PrecoDTOResponseViagem(PrecoModel model, PisoDTOResponse piso, List<Integer> assentosOcupados) {
        this(model.getId(), model.getPreco(), model.getNPiso(), model.getCheio(), model.getNAssentosDisponiveis(), piso,
                assentosOcupados);
    }
}
