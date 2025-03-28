package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;

import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTOVenta;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.PassagensPDF;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaReembolsoModel;
import com.alvaro.empresas.passagens.pagamentos.services.FaturaPassagemService;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.services.validacao.ValidarCompraPassagens;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PassagemService {
    @Value("${api.viaje.min-time-before-buy-pasaje-min}")
    private Integer tempoMinimoAntesCompraPassagem;
    @Autowired
    private PassagemRepository passagemRepository;
    @Autowired
    private PrecoService precioService;
    @Autowired
    private FaturaPassagemService faturaPassagemService;
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private ValidarCompraPassagens validarCompraPassagens;

    private static final Logger logger = LoggerFactory.getLogger(PassagemService.class);

    public PassagemModel findById(UUID id) {
        var modelo = passagemRepository.findById(id);
        return modelo.orElseThrow(() -> new ObjectNotFoundException(id, PassagemModel.class.getName()));
    }

    public PassagemDTOEmpresaResponse getOne(UUID id) {
        var modelo = findById(id);
        return new PassagemDTOEmpresaResponse(modelo);
    }

    public byte[] obterDownloadPassagem(UUID idPassagem) {
        var passagemModel = findById(idPassagem);
        PassagensPDF passagemPDF = new PassagensPDF();
        byte[] arrayVazio = new byte[0];
        try {
            ParadaModel saida = passagemModel.getSaida();
            ParadaModel destino = passagemModel.getDestino();
            passagemPDF.addPassagem(passagemModel, passagemModel.getPreco().getEmpresa().getNome(), saida, destino,
                    passagemModel.getFaturaPassagem().getMetodoPagamento());
            arrayVazio = passagemPDF.closeAndGetBytes();
            return arrayVazio;
        } catch (IOException exception) {
            throw new ValidationException("passagem", "Houve um erro ao criar a passagem");
        }
    }

    public List<PassagemDTOEmpresaResponse> getPassagensByPreco(UUID idPrecio) {
        return passagemRepository.findByPrecoIdAndEstaPago(idPrecio, true).stream().map(PassagemDTOEmpresaResponse::new)
                .toList();
    }

    private void validarViagem(ViagemModel viagem, Integer idLugarSaida, Integer idLugarDestino) {
        var saida = viagem.getParadaByLugarId(idLugarSaida);
        var destino = viagem.getParadaByLugarId(idLugarDestino);
        if (saida == null)
            throw new ValidationException("idLugarSaida", "A saída não faz parte do trajeto");
        else if (saida.getDataHora().isBefore(LocalDateTime.now().minusMinutes(tempoMinimoAntesCompraPassagem)))
            throw new RestRuntimeException("O ônibus já iniciou o viagem");
        if (destino == null)
            throw new ValidationException("idLugarDestino", "O destino não faz parte do trajeto");
    }

    @Transactional
    public FaturaPassagemModel salvarCliente(PassagensDTO dto, BindingResult bindingResult) {
        var preco = precioService.findById(dto.idPreco());
        validarCompraPassagens.validarPassagensDTO(bindingResult, dto, "/passagens");
        var viagem = preco.getViagem();

        validarViagem(viagem, dto.idLugarSaida(), dto.idLugarDestino());

        ParadaModel saida = viagem.getParadaByLugarId(dto.idLugarSaida());
        ParadaModel destino = viagem.getParadaByLugarId(dto.idLugarDestino());

        PisoModel pisoEscolhido = viagem.getOnibus().getPisoByNumero(preco.getNPiso());
        validarAssentos(pisoEscolhido, preco, dto.passagens());

        BigDecimal valorTotal = preco.getPreco().multiply(BigDecimal.valueOf(dto.passagens().size()));
        FaturaPassagemModel pago = faturaPassagemService.saveCliente(dto.contato(), valorTotal, null, TipoPagamento.QR);

        viagem.addValorArrecadadoWeb(pago.getValorTotal());

        viagemRepository.save(viagem);

        PassagemModel passagemModel;
        List<PassagemModel> passagensList = new ArrayList<>();

        for (PassagemDTO passagemDTO : dto.passagens()) {
            passagemModel = new PassagemModel(passagemDTO, true, preco.getPreco(), false, false, saida, destino, preco,
                    pago);
            passagensList.add(passagemModel);
        }

        passagemRepository.saveAll(passagensList);
        return pago;
    }

    @Transactional
    public UUID salvarEmpresa(PassagensDTOVenta dto, ViagemModel viagem, BindingResult bindingResult) {
        validarCompraPassagens.validarPassagensDTOVenta(bindingResult, dto, "/passagens/vender");
        ParadaModel saida;
        ParadaModel destino;

        validarViagem(viagem, dto.idLugarSaida(), dto.idLugarDestino());

        saida = viagem.getParadaByLugarId(dto.idLugarSaida());
        destino = viagem.getParadaByLugarId(dto.idLugarDestino());

        List<PassagemDTO> cadeirasPiso1 = new ArrayList<>(), cadeirasPiso2 = new ArrayList<>();

        PisoModel piso1 = viagem.getOnibus().getPisoByNumero(1);
        PisoModel piso2 = viagem.getOnibus().getPisoByNumero(2);

        for (PassagemDTO passagemFor : dto.passagens()) {
            if (passagemFor.nAssento() > 0 && passagemFor.nAssento() <= piso1.getNAssentos())
                cadeirasPiso1.add(passagemFor);
            else
                cadeirasPiso2.add(passagemFor);
        }

        PrecoModel preco1 = viagem.getPrecoByNPiso(1);
        PrecoModel preco2 = viagem.getPrecoByNPiso(2);

        BigDecimal valorTotal = BigDecimal.ZERO;
        if (piso2 == null && !cadeirasPiso2.isEmpty())
            throw new ValidationException("passagens", "Há um número de cadeira inválido");

        if (!cadeirasPiso1.isEmpty()) {
            validarAssentos(piso1, preco1, cadeirasPiso1);
            valorTotal = valorTotal.add(preco1.getPreco().multiply(BigDecimal.valueOf(cadeirasPiso1.size())));
        }

        if (!cadeirasPiso2.isEmpty()) {
            validarAssentos(piso2, preco2, cadeirasPiso2);
            valorTotal = valorTotal.add(preco2.getPreco().multiply(BigDecimal.valueOf(cadeirasPiso2.size())));
        }

        if (valorTotal.compareTo(BigDecimal.ZERO) == 0)
            throw new ValidationException("passagens", "A soma das passagens é zero");

        boolean emDinheiro = false;
        boolean estaPago = true;

        FaturaPassagemModel pago = faturaPassagemService.saveEmpresa(valorTotal, viagem, dto.metodoPagamento(),
                estaPago);

        viagem.addValorArrecadadoNaoWeb(pago.getValorTotal());
        if (dto.metodoPagamento().equals(TipoPagamento.DINHEIRO)) {
            emDinheiro = true;
            viagem.addValorArrecadadoDinheiro(pago.getValorTotal());
        }

        if (!cadeirasPiso1.isEmpty()) {
            atualizarNAssentosDisponiveis(preco1, cadeirasPiso1);
            precioService.updateFromService(preco1);
        }

        if (preco2 != null && !cadeirasPiso2.isEmpty()) {
            atualizarNAssentosDisponiveis(preco2, cadeirasPiso2);
            precioService.updateFromService(preco2);
        }

        viagemRepository.save(viagem); // Atualizar os valores arrecadados

        List<PassagemModel> passagens = new ArrayList<>();
        for (PassagemDTO passagemDTO : cadeirasPiso1) {
            var passagem = new PassagemModel(passagemDTO, false, preco1.getPreco(), estaPago, emDinheiro, saida,
                    destino, preco1, pago);
            passagens.add(passagem);
        }

        if (preco2 != null)
            for (PassagemDTO passagemDTO : cadeirasPiso2) {
                var passagem = new PassagemModel(passagemDTO, false, preco2.getPreco(), estaPago, emDinheiro, saida,
                        destino, preco2, pago);
                passagens.add(passagem);
            }

        passagemRepository.saveAll(passagens);

        return pago.getId();
    }

    public void validarAssentos(PisoModel piso, PrecoModel precio, List<PassagemDTO> sillasSolicitadas) {
        int numeroMinimo = piso.getPrimeiroAssento();
        int numeroMaximo = piso.getUltimoAssento();

        List<Integer> ocupados = passagemRepository.getPassagensVendidasENaoReembolsadas(precio.getId());

        if (precio.getNAssentosDisponiveis() < sillasSolicitadas.size())
            throw new ValidationException("passagens", "Não há assentos disponíveis");

        for (PassagemDTO sillasSolicitada : sillasSolicitadas) {
            for (Integer ocupado : ocupados)
                if (ocupado.equals(sillasSolicitada.nAssento()))
                    throw new ValidationException("Um dos assentos já foi comprado");

            if (sillasSolicitada.nAssento() > numeroMaximo || sillasSolicitada.nAssento() < numeroMinimo)
                throw new ValidationException("nAssento", "O número do assento informado é inválido");
        }
    }

    private void atualizarNAssentosDisponiveis(PrecoModel preco, List<PassagemDTO> assentos) {
        int nAssentosDiposniveis = preco.getNAssentosDisponiveis() - assentos.size();
        if (nAssentosDiposniveis < 0)
            throw new ValidationException("passagens", "Não há assentos disponíveis");
        if (nAssentosDiposniveis == 0)
            preco.setCheio(true);
        preco.setNAssentosDisponiveis(nAssentosDiposniveis);
    }

    public void delete(UUID idPassagem) {
        var model = findById(idPassagem);

        if (!model.getFaturaPassagem().getEstaPago()) {
            logger.error("Se tentou reembolsar uma passagem que não foi pago");
            throw new RestRuntimeException(HttpStatus.CONFLICT, "A passagem não foi paga");
        }

        if (model.getFaturaReembolso() != null)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "A passagem já foi rembolsado");
        var viagem = model.getPreco().getViagem();
        boolean resultado;
        if (model.getEmDinheiro()) {
            resultado = viagem.subtrairValorDinheiro(model.getPrecoPago());
        } else if (!model.getCompradoWeb())
            resultado = viagem.subtrairValorNaoWeb(model.getPrecoPago());
        else {
            logger.warn("Se precisa configurar uma API para a operação");
            // Neste caso não se pode fazer um reembolso sem uma API
            throw new RestRuntimeException(HttpStatus.CONFLICT,
                    "A passagem foi comprado na web, o reembolso não esta disponível");
        }
        if (!resultado) {
            logger.warn("Se tentou retirar um valor que não podia da caixa");
            throw new RestRuntimeException(HttpStatus.CONFLICT,
                    "O valor do dinheiro registrado é menor que o preço da passagem");
        }

        var faturaReembolsada = new FaturaReembolsoModel(model.getPrecoPago(), model.getFaturaPassagem(), model);
        model.setFaturaReembolso(faturaReembolsada);
        passagemRepository.save(model);
    }
}
