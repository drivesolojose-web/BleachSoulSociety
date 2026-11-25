package com.bleachsoulsociety.service;

import com.bleachsoulsociety.dao.DivisionDao;
import com.bleachsoulsociety.dto.DivisionDto;
import com.bleachsoulsociety.dto.DivisionRequest;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.exception.ValidationException;
import com.bleachsoulsociety.model.Division;

import java.util.List;

public class DivisionService {
    private final DivisionDao dao;

    public DivisionService(DivisionDao dao) {
        this.dao = dao;
    }

    public List<DivisionDto> listar(String nombre) {
        return dao.findAll(nombre).stream().map(this::dto).toList();
    }

    public DivisionDto buscar(int id) {
        return dto(dao.findById(Validaciones.idPositivo(id, "id"))
                .orElseThrow(() -> new NotFoundException("No existe la división solicitada.")));
    }

    public DivisionDto crear(DivisionRequest request) {
        return buscar(dao.create(validar(request, null)));
    }

    public DivisionDto actualizar(int id, DivisionRequest request) {
        Validaciones.idPositivo(id, "id");
        Division actualizada = validar(request, id);
        buscar(id);
        dao.update(actualizada);
        return buscar(id);
    }

    public void eliminar(int id) {
        if (!dao.delete(Validaciones.idPositivo(id, "id"))) {
            throw new NotFoundException("No existe la división solicitada.");
        }
    }

    private Division validar(DivisionRequest request, Integer id) {
        if (request == null) throw new ValidationException("El cuerpo JSON es obligatorio.");
        return new Division(id, Validaciones.obligatorio(request.nombre(), "nombre", 100),
                Validaciones.opcional(request.descripcion(), "descripcion", 500));
    }

    private DivisionDto dto(Division division) {
        return new DivisionDto(division.id(), division.nombre(), division.descripcion());
    }
}