package com.bleachsoulsociety.service;

import com.bleachsoulsociety.dao.PersonajeDao;
import com.bleachsoulsociety.dto.PersonajeDto;
import com.bleachsoulsociety.dto.PersonajeRequest;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.exception.ValidationException;
import com.bleachsoulsociety.model.Personaje;

import java.util.List;

public class PersonajeService {
    private final PersonajeDao dao;

    public PersonajeService(PersonajeDao dao) {
        this.dao = dao;
    }

    public List<PersonajeDto> listar(String nombre, Integer divisionId) {
        Validaciones.idOpcional(divisionId, "divisionId");
        return dao.findAll(nombre, divisionId).stream().map(this::dto).toList();
    }

    public PersonajeDto buscar(int id) {
        return dto(dao.findById(Validaciones.idPositivo(id, "id"))
                .orElseThrow(() -> new NotFoundException("No existe el personaje solicitado.")));
    }

    public PersonajeDto crear(PersonajeRequest request) {
        Personaje nuevo = validar(request, null);
        return buscar(dao.create(nuevo));
    }

    public PersonajeDto actualizar(int id, PersonajeRequest request) {
        Validaciones.idPositivo(id, "id");
        Personaje actualizado = validar(request, id);
        buscar(id);
        dao.update(actualizado);
        return buscar(id);
    }

    public void eliminar(int id) {
        if (!dao.delete(Validaciones.idPositivo(id, "id"))) {
            throw new NotFoundException("No existe el personaje solicitado.");
        }
    }

    private Personaje validar(PersonajeRequest request, Integer id) {
        if (request == null) throw new ValidationException("El cuerpo JSON es obligatorio.");
        return new Personaje(id, Validaciones.obligatorio(request.nombre(), "nombre", 100),
                Validaciones.opcional(request.apellido(), "apellido", 100),
                Validaciones.obligatorio(request.especie(), "especie", 60),
                Validaciones.opcional(request.rango(), "rango", 100),
                Validaciones.idOpcional(request.divisionId(), "divisionId"));
    }

    private PersonajeDto dto(Personaje personaje) {
        return new PersonajeDto(personaje.id(), personaje.nombre(), personaje.apellido(),
                personaje.especie(), personaje.rango(), personaje.divisionId());
    }
}