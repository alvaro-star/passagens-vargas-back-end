package com.alvaro.empresas.passagens.pagamentos.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.dtos.pasagens.ContatoInputDTO;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.PassagensPDF;
import com.alvaro.empresas.passagens.models.ContatoModel;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FaturaPassagemRepository;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;

@Service
public class FaturaPassagemService {
    @Autowired
    private FaturaPassagemRepository faturaPassagemRepository;
    @Autowired
    private PassagemRepository passagemRepository;
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private ViagemRepository viagemRepository;

    public FaturaPassagemModel saveCliente(ContatoInputDTO contatoInputDTO, BigDecimal precoTotal, ViagemModel viagem, TipoPagamento metodo, boolean estaPago) {
        BigDecimal taxa = new BigDecimal("0.1");//Quanto será cobrado pelo serviço
        BigDecimal taxaServico = precoTotal.multiply(taxa);

        if (!metodo.equals(TipoPagamento.QR))
            throw new ValidationException("metodo", "Método de Pagamento inválido");

        LocalDateTime dataPagamento = (estaPago) ? LocalDateTime.now() : null;
        var contatoModel = new ContatoModel(contatoInputDTO);
        var pagamento = new FaturaPassagemModel(precoTotal, BigDecimal.ZERO, taxaServico, estaPago, metodo, viagem, dataPagamento, contatoModel);
        return faturaPassagemRepository.save(pagamento);
    }

    public FaturaPassagemModel saveEmpresa(BigDecimal precoTotal, ViagemModel viagem, TipoPagamento metodo, boolean estaPago) {
        LocalDateTime dataPagamento = (estaPago) ? LocalDateTime.now() : null;
        BigDecimal taxaServico = BigDecimal.ZERO;

        var pagamento = new FaturaPassagemModel(precoTotal, BigDecimal.ZERO, taxaServico, estaPago, metodo, viagem, dataPagamento, null);
        return faturaPassagemRepository.save(pagamento);
    }

    @Transactional
    public void pagarQr(UUID idPagamento) {//
        FaturaPassagemModel pagamento = faturaPassagemRepository.findById(idPagamento).orElseThrow(() -> new EntityNotFoundException(idPagamento, FaturaPassagemModel.class));
        if (pagamento.getEstaPago()) {
            reembolso();
            mandarEmail("O preço da passagem já foi pago");
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O preço já foi pago");
        }

        PrecoModel preco = pagamento.getPassagens().get(0).getPreco();
        List<Integer> assentosVendidos = passagemRepository.getPassagensVendidasENaoReembolsadas(preco.getId());

        int nPassagens = 0;
        for (PassagemModel passagem : pagamento.getPassagens()) {
            if (assentosVendidos.contains(passagem.getNAssento())) {
                reembolso();
                mandarEmail("Um dos assentos já foi vendido, o pagamento foi cancelado");
                throw new RestRuntimeException(HttpStatus.CONFLICT, "Um dos assentos já foi vendido, o pagamento foi cancelado");
            }
            nPassagens++;
        }

        int nAssentosDisponiveis = preco.getNAssentosDisponiveis() - nPassagens;
        if (nAssentosDisponiveis == 0) {
            preco.setNAssentosDisponiveis(0);
            preco.setCheio(true);
        } else if (nAssentosDisponiveis > 0) {
            preco.setNAssentosDisponiveis(nAssentosDisponiveis);
        } else {
            mandarEmail("Não há assentos disponíveis");
            return;
        }

        precoRepository.save(preco);
        passagemRepository.updateValuePagado(pagamento.getId(), true);
        var viagem = calcularValorArrecadado(pagamento);
        viagemRepository.save(viagem);
        pagamento.setEstaPago(true);
        pagamento.setDataPagamento(LocalDateTime.now());
        faturaPassagemRepository.save(pagamento);
    }

    private static ViagemModel calcularValorArrecadado(FaturaPassagemModel pagamento) {
        var viagem = pagamento.getViagem();
        BigDecimal valorTotalPago = pagamento.getValorTotal() != null ? pagamento.getValorTotal() : BigDecimal.ZERO;
        if (pagamento.getPassagens().get(0).getCompradoWeb()) {
            BigDecimal valorArrecadadoWeb = viagem.getValorArrecadadoWeb() != null ? viagem.getValorArrecadadoWeb() : BigDecimal.ZERO;
            viagem.setValorArrecadadoWeb(valorArrecadadoWeb.add(valorTotalPago));
        } else {
            BigDecimal valorArrecadadoNaoWeb = viagem.getValorArrecadadoNaoWeb() != null ? viagem.getValorArrecadadoNaoWeb() : BigDecimal.ZERO;
            viagem.setValorArrecadadoNaoWeb(valorArrecadadoNaoWeb.add(valorTotalPago));
        }
        return viagem;
    }

    public void deletePassagemCodigoVencido(UUID idPagamento) {
        FaturaPassagemModel pagamento = faturaPassagemRepository.findByIdOrThr(idPagamento);
        if (!pagamento.getEstaPago()) {
            for (PassagemModel passagem : pagamento.getPassagens())
                passagemRepository.delete(passagem);
        }
    }

    public void gerarQr(Float valor) {
    }

    public void reembolso() {
    }

    public void mandarEmail(String mensagem) {
    }

    public byte[] downloadFatura(UUID id) {
        var fatura = faturaPassagemRepository.findByIdOrThr(id);
        PassagensPDF pdfPassagens = new PassagensPDF();

        if (fatura.getViagemId() == null)
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O comprovante possui uma viagem nula");
        String nomeEmpresa = fatura.getViagem().getEmpresa().getNome();

        try {
            for (PassagemModel passagemModel : fatura.getPassagens())
                pdfPassagens.addPassagem(passagemModel, nomeEmpresa, fatura.getMetodoPagamento());
            return pdfPassagens.closePdfAndToBytes();
        } catch (IOException exception) {
            throw new RestRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR, "Houve um erro na hora de criar o documento com os bilhetes");
        }
    }

    public PageOutput<FaturaPassagemModel> findAllFromViagem(UUID idViagem, Pageable pageable) {
        if (idViagem == null)
            throw new RestRuntimeException(HttpStatus.BAD_REQUEST, "ID da viagem não pode ser nulo");
        var models = faturaPassagemRepository.findByViagemId(idViagem, pageable);
        return new PageOutput<>(models);
    }
}