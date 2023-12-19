package billing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BillingApplication {
	private static final String URL = "jdbc:mysql://localhost:3306/Billgenerator";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL JDBC driver
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			// Connect to the MySQL database
			try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

				// Create a table to store the billing details if it doesn't exist
				createBillingTable(connection);

				// Create a Scanner object for user input
				Scanner scanner = new Scanner(System.in);

				// Get user inputs
				System.out.print("Enter item name: ");
				String itemName = scanner.nextLine();

				System.out.print("Enter quantity: ");
				int quantity = scanner.nextInt();

				System.out.print("Enter price: ");
				double price = scanner.nextDouble();

				// Calculate the total amount
				double totalAmount = quantity * price;

				// Insert the billing details into the database
				insertBillingDetails(connection, itemName, quantity, price, totalAmount);

				// Display the billing details
				displayBillingDetails(connection);

				// Close the connection and scanner
				scanner.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void createBillingTable(Connection connection) throws SQLException {
		String createTableQuery = "CREATE TABLE IF NOT EXISTS billing (id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "item_name VARCHAR(100), quantity INT, price DOUBLE, total_amount DOUBLE)";
		try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
			statement.execute();
		}
	}

	private static void insertBillingDetails(Connection connection, String itemName, int quantity, double price,
			double totalAmount) throws SQLException {
		String insertQuery = "INSERT INTO billing (item_name, quantity, price, total_amount) VALUES (?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
			statement.setString(1, itemName);
			statement.setInt(2, quantity);
			statement.setDouble(3, price);
			statement.setDouble(4, totalAmount);
			statement.executeUpdate();
		}
	}

	private static void displayBillingDetails(Connection connection) throws SQLException {
		String selectQuery = "SELECT * FROM billing";
		try (PreparedStatement statement = connection.prepareStatement(selectQuery);
				ResultSet resultSet = statement.executeQuery()) {

			System.out.println("Billing Details:");
			System.out.println("----------------------------------------------------");
			System.out.printf("%-4s %-15s %-8s %-8s %-8s%n", "ID", "Item Name", "Quantity", "Price", "Total Amount");
			System.out.println("----------------------------------------------------");

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String itemName = resultSet.getString("item_name");
				int quantity = resultSet.getInt("quantity");
				double price = resultSet.getDouble("price");
				double totalAmount = resultSet.getDouble("total_amount");

				System.out.printf("%-4d %-15s %-8d %-8.2f %-8.2f%n", id, itemName, quantity, price, totalAmount);
			}
		}
	}
}
