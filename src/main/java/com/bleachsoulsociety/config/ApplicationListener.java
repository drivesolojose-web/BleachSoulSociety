package com.bleachsoulsociety.config;

import com.bleachsoulsociety.dao.JdbcDivisionDao;
import com.bleachsoulsociety.dao.JdbcHabilidadDao;
import com.bleachsoulsociety.dao.JdbcPersonajeDao;
import com.bleachsoulsociety.dao.JdbcPersonajeHabilidadDao;
import com.bleachsoulsociety.dao.JdbcZanpakutoDao;
import com.bleachsoulsociety.service.DivisionService;
import com.bleachsoulsociety.service.HabilidadService;
import com.bleachsoulsociety.service.PersonajeHabilidadService;
import com.bleachsoulsociety.service.PersonajeService;
import com.bleachsoulsociety.service.ZanpakutoService;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        HikariDataSource dataSource = DatabaseConfig.createDataSource();
        AppServices services = new AppServices(dataSource,
                new PersonajeService(new JdbcPersonajeDao(dataSource)),
                new DivisionService(new JdbcDivisionDao(dataSource)),
                new ZanpakutoService(new JdbcZanpakutoDao(dataSource)),
                new HabilidadService(new JdbcHabilidadDao(dataSource)),
                new PersonajeHabilidadService(new JdbcPersonajeHabilidadDao(dataSource)));
        event.getServletContext().setAttribute(AppServices.class.getName(), services);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Object services = event.getServletContext().getAttribute(AppServices.class.getName());
        if (services instanceof AppServices appServices) appServices.dataSource().close();
    }
}