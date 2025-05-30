package pemesanantiketbioskop;

public class Admin extends User {

    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public void displayInfo() {
        System.out.println("Admin: " + username);
    }
}
