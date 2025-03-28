package com.alvaro.empresas.passagens.pagamentos.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.dtos.FaturaPasajeDTO;
import com.alvaro.empresas.passagens.dtos.pasagens.ContatoDTO;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.PassagensPDF;
import com.alvaro.empresas.passagens.models.ContatoModel;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaEmpresaModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.pagamentos.repositories.FaturaPassagemRepository;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.services.PrecoService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FaturaPassagemService {
    @Autowired
    private FaturaPassagemRepository faturaPassagemRepository;
    @Autowired
    private PassagemRepository passagemRepository;
    @Autowired
    private PrecoService precoService;
    @Autowired
    private ViagemRepository viagemRepository;

    public FaturaPassagemModel saveCliente(ContatoDTO contatoDTO, BigDecimal precoTotal, ViagemModel viagem, TipoPagamento metodo) {
        BigDecimal taxa = new BigDecimal("0.1");//Quanto será cobrado pelo serviço
        BigDecimal taxaServico = precoTotal.multiply(taxa);

        if (!metodo.equals(TipoPagamento.QR))
            throw new ValidationException("metodo", "Método de Pagamento inválido");

        var contatoModel = new ContatoModel(contatoDTO);
        var pagamento = new FaturaPassagemModel(precoTotal, BigDecimal.ZERO, taxaServico, false, metodo, viagem, null, contatoModel);
        return faturaPassagemRepository.save(pagamento);
    }

    public FaturaPassagemModel saveEmpresa(BigDecimal precoTotal, ViagemModel viagem, TipoPagamento metodo, boolean estaPago) {
        LocalDateTime dataPagamento = LocalDateTime.now();
        BigDecimal taxaServico = BigDecimal.ZERO;
        var pagamento = new FaturaPassagemModel(precoTotal, BigDecimal.valueOf(0), taxaServico, estaPago, metodo, viagem, dataPagamento, null);
        return faturaPassagemRepository.save(pagamento);
    }

    //O tipo de retorno é vazio, mas estamos colocando booleano por teste

    @Transactional
    public void pagarQr(UUID idPagamento) {//
        FaturaPassagemModel pagamento = faturaPassagemRepository.findById(idPagamento).orElseThrow(() -> new ObjectNotFoundException(idPagamento, FaturaPassagemModel.class.getName()));
        if (pagamento.getEstaPago()) {
            reembolso();
            mandarEmail("O preço já foi pago");
            throw new RestRuntimeException(HttpStatus.CONFLICT, "O preço já foi pago");
        }

        PrecoModel preco = pagamento.getPassagens().get(0).getPreco();
        List<Integer> assentosVendidos = passagemRepository.getPasajesVendidosAndNoRembolso(preco.getId());

        int nPassagens = 0;
        for (PassagemModel passagem : pagamento.getPassagens()) {
            if (assentosVendidos.contains(passagem.getNAssento())) {
                reembolso();
                mandarEmail("Um dos assentos já foi pago, o pagamento foi cancelado");
                throw new RestRuntimeException(HttpStatus.CONFLICT, "Um dos assentos já foi pago, o pagamento foi cancelado");
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

        precoService.updateFromService(preco);
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

    public void codigoVencido(UUID idPagamento) {//
        FaturaPassagemModel pagamento = faturaPassagemRepository.findById(idPagamento).orElseThrow(() -> new ObjectNotFoundException(idPagamento, FaturaPassagemModel.class.getName()));
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
        var fatura = faturaPassagemRepository.findById(id);
        PassagensPDF passagensPDF = new PassagensPDF();
        byte[] arrayBytesVazio = new byte[0];

        if (!fatura.isPresent())
            throw new ObjectNotFoundException(id, FaturaEmpresaModel.class.getName());
        if (fatura.get().getViagem() == null)
            throw new ValidationException("protocolo", "O comprovante possui uma viagem nula");
        String nomeEmpresa = fatura.get().getViagem().getEmpresa().getNome();
        ParadaModel saida = fatura.get().getPassagens().get(0).getSaida();
        ParadaModel destino = fatura.get().getPassagens().get(0).getDestino();
        try {
            for (PassagemModel passagemModel : fatura.get().getPassagens())
                passagensPDF.addPasaje(passagemModel, nomeEmpresa, saida, destino, fatura.get().getMetodoPagamento());
            arrayBytesVazio = passagensPDF.closeAndGetBytes();
            return arrayBytesVazio;
        } catch (IOException exception) {
            throw new ValidationException("passagens", "Houve um erro na hora de criar os bilhetes");
        }
    }

    public Page<FaturaPasajeDTO> findAllFromViagem(UUID idViagem, Pageable pageable) {
        if (idViagem == null)
            throw new RestRuntimeException(HttpStatus.BAD_REQUEST, "ID da viagem não pode ser nulo");
        Page<FaturaPassagemModel> models = faturaPassagemRepository.findByViagemId(idViagem, pageable);
        return models.map(FaturaPasajeDTO::new);
    }
}