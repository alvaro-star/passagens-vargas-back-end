package com.alvaro.empresas.passagens.autobuses.dtos;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.dtos.TrayectoDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AutobusDTO {
    private Integer id;
    @NotBlank(message = "no puede ser nulo")
    private String placa;

    @NotNull(message = "nao pode ser nulo")
    private Integer idEmpresa;

    private List<PisoDTO> pisos;
    private List<TrayectoDto> trayectos;

    public AutobusDTO(AutobusModel model) {
        id = model.getId();
        placa = model.getPlaca();
    }
}
