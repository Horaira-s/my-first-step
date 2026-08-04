package src;

public class Book extends LibraryItem {

    private String author;
    private String category;
    private boolean available;

    public Book(int bookId, String title, String author, String category) {
        super(bookId, title);
        this.author = author;
        this.category = category;
        this.available = true;
    }

    public int getBookId() {
        return getItemId();
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------");
        System.out.println("Book ID     : " + getItemId());
        System.out.println("Title       : " + getTitle());
        System.out.println("Author      : " + author);
        System.out.println("Category    : " + category);
        System.out.println("Status      : " + (available ? "Available" : "Issued"));
    }

    public void displayBook() {
        displayDetails();
    }
}
