package org.example.utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

public class ThymeleafUtil {
    private static final TemplateEngine templateEngine = initTemplateEngine();

    private static TemplateEngine initTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    public static void render(HttpServletRequest request,
                              HttpServletResponse response,
                              ServletContext servletContext,
                              String viewName,
                              Map<String, Object> model) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        var exchange = application.buildExchange(request, response);

        WebContext context = new WebContext(exchange);
        if (model != null) {
            context.setVariables(model);
        }

        templateEngine.process(viewName, context, response.getWriter());
    }

}
