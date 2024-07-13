package com.alvaro.empresas.passagens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_factura_empresa", indexes = @Index(
        name = "idxtb_factura_empresa_fk_idtb_empresa_inicio_conteo",
        columnList = "fk_idtb_empresa, inicio_conteo"))
@NoArgsConstructor
public class FacturaEmpresaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_factura_empresa")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private EmpresaModel empresa;
    @Column(nullable = false)
    private LocalDateTime inicioConteo;
    @Column(nullable = false)
    private LocalDateTime finConteo;
    @Column(nullable = false)
    private BigDecimal valorPagado;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
