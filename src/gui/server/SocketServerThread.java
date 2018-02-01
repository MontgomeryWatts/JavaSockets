package gui.server;

import gui.CommunicationRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static gui.CommunicationRequest.*;


public class SocketServerThread extends Thread{
    private SocketServer parentServer;
    private Socket clientSocket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
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
        toClient = new ObjectOutputStream(socket.getOutputStream());
        toClient.flush();
        fromClient = new ObjectInputStream(socket.getInputStream());
    }

    private String[] parseData(String data){
        try{
            String[] info = { data.substring(0, data.indexOf(';')), data.substring( data.indexOf(';') + 1 ) };
            return info;
        } catch(Exception e){}
        return null;
    }

    /**
     * Communicates with client while the client has not sent the shutdown message.
     */
    public void run() {
        System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());
        CommunicationRequest<?> clientInput = null;

        //Try to register/login the user
        do{
            try {
                clientInput = (CommunicationRequest<?>)fromClient.readObject();
                String info = (String) clientInput.getData();
                String[] parsedInfo = parseData(info);

                if (parsedInfo != null) {
                    boolean loginSuccess;
                    loginSuccess = (clientInput.getType() == CommType.NEW_USER) ? parentServer.registerNewUser(parsedInfo[0], parsedInfo[1])
                            : (parentServer.authenticatePassword(parsedInfo[0], parsedInfo[1]) && (!parentServer.userAlreadyOnline(parsedInfo[0])));

                    if (loginSuccess) {
                        sendRequest(toClient, CommType.SUCCESSFUL_LOGIN, null);
                        username = parsedInfo[0];
                    } else
                        sendRequest(toClient, CommType.FAILED_LOGIN, null);
                }

            } catch(IOException ioe){
                System.err.println("IOException while attempting to login a user.");
            } catch(ClassNotFoundException cnfe){
                //This should never happen
                System.err.println("ClassNotFoundException while attempting to login a user.");
            }
        } while((clientInput.getType() != CommType.SUCCESSFUL_LOGIN)
                && (clientInput.getType() != CommType.CLOSE_THREAD));

        //If the user successfully logged in
        if(clientInput.getType() != CommType.CLOSE_THREAD) {
            parentServer.addThread(username, toClient);
            System.out.println(clientSocket.getRemoteSocketAddress() + " has logged in as " + username);
        }

        try{
            while(clientInput.getType() != CommType.CLOSE_THREAD){
                clientInput = (CommunicationRequest<?>)fromClient.readObject();
                if(clientInput.getType() == CommType.CLOSE_THREAD) {
                    clientSocket.close();
                    parentServer.removeThread(username);
                }
                else if (clientInput.getType() == CommType.WHISPER){
                    String[] parsedInfo = parseData( (String)clientInput.getData() );
                    if(parsedInfo != null)
                        parentServer.sendWhisper(username, parsedInfo[0], parsedInfo[1]);
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
