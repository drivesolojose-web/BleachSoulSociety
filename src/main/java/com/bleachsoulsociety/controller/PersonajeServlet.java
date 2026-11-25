package com.bleachsoulsociety.controller;

import com.bleachsoulsociety.dto.AsignacionHabilidadRequest;
import com.bleachsoulsociety.dto.PersonajeRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/personajes/*")
public class PersonajeServlet extends ApiServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length == 0) {
            responder(response, HttpServletResponse.SC_OK, servicios().personajes().listar(
                    request.getParameter("nombre"), filtroId(request.getParameter("divisionId"), "divisionId")));
        } else if (ruta.length == 1) {
            responder(response, HttpServletResponse.SC_OK, servicios().personajes().buscar(id(ruta[0], "id")));
        } else if (ruta.length == 2 && "habilidades".equals(ruta[1])) {
            responder(response, HttpServletResponse.SC_OK,
                    servicios().personajeHabilidades().listar(id(ruta[0], "personajeId")));
        } else {
            throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de personajes no encontrada.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length == 0) {
            responder(response, HttpServletResponse.SC_CREATED,
                    servicios().personajes().crear(leerJson(request, PersonajeRequest.class)));
        } else if (ruta.length == 2 && "habilidades".equals(ruta[1])) {
            int personajeId = id(ruta[0], "personajeId");
            AsignacionHabilidadRequest asignacion = leerJson(request, AsignacionHabilidadRequest.class);
            servicios().personajeHabilidades().asignar(personajeId, asignacion.habilidadId());
            responder(response, HttpServletResponse.SC_CREATED, Map.of("mensaje", "Habilidad asignada."));
        } else {
            throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de personajes no encontrada.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length != 1) throw new com.bleachsoulsociety.exception.NotFoundException("Indica el identificador del personaje.");
        responder(response, HttpServletResponse.SC_OK,
                servicios().personajes().actualizar(id(ruta[0], "id"), leerJson(request, PersonajeRequest.class)));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length == 1) {
            servicios().personajes().eliminar(id(ruta[0], "id"));
            responder(response, HttpServletResponse.SC_OK, Map.of("mensaje", "Personaje eliminado."));
        } else if (ruta.length == 3 && "habilidades".equals(ruta[1])) {
            servicios().personajeHabilidades().retirar(id(ruta[0], "personajeId"), id(ruta[2], "habilidadId"));
            responder(response, HttpServletResponse.SC_OK, Map.of("mensaje", "Habilidad retirada."));
        } else {
            throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de personajes no encontrada.");
        }
    }
}