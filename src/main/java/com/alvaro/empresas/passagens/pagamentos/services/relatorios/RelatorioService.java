package com.alvaro.empresas.passagens.pagamentos.services.relatorios;

import com.alvaro.empresas.passagens.dtos.viagens.JPQL.PassagemJPQLBusca;
import com.alvaro.empresas.passagens.dtos.viagens.JPQL.ViagemDTOJPQLRelatorio;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.helpers.thymeleaf.CidadeTHModel;
import com.alvaro.empresas.passagens.helpers.thymeleaf.MetodoTHModel;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.pagamentos.dtos.RelatorioSolicitacaoDTO;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.services.LugarService;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.services.EmpresaService;
import com.alvaro.empresas.passagens.services.validacao.TempoViagemService;
import com.itextpdf.kernel.geom.PageSize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
public class RelatorioService {
    private final EmpresaService empresaService;
    private final PassagemRepository passagemRepository;
    private final EmailService emailService;
    private final DateAuxiliarFunctions dateAuxiliarFunctions;
    private final PDFThymeleaf pdfThymeleaf;
    private final LugarService lugarService;
    private final TempoViagemService tempoViagemService;

    public RelatorioService(EmpresaService empresaService, PassagemRepository passagemRepository,
            EmailService emailService, DateAuxiliarFunctions dateAuxiliarFunctions, PDFThymeleaf pdfThymeleaf,
            LugarService lugarService, TempoViagemService tempoViagemService) {
        this.empresaService = empresaService;
        this.passagemRepository = passagemRepository;
        this.emailService = emailService;
        this.dateAuxiliarFunctions = dateAuxiliarFunctions;
        this.pdfThymeleaf = pdfThymeleaf;
        this.lugarService = lugarService;
        this.tempoViagemService = tempoViagemService;
    }

    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViagemDias;

    // Categorize cidades pelo número de passageiros indo para elas, não por ônibus
    // Ordena as listas de cidades com base no número de passagens vendidas
    public byte[] makeRelatorioMensal(RelatorioSolicitacaoDTO solicitacaoDTO) {
        var empresa = empresaService.findById(solicitacaoDTO.idEmpresa());
        LocalDateTime inicio = dateAuxiliarFunctions.getFirstDayOfMonth(solicitacaoDTO.data());
        LocalDateTime fim = dateAuxiliarFunctions.getLastDayOfMonth(solicitacaoDTO.data());
        List<ViagemDTOJPQLRelatorio> viagens = tempoViagemService.findViagensFromEmpresa(empresa, inicio, fim);

        HashMap<Integer, Integer> saidasIdNPassagens = new HashMap<>(), destinosIdNPassagens = new HashMap<>();
        List<PassagemJPQLBusca> passagensBD;
        RelatorioModel relatorio = new RelatorioModel(empresa);

        HashMap<String, HashMetodoPagamentoValor> pagamentosWeb = new HashMap<>(), pagamentosNaoWeb = new HashMap<>();
        for (TipoPagamento metodo : TipoPagamento.values()) {
            pagamentosWeb.put(metodo.toString(), new HashMetodoPagamentoValor(metodo.toString(), 0.0));
            pagamentosNaoWeb.put(metodo.toString(), new HashMetodoPagamentoValor(metodo.toString(), 0.0));
        }

        for (ViagemDTOJPQLRelatorio viagem : viagens) {
            relatorio.nViagens++;
            if (viagem.viagem().isCancelado())
                relatorio.nViagensCanceladas++;

            for (PrecoModel preco : viagem.viagem().getPrecos()) {
                passagensBD = passagemRepository.getPassagensPagas(preco.getId());
                classificarPassagemFromPreco(passagensBD, saidasIdNPassagens, destinosIdNPassagens, pagamentosWeb,
                        pagamentosNaoWeb, relatorio);
            }
        }

        List<LugarModel> saidas = lugarService.findAllById(saidasIdNPassagens.keySet());
        List<LugarModel> destinos = lugarService.findAllById(destinosIdNPassagens.keySet());

        ordenarLugares(saidas, saidasIdNPassagens);
        ordenarLugares(destinos, destinosIdNPassagens);

        relatorio.setValorArrecadadoWeb(getValorTotalArrecadado(pagamentosWeb));
        relatorio.setValorArrecadadoNaoWeb(getValorTotalArrecadado(pagamentosNaoWeb));
        relatorio.setDinheiroPorMetodoNaoWeb(pagamentosNaoWeb);
        relatorio.setDinheiroPorMetodoWeb(pagamentosWeb);
        relatorio.setNMes(inicio.getMonthValue());
        relatorio.setNAno(inicio.getYear());

        List<CidadeTHModel> saidasThModels = new ArrayList<>(), destinosThModels = new ArrayList<>();
        List<MetodoTHModel> metodos = new ArrayList<>();
        for (LugarModel saida : saidas)
            saidasThModels.add(new CidadeTHModel(saida.getCidade().getNome(), saidasIdNPassagens.get(saida.getId())));
        for (LugarModel destino : destinos)
            destinosThModels
                    .add(new CidadeTHModel(destino.getCidade().getNome(), destinosIdNPassagens.get(destino.getId())));

        for (TipoPagamento value : TipoPagamento.values()) {
            metodos.add(
                    new MetodoTHModel(value.toString(), relatorio.getDinheiroPorMetodoWeb().get(value.toString()).valor,
                            relatorio.getDinheiroPorMetodoNaoWeb().get(value.toString()).valor));
        }

        return generatePdfFromHtml(relatorio, saidasThModels, destinosThModels, metodos);
    }

    public void ordenarLugares(List<LugarModel> lugares, HashMap<Integer, Integer> lugaresNPassagens) {
        lugares.sort(Comparator.comparingInt(l -> lugaresNPassagens.get(l.getId())));
    }

    public void classificarPassagemFromPreco(List<PassagemJPQLBusca> passagens, HashMap<Integer, Integer> saidasId,
            HashMap<Integer, Integer> destinosId, HashMap<String, HashMetodoPagamentoValor> pagamentosWeb,
            HashMap<String, HashMetodoPagamentoValor> pagamentosNaoWeb, RelatorioModel relatorio) {

        relatorio.nPassagensTotal += passagens.size();
        for (PassagemJPQLBusca passagem : passagens) {
            addValueInHashMap(saidasId, passagem.saidaLugarId());
            addValueInHashMap(destinosId, passagem.destinoLugarId());
            if (passagem.faturaReembolsoId() != null) {
                relatorio.nPassagensCanceladas++;
                continue;
            }
            if (passagem.isCompradoWeb()) {
                pagamentosWeb.get(passagem.metodoPagamento().toString()).valor += passagem.precoPago().doubleValue();
                if (passagem.emDinheiro())
                    emailService.mandarEmail("vargasaveo248@gmail.com", "Web - Erro de Processamento",
                            "Existe uma passagem que foi comprada em dinheiro");
            } else {
                pagamentosNaoWeb.get(passagem.metodoPagamento().toString()).valor += passagem.precoPago().doubleValue();
            }
        }
    }

    public double getValorTotalArrecadado(HashMap<String, HashMetodoPagamentoValor> pagamento) {
        double sum = 0;
        for (HashMetodoPagamentoValor value : pagamento.values())
            sum += value.valor;
        return sum;
    }

    public void addValueInHashMap(HashMap<Integer, Integer> hashMap, Integer key) {
        Integer auxMap = hashMap.get(key);
        if (auxMap == null)
            hashMap.put(key, 0);
        else
            hashMap.put(key, auxMap + 1);
    }

    public byte[] generatePdfFromHtml(RelatorioModel relatorio, List<CidadeTHModel> saidasThModels,
            List<CidadeTHModel> destinosThModels, List<MetodoTHModel> metodos) {
        var context = new Context();
        context.setVariable("empresaNome", relatorio.getEmpresa().getNome());
        context.setVariable("nMes", relatorio.getNMes());
        context.setVariable("nAno", relatorio.getNAno());
        context.setVariable("nViagens", relatorio.getNViagens());
        context.setVariable("nViagensCanceladas", relatorio.getNViagensCanceladas());
        context.setVariable("nPassagensVendidas", relatorio.getNPassagensTotal());
        context.setVariable("nPassagensCanceladas", relatorio.getNPassagensCanceladas());
        context.setVariable("saidas", saidasThModels);
        context.setVariable("destinos", destinosThModels);
        context.setVariable("metodos", metodos);
        context.setVariable("valorArrecadadoWeb", relatorio.getValorArrecadadoWeb());
        context.setVariable("valorArrecadadoNaoWeb", relatorio.getValorArrecadadoNaoWeb());
        context.setVariable("valorTotal", relatorio.getValorArrecadadoNaoWeb() + relatorio.getValorArrecadadoWeb());
        return pdfThymeleaf.generatePDFByTemplate("/empresa/relatorio", context, PageSize.A4);
    }
}