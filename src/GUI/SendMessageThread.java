package GUI;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class SendMessageThread extends Thread{
    private PrintStream toServer;
    private boolean running;

    /**
     * Constructor for SendMessageThread. Used to send messages to the
     * server.
     * @param s The socket to communicate on.
     */
    SendMessageThread(Socket s){
        running = true;
        try{
            toServer = new PrintStream(s.getOutputStream());
        } catch(IOException e){
            System.out.println("Error constructing SendMessageThread");
        }
    }

    /**
     * Used to terminate the loop within run()
     */
    void close(){
        running = false;
    }

    /**
     * Sends a message to the server.
     * @param message The String to send to the server.
     */
    void send(String message) {
        toServer.println(message);
    }

    /**
     * Sleep until interrupted. All calls to send() happen from the JavaFX client.
     */
    public void run(){
        while (running){
            try {
                sleep(50);
            } catch(InterruptedException e) {}
        }
    }
}

