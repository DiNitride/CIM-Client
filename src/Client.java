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

    public void sendMessage(String message) {
        outputStream.println(message);
    }

    public void addMessageToView(String message) {
        this.output.append(message + "\n");
    }

    public void connect() {

        addMessageToView("Attempting to connect to server at '" + this.host + "' . . .");
    	
        try {
            clientSocket = new Socket(this.host, this.port);
            this.closed = false;
            receiveLoop = new ReceiveThread();  receiveLoop.start();
            outputStream = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (java.net.ConnectException ex) {
        	System.out.println("Error connecting");
        	addMessageToView("Error connecting to server! Please retry");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

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
                        Client.this.addMessageToView(msg);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}

