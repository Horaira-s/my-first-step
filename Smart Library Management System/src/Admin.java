package src;

public class Admin extends Person {

    public Admin(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Library Administrator");
    }

    @Override
    public void display() {
        System.out.println("----------------------------------");
        displayPerson();
        displayRole();
    }

    public void displayAdmin() {
        display();
    }
}
