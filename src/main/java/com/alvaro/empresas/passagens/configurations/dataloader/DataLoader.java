package com.alvaro.empresas.passagens.configurations.dataloader;

import com.alvaro.empresas.passagens.autobuses.enums.TypePosicao;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
import com.alvaro.empresas.passagens.enums.TypeParada;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.pagos.models.FacturaPasajeModel;
import com.alvaro.empresas.passagens.pagos.repositories.FacturaPasajeRepository;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.CiudadRepository;
import com.alvaro.empresas.passagens.paradas.repositories.DepartamentoRepository;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.*;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Profile({"h2", "devsql", "share"})
@Configuration
public class DataLoader {
    @Autowired
    private RoleService roleService;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private CiudadRepository ciudadRepository;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private AutobusRepository autobusRepository;
    @Autowired
    private PisoRepository pisoRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private PrecioRepository precioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasajeRepository pasajeRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private FacturaPasajeRepository facturaPasajeRepository;

    @Bean
    public String loadData() {
        List<EmpresaModel> empresas = loadEmpresas();
        loadUsers(empresas);
        List<LugarModel> lugares = loadLugares();
        List<AutobusModel> autobuses = loadAutobuses(empresas);
        if (lugares.size() < 5) {
            System.out.println("Numero insuficientes de lugares");
            return "";
        }
        //Cria os Pisos
        int indice = 0;
        int nLinhas = 12;
        int nColunas = 3;
        for (AutobusModel autobus : autobuses) {
            List<PisoModel> pisos = new ArrayList<>();
            pisos.add(new PisoModel(nLinhas, nColunas, TypePosicao.IZQUIERDA, 1, TypePosicao.DERECHA, nLinhas * nColunas, 1, autobus));
            if (indice % 2 == 0) {
                pisos.add(new PisoModel(nLinhas, nColunas, TypePosicao.IZQUIERDA, 2, TypePosicao.DERECHA, nLinhas * nColunas, nLinhas * nColunas + 1, autobus));
            }
            pisoRepository.saveAll(pisos);

            indice++;

            //Cria os trayectos y las paradas de cada autobus
            LocalDateTime dia1SemanaAntes = LocalDateTime.now().minusDays(2);
            int j;
            for (j = 0; j < 15; j++) {
                dia1SemanaAntes = dia1SemanaAntes.plusDays(1);
                var dataInicio = dia1SemanaAntes.withHour(15).withMinute(0).withSecond(0).withNano(0);
                var viaje = viajeRepository.save(new ViajeModel(autobus, autobus.getEmpresa(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false, dataInicio));

                List<ParadaModel> paradas = new ArrayList<>();

                var parada = new ParadaModel(dataInicio, 10, TypeParada.SALIDA, lugares.get(0), viaje, viaje.getEmpresa());
                dataInicio = dataInicio.plusHours(2);
                paradas.add(parada);
                for (int i = 1; i < 4; i++) {
                    dataInicio = dataInicio.plusHours(2);
                    parada = new ParadaModel(dataInicio, 10, TypeParada.CAMINO, lugares.get(i), viaje, viaje.getEmpresa());
                    paradas.add(parada);
                }
                dataInicio = dataInicio.plusHours(2);
                parada = new ParadaModel(dataInicio, 20, TypeParada.DESTINO, lugares.get(5), viaje, viaje.getEmpresa());
                paradas.add(parada);


                paradaRepository.saveAll(paradas);
                BigDecimal precioBruto = BigDecimal.valueOf(200);
                BigDecimal valorTotal;
                for (PisoModel piso : pisos) {
                    var precio = precioRepository.save(new PrecioModel(precioBruto, piso.getNPiso(), piso.getNSillas(), viaje, viaje.getEmpresa()));
                    precioBruto = precioBruto.subtract(BigDecimal.valueOf(20));
                    var facturaPasaje = new FacturaPasajeModel(precioBruto, BigDecimal.ZERO, BigDecimal.ZERO, true, TipoPagamento.EFECTIVO, precio.getViaje(), LocalDateTime.now(), null);
                    facturaPasajeRepository.save(facturaPasaje);

                    var pasaje = new PasajeModel(piso.getPrimeraSilla() + 3, false, precioBruto, true, true, "Alvaro Vargas Alvarez", "3308731", new Date(2000, 1, 1), paradas.get(0), paradas.get(2), precio, facturaPasaje);
                    pasajeRepository.save(pasaje);
                }
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

        var usuarioCliente = new UsuarioModel("cliente@gmail.com", "Rick Sanchez", "(11) - 11111-1111", passwordEncoder.encode("cliente123"));
        usuarioCliente.setRoles(new HashSet<RoleModel>(Arrays.asList(cliente)));
        users.add(usuarioCliente);
        var usuarioAdmin = new UsuarioModel("admin@gmail.com", "Darth Vader", "(11) - 11111-1111", passwordEncoder.encode("admin123"));
        usuarioAdmin.setRoles(new HashSet<RoleModel>(Arrays.asList(admin, cliente)));
        users.add(usuarioAdmin);

        String nickNameAdmin, nickNameFuncionario;
        for (ENUM_EMPRESAS value : ENUM_EMPRESAS.values()) {
            nickNameAdmin = value.toString().toLowerCase() + "admin";
            nickNameFuncionario = value.toString().toLowerCase() + "funcionario";

            var usuarioEmpresaAdmin = new UsuarioModel(nickNameAdmin + "@gmail.com", "Hero Nakamura", "(33) - 33333-3333", passwordEncoder.encode(nickNameAdmin), empresas.get(value.ordinal()).getId());
            usuarioEmpresaAdmin.setRoles(new HashSet<RoleModel>(Arrays.asList(empresaAdmin, empresaFuncionario, cliente)));

            users.add(usuarioEmpresaAdmin);

            var usuarioFuncionario = new UsuarioModel(nickNameFuncionario + "@gmail.com", "Rick Sanchez", "(11) - 11111-1111", passwordEncoder.encode(nickNameFuncionario), empresas.get(value.ordinal()).getId());
            usuarioFuncionario.setRoles(new HashSet<RoleModel>(Arrays.asList(empresaFuncionario, cliente)));
            users.add(usuarioFuncionario);
            if (value.ordinal() == 2) break;
        }
        usuarioRepository.saveAll(users);
    }

    private List<EmpresaModel> loadEmpresas() {
        List<EmpresaModel> empresas = new ArrayList<>();
        for (ENUM_EMPRESAS value : ENUM_EMPRESAS.values()) {
            var empresa = new EmpresaModel(value.toString(), "https://github.com/alvaro-star.png", "202345", true, false);
            empresas.add(empresa);
        }
        empresaRepository.saveAll(empresas);
        return empresas;
    }

    private List<LugarModel> loadLugares() {
        List<String> nombresDepartamentos = Arrays.asList("Santa Cruz", "La Paz", "Cochabamba", "Oruro", "Potosí", "Tarija", "Chuquisaca", "Pando", "Beni");
        List<LugarModel> lugares = new ArrayList<>();
        for (String nombreDepartamento : nombresDepartamentos) {
            var depModel = departamentoRepository.save(new DepartamentoModel(nombreDepartamento, "SC"));
            var ciudad = ciudadRepository.save(new CiudadModel(nombreDepartamento, depModel));
            var lugar = lugarRepository.save(new LugarModel("Terminal de " + nombreDepartamento, ciudad));
            lugares.add(lugar);
        }
        return lugares;
    }

    private List<AutobusModel> loadAutobuses(List<EmpresaModel> empresas) {
        ArrayList<AutobusModel> autobuses = new ArrayList<>();
        int nRepeticoes = 3;
        char terminalPlacaChar;
        String terminalPlaca;
        for (ENUM_EMPRESAS value : ENUM_EMPRESAS.values()) {
            terminalPlacaChar = value.toString().charAt(0);
            terminalPlaca = Character.toString(terminalPlacaChar);
            for (int k = 0; k < nRepeticoes; k++)
                autobuses.add(new AutobusModel(k + terminalPlaca + terminalPlaca + terminalPlaca, true, empresas.get(value.ordinal())));
            if (value.ordinal() == 2) break;
        }

        return autobusRepository.saveAll(autobuses);
    }
}
