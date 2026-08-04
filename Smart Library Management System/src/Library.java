package src;

import src.exceptions.BookNotAvailableException;
import src.exceptions.BookNotBorrowedException;
import src.exceptions.BookNotFoundException;
import src.exceptions.DuplicateEntityException;
import src.exceptions.LibraryException;
import src.exceptions.MemberNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Library implements LibraryOperations {

    private final String libraryName;
    private final List<Book> books;
    private final List<Member> members;

    public Library(String libraryName) {
        this.libraryName = libraryName;
        books = new ArrayList<>();
        members = new ArrayList<>();
    }

    @Override
    public void addBook(Book book) throws DuplicateEntityException {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (findBookById(book.getBookId()) != null) {
            throw new DuplicateEntityException("Book ID already exists.");
        }
        books.add(book);
        System.out.println("Book added successfully!");
    }

    @Override
    public void addMember(Member member) throws DuplicateEntityException {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null.");
        }
        if (findMemberById(member.getId()) != null) {
            throw new DuplicateEntityException("Member ID already exists.");
        }
        members.add(member);
        System.out.println("Member registered successfully!");
    }

    private Book findBookById(int id) {
        for (Book book : books) {
            if (book.getBookId() == id) {
                return book;
            }
        }
        return null;
    }

    private Member findMemberById(int id) {
        for (Member member : members) {
            if (member.getId() == id) {
                return member;
            }
        }
        return null;
    }

    public Book getBookById(int id) throws BookNotFoundException {
        Book book = findBookById(id);
        if (book == null) {
            throw new BookNotFoundException(id);
        }
        return book;
    }

    public Member getMemberById(int id) throws MemberNotFoundException {
        Member member = findMemberById(id);
        if (member == null) {
            throw new MemberNotFoundException(id);
        }
        return member;
    }

    @Override
    public void displayAllBooks() {
        System.out.println("\n========== ALL BOOKS ==========");
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        for (Book book : books) {
            book.display();
        }
    }

    @Override
    public void displayAllMembers() {
        System.out.println("\n========== ALL MEMBERS ==========");
        if (members.isEmpty()) {
            System.out.println("No members available.");
            return;
        }
        for (Member member : members) {
            member.display();
        }
    }

    @Override
    public void issueBook(int bookId, int memberId) throws LibraryException {
        Book book = getBookById(bookId);
        Member member = getMemberById(memberId);
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(bookId);
        }

        book.setAvailable(false);
        member.borrowBook(book);
        System.out.println("Book issued successfully!");
    }

    @Override
    public void returnBook(int bookId, int memberId) throws LibraryException {
        Book book = getBookById(bookId);
        Member member = getMemberById(memberId);
        if (!member.getBorrowedBooks().contains(book)) {
            throw new BookNotBorrowedException(bookId, memberId);
        }

        book.setAvailable(true);
        member.returnBook(book);
        System.out.println("Book returned successfully!");
    }

    public String getLibraryName() {
        return libraryName;
    }

    @Override
    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    @Override
    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }
}
