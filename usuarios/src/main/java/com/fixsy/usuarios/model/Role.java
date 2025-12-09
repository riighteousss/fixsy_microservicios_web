package com.fixsy.usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // Usuario, Admin, Cliente

    @Column
    private String descripcion;

    @Column(name = "email_domain")
    private String emailDomain; // Dominio de email asociado (ej: admin.fixsy.com)

    // Constructor conveniente
    public Role(String nombre, String descripcion, String emailDomain) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.emailDomain = emailDomain;
    }
}

