package src;

import src.exceptions.DuplicateEntityException;
import src.exceptions.LibraryException;

import java.util.List;

public interface LibraryOperations {

    void addBook(Book book) throws DuplicateEntityException;

    void addMember(Member member) throws DuplicateEntityException;

    void issueBook(int bookId, int memberId) throws LibraryException;

    void returnBook(int bookId, int memberId) throws LibraryException;

    List<Book> getBooks();

    List<Member> getMembers();

    void displayAllBooks();

    void displayAllMembers();
}
