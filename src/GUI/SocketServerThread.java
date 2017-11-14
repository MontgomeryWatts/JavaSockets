package GUI;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import static GUI.SocketServer.NEW_USER;
import static GUI.SocketServer.RETURN_USER;

public class SocketServerThread extends Thread{
    private SocketServer parentServer;
    private Socket clientSocket;
    private Scanner fromClient;
    private PrintStream toClient;

    SocketServerThread(SocketServer server, Socket socket) throws IOException{
        parentServer = server;
        clientSocket = socket;
        fromClient = new Scanner(clientSocket.getInputStream());
        toClient = new PrintStream(clientSocket.getOutputStream());
    }

    /**
     * Sends a message to the client
     * @param message The String to send to the client
     */
    void print(String message){
        toClient.println(message);
    }

    /**
     * Communicates with client while the client has not sent the shutdown message.
     */
    public void run() {
        System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());
        String clientInput, pass;
        String user = null;

        do{
            clientInput = fromClient.nextLine();
            switch(clientInput){
                case NEW_USER:
                    user = fromClient.nextLine();
                    pass = fromClient.nextLine();
                    if(parentServer.storePassword(user, pass))
                        print(SocketServer.SUCCESSFUL_LOGIN);
                    else
                        print(SocketServer.FAILED_LOGIN);
                    break;
                case RETURN_USER:
                    user = fromClient.nextLine();
                    pass = fromClient.nextLine();
                    if(parentServer.authenticatePassword(user, pass))
                        print(SocketServer.SUCCESSFUL_LOGIN);
                    else
                        print(SocketServer.FAILED_LOGIN);
                    break;
                default:
                    break;
            }
        } while((!clientInput.equals(SocketServer.SUCCESSFUL_LOGIN))
                && (!clientInput.equals(SocketServer.CLOSE_THREAD)));
        parentServer.printToAllClients(user + " has connected.");
        try{
            while(!clientInput.equals(SocketServer.CLOSE_THREAD)){
                clientInput = fromClient.nextLine();
                if(!clientInput.equals(SocketServer.CLOSE_THREAD))
                    parentServer.printToAllClients(user + ": " + clientInput);
                else {
                    print("Closing threads.");
                    clientSocket.close();

                    parentServer.removeThread(this);
                    parentServer.printToAllClients(user + " has disconnected.");
                }
            }
        } catch(IOException e){
            System.out.println("Client disconnected unexpectedly.");
        }
    }
}
