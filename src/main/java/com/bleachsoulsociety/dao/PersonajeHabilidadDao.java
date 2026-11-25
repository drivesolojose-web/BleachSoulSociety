package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.model.Habilidad;

import java.util.List;

public interface PersonajeHabilidadDao {
    List<Habilidad> findByPersonajeId(int personajeId);
    void assign(int personajeId, int habilidadId);
    boolean remove(int personajeId, int habilidadId);
}