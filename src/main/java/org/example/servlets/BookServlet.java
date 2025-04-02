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

@WebServlet("/books/*")
@Slf4j
public class BookServlet extends HttpServlet {
    private BookController bookController;

    @Override
    public void init() throws ServletException {
        DIContainer container = (DIContainer) getServletContext().getAttribute(DIContainer.class.getName());
        bookController = container.getSingleton(BookController.class);
        log.info("BookServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String passInfo = request.getPathInfo();

        try {
            if (passInfo == null || "/".equals(passInfo)) {
                Collection<Book> books = bookController.getBooks();

                Map<String, Object> model = new HashMap<>();
                model.put("books", books);

                ThymeleafUtil.render(request, response, getServletContext(), "books/books", model);
                return;
            } else if ("/create".equals(passInfo)) {
                ThymeleafUtil.render(request, response, getServletContext(), "books/book-create", null);
            } else if (passInfo.startsWith("/update/")) {
                Book book = bookController.getBookById(Long.parseLong(passInfo.substring(8)));

                Map<String, Object> model = new HashMap<>();
                model.put("book", book);

                ThymeleafUtil.render(request, response, getServletContext(), "books/book-update", model);
            } else if (passInfo.startsWith("/delete/")) {
                Book book = bookController.getBookById(Long.parseLong(passInfo.substring(8)));

                Map<String, Object> model = new HashMap<>();
                model.put("book", book);

                ThymeleafUtil.render(request, response, getServletContext(), "books/book-delete", model);
            } else {
                Long id = Long.parseLong(passInfo.substring(1));

                Book book = bookController.getBookById(id);

                Map<String, Object> model = new HashMap<>();
                model.put("book", book);
                ThymeleafUtil.render(request, response, getServletContext(), "books/book", model);
            }

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String methodOverride = request.getParameter("_method");
        if ("PUT".equalsIgnoreCase(methodOverride)) {
            doPut(request, response);
            return;
        } else if ("DELETE".equalsIgnoreCase(methodOverride)) {
            doDelete(request, response);
            return;
        }

        String passInfo = request.getPathInfo();

        try {
            Book book = mapToBook(request);
            book = bookController.createBook(book);
            response.sendRedirect(request.getContextPath() + "/books/" + book.getId());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String passInfo = request.getPathInfo();

        try {
            Book book = mapToBook(request);
            book = bookController.updateBook(book);
            response.sendRedirect(request.getContextPath() + "/books/" + book.getId());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String passInfo = request.getPathInfo();

        try {
            Long id = Long.parseLong(request.getParameter("id"));
            bookController.deleteBookById(id);
            response.setStatus(204);
            response.sendRedirect(request.getContextPath() + "/books");

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    private Book mapToBook(HttpServletRequest request) {
        Book book = Book.builder()
                .title(request.getParameter("title"))
                .author(request.getParameter("author"))
                .description(request.getParameter("description"))
                .build();
        if (request.getParameter("id") != null) {
            book.setId(Long.parseLong(request.getParameter("id")));
        }
        return book;
    }

}
