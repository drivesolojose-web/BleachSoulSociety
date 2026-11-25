package com.bleachsoulsociety.controller;

import com.bleachsoulsociety.config.AppServices;
import com.bleachsoulsociety.exception.DataAccessException;
import com.bleachsoulsociety.exception.NotFoundException;
import com.bleachsoulsociety.exception.ValidationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

public abstract class ApiServlet extends HttpServlet {
    protected final Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try {
            super.service(request, response);
        } catch (ValidationException | JsonParseException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            responderError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DataAccessException e) {
            if (esErrorDeIntegridad(e)) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST, "La operación incumple una restricción de datos.");
            } else {
                log("Error al acceder a la base de datos.", e);
                responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno al acceder a los datos.");
            }
        } catch (RuntimeException e) {
            log("Error inesperado en la API.", e);
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor.");
        }
    }

    protected AppServices servicios() {
        return (AppServices) getServletContext().getAttribute(AppServices.class.getName());
    }

    protected <T> T leerJson(HttpServletRequest request, Class<T> tipo) throws IOException {
        T valor = gson.fromJson(request.getReader(), tipo);
        if (valor == null) throw new ValidationException("El cuerpo JSON es obligatorio.");
        return valor;
    }

    protected void responder(HttpServletResponse response, int estado, Object cuerpo) throws IOException {
        response.setStatus(estado);
        response.getWriter().write(gson.toJson(cuerpo));
    }

    protected int id(String valor, String nombre) {
        try {
            int id = Integer.parseInt(valor);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException("El parámetro " + nombre + " debe ser un identificador positivo.");
        }
    }

    protected Integer filtroId(String valor, String nombre) {
        if (valor == null || valor.isBlank()) return null;
        return id(valor, nombre);
    }

    protected String[] segmentos(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null || path.isBlank() || "/".equals(path)) return new String[0];
        return Arrays.stream(path.split("/"))
                .filter(segmento -> !segmento.isBlank())
                .toArray(String[]::new);
    }

    private void responderError(HttpServletResponse response, int estado, String mensaje) throws IOException {
        if (response.isCommitted()) return;
        response.resetBuffer();
        responder(response, estado, new ErrorDto(mensaje));
    }

    private boolean esErrorDeIntegridad(DataAccessException error) {
        Throwable causa = error;
        while (causa != null) {
            if (causa instanceof java.sql.SQLException sqlException &&
                    sqlException.getSQLState() != null && sqlException.getSQLState().startsWith("23")) return true;
            causa = causa.getCause();
        }
        return false;
    }

    private record ErrorDto(String error) {
    }
}