package com.fixsy.usuarios.controller;

import com.fixsy.usuarios.dto.LoginDTO;
import com.fixsy.usuarios.dto.RegisterDTO;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> register(@RequestBody RegisterDTO dto) {
        try {
            User user = userService.registerUserDirect(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("nombres", user.getNombre());
            response.put("apellidos", user.getApellido());
            response.put("role", user.getRole().getNombre());

            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            User user = userService.loginDirect(dto.getEmail(), dto.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("nombres", user.getNombre());
            response.put("apellidos", user.getApellido());
            response.put("telefono", user.getPhone());
            response.put("role", user.getRole().getNombre());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
