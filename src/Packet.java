import java.time.LocalDateTime;

public class Packet {

    String type, timestamp, token, payload;

    /*
    This class has two constructors, one is for building instances of message inside the client to send, the
    other is for converting raw packets into the class objects
     */
    public Packet(String type, String token, String payload) {
        /*
        First constructor, takes in elements of the method individually
         */
        this.type = type;
        this.token = token;
        this.payload = payload;
        this.timestamp = getIso();

    }

    public Packet(String raw) {
        /*
        Second constructor, takes in a raw packet and parses the elements out of it.
         */
        this.type = raw.substring(0, 3);
        this.token = raw.substring(3, 43);
        this.timestamp = raw.substring(43, 66);
        this.payload = raw.substring(66);

    }

    private String getIso() {
        // Returns the current time in ISO format
        String time = LocalDateTime.now().toString();
        if (time.length() > 23) {
            return time.substring(0, 23);
        } else {
            return time;
        }
    }

    public String toString() {
        // Converts the object into a raw string form to be sent.
        return this.type + this.token + this.timestamp + this.payload;

    }

}
