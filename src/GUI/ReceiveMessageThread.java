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
    private TextArea messageArea;
    private TextArea peopleOnline;

    /**
     * Constructor for ReceiveMessageThread. Used to retrieve messages from
     * the server and append them to the GUI's text area.
     * @param s The socket to communicate on.
     * @param messageArea The text area of the GUI to append messages to.
     * @param peopleOnline The text area of the GUI to append usernames to.
     */
    ReceiveMessageThread(Socket s, TextArea messageArea, TextArea peopleOnline) {
        this.messageArea = messageArea;
        this.peopleOnline = peopleOnline;
        try {
            fromServer = new Scanner(s.getInputStream(), "utf-8");
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
                String[] fields = message.split(" ");
                if(fields[0].equals(SocketServer.USER_ONLINE))
                    peopleOnline.appendText(fields[1] + "\n");
                else if (fields[0].equals(SocketServer.USER_OFFLINE)){
                    String newText = peopleOnline.getText();
                    peopleOnline.clear();
                    newText = newText.replaceAll(fields[1] + "\n", "");
                    peopleOnline.appendText(newText);
                }
                else
                    messageArea.appendText(message + "\n");
            } catch(NoSuchElementException nse){
                running = false;
            }
        }
    }
}
