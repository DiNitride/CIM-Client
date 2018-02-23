public class Main {

    public static void main(String args[]) {
        //new Window();

        Packet p = new Packet("004BSFzX0OjP5dhGQxj5IDatlhH7gMbM5nsEmaj4Bki2018-02-23T12:41:18.273yo mom gaty");
        // Packet p = new Packet("004", "L348zaqoxaaaeARmjuNNSSxvrmPwRsiUZpWC26Ry", "yo mom gay");

        System.out.println(p.type);
        System.out.println(p.token);
        System.out.println(p.timestamp);
        System.out.println(p.payload);

        System.out.println(p.toString());

    }

}
