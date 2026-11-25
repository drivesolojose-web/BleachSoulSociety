package com.bleachsoulsociety.config;

import com.bleachsoulsociety.service.DivisionService;
import com.bleachsoulsociety.service.HabilidadService;
import com.bleachsoulsociety.service.PersonajeHabilidadService;
import com.bleachsoulsociety.service.PersonajeService;
import com.bleachsoulsociety.service.ZanpakutoService;
import com.zaxxer.hikari.HikariDataSource;

public record AppServices(HikariDataSource dataSource, PersonajeService personajes,
                          DivisionService divisiones, ZanpakutoService zanpakutos,
                          HabilidadService habilidades, PersonajeHabilidadService personajeHabilidades) {
}