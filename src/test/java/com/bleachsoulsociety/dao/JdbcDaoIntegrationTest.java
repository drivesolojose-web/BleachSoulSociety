package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.exception.DataAccessException;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.model.Division;
import com.bleachsoulsociety.model.Habilidad;
import com.bleachsoulsociety.model.Personaje;
import com.bleachsoulsociety.model.Zanpakuto;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcDaoIntegrationTest {
    private JdbcDataSource dataSource;
    private JdbcDivisionDao divisiones;
    private JdbcPersonajeDao personajes;
    private JdbcHabilidadDao habilidades;
    private JdbcZanpakutoDao zanpakutos;
    private JdbcPersonajeHabilidadDao personajeHabilidades;

    @BeforeEach
    void prepararBaseAislada() throws SQLException {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:bleach_test_" + UUID.randomUUID()
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        try (Connection conexion = dataSource.getConnection(); Statement sql = conexion.createStatement()) {
            sql.execute("CREATE TABLE divisiones (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100) NOT NULL UNIQUE, descripcion VARCHAR(500))");
            sql.execute("CREATE TABLE personajes (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100) NOT NULL, apellido VARCHAR(100), especie VARCHAR(60) NOT NULL, rango VARCHAR(100), division_id INT, FOREIGN KEY (division_id) REFERENCES divisiones(id) ON DELETE SET NULL)");
            sql.execute("CREATE TABLE habilidades (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100) NOT NULL UNIQUE, descripcion VARCHAR(500), tipo VARCHAR(50) NOT NULL)");
            sql.execute("CREATE TABLE zanpakutos (id INT AUTO_INCREMENT PRIMARY KEY, personaje_id INT NOT NULL, nombre VARCHAR(100) NOT NULL, shikai VARCHAR(500), bankai VARCHAR(500), FOREIGN KEY (personaje_id) REFERENCES personajes(id) ON DELETE CASCADE)");
            sql.execute("CREATE TABLE personaje_habilidad (personaje_id INT NOT NULL, habilidad_id INT NOT NULL, PRIMARY KEY (personaje_id, habilidad_id), FOREIGN KEY (personaje_id) REFERENCES personajes(id) ON DELETE CASCADE, FOREIGN KEY (habilidad_id) REFERENCES habilidades(id) ON DELETE CASCADE)");
        }
        divisiones = new JdbcDivisionDao(dataSource);
        personajes = new JdbcPersonajeDao(dataSource);
        habilidades = new JdbcHabilidadDao(dataSource);
        zanpakutos = new JdbcZanpakutoDao(dataSource);
        personajeHabilidades = new JdbcPersonajeHabilidadDao(dataSource);
    }

    @Test
    void divisionDaoCubreCrudFiltrosRegistrosInexistentesYClaveDuplicada() {
        int id = divisiones.create(new Division(null, "Sexta División", "Disciplina"));
        assertEquals(new Division(id, "Sexta División", "Disciplina"), divisiones.findById(id).orElseThrow());
        assertEquals(1, divisiones.findAll("Sexta").size());
        assertEquals(0, divisiones.findAll("Primera").size());

        assertTrue(divisiones.update(new Division(id, "Sexta", "Actualizada")));
        assertEquals("Actualizada", divisiones.findById(id).orElseThrow().descripcion());
        assertFalse(divisiones.update(new Division(999, "No existe", null)));
        assertTrue(divisiones.delete(id));
        assertFalse(divisiones.delete(id));
        assertTrue(divisiones.findById(id).isEmpty());
        assertThrows(DataAccessException.class, () -> {
            divisiones.create(new Division(null, "Única", null));
            divisiones.create(new Division(null, "Única", null));
        });
    }

    @Test
    void personajeDaoFiltraPorNombreYDivisionYConservaLaDivisionNula() {
        int divisionId = divisiones.create(new Division(null, "Décima", null));
        int ichigoId = personajes.create(new Personaje(null, "Ichigo", "Kurosaki", "Shinigami", null, divisionId));
        int rukiaId = personajes.create(new Personaje(null, "Rukia", "Kuchiki", "Shinigami", "Teniente", null));

        assertEquals(new Personaje(ichigoId, "Ichigo", "Kurosaki", "Shinigami", null, divisionId),
                personajes.findById(ichigoId).orElseThrow());
        assertEquals(List.of(ichigoId), personajes.findAll("Ichigo", divisionId).stream().map(Personaje::id).toList());
        assertEquals(null, personajes.findById(rukiaId).orElseThrow().divisionId());
        assertTrue(personajes.update(new Personaje(ichigoId, "Kurosaki", "Ichigo", "Shinigami", "Sustituto", null)));
        assertEquals("Sustituto", personajes.findById(ichigoId).orElseThrow().rango());
        assertFalse(personajes.update(new Personaje(999, "No existe", null, "Humano", null, null)));
        assertTrue(personajes.delete(rukiaId));
        assertFalse(personajes.delete(rukiaId));
        assertTrue(personajes.findById(rukiaId).isEmpty());
    }

    @Test
    void habilidadDaoCubreCrudFiltrosYClavesDuplicadas() {
        int id = habilidades.create(new Habilidad(null, "Kidō", "Hechizos", "Espiritual"));
        habilidades.create(new Habilidad(null, "Hakuda", "Combate cuerpo a cuerpo", "Combate"));

        assertEquals(new Habilidad(id, "Kidō", "Hechizos", "Espiritual"), habilidades.findById(id).orElseThrow());
        assertEquals(List.of("Kidō"), habilidades.findAll("Kid", "Espiritual").stream().map(Habilidad::nombre).toList());
        assertTrue(habilidades.update(new Habilidad(id, "Hadō", "Hechizo", "Espiritual")));
        assertEquals("Hadō", habilidades.findById(id).orElseThrow().nombre());
        assertFalse(habilidades.update(new Habilidad(999, "No existe", null, "Combate")));
        assertTrue(habilidades.delete(id));
        assertFalse(habilidades.delete(id));
        assertTrue(habilidades.findById(id).isEmpty());
        assertThrows(DataAccessException.class, () -> {
            habilidades.create(new Habilidad(null, "Duplicada", null, "Combate"));
            habilidades.create(new Habilidad(null, "Duplicada", null, "Combate"));
        });
    }

    @Test
    void zanpakutoDaoCubreCrudFiltroYClaveForanea() {
        int personajeId = personajes.create(new Personaje(null, "Ichigo", "Kurosaki", "Shinigami", null, null));
        int id = zanpakutos.create(new Zanpakuto(null, personajeId, "Zangetsu", "Getsuga Tensho", "Tensa Zangetsu"));

        assertEquals(new Zanpakuto(id, personajeId, "Zangetsu", "Getsuga Tensho", "Tensa Zangetsu"),
                zanpakutos.findById(id).orElseThrow());
        assertEquals(1, zanpakutos.findAll("Zang", personajeId).size());
        assertTrue(zanpakutos.update(new Zanpakuto(id, personajeId, "Zangetsu", "Getsuga", null)));
        assertEquals("Getsuga", zanpakutos.findById(id).orElseThrow().shikai());
        assertFalse(zanpakutos.update(new Zanpakuto(999, personajeId, "No existe", null, null)));
        assertTrue(zanpakutos.delete(id));
        assertFalse(zanpakutos.delete(id));
        assertThrows(DataAccessException.class,
                () -> zanpakutos.create(new Zanpakuto(null, 999, "Sin portador", null, null)));
    }

    @Test
    void relacionDeHabilidadesEsIdempotenteYCompruebaLosRegistros() {
        int personajeId = personajes.create(new Personaje(null, "Ichigo", null, "Shinigami", null, null));
        int habilidadId = habilidades.create(new Habilidad(null, "Zanjutsu", null, "Combate"));

        personajeHabilidades.assign(personajeId, habilidadId);
        personajeHabilidades.assign(personajeId, habilidadId);
        assertEquals(1, personajeHabilidades.findByPersonajeId(personajeId).size());
        assertTrue(personajeHabilidades.remove(personajeId, habilidadId));
        assertFalse(personajeHabilidades.remove(personajeId, habilidadId));
        assertEquals(0, personajeHabilidades.findByPersonajeId(personajeId).size());
        assertThrows(NotFoundException.class, () -> personajeHabilidades.findByPersonajeId(999));
        assertThrows(NotFoundException.class, () -> personajeHabilidades.assign(999, habilidadId));
        assertThrows(NotFoundException.class, () -> personajeHabilidades.assign(personajeId, 999));
    }
}