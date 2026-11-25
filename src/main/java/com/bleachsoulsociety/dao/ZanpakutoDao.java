package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.model.Zanpakuto;

import java.util.List;
import java.util.Optional;

public interface ZanpakutoDao {
    List<Zanpakuto> findAll(String nombre, Integer personajeId);
    Optional<Zanpakuto> findById(int id);
    int create(Zanpakuto zanpakuto);
    boolean update(Zanpakuto zanpakuto);
    boolean delete(int id);
}