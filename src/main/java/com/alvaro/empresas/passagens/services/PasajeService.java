package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasajes.PasajesDTO;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.models.*;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.PasajeroRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PasajeService {
    @Autowired
    private PasajeRepository pasajeRepository;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private PagoService pagoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public PasajeModel findById(UUID id) {
        var model = pasajeRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, PasajeModel.class.getName()));
    }

    //En desarrollo
    public void getOne(UUID id) {
        var model = findById(id);
    }

    //Solo podra salvar pasajes de un mismo piso
    @Transactional
    public PagoModel save(PasajesDTO dto, MetodoPagamentoEnum metodo, boolean guardarContacto, boolean compradoWeb) {
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

        boolean estaPago;

        switch (metodo) {
            case QR -> estaPago = false;
            case EFECTIVO -> estaPago = true;
            default -> throw new ValidationException(new FieldMessage("metodo", "Metodo de Pago invalido"));
        }

        PagoModel pago = pagoService.save(dto, precio.getPrecio(), viaje, metodo, guardarContacto);

        if (estaPago){
            viaje.setValorArrecadado(pago.getValorTotal());
        }


        for (PasajeDTO pasajeDTO : dto.pasajes()) {
            var pasajero = new PasajeroModel(pasajeDTO);
            var pasajeModel = new PasajeModel(pasajeDTO.nSilla(), compradoWeb, estaPago, salida, destino, precio, pago, pasajero);
            pasajeRepository.save(pasajeModel);
        }

        if (metodo == MetodoPagamentoEnum.EFECTIVO) {
            int nSillasDisponibles = precio.getNSillasDisponibles() - dto.pasajes().size();

            if (nSillasDisponibles < 0)
                throw new ValidationException(new FieldMessage("pasajes", "No hay sillas disponibles"));

            precio.setNSillasDisponibles(nSillasDisponibles);

            if (nSillasDisponibles == 0)
                precio.setLleno(true);

            precioService.updateFromService(precio);
        }

        return pago;
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

    public boolean empresaValida(Authentication usuario, UUID idPrecio) {
        var autorizado = usuarioRepository.findByEmail(usuario.getName());
        var precio = precioService.findById(idPrecio);
        return autorizado.getIdEmpresa() == precio.getViaje().getAutobus().getEmpresa().getId();
    }
}
