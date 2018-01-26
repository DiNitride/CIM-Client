import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Client extends JPanel {

    private int             ANNOUNCEMENT_WIDTH = 45;

	private boolean         closed          = true;
    private String          host            = "localhost";
    private int             port            = 46400;
    private String          username;
    private String          password;
    private ReceiveThread   receiveLoop;
    private Socket          clientSocket;
    private JTextArea       output;
    private JTextField      input;
    private JScrollPane     scrollOutput;
    private PrintWriter     outputStream;

    public Client() {

        setLayout(new BorderLayout());
        GridBagConstraints c = new GridBagConstraints();

        input = new JTextField(30);
        input.setBorder(new EmptyBorder(10, 10, 10, 10));
        input.addActionListener(new EnterAction());

        output = new JTextArea();
        output.setPreferredSize(new Dimension(50,50));
        output.setLineWrap(true);
        output.setEditable(false);
        output.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollOutput = new JScrollPane(output);

        add(scrollOutput, BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

    }

    public Client(String host, int port, String user, String password) {
        this();
        this.host = host;
        this.port = port;
        this.username = user;
        this.password = password;
    }

    public void sendMessage(String message) {
        outputStream.println(message);
    }

    public void print(String message) {
        this.output.append(message + "\n");
    }

    public void connect() {

        print("=========== CIM Messenger ===========");
        print("Welcome to CIM Messenger 0.2b");
        print("Attempting to connect to server at " + this.host + ":" + this.port);
        print("Logging with as " + this.username);
    	
        try {
            clientSocket = new Socket(this.host, this.port);
            this.closed = false;
            receiveLoop = new ReceiveThread();  receiveLoop.start();
            outputStream = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (java.net.ConnectException ex) {
        	System.out.println("Error connecting");
        	print("Error connecting to server! Please restart the program");
        	print("=========== Goodbye ===========");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public String getAnnouncementString(String announcement) {
        String announcementString;

        return announcementString;
    }

    class EnterAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Client.this.sendMessage(Client.this.input.getText());
            Client.this.output.setCaretPosition(output.getDocument().getLength());
            Client.this.input.setText("");
        }
    }

    class ReceiveThread extends Thread implements Runnable {

        public void run() {
            try {
                while (!Client.this.closed) {
                    BufferedReader socketIn = new BufferedReader(new InputStreamReader(Client.this.clientSocket.getInputStream()));
                    String msg;
                    while ((msg = socketIn.readLine()) != null) {
                        System.out.println(msg);
                        Client.this.print(msg);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}

