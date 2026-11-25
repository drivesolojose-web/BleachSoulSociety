package com.bleachsoulsociety.dto;

public record PersonajeRequest(String nombre, String apellido, String especie,
                               String rango, Integer divisionId) {
}