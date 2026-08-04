package src;

public abstract class LibraryItem implements Displayable {

    private final int itemId;
    private String title;

    protected LibraryItem(int itemId, String title) {
        this.itemId = itemId;
        this.title = title;
    }

    public int getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void display() {
        displayDetails();
    }

    public abstract void displayDetails();
}
