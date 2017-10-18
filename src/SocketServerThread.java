import java.io.*;
import java.net.*;

public class SocketServerThread extends Thread{
    private SocketServer server;
    private Socket serverSocket;
    private DataInputStream fromClient;
    private DataOutputStream toClient;

    public SocketServerThread(SocketServer server, Socket socket) throws IOException{
        this.server = server;
        this.serverSocket = socket;
        this.fromClient = new DataInputStream(serverSocket.getInputStream());
        this.toClient = new DataOutputStream(serverSocket.getOutputStream());
    }

    public void print(String message){
        try {
            toClient.writeUTF(message);
            toClient.flush();
        } catch (IOException e){}
    }

    public void run() {
        System.out.println("Connected to " + serverSocket.getRemoteSocketAddress());
        while(true) {
                try{
                    while(true)
                        server.printToAllClients(fromClient.readUTF());
                } catch(IOException e){
                    System.out.println("Client disconnected.");
                    break;
                }
                finally{
                    try{
                        serverSocket.close();
                    } catch(IOException e){}
                }
        }
    }

}
