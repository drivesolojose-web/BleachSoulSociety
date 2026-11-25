package com.bleachsoulsociety.controller;

import com.bleachsoulsociety.dto.ZanpakutoRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/zanpakutos/*")
public class ZanpakutoServlet extends ApiServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length == 0) responder(response, 200, servicios().zanpakutos().listar(
                request.getParameter("nombre"), filtroId(request.getParameter("personajeId"), "personajeId")));
        else if (ruta.length == 1) responder(response, 200, servicios().zanpakutos().buscar(id(ruta[0], "id")));
        else throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de zanpakutos no encontrada.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (segmentos(request).length != 0) throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de zanpakutos no encontrada.");
        responder(response, 201, servicios().zanpakutos().crear(leerJson(request, ZanpakutoRequest.class)));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length != 1) throw new com.bleachsoulsociety.exception.NotFoundException("Indica el identificador de la zanpakuto.");
        responder(response, 200, servicios().zanpakutos().actualizar(id(ruta[0], "id"), leerJson(request, ZanpakutoRequest.class)));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length != 1) throw new com.bleachsoulsociety.exception.NotFoundException("Indica el identificador de la zanpakuto.");
        servicios().zanpakutos().eliminar(id(ruta[0], "id"));
        responder(response, 200, Map.of("mensaje", "Zanpakuto eliminada."));
    }
}