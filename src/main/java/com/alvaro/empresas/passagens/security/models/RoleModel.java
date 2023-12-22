package com.alvaro.empresas.passagens.security.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "tb_role")
@Getter
@Setter
@NoArgsConstructor
public class RoleModel implements GrantedAuthority {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idtb_role")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private RoleList nome;

    public RoleModel(@NotNull RoleList nome) {
        this.nome = nome;
    }

    @Override
    public String getAuthority() {
        return nome.toString();
    }
}
