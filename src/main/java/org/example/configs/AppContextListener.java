package org.example.configs;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

@WebListener
@Slf4j
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        DIContainer container = new DIContainer();
        AppConfig.configure(container);
        servletContext.setAttribute(DIContainer.class.getName(), container);
        log.info("AppContextListener initialized successfully");
    }
}
