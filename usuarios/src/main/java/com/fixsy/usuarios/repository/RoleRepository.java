package com.fixsy.usuarios.repository;

import com.fixsy.usuarios.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombre(String nombre);
    Optional<Role> findByEmailDomain(String emailDomain);
    boolean existsByNombre(String nombre);
}

