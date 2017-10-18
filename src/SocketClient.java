import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketClient {

    public static void main(String[] args) {
	String hostName = args[0];
	int portNumber = Integer.parseInt(args[1]);
	    try{
            Socket client = new Socket(hostName, portNumber);
            Scanner input = new Scanner(System.in);

            System.out.println("Connected to " + client.getRemoteSocketAddress());
            ReceiveMessageThread rmt = new ReceiveMessageThread(client);
            SendMessageThread smt = new SendMessageThread(client);
            rmt.start();
            smt.start();
        }
        catch(IOException e){}
    }
}
