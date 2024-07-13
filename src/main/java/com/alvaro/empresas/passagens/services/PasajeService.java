package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTOVenta;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.helpers.PasajesPDF;
import com.alvaro.empresas.passagens.models.FacturaPasajeModel;
import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PasajeService {
    private final PasajeRepository pasajeRepository;
    private final PrecioService precioService;
    private final FacturaPasajeService facturaPasajeService;
    private final ViajeRepository viajeRepository;

    @Autowired
    public PasajeService(PasajeRepository pasajeRepository, PrecioService precioService, FacturaPasajeService facturaPasajeService, ViajeRepository viajeRepository) {
        this.pasajeRepository = pasajeRepository;
        this.precioService = precioService;
        this.facturaPasajeService = facturaPasajeService;
        this.viajeRepository = viajeRepository;
    }

    public PasajeModel findById(UUID id) {
        var model = pasajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PasajeModel.class.getName()));
    }

    //En desarrollo
    public void getOne(UUID id) {
        var model = findById(id);
    }

    //Exclusivo para el servicio online
    @Transactional
    public FacturaPasajeModel save(PasajesDTO dto, MetodoPagamentoEnum metodo, boolean guardarContacto, boolean compradoWeb) {
        var precio = precioService.findById(dto.idPrecio());
        var viaje = precio.getViaje();
        ParadaModel salida;
        ParadaModel destino;

        List<Integer> ocupados = pasajeRepository.getPasajesVendidos(precio.getId());
        validarSilla(viaje, precio, dto.pasajes(), ocupados);
        salida = viaje.getParadaByLugarId(dto.idLugarSalida());
        if (salida == null)
            throw new ValidationException(new FieldMessage("idLugarSalida", "La salida no hace parte del trayecto"));
        destino = viaje.getParadaByLugarId(dto.idLugarDestino());
        if (destino == null)
            throw new ValidationException(new FieldMessage("idLugarDestino", "El destino no hace parte del trayecto"));

        BigDecimal valorTotal = precio.getPrecio().multiply(BigDecimal.valueOf(dto.pasajes().size()));
        FacturaPasajeModel pago = facturaPasajeService.save(dto.contacto(), valorTotal, viaje, metodo, guardarContacto);

        BigDecimal valorArrecadadoWeb = viaje.getValorArrecadadoWeb() != null ? viaje.getValorArrecadadoWeb() : BigDecimal.ZERO;
        viaje.setValorArrecadadoWeb(valorArrecadadoWeb.add(pago.getValorTotal()));
        viajeRepository.save(viaje);
        if (!metodo.equals(MetodoPagamentoEnum.QR))
            throw new ValidationException(new FieldMessage("metodo", "Metodo de Pago invalido"));

        PasajeModel pasajeModel;
        List<PasajeModel> pasajesList = new ArrayList<>();

        for (PasajeDTO pasajeDTO : dto.pasajes()) {
            pasajeModel = new PasajeModel(pasajeDTO.nSilla(), compradoWeb, precio.getPrecio(), false, false, pasajeDTO.nombre(), pasajeDTO.carnet(), pasajeDTO.nascimento(), salida, destino, precio, pago);
            pasajesList.add(pasajeModel);
        }

        pasajeRepository.saveAll(pasajesList);
        return pago;
    }


    @Transactional
    public byte[] saveEmpresa(String empresaNombre, PasajesDTOVenta dto, MetodoPagamentoEnum metodo, ViajeModel viaje, boolean guardarContacto, boolean compradoWeb) {
        ParadaModel salida;
        ParadaModel destino;

        salida = viaje.getParadaByLugarId(dto.idLugarSalida());
        if (salida == null)
            throw new ValidationException(new FieldMessage("idLugarSalida", "La salida no hace parte del trayecto"));
        destino = viaje.getParadaByLugarId(dto.idLugarDestino());
        if (destino == null)
            throw new ValidationException(new FieldMessage("idLugarDestino", "El destino no hace parte del trayecto"));

        List<PasajeDTO> sillasPiso1 = new ArrayList<>();
        List<PasajeDTO> sillasPiso2 = new ArrayList<>();
        PisoModel piso1 = viaje.getAutobus().getPisoByNumero(1);
        PisoModel piso2 = viaje.getAutobus().getPisoByNumero(2);

        for (PasajeDTO pasajeFor : dto.pasajes()) {
            if (pasajeFor.nSilla() > 0 && pasajeFor.nSilla() <= piso1.getNSillas())
                sillasPiso1.add(pasajeFor);
            else
                sillasPiso2.add(pasajeFor);
        }

        PrecioModel precio1 = viaje.getPrecioByNPiso(1);
        PrecioModel precio2 = viaje.getPrecioByNPiso(2);

        BigDecimal valorTotal = BigDecimal.ZERO;
        if (piso2 == null) {
            if (!sillasPiso2.isEmpty())
                throw new ValidationException("pasajes", "Hay un numero dela silla invalida");
            validarSillasEmpresa(piso1, precio1, sillasPiso1);
            valorTotal = precio1.getPrecio().multiply(BigDecimal.valueOf(sillasPiso1.size()));
        } else {
            if (!sillasPiso1.isEmpty()) {
                validarSillasEmpresa(piso1, precio1, sillasPiso1);
                valorTotal = valorTotal.add(precio1.getPrecio().multiply(BigDecimal.valueOf(sillasPiso1.size())));
            }
            if (!sillasPiso2.isEmpty()) {
                validarSillasEmpresa(piso2, precio2, sillasPiso2);
                valorTotal = valorTotal.add(precio2.getPrecio().multiply(BigDecimal.valueOf(sillasPiso2.size())));
            }
        }

        if (valorTotal.compareTo(BigDecimal.ZERO) == 0)
            throw new ValidationException("pasajes", "La suma delos pasajes es zero");

        boolean estaPago;
        boolean enEfectivo = false;

        FacturaPasajeModel pago = facturaPasajeService.save(dto.contacto(), valorTotal, viaje, metodo, guardarContacto);

        BigDecimal valorArrecadadoNoWeb = viaje.getValorArrecadadoNoWeb() != null ? viaje.getValorArrecadadoNoWeb() : BigDecimal.ZERO;
        BigDecimal valorTotalPago = pago.getValorTotal() != null ? pago.getValorTotal() : BigDecimal.ZERO;
        viaje.setValorArrecadadoNoWeb(valorArrecadadoNoWeb.add(valorTotalPago));
        switch (metodo) {
            case QR -> estaPago = false;
            case EFECTIVO -> {
                estaPago = true;
                enEfectivo = true;

                BigDecimal valorArrecadadoEfectivo = viaje.getValorArrecadadoEfectivo() != null ? viaje.getValorArrecadadoEfectivo() : BigDecimal.ZERO;
                viaje.setValorArrecadadoEfectivo(valorArrecadadoEfectivo.add(valorTotalPago));

                if (!sillasPiso1.isEmpty()) {
                    actualizarNSillasDisponibles(precio1, sillasPiso1);
                    precioService.updateFromService(precio1);
                }

                if (precio2 != null && !sillasPiso2.isEmpty()) {
                    actualizarNSillasDisponibles(precio2, sillasPiso2);
                    precioService.updateFromService(precio1);
                }
            }
            default -> throw new ValidationException("metodo", "Metodo de Pago invalido");
        }

        viajeRepository.save(viaje);//Actualizar los valores arrecadados

        PasajeModel pasaje;
        List<PasajeModel> pasajesModels = new ArrayList<>();
        for (PasajeDTO pasajeDTO : sillasPiso1) {
            pasaje = new PasajeModel(
                    pasajeDTO.nSilla(), compradoWeb, precio1.getPrecio(), estaPago, enEfectivo, pasajeDTO.nombre(),
                    pasajeDTO.carnet(), pasajeDTO.nascimento(), salida, destino, precio1, pago);
            pasajesModels.add(pasaje);
        }

        if (precio2 != null)
            for (PasajeDTO pasajeDTO : sillasPiso2) {
                pasaje = new PasajeModel(pasajeDTO.nSilla(), compradoWeb, precio1.getPrecio(), estaPago, enEfectivo, pasajeDTO.nombre(), pasajeDTO.carnet(), pasajeDTO.nascimento(), salida, destino, precio2, pago);
                pasajesModels.add(pasaje);
            }

        pasajeRepository.saveAll(pasajesModels);
        PasajesPDF pasajesPDF = new PasajesPDF();
        byte[] emptyByteArray = new byte[0];

        try {
            for (PasajeModel pasajeModel : pasajesModels)
                pasajesPDF.addPasaje(pasajeModel, empresaNombre, salida, destino);
            emptyByteArray = pasajesPDF.closeAndGetBytes();
            return emptyByteArray;
        } catch (IOException exception) {
            throw new ValidationException("pasajes", "Hubo un error ala hora de crear los boletos");
        }
    }

    public byte[] getOnePasajeDownload(UUID idPasaje) {
        var pasajeModel = findById(idPasaje);
        PasajesPDF pasajePDF = new PasajesPDF();
        byte[] emptyByteArray = new byte[0];
        try {
            pasajePDF.addPasaje(
                    pasajeModel,
                    pasajeModel.getPrecio().getEmpresa().getNombre(),
                    pasajeModel.getSalida(),
                    pasajeModel.getDestino());
            emptyByteArray = pasajePDF.closeAndGetBytes();
            return emptyByteArray;
        } catch (IOException exception) {
            throw new ValidationException("pasaje", "Hubo un error ala hra de crear el boleto");
        }
    }

    public List<PasajeDTOEmpresaResponse> getPasajesFromPrecio(UUID idPrecio) {
        return pasajeRepository.findByPrecioIdAndEstaPagado(idPrecio, true).stream().map(model -> {
            ParadaDTOComplete salida = new ParadaDTOComplete(model.getSalida(), null);
            ParadaDTOComplete destino = new ParadaDTOComplete(model.getDestino(), null);
            return new PasajeDTOEmpresaResponse(model, salida, destino);
        }).toList();
    }

    //Validadores
    public void actualizarNSillasDisponibles(PrecioModel precio, List<PasajeDTO> sillasPiso) {
        int nSillasDisponibles = precio.getNSillasDisponibles() - sillasPiso.size();
        if (nSillasDisponibles < 0)
            throw new ValidationException(new FieldMessage("pasajes", "No hay sillas disponibles"));
        if (nSillasDisponibles == 0)
            precio.setLleno(true);
        precio.setNSillasDisponibles(nSillasDisponibles);
    }

    public void validarSillasEmpresa(PisoModel piso, PrecioModel precio, List<PasajeDTO> sillasSolicitadas) {
        int numeroMinimo = piso.getPrimeraSilla();
        int numeroMaximo = piso.getNSillas() + piso.getPrimeraSilla() - 1;

        List<Integer> ocupados = pasajeRepository.getPasajesVendidos(precio.getId());

        if (precio.getNSillasDisponibles() < sillasSolicitadas.size())
            throw new ValidationException("pasajes", "No hay tantas sillas disponibles");

        for (PasajeDTO sillasSolicitada : sillasSolicitadas) {
            for (Integer ocupado : ocupados)
                if (ocupado.equals(sillasSolicitada.nSilla()))//Erro
                    throw new ValidationException("El viaje ya posse un pasaje registrado");

            if (sillasSolicitada.nSilla() > numeroMaximo || sillasSolicitada.nSilla() < numeroMinimo)
                throw new ValidationException(new FieldMessage("nSilla", "El numero de Silla informado es invalido"));
        }
    }

    public void validarSilla(ViajeModel viaje, PrecioModel precio, List<PasajeDTO> pasajesDTO, List<Integer> ocupados) {
        PisoModel piso = viaje.getAutobus().getPisoByNumero(precio.getNPiso());
        if (piso == null)
            throw new ValidationException(new FieldMessage("piso", "El piso informado no existe"));

        int numeroMinimo = piso.getPrimeraSilla();
        int numeroMaximo = piso.getNSillas() + piso.getPrimeraSilla() - 1;
        if (precio.getNSillasDisponibles() < pasajesDTO.size())
            throw new ValidationException(new FieldMessage("pasajes", "No hay tantas sillas disponibles"));

        for (PasajeDTO pasajeDTO : pasajesDTO) {
            for (Integer ocupado : ocupados)
                if (ocupado.equals(pasajeDTO.nSilla()))//Erro
                    throw new ValidationException("El viaje ya posse un pasaje registrado");

            if (pasajeDTO.nSilla() > numeroMaximo || pasajeDTO.nSilla() < numeroMinimo)
                throw new ValidationException(new FieldMessage("nSilla", "El numero de Silla informado es invalido"));
        }
    }
}
