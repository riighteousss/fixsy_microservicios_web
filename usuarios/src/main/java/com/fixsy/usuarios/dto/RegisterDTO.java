package com.fixsy.usuarios.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String email;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String contrasena;
}
