package GUI.server;

import GUI.CommunicationRequest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import static GUI.CommunicationRequest.CommType.MESSAGE;
import static GUI.CommunicationRequest.CommType.USER_OFFLINE;
import static GUI.CommunicationRequest.CommType.USER_ONLINE;
import static GUI.CommunicationRequest.sendRequest;

public class SocketServer {
    private final File LOGIN_INFO_FILE = new File("logininfo.txt");
    private HashMap<String, ObjectOutputStream> threads;
    private Salt salt;

    /**
     * Constructor for SocketServer. Used to keep track of alive SocketServerThreads and
     * authenticate/register users with its Salt.
     */
    private SocketServer(){
        threads = new HashMap<>();
        salt = new Salt(LOGIN_INFO_FILE);
    }

    /**
     * Adds a SocketServerThread's corresponding username and OutputObjectStream
     * to the HashMap. Retrieves all other users already online.
     * @param username String representing username of the corresponding user
     * @param outputStream ObjectOutputStream to send messages to the user
     */
    void addThread(String username, ObjectOutputStream outputStream){
        synchronized (threads) {
            //Send all online usernames to new thread
            for(String user: threads.keySet())
                try{
                outputStream.writeObject(new CommunicationRequest<>(USER_ONLINE, user));
                } catch(IOException ioe){}

            //Add new thread, inform all users that someone has come online.
            threads.put(username, outputStream);
            printToAllClients(new CommunicationRequest(USER_ONLINE, username));
            printToAllClients(new CommunicationRequest(MESSAGE, username + " has connected."));
        }
    }

    /**
     * Calls the server's salt in order to determine if the password for the given username is correct
     * @param username a String containing the username
     * @param password a String containing the password.
     * @return true if the password given matches the hashed password saved to file.
     */
    boolean authenticatePassword(String username, String password){
        synchronized (salt) {
            return salt.authenticatePassword(username, password);
        }
    }

    /**
     * Sends a CommunicationRequest to all connected clients
     * @param request The CommunicationRequest to send.
     */
    void printToAllClients(CommunicationRequest request) {
        if ((request.getType() != USER_ONLINE) && (request.getType() != USER_OFFLINE))
            System.out.println(request.getData());
        for (String username : threads.keySet())
                sendRequest(threads.get(username), request);
    }

    /**
     * Removes a username and PrintStream of a given String from the
     * map of currently online users.
     * @param username String representing the username whose mapping should be removed
     */
    void removeThread(String username) {
        synchronized (threads) {
            threads.remove(username);
            printToAllClients(new CommunicationRequest(USER_OFFLINE,  username));
        }
    }

    /**
     * Calls the server's salt to handle storing a salted, hashed password to a text file.
     * @param username a String containing the username
     * @param password a String containing the password
     * @return true if the username and hashed password are successfully written to file.
     */
    boolean registerNewUser(String username, String password){
        synchronized (salt) {
            return salt.registerNewUser(username, password);
        }
    }

    /**
     * Sends a private message from one user to another
     * @param sendingUsername String representing the username of the user sending the message
     * @param receivingUsername String representing the username of the user receiving the message
     * @param message String of the message to send.
     */
    void sendWhisper(String sendingUsername, String receivingUsername, String message){
        ObjectOutputStream outputStream;
        if((outputStream = threads.get(receivingUsername)) != null){
            sendRequest(outputStream, MESSAGE, sendingUsername + " whispers: " + message);
            if((outputStream = threads.get(sendingUsername)) != null){
                sendRequest(outputStream, MESSAGE, "You whisper to " + receivingUsername + ": " + message);
            }
        }
    }

    /**
     * Returns true if the given username is online
     * @param username String containing the username to search for
     * @return true if the user is online
     */
    boolean userAlreadyOnline(String username){
        synchronized (threads){
            for(String user: threads.keySet()){
                if(user.equals(username))
                    return true;
            }
            return false;
        }
    }

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
                serverThread.start();
            } catch(IOException e){
                System.out.println("Error accepting new client.");
            }

        }

    }
}
