package com.fixsy.usuarios.config;

import com.fixsy.usuarios.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class RoleFixer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    // Removed @Transactional to allow independent commits of native queries
    public void run(String... args) throws Exception {
        System.out.println("Running RoleFixer with NATIVE query...");

        try {
            // Reassign users from duplicate roles to canonical roles (1=Usuario, 2=Admin,
            // 3=Soporte)
            try {
                roleRepository.reassignAdminUsersNative();
                roleRepository.reassignSoporteUsersNative();
                roleRepository.reassignUsuarioUsersNative();

                // Migrate legacy admin email to new strict domain
                try {
                    // Si el correo se habia cambiado a admin.fixsy.com, lo devolvemos a
                    // adminfixsy.cl
                    roleRepository.updateUserEmailNative("admin@admin.fixsy.com", "admin@adminfixsy.cl");
                    System.out.println("Migrated admin@admin.fixsy.com back to admin@adminfixsy.cl");
                } catch (Exception e) {
                    System.out.println("Admin email already correct or not found");
                }

                // Fix specific admin email if it exists
                // Note: We don't have a direct email update method exposed yet, but we can rely
                // on users re-registering or manual fix.
                // Or we can add one quickly. Let's rely on the user logging in with the old
                // email for now, but valid role.
                // Actually, if they log in with OLD email, and we updated the role domain to
                // NEW domain, they get "Usuario" role.
                // We MUST update their email or revert the domain requirement.
                // Since I can't easily update email without adding a method, I will add a
                // method to RoleRepository now.
                System.out.println("Duplicate role users reassigned");
            } catch (Exception e) {
                System.err.println("Warning reassigning users: " + e.getMessage());
            }

            // Delete potential duplicates
            try {
                roleRepository.deleteDuplicatesNative();
                System.out.println("Duplicates deleted");
            } catch (Exception e) {
                System.err.println("Could not delete duplicate roles: " + e.getMessage());
            }

            // Force update regardless of current state using native query
            roleRepository.updateRoleNameNative(2L, "Admin");
            System.out.println("Role 2 forced to Admin");

            roleRepository.updateRoleNameNative(3L, "Soporte");
            System.out.println("Role 3 forced to Soporte");

            roleRepository.updateRoleNameNative(1L, "Usuario");
            System.out.println("Role 1 forced to Usuario");

            // Fix Admin Domain
            roleRepository.updateRoleDomainNative(2L, "adminfixsy.cl");
            System.out.println("Role 2 domain forced to adminfixsy.cl");

            // Fix user "asada123" (likely ID 6 based on flow, but using native update for
            // specific ID if known, or email)
            // Since we can't easily auto-detect ID here without injecting repo logic,
            // and the user provided the specific email, we'll try to update by email if
            // possible or just rely on the user ID found.
            // Simplified: The user can re-register or we fix ID 6 explicitly if we found
            // it.
            // We will add a method to fix user role by email native.
            roleRepository.updateUserRoleByEmailNative("asada123@admin.fixsy.com", 2L);
            System.out.println("User asada123 force-updated to Admin");
        } catch (Exception e) {
            System.err.println("Error forcing role update: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
