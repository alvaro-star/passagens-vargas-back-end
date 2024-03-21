package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOList;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.models.PosicionIndisponibleModel;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AutobusService {
    @Autowired
    private AutobusRepository autobusRepository;
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private PisoService pisoService;

    public AutobusModel findById(Long id) {
        var model = autobusRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, AutobusModel.class.getName()));
    }

    public Page<AutobusDTOList> findAll(Pageable pageable) {
        Page<AutobusModel> models = autobusRepository.findAll(pageable);
        return models.map(model -> new AutobusDTOList(model, model.getEmpresa().getId()));
    }

    public Page<AutobusDTOList> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        var empresa = empresaService.findById(idEmpresa);
        Page<AutobusModel> autobuses = autobusRepository.findByEmpresa(empresa, pageable);
        return autobuses.map((autobus) -> new AutobusDTOList(autobus, empresa.getId()));
    }

    public AutobusDTOResponse getOne(Long id) {
        var model = findById(id);
        List<TrayectoDTO> trayectosDto = new ArrayList<>();
        List<PisoDTOResponse> pisosDto = new ArrayList<>();

        for (TrayectoModel trayecto : model.getTrayectos()) {
            trayectosDto.add(new TrayectoDTO(trayecto, model.getId()));
        }

        for (PisoModel piso : model.getPisos()) {
            List<Integer> bloqueadosDto = new ArrayList<>();
            for (PosicionIndisponibleModel bloqueado : piso.getPosicionesIndisponibles()) {
                bloqueadosDto.add(bloqueado.getNumero());
            }
            pisosDto.add(new PisoDTOResponse(piso, model.getId(), bloqueadosDto));
        }

        return new AutobusDTOResponse(model, model.getEmpresa().getId(), pisosDto, trayectosDto);
    }

    public ValidationError validar(BindingResult bindingResult, AutobusDTO dto) {
        ValidationError err = new ValidationError(
                System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validação",
                "Erro durante a validação",
                "/autobuses");
        for (FieldError erro : bindingResult.getFieldErrors()) {
            err.addError(erro.getField(), erro.getDefaultMessage());
        }

        if (!bindingResult.hasFieldErrors("placa")) {
            if (autobusRepository.existsByPlaca(dto.placa())) {
                err.addError("placa", "La placa ya esta registrada");
            }
        }

        int counter = 1;
        for (PisoDTO pisoDto : dto.pisos()) {
            if (pisoDto.getNLinhas() == null) {
                err.addError("nLinhas" + counter, "No puede ser nulo");
            }
            if (pisoDto.getNColunas() == null) {
                err.addError("nColunas" + counter, "No puede ser nulo");
            } else {
                if (pisoDto.getNColunas() > 4)
                    err.addError("nColunas" + counter, "No puede ser maior que 4");
            }
            if (pisoDto.getDistribuicaoFileira() == null || pisoDto.getDistribuicaoFileira() == "") {
                err.addError("distribuicaoFileira" + counter, "No puede ser vazio");
            }
            counter++;
        }

        return err;
    }

    @Transactional
    public AutobusDTOResponse salvar(AutobusDTO dto) {
        EmpresaModel empresa = empresaService.findById(dto.idEmpresa());
        var model = new AutobusModel(dto);
        model.setEmpresa(empresa);
        var save = autobusRepository.save(model);

        List<PisoDTOResponse> pisosGuardados = new ArrayList<>();
        pisosGuardados.add(pisoService.salvar(dto.pisos().get(0), save, 1, 1));
        if (dto.pisos().size() == 2) {
            var primeraSilla = pisosGuardados.get(0).nSillas() + 1;
            pisosGuardados.add(pisoService.salvar(dto.pisos().get(1), save, 2, primeraSilla));
        }

        return new AutobusDTOResponse(save, save.getEmpresa().getId(), pisosGuardados);
    }

    public AutobusDTOList update(AutobusDTOUpdate dto, Long id) {
        var model = this.findById(id);
        model.updateValues(dto);
        var update = autobusRepository.save(model);
        return new AutobusDTOList(update, update.getEmpresa().getId());
    }

    @Transactional
    public void delete(AutobusModel model) {
        autobusRepository.delete(model);
    }
}
