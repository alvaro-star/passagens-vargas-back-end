package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ParadaDTOList;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOListBusqueda;
import com.alvaro.empresas.passagens.dtos.viajes.Busca.ViajeDTOSolicitacao;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTO;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOResponse;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOUpdate;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        if (dto.idDestino() == dto.idSalida()) {
            throw new ValidationException(new FieldMessage("idDestino", "El destino no puede ser el mismo que la salida"));
        }
        LocalDateTime hj = LocalDateTime.now();
        LocalDateTime fechaSalida = dto.fechaSalida();
        LocalDateTime endDay = fechaSalida.with(LocalDateTime.MAX);

        List<UUID> idCodigos = null;
        List<ViajeDTOListBusqueda> viajesSelecionados = new ArrayList<>();

        if (hj.toLocalDate().isEqual(fechaSalida.toLocalDate())) {
            idCodigos = paradaRepository.cargarSalidasDelDia(dto.idSalida(), hj, endDay);
        } else {
            LocalDateTime startDay = fechaSalida.with(LocalDateTime.MIN);
            idCodigos = paradaRepository.cargarSalidasDelDia(dto.idSalida(), startDay, endDay);
        }

        if (idCodigos != null) {
            for (UUID codigo : idCodigos) {
                List<ParadaModel> paradas = paradaRepository.cargarParadasdoTrayecto(codigo);

                int salidaIndex = buscaBinaria(paradas, dto.idSalida(), 0, paradas.size() - 2);
                if (salidaIndex == -1) {
                    continue;
                }
                int destinoIndex = buscaBinaria(paradas, dto.idDestino(), salidaIndex + 1, paradas.size() - 1);
                if (destinoIndex == -1) {
                    continue;
                }

                String logo = viajeRepository.getLogoEmpresa(codigo);

                ParadaModel salida = paradas.get(salidaIndex);
                ParadaModel destino = paradas.get(destinoIndex);

                List<ViajeModel> viajes = viajeRepository.getFromTrayecto(codigo, salida.getDataHora(), destino.getDataHora());

                for (ViajeModel viaje : viajes) {
                    /*if (!(salida.getDataHora().isBefore(viaje.getSalida().getDataHora()))
                            && !(destino.getDataHora().isAfter(viaje.getDestino().getDataHora()))) {

                        ParadaDTOList salidaDTO = convertToParadaDTOList(salida);
                        ParadaDTOList destinoDTO = convertToParadaDTOList(destino);
                        List<PrecioDTO> precios = new ArrayList<>();
                        for (PrecioModel precio : viaje.getPrecios()) {
                            precios.add(new PrecioDTO(precio));
                        }
                        viajesSelecionados.add(new ViajeDTOListBusqueda(viaje, logo, salidaDTO, destinoDTO, precios));
                    }*/
                    ParadaDTOList salidaDTO = convertToParadaDTOList(salida);
                    ParadaDTOList destinoDTO = convertToParadaDTOList(destino);
                    List<PrecioDTO> precios = new ArrayList<>();
                    for (PrecioModel precio : viaje.getPrecios()) {
                        precios.add(new PrecioDTO(precio));
                    }
                    viajesSelecionados.add(new ViajeDTOListBusqueda(viaje, logo, salidaDTO, destinoDTO, precios));
                }
            }
        }

        return viajesSelecionados;
    }

    public ParadaDTOList convertToParadaDTOList(ParadaModel model) {
        return new ParadaDTOList(
                model,
                model.getLugar().getNombre(),
                model.getLugar().getCiudad().getNombre(),
                model.getLugar().getCiudad().getDepartamento().getNombre());
    }


    public List<ViajeModel> findViajesBeteween(UUID codigoTrayecto, LocalDateTime salida, LocalDateTime destino) {
        return viajeRepository.cargarViajesConIntervalosComunes(codigoTrayecto, salida, destino);
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
        if (dto.salida() == dto.destino()) {
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

        if (!destino.getDataHora().isAfter(salida.getDataHora())) {
            throw new ValidationException(new FieldMessage("salida", "La salida posee un horario superior al del destino"));
        }

        if (trayecto.posseeViaje(salida.getId(), destino.getId())) {
            throw new ValidationException(new FieldMessage("id", "El viaje ya se encuentra registrado"));
        }

        var model = new ViajeModel(dto);
        model.setTrayecto(trayecto);
        model.setSalida(salida);
        model.setDestino(destino);

        var saved = viajeRepository.save(model);

        //Tratando os precos da viajem
        int nPisos = trayecto.getAutobus().getPisos().size();
        List<PrecioModel> precios = new ArrayList<>();

        precios.add(new PrecioModel(dto.precioPiso1(), 1));
        if (nPisos == 2) {
            if (dto.precioPiso2() == null) {
                precios.add(new PrecioModel(dto.precioPiso1(), 2));
            } else {
                precios.add(new PrecioModel(dto.precioPiso2(), 2));
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
    public ViajeDTOResponse update(ViajeDTOUpdate novosDados, Integer id) {
        var model = this.findById(id);
        model.updateValues(novosDados);

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
    }

    @Transactional
    public void delete(ViajeModel model) {
        viajeRepository.delete(model);
    }

    public int buscaBinaria(List<ParadaModel> lista, Integer idAlvo, int inicioBusca, int fimBusca) {
        int inicio = inicioBusca;
        int fim = fimBusca;

        while (inicio <= fim) {
            int meio = inicio + (fim - inicio) / 2;

            // Verifica se o elemento está no meio
            if (lista.get(meio).getId() == idAlvo) {
                return meio;
            }

            // Se o elemento é menor que o meio, busca na metade inferior
            if (lista.get(meio).getId() > idAlvo) {
                fim = meio - 1;
            } else { // Se o elemento é maior que o meio, busca na metade superior
                inicio = meio + 1;
            }
        }

        // Retorna -1 se o elemento não foi encontrado
        return -1;
    }
}
