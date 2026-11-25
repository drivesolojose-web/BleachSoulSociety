package com.bleachsoulsociety.service;

import com.bleachsoulsociety.dao.HabilidadDao;
import com.bleachsoulsociety.dto.HabilidadDto;
import com.bleachsoulsociety.dto.HabilidadRequest;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.exception.ValidationException;
import com.bleachsoulsociety.model.Habilidad;

import java.util.List;

public class HabilidadService {
    private final HabilidadDao dao;

    public HabilidadService(HabilidadDao dao) {
        this.dao = dao;
    }

    public List<HabilidadDto> listar(String nombre, String tipo) {
        return dao.findAll(nombre, tipo).stream().map(this::dto).toList();
    }

    public HabilidadDto buscar(int id) {
        return dto(dao.findById(Validaciones.idPositivo(id, "id"))
                .orElseThrow(() -> new NotFoundException("No existe la habilidad solicitada.")));
    }

    public HabilidadDto crear(HabilidadRequest request) {
        return buscar(dao.create(validar(request, null)));
    }

    public HabilidadDto actualizar(int id, HabilidadRequest request) {
        Validaciones.idPositivo(id, "id");
        Habilidad actualizada = validar(request, id);
        buscar(id);
        dao.update(actualizada);
        return buscar(id);
    }

    public void eliminar(int id) {
        if (!dao.delete(Validaciones.idPositivo(id, "id"))) {
            throw new NotFoundException("No existe la habilidad solicitada.");
        }
    }

    private Habilidad validar(HabilidadRequest request, Integer id) {
        if (request == null) throw new ValidationException("El cuerpo JSON es obligatorio.");
        return new Habilidad(id, Validaciones.obligatorio(request.nombre(), "nombre", 100),
                Validaciones.opcional(request.descripcion(), "descripcion", 500),
                Validaciones.obligatorio(request.tipo(), "tipo", 50));
    }

    private HabilidadDto dto(Habilidad habilidad) {
        return new HabilidadDto(habilidad.id(), habilidad.nombre(), habilidad.descripcion(), habilidad.tipo());
    }
}