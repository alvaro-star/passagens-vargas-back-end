package com.alvaro.empresas.passagens.helpers;

import com.alvaro.empresas.passagens.autobuses.enums.EnumPosicao;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.CiudadRepository;
import com.alvaro.empresas.passagens.paradas.repositories.DepartamentoRepository;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.repositories.PrecioRepository;
import com.alvaro.empresas.passagens.repositories.TrayectoRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Profile({"dev", "share"})
@Configuration
public class ShareProfile {
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
    private TrayectoRepository trayectoRepository;
    @Autowired
    private ParadaRepository paradaRepository;
    @Autowired
    private ViajeRepository viajeRepository;
    @Autowired
    private PrecioRepository precioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public void loadData() {
        var criptogrador = new BCryptPasswordEncoder();
        //Cadastra os Roles
        var admin = new RoleModel(RoleList.ROLE_ADMIN);
        roleService.save(admin);
        var user = new RoleModel(RoleList.ROLE_CLIENTE);
        roleService.save(user);
        var empresaAdmin = new RoleModel(RoleList.ROLE_EMPRESA_ADMIN);
        roleService.save(empresaAdmin);
        var empresaFuncionario = new RoleModel(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        roleService.save(empresaFuncionario);

        //Empresas
        EmpresaModel marzo = empresaRepository.save(new EmpresaModel("27 de Marzo", "https://github.com/alvaro-star.png", "202345"));
        EmpresaModel abril = empresaRepository.save(new EmpresaModel("20 de Abril", "https://github.com/alvaro-star.png", "202345"));
        EmpresaModel copacabana = empresaRepository.save(new EmpresaModel("Copacabana", "https://github.com/alvaro-star.png", "202345"));
        var usuarioCliente = usuarioRepository.save(new UsuarioModel("cliente@gmail.com", "Rick Sanchez", "(11) - 11111-1111", criptogrador.encode("cliente")));
        var usuarioAdmin = usuarioRepository.save(new UsuarioModel("admin@gmail.com", "Darth Vader", "(11) - 11111-1111", criptogrador.encode("admin")));
        var usuarioEmpresaAdmin = usuarioRepository.save(new UsuarioModel("empresaadmin@gmail.com", "Hero Nakamura", "(33) - 33333-3333", criptogrador.encode("empresaadmin"), marzo.getId()));
        var usuarioFuncionario = usuarioRepository.save(new UsuarioModel("empresafuncionario@gmail.com", "Rick Sanchez", "(11) - 11111-1111", criptogrador.encode("empresafuncionario"), marzo.getId()));

        List<String> nombresDepartamentos = Arrays.asList("Santa Cruz", "La Paz", "Cochabamba", "Oruro", "Potosí", "Tarija", "Chuquisaca", "Pando", "Beni");
        List<LugarModel> lugares = new ArrayList<>();
        for (String nombreDepartamento : nombresDepartamentos) {
            var depModel = departamentoRepository.save(new DepartamentoModel(nombreDepartamento, "SC"));
            var ciudad = ciudadRepository.save(new CiudadModel(nombreDepartamento, depModel));
            var lugar = lugarRepository.save(new LugarModel("Terminal " + nombreDepartamento, ciudad));
            lugares.add(lugar);
        }
        //Autobuses
        ArrayList<AutobusModel> autobuses = new ArrayList<>();
        autobuses.add(autobusRepository.save(new AutobusModel("2023J", marzo)));
        autobuses.add(autobusRepository.save(new AutobusModel("2023K", abril)));
        autobuses.add(autobusRepository.save(new AutobusModel("2023N", copacabana)));
        autobuses.add(autobusRepository.save(new AutobusModel("2023P", marzo)));

        //Cria os Pisos
        int indice = 0;
        for (AutobusModel autobus : autobuses) {
            List<PisoModel> pisos = new ArrayList<>();
            pisos.add(pisoRepository.save(new PisoModel(20, 4, EnumPosicao.IZQUIERDA, 1, EnumPosicao.DERECHA, 20 * 4, 1, autobus)));
            if (indice % 2 == 0)
                pisos.add(pisoRepository.save(new PisoModel(20, 4, EnumPosicao.IZQUIERDA, 2, EnumPosicao.DERECHA, 20 * 4, 81, autobus)));

            indice++;

            //Cria os trayectos y las paradas de cada autobus
            for (DayOfWeek dia : DayOfWeek.values()) {
                var trayecto = trayectoRepository.save(new TrayectoModel(autobus));
                var dataInicio = LocalDateTime.now().with(TemporalAdjusters.next(dia)).withHour(8);
                int nLista = 0;
                List<ParadaModel> paradas = new ArrayList<>();
                //Registra as paradas
                if (lugares.size() > 5) {
                    for (int i = 0; i < 5; i++) {
                        dataInicio = dataInicio.plusHours(1);
                        var parada = paradaRepository.save(new ParadaModel(dataInicio, 10, lugares.get(i), trayecto));
                        paradas.add(parada);
                        nLista++;
                    }
                } else {
                    for (LugarModel lugar : lugares) {
                        dataInicio = dataInicio.plusHours(1);
                        var parada = paradaRepository.save(new ParadaModel(dataInicio, 10, lugar, trayecto));
                        paradas.add(parada);
                        nLista++;
                    }
                }
                // Cria a viajem
                var viajeSaved = viajeRepository.save(new ViajeModel(paradas.get(0), paradas.get(nLista - 1), trayecto));

                float precioBruto = 200;
                for (PisoModel piso : pisos) {
                    var precio = precioRepository.save(new PrecioModel(precioBruto, piso.getNPiso(), piso.getNSillas(), viajeSaved));
                    precioBruto = precioBruto - 2;
                }
            }
        }
    }
}
