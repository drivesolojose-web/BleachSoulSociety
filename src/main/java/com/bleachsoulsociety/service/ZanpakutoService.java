package com.bleachsoulsociety.service;

import com.bleachsoulsociety.dao.ZanpakutoDao;
import com.bleachsoulsociety.dto.ZanpakutoDto;
import com.bleachsoulsociety.dto.ZanpakutoRequest;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.exception.ValidationException;
import com.bleachsoulsociety.model.Zanpakuto;

import java.util.List;

public class ZanpakutoService {
    private final ZanpakutoDao dao;

    public ZanpakutoService(ZanpakutoDao dao) {
        this.dao = dao;
    }

    public List<ZanpakutoDto> listar(String nombre, Integer personajeId) {
        Validaciones.idOpcional(personajeId, "personajeId");
        return dao.findAll(nombre, personajeId).stream().map(this::dto).toList();
    }

    public ZanpakutoDto buscar(int id) {
        return dto(dao.findById(Validaciones.idPositivo(id, "id"))
                .orElseThrow(() -> new NotFoundException("No existe la zanpakuto solicitada.")));
    }

    public ZanpakutoDto crear(ZanpakutoRequest request) {
        return buscar(dao.create(validar(request, null)));
    }

    public ZanpakutoDto actualizar(int id, ZanpakutoRequest request) {
        Validaciones.idPositivo(id, "id");
        Zanpakuto actualizada = validar(request, id);
        buscar(id);
        dao.update(actualizada);
        return buscar(id);
    }

    public void eliminar(int id) {
        if (!dao.delete(Validaciones.idPositivo(id, "id"))) {
            throw new NotFoundException("No existe la zanpakuto solicitada.");
        }
    }

    private Zanpakuto validar(ZanpakutoRequest request, Integer id) {
        if (request == null) throw new ValidationException("El cuerpo JSON es obligatorio.");
        return new Zanpakuto(id, Validaciones.idPositivo(
                request.personajeId() == null ? 0 : request.personajeId(), "personajeId"),
                Validaciones.obligatorio(request.nombre(), "nombre", 100),
                Validaciones.opcional(request.shikai(), "shikai", 500),
                Validaciones.opcional(request.bankai(), "bankai", 500));
    }

    private ZanpakutoDto dto(Zanpakuto zanpakuto) {
        return new ZanpakutoDto(zanpakuto.id(), zanpakuto.personajeId(), zanpakuto.nombre(),
                zanpakuto.shikai(), zanpakuto.bankai());
    }
}