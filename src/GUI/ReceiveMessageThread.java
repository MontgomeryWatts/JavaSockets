package GUI;

import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessageThread extends Thread {

    private DataInputStream fromServer;
    private boolean running;
    private TextArea textArea;

    /**
     * Constructor for ReceiveMessageThread. Used to retrieve messages from
     * the server and append them to the GUI's text area.
     * @param s The socket to communicate on.
     * @param textArea The GUI to append text to.
     */
    ReceiveMessageThread(Socket s, TextArea textArea) {
        this.textArea = textArea;
        try {
            fromServer = new DataInputStream(s.getInputStream());
            running = true;
        } catch (IOException e) {
            System.out.println("Error constructing ReceiveMessageThread");
        }
    }

    /**
     * Used to stop the while loop.
     */
    void close() {
        running = false;
    }

    /**
     * Checks for messages from the server and appends them to the text area.
     */
    public void run() {
        String message;
        while (running) {
            try {
                message = fromServer.readUTF();
                textArea.appendText(message + "\n");
            } catch (IOException e) {
                System.out.println("Connection to server lost.");
                close();
            }
        }
    }
}
