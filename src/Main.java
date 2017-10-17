
public class Main {
    public static void main(String[] args) {

        new getIpAddress();
        Server server = new Server(9000);
        server.setDefaultCloseOperation(3);
        new Thread(server).start();

        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
}
