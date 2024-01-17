package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.autobuses.enums.EnumPosicao;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
import com.alvaro.empresas.passagens.autobuses.repositories.AutobusRepository;
import com.alvaro.empresas.passagens.autobuses.repositories.PisoRepository;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.alvaro.empresas.passagens.paradas.repositories.CiudadRepository;
import com.alvaro.empresas.passagens.paradas.repositories.DepartamentoRepository;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.paradas.repositories.ParadaRepository;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.repositories.TrayectoRepository;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/dados")
public class DadosResource {
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

    @GetMapping("/roles")
    public ResponseEntity<Object> loadRoles() {
        var admin = new RoleModel(RoleList.ROLE_ADMIN);
        roleService.save(admin);
        var user = new RoleModel(RoleList.ROLE_CLIENTE);
        roleService.save(user);
        var empresaAdmin = new RoleModel(RoleList.ROLE_EMPRESA_ADMIN);
        roleService.save(empresaAdmin);
        var empresaFuncionario = new RoleModel(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        roleService.save(empresaFuncionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Criado"));
    }

    @GetMapping("/lugares")
    public ResponseEntity<Object> loadLugars() {
        List<String> nombresDepartamentos = Arrays.asList("Santa Cruz", "La Paz", "Cochabamba", "Oruro", "Potosí", "Tarija", "Chuquisaca", "Pando", "Beni");

        for (String nombreDepartamento : nombresDepartamentos) {
            var depModel = departamentoRepository.save(new DepartamentoModel(nombreDepartamento));
            var ciudadu = ciudadRepository.save(new CiudadModel(nombreDepartamento, depModel));
            lugarRepository.save(new LugarModel("Terminal " + nombreDepartamento, ciudadu));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Criado"));
    }

    @GetMapping("/autobuses")
    public ResponseEntity<Object> empresas() {
        EmpresaModel marzo = empresaRepository.save(new EmpresaModel("27 de Marzo", "teste", "202345"));
        EmpresaModel abril = empresaRepository.save(new EmpresaModel("20 de Abril", "teste", "202345"));
        EmpresaModel copacabana = empresaRepository.save(new EmpresaModel("Copacabana", "teste", "202345"));

        ArrayList<AutobusModel> autobuses = new ArrayList<>();
        autobuses.add(autobusRepository.save(new AutobusModel("2023J", marzo)));
        autobuses.add(autobusRepository.save(new AutobusModel("2023K", abril)));
        autobuses.add(autobusRepository.save(new AutobusModel("2023N", copacabana)));
        autobuses.add(autobusRepository.save(new AutobusModel("2023P", marzo)));
        int indice = 0;
        for (AutobusModel autobus : autobuses) {
            pisoRepository.save(new PisoModel(20, 4, EnumPosicao.IZQUIERDA, 1, EnumPosicao.DERECHA, 20 * 4, 1, autobus));
            if (indice % 2 == 0) {
                pisoRepository.save(new PisoModel(20, 4, EnumPosicao.IZQUIERDA, 2, EnumPosicao.DERECHA, 20 * 4, 81, autobus));
            }
            indice++;
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Criado"));
    }

    @GetMapping("/viajes")
    @Transactional
    public ResponseEntity<Object> loadViajes() {
        List<AutobusModel> autobuses = autobusRepository.findAll();
        List<LugarModel> lugares = lugarRepository.findAll();

        for (AutobusModel autobus : autobuses) {
            var trayecto = trayectoRepository.save(new TrayectoModel(autobus));
            var dataInicio = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            if (lugares.size() > 5) {
                for (int i = 0; i < 5; i++) {
                    dataInicio = dataInicio.plusHours(1);
                    var parada = paradaRepository.save(new ParadaModel(dataInicio, 10, lugares.get(i), trayecto));
                }
            } else {
                for (LugarModel lugare : lugares) {
                    dataInicio = dataInicio.plusHours(1);
                    var parada = paradaRepository.save(new ParadaModel(dataInicio, 10, lugare, trayecto));
                }
            }

        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Criado"));
    }
}
