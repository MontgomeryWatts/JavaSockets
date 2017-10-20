import java.io.*;
import java.net.*;

public class SocketServerThread extends Thread{
    private SocketServer server;
    private Socket clientSocket;
    private DataInputStream fromClient;
    private DataOutputStream toClient;

    public SocketServerThread(SocketServer server, Socket socket) throws IOException{
        this.server = server;
        this.clientSocket = socket;
        this.fromClient = new DataInputStream(clientSocket.getInputStream());
        this.toClient = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void print(String message){
        try {
            toClient.writeUTF("Message received from server: " + message);
            toClient.flush();
        } catch (IOException e){}
    }

    public void run() {
        System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());
        String clientInput = "";
        try{
            while(!clientInput.equals("q")){
                clientInput = fromClient.readUTF();
                if(!clientInput.equals("q"))
                    server.printToAllClients(clientInput);
                else
                    print("Closing program.");
            }
        } catch(IOException e){
            System.out.println("Client disconnected.");
        }
        finally{
            try{
                clientSocket.close();
                server.removeThread(this);
            } catch(IOException e){}
        }
    }

}
