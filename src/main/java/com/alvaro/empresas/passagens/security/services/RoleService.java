package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.repositories.RoleRepository;
import com.alvaro.empresas.passagens.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public RoleModel save(RoleModel model) {
        return roleRepository.save(model);
    }

    public Optional<RoleModel> getByRoleName(RoleList name) {
        return roleRepository.findByNome(name);
    }

    public Set<RoleModel> loadRole(String role) {
        Set<RoleModel> roles = new HashSet<>();
        if (role.equals(RoleList.ADMIN.getRole())) {
            roles.add(this.getByRoleName(RoleList.ADMIN).get());
            roles.add(this.getByRoleName(RoleList.USER).get());
        } else if (role.equals(RoleList.USER.getRole())) {
            roles.add(this.getByRoleName(RoleList.USER).get());
        } else {
            roles.add(this.getByRoleName(RoleList.INVALIDO).get());
        }
        return roles;

        /*if (this.role.equals(RoleList.ADMIN.getRole())) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        } else if (this.role.equals(RoleList.USER.getRole())) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        } else {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }*/
    }
}
