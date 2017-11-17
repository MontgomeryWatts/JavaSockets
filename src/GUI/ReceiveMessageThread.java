package GUI;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Scanner;

public class ReceiveMessageThread extends Observable implements Runnable{

    private Scanner fromServer;
    private boolean running;
    private TextArea textArea;

    /**
     * Constructor for ReceiveMessageThread. Used to retrieve messages from
     * the server and append them to the GUI's text area.
     * @param s The socket to communicate on.
     * @param textArea The text area of the GUI to append text to.
     */
    ReceiveMessageThread(Socket s, TextArea textArea) {
        this.textArea = textArea;
        try {
            fromServer = new Scanner(s.getInputStream());
            running = true;
        } catch (IOException e) {
            System.out.println("Error constructing ReceiveMessageThread");
        }
    }

    /**
     * Checks for messages from the server and appends them to the text area.
     */
    public void run() {
        String message;
        do{
            message = fromServer.nextLine();
        } while(!message.equals(SocketServer.SUCCESSFUL_LOGIN));
        super.setChanged();
        super.notifyObservers();
        while (running) {
            try {
                message = fromServer.nextLine();
                textArea.appendText(message + "\n");
            } catch(NoSuchElementException nse){
                running = false;
            }
        }
    }
}
