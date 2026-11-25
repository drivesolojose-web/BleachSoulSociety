package com.bleachsoulsociety.service;

import com.bleachsoulsociety.exception.ValidationException;

final class Validaciones {
    private Validaciones() {
    }

    static String obligatorio(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidationException("El campo " + campo + " es obligatorio.");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new ValidationException("El campo " + campo + " no puede superar " + maximo + " caracteres.");
        }
        return limpio;
    }

    static String opcional(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) return null;
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new ValidationException("El campo " + campo + " no puede superar " + maximo + " caracteres.");
        }
        return limpio;
    }

    static int idPositivo(int id, String campo) {
        if (id <= 0) throw new ValidationException("El campo " + campo + " debe ser un identificador positivo.");
        return id;
    }

    static Integer idOpcional(Integer id, String campo) {
        if (id != null && id <= 0) throw new ValidationException("El campo " + campo + " debe ser un identificador positivo.");
        return id;
    }
}