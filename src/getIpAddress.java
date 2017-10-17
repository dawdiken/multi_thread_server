import java.net.InetAddress;
import java.net.UnknownHostException;

public class getIpAddress {



    public getIpAddress(){

        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

        } catch (UnknownHostException e) {

            System.out.printf("not working");
        }
    }
}