import java.io.*;
import java.net.*;

public class SocketServerThread extends Thread{
    private Socket serverSocket;
    private DataInputStream fromClient;
    private DataOutputStream toClient;
    //private static ArrayList<Socket> clients = new ArrayList<>();

    public SocketServerThread(Socket socket) throws IOException{
        serverSocket = socket;
        fromClient = new DataInputStream(serverSocket.getInputStream());
        toClient = new DataOutputStream(serverSocket.getOutputStream());
    }

    public void print(String message){
        try {
            toClient.writeUTF("Message received by server: " + message);
            toClient.flush();
        } catch (IOException e){
            System.out.println("Error");
        }
    }

    public void run() {
        System.out.println("Connected to " + serverSocket.getRemoteSocketAddress());
        while(true) {
            try {
                String clientInput;
                try{
                    //While there is input from the client, read it and add to message Queue
                    while((clientInput = fromClient.readUTF()) != null) {
                        SocketServer.messagesToSend.add(clientInput);
                    }
                } catch(IOException e){
                    System.out.println("Client disconnected.");
                }
                serverSocket.close();

            }catch(SocketTimeoutException s) {
                System.out.println("Socket timed out.");
                break;
            }catch(IOException e) {
                break;
            }
        }
    }

}
