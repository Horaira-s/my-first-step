package src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import src.exceptions.LibraryException;
import src.exceptions.DuplicateEntityException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LibraryWebServer {

    private final LibraryOperations library;
    private HttpServer server;

    public LibraryWebServer() {
        library = new Library("Smart Library");
        try {
            library.addBook(new Book(101, "Java Programming", "James Gosling", "Programming"));
            library.addBook(new Book(102, "Clean Code", "Robert C. Martin", "Software Engineering"));
            library.addMember(new Member(201, "Abu Horaira", "abu@gmail.com"));
        } catch (LibraryException e) {
            throw new IllegalStateException("Failed to initialize library data", e);
        }
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/styles.css", new StaticHandler());
        server.createContext("/app.js", new StaticHandler());
        server.createContext("/api/books", new BooksHandler());
        server.createContext("/api/members", new MembersHandler());
        server.createContext("/api/issue", new IssueHandler());
        server.createContext("/api/return", new ReturnHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Web server started on http://localhost:" + port);
    }

    public void stop(int delaySeconds) {
        if (server != null) {
            server.stop(delaySeconds);
            System.out.println("Web server stopped.");
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new LibraryWebServer().start(port);
    }

    private class BooksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Book> books = library.getBooks();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < books.size(); i++) {
                    Book book = books.get(i);
                    if (i > 0) {
                        json.append(",");
                    }
                    json.append("{\"bookId\":")
                            .append(book.getBookId())
                            .append(",\"title\":\"")
                            .append(escapeJson(book.getTitle()))
                            .append("\",\"author\":\"")
                            .append(escapeJson(book.getAuthor()))
                            .append("\",\"category\":\"")
                            .append(escapeJson(book.getCategory()))
                            .append("\",\"available\":")
                            .append(book.isAvailable());
                    json.append("}");
                }
                json.append("]");
                sendJson(exchange, 200, json.toString());
            } else if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> form = readForm(exchange);
                try {
                    int bookId = Integer.parseInt(form.getOrDefault("bookId", "0"));
                    String title = form.getOrDefault("title", "");
                    String author = form.getOrDefault("author", "");
                    String category = form.getOrDefault("category", "");
                    library.addBook(new Book(bookId, title, author, category));
                    sendJson(exchange, 200, "{\"status\":\"ok\"}");
                } catch (LibraryException e) {
                    sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
                } catch (NumberFormatException e) {
                    sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"Invalid book ID\"}");
                } catch (Exception e) {
                    sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            } else {
                sendText(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private class MembersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Member> members = library.getMembers();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < members.size(); i++) {
                    Member member = members.get(i);
                    if (i > 0) {
                        json.append(",");
                    }
                    json.append("{\"id\":")
                            .append(member.getId())
                            .append(",\"name\":\"")
                            .append(escapeJson(member.getName()))
                            .append("\",\"email\":\"")
                            .append(escapeJson(member.getEmail()))
                            .append("\",\"borrowedBooks\":[");
                    List<Book> borrowed = member.getBorrowedBooks();
                    for (int j = 0; j < borrowed.size(); j++) {
                        if (j > 0) {
                            json.append(",");
                        }
                        json.append("\"").append(escapeJson(borrowed.get(j).getTitle())).append("\"");
                    }
                    json.append("]}");
                }
                json.append("]");
                sendJson(exchange, 200, json.toString());
            } else if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> form = readForm(exchange);
                try {
                    int memberId = Integer.parseInt(form.getOrDefault("memberId", "0"));
                    String name = form.getOrDefault("name", "");
                    String email = form.getOrDefault("email", "");
                    library.addMember(new Member(memberId, name, email));
                    sendJson(exchange, 200, "{\"status\":\"ok\"}");
                } catch (LibraryException e) {
                    sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
                } catch (NumberFormatException e) {
                    sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"Invalid member ID\"}");
                } catch (Exception e) {
                    sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            } else {
                sendText(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private class IssueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method Not Allowed");
                return;
            }
            Map<String, String> form = readForm(exchange);
            try {
                int bookId = Integer.parseInt(form.getOrDefault("bookId", "0"));
                int memberId = Integer.parseInt(form.getOrDefault("memberId", "0"));
                library.issueBook(bookId, memberId);
                sendJson(exchange, 200, "{\"status\":\"ok\"}");
            } catch (LibraryException e) {
                sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
            } catch (NumberFormatException e) {
                sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"Invalid issue request\"}");
            } catch (Exception e) {
                sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class ReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method Not Allowed");
                return;
            }
            Map<String, String> form = readForm(exchange);
            try {
                int bookId = Integer.parseInt(form.getOrDefault("bookId", "0"));
                int memberId = Integer.parseInt(form.getOrDefault("memberId", "0"));
                library.returnBook(bookId, memberId);
                sendJson(exchange, 200, "{\"status\":\"ok\"}");
            } catch (LibraryException e) {
                sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
            } catch (NumberFormatException e) {
                sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"Invalid return request\"}");
            } catch (Exception e) {
                sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if ("/".equals(path)) {
                path = "/index.html";
            }
            String normalized = path.startsWith("/") ? path.substring(1) : path;
            Path file = Paths.get("web", normalized).toAbsolutePath().normalize();
            Path webRoot = Paths.get("web").toAbsolutePath().normalize();
            if (!file.startsWith(webRoot)) {
                sendText(exchange, 403, "Forbidden");
                return;
            }
            if (!Files.exists(file) || Files.isDirectory(file)) {
                sendText(exchange, 404, "Not found");
                return;
            }
            byte[] data = Files.readAllBytes(file);
            String contentType = "text/plain; charset=utf-8";
            if (normalized.endsWith(".html")) {
                contentType = "text/html; charset=utf-8";
            } else if (normalized.endsWith(".css")) {
                contentType = "text/css; charset=utf-8";
            } else if (normalized.endsWith(".js")) {
                contentType = "application/javascript; charset=utf-8";
            }
            exchange.getResponseHeaders().set("Content-Type", contentType);
            sendBytes(exchange, 200, data);
        }
    }

    private Map<String, String> readForm(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> values = new LinkedHashMap<>();
        if (body.isEmpty()) {
            return values;
        }
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) {
                values.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8), URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
            }
        }
        return values;
    }

    private void sendJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        sendText(exchange, statusCode, body);
    }

    private void sendText(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        sendBytes(exchange, statusCode, data);
    }

    private void sendBytes(HttpExchange exchange, int statusCode, byte[] data) throws IOException {
        exchange.sendResponseHeaders(statusCode, data.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(data);
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
