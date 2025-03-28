package com.alvaro.empresas.passagens.configuracoes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.alvaro.empresas.passagens.enums.TipoParada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;
import com.alvaro.empresas.passagens.onibus.repositories.OnibusRepository;
import com.alvaro.empresas.passagens.onibus.repositories.PisoRepository;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.CidadeRepository;
import com.alvaro.empresas.passagens.paradas.repositories.DepartamentoRepository;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.repositories.ViagemRepository;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;

@Profile({"h2", "mysql"})
@Configuration
public class DataLoader {

    private static final List<String> empresasName = List.of("MARZO", "ABRIL", "COPACABANA");
    @Autowired
    private RoleService roleService;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private OnibusRepository onibusRepository;
    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // private PrecioRepository precioRepository;
    // private FacturaPasajeRepository facturaPasajeRepository;
    // private PasajeRepository pasajeRepository;
    @Bean
    public String loadData() {
        List<EmpresaModel> empresas = loadEmpresas();
        loadUsers(empresas);
        List<LugarModel> lugares = loadLugares();
        List<OnibusModel> autobuses = loadAutobuses(empresas);
        if (lugares.size() < 5) {
            System.out.println("Numero insuficientes de lugares");
            return "";
        }
        // Cria os Pisos
        int indice = 0;
        int nLinhas = 12;
        int nColunas = 3;
        for (OnibusModel autobus : autobuses) {
            List<PisoModel> pisos = new ArrayList<>();
            pisos.add(new PisoModel(nLinhas, nColunas, TipePosicao.IZQUIERDA, 1, TipePosicao.DERECHA,
                    nLinhas * nColunas, 1, autobus));
            if (indice % 2 == 0) {
                pisos.add(new PisoModel(nLinhas, nColunas, TipePosicao.IZQUIERDA, 2, TipePosicao.DERECHA,
                        nLinhas * nColunas, nLinhas * nColunas + 1, autobus));
            }
            pisoRepository.saveAll(pisos);

            indice++;

            // Cria os trayectos y las paradas de cada autobus
            LocalDateTime dia1SemanaAntes = LocalDateTime.now().minusDays(2);
            int j;
            for (j = 0; j < 15; j++) {
                dia1SemanaAntes = dia1SemanaAntes.plusDays(1);
                var dataInicio = dia1SemanaAntes.withHour(15).withMinute(0).withSecond(0).withNano(0);
                var viaje = viagemRepository.save(new ViagemModel(autobus, autobus.getEmpresa(), BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, false, dataInicio));

                List<ParadaModel> paradas = new ArrayList<>();

                var parada = new ParadaModel(dataInicio, 10, TipoParada.SAIDA, lugares.get(0), viaje);
                dataInicio = dataInicio.plusHours(2);
                paradas.add(parada);
                for (int i = 1; i < 4; i++) {
                    dataInicio = dataInicio.plusHours(2);
                    parada = new ParadaModel(dataInicio, 10, TipoParada.CAMINO, lugares.get(i), viaje);
                    paradas.add(parada);
                }
                dataInicio = dataInicio.plusHours(2);
                parada = new ParadaModel(dataInicio, 20, TipoParada.DESTINO, lugares.get(5), viaje);
                paradas.add(parada);

                paradaRepository.saveAll(paradas);
            }
        }
        return "Sucesso";
    }

    private void loadUsers(List<EmpresaModel> empresas) {
        var admin = roleService.save(new RoleModel(RoleList.ROLE_ADMIN));
        var cliente = roleService.save(new RoleModel(RoleList.ROLE_CLIENTE));
        var empresaAdmin = roleService.save(new RoleModel(RoleList.ROLE_EMPRESA_ADMIN));
        var empresaFuncionario = roleService.save(new RoleModel(RoleList.ROLE_EMPRESA_FUNCIONARIO));
        ArrayList<UsuarioModel> users = new ArrayList<>();

        var usuarioCliente = new UsuarioModel("cliente@gmail.com", "Rick Sanchez", "(11) - 11111-1111",
                passwordEncoder.encode("cliente123"));
        usuarioCliente.setRoles(new HashSet<RoleModel>(Arrays.asList(cliente)));
        users.add(usuarioCliente);
        var usuarioAdmin = new UsuarioModel("admin@gmail.com", "Darth Vader", "(11) - 11111-1111",
                passwordEncoder.encode("admin123"));
        usuarioAdmin.setRoles(new HashSet<RoleModel>(Arrays.asList(admin, cliente)));
        users.add(usuarioAdmin);

        String nickNameAdmin, nickNameFuncionario;
        for (int i = 0; i < empresasName.size(); i++) {
            String value = empresasName.get(i);
            nickNameAdmin = value.toString().toLowerCase() + "admin";
            nickNameFuncionario = value.toString().toLowerCase() + "funcionario";

            var usuarioEmpresaAdmin = new UsuarioModel(nickNameAdmin + "@gmail.com", "Hero Nakamura",
                    "(33) - 33333-3333", passwordEncoder.encode(nickNameAdmin), empresas.get(i).getId());
            usuarioEmpresaAdmin
                    .setRoles(new HashSet<RoleModel>(Arrays.asList(empresaAdmin, empresaFuncionario, cliente)));

            users.add(usuarioEmpresaAdmin);

            var usuarioFuncionario = new UsuarioModel(nickNameFuncionario + "@gmail.com", "Rick Sanchez",
                    "(11) - 11111-1111", passwordEncoder.encode(nickNameFuncionario),
                    empresas.get(i).getId());
            usuarioFuncionario.setRoles(new HashSet<RoleModel>(Arrays.asList(empresaFuncionario, cliente)));
            users.add(usuarioFuncionario);
            if (i == 2)
                break;
        }
        usuarioRepository.saveAll(users);
    }

    private List<EmpresaModel> loadEmpresas() {
        List<EmpresaModel> empresas = new ArrayList<>();
        for (String empresaName : empresasName) {
            var empresa = new EmpresaModel(empresaName, "https://github.com/alvaro-star.png", "202345", true, false);
            empresas.add(empresa);
        }
        return empresaRepository.saveAll(empresas);
    }

    private List<LugarModel> loadLugares() {
        List<String> nombresDepartamentos = Arrays.asList("Santa Cruz", "La Paz", "Cochabamba", "Oruro", "Potosí",
                "Tarija", "Chuquisaca", "Pando", "Beni");
        List<LugarModel> lugares = new ArrayList<>();
        for (String nombreDepartamento : nombresDepartamentos) {
            var depModel = departamentoRepository.save(new DepartamentoModel(nombreDepartamento, "SC"));
            var ciudad = cidadeRepository.save(new CidadeModel(nombreDepartamento, depModel));
            var lugar = lugarRepository.save(new LugarModel("Terminal de " + nombreDepartamento, ciudad));
            lugares.add(lugar);
        }
        return lugares;
    }

    private List<OnibusModel> loadAutobuses(List<EmpresaModel> empresas) {
        ArrayList<OnibusModel> autobuses = new ArrayList<>();
        int nRepeticoes = 3;
        char terminalPlacaChar;
        String terminalPlaca;
        for (int i = 0; i < empresasName.size(); i++) {
            String empresaName = empresasName.get(i);
            terminalPlacaChar = empresaName.charAt(0);
            terminalPlaca = Character.toString(terminalPlacaChar);
            for (int k = 0; k < nRepeticoes; k++)
                autobuses.add(new OnibusModel(k + terminalPlaca + terminalPlaca + terminalPlaca, true,
                        empresas.get(i)));
            if (i == 2)
                break;
        }

        return onibusRepository.saveAll(autobuses);
    }
}
