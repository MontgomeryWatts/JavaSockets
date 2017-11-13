package GUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketServerThread extends Thread{
    private SocketServer parentServer;
    private Socket clientSocket;
    private DataInputStream fromClient;
    private DataOutputStream toClient;
    private int ID;

    SocketServerThread(SocketServer server, Socket socket) throws IOException{
        parentServer = server;
        clientSocket = socket;
        fromClient = new DataInputStream(clientSocket.getInputStream());
        toClient = new DataOutputStream(clientSocket.getOutputStream());
    }

    /**
     * Sends a message to the client
     * @param message The String to send to the client
     */
    void print(String message){
        try {
            toClient.writeUTF(message);
            toClient.flush();
        } catch (IOException e){}
    }

    /**
     * Returns the ID number assigned to this thread's corresponding client.
     * @return
     */
    private int getID(){ return ID;}


    /**
     * Sets the ID number for this thread's corresponding client.
     * @param newID The int to set as the new ID.
     */
    void setID(int newID){
        ID = newID;
    }

    /**
     * Communicates with client while the client has not sent the shutdown message.
     */
    public void run() {
        System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());
        String clientInput = "";
        try{
            while(!clientInput.equals(SocketServer.CLOSE_THREAD_MESSAGE)){
                clientInput = fromClient.readUTF();
                if(!clientInput.equals(SocketServer.CLOSE_THREAD_MESSAGE))
                    parentServer.printToAllClients("Anonymous " + getID() + ": " + clientInput);
                else {
                    print("Closing threads.");
                    clientSocket.close();

                    parentServer.removeThread(this);
                    parentServer.printToAllClients("Anonymous " + getID() + " has disconnected.");
                }
            }
        } catch(IOException e){
            System.out.println("Client disconnected unexpectedly.");
        }
    }
}
