package fr.mtrfranceaddon.mod.common.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.mtrfranceaddon.mod.common.Init;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Locale;

/**
 * MTRFRA Webserver
 */
public final class TactileMapServer {

    /**
     * PATH
     * fabric/src/main/resources/assets/mtrfranceaddon/web/.
     */
    private static final String WEB_ROOT = "/assets/mtrfranceaddon/web/";
    private static final String DEFAULT_FILE = "index.html";

    private static HttpServer server;
    private static int port = -1;

    private TactileMapServer() {
    }

    public static synchronized int getPortOrStart() {
        if (server == null) {
            start();
        }
        return port;
    }

    private static void start() {
        try {
            final HttpServer newServer = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            newServer.createContext("/", new StaticFileHandler());
            newServer.setExecutor(null); // create port
            newServer.start();

            server = newServer;
            port = newServer.getAddress().getPort();
            Init.LOGGER.info("TactileMapServer démarré sur le port {}", port);
        } catch (IOException e) {
            Init.LOGGER.error("Impossible de démarrer TactileMapServer", e);
            server = null;
            port = -1;
        }
    }

    public static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            port = -1;
        }
    }

    private static final class StaticFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            if (path == null || path.isEmpty() || path.equals("/")) {
                path = DEFAULT_FILE;
            } else {
                path = path.startsWith("/") ? path.substring(1) : path;
            }

            if (path.contains("..")) {
                sendError(exchange, 400);
                return;
            }

            final String resourcePath = WEB_ROOT + path;

            try (InputStream resourceStream = TactileMapServer.class.getResourceAsStream(resourcePath)) {
                if (resourceStream == null) {
                    sendError(exchange, 404);
                    return;
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = resourceStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                final byte[] content = buffer.toByteArray();

                exchange.getResponseHeaders().add("Content-Type", getMimeType(path));
                exchange.sendResponseHeaders(200, content.length);

                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(content);
                }
            }
        }

        private static void sendError(HttpExchange exchange, int statusCode) throws IOException {
            exchange.sendResponseHeaders(statusCode, -1);
            exchange.close();
        }

        private static String getMimeType(String fileName) {
            final String lowerCaseFileName = fileName.toLowerCase(Locale.ROOT);
            if (lowerCaseFileName.endsWith(".html")) return "text/html; charset=utf-8";
            if (lowerCaseFileName.endsWith(".js")) return "text/javascript; charset=utf-8";
            if (lowerCaseFileName.endsWith(".css")) return "text/css; charset=utf-8";
            if (lowerCaseFileName.endsWith(".json")) return "application/json; charset=utf-8";
            if (lowerCaseFileName.endsWith(".svg")) return "image/svg+xml";
            if (lowerCaseFileName.endsWith(".png")) return "image/png";
            return "application/octet-stream";
        }
    }
}