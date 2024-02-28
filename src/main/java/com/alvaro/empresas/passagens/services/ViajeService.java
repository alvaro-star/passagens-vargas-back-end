package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOListBusqueda;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTO;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOResponse;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOList;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ViajeService {
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private TrayectoService trayectoService;
    @Autowired
    private PrecioService precioService;


    public ViajeModel findById(Integer id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViajeModel.class.getName()));
    }

    public Page<ViajeDTOList> findAll(Pageable pageable) {
        Page<ViajeModel> models = viajeRepository.findAll(pageable);
        return models.map(model -> {
            UUID idTrayecto = model.getTrayecto().getCodigo();
            Integer salida = model.getSalida().getId();
            Integer destino = model.getDestino().getId();
            return new ViajeDTOList(model, idTrayecto, salida, destino);
        });
    }

    public List<ViajeDTOListBusqueda> getViajesFromDia(ViajeDTOSolicitacao dto) {
        if (dto.idLugarDestino().equals(dto.idLugarSalida())) {
            throw new ValidationException(new FieldMessage("idDestino", "El destino no puede ser el mismo que la salida"));
        }

        LocalDateTime hj = LocalDateTime.now();
        LocalDateTime startDay;
        LocalDateTime endDay = dto.fechaSalida().atTime(LocalTime.MAX);

        List<byte[]> codigosBytes;
        List<ViajeDTOListBusqueda> viajesSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(dto.fechaSalida())) {
            startDay = hj.plusMinutes(30);
            if (hj.toLocalTime().isAfter(LocalTime.of(23, 30)))
                return new ArrayList<>();
        } else
            startDay = dto.fechaSalida().atTime(LocalTime.MIN);

        codigosBytes = paradaRepository.cargarSalidasDelDia(dto.idLugarSalida(), startDay, endDay);

        if (codigosBytes != null) {
            for (byte[] idCodigo : codigosBytes) {
                UUID codigo = convertBytesToUUID(idCodigo);

                List<ParadaModel> nVezesTrayectoPassaSalida = paradaRepository.nVezesTrayectoPassa(dto.idLugarSalida(), codigo);

                if (nVezesTrayectoPassaSalida.size() != 1)
                    continue;

                List<ParadaModel> nVezesTrayectoPassaDestino = paradaRepository.nVezesTrayectoPassa(dto.idLugarDestino(), codigo);
                if (nVezesTrayectoPassaDestino.size() != 1)
                    continue;

                String logo = viajeRepository.getLogoEmpresaFromTrayecto(codigo);

                ParadaModel salida = nVezesTrayectoPassaSalida.get(0);
                ParadaModel destino = nVezesTrayectoPassaDestino.get(0);
                List<ViajeModel> viajes = viajeRepository.getFromTrayecto(codigo, salida.getDataHora(), destino.getDataHora());
                for (ViajeModel viaje : viajes) {
                    ParadaDTOList salidaDTO = convertToParadaDTOList(viaje.getSalida());
                    ParadaDTOList destinoDTO = convertToParadaDTOList(viaje.getDestino());
                    List<PrecioDTO> precios = new ArrayList<>();
                    for (PrecioModel precio : viaje.getPrecios()) {
                        if (!precio.getLleno())
                            precios.add(new PrecioDTO(precio));
                    }
                    viajesSelecionados.add(new ViajeDTOListBusqueda(viaje, logo, salidaDTO, destinoDTO, precios));
                }
            }
        }

        return viajesSelecionados;
    }

    public ViajeDTOListBusqueda getOne(Integer id) {
        var model = this.findById(id);
        UUID codigoTrayecto = model.getTrayecto().getCodigo();

        var salida = convertToParadaDTOList(model.getSalida());
        var destino = convertToParadaDTOList(model.getDestino());

        List<PrecioDTO> precios = new ArrayList<>();
        for (PrecioModel precioModel : model.getPrecios()) {
            precios.add(new PrecioDTO(precioModel, model.getId()));
        }

        String logo = viajeRepository.getLogoEmpresaFromTrayecto(codigoTrayecto);

        return new ViajeDTOListBusqueda(model, logo, salida, destino, precios);
    }

    @Transactional
    public ViajeDTOResponse save(ViajeDTO dto) {
        var trayecto = trayectoService.findById(dto.idTrayecto());

        if (!trayecto.getViajes().isEmpty())
            throw new ValidationException(new FieldMessage("id", "El trayecto ya posee un viaje"));

        //En este punto queda claro que hay minimo dos paradas
        if (trayecto.getParadas().size() < 2)
            throw new ValidationException(new FieldMessage("paradas", "El trayecto no posee suficientes paradas"));

        var salida = trayecto.getMenorParada();
        var destino = trayecto.getMaiorParada();

        if (!destino.getDataHora().isAfter(salida.getDataHora()))
            throw new ValidationException(new FieldMessage("salida", "La salida posee un horario superior al del destino"));

        var model = new ViajeModel();
        model.setTrayecto(trayecto);
        model.setSalida(salida);
        model.setDestino(destino);

        var saved = viajeRepository.save(model);

        //Tratando os precos da viajem
        List<PisoModel> pisos = trayecto.getAutobus().getPisos();

        List<PrecioModel> precios = new ArrayList<>();

        //So podem existir dois pisos
        switch (pisos.size()) {
            case 1 -> precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(0).getNSillas()));
            case 2 -> {
                if (pisos.get(0).getNPiso() == 1) {
                    precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(0).getNSillas()));
                    if (dto.precioPiso2() == null)
                        precios.add(new PrecioModel(dto.precioPiso1(), 2, pisos.get(1).getNSillas()));
                    else
                        precios.add(new PrecioModel(dto.precioPiso2(), 2, pisos.get(1).getNSillas()));
                } else {//Numero piso for 2
                    precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(1).getNSillas()));
                    if (dto.precioPiso2() == null)
                        precios.add(new PrecioModel(dto.precioPiso1(), 2, pisos.get(0).getNSillas()));
                    else
                        precios.add(new PrecioModel(dto.precioPiso2(), 2, pisos.get(0).getNSillas()));
                }
            }
        }
        //Guardando los precios
        List<PrecioDTO> preciosSalvos = precioService.saveAll(precios, saved);
        //Preparando o dto
        var salidaResponse = new ParadaDTO(salida, salida.getLugar().getId(), trayecto.getCodigo());
        var destinoResponse = new ParadaDTO(destino, destino.getLugar().getId(), trayecto.getCodigo());

        return new ViajeDTOResponse(saved, preciosSalvos, trayecto.getCodigo(), salidaResponse, destinoResponse);
    }

    @Transactional
    public void delete(ViajeModel model) {
        for (PrecioModel precio : model.getPrecios()) {
            if (!precio.getPasajes().isEmpty()) {
                throw new ValidationException("El viaje no pudo ser Eliminado");
            }
        }
        viajeRepository.delete(model);
    }

    public ParadaDTOList convertToParadaDTOList(ParadaModel model) {
        return new ParadaDTOList(
                model,
                model.getLugar().getNombre(),
                model.getLugar().getCiudad().getNombre(),
                model.getLugar().getCiudad().getDepartamento().getNombre());
    }

    private UUID convertBytesToUUID(byte[] bytes) {
        if (bytes.length < 16) {
            throw new IllegalArgumentException("A array de bytes deve ter pelo menos 16 bytes.");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        long mostSignificantBits = byteBuffer.getLong();
        long leastSignificantBits = byteBuffer.getLong();

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    public List<ViajeModel> findViajesBeteween(UUID codigoTrayecto, LocalDateTime salida, LocalDateTime destino) {
        return viajeRepository.cargarViajesConIntervalosComunes(codigoTrayecto, salida, destino);
    }
}
/*public ViajeDTOResponse update(ViajeDTOUpdate novosDados, Integer id) {
        var model = this.findById(id);

        if (novosDados.salida() != null) {
            var salida = model.getTrayecto().getParadaById(novosDados.salida());
            if (salida == null)
                throw new ValidationException(new FieldMessage("salida", "La salida no pertenece al trayecto"));
            model.setSalida(salida);
        }

        if (novosDados.destino() != null) {
            var destino = model.getTrayecto().getParadaById(novosDados.destino());
            if (destino == null) {
                throw new ValidationException(new FieldMessage("destino", "El destino no pertenece al trayecto"));
            }
            model.setDestino(destino);
        }

        if (model.getSalida().getId() == model.getDestino().getId()) {
            throw new ValidationException(new FieldMessage("destino", "El destino no puede ser el mismo que la salida"));
        }

        if (!model.getDestino().getDataHora().isAfter(model.getSalida().getDataHora())) {
            throw new ValidationException(new FieldMessage("salida", "La salida posee un horario superior al del destino"));
        }

        var updated = viajeRepository.save(model);

        UUID trayecto = model.getTrayecto().getCodigo();
        var salidaResponse = new ParadaDTO(updated.getSalida(), updated.getSalida().getLugar().getId(), trayecto);
        var destinoResponse = new ParadaDTO(updated.getDestino(), updated.getDestino().getLugar().getId(), trayecto);

        return new ViajeDTOResponse(updated, null, trayecto, salidaResponse, destinoResponse);
    }*/
/*
//Restos
//Save
        if (dto.salida().equals(dto.destino())) {
            throw new ValidationException(new FieldMessage("destino", "El destino no puede ser el mismo que la salida"));
        }
        var salida = trayecto.getParadaById(dto.salida());

        if (salida == null) {
            throw new ValidationException(new FieldMessage("salida", "La salida no pertenece al trayecto"));
        }

        var destino = trayecto.getParadaById(dto.destino());

        if (destino == null) {
            throw new ValidationException(new FieldMessage("destino", "El destino no pertenece al trayecto"));
        }


 //

        Como solo se podra registrar un viaje, no es necessario ver si hay viajes iguales
        Integer viajesIguais = viajeRepository.getViajesIguais(trayecto.getCodigo(), salida.getId(), destino.getId());
        if (viajesIguais > 0) {
            throw new ValidationException(new FieldMessage("id", "El viaje ya se encuentra registrado"));
        }
 */