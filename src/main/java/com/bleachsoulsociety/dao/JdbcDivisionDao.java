package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.exception.DataAccessException;
import com.bleachsoulsociety.model.Division;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcDivisionDao implements DivisionDao {
    private final DataSource dataSource;

    public JdbcDivisionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Division> findAll(String nombre) {
        StringBuilder sql = new StringBuilder("SELECT id, nombre, descripcion FROM divisiones");
        if (nombre != null && !nombre.isBlank()) sql.append(" WHERE nombre LIKE ?");
        sql.append(" ORDER BY nombre");
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {
            if (nombre != null && !nombre.isBlank()) sentencia.setString(1, "%" + nombre + "%");
            try (ResultSet resultado = sentencia.executeQuery()) {
                List<Division> divisiones = new ArrayList<>();
                while (resultado.next()) divisiones.add(mapear(resultado));
                return divisiones;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar las divisiones.", e);
        }
    }

    @Override
    public Optional<Division> findById(int id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("SELECT id, nombre, descripcion FROM divisiones WHERE id=?")) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo consultar la división.", e);
        }
    }

    @Override
    public int create(Division division) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO divisiones (nombre, descripcion) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, division.nombre());
            sentencia.setString(2, division.descripcion());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) return claves.getInt(1);
                throw new SQLException("La inserción no devolvió el identificador.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo crear la división.", e);
        }
    }

    @Override
    public boolean update(Division division) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE divisiones SET nombre=?, descripcion=? WHERE id=?")) {
            sentencia.setString(1, division.nombre());
            sentencia.setString(2, division.descripcion());
            sentencia.setInt(3, division.id());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la división.", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("DELETE FROM divisiones WHERE id=?")) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar la división.", e);
        }
    }

    private Division mapear(ResultSet resultado) throws SQLException {
        return new Division(resultado.getInt("id"), resultado.getString("nombre"), resultado.getString("descripcion"));
    }
}