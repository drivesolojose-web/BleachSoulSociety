package com.bleachsoulsociety.service;

import com.bleachsoulsociety.dao.PersonajeHabilidadDao;
import com.bleachsoulsociety.dto.HabilidadDto;
import com.bleachsoulsociety.exception.NotFoundException;

import java.util.List;

public class PersonajeHabilidadService {
    private final PersonajeHabilidadDao dao;

    public PersonajeHabilidadService(PersonajeHabilidadDao dao) {
        this.dao = dao;
    }

    public List<HabilidadDto> listar(int personajeId) {
        return dao.findByPersonajeId(Validaciones.idPositivo(personajeId, "personajeId")).stream()
                .map(habilidad -> new HabilidadDto(habilidad.id(), habilidad.nombre(), habilidad.descripcion(), habilidad.tipo()))
                .toList();
    }

    public void asignar(int personajeId, Integer habilidadId) {
        dao.assign(Validaciones.idPositivo(personajeId, "personajeId"),
                Validaciones.idPositivo(habilidadId == null ? 0 : habilidadId, "habilidadId"));
    }

    public void retirar(int personajeId, int habilidadId) {
        if (!dao.remove(Validaciones.idPositivo(personajeId, "personajeId"),
                Validaciones.idPositivo(habilidadId, "habilidadId"))) {
            throw new NotFoundException("La habilidad no está asignada a ese personaje.");
        }
    }
}