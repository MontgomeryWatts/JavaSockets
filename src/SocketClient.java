import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketClient {

    public static void main(String[] args) {
	    try{
            Socket client = new Socket(args[0], Integer.parseInt(args[1]));

            System.out.println("Connected to " + client.getRemoteSocketAddress());
            ReceiveMessageThread rmt = new ReceiveMessageThread(client);
            SendMessageThread smt = new SendMessageThread(client);

            rmt.start();
            smt.start();
            smt.join();
            rmt.turnOff();
        }
        catch(IOException e){}
        catch(InterruptedException ie){}
    }

}
