package com.alvaro.empresas.passagens.security.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "tb_role")
@Data
@NoArgsConstructor
public class RoleModel implements GrantedAuthority {
    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "idtb_role")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private RoleList nombre;

    public RoleModel(@NotNull RoleList nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getAuthority() {
        return nombre.toString();
    }
}
