package cli;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer {
    private ArrayList<SocketServerThread> threads;
    private int clients;

    public SocketServer(){
        threads = new ArrayList<>();
        clients = 0;
    }

    public void addThread(SocketServerThread thread){
        threads.add(thread);
        clients++;
    }

    public void removeThread(SocketServerThread thread) { threads.remove(thread); }

    public synchronized void printToAllClients(String clientInput) {
        System.out.println(clientInput);
        for (SocketServerThread s : threads) {
            s.print(clientInput);
        }
    }

    public int getClientNumber(){ return clients;}

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
            } catch(IOException e){}

            try{
                SocketServerThread serverThread = new SocketServerThread(mainServer, socket);
                mainServer.addThread(serverThread);
                serverThread.start();
                serverThread.setID(mainServer.getClientNumber());
            } catch(IOException e){}
        }

    }
}
