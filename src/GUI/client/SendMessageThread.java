package GUI.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class SendMessageThread extends Thread{
    private final int MESSAGE_LIMIT = 5;

    private PrintStream toServer;
    private boolean running;
    private int messages;

    /**
     * Constructor for SendMessageThread. Used to send messages to the
     * server.
     * @param s The socket to communicate on.
     */
    SendMessageThread(Socket s){
        running = true;
        messages = MESSAGE_LIMIT;
        try{
            toServer = new PrintStream(s.getOutputStream(), true, "utf-8");
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

        if (messages > 0) {
            toServer.println(message);
            messages--;
        }
        else
            Platform.runLater(this::tooManyMessages);
    }

    /**
     * Show a popup indicating the user is sending too many messages.
     */
    private void tooManyMessages(){
        messages = -5;
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning bucko!");
        alert.setHeaderText(null);
        alert.setContentText("Slow down man.");
        alert.showAndWait();
    }

    /**
     * Sleep for a second, if user has messages less than
     * the message limit, allow them to send another message.
     * All calls to send() happen from the JavaFX client.
     */
    public void run(){
        while (running){
            try {
                sleep(1000);
                if(messages < MESSAGE_LIMIT)
                    messages++;
            } catch(InterruptedException e) {}
        }
    }
}

