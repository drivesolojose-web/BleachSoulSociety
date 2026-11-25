package com.bleachsoulsociety.service;

import com.bleachsoulsociety.dao.PersonajeDao;
import com.bleachsoulsociety.dto.PersonajeDto;
import com.bleachsoulsociety.dto.PersonajeRequest;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.exception.ValidationException;
import com.bleachsoulsociety.model.Personaje;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PersonajeServiceTest {
    private final PersonajeDao dao = mock(PersonajeDao.class);
    private final PersonajeService servicio = new PersonajeService(dao);

    @Test
    void crearDevuelveDatosLimpiosEnUnDto() {
        when(dao.create(any(Personaje.class))).thenReturn(1);
        when(dao.findById(1)).thenReturn(Optional.of(
                new Personaje(1, "Ichigo", "Kurosaki", "Shinigami", "Sustituto", null)));

        PersonajeDto resultado = servicio.crear(new PersonajeRequest(" Ichigo ", " Kurosaki ",
                "Shinigami", "Sustituto", null));

        assertEquals(1, resultado.id());
        assertEquals("Ichigo", resultado.nombre());
        assertEquals("Kurosaki", resultado.apellido());
        verify(dao).create(new Personaje(null, "Ichigo", "Kurosaki", "Shinigami", "Sustituto", null));
    }

    @Test
    void crearRechazaNombreVacio() {
        assertThrows(ValidationException.class, () -> servicio.crear(
                new PersonajeRequest("  ", "Kurosaki", "Shinigami", null, null)));
        verifyNoInteractions(dao);
    }

    @Test
    void listarRechazaUnFiltroDeDivisionNoPositivo() {
        assertThrows(ValidationException.class, () -> servicio.listar(null, 0));
        verifyNoInteractions(dao);
    }

    @Test
    void buscarInexistenteDevuelveNotFound() {
        assertThrows(NotFoundException.class, () -> servicio.buscar(99));
        assertThrows(ValidationException.class, () -> servicio.buscar(0));
    }

    @Test
    void listarConvierteLosResultadosYConservaLosFiltros() {
        when(dao.findAll("Ichigo", 2)).thenReturn(List.of(
                new Personaje(1, "Ichigo", "Kurosaki", "Shinigami", null, 2)));

        List<PersonajeDto> resultado = servicio.listar("Ichigo", 2);

        assertEquals(1, resultado.size());
        assertEquals("Kurosaki", resultado.get(0).apellido());
        verify(dao).findAll("Ichigo", 2);
    }

    @Test
    void eliminarUnRegistroInexistenteDevuelveNotFound() {
        when(dao.delete(7)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> servicio.eliminar(7));
        verify(dao, never()).update(any(Personaje.class));
    }
}