package com.fixsy.usuarios.repository;

import com.fixsy.usuarios.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
        Optional<Role> findByNombre(String nombre);

        Optional<Role> findByEmailDomain(String emailDomain);

        boolean existsByNombre(String nombre);

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "UPDATE roles SET nombre = :nombre WHERE id = :id", nativeQuery = true)
        void updateRoleNameNative(@org.springframework.data.repository.query.Param("id") Long id,
                        @org.springframework.data.repository.query.Param("nombre") String nombre);

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "UPDATE roles SET email_domain = :domain WHERE id = :id", nativeQuery = true)
        void updateRoleDomainNative(@org.springframework.data.repository.query.Param("id") Long id,
                        @org.springframework.data.repository.query.Param("domain") String domain);

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "UPDATE users u JOIN roles r ON u.role_id = r.id SET u.role_id = 2 WHERE r.nombre = 'Admin' AND r.id <> 2", nativeQuery = true)
        void reassignAdminUsersNative();

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "UPDATE users u JOIN roles r ON u.role_id = r.id SET u.role_id = 2 WHERE r.email_domain = 'adminfixsy.cl' AND r.id <> 2", nativeQuery = true)
        void reassignAdminDomainUsersNative();

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "UPDATE users u JOIN roles r ON u.role_id = r.id SET u.role_id = 3 WHERE r.nombre = 'Soporte' AND r.id <> 3", nativeQuery = true)
        void reassignSoporteUsersNative();

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "UPDATE users u JOIN roles r ON u.role_id = r.id SET u.role_id = 1 WHERE r.nombre = 'Usuario' AND r.id <> 1", nativeQuery = true)
        void reassignUsuarioUsersNative();

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "DELETE FROM roles WHERE (nombre = 'Admin' AND id <> 2) OR (nombre = 'Soporte' AND id <> 3) OR (nombre = 'Usuario' AND id <> 1) OR (email_domain = 'admin.fixsy.com' AND id <> 2)", nativeQuery = true)
        void deleteDuplicatesNative();

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "UPDATE users SET role_id = :roleId WHERE email = :email", nativeQuery = true)
        void updateUserRoleByEmailNative(@org.springframework.data.repository.query.Param("email") String email,
                        @org.springframework.data.repository.query.Param("roleId") Long roleId);

        @Transactional
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query(value = "UPDATE users SET email = :newEmail WHERE email = :oldEmail", nativeQuery = true)
        void updateUserEmailNative(@org.springframework.data.repository.query.Param("oldEmail") String oldEmail,
                        @org.springframework.data.repository.query.Param("newEmail") String newEmail);
}
