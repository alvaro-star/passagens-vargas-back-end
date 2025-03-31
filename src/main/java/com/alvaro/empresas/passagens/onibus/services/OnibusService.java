package com.alvaro.empresas.passagens.onibus.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusCreateDTO;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTOResponse;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusUpdateDTO;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoCreateDTO;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoResponseDTO;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.onibus.repositories.OnibusRepository;
import com.alvaro.empresas.passagens.onibus.services.validacao.ValidarPiso;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.services.EmpresaService;
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
    private EmpresaRepository empresaRepository;
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private ValidarPiso validarPiso;
    @Autowired
    private UserLoguedComponent userLogued;

    public OnibusDTOResponse findById(UUID id) {
        var model = onibusRepository.findByIdOrThr(id);
        var pisosDTO = model.getPisos().stream().map(PisoResponseDTO::new).toList();
        return new OnibusDTOResponse(model, pisosDTO);
    }

    public Page<OnibusDTOResponse> findAll(Pageable pageable) {
        Page<OnibusModel> models = onibusRepository.findAll(pageable);
        return models.map(OnibusDTOResponse::new);
    }

    public Page<OnibusDTOResponse> findByEmpresaId(UUID idEmpresa, Pageable pageable) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        Page<OnibusModel> onibus = onibusRepository.findByEmpresaId(idEmpresa, pageable);
        return onibus.map(OnibusDTOResponse::new);
    }

    @Transactional
    public OnibusDTOResponse save(OnibusCreateDTO dto) {
        var empresa = empresaRepository.findByIdOrThr(dto.idEmpresa());
        userLogued.validIfIsMyEmpresa(empresa.getId());
        ValidEnabledEntities.validEmpresa(empresa);
        validarPiso.validarOnibusDTO(dto);

        var model = new OnibusModel(dto, empresa);
        validarPlaca(dto.placa());

        dto.pisos().forEach(pisoDTO -> {
            var nPisos = (model.getPisos() != null) ? model.getPisos().size() : 0;
            int nPrimeiroAssento = (nPisos == 0) ? 1 : model.getPisos().get(nPisos - 1).getNAssentos() + 1;
            var pisoModel = new PisoModel(pisoDTO, nPisos + 1, nPrimeiroAssento);
            model.addPiso(pisoModel);
        });
        onibusRepository.save(model);

        var pisosDTO = model.getPisos().stream().map(PisoResponseDTO::new).toList();
        return new OnibusDTOResponse(model, pisosDTO);
    }

    public OnibusDTOResponse update(UUID id, OnibusUpdateDTO dto) {
        var model = onibusRepository.findByIdOrThr(id);
        userLogued.validIfIsMyEmpresa(model.getEmpresaId());

        ValidEnabledEntities.validEmpresa(model.getEmpresa());
        ValidEnabledEntities.validOnibus(model);

        if (!model.getPlaca().equals(dto.placa()))
            validarPlaca(dto.placa());

        model.updateValues(dto);
        onibusRepository.save(model);

        var pisosDTO = model.getPisos().stream().map(PisoResponseDTO::new).toList();
        return new OnibusDTOResponse(model, pisosDTO);
    }

    @Transactional
    public void delete(UUID id) {
        var model = onibusRepository.findByIdOrThr(id);
        userLogued.validIfIsMyEmpresa(model.getEmpresaId());

        var empresa = model.getEmpresa();
        ValidEnabledEntities.validEmpresa(empresa);
        var agora = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, 1);
        Page<ViagemModel> viagensFuturas = viagemRepository.findAfterDate(model.getEmpresa().getId(), agora, pageable);

        var viagem = viagemRepository.findFirst1ByOnibusId(model.getId());
        if (viagem.isEmpty())
            onibusRepository.delete(model);
        else if (viagensFuturas.getTotalElements() == 0) {
            model.setEnabled(false);
            onibusRepository.save(model);
        } else
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O ônibus tem uma viagem programada no futuro");
    }

    private void validarPlaca(String placa) {
        var existe = onibusRepository.existsByPlaca(placa);
        if (existe)
            throw new ValidationException("placa", "A placa já esta registrada");
    }
}