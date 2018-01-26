import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window extends JFrame {

    Client client;

    public Window() {

        super();
        setTitle("CIM Messenger");
        ImageIcon img = new ImageIcon("resources/ico.png");
        setIconImage(img.getImage());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        setSize(width / 2, height / 2);
        setMinimumSize(new Dimension(500, 500));
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Login login = new Login();
        add(login);

        setLocationRelativeTo(null);
        setVisible(true);

    }

    class Login extends JPanel {

        final Dimension FIELD_SIZE = new Dimension(300, 30);

        private String password, username, host;
        private int port;

        JLabel title, host_label, port_label, user_label, password_label;
        JButton login_button;
        JTextField host_field, port_field, username_field;
        JPasswordField password_field;
        JPanel centerPanel;

        public Login() {

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
            c.gridy = 3;
            centerPanel.add(username_field, c);

            password_field = new JPasswordField();
            password_field.setPreferredSize(FIELD_SIZE);
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

                // Add checking for null here

                host = Login.this.host_field.getText();
                port = Integer.parseInt(Login.this.port_field.getText());
                username = Login.this.username_field.getText();
                password = new String(Login.this.password_field.getPassword());

                System.out.println("Logging in!");
                System.out.println("Host: " + host + " on port: " + port);
                System.out.println("Username: " + username + " with password: " + password);

                Window.this.client = new Client(host, port, username, password);

                Window.this.setContentPane(Window.this.client);
                Window.this.revalidate();

                Window.this.client.connect();

            }

        }

    }
}
