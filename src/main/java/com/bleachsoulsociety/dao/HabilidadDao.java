package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.model.Habilidad;

import java.util.List;
import java.util.Optional;

public interface HabilidadDao {
    List<Habilidad> findAll(String nombre, String tipo);
    Optional<Habilidad> findById(int id);
    int create(Habilidad habilidad);
    boolean update(Habilidad habilidad);
    boolean delete(int id);
}