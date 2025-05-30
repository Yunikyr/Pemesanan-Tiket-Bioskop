package pemesanantiketbioskop;

public class User {
    protected String username;
    protected String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void displayInfo() {
        System.out.println("User: " + username);
    }
}
