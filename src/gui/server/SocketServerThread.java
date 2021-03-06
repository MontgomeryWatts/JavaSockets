package gui.server;

import gui.CommunicationRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static gui.CommunicationRequest.*;
import static gui.CommunicationRequest.CommType.*;


/**
 * Server-side thread that handles a connection to a client
 */

public class SocketServerThread implements Runnable{
    private SocketServer parentServer;
    private Socket clientSocket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private String username;

    /**
     * Creates a SocketServerThread which communicates with clients
     * @param server The SocketServer this thread belongs to
     * @param socket The Socket of the incoming client connection
     * @throws IOException If the SocketServerThread is unable to get I/O Streams from the Socket
     */
    SocketServerThread(SocketServer server, Socket socket) throws IOException{
        parentServer = server;
        clientSocket = socket;
        toClient = new ObjectOutputStream(socket.getOutputStream());
        toClient.flush();
        fromClient = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Communicates with client while the client has not sent the shutdown message.
     */
    public void run() {
        System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());
        CommunicationRequest<?> clientInput = null;
        boolean loginSuccess = false;
        //Try to register/login the user
        do{
            try {
                clientInput = (CommunicationRequest<?>)fromClient.readObject();
                String info = (String) clientInput.getData();
                username =  clientInput.getRelevantUser();

                loginSuccess = (clientInput.getType() == NEW_USER) ? parentServer.registerNewUser(username, info)
                        : (parentServer.authenticatePassword(username, info) && (!parentServer.userAlreadyOnline(username)));

                if (loginSuccess)
                    sendRequest(toClient, new CommunicationRequest<>(SUCCESSFUL_LOGIN, null));
                else
                    sendRequest(toClient, new CommunicationRequest<>(FAILED_LOGIN, null));

            } catch(IOException ioe){
                System.err.println("IOException while attempting to login a user.");
            } catch(ClassNotFoundException cnfe){
                //This should never happen
                System.err.println("ClassNotFoundException while attempting to login a user.");
            }
        } while((!loginSuccess) && (clientInput.getType() != CLOSE_THREAD));

        //If the user successfully logged in
        if(clientInput.getType() != CLOSE_THREAD) {
            parentServer.addThread(username, toClient);
            System.out.println(clientSocket.getRemoteSocketAddress() + " has logged in as " + username);
        }

        try{
            while(clientInput.getType() != CLOSE_THREAD){
                clientInput = (CommunicationRequest<?>)fromClient.readObject();
                if(clientInput.getType() == CLOSE_THREAD) {
                    clientSocket.close();
                    parentServer.removeThread(username);
                }
                else if (clientInput.getType() == WHISPER){
                    String receivingUsername = clientInput.getRelevantUser();
                    String message = (String)clientInput.getData();
                    if((receivingUsername != null) && (message != null))
                        parentServer.sendWhisper(username, receivingUsername, message);
                }
                else
                    parentServer.printToAllClients(clientInput);
            }
        } catch(IOException e){
            System.err.println("IOException thrown while communicating with" + username);
        } catch(ClassNotFoundException cnfe){
            //Should never happen
            System.err.println("ClassNotFoundException thrown while communicating with " + username);
        }

        System.out.println(clientSocket.getRemoteSocketAddress() + " has logged off as " + username);
    }

}
