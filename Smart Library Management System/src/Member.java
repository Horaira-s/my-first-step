package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Member extends Person {

    private final List<Book> borrowedBooks;

    public Member(int id, String name, String email) {
        super(id, name, email);
        borrowedBooks = new ArrayList<>();
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Library Member");
    }

    public void borrowBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (!borrowedBooks.contains(book)) {
            borrowedBooks.add(book);
        }
    }

    public void returnBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        borrowedBooks.remove(book);
    }

    public List<Book> getBorrowedBooks() {
        return Collections.unmodifiableList(borrowedBooks);
    }

    @Override
    public void display() {
        System.out.println("----------------------------------");
        displayPerson();
        displayRole();
        System.out.println("Borrowed Books: " + borrowedBooks.size());
        if (!borrowedBooks.isEmpty()) {
            System.out.println("Books:");
            for (Book book : borrowedBooks) {
                System.out.println("- " + book.getTitle());
            }
        }
    }

    public void displayMember() {
        display();
    }
}
