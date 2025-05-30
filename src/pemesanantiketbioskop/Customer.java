package pemesanantiketbioskop;

public class Customer extends User {

    public Customer(String username, String password) {
        super(username, password);
    }

    @Override
    public void displayInfo() {
        System.out.println("Customer: " + username);
    }
}
