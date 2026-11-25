package com.bleachsoulsociety.service;

import com.bleachsoulsociety.dao.DivisionDao;
import com.bleachsoulsociety.dao.HabilidadDao;
import com.bleachsoulsociety.dao.PersonajeHabilidadDao;
import com.bleachsoulsociety.dao.ZanpakutoDao;
import com.bleachsoulsociety.dto.DivisionRequest;
import com.bleachsoulsociety.dto.HabilidadRequest;
import com.bleachsoulsociety.dto.ZanpakutoRequest;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.exception.ValidationException;
import com.bleachsoulsociety.model.Division;
import com.bleachsoulsociety.model.Habilidad;
import com.bleachsoulsociety.model.Zanpakuto;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ServiceValidationTest {
    @Test
    void divisionRecortaLosCamposYValidaNombreObligatorio() {
        DivisionDao dao = mock(DivisionDao.class);
        DivisionService servicio = new DivisionService(dao);
        when(dao.create(any(Division.class))).thenReturn(3);
        when(dao.findById(3)).thenReturn(Optional.of(new Division(3, "Sexta", "Disciplina")));

        assertEquals("Sexta", servicio.crear(new DivisionRequest(" Sexta ", " Disciplina ")).nombre());
        verify(dao).create(new Division(null, "Sexta", "Disciplina"));
        assertThrows(ValidationException.class, () -> servicio.crear(new DivisionRequest(" ", null)));
    }

    @Test
    void habilidadExigeTipoYNombre() {
        HabilidadDao dao = mock(HabilidadDao.class);
        HabilidadService servicio = new HabilidadService(dao);

        assertThrows(ValidationException.class,
                () -> servicio.crear(new HabilidadRequest("Kidō", "Hechizos", " ")));
        assertThrows(ValidationException.class, () -> servicio.crear(null));
        verifyNoInteractions(dao);
    }

    @Test
    void zanpakutoExigeUnPersonajeYNombreYAdmiteCamposOpcionalesNulos() {
        ZanpakutoDao dao = mock(ZanpakutoDao.class);
        ZanpakutoService servicio = new ZanpakutoService(dao);
        when(dao.create(any(Zanpakuto.class))).thenReturn(4);
        when(dao.findById(4)).thenReturn(Optional.of(new Zanpakuto(4, 2, "Zangetsu", null, null)));

        assertEquals("Zangetsu", servicio.crear(new ZanpakutoRequest(2, " Zangetsu ", " ", null)).nombre());
        verify(dao).create(new Zanpakuto(null, 2, "Zangetsu", null, null));
        assertThrows(ValidationException.class,
                () -> servicio.crear(new ZanpakutoRequest(null, "Zangetsu", null, null)));
    }

    @Test
    void habilidadDePersonajeValidaIdsYRegistroNoAsignado() {
        PersonajeHabilidadDao dao = mock(PersonajeHabilidadDao.class);
        PersonajeHabilidadService servicio = new PersonajeHabilidadService(dao);
        when(dao.remove(1, 2)).thenReturn(false);

        assertThrows(ValidationException.class, () -> servicio.asignar(1, null));
        assertThrows(ValidationException.class, () -> servicio.listar(0));
        assertThrows(NotFoundException.class, () -> servicio.retirar(1, 2));
        verify(dao, never()).assign(1, 0);
    }
}