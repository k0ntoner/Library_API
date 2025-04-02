package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.configs.DIContainer;
import org.example.controllers.BookController;
import org.example.controllers.BookCopyController;
import org.example.entities.Book;
import org.example.entities.BookCopy;
import org.example.enums.Status;
import org.example.services.BookService;
import org.example.utils.ThymeleafUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/copies/*")
@Slf4j
public class BookCopyServlet extends HttpServlet {
    private BookCopyController bookCopyController;
    private BookController bookController;

    @Override
    public void init() throws ServletException {
        DIContainer container = (DIContainer) getServletContext().getAttribute(DIContainer.class.getName());
        bookCopyController = container.getSingleton(BookCopyController.class);
        bookController = container.getSingleton(BookController.class);
        log.info("BookCopyServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String passInfo = request.getPathInfo();

        try {
            if (passInfo == null || "/".equals(passInfo)) {
                Collection<BookCopy> copies = bookCopyController.getBookCopies();

                Map<String, Object> model = new HashMap<>();
                model.put("copies", copies);

                ThymeleafUtil.render(request, response, getServletContext(), "copies/copies", model);
                return;
            } else if ("/create".equals(passInfo)) {
                Collection<Book> books = bookController.getBooks();
                Map<String, Object> model = new HashMap<>();
                model.put("books", books);

                ThymeleafUtil.render(request, response, getServletContext(), "copies/copy-create", model);
            } else if (passInfo.startsWith("/update/")) {
                BookCopy copy = bookCopyController.getBookCopyById(Long.parseLong(passInfo.substring(8)));

                Map<String, Object> model = new HashMap<>();
                model.put("copy", copy);

                ThymeleafUtil.render(request, response, getServletContext(), "copies/copy-update", model);
            } else if (passInfo.startsWith("/delete/")) {
                BookCopy copy = bookCopyController.getBookCopyById(Long.parseLong(passInfo.substring(8)));

                Map<String, Object> model = new HashMap<>();
                model.put("copy", copy);

                ThymeleafUtil.render(request, response, getServletContext(), "copies/copy-delete", model);
            } else {
                Long id = Long.parseLong(passInfo.substring(1));

                BookCopy copy = bookCopyController.getBookCopyById(id);

                Map<String, Object> model = new HashMap<>();
                model.put("copy", copy);
                ThymeleafUtil.render(request, response, getServletContext(), "copies/copy", model);
            }

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
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
            BookCopy copy = mapToCopy(request);
            copy = bookCopyController.createBookCopy(copy);
            response.sendRedirect(request.getContextPath() + "/copies/" + copy.getId());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String passInfo = request.getPathInfo();

        try {
            BookCopy copy = mapToCopy(request);
            copy = bookCopyController.updateBookCopy(copy);
            response.sendRedirect(request.getContextPath() + "/copies/" + copy.getId());
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
            bookCopyController.deleteBookCopyById(id);
            response.sendRedirect(request.getContextPath() + "/copies");
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    private BookCopy mapToCopy(HttpServletRequest request) throws SQLException, IOException {
        BookCopy copy = BookCopy.builder()
                .status(Status.valueOf(request.getParameter("status")))
                .build();
        if (request.getParameter("id") != null) {
            copy.setId(Long.parseLong(request.getParameter("id")));
        }
        copy.setBook(bookController.getBookById(Long.parseLong(request.getParameter("bookId"))));
        return copy;
    }
}
