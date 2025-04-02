package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.configs.DIContainer;
import org.example.controllers.BookController;
import org.example.entities.Book;
import org.example.utils.ThymeleafUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/*")
@Slf4j
public class LibraryServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        log.info("LibraryServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String passInfo = request.getPathInfo();

        try {
            if (passInfo == null || "/".equals(passInfo)) {
                ThymeleafUtil.render(request, response, getServletContext(), "library", null);
                return;
            }

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }

    }
}
