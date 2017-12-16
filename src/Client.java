import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {

    private boolean         closed      = true;
    private String          host        = "localhost";
    private int             port        = 46400;
    // private SendThread      sendLoop;
    private ReceiveThread   receiveLoop;
    private Socket          clientSocket;
    public  JTextArea       output;
    public  JTextField      input;
    public PrintWriter      outputStream;

    public Client() {
        setTitle("CIM");
        setSize(500, 500);
        output = new JTextArea();
        input = new JTextField(30);
        input.addActionListener(new EnterAction());
        add(output, BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        this.connect();
    }

    private void connect() {
        try {
            clientSocket = new Socket(this.host, this.port);
            this.closed = false;
            receiveLoop = new ReceiveThread();  receiveLoop.start();
            // sendLoop    = new SendThread();     sendLoop.start();
            outputStream = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class EnterAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            // Implement sending data here
            // Client.this.output.append(Client.this.input.getText());
            Client.this.outputStream.println(Client.this.input.getText());
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
                        Client.this.output.append(msg + "\n");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

//    class SendThread extends Thread implements Runnable {
//
//        public void run() {
//            try {
//                PrintWriter socketOut = new PrintWriter(Client.this.clientSocket.getOutputStream(), true);
//                BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
//                while (!Client.this.closed) {
//                    socketOut.println(sysIn.readLine());
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

}

