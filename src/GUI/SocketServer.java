package GUI;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer {
    static final String CLOSE_THREAD = "k95NwPKr3H6AE4ejdaZmYBzvkw6OzjV/m7cahLVhWZs=";
    static final String NEW_USER = "PLCWEZlGvRJiyG7aLKLX7TxFfzKa2/sfdJl5w0PoF+Y=";
    static final String RETURN_USER = "f9UBEmUBoHH6xsGq8cF4/k5cDLO3xEtdlAGPJun5+wE=";
    static final String SUCCESSFUL_LOGIN ="7KZMblCjkjk7z42pv/bkmplzJ+Frjcny/dtdZZ8FOiM=";
    static final String FAILED_LOGIN = "446ASQ8QfXr1p/LaUrDwnFyV494dp1Prjlvneh0qVwA=";
    static final String USER_ONLINE = "CY9rzUYh03PK3k6DJie09gASQ8LO3xEtdln5EZl=";
    static final String USER_OFFLINE = "1isut5mzYdfl8gRsG66p+gKZ#1GRcs%$2/3c=";
    private final File LOGIN_INFO_FILE = new File("logininfo.txt");
    private ArrayList<SocketServerThread> threads;
    private Salt salt;

    /**
     * Constructor for SocketServer. Used to keep track of alive SocketServerThreads and
     * authenticate/register users with its Salt.
     */
    private SocketServer(){
        threads = new ArrayList<>();
        salt = new Salt(LOGIN_INFO_FILE);
    }

    /**
     * Adds a SocketServerThread to the list of threads, and gives it an ID number.
     * @param thread The SocketServerThread to add
     */
    void addThread(SocketServerThread thread){
        synchronized (threads) {
            threads.add(thread);
            printToAllClients(USER_ONLINE + " " + thread.getUsername());
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
     * Tells the SocketServerThread calling this method what other users are online.
     * @param thread The calling SocketServerThread.
     */
    void getAllUsers(SocketServerThread thread){
        synchronized (threads){
            for(SocketServerThread user: threads)
                thread.print(USER_ONLINE + " " + user.getUsername());
        }
    }

    /**
     * Prints a message to all threads(clients) contained in the arraylist.
     * @param clientInput The message to send to all clients
     */
    void printToAllClients(String clientInput) {
        String[] fields = clientInput.split(" ");
        if ((!fields[0].equals(USER_ONLINE)) && (!fields[0].equals(USER_OFFLINE)))
            System.out.println(clientInput);
        for (SocketServerThread s : threads) {
            s.print(clientInput);
        }
    }

    /**
     * Removes a thread from the list of threads.
     * @param thread The SocketServerThread to add to the ArrayList
     */
    void removeThread(SocketServerThread thread) {
        synchronized (threads) {
            threads.remove(thread);
            printToAllClients(USER_OFFLINE + " " + thread.getUsername());
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

    boolean userAlreadyOnline(String username){
        synchronized (threads){
            for(SocketServerThread thread: threads){
                if(thread.getUsername().equals(username))
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
