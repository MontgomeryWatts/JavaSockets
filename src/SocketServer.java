
import java.io.*;
import java.net.*;

public class SocketServer extends Thread{
    private ServerSocket serverSocket;

    public SocketServer(int port) throws IOException{
        this.serverSocket = new ServerSocket(port);
    }

    public void run() {
        while(true) {
            try {
                System.out.println("Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();

                System.out.println("Connected to " + server.getRemoteSocketAddress());
                DataInputStream fromClient = new DataInputStream(server.getInputStream());
                while(true) {
                    System.out.println(fromClient.readUTF());
                }
                //server.close();

            }catch(SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            }catch(IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String [] args) {
        int port = Integer.parseInt(args[0]);
        try {
            Thread t = new SocketServer(port);
            t.start();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
