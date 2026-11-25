package com.bleachsoulsociety.controller;

import com.bleachsoulsociety.dto.DivisionRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/divisiones/*")
public class DivisionServlet extends ApiServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length == 0) responder(response, 200, servicios().divisiones().listar(request.getParameter("nombre")));
        else if (ruta.length == 1) responder(response, 200, servicios().divisiones().buscar(id(ruta[0], "id")));
        else throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de divisiones no encontrada.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (segmentos(request).length != 0) throw new com.bleachsoulsociety.exception.NotFoundException("Ruta de divisiones no encontrada.");
        responder(response, 201, servicios().divisiones().crear(leerJson(request, DivisionRequest.class)));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length != 1) throw new com.bleachsoulsociety.exception.NotFoundException("Indica el identificador de la división.");
        responder(response, 200, servicios().divisiones().actualizar(id(ruta[0], "id"), leerJson(request, DivisionRequest.class)));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] ruta = segmentos(request);
        if (ruta.length != 1) throw new com.bleachsoulsociety.exception.NotFoundException("Indica el identificador de la división.");
        servicios().divisiones().eliminar(id(ruta[0], "id"));
        responder(response, 200, Map.of("mensaje", "División eliminada."));
    }
}