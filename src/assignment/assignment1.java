package assignment;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class assignment1 extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtId;
    private JButton btnInsert, btnUpdate, btnDelete, btnViewLast;
    private JTextArea textArea;
    
    private static final String URL = "jdbc:mysql://localhost:3306/class";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public assignment1() {
        setTitle("Customer Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 2, 10, 10));

        // Initialize components
        txtId = new JTextField();
        
        btnInsert = new JButton("Insert");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnViewLast = new JButton("View Last");
        
        textArea = new JTextArea();
        textArea.setEditable(false);

        // Add components to the frame
        add(new JLabel("ID (for Update/Delete):"));
        add(txtId);

        add(btnInsert);
        add(btnUpdate);
        add(btnDelete);
        add(btnViewLast);

        add(new JScrollPane(textArea));

        // Add action listeners
        btnInsert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showInsertDialog();
            }
        });
        
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUpdateDialog();
            }
        });
        
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRecord();
            }
        });
        
        btnViewLast.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewLastRecord();
            }
        });
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void showInsertDialog() {
        JTextField txtName = new JTextField();
        JTextField txtAddress = new JTextField();
        JTextField txtContact = new JTextField();
        JTextField txtCity = new JTextField();
        JTextField txtPostalCode = new JTextField();
        JTextField txtCountry = new JTextField();
        
        Object[] message = {
            "Name:", txtName,
            "Address:", txtAddress,
            "Contact:", txtContact,
            "City:", txtCity,
            "Postal Code:", txtPostalCode,
            "Country:", txtCountry
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Enter Customer Details", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO Customers (Customersname, Address, CustomerContact, City, PostalCode, Country) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, txtName.getText());
                    stmt.setString(2, txtAddress.getText());
                    stmt.setString(3, txtContact.getText());
                    stmt.setString(4, txtCity.getText());
                    stmt.setString(5, txtPostalCode.getText());
                    stmt.setString(6, txtCountry.getText());
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Record inserted successfully.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error inserting record.");
            }
        }
    }

    private void showUpdateDialog() {
        int id;
        try {
            id = Integer.parseInt(txtId.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.");
            return;
        }

        try (Connection conn = getConnection()) {
            String selectSql = "SELECT * FROM Customers WHERE idCustomers = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, id);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        JTextField txtName = new JTextField(rs.getString("Customersname"));
                        JTextField txtAddress = new JTextField(rs.getString("Address"));
                        JTextField txtContact = new JTextField(rs.getString("CustomerContact"));
                        JTextField txtCity = new JTextField(rs.getString("City"));
                        JTextField txtPostalCode = new JTextField(rs.getString("PostalCode"));
                        JTextField txtCountry = new JTextField(rs.getString("Country"));
                        
                        Object[] message = {
                            "Name:", txtName,
                            "Address:", txtAddress,
                            "Contact:", txtContact,
                            "City:", txtCity,
                            "Postal Code:", txtPostalCode,
                            "Country:", txtCountry
                        };
                        
                        int option = JOptionPane.showConfirmDialog(this, message, "Update Customer Details", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            String updateSql = "UPDATE Customers SET Customersname = ?, Address = ?, CustomerContact = ?, City = ?, PostalCode = ?, Country = ? WHERE idCustomers = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                updateStmt.setString(1, txtName.getText());
                                updateStmt.setString(2, txtAddress.getText());
                                updateStmt.setString(3, txtContact.getText());
                                updateStmt.setString(4, txtCity.getText());
                                updateStmt.setString(5, txtPostalCode.getText());
                                updateStmt.setString(6, txtCountry.getText());
                                updateStmt.setInt(7, id);
                                int rowsAffected = updateStmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(this, "Record updated successfully.");
                                } else {
                                    JOptionPane.showMessageDialog(this, "Record not found.");
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Record not found.");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating record.");
        }
    }

    private void deleteRecord() {
        int id;
        try {
            id = Integer.parseInt(txtId.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.");
            return;
        }

        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM Customers WHERE idCustomers = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Record deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Record not found.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting record.");
        }
    }

    private void viewLastRecord() {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM Customers ORDER BY idCustomers DESC LIMIT 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    String record = String.format(
                        "ID: %d\nName: %s\nAddress: %s\nContact: %s\nCity: %s\nPostal Code: %s\nCountry: %s",
                        rs.getInt("idCustomers"),
                        rs.getString("Customersname"),
                        rs.getString("Address"),
                        rs.getString("CustomerContact"),
                        rs.getString("City"),
                        rs.getString("PostalCode"),
                        rs.getString("Country")
                    );
                    JOptionPane.showMessageDialog(this, record, "Last Record", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No records found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching record.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            assignment1 app = new assignment1();
            app.setVisible(true);
        });
    }
}
