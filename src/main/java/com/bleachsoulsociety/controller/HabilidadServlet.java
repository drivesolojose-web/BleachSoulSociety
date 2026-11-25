package com.bleachsoulsociety.controller;

import com.bleachsoulsociety.dto.HabilidadRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/habilidades/*")
public class HabilidadServlet extends ApiServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length == 0) responder(response, 200, servicios().habilidades().listar(
                request.getParameter("nombre"), request.getParameter("tipo")));
        else if (ruta.length == 1) responder(response, 200, servicios().habilidades().buscar(id(ruta[0], "id")));
        else throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de habilidades no encontrada.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (segmentos(request).length != 0) throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de habilidades no encontrada.");
        responder(response, 201, servicios().habilidades().crear(leerJson(request, HabilidadRequest.class)));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length != 1) throw new com.bleachsoulsociety.exception.NotFoundException("Indica el identificador de la habilidad.");
        responder(response, 200, servicios().habilidades().actualizar(id(ruta[0], "id"), leerJson(request, HabilidadRequest.class)));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length != 1) throw new com.bleachsoulsociety.exception.NotFoundException("Indica el identificador de la habilidad.");
        servicios().habilidades().eliminar(id(ruta[0], "id"));
        responder(response, 200, Map.of("mensaje", "Habilidad eliminada."));
    }
}