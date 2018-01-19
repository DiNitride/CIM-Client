import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame {

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

        setTitle("CIM Messenger");
        ImageIcon img = new ImageIcon("resources/ico.png");
        setIconImage(img.getImage());
        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        input = new JTextField(30);
        input.setBorder(new EmptyBorder(10, 10, 10, 10));
        input.addActionListener(new EnterAction());
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
    	
    	addMessage("Attempting to connect to server at '" + this.host + "' . . .");
    	
        try {
            clientSocket = new Socket(this.host, this.port);
            this.closed = false;
            receiveLoop = new ReceiveThread();  receiveLoop.start();
            outputStream = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (java.net.ConnectException ex) {
        	System.out.println("Error connecting");
        	addMessage("Error connecting to server! Please retry");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    
    public void addMessage(String message) {
    	Client.this.output.append(message + "\n");
    }

    class EnterAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Client.this.outputStream.println(Client.this.input.getText());
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
                        Client.this.addMessage(msg);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}

