package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationError;
import com.alvaro.empresas.passagens.dtos.TrayectoDto;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.services.EmpresaService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Service
public class AutobusService {
    @Autowired
    private AutobusRepository autobusRepository;
    @Autowired
    private EmpresaService empresaService;

    public AutobusModel findById(Integer id) {
        var model = autobusRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, AutobusModel.class.getName()));
    }

    public List<AutobusDTO> findAll() {
        List<AutobusDTO> dtos = new ArrayList<>();
        List<AutobusModel> models = autobusRepository.findAll();
        models.forEach(model -> {
            var dto = new AutobusDTO(model);
            dto.setIdEmpresa(model.getEmpresa().getId());
            dtos.add(dto);
        });
        return dtos;
    }

    public AutobusDTO getOne(Integer id) {
        var model = findById(id);
        var dto = new AutobusDTO(model);
        dto.setIdEmpresa(model.getEmpresa().getId());
        List<TrayectoDto> trayectosDto = new ArrayList<>();
        List<PisoDTO> pisosDto = new ArrayList<>();
        for (TrayectoModel trayecto : model.getTrayectos()) {
            var trayectoDto = new TrayectoDto(trayecto);
            trayectoDto.setIdAutobus(model.getId());
            trayectosDto.add(trayectoDto);
        }

        for (PisoModel piso : model.getPisos()) {
            var pisoDTO = new PisoDTO(piso);
            pisoDTO.setIdAutobus(model.getId());
            pisosDto.add(pisoDTO);
        }
        dto.setTrayectos(trayectosDto);
        dto.setPisos(pisosDto);
        return dto;
    }

    public ValidationError validar(BindingResult bindingResult, AutobusDTO dto) {
        ValidationError err = new ValidationError(
                System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de Validacao",
                "Erro durante a validacao",
                "/autobuses");
        for (FieldError erro : bindingResult.getFieldErrors()) {
            err.addError(erro.getField(), erro.getDefaultMessage());
            System.out.println("\n" + erro.getField() + "Steve" + erro.getDefaultMessage());
        }
        if (!bindingResult.hasFieldErrors("placa")) {
            if (autobusRepository.existsByPlaca(dto.getPlaca())) {
                err.addError("placa", "La placa ya esta registrada");
            }
        }

        return err;
    }

    public AutobusDTO save(AutobusDTO dto) {
        EmpresaModel empresa = empresaService.findById(dto.getIdEmpresa());
        var model = new AutobusModel(dto);
        model.setEmpresa(empresa);

        var save = autobusRepository.save(model);
        var dtoSave = new AutobusDTO(save);
        dtoSave.setIdEmpresa(save.getEmpresa().getId());
        return dtoSave;
    }

    public AutobusDTO update(AutobusDTOUpdate dto, Integer id) {
        var model = this.findById(id);
        model.updateValues(dto);
        var update = autobusRepository.save(model);
        var updateDto = new AutobusDTO(update);
        updateDto.setIdEmpresa(update.getEmpresa().getId());
        return updateDto;
    }

    public void delete(AutobusModel model) {
        autobusRepository.delete(model);
    }

}
