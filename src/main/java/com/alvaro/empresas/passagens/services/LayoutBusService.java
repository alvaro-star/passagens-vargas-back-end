package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.AsientoBloqueadoDTO;
import com.alvaro.empresas.passagens.dtos.LayoutBusDTO;
import com.alvaro.empresas.passagens.enums.autobus.EnumPosicao;
import com.alvaro.empresas.passagens.enums.autobus.EnumTipoBus;
import com.alvaro.empresas.passagens.models.AsientoBloqueadoModel;
import com.alvaro.empresas.passagens.models.LayoutBusModel;
import com.alvaro.empresas.passagens.repositories.AsientoBloqueadoRepository;
import com.alvaro.empresas.passagens.repositories.LayoutBusRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LayoutBusService {
    @Autowired
    private LayoutBusRepository layoutBusRepository;
    @Autowired
    private AsientoBloqueadoRepository asientoBloqueadoRepository;

    public LayoutBusModel findById(Integer id) {
        Optional<LayoutBusModel> model = layoutBusRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, LayoutBusModel.class.getName()));
    }

    public List<LayoutBusModel> findAll() {
        return layoutBusRepository.findAll();
    }

    @Transactional
    public LayoutBusModel save(LayoutBusDTO dto) {
        List<AsientoBloqueadoDTO> asientosBloqueados = dto.getAsientosBloqueados();
        var layoutModel = new LayoutBusModel();
        layoutModel.setNSillas(dto.getNSillas());
        layoutModel.setNFilas(dto.getNFilas());

        switch (dto.getPosicionPasillo()) {
            case "medio":
                layoutModel.setPosicionPasillo(EnumPosicao.MEDIO);
                break;
            case "izquierda":
                layoutModel.setPosicionPasillo(EnumPosicao.IZQUIERDA);
                break;
            case "derecha":
                layoutModel.setPosicionPasillo(EnumPosicao.DERECHA);
                break;
        }

        switch (dto.getTipo()) {
            case "leito":
                layoutModel.setTipo(EnumTipoBus.LEITO);
                break;
            case "tradicional":
                layoutModel.setTipo(EnumTipoBus.TRADICIONAL);
                break;
        }

        switch (dto.getInicioContagem()) {
            case "izquierda":
                layoutModel.setInicioContagem(EnumPosicao.DERECHA);
                break;
            case "derecha":
                layoutModel.setInicioContagem(EnumPosicao.IZQUIERDA);
                break;
        }

        var layoutModelSave = layoutBusRepository.save(layoutModel);
        for (AsientoBloqueadoDTO asientoDto : asientosBloqueados) {
            var asientoModel = new AsientoBloqueadoModel(asientoDto);
            asientoModel.setLayout(layoutModelSave);
            asientoBloqueadoRepository.save(asientoModel);
        }

        return layoutModelSave;
    }
}
