package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationError;
import com.alvaro.empresas.passagens.dtos.TrayectoDTO;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.services.EmpresaService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<AutobusDTO> findAll(Pageable pageable) {
        Page<AutobusModel> models = autobusRepository.findAll(pageable);
        Page<AutobusDTO> dtos = models.map(model -> new AutobusDTO(model, model.getEmpresa().getId()));
        return dtos;
    }

    public Page<AutobusDTO> findAllFromEmpresa(Integer idEmpresa, Pageable pageable) {
        var empresa = empresaService.findById(idEmpresa);
        Page<AutobusModel> autobuses = autobusRepository.findByEmpresa(empresa, pageable);
        return autobuses.map((autobus) -> new AutobusDTO(autobus, empresa.getId()));
    }

    public AutobusDTO getOne(Integer id) {
        var model = findById(id);
        var dto = new AutobusDTO(model);
        dto.setIdEmpresa(model.getEmpresa().getId());
        List<TrayectoDTO> trayectosDto = new ArrayList<>();
        List<PisoDTOResponse> pisosDto = new ArrayList<>();
        for (TrayectoModel trayecto : model.getTrayectos()) {
            var trayectoDto = new TrayectoDTO(trayecto);
            trayectoDto.setIdAutobus(model.getId());
            trayectosDto.add(trayectoDto);
        }

        for (PisoModel piso : model.getPisos()) {
            pisosDto.add(new PisoDTOResponse(piso, model.getId()));
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
        }

        if (!bindingResult.hasFieldErrors("placa")) {
            if (autobusRepository.existsByPlaca(dto.getPlaca())) {
                err.addError("placa", "La placa ya esta registrada");
            }
        }
        /*
        int counter = 1;
        for (PisoDTOResponse pisoDto : dto.getPisos()) {
            if (pisoDto.nLinhas() == null) {
                err.addError("nLinhas" + counter, "No puede ser nulo");
            }
            if (pisoDto.nColunas() == null) {
                err.addError("nColunas" + counter, "No puede ser nulo");
            } else {
                if (pisoDto.nColunas() > 4)
                    err.addError("nColunas" + counter, "No puede ser maior que 4");
            }
            if (pisoDto.distribuicaoFileira() == null || pisoDto.distribuicaoFileira() == "") {
                err.addError("distribuicaoFileira" + counter, "No puede ser vazio");
            }
            counter++;
        }*/

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
