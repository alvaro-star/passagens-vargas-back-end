package com.alvaro.empresas.passagens.autobuses.services;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOList;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOResponse;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import com.alvaro.empresas.passagens.services.EmpresaService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AutobusService {
    private final AutobusRepository autobusRepository;
    private final EmpresaService empresaService;
    private final PisoService pisoService;
    private final ViajeRepository viajeRepository;

    @Autowired
    public AutobusService(
            AutobusRepository autobusRepository,
            EmpresaService empresaService,
            PisoService pisoService,
            ViajeRepository viajeRepository
    ) {
        this.autobusRepository = autobusRepository;
        this.empresaService = empresaService;
        this.pisoService = pisoService;
        this.viajeRepository = viajeRepository;
    }

    public AutobusModel findById(Integer id) {
        var model = autobusRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, AutobusModel.class.getName()));
    }

    public Page<AutobusDTOList> findAll(Pageable pageable) {
        Page<AutobusModel> models = autobusRepository.findAll(pageable);
        return models.map(model ->
                new AutobusDTOList(model, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, model.getEmpresa().getId())
        );
    }

    public Page<AutobusDTOList> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        var empresa = empresaService.findById(idEmpresa);
        Page<AutobusModel> autobuses = autobusRepository.findByEmpresaId(empresa.getId(), pageable);
        return autobuses.map((autobus) -> new AutobusDTOList(autobus,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                empresa.getId())
        );
    }

    public AutobusDTOResponse getOne(Integer id) {
        var model = findById(id);
        List<PisoDTOResponse> pisosDto = new ArrayList<>();
        for (PisoModel piso : model.getPisos())
            pisosDto.add(new PisoDTOResponse(piso, model.getId()));

        return new AutobusDTOResponse(model, model.getEmpresa().getId(), pisosDto);
    }


    @Transactional
    public AutobusDTOResponse salvar(AutobusDTO dto, EmpresaModel empresa) {
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

    public AutobusDTOList update(AutobusDTOUpdate dto, AutobusModel model) {
        model.updateValues(dto);
        var update = autobusRepository.save(model);
        return new AutobusDTOList(update,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                update.getEmpresa().getId());
    }

    @Transactional
    public String delete(AutobusModel model) {
        var now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 1);
        Page<ViajeModel> viajesFuturos = viajeRepository.findViajesFuturos(model.getEmpresa().getId(), now, pageable);

        var viaje = viajeRepository.findFirst1ByAutobusId(model.getId());
        if (viaje.isEmpty())
            autobusRepository.delete(model);
        else {
            if (viajesFuturos.getTotalElements() == 0) {
                model.setEnable(false);
                autobusRepository.save(model);
            } else return "El atobus tiene un viaje programado en el futuro";
        }
        return "";
    }
}
