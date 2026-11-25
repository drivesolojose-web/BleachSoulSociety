package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.model.Personaje;

import java.util.List;
import java.util.Optional;

public interface PersonajeDao {
    List<Personaje> findAll(String nombre, Integer divisionId);
    Optional<Personaje> findById(int id);
    int create(Personaje personaje);
    boolean update(Personaje personaje);
    boolean delete(int id);
}