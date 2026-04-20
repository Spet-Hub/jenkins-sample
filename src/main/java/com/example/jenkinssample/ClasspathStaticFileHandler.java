package com.example.jenkinssample;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

final class ClasspathStaticFileHandler implements HttpHandler {
    private final ClassLoader classLoader;
    private final String resourceRoot;

    ClasspathStaticFileHandler(String resourceRoot) {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.resourceRoot = resourceRoot;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
            sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        String resourcePath = resolveResourcePath(exchange);
        if (resourcePath == null) {
            sendText(exchange, 404, "Not Found");
            return;
        }

        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                sendText(exchange, 404, "Not Found");
                return;
            }

            byte[] content = inputStream.readAllBytes();
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", detectContentType(resourcePath));

            if ("HEAD".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                return;
            }

            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(content);
            }
        }
    }

    private String resolveResourcePath(HttpExchange exchange) {
        String rawPath = exchange.getRequestURI().getPath();
        String normalizedPath = rawPath == null ? "" : rawPath.trim();

        if (normalizedPath.isEmpty() || "/".equals(normalizedPath)) {
            return resourceRoot + "/index.html";
        }

        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }

        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath + "index.html";
        }

        Path candidate = Paths.get(normalizedPath).normalize();
        String resourcePath = candidate.toString().replace('\\', '/');
        if (resourcePath.isBlank() || ".".equals(resourcePath) || resourcePath.startsWith("..")) {
            return null;
        }

        return resourceRoot + "/" + resourcePath;
    }

    private String detectContentType(String resourcePath) {
        String contentType = URLConnection.guessContentTypeFromName(resourcePath);
        return contentType != null ? contentType : "application/octet-stream";
    }

    private void sendText(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] content = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, content.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(content);
        }
    }
}
