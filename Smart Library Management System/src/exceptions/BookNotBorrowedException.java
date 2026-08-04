package src.exceptions;

public class BookNotBorrowedException extends LibraryException {

    public BookNotBorrowedException(int bookId, int memberId) {
        super("Book with ID " + bookId + " was not borrowed by member with ID " + memberId + ".");
    }
}
