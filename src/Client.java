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

    // Constant variables that do not change during runtime
    // The static keyword makes all instances of this class share the same memory for
    // this attribute, as it never changes
    private static          int ANNOUNCEMENT_WIDTH = 90;

    // Attributes for the class
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
        /*
        Class constructor. Initialises attributes and window
         */
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
        // Adds the action listener so the user can enter input
        // Disabled until the server connection is complete
        this.input.addActionListener(new EnterAction());
    }

    private void sendMessage(Packet data) {
        // Sends a message to the server
        System.out.println("Sending packet: " + data.toString());
        this.outputStream.println(data.toString());
    }

    private void print(String message) {
        // Prints text to the output window
        this.output.append(message + "\n");
    }

    public void connect() {

        /*
        Handles connecting to the server
         */
        postAnnouncement("Welcome to CIM Messenger 0.2b");
        print("Attempting to connect to server at " + this.host + ":" + this.port);
        print("Logging with as " + this.username);
    	
        try {
            // Connect to server!
            clientSocket = new Socket(this.host, this.port);
            this.closed = false;
            this.handshakeStage = 1;
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

    public void disconnect() {
        /*
        This method disconnects from the server and closes the socket connection
         */
        if (!this.closed) {
            try {
                this.closed = true;
                this.clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.closed = true;
        }
    }

    public void postAnnouncement(String announcement) {
        // prints a formatted message to the output window
        print(getAnnouncementString(announcement));
    }

    public String getAnnouncementString(String announcement) {
        /*
        This takes an input string and formats it with padding characters to create
        an announcement style for the chat window.
         */
        String padding = "", extraPadding = "";
        String announcementString;
        int len = announcement.length();
        if (len > (ANNOUNCEMENT_WIDTH - 4)) {
            /*
            This first checks that the announcement is not too long as to overflow the padding
             */
            announcement = "Announcement too long";
            len = announcement.length();
        }
        // Calculate the total amount of padding required
        // This will be the whole available width, minus the length of the announcement and 2 extra spaces for whitespace
        int totalPadding = ANNOUNCEMENT_WIDTH - (len + 2);
        int paddingLen = totalPadding / 2; // Calculates padding required either side of the message
        int extraPaddingLen = totalPadding % 2; // If an extra character is needed to account for odd lengths, it's added here
        for (int i = 0; i < paddingLen; i++) {
            /*
            Creates two strings for the padding to be added to either side of the message
             */
            padding = padding + "=";
        }
        for (int i = 0; i < extraPaddingLen; i++) {
            extraPadding = extraPadding + "=";
        }
        // Concates all items together into one string
        announcementString = padding + " " + announcement + " " + extraPadding + padding;
        return announcementString;
    }

    private void handshake(Packet handshakePacket) {
        /*
        This function is called every time the client receives data before it is properly connected.
        It passes the packet type into a switch statement to then invoke the required function for each
        stage of handshake.
         */
        String type = handshakePacket.type;
        switch (type) {
            case "001": sendAuthorisation();
                        break;
            case "003": authorisationError();
                        break;
            case "004": authorisationSuccess(handshakePacket);
                        break;
            case "006": authorised();
                        break;
        }
    }

    private void sendAuthorisation() {
        /*
        Sends a packet to the server containing authorisation information
         */
        Packet response = new Packet("002", this.token, this.username + "." + this.password);
        this.sendMessage(response);
        this.handshakeStage = 2;
    }

    private void authorisationError() {
        /*
        If the server returns an error packet, this method is called to close the server
         */
        print("Error authenticating! Please retry");
        postAnnouncement("Goodbye!");
        disconnect();
    }

    private void authorisationSuccess(Packet tokenPacket) {
        /*
        When the server responds that the authorisation was valid, this is called to respond confirmation to the server
         */
        print("Authenticated successfully!");
        this.token = tokenPacket.payload;
        Packet response = new Packet("005", this.token, "null");
        this.sendMessage(response);
        print("Awaiting confirmation that the connection has been accepted. . .");
    }

    private void authorised() {
        /*
        Changes the attributes internally to represent the client being connected
        and enables the keyboard input
         */
        this.handshakeStage = 0;
        enableInput();
        postAnnouncement("Client accepted, ready!");
    }

    class EnterAction implements ActionListener {

        /*
        Manages getting input from the text bot
         */

        public void actionPerformed(ActionEvent e) {
            Client.this.sendMessage(new Packet("100", Client.this.token, Client.this.input.getText()));
            Client.this.output.setCaretPosition(output.getDocument().getLength());
            Client.this.input.setText("");
        }
    }

    class ReceiveThread extends Thread implements Runnable {

        /*
        Separate thread to handle receiving data from the server
         */

        public void run() {
            try {
                while (!Client.this.closed) {
                    // Buffered reader to keep accepting data until a new line character is reached
                    BufferedReader socketIn = new BufferedReader(new InputStreamReader(Client.this.clientSocket.getInputStream()));
                    String data;
                    Packet packet;
                    while ((data = socketIn.readLine()) != null) {
                        packet = new Packet(data);
                        // Log to console that we have recieved data
                        System.out.println("Received new line of data: " + packet.toString());
                        // If the Client has not finished the handshake, pass it to the handshake method
                        if (Client.this.handshakeStage != 0) {
                            handshake(packet);
                        } else if (packet.type.equals("100")) {
                            // Otherwise, print the message
                            print(packet.payload);
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}
