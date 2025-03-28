package com.alvaro.empresas.passagens.onibus.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTO;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTOResponse;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTOUpdate;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.onibus.repositories.OnibusRepository;
import com.alvaro.empresas.passagens.onibus.services.validacao.ValidarPiso;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
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
public class OnibusService {
    @Autowired
    private OnibusRepository onibusRepository;
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private PisoService pisoService;
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private ValidarPiso validarPiso;

    public OnibusModel findById(UUID id) {
        var model = onibusRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, OnibusModel.class.getName()));
    }

    public Page<OnibusDTOResponse> findAll(Pageable pageable) {
        Page<OnibusModel> models = onibusRepository.findAll(pageable);
        return models.map(OnibusDTOResponse::new);
    }

    public Page<OnibusDTOResponse> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        var empresa = empresaService.findById(idEmpresa);
        Page<OnibusModel> onibus = onibusRepository.findByEmpresaId(empresa.getId(), pageable);
        return onibus.map(OnibusDTOResponse::new);
    }

    public OnibusDTOResponse getOne(UUID id) {
        var model = findById(id);
        var pisosDto = model.getPisos().stream().map(PisoDTOResponse::new).toList();
        return new OnibusDTOResponse(model, pisosDto);
    }

    @Transactional
    public OnibusDTOResponse salvar(OnibusDTO dto, BindingResult bindingResult) {
        validarPiso.validarOnibusDTO(bindingResult, dto);
        var empresa = empresaService.findById(dto.idEmpresa());
        ValidEnabledEntities.validEmpresa(empresa);
        var model = new OnibusModel(dto, empresa);

        onibusRepository.save(model);
        List<PisoDTOResponse> pisosSalvos = new ArrayList<>();
        pisosSalvos.add(pisoService.salvar(dto.pisos().get(0), model, 1, 1));
        if (dto.pisos().size() == 2) {
            var primeiroAssento = pisosSalvos.get(0).nAssentos() + 1;
            pisosSalvos.add(pisoService.salvar(dto.pisos().get(1), model, 2, primeiroAssento));
        }
        return new OnibusDTOResponse(model, pisosSalvos);
    }

    public OnibusDTOResponse update(OnibusDTOUpdate dto, OnibusModel model, BindingResult bindingResult) {
        var empresa = empresaService.findById(model.getEmpresaId());
        ValidEnabledEntities.validEmpresa(empresa);
        var transform = new OnibusDTO(dto.placa());
        validarPiso.validarOnibusDTO(bindingResult, transform);
        model.updateValues(dto);
        onibusRepository.save(model);
        return new OnibusDTOResponse(model);
    }

    @Transactional
    public void delete(OnibusModel model) {
        var empresa = empresaService.findById(model.getEmpresaId());
        ValidEnabledEntities.validEmpresa(empresa);
        var agora = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, 1);
        Page<ViagemModel> viagensFuturas = viagemRepository.findAfterDate(model.getEmpresa().getId(), agora, pageable);

        var viagem = viagemRepository.findFirst1ByOnibusId(model.getId());
        if (viagem.isEmpty())
            onibusRepository.delete(model);
        else if (viagensFuturas.getTotalElements() == 0) {
            model.setHabilitado(false);
            onibusRepository.save(model);
        } else
            throw new RestRuntimeException(HttpStatus.BAD_REQUEST, "O ônibus tem uma viagem programada no futuro");
    }
}