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

    /**
     * Constructor for SocketServer. Used to keep track of alive SocketServerThreads and
     * hand out ID numbers.
     */
    private SocketServer(){
        threads = new ArrayList<>();
        clients = 0;
    }

    /**
     * Adds a SocketServerThread to the list of threads, and gives it an ID number.
     * @param thread The SocketServerThread to add
     */
    private void addThread(SocketServerThread thread){
        threads.add(thread);
        clients++;
        thread.setID(getClientNumber());
    }

    /**
     * Removes a thread from the list of threads.
     * @param thread The SocketServerThread to add to the ArrayList
     */
    synchronized void removeThread(SocketServerThread thread) { threads.remove(thread); }


    /**
     * Prints a message to all threads(clients) contained in the arraylist.
     * @param clientInput The message to send to all clients
     */
    void printToAllClients(String clientInput) {
        System.out.println(clientInput);
        for (SocketServerThread s : threads) {
            s.print(clientInput);
        }
    }

    /**
     * Returns total number of clients served, used to give each thread an ID number.
     * @return The ID number of the newly spawned thread.
     */
    private int getClientNumber(){ return clients;}

    public static void main(String [] args) {

        int serverPort = Integer.parseInt(args[0]);
        SocketServer mainServer = new SocketServer();
        ServerSocket serverSocket = null;
        Socket socket;

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
                SocketServerThread serverThread = new SocketServerThread(mainServer, socket);
                mainServer.addThread(serverThread);
                serverThread.start();
            } catch(IOException e){
                System.out.println("Error accepting new client.");
            }

        }

    }
}
