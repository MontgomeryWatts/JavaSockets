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

    public SocketServerThread(SocketServer server, Socket socket) throws IOException{
        parentServer = server;
        clientSocket = socket;
        fromClient = new DataInputStream(clientSocket.getInputStream());
        toClient = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void print(String message){
        try {
            toClient.writeUTF(message);
            toClient.flush();
        } catch (IOException e){}
    }

    public int getID(){ return ID;}

    public void setID(int newID){
        ID = newID;
    }

    public void run() {
        System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());
        String clientInput = "";
        try{
            while(!clientInput.equals(SocketServer.CLOSE_THREAD_MESSAGE)){
                clientInput = fromClient.readUTF();
                if(!clientInput.equals(SocketServer.CLOSE_THREAD_MESSAGE))
                    parentServer.printToAllClients("Anonymous " + getID() + ": " + clientInput);
                else {
                    print("Closing program.");
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
