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

        JLabel title;
        JButton login_button;
        JTextField host_field, port_field, username_field;
        JPasswordField password_field;
        JPanel centerPanel;

        public Login() {

            setLayout(new BorderLayout());
            centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setSize(new Dimension(100, 100));

            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.BOTH;
            c.gridx = 0;

            title = new JLabel("CIM Messenger", SwingConstants.CENTER);
            title.setFont(new Font("", Font.PLAIN, 40));
            c.insets = new Insets(10, 10, 10 , 10);
            c.gridy = 0;
            centerPanel.add(title, c);

            c.insets = new Insets(5, 0, 0 , 0);

            host_field = new JTextField();
            host_field.setPreferredSize(FIELD_SIZE);
            c.gridy = 1;
            centerPanel.add(host_field, c);

            port_field = new JTextField();
            port_field.setPreferredSize(FIELD_SIZE);
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

            login_button = new JButton("Login");
            login_button.setPreferredSize(new Dimension(250, 45));
            c.gridy = 5;
            centerPanel.add(login_button, c);

            add(centerPanel, BorderLayout.CENTER);

        }

        class LoginListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {

            }

        }

    }
}
