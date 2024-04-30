package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.EmpresaDto;
import com.alvaro.empresas.passagens.dtos.EmpresaResponseDto;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;

    public EmpresaModel findById(UUID id) {
        Optional<EmpresaModel> model = empresaRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, EmpresaModel.class.getName()));
    }

    public EmpresaResponseDto getOne(UUID id) {
        EmpresaModel model = this.findById(id);
        Object[] valorArrecadado = empresaRepository.getArrecadacao(id);
        return new EmpresaResponseDto(model, (BigDecimal) valorArrecadado[0], (BigDecimal) valorArrecadado[0]);
    }

    public Page<EmpresaResponseDto> findAll(Pageable pageable) {
        return empresaRepository.findAll(pageable).map(model -> {
            Object[] valorArrecadado = empresaRepository.getArrecadacao(model.getId());
            return new EmpresaResponseDto(model, (BigDecimal) valorArrecadado[0], (BigDecimal) valorArrecadado[0]);
        });
    }

    @Transactional
    public EmpresaResponseDto save(EmpresaDto dto) {
        var model = new EmpresaModel();
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        var modelSaved = empresaRepository.save(model);
        return new EmpresaResponseDto(modelSaved, new BigDecimal("00.0"), new BigDecimal("00.0"));
    }

    public EmpresaResponseDto update(EmpresaDto dto, UUID id) {
        var model = this.findById(id);
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        Object[] valorArrecadado = empresaRepository.getArrecadacao(id);
        EmpresaModel update = empresaRepository.save(model);
        return new EmpresaResponseDto(update, (BigDecimal) valorArrecadado[0], (BigDecimal) valorArrecadado[1]);
    }

    @Transactional
    public void delete(UUID id) {
        var model = this.findById(id);
        empresaRepository.delete(model);
    }

}
