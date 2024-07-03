package MovieTicketBooking;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class BookingTicket extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel showsPanel;
    private JPanel bookingFormPanel;
    private JTable bookingsTable;

    public BookingTicket() {
        setTitle("Movie Ticket Booking");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize bookingsTable
        bookingsTable = new JTable();

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

        JLabel homeLabel = new JLabel("Home");
        JLabel showsLabel = new JLabel("Shows");
        JLabel bookTicketLabel = new JLabel("Book Tickets");
        JLabel bookingsLabel = new JLabel("Booking");
        JLabel helpLabel = new JLabel("Help");

        navPanel.add(homeLabel);
        navPanel.add(showsLabel);
        navPanel.add(bookTicketLabel);
        navPanel.add(bookingsLabel);
        navPanel.add(helpLabel);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Example image paths
        String[] imagePaths = {
                "C:\\Users\\Pictures\\kaliki.jpeg",
                "C:\\Users\\Pictures\\pushpa2.jpg",
                "C:\\Users\\Pictures\\devara.jpg",
                "C:\\Users\\Pictures\\vikram.jpg"
        };

        for (String path : imagePaths) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                Image scaledImg = img.getScaledInstance(600, 400, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                imageLabel.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        cardLayout.show(mainPanel, "Shows");
                    }
                });
                homePanel.add(imageLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        showsPanel = new JPanel();
        showsPanel.setLayout(new BoxLayout(showsPanel, BoxLayout.Y_AXIS));

        bookingFormPanel = new JPanel();
        bookingFormPanel.setLayout(new BorderLayout());

        mainPanel.add(new JScrollPane(homePanel), "Home");
        mainPanel.add(new JScrollPane(showsPanel), "Shows");
        mainPanel.add(bookingFormPanel, "Book Ticket");
        mainPanel.add(new JScrollPane(bookingsTable), "Bookings");

        homeLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "Home");
            }
        });

        showsLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showShowsPanel();
                cardLayout.show(mainPanel, "Shows");
            }
        });

        bookTicketLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "Book Ticket");
            }
        });

        bookingsLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                displayBookings();
                cardLayout.show(mainPanel, "Bookings");
            }
        });

        helpLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Implement help and support
            }
        });

        add(navPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void showShowsPanel() {
        showsPanel.removeAll();
        showsPanel.setLayout(new BoxLayout(showsPanel, BoxLayout.Y_AXIS));

        try {
            String url = "jdbc:oracle:thin:@localhost:1521:XE";
            String username = "movies";
            String password = "movie";
            Connection conn = DriverManager.getConnection(url, username, password);

            String sql = "SELECT S.SID, S.TITLE, S.PRICE, S.DESCRIPTION, S.IMAGE_ID, T.TIMING FROM SHOWS S INNER JOIN SHOWTIME T ON S.SID=T.SID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int sid = rs.getInt("SID");
                String title = rs.getString("TITLE");
                double price = rs.getDouble("PRICE");
                String description = rs.getString("DESCRIPTION");
                String imagePath = rs.getString("IMAGE_ID");
                String timing = rs.getString("TIMING");

                JPanel showPanel = new JPanel();
                showPanel.setLayout(new BorderLayout());
                showPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        BufferedImage img = ImageIO.read(new File(imagePath));
                        if (img != null) {
                            Image scaledImg = img.getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                            JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                            showPanel.add(imageLabel, BorderLayout.WEST);
                        } else {
                            JLabel imageLabel = new JLabel("No Image Available");
                            showPanel.add(imageLabel, BorderLayout.WEST);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        JLabel imageLabel = new JLabel("Image Load Error");
                        showPanel.add(imageLabel, BorderLayout.WEST);
                    }
                } else {
                    JLabel imageLabel = new JLabel("No Image Available");
                    showPanel.add(imageLabel, BorderLayout.WEST);
                }

                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BorderLayout());
                textPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setText("Title: " + (title != null ? title : "N/A") +
                        "\nPrice: $" + price +
                        "\nDescription: " + (description != null ? description : "N/A") +
                        "\nTiming: " + (timing != null ? timing : "N/A"));
                textArea.setBackground(showPanel.getBackground());

                textPanel.add(textArea, BorderLayout.CENTER);

                JButton bookButton = new JButton("Book");
                bookButton.addActionListener(e -> showBookingForm(title)); // Pass title to booking form

                textPanel.add(bookButton, BorderLayout.EAST);

                showPanel.add(textPanel, BorderLayout.CENTER);

                showsPanel.add(showPanel);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching shows: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        showsPanel.revalidate();
        showsPanel.repaint();
    }

    private void showBookingForm(String movieTitle) {
        bookingFormPanel.removeAll();
        bookingFormPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Book Tickets for " + movieTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bookingFormPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Name:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx++;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        formPanel.add(emailLabel, gbc);

        gbc.gridx++;
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel phoneLabel = new JLabel("Phone:");
        formPanel.add(phoneLabel, gbc);

        gbc.gridx++;
        JTextField phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel ticketsLabel = new JLabel("Number of Tickets:");
        formPanel.add(ticketsLabel, gbc);

        gbc.gridx++;
        JSpinner ticketsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        formPanel.add(ticketsSpinner, gbc);

        bookingFormPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Book Tickets");
        bookButton.addActionListener(e -> {
            int numTickets = (int) ticketsSpinner.getValue();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(BookingTicket.this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    String url = "jdbc:oracle:thin:@localhost:1521:XE";
                    String username = "movies";
                    String password = "movie";
                    Connection conn = DriverManager.getConnection(url, username, password);

                    String insertSql = "INSERT INTO bookings (name, email, phone, num_tickets, movie_title) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(insertSql);
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setString(3, phone);
                    stmt.setInt(4, numTickets);
                    stmt.setString(5, movieTitle);

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(BookingTicket.this, "Booking Successful for " + numTickets + " tickets.");
                    }

                    stmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(BookingTicket.this, "Error booking tickets: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(bookButton);

        bookingFormPanel.add(buttonPanel, BorderLayout.SOUTH);

        cardLayout.show(mainPanel, "Book Ticket");
    }

    private void displayBookings() {
        try {
            String url = "jdbc:oracle:thin:@localhost:1521:XE";
            String username = "movies";
            String password = "movie";
            Connection conn = DriverManager.getConnection(url, username, password);

            String sql = "SELECT * FROM bookings";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Create a table model for bookings
            DefaultTableModel tableModel = new DefaultTableModel();
            ResultSetMetaData metaData = rs.getMetaData();

            // Get column names
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Add rows to the table model
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }

            // Set the table model to the bookings table
            bookingsTable.setModel(tableModel);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            BookingTicket window = new BookingTicket();
            window.setVisible(true);
        });
    }
}
