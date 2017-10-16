import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketServer {
    private static ArrayList<SocketServerThread> threads;
    public static Queue<String> messagesToSend;

    public SocketServer(){
        threads = new ArrayList<>();
        messagesToSend = new ConcurrentLinkedQueue<>();
    }

    public void addThread(SocketServerThread thread){
        threads.add(thread);
    }

    public void printToAllClients(String clientInput) {
        for (SocketServerThread s : threads) {
            s.print(messagesToSend.remove());
        }
    }

    public static void main(String [] args) {
        int serverPort = Integer.parseInt(args[0]);
        SocketServer mainServer = new SocketServer();
        ServerSocket serverSocket = null;
        Socket socket = null;
        String message;
        try{
            System.out.println("Starting server on port " + serverPort + "...");
            serverSocket = new ServerSocket(serverPort);
        }catch(IOException e){}
        System.out.println("Server started successfully.");
        while(true){
            message = mainServer.messagesToSend.poll();
            if(message != null)
                mainServer.printToAllClients(message);
            try{
                socket = serverSocket.accept();
            } catch(IOException e){}

            try{
                SocketServerThread serverThread = new SocketServerThread(socket);
                mainServer.addThread(serverThread);
                serverThread.start();
            } catch(IOException e){}
        }
    }
}
