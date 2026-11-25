package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.exception.DataAccessException;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.model.Habilidad;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPersonajeHabilidadDao implements PersonajeHabilidadDao {
    private final DataSource dataSource;

    public JdbcPersonajeHabilidadDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Habilidad> findByPersonajeId(int personajeId) {
        String sql = "SELECT p.id AS personaje_id, h.id, h.nombre, h.descripcion, h.tipo FROM personajes p " +
                "LEFT JOIN personaje_habilidad ph ON ph.personaje_id=p.id " +
                "LEFT JOIN habilidades h ON h.id=ph.habilidad_id WHERE p.id=? ORDER BY h.nombre";
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, personajeId);
            try (ResultSet resultado = sentencia.executeQuery()) {
                List<Habilidad> habilidades = new ArrayList<>();
                if (!resultado.next()) throw new NotFoundException("No existe el personaje solicitado.");
                do {
                    int habilidadId = resultado.getInt("id");
                    if (!resultado.wasNull()) habilidades.add(new Habilidad(habilidadId,
                            resultado.getString("nombre"), resultado.getString("descripcion"), resultado.getString("tipo")));
                } while (resultado.next());
                return habilidades;
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar las habilidades del personaje.", e);
        }
    }

    @Override
    public void assign(int personajeId, int habilidadId) {
        try (Connection conexion = dataSource.getConnection()) {
            conexion.setAutoCommit(false);
            try {
                // Se validan ambos extremos y se confirman juntos para no dejar asignaciones parciales.
                comprobarExiste(conexion, "personajes", personajeId, "personaje");
                comprobarExiste(conexion, "habilidades", habilidadId, "habilidad");
                String sql = "INSERT INTO personaje_habilidad (personaje_id, habilidad_id) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE habilidad_id=VALUES(habilidad_id)";
                try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                    sentencia.setInt(1, personajeId);
                    sentencia.setInt(2, habilidadId);
                    sentencia.executeUpdate();
                }
                conexion.commit();
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo asignar la habilidad al personaje.", e);
        }
    }

    @Override
    public boolean remove(int personajeId, int habilidadId) {
        String sql = "DELETE FROM personaje_habilidad WHERE personaje_id=? AND habilidad_id=?";
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, personajeId);
            sentencia.setInt(2, habilidadId);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo retirar la habilidad del personaje.", e);
        }
    }

    private void comprobarExiste(Connection conexion, String tabla, int id, String entidad) throws SQLException {
        String sql = "SELECT id FROM " + tabla + " WHERE id=?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) throw new NotFoundException("No existe el " + entidad + " con identificador " + id + ".");
            }
        }
    }
}