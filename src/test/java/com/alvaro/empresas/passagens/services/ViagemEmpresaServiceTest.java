package com.alvaro.empresas.passagens.services;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import com.alvaro.empresas.passagens.helpers.utils.DateTimeUtils;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.onibus.services.OnibusService;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.PrecoRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.services.RepositoryMocks.LugarRepositoryMocker;
import com.alvaro.empresas.passagens.services.RepositoryMocks.OnibusRepositoryMock;
import com.alvaro.empresas.passagens.services.RepositoryMocks.ViagemRepositoryMock;
import com.alvaro.empresas.passagens.configuracoes.validations.services.TempoViagemService;

@SpringBootTest
@ComponentScan(basePackages = {
        "com.alvaro.empresas.passagens.services.RepositoryMocks" }, includeFilters = @ComponentScan.Filter(Service.class))
@ActiveProfiles("test")
class ViagemEmpresaServiceTest {
    @Mock
    private TempoViagemService tempoViagemService;

    @Mock
    private EmpresaService emrEmpresaService;

    @Mock
    private ViagemRepository viajeRepository;

    @Mock
    private ParadaRepository paradaRepository;

    @Mock
    private PrecoService precoService;

    @Mock
    private LugarRepository lugarRepository;

    @Mock
    private DateTimeUtils helperDate;

    @Mock
    private PrecoRepository precoRepository;

    @Mock
    private OnibusService onibusService;

    @InjectMocks
    private ViagemEmpresaService viagemEmpresaService; // Sua classe onde as dependências são injetadas

    @Autowired
    private ViagemRepositoryMock viajeRepositoryMock;
    @Autowired
    private LugarRepositoryMocker lugarRepositoryMocker;
    @Autowired
    private OnibusRepositoryMock onibusRepositoryMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    public EmpresaModel generateEmpresa(String nome) {
        var empresa = new EmpresaModel(nome, "", "11111", true, false);
        empresa.setId(UUID.randomUUID());
        return empresa;
    }
    /*
     * void testarSaveOneCopyWithDiffDays(ViagemModel viaje, int
     * diffDiasEntreViagens) {
     * var date =
     * viaje.getSaida().getDataHora().plusDays(diffDiasEntreViagens).toLocalDate();
     * var solicitud = new ViagemDTOFormCopy(viaje.getId(), date);
     * 
     * var viajeCopia = viagemEmpresaService.saveOneCopy(solicitud, viaje);
     * assertTrue(() -> {
     * for (ParadaModel paradaOriginal : viaje.getParadas()) {
     * var paradaCopia = viajeCopia.getParadaByLugarId(paradaOriginal.getLugarId());
     * if (!paradaCopia.getTipo().equals(paradaOriginal.getTipo()))
     * return false;
     * var diffDiasParada = ChronoUnit.DAYS.between(paradaOriginal.getDataHora(),
     * paradaCopia.getDataHora());
     * if (diffDiasParada != diffDiasEntreViagens)
     * return false;
     * }
     * return true;
     * }, "Verifica se as paradas possuem um deslocamento temporal correto");
     * }
     * 
     * @Test
     * 
     * @DisplayName("Valida si el metodo creo de forma correcta las fechas de una copia de un viaje"
     * )
     * void saveOneCopy() {
     * var empresa = generateEmpresa("23 de marzo");
     * var onibus = onibusRepositoryMock.generateOnibus("2023", true, empresa);
     * var dataInicioViagem = LocalDateTime.now();
     * List<LugarModel> lugares = Arrays.stream(LugarEnum.values())
     * .map(value ->
     * lugarRepositoryMocker.generateLugar(value.toString())).toList();
     * var viaje = viajeRepositoryMock.createViagem(onibus, dataInicioViagem,
     * lugares, 2);
     * 
     * when(tempoViagemService.existsViagensActiveFromOnibus(any(), any(),
     * any())).thenReturn(false);
     * when(viajeRepository.save(any())).thenAnswer(invocation -> {
     * ViagemModel model = invocation.getArgument(0, ViagemModel.class);
     * model.setId(UUID.randomUUID());
     * return model;
     * });
     * 
     * testarSaveOneCopyWithDiffDays(viaje, 5);
     * testarSaveOneCopyWithDiffDays(viaje, -5);
     * 
     * }
     */
}