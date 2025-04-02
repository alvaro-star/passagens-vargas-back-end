package com.alvaro.empresas.passagens.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTOEmpresaResponse;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.PassagensDTOVenta;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.PassagensPDF;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaReembolsoModel;
import com.alvaro.empresas.passagens.pagamentos.services.FaturaPassagemService;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.services.validacao.ValidarCompraPassagens;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PassagemService {
    @Value("${api.viaje.min-time-before-buy-pasaje-min}")
    private Integer tempoMinimoAntesCompraPassagem;
    @Autowired
    private PassagemRepository passagemRepository;
    @Autowired
    private FaturaPassagemService faturaPassagemService;
    @Autowired
    private ViagemRepository viagemRepository;
    @Autowired
    private ValidarCompraPassagens validarCompraPassagens;
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private UserLoguedComponent userLogued;

    public PassagemDTOEmpresaResponse findById(UUID id) {
        var modelo = passagemRepository.findByIdOrThr(id);
        return new PassagemDTOEmpresaResponse(modelo);
    }

    public byte[] getPassagemPdf(UUID idPassagem) {
        var passagemModel = passagemRepository.findByIdOrThr(idPassagem);
        PassagensPDF passagemPDF = new PassagensPDF();
        try {
            passagemPDF.addPassagem(passagemModel, passagemModel.getPreco().getEmpresa().getNome(), passagemModel.getFaturaPassagem().getMetodoPagamento());
            return passagemPDF.closePdfAndToBytes();
        } catch (IOException exception) {
            throw new RestRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR, "Houve um erro ao criar a passagem");
        }
    }

    public List<PassagemDTOEmpresaResponse> getPassagensByPreco(UUID idPreco) {
        var preco = precoRepository.findByIdOrThr(idPreco);
        userLogued.validIfIsAdminOrOwnerEmpresa(preco.getEmpresaId());
        return passagemRepository.findByPrecoIdAndEstaPago(idPreco, true).stream().map(PassagemDTOEmpresaResponse::new).toList();
    }

    private void validarParadasViagem(ViagemModel viagem, Integer idLugarSaida, Integer idLugarDestino) {
        var saida = viagem.getParadaByLugarId(idLugarSaida);
        var destino = viagem.getParadaByLugarId(idLugarDestino);
        if (saida == null) throw new ValidationException("idLugarSaida", "A saída não faz parte da viagem");
        else if (saida.getDataHora().isBefore(LocalDateTime.now().minusMinutes(tempoMinimoAntesCompraPassagem)))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O ônibus já iniciou o viagem");
        if (destino == null)
            throw new ValidationException("idLugarDestino", "O destino escolhido não faz parte da viagem");
    }

    @Transactional
    public FaturaPassagemModel saveCliente(PassagensDTO dto, BindingResult bindingResult) {
        var preco = precoRepository.findByIdOrThr(dto.idPreco());
        validarCompraPassagens.validarPassagensDTO(bindingResult, dto, "/passagens");
        var viagem = preco.getViagem();

        validarParadasViagem(viagem, dto.idLugarSaida(), dto.idLugarDestino());

        ParadaModel saida = viagem.getParadaByLugarId(dto.idLugarSaida());
        ParadaModel destino = viagem.getParadaByLugarId(dto.idLugarDestino());

        PisoModel pisoEscolhido = viagem.getOnibus().getPisoByNumero(preco.getNPiso());
        validarAssentos(pisoEscolhido, preco, dto.passagens());

        BigDecimal valorTotal = preco.getPreco().multiply(BigDecimal.valueOf(dto.passagens().size()));
        FaturaPassagemModel pago = faturaPassagemService.saveCliente(dto.contato(), valorTotal, null, TipoPagamento.QR, false);

        viagem.addValorArrecadadoWeb(pago.getValorTotal());

        viagemRepository.save(viagem);

        var passagensModel = dto.passagens().stream().map(passagem -> new PassagemModel(passagem, true, preco.getPreco(), false, false, saida, destino, preco, pago)).collect(Collectors.toList());
        passagemRepository.saveAll(passagensModel);
        return pago;
    }

    @Transactional
    public UUID saveEmpresa(PassagensDTOVenta dto) {
        var viagem = viagemRepository.findByIdOrThr(dto.idViagem());
        userLogued.validIfIsMyEmpresa(viagem.getEmpresaId());
        ValidEnabledEntities.validEmpresa(viagem.getEmpresa());

        validarCompraPassagens.validarPassagensDTOVenta(dto, "/passagens/vender");

        validarParadasViagem(viagem, dto.idLugarSaida(), dto.idLugarDestino());

        var saida = viagem.getParadaByLugarId(dto.idLugarSaida());
        var destino = viagem.getParadaByLugarId(dto.idLugarDestino());

        List<PassagemDTO> cadeirasPiso1 = new ArrayList<>(), cadeirasPiso2 = new ArrayList<>();

        PisoModel piso1 = viagem.getOnibus().getPisoByNumero(1);
        PisoModel piso2 = viagem.getOnibus().getPisoByNumero(2);


        for (PassagemDTO passagemFor : dto.passagens()) {
            if (passagemFor.nAssento() > 0 && passagemFor.nAssento() <= piso1.getNAssentos())
                cadeirasPiso1.add(passagemFor);
            else cadeirasPiso2.add(passagemFor);
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

        FaturaPassagemModel pago = faturaPassagemService.saveEmpresa(valorTotal, viagem, dto.metodoPagamento(), estaPago);

        viagem.addValorArrecadadoNaoWeb(pago.getValorTotal());
        if (dto.metodoPagamento().equals(TipoPagamento.DINHEIRO)) {
            emDinheiro = true;
            viagem.addValorArrecadadoDinheiro(pago.getValorTotal());
        }

        if (!cadeirasPiso1.isEmpty()) {
            atualizarNAssentosDisponiveis(preco1, cadeirasPiso1);
            precoRepository.save(preco1);
        }

        if (preco2 != null && !cadeirasPiso2.isEmpty()) {
            atualizarNAssentosDisponiveis(preco2, cadeirasPiso2);
            precoRepository.save(preco2);
        }

        viagemRepository.save(viagem); // Atualizar os valores arrecadados

        List<PassagemModel> passagens = new ArrayList<>();
        for (PassagemDTO passagemDTO : cadeirasPiso1) {
            var passagem = new PassagemModel(passagemDTO, false, preco1.getPreco(), estaPago, emDinheiro, saida, destino, preco1, pago);
            passagens.add(passagem);
        }

        if (preco2 != null) for (PassagemDTO passagemDTO : cadeirasPiso2) {
            var passagem = new PassagemModel(passagemDTO, false, preco2.getPreco(), estaPago, emDinheiro, saida, destino, preco2, pago);
            passagens.add(passagem);
        }

        passagemRepository.saveAll(passagens);

        return pago.getId();
    }

    public void validarAssentos(PisoModel piso, PrecoModel preco, List<PassagemDTO> sillasSolicitadas) {
        int numeroMinimo = piso.getPrimeiroAssento();
        int numeroMaximo = piso.getUltimoAssento();

        List<Integer> ocupados = passagemRepository.getPassagensVendidasENaoReembolsadas(preco.getId());

        if (preco.getNAssentosDisponiveis() < sillasSolicitadas.size())
            throw new ValidationException("passagens", "Não há assentos disponíveis");

        for (PassagemDTO sillasSolicitada : sillasSolicitadas) {
            for (Integer ocupado : ocupados)
                if (ocupado.equals(sillasSolicitada.nAssento()))
                    throw new ValidationException("nAssento", "O assento já foi comprado");

            if (sillasSolicitada.nAssento() > numeroMaximo || sillasSolicitada.nAssento() < numeroMinimo)
                throw new ValidationException("nAssento", "O número do assento informado é inválido");
        }
    }

    private void atualizarNAssentosDisponiveis(PrecoModel preco, List<PassagemDTO> assentos) {
        int nAssentosDiposniveis = preco.getNAssentosDisponiveis() - assentos.size();
        if (nAssentosDiposniveis < 0) throw new ValidationException("passagens", "Não há assentos disponíveis");
        if (nAssentosDiposniveis == 0) preco.setCheio(true);
        preco.setNAssentosDisponiveis(nAssentosDiposniveis);
    }

    public void delete(UUID idPassagem) {
        var model = passagemRepository.findByIdOrThr(idPassagem);

        if (!model.getFaturaPassagem().getEstaPago()) {
            log.error("Se tentou reembolsar uma passagem que não foi pago");
            throw new RestRuntimeException(HttpStatus.CONFLICT, "A passagem não foi paga");
        }

        if (model.getFaturaReembolso() != null)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "A passagem já foi rembolsado");
        var viagem = model.getPreco().getViagem();
        boolean resultado;
        if (model.getEmDinheiro()) {
            resultado = viagem.subtrairValorDinheiro(model.getPrecoPago());
        } else if (!model.getCompradoWeb()) resultado = viagem.subtrairValorNaoWeb(model.getPrecoPago());
        else {
            log.warn("Se precisa configurar uma API para a operação");
            // Neste caso não se pode fazer um reembolso sem uma API
            throw new RestRuntimeException(HttpStatus.CONFLICT, "A passagem foi comprado na web, o reembolso não esta disponível");
        }
        if (!resultado) {
            log.warn("Se tentou retirar um valor que não podia da caixa");
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O valor do dinheiro registrado é menor que o preço da passagem");
        }

        var faturaReembolsada = new FaturaReembolsoModel(model.getPrecoPago(), model.getFaturaPassagem(), model);
        model.setFaturaReembolso(faturaReembolsada);
        passagemRepository.save(model);
    }
}
