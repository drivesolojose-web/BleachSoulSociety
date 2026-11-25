package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.exception.DataAccessException;
import com.bleachsoulsociety.model.Zanpakuto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcZanpakutoDao implements ZanpakutoDao {
    private final DataSource dataSource;

    public JdbcZanpakutoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Zanpakuto> findAll(String nombre, Integer personajeId) {
        StringBuilder sql = new StringBuilder("SELECT id, personaje_id, nombre, shikai, bankai FROM zanpakutos WHERE 1=1");
        List<Object> parametros = new ArrayList<>();
        if (nombre != null && !nombre.isBlank()) {
            sql.append(" AND nombre LIKE ?");
            parametros.add("%" + nombre + "%");
        }
        if (personajeId != null) {
            sql.append(" AND personaje_id=?");
            parametros.add(personajeId);
        }
        sql.append(" ORDER BY nombre");
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) sentencia.setObject(i + 1, parametros.get(i));
            try (ResultSet resultado = sentencia.executeQuery()) {
                List<Zanpakuto> lista = new ArrayList<>();
                while (resultado.next()) lista.add(mapear(resultado));
                return lista;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar las zanpakutos.", e);
        }
    }

    @Override
    public Optional<Zanpakuto> findById(int id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("SELECT id, personaje_id, nombre, shikai, bankai FROM zanpakutos WHERE id=?")) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo consultar la zanpakuto.", e);
        }
    }

    @Override
    public int create(Zanpakuto zanpakuto) {
        String sql = "INSERT INTO zanpakutos (personaje_id, nombre, shikai, bankai) VALUES (?, ?, ?, ?)";
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            rellenar(sentencia, zanpakuto);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) return claves.getInt(1);
                throw new SQLException("La inserción no devolvió el identificador.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo crear la zanpakuto.", e);
        }
    }

    @Override
    public boolean update(Zanpakuto zanpakuto) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE zanpakutos SET personaje_id=?, nombre=?, shikai=?, bankai=? WHERE id=?")) {
            rellenar(sentencia, zanpakuto);
            sentencia.setInt(5, zanpakuto.id());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la zanpakuto.", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement("DELETE FROM zanpakutos WHERE id=?")) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar la zanpakuto.", e);
        }
    }

    private void rellenar(PreparedStatement sentencia, Zanpakuto zanpakuto) throws SQLException {
        sentencia.setInt(1, zanpakuto.personajeId());
        sentencia.setString(2, zanpakuto.nombre());
        sentencia.setString(3, zanpakuto.shikai());
        sentencia.setString(4, zanpakuto.bankai());
    }

    private Zanpakuto mapear(ResultSet resultado) throws SQLException {
        return new Zanpakuto(resultado.getInt("id"), resultado.getInt("personaje_id"),
                resultado.getString("nombre"), resultado.getString("shikai"), resultado.getString("bankai"));
    }
}