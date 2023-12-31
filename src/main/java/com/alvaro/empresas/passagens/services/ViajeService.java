package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.*;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ViajeService {
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private TrayectoService trayectoService;
    @Autowired
    private PrecioService precioService;


    public ViajeModel findById(Integer id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViajeModel.class.getName()));
    }

    public List<ViajeDTOList> findAll() {
        List<ViajeModel> models = viajeRepository.findAll();
        List<ViajeDTOList> dtos = new ArrayList<>();
        for (ViajeModel model : models) {
            UUID idTrayecto = model.getTrayecto().getCodigo();
            Integer salida = model.getSalida().getId();
            Integer destino = model.getDestino().getId();
            dtos.add(new ViajeDTOList(model, idTrayecto, salida, destino));
        }
        return dtos;
    }

    public ViajeDTOResponse getOne(Integer id) {
        var model = this.findById(id);
        UUID codigoTrayecto = model.getTrayecto().getCodigo();
        var salida = model.getSalida();
        var destino = model.getDestino();

        var salidaResponse = new ParadaDTO(salida, salida.getLugar().getId(), codigoTrayecto);
        var destinoResponse = new ParadaDTO(destino, destino.getLugar().getId(), codigoTrayecto);

        List<PrecioDTO> precios = new ArrayList<>();
        for (PrecioModel precioModel : model.getPrecios()) {
            precios.add(new PrecioDTO(precioModel, model.getId()));
        }

        return new ViajeDTOResponse(model, precios, codigoTrayecto, salidaResponse, destinoResponse);
    }

    @Transactional
    public ViajeDTOResponse save(ViajeDTO dto) {
        var trayecto = trayectoService.findById(dto.idTrayecto());
        var salida = trayecto.getParadaById(dto.salida());
        if (salida == null) {
            return null;
        }
        var destino = trayecto.getParadaById(dto.destino());

        if (destino == null) {
            return null;
        }

        if (!destino.getDataHora().isAfter(salida.getDataHora())) {
            return null;
        }

        if (trayecto.posseeViaje(salida.getId(), destino.getId())) {
            return null;
        }

        //COmparar as horas

        var model = new ViajeModel(dto);
        model.setTrayecto(trayecto);
        model.setSalida(salida);
        model.setDestino(destino);

        var save = viajeRepository.save(model);

        //Tratando os precos da viajem
        int nPisos = trayecto.getAutobus().getPisos().size();
        List<PrecioModel> precios = new ArrayList<>();

        precios.add(new PrecioModel(dto.precioPiso1(), 1, save));
        if (nPisos == 2) {
            if (dto.precioPiso2() == null) {
                precios.add(new PrecioModel(dto.precioPiso1(), 2, save));
            } else {
                precios.add(new PrecioModel(dto.precioPiso2(), 2, save));
            }
        }

        List<PrecioDTO> preciosSalvos = precioService.saveAll(precios, save.getId());
        //Preparando o dto
        var salidaResponse = new ParadaDTO(salida, salida.getLugar().getId(), trayecto.getCodigo());
        var destinoResponse = new ParadaDTO(destino, destino.getLugar().getId(), trayecto.getCodigo());

        return new ViajeDTOResponse(save, preciosSalvos, trayecto.getCodigo(), salidaResponse, destinoResponse);
    }

    public ViajeDTOResponse update(ViajeDTOUpdate novosDados, Integer id) {
        var model = this.findById(id);
        model.updateValues(novosDados);

        if (novosDados.salida() != null) {
            var salida = model.getTrayecto().getParadaById(novosDados.salida());
            if (salida == null)
                return null;

            model.setSalida(salida);
        }

        if (novosDados.destino() != null) {
            var destino = model.getTrayecto().getParadaById(novosDados.destino());
            if (destino == null) {
                return null;
            }
            model.setDestino(destino);
        }

        var updated = viajeRepository.save(model);

        UUID trayecto = model.getTrayecto().getCodigo();
        var salidaResponse = new ParadaDTO(updated.getSalida(), updated.getSalida().getLugar().getId(), trayecto);
        var destinoResponse = new ParadaDTO(updated.getDestino(), updated.getDestino().getLugar().getId(), trayecto);

        return new ViajeDTOResponse(updated, null, trayecto, salidaResponse, destinoResponse);
    }

    @Transactional
    public void delete(ViajeModel model) {
        viajeRepository.delete(model);
    }
}
