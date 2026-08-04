package src.exceptions;

public class BookNotAvailableException extends LibraryException {

    public BookNotAvailableException(int bookId) {
        super("Book with ID " + bookId + " is not available for issue.");
    }
}
