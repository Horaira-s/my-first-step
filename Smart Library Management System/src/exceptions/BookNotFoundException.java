package src.exceptions;

public class BookNotFoundException extends LibraryException {

    public BookNotFoundException(int bookId) {
        super("Book not found with ID: " + bookId);
    }
}
