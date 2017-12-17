package GUI.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import static GUI.CommunicationProtocol.*;


public class SocketServerThread extends Thread{
    private SocketServer parentServer;
    private Socket clientSocket;
    private Scanner fromClient;
    private PrintStream toClient;
    private String username;

    /**
     * Creates a SocketServerThread which communicates with clients
     * @param server The SocketServer this thread belongs to
     * @param socket The Socket of the incoming client connection
     * @throws IOException If the SocketServerThread is unable to get I/OStreams from the Socket
     */
    SocketServerThread(SocketServer server, Socket socket) throws IOException{
        parentServer = server;
        clientSocket = socket;
        fromClient = new Scanner(clientSocket.getInputStream(), "utf-8");
        toClient = new PrintStream(clientSocket.getOutputStream());
    }

    /**
     * Returns the username of the client associated with this thread
     * @return String of the username
     */
    String getUsername(){
        return username;
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

        //Try to register/login the user
        do{
            clientInput = fromClient.nextLine();
            if(clientInput.equals(NEW_USER)) {
                username = fromClient.nextLine();
                pass = fromClient.nextLine();
                if (parentServer.registerNewUser(username, pass))
                    print(SUCCESSFUL_LOGIN);
                else
                    print(FAILED_LOGIN);
            }
            else if (clientInput.equals(RETURN_USER)){
                username = fromClient.nextLine();
                pass = fromClient.nextLine();
                if((parentServer.authenticatePassword(username, pass)) && (!parentServer.userAlreadyOnline(username)))
                    print(SUCCESSFUL_LOGIN);
                else
                    print(FAILED_LOGIN);
            }
        } while((!clientInput.equals(SUCCESSFUL_LOGIN))
                && (!clientInput.equals(CLOSE_THREAD)));

        //If the user did not close the window
        if(!clientInput.equals(CLOSE_THREAD)) {
            parentServer.getAllUsers(this);
            parentServer.addThread(this);
            System.out.println(clientSocket.getRemoteSocketAddress() + " has logged in as " + username);
            parentServer.printToAllClients(username + " has connected.");
        }

        try{
            while(!clientInput.equals(CLOSE_THREAD)){
                clientInput = fromClient.nextLine();
                if(clientInput.equals(CLOSE_THREAD)) {
                    clientSocket.close();
                    parentServer.removeThread(this);
                    parentServer.printToAllClients(username + " has disconnected.");
                } else
                    parentServer.printToAllClients(username + ": " + clientInput);
            }
        } catch(IOException e){
            parentServer.removeThread(this);
        }

        System.out.println(clientSocket.getRemoteSocketAddress() + " has logged off as " + username);
    }
}
