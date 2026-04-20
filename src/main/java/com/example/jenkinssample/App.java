package com.example.jenkinssample;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

public final class App {
    private static final int DEFAULT_PORT = 18888;

    private App() {
    }

    public static void main(String[] args) throws IOException {
        int port = resolvePort();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ClasspathStaticFileHandler("static"));
        server.setExecutor(null);

        System.out.printf("Starting Java app on :%d%n", port);
        System.out.println("Serving static resources from classpath:/static");
        server.start();
    }

    private static int resolvePort() {
        String portValue = Optional.ofNullable(System.getenv("PORT")).orElse("");
        if (portValue.isBlank()) {
            return DEFAULT_PORT;
        }

        try {
            return Integer.parseInt(portValue);
        } catch (NumberFormatException ex) {
            System.err.printf("Invalid PORT value '%s', falling back to %d%n", portValue, DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
}
