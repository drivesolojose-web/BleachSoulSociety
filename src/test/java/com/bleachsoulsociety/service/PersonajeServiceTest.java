package com.bleachsoulsociety.service;

import com.bleachsoulsociety.dao.PersonajeDao;
import com.bleachsoulsociety.dto.PersonajeDto;
import com.bleachsoulsociety.dto.PersonajeRequest;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.exception.ValidationException;
import com.bleachsoulsociety.model.Personaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonajeServiceTest {
    private PersonajeService servicio;
    private PersonajeDaoMemoria dao;

    @BeforeEach
    void preparar() {
        dao = new PersonajeDaoMemoria();
        servicio = new PersonajeService(dao);
    }

    @Test
    void crearDevuelveDatosLimpiosEnUnDto() {
        PersonajeDto resultado = servicio.crear(new PersonajeRequest(" Ichigo ", " Kurosaki ",
                "Shinigami", "Sustituto", null));

        assertEquals(1, resultado.id());
        assertEquals("Ichigo", resultado.nombre());
        assertEquals("Kurosaki", resultado.apellido());
    }

    @Test
    void crearRechazaNombreVacio() {
        assertThrows(ValidationException.class, () -> servicio.crear(
                new PersonajeRequest("  ", "Kurosaki", "Shinigami", null, null)));
        assertEquals(0, dao.personajes.size());
    }

    @Test
    void listarRechazaUnFiltroDeDivisionNoPositivo() {
        assertThrows(ValidationException.class, () -> servicio.listar(null, 0));
    }

    @Test
    void buscarInexistenteDevuelveNotFound() {
        assertThrows(NotFoundException.class, () -> servicio.buscar(99));
    }

    private static class PersonajeDaoMemoria implements PersonajeDao {
        private final List<Personaje> personajes = new ArrayList<>();

        @Override
        public List<Personaje> findAll(String nombre, Integer divisionId) {
            return List.copyOf(personajes);
        }

        @Override
        public Optional<Personaje> findById(int id) {
            return personajes.stream().filter(personaje -> personaje.id() == id).findFirst();
        }

        @Override
        public int create(Personaje personaje) {
            Personaje nuevo = new Personaje(1, personaje.nombre(), personaje.apellido(),
                    personaje.especie(), personaje.rango(), personaje.divisionId());
            personajes.add(nuevo);
            return nuevo.id();
        }

        @Override
        public boolean update(Personaje personaje) {
            for (int i = 0; i < personajes.size(); i++) {
                if (personajes.get(i).id().equals(personaje.id())) {
                    personajes.set(i, personaje);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean delete(int id) {
            return personajes.removeIf(personaje -> personaje.id() == id);
        }
    }
}