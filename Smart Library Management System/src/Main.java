package src;

import src.exceptions.DuplicateEntityException;
import src.exceptions.LibraryException;

import java.awt.Desktop;
import java.net.URI;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Start web server
        final LibraryWebServer webServer = new LibraryWebServer();
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.err.println("Invalid port argument, using default 8080.");
            }
        }
        try {
            webServer.start(port);
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("http://localhost:" + port + "/"));
                }
            } catch (Exception e) {
                System.err.println("Unable to open browser: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Failed to start web server: " + e.getMessage());
        }

        // The console demo (same output as before)
        LibraryOperations library = new Library("Smart Library");

        Book book1 = new Book(101, "Java Programming", "James Gosling", "Programming");
        Book book2 = new Book(102, "Clean Code", "Robert C. Martin", "Software Engineering");

        try {
            library.addBook(book1);
            library.addBook(book2);
        } catch (DuplicateEntityException dee) {
            System.err.println("Library setup error: " + dee.getMessage());
        }

        Member member = new Member(201, "Abu Horaira", "abu@gmail.com");
        Admin admin = new Admin(1, "Library Admin", "admin@library.com");

        try {
            library.addMember(member);
        } catch (DuplicateEntityException dee) {
            System.err.println("Library setup error: " + dee.getMessage());
        }

        System.out.println("\n========== ADMIN ==========");
        admin.display();

        System.out.println("\n========== MEMBER ==========");
        member.display();

        System.out.println("\n========== BOOKS ==========");
        library.displayAllBooks();

        System.out.println("\n========== ISSUE BOOK ==========");
        try {
            library.issueBook(101, 201);
        } catch (LibraryException le) {
            System.err.println("Issue failed: " + le.getMessage());
        }

        System.out.println("\n========== MEMBER AFTER ISSUE ==========");
        member.display();

        System.out.println("\n========== BOOKS AFTER ISSUE ==========");
        library.displayAllBooks();

        System.out.println("\n========== RETURN BOOK ==========");
        try {
            library.returnBook(101, 201);
        } catch (LibraryException le) {
            System.err.println("Return failed: " + le.getMessage());
        }

        System.out.println("\n========== FINAL BOOK STATUS ==========");
        library.displayAllBooks();

        // Add shutdown hook to stop server on Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                webServer.stop(0);
            } catch (Exception ignored) {}
        }));

        // Keep application alive until user wants to stop
        System.out.println("\nPress ENTER to stop the server and exit (or Ctrl+C)...");
        try {
            if (System.console() != null) {
                try (Scanner sc = new Scanner(System.in)) {
                    sc.nextLine();
                }
            } else {
                System.out.println("No interactive console detected; use Ctrl+C to stop.");
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (InterruptedException ie) {
        } catch (Exception e) {
            System.err.println("Error waiting for input: " + e.getMessage());
        }

        try {
            webServer.stop(0);
        } catch (Exception e) {
            System.err.println("Error while stopping web server: " + e.getMessage());
        }

        System.out.println("Application exiting.");
    }
}
