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
        DataOutputStream outToServer = new DataOutputStream(client.getOutputStream());
        DataInputStream fromServer = new DataInputStream(client.getInputStream());

        System.out.println("Enter a message to send to the server. Enter q to quit. ");
        String message = "";
        String response;
        while(!message.equals("q")) {

            //Read in message and send to server
            message = input.nextLine();
            outToServer.writeUTF(message);
            outToServer.flush();

            //Added another check to message, otherwise would get stuck in try block
            if(!message.equals("q")) {
                //Read response from server
                try{
                    response = fromServer.readUTF();
                    System.out.println(response);
                } catch(IOException e){}
            }
        }
    }
    catch(IOException e){}
    }
}
