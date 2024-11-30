package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.autobuses.services.validacao.ValidarPiso;
import com.alvaro.empresas.passagens.configurations.exceptions.InternalException.GeneralException;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import com.alvaro.empresas.passagens.services.EmpresaService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
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
    private EmpresaEnabled empresaEnabled;
    @Autowired
    private PisoService pisoService;
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private ValidarPiso validarPiso;

    public AutobusModel findById(Integer id) {
        var model = autobusRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, AutobusModel.class.getName()));
    }

    public Page<AutobusDTOResponse> findAll(Pageable pageable) {
        Page<AutobusModel> models = autobusRepository.findAll(pageable);
        return models.map(AutobusDTOResponse::new);
    }

    public Page<AutobusDTOResponse> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        var empresa = empresaService.findById(idEmpresa);
        Page<AutobusModel> autobuses = autobusRepository.findByEmpresaId(empresa.getId(), pageable);
        return autobuses.map(AutobusDTOResponse::new);
    }

    public AutobusDTOResponse getOne(Integer id) {
        var model = findById(id);
        List<PisoDTOResponse> pisosDto = new ArrayList<>();
        for (PisoModel piso : model.getPisos())
            pisosDto.add(new PisoDTOResponse(piso));
        return new AutobusDTOResponse(model, pisosDto);
    }


    @Transactional
    public AutobusDTOResponse salvar(AutobusDTO dto, BindingResult bindingResult) {
        empresaEnabled.validEmpresaEnabled(dto.idEmpresa());
        validarPiso.validarAutobusDTO(bindingResult, dto);

        var empresa = empresaService.findById(dto.idEmpresa());
        var model = new AutobusModel(dto);

        model.setEmpresa(empresa);
        autobusRepository.save(model);

        List<PisoDTOResponse> pisosGuardados = new ArrayList<>();
        pisosGuardados.add(pisoService.salvar(dto.pisos().get(0), model, 1, 1));
        if (dto.pisos().size() == 2) {
            var primeraSilla = pisosGuardados.get(0).nSillas() + 1;
            pisosGuardados.add(pisoService.salvar(dto.pisos().get(1), model, 2, primeraSilla));
        }

        return new AutobusDTOResponse(model, pisosGuardados);
    }

    public AutobusDTOResponse update(AutobusDTOUpdate dto, AutobusModel model, BindingResult bindingResult) {
        empresaEnabled.validEmpresaEnabled(model.getEmpresaId());
        var transform = new AutobusDTO(dto.placa());
        validarPiso.validarAutobusDTO(bindingResult, transform);
        model.updateValues(dto);
        autobusRepository.save(model);
        return new AutobusDTOResponse(model);
    }

    @Transactional
    public void delete(AutobusModel model) {
        empresaEnabled.validEmpresaEnabled(model.getEmpresaId());
        var now = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, 1);
        Page<ViajeModel> viajesFuturos = viajeRepository.findAfterDate(model.getEmpresa().getId(), now, pageable);

        var viaje = viajeRepository.findFirst1ByAutobusId(model.getId());
        if (viaje.isEmpty())
            autobusRepository.delete(model);
        else if (viajesFuturos.getTotalElements() == 0) {
            model.setEnable(false);
            autobusRepository.save(model);
        } else
            throw new GeneralException(HttpStatus.BAD_REQUEST, "El atobus tiene un viaje programado en el futuro");
    }
}
