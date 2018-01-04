package GUI.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import static GUI.CommunicationProtocol.USER_OFFLINE;
import static GUI.CommunicationProtocol.USER_ONLINE;

public class SocketServer {
    private final File LOGIN_INFO_FILE = new File("logininfo.txt");
    private HashMap<String, PrintStream> threads;
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
     * Adds a SocketServerThread's corresponding username and PrintStream
     * to the HashMap. Retrieves all other users already online.
     * @param username String representing username of the corresponding user
     * @param printStream PrintStream to send messages to the user
     */
    void addThread(String username, PrintStream printStream){
        synchronized (threads) {
            //Send all online usernames to new thread
            for(String user: threads.keySet())
                printStream.println(USER_ONLINE + " " + user);

            //Add new thread, inform all users that someone has come online.
            threads.put(username, printStream);
            printToAllClients(USER_ONLINE + " " + username);
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
     * Prints a message to all threads(clients) contained in the HashMap.
     * @param clientInput The message to send to all clients
     */
    void printToAllClients(String clientInput) {
        String[] fields = clientInput.split(" ");
        if ((!fields[0].equals(USER_ONLINE)) && (!fields[0].equals(USER_OFFLINE)))
            System.out.println(clientInput);
        for (String username : threads.keySet())
            threads.get(username).println(clientInput);
    }

    /**
     * Removes a username and PrintStream of a given String from the
     * map of currently online users.
     * @param username String representing the username whose mapping should be removed
     */
    void removeThread(String username) {
        synchronized (threads) {
            threads.remove(username);
            printToAllClients(USER_OFFLINE + " " + username);
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
        PrintStream printStream;
        if((printStream = threads.get(receivingUsername)) != null){
            printStream.println(sendingUsername + " whispers: " + message);
            if((printStream = threads.get(sendingUsername)) != null){
                printStream.println("You whispered to " + receivingUsername + ": " + message);
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
