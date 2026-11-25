package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.exception.DataAccessException;
import com.bleachsoulsociety.model.Habilidad;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcHabilidadDao implements HabilidadDao {
    private final DataSource dataSource;

    public JdbcHabilidadDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Habilidad> findAll(String nombre, String tipo) {
        StringBuilder sql = new StringBuilder("SELECT id, nombre, descripcion, tipo FROM habilidades WHERE 1=1");
        List<String> parametros = new ArrayList<>();
        if (nombre != null && !nombre.isBlank()) {
            sql.append(" AND nombre LIKE ?");
            parametros.add("%" + nombre + "%");
        }
        if (tipo != null && !tipo.isBlank()) {
            sql.append(" AND tipo = ?");
            parametros.add(tipo);
        }
        sql.append(" ORDER BY nombre");
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) sentencia.setString(i + 1, parametros.get(i));
            try (ResultSet resultado = sentencia.executeQuery()) {
                List<Habilidad> lista = new ArrayList<>();
                while (resultado.next()) lista.add(mapear(resultado));
                return lista;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar las habilidades.", e);
        }
    }

    @Override
    public Optional<Habilidad> findById(int id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("SELECT id, nombre, descripcion, tipo FROM habilidades WHERE id=?")) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo consultar la habilidad.", e);
        }
    }

    @Override
    public int create(Habilidad habilidad) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO habilidades (nombre, descripcion, tipo) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            rellenar(sentencia, habilidad);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) return claves.getInt(1);
                throw new SQLException("La inserción no devolvió el identificador.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo crear la habilidad.", e);
        }
    }

    @Override
    public boolean update(Habilidad habilidad) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE habilidades SET nombre=?, descripcion=?, tipo=? WHERE id=?")) {
            rellenar(sentencia, habilidad);
            sentencia.setInt(4, habilidad.id());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la habilidad.", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("DELETE FROM habilidades WHERE id=?")) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar la habilidad.", e);
        }
    }

    private void rellenar(PreparedStatement sentencia, Habilidad habilidad) throws SQLException {
        sentencia.setString(1, habilidad.nombre());
        sentencia.setString(2, habilidad.descripcion());
        sentencia.setString(3, habilidad.tipo());
    }

    private Habilidad mapear(ResultSet resultado) throws SQLException {
        return new Habilidad(resultado.getInt("id"), resultado.getString("nombre"),
                resultado.getString("descripcion"), resultado.getString("tipo"));
    }
}