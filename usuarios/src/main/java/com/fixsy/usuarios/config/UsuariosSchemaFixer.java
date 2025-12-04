package com.fixsy.usuarios.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuariosSchemaFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            dropObsoleteColumn("users", "name");
            dropObsoleteColumn("users", "role");
        } catch (Exception ex) {
            log.warn("No se pudo verificar o eliminar columnas obsoletas en users: {}", ex.getMessage());
        }
    }

    private void dropObsoleteColumn(String table, String column) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) > 0 FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Boolean.class,
                table,
                column
        );

        if (Boolean.TRUE.equals(exists)) {
            log.info("Removiendo columna obsoleta '{}' de la tabla {}...", column, table);
            jdbcTemplate.execute("ALTER TABLE " + table + " DROP COLUMN " + column);
            log.info("Columna '{}' eliminada correctamente.", column);
        }
    }
}
