import java.time.LocalDateTime;

public class Packet {

    String type, timestamp, token, payload;

    public Packet(String type, String token, String payload) {

        this.type = type;
        this.token = token;
        this.payload = payload;
        this.timestamp = getIso();

    }

    public Packet(String raw) {

        this.type = raw.substring(0, 3);
        this.token = raw.substring(3, 43);
        this.timestamp = raw.substring(43, 66);
        this.payload = raw.substring(66);

    }

    private String getIso() {
        return LocalDateTime.now().toString();
    }

    public String toString() {

        return this.type + this.token + this.timestamp + this.payload;

    }

}
