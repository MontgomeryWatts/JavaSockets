import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketClient {

    public static void main(String[] args) {
	String server = args[0];
	int portNumber = Integer.parseInt(args[1]);
	try{
        Socket client = new Socket(server, portNumber);
        Scanner input = new Scanner(System.in);

        System.out.println("Connected to " + client.getRemoteSocketAddress());
        OutputStream toServer = client.getOutputStream();
        DataOutputStream outToServer = new DataOutputStream(toServer);

        System.out.println("Enter a message to send to the server. Enter q to quit. ");
        String message = "";
        while(!message.equals("q")) {
            message = input.nextLine();

            outToServer.writeUTF(message);
            outToServer.flush();
        }
    }
    catch(IOException e){}
    }
}
