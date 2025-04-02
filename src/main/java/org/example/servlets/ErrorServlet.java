package org.example.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.utils.ThymeleafUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/error")
public class ErrorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processError(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processError(request, response);
    }

    private void processError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", statusCode != null ? statusCode : 500);
        model.put("message", message != null ? message : "Unexpected error");
        model.put("exception", exception != null ? exception : "Unknown error");

        ThymeleafUtil.render(request, response, getServletContext(), "errors/error", model);
    }
}
