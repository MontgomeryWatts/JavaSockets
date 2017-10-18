import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer {
    private static ArrayList<SocketServerThread> threads;

    public SocketServer(){
        threads = new ArrayList<>();
    }

    public void addThread(SocketServerThread thread){
        threads.add(thread);
    }

    public synchronized void printToAllClients(String clientInput) {
        System.out.println(clientInput);
        for (SocketServerThread s : threads) {
            s.print(clientInput);
        }
    }

    public static void main(String [] args) {
        int serverPort = Integer.parseInt(args[0]);
        SocketServer mainServer = new SocketServer();
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
                SocketServerThread serverThread = new SocketServerThread(mainServer, socket);
                mainServer.addThread(serverThread);
                serverThread.start();
            } catch(IOException e){}
        }
    }
}
