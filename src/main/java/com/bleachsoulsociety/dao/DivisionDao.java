package com.bleachsoulsociety.dao;

import com.bleachsoulsociety.model.Division;

import java.util.List;
import java.util.Optional;

public interface DivisionDao {
    List<Division> findAll(String nombre);
    Optional<Division> findById(int id);
    int create(Division division);
    boolean update(Division division);
    boolean delete(int id);
}