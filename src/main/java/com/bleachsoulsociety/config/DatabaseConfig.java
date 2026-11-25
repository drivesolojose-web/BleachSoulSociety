package com.bleachsoulsociety.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DatabaseConfig {
    private DatabaseConfig() {
    }

    public static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        // La URL y las credenciales se configuran en el entorno de Tomcat, nunca en el código fuente.
        config.setJdbcUrl(obligatoria("DB_URL"));
        // Tomcat carga cada WAR con su propio classloader; se indica el driver para Hikari explícitamente.
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("characterEncoding", "UTF-8");
        config.addDataSourceProperty("connectionCollation", "utf8mb4_0900_ai_ci");
        config.setUsername(obligatoria("DB_USER"));
        config.setPassword(obligatoria("DB_PASSWORD"));
        config.setMaximumPoolSize(Integer.parseInt(System.getenv().getOrDefault("DB_POOL_SIZE", "10")));
        config.setPoolName("BleachSoulSocietyPool");
        return new HikariDataSource(config);
    }

    private static String obligatoria(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable de entorno " + nombre + ".");
        }
        return valor;
    }
}