package GUI;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer {
    static final String CLOSE_THREAD_MESSAGE = "Shutdown Thread";
    static final File LOGIN_INFO_FILE = new File("logininfo.txt");
    private ArrayList<SocketServerThread> threads;
    private int clients;

    private SocketServer(){
        threads = new ArrayList<>();
        clients = 0;
    }

    private void addThread(SocketServerThread thread){
        threads.add(thread);
        clients++;
    }

    void removeThread(SocketServerThread thread) { threads.remove(thread); }

    void printToAllClients(String clientInput) {
        System.out.println(clientInput);
        for (SocketServerThread s : threads) {
            s.print(clientInput);
        }
    }

    private int getClientNumber(){ return clients;}

    public static void main(String [] args) {

        int serverPort = Integer.parseInt(args[0]);
        SocketServer mainServer = new SocketServer();
        ServerSocket serverSocket = null;
        Socket socket = null;

        try{
            System.out.println("Starting server on port " + serverPort + "...");
            serverSocket = new ServerSocket(serverPort);
        }catch(IOException e){
            System.out.println("Error starting port on " + serverPort);
            System.exit(1);
        }

        System.out.println("Server started successfully.");
        while(true){

            try{
                socket = serverSocket.accept();
            } catch(IOException e){
                System.out.println("Error accepting new Client.");
            }

            try{
                SocketServerThread serverThread = new SocketServerThread(mainServer, socket);
                mainServer.addThread(serverThread);
                serverThread.start();
                serverThread.setID(mainServer.getClientNumber());
            } catch(IOException e){
                System.out.println("Error creating new SocketServerThread");
            }
        }

    }
}
