import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Window extends JFrame implements WindowListener {

    // Attribute to hold client class to be switched too later
    Client client;

    public Window() {
        // Constructor to initialise the main window and create window frame
        super();
        addWindowListener(this);
        setTitle("CIM Messenger");

        // Set window icon
        ImageIcon img = new ImageIcon("resources/ico.png");
        setIconImage(img.getImage());

        // Set window size to 50% of fullscreen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        setSize(width / 2, height / 2);
        setMinimumSize(new Dimension(500, 500));
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create new instance of login class and add it to Window
        Login login = new Login();
        add(login);

        // Show window to user
        setLocationRelativeTo(null);
        setVisible(true);

    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
        // Function required to implement WindowListener
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        // When the user closes the window, disconnect from the server properly
        System.out.println("DISCONNECTING");
        if (this.client != null) {
            this.client.disconnect();
        }

    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        // Function required to implement WindowListener
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {
        // Function required to implement WindowListener
    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {
        // Function required to implement WindowListener
    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {
        // Function required to implement WindowListener
    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {
        // Function required to implement WindowListener
    }


    class Login extends JPanel {

        /*
        Class to create the login window for the user
         */

        // Attributes
        final Dimension FIELD_SIZE = new Dimension(300, 30);

        private String password, username, host;
        private int port;

        JLabel title, host_label, port_label, user_label, password_label;
        JButton login_button;
        JTextField host_field, port_field, username_field;
        JPasswordField password_field;
        JPanel centerPanel;

        public Login() {

            /*
            Constructor to initialise
             */

            setLayout(new BorderLayout());
            centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setSize(new Dimension(100, 100));

            GridBagConstraints c = new GridBagConstraints();

            // Grid Bag Coordinate Design
            //  +-------------------------------------+
            //  |                                     |
            //  |                                     |
            //  |            0            1           |
            //  |                                     |
            //  |         +---------------------+     |
            //  |    0    |        Title        |     |
            //  |         +---------------------+     |
            //  |                                     |
            //  |    1    Address  +------------+     |
            //  |                  +------------+     |
            //  |                                     |
            //  |    2    Port     +------------+     |
            //  |                  +------------+     |
            //  |                                     |
            //  |    3    Username +------------+     |
            //  |                  +------------+     |
            //  |                                     |
            //  |    4    Password +------------+     |
            //  |                  +------------+     |
            //  |                                     |
            //  |    5    +---------------------+     |
            //  |         |        Login        |     |
            //  |         +---------------------+     |
            //  |                                     |
            //  +-------------------------------------+

            // Text Entry

            c.fill = GridBagConstraints.BOTH;
            c.gridx = 1;

            c.ipadx = 10;

            c.insets = new Insets(5, 0, 0 , 0);

            host_field = new JTextField();
            host_field.setPreferredSize(FIELD_SIZE);
            host_field.setText("localhost");
            c.gridy = 1;
            centerPanel.add(host_field, c);

            port_field = new JTextField();
            port_field.setPreferredSize(FIELD_SIZE);
            port_field.setText("46400");
            c.gridy = 2;
            centerPanel.add(port_field, c);

            username_field = new JTextField();
            username_field.setPreferredSize(FIELD_SIZE);
            username_field.setText("DiniTest");
            c.gridy = 3;
            centerPanel.add(username_field, c);

            password_field = new JPasswordField();
            password_field.setPreferredSize(FIELD_SIZE);
            password_field.setText("password");
            c.gridy = 4;
            centerPanel.add(password_field, c);

            // Labels

            c.gridx = 0;

            host_label = new JLabel("Host Address");
            c.gridy = 1;
            centerPanel.add(host_label, c);

            port_label = new JLabel("Port");
            c.gridy = 2;
            centerPanel.add(port_label, c);

            user_label = new JLabel("Username");
            c.gridy = 3;
            centerPanel.add(user_label, c);

            password_label = new JLabel("Password");
            c.gridy = 4;
            centerPanel.add(password_label, c);

            // Title and Login Button

            c.gridwidth = 2;
            c.gridx = 0;

            login_button = new JButton("Login");
            login_button.setPreferredSize(new Dimension(250, 45));
            login_button.addActionListener(new LoginListener());
            c.gridy = 5;
            centerPanel.add(login_button, c);

            title = new JLabel("CIM Messenger", SwingConstants.CENTER);
            title.setFont(new Font("", Font.PLAIN, 40));
            c.insets = new Insets(10, 10, 10 , 10);
            c.gridy = 0;
            centerPanel.add(title, c);

            add(centerPanel, BorderLayout.CENTER);

        }

        class LoginListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {

                // Get data from text input and store in variables
                host = Login.this.host_field.getText();
                port = Integer.parseInt(Login.this.port_field.getText());
                username = Login.this.username_field.getText();
                password = new String(Login.this.password_field.getPassword());

                // If username or password are blank ignore
                if (username.equals("") || password.equals("")) {
                    return;
                }

                // Hash the password for obsurity!
                // This is done pre-sending to the server, so the plaintext password is never sent
                password = hash(password);

                // Log to console
                System.out.println("Logging in!");
                System.out.println("Host: " + host + " on port: " + port);
                System.out.println("Username: " + username + " with password: " + password);

                // Create new Client instance and set Window.client attribute to the instance
                Window.this.client = new Client(host, port, username, password);

                // Switch the visible window to the client and connect
                Window.this.setContentPane(Window.this.client);
                Window.this.revalidate();
                Window.this.client.connect();

            }

            private String hash(String input) {
                /*
                Method to hash a plaintext password into SHA-356 hash
                 */
                try {
                    // Create a message digest object
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    // Turn hash string into array of bytes
                    byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                    // Create a string buffer to enter characters into
                    StringBuffer hexString = new StringBuffer();
                    // For every hashed byte in the array
                    // Convert to hexadecimal character
                    for (int i = 0; i < encodedhash.length; i++) {
                        String hex = Integer.toHexString(0xff & encodedhash[i]);
                        if(hex.length() == 1) hexString.append('0');
                        hexString.append(hex);
                    }
                    // Finally convert to string and return
                    return hexString.toString();

                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }

                return "";
            }

        }

    }
}
