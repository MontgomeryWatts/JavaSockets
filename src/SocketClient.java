import java.io.*;
import java.net.*;

public class SocketClient {

    public static void main(String[] args) {
	    try{
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));


            System.out.println("Connected to " + socket.getRemoteSocketAddress());
            ReceiveMessageThread rmt = new ReceiveMessageThread(socket);
            SendMessageThread smt = new SendMessageThread(socket);

            rmt.start();
            smt.start();
            smt.join();
            rmt.turnOff();
        }
        catch(IOException e){}
        catch(InterruptedException ie){}
    }

}
