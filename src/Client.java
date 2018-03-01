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

    private static          int ANNOUNCEMENT_WIDTH = 90;

	private boolean         closed          = true;
    private String          host;
    private int             port;
    private String          username;
    private String          password;
    private String          token           = "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN";
    private ReceiveThread   receiveLoop;
    private Socket          clientSocket;
    private JTextArea       output;
    private JTextField      input;
    private JScrollPane     scrollOutput;
    private PrintWriter     outputStream;
    private int             handshakeStage;

    public Client(String host, int port, String user, String password) {

        this.host = host;
        this.port = port;
        this.username = user;
        this.password = password;

        setLayout(new BorderLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.input = new JTextField(30);
        this.input.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.output = new JTextArea();
        this.output.setFont(new Font("monospaced", Font.PLAIN, 12));
        this.output.setLineWrap(true);
        this.output.setEditable(false);
        this.output.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollOutput = new JScrollPane(output);

        add(scrollOutput, BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

    }

    private void enableInput() {
        this.input.addActionListener(new EnterAction());
    }

    private void sendMessage(Packet data) {
        this.outputStream.println(data.toString());
    }

    private void print(String message) {
        this.output.append(message + "\n");
    }

    public void connect() {

        postAnnouncement("Welcome to CIM Messenger 0.2b");
        print("Attempting to connect to server at " + this.host + ":" + this.port);
        print("Logging with as " + this.username);
    	
        try {
            // Connect to server!
            clientSocket = new Socket(this.host, this.port);
            this.closed = false;
            print("Connected to server. . .\nAuthenticating. . .");

            // Start receiving data
            receiveLoop = new ReceiveThread();
            receiveLoop.start();
            outputStream = new PrintWriter(this.clientSocket.getOutputStream(), true);

        } catch (java.net.ConnectException ex) {
        	System.out.println("Error connecting");

        	print("Error connecting to server! Please restart the program");
        	postAnnouncement("Goodbye!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void postAnnouncement(String announcement) {
        print(getAnnouncementString(announcement));
    }

    public String getAnnouncementString(String announcement) {
        String padding = "", extraPadding = "";
        String announcementString;
        int len = announcement.length();
        if (len > (ANNOUNCEMENT_WIDTH - 4)) {
            // TODO: Deal with this better
            announcement = "Announcement too long";
            len = announcement.length();
        }
        int totalPadding = ANNOUNCEMENT_WIDTH - (len + 2);
        int paddingLen = totalPadding / 2;
        int extraPaddingLen = totalPadding % 2;
        for (int i = 0; i < paddingLen; i++) {
            padding = padding + "=";
        }
        for (int i = 0; i < extraPaddingLen; i++) {
            extraPadding = extraPadding + "=";
        }
        announcementString = padding + " " + announcement + " " + extraPadding + padding;
        return announcementString;
    }

    private void handshake(Packet handshakePacket) {
        if (handshakePacket.type == "001") {
            print("Received ack");
            Packet response = new Packet("002", this.token, this.username + "." + this.password);
            this.sendMessage(response);
            this.handshakeStage = 2;
        }
    }

    class EnterAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //Client.this.sendMessage(Client.this.input.getText());
            Client.this.print(Client.this.input.getText()); // just print for now
            Client.this.output.setCaretPosition(output.getDocument().getLength());
            Client.this.input.setText("");
        }
    }

    class ReceiveThread extends Thread implements Runnable {

        public void run() {
            try {
                while (!Client.this.closed) {
                    BufferedReader socketIn = new BufferedReader(new InputStreamReader(Client.this.clientSocket.getInputStream()));
                    String data;
                    Packet packet;
                    while ((data = socketIn.readLine()) != null) {
                        packet = new Packet(data);
                        if (Client.this.handshakeStage != 0) {
                            handshake(packet);
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}
