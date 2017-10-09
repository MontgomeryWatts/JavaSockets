import java.io.*;
import java.net.*;

public class SocketServer extends Thread{
    private Socket serverSocket;

    public SocketServer(Socket socket) throws IOException{
        this.serverSocket = socket;
    }

    public void run() {
        System.out.println("Connected to " + serverSocket.getRemoteSocketAddress());
        while(true) {
            try {
                DataInputStream fromClient = new DataInputStream(serverSocket.getInputStream());
                DataOutputStream toClient = new DataOutputStream(serverSocket.getOutputStream());
                String clientInput;
                try{
                    //While there is input from the client, read it and reply that the message was received
                    while((clientInput = fromClient.readUTF()) != null) {
                        if(!clientInput.equals("q")) {
                            System.out.println(clientInput);
                            toClient.writeUTF("Message received by server: " + clientInput);
                            toClient.flush();
                        }
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

    public static void main(String [] args) {
        int serverPort = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;
        Socket socket = null;
        try{
            System.out.println("Starting server on port " + serverPort + "...");
            serverSocket = new ServerSocket(serverPort);
        }catch(IOException e){}
        System.out.println("Server started successfully.");
        while(true){
            try{
                socket = serverSocket.accept();
            } catch(IOException e){}

            try{
                new SocketServer(socket).start();
            } catch(IOException e){}
        }
    }
}
