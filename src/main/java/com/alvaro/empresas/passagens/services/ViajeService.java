package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.services.AutobusService;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOListBusqueda;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTO;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOResponse;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.helpers.ConvertsType;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.CiudadRepository;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PrecioService precioService;
    @Autowired
    private CiudadRepository ciudadRepository;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private AutobusService autobusService;

    public ViajeModel findById(UUID id) {
        var model = viajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, ViajeModel.class.getName()));
    }

    public Page<ViajeDTOList> findAll(Pageable pageable) {
        Page<ViajeModel> models = viajeRepository.findAll(pageable);
        return models.map(model -> {
            Integer idAutobus = model.getAutobus().getId();
            return new ViajeDTOList(model, idAutobus);
        });
    }

    //Inconcluso
    public List<ViajeDTOListBusqueda> getViajesFromDia(ViajeDTOSolicitacao dto) {
        // Converter o id da ciudad para um LUGAR
        if (dto.idCiudadDestino().equals(dto.idCiudadSalida()))
            throw new ValidationException("idDestino", "El destino no puede ser el mismo que la salida");

        var salidaC = ciudadRepository.findById(dto.idCiudadSalida());
        if (!salidaC.isPresent())
            throw new ObjectNotFoundException(dto.idCiudadSalida(), CiudadModel.class.getName());
        var destinoC = ciudadRepository.findById(dto.idCiudadDestino());
        if (!destinoC.isPresent())
            throw new ObjectNotFoundException(dto.idCiudadDestino(), CiudadModel.class.getName());

        LocalDateTime hj = LocalDateTime.now();
        LocalDateTime startDay;
        LocalDateTime endDay = dto.fechaSalida().atTime(LocalTime.MAX);

        List<byte[]> codigosBytes = new ArrayList<>();
        List<ViajeDTOListBusqueda> viajesSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(dto.fechaSalida())) {
            startDay = hj.plusMinutes(30);
            if (hj.toLocalTime().isAfter(LocalTime.of(23, 30))) return new ArrayList<>();
        } else startDay = dto.fechaSalida().atTime(LocalTime.MIN);


        List<LugarModel> lugaresSalida = salidaC.get().getLugares();
        List<LugarModel> lugaresDestino = destinoC.get().getLugares();
        for (LugarModel lugarSalida : lugaresSalida) {
            codigosBytes = paradaRepository.cargarSalidasDelDia(lugarSalida.getId(), startDay, endDay);

            if (!codigosBytes.isEmpty()) {
                for (byte[] idCodigo : codigosBytes) {
                    UUID codigo = ConvertsType.convertBytesToUUIDHelper(idCodigo);
                    List<ParadaModel> nVezesTrayectoPassaSalida = paradaRepository.nVezesViajePassa(lugarSalida.getId(), codigo);

                    if (nVezesTrayectoPassaSalida.size() != 1) continue;

                    for (LugarModel lugarDestino : lugaresDestino) {
                        List<ParadaModel> nVezesTrayectoPassaDestino = paradaRepository.nVezesViajePassa(lugarDestino.getId(), codigo);
                        if (nVezesTrayectoPassaDestino.size() != 1) continue;

                        String logo = viajeRepository.getLogoEmpresaFromViaje(codigo);

                        ParadaModel salida = nVezesTrayectoPassaSalida.get(0);
                        ParadaModel destino = nVezesTrayectoPassaDestino.get(0);

                        ViajeModel viaje = this.findById(codigo);

                        ParadaDTOComplete salidaDTO = new ParadaDTOComplete(salida, viaje.getCodigo());
                        ParadaDTOComplete destinoDTO = new ParadaDTOComplete(destino, viaje.getCodigo());

                        List<PrecioDTO> precios = new ArrayList<>();
                        for (PrecioModel precio : viaje.getPrecios())
                            if (!precio.getLleno()) precios.add(new PrecioDTO(precio));

                        viajesSelecionados.add(new ViajeDTOListBusqueda(viaje, logo, salidaDTO, destinoDTO, precios));
                    }
                }
            }
        }

        return viajesSelecionados;
    }

    public ViajeDTOResponse getOne(UUID id) {
        var model = this.findById(id);
        Integer idAutobus = model.getAutobus().getId();
        List<ParadaDTOComplete> paradasDTOs = new ArrayList<>();

        for (ParadaModel paradaModel : model.getParadas())
            paradasDTOs.add(new ParadaDTOComplete(paradaModel, model.getCodigo()));

        List<PrecioDTO> precios = new ArrayList<>();
        for (PrecioModel precioModel : model.getPrecios())
            precios.add(new PrecioDTO(precioModel, model.getCodigo()));

        return new ViajeDTOResponse(model, idAutobus, paradasDTOs, precios);
    }

    @Transactional
    public ViajeDTOResponse save(ViajeDTO dto) {
        var lugarSalida = lugarRepository.findById(dto.salida().idLugar());
        if (lugarSalida.isEmpty())
            throw new ValidationException("salida.idLugar", "El lugarSalida no fue allado");

        var lugarDestino = lugarRepository.findById(dto.salida().idLugar());
        if (lugarDestino.isEmpty())
            throw new ValidationException("destino.idLugar", "El lugarDestino no fue allado");

        if (!dto.destino().dataHora().isAfter(dto.salida().dataHora()))
            throw new ValidationException("salida", "La salida posee un horario superior al del destino");


        var autobus = autobusService.findById(dto.idAutobus());
        var model = new ViajeModel(autobus);

        var saved = viajeRepository.save(model);

        var salida = new ParadaModel(dto.salida().dataHora(), dto.salida().plataforma(), lugarSalida.get(), saved);
        var destino = new ParadaModel(dto.salida().dataHora(), dto.salida().plataforma(), lugarSalida.get(), saved);

        //Tratando los precios del viaje
        List<PisoModel> pisos = autobus.getPisos();

        List<PrecioModel> precios = new ArrayList<>();

        //Solo pueden existir dos precios
        switch (pisos.size()) {
            case 1 -> precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(0).getNSillas()));
            case 2 -> {
                if (pisos.get(0).getNPiso() == 1) {
                    precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(0).getNSillas()));
                    if (dto.precioPiso2() == null)
                        precios.add(new PrecioModel(dto.precioPiso1(), 2, pisos.get(1).getNSillas()));
                    else precios.add(new PrecioModel(dto.precioPiso2(), 2, pisos.get(1).getNSillas()));
                } else {//Numero piso for 2
                    precios.add(new PrecioModel(dto.precioPiso1(), 1, pisos.get(1).getNSillas()));
                    if (dto.precioPiso2() == null)
                        precios.add(new PrecioModel(dto.precioPiso1(), 2, pisos.get(0).getNSillas()));
                    else precios.add(new PrecioModel(dto.precioPiso2(), 2, pisos.get(0).getNSillas()));
                }
            }
        }
        //Guardando los precios
        List<PrecioDTO> preciosSalvos = precioService.saveAll(precios, saved);
        List<ParadaDTOComplete> paradas = new ArrayList<>();
        var salidaSaved = paradaRepository.save(salida);
        paradas.add(new ParadaDTOComplete(salidaSaved, model.getCodigo()));
        var destinoSaved = paradaRepository.save(destino);
        paradas.add(new ParadaDTOComplete(destinoSaved, model.getCodigo()));

        return new ViajeDTOResponse(saved, autobus.getId(), paradas, preciosSalvos);
    }

    public ViajeDTOList update(ViajeDTOUpdate dto, UUID id) {//Validacao para que a mudanca seja feita
        //O autobus deve ter o mesmo numero de asientos
        var autobus = autobusService.findById(dto.idAutobus());
        var model = this.findById(id);

        int size = model.getAutobus().getPisos().size();
        if (size != autobus.getPisos().size())
            throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

        if (size == 1) {
            if (model.getAutobus().getPisos().get(0) != autobus.getPisos().get(0)) {
                throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));
            }
        } else if (size == 2) {
            if (model.getAutobus().getPisos().get(0) != autobus.getPisos().get(0))
                throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

            if (model.getAutobus().getPisos().get(1) != autobus.getPisos().get(1))
                throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

        } else
            throw new ValidationException(new FieldMessage("idAutobus", "El autobus no es compatible"));

        model.setAutobus(autobus);
        var update = viajeRepository.save(model);
        return new ViajeDTOList(update, autobus.getId());
    }


    @Transactional
    public void delete(ViajeModel model) {
        for (PrecioModel precio : model.getPrecios())
            if (!precio.getPasajes().isEmpty()) throw new ValidationException("El viaje no pudo ser eliminado");
        viajeRepository.delete(model);
    }
}