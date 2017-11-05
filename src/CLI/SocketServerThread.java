package CLI;

import java.io.*;
import java.net.*;

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
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getID(){ return ID;}

    public void setID(int newID){
        ID = newID;
    }

    public void run() {
        System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());
        String clientInput = "";
        try{
            while(!clientInput.equals("q")){
                clientInput = fromClient.readUTF();
                if(!clientInput.equals("q"))
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
