import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {

    // Setting attributes of class
    private boolean         closed          = true;
    private String          host            = "localhost";
    private int             port            = 46400;
    private ReceiveThread   receiveLoop;
    private Socket          clientSocket;
    private JTextArea       output;
    private JTextField      input;
    private JScrollPane     scrollOutput;
    private PrintWriter     outputStream;

    public Client() {
        // Class Constructor
        // Initialises Window and starts connection

        setTitle("CIM Messenger");
        ImageIcon img = new ImageIcon("resources/ico.png");
        setIconImage(img.getImage());
        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        input = new JTextField(30);
        input.setBorder(new EmptyBorder(10, 10, 10, 10));
        input.addActionListener(new EnterAction()); // Adds event listener for enter action
        output = new JTextArea();
        output.setLineWrap(true);
        output.setEditable(false);
        output.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollOutput = new JScrollPane(output);

        add(scrollOutput, BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

        setVisible(true);
        this.connect();

    }

    private void connect() {
        // Connects to server and starts separate thread to receive data

        try {
            clientSocket = new Socket(this.host, this.port);
            this.closed = false;
            receiveLoop = new ReceiveThread();  receiveLoop.start();
            outputStream = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    class EnterAction implements ActionListener {

        /*
        Action Listener class to wait for key press and get data from the text input
         */

        public void actionPerformed(ActionEvent e) {
            Client.this.outputStream.println(Client.this.input.getText());
            Client.this.output.setCaretPosition(output.getDocument().getLength());
            Client.this.input.setText("");
        }

    }

    class ReceiveThread extends Thread implements Runnable {

        /*
        Separate class to handle repeatedly checking the connection for incoming data
         */

        public void run() {
            try {
                while (!Client.this.closed) {
                    // Buffered reader keeps taking data until a new line character
                    BufferedReader socketIn = new BufferedReader(new InputStreamReader(Client.this.clientSocket.getInputStream()));
                    String msg;
                    while ((msg = socketIn.readLine()) != null) {
                        System.out.println(msg);
                        Client.this.output.append(msg + "\n");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}

