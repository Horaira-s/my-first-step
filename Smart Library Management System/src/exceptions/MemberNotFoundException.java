package src.exceptions;

public class MemberNotFoundException extends LibraryException {

    public MemberNotFoundException(int memberId) {
        super("Member not found with ID: " + memberId);
    }
}
