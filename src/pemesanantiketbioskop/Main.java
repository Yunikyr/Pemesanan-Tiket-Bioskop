package pemesanantiketbioskop;

import java.sql.*;
import java.util.*;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3307/bioskop";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    // Representasi kursi bioskop: 5 baris (A-E), 5 kolom (1-5)
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final char[] ROW_LETTERS = {'A', 'B', 'C', 'D', 'E'};

    public static void main(String[] args) {
        try (
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Scanner scanner = new Scanner(System.in)
        ) {
            // Input user dan simpan sama seperti sebelumnya
            System.out.print("Masukkan username: ");
            String username = scanner.nextLine();
            System.out.print("Masukkan password: ");
            String password = scanner.nextLine();

            String encryptedPass = AESUtil.encrypt(password);
            String decryptedPass = AESUtil.decrypt(encryptedPass);

            System.out.println("\n=== ENKRIPSI & DEKRIPSI ===");
            System.out.println("Password Asli     : " + password);
            System.out.println("Terenkripsi        : " + encryptedPass);
            System.out.println("Didekripsi kembali : " + decryptedPass);

            PreparedStatement insertUser = conn.prepareStatement(
                "INSERT INTO users(username, password, role) VALUES (?, ?, 'customer')",
                Statement.RETURN_GENERATED_KEYS);
            insertUser.setString(1, username);
            insertUser.setString(2, encryptedPass);
            insertUser.executeUpdate();

            ResultSet rs = insertUser.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) userId = rs.getInt(1);

            Customer cust = new Customer(username, password);
            cust.displayInfo();

            System.out.print("Masukkan nama film: ");
            String film = scanner.nextLine();
            System.out.print("Jumlah tiket: ");
            int jumlah = scanner.nextInt();
            scanner.nextLine();

            // Ambil kursi yang sudah dipesan untuk film ini dari database
            Set<String> kursiTerpesan = new HashSet<>();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT kursi FROM pemesanan WHERE film = ?");
            ps.setString(1, film);
            ResultSet rsKursi = ps.executeQuery();
            while (rsKursi.next()) {
                String kursiStr = rsKursi.getString("kursi"); // contoh: "A1, A2"
                if (kursiStr != null) {
                    String[] kursiArray = kursiStr.split(",\\s*");
                    kursiTerpesan.addAll(Arrays.asList(kursiArray));
                }
            }

            // Tampilkan denah kursi
            System.out.println("\nDenah kursi (O=tersedia, X=sudah dipesan):");
            for (int r = 0; r < ROWS; r++) {
                for (int c = 1; c <= COLS; c++) {
                    String seat = "" + ROW_LETTERS[r] + c;
                    if (kursiTerpesan.contains(seat)) {
                        System.out.print(seat + "(X)  ");
                    } else {
                        System.out.print(seat + "(O)  ");
                    }
                }
                System.out.println();
            }

            // Pilih kursi
            List<String> kursiDipilih = new ArrayList<>();
            for (int i = 1; i <= jumlah; i++) {
                while (true) {
                    System.out.print("Pilih kursi ke-" + i + ": ");
                    String kursi = scanner.nextLine().toUpperCase();

                    // Validasi kursi valid (A1-E5)
                    if (!isValidSeat(kursi)) {
                        System.out.println("❌ Kursi tidak valid, coba lagi.");
                        continue;
                    }
                    // Cek apakah sudah dipesan
                    if (kursiTerpesan.contains(kursi) || kursiDipilih.contains(kursi)) {
                        System.out.println("❌ Kursi sudah dipesan, pilih kursi lain.");
                        continue;
                    }

                    kursiDipilih.add(kursi);
                    break;
                }
            }

            // Simpan pesanan ke database
            PreparedStatement insertOrder = conn.prepareStatement(
                "INSERT INTO pemesanan(user_id, film, jumlah_tiket, kursi) VALUES (?, ?, ?, ?)");
            insertOrder.setInt(1, userId);
            insertOrder.setString(2, film);
            insertOrder.setInt(3, jumlah);
            insertOrder.setString(4, String.join(", ", kursiDipilih));
            insertOrder.executeUpdate();
            
            System.out.println("");
            System.out.println("Pemesanan berhasil!");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isValidSeat(String seat) {
        if (seat.length() < 2 || seat.length() > 3) return false;

        char row = seat.charAt(0);
        if ("ABCDE".indexOf(row) == -1) return false;

        String colStr = seat.substring(1);
        try {
            int col = Integer.parseInt(colStr);
            if (col < 1 || col > 5) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
