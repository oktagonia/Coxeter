import gui.*;

class Main {
    public static void main(String[] args) {
        try {
            new Server();
            new Client().run();
        } catch (Exception e) {
            System.out.println("REEEEEEE");
        }
    }
}
