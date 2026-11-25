package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.exception.DataAccessException;
import com.bleachsoulsociety.model.Personaje;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPersonajeDao implements PersonajeDao {
    private final DataSource dataSource;

    public JdbcPersonajeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Personaje> findAll(String nombre, Integer divisionId) {
        StringBuilder sql = new StringBuilder("SELECT id, nombre, apellido, especie, rango, division_id FROM personajes WHERE 1=1");
        List<Object> parametros = new ArrayList<>();
        if (nombre != null && !nombre.isBlank()) {
            sql.append(" AND (nombre LIKE ? OR apellido LIKE ?)");
            parametros.add("%" + nombre + "%");
            parametros.add("%" + nombre + "%");
        }
        if (divisionId != null) {
            sql.append(" AND division_id = ?");
            parametros.add(divisionId);
        }
        sql.append(" ORDER BY nombre, apellido");
        // Los filtros opcionales solo se enlazan si se han recibido en la petición.
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                sentencia.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                List<Personaje> personajes = new ArrayList<>();
                while (resultado.next()) {
                    personajes.add(mapear(resultado));
                }
                return personajes;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar los personajes.", e);
        }
    }

    @Override
    public Optional<Personaje> findById(int id) {
        String sql = "SELECT id, nombre, apellido, especie, rango, division_id FROM personajes WHERE id = ?";
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo consultar el personaje.", e);
        }
    }

    @Override
    public int create(Personaje personaje) {
        String sql = "INSERT INTO personajes (nombre, apellido, especie, rango, division_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            rellenar(sentencia, personaje);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) return claves.getInt(1);
                throw new SQLException("La inserción no devolvió el identificador.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo crear el personaje.", e);
        }
    }

    @Override
    public boolean update(Personaje personaje) {
        String sql = "UPDATE personajes SET nombre=?, apellido=?, especie=?, rango=?, division_id=? WHERE id=?";
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            rellenar(sentencia, personaje);
            sentencia.setInt(6, personaje.id());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el personaje.", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("DELETE FROM personajes WHERE id=?")) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar el personaje.", e);
        }
    }

    private void rellenar(PreparedStatement sentencia, Personaje personaje) throws SQLException {
        sentencia.setString(1, personaje.nombre());
        sentencia.setString(2, personaje.apellido());
        sentencia.setString(3, personaje.especie());
        sentencia.setString(4, personaje.rango());
        if (personaje.divisionId() == null) sentencia.setNull(5, java.sql.Types.INTEGER);
        else sentencia.setInt(5, personaje.divisionId());
    }

    private Personaje mapear(ResultSet resultado) throws SQLException {
        int divisionId = resultado.getInt("division_id");
        Integer division = resultado.wasNull() ? null : divisionId;
        return new Personaje(resultado.getInt("id"), resultado.getString("nombre"),
                resultado.getString("apellido"), resultado.getString("especie"),
                resultado.getString("rango"), division);
    }
}