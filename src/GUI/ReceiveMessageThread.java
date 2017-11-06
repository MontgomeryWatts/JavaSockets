package GUI;

import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessageThread extends Thread {

    private DataInputStream fromServer;
    private volatile boolean running;
    private TextArea textArea;

    public ReceiveMessageThread(Socket s, TextArea textArea) {
        this.textArea = textArea;
        try {
            fromServer = new DataInputStream(s.getInputStream());
            running = true;
        } catch (IOException e) {
            System.out.println("Error constructing ReceiveMessageThread");
        }
    }

    public void close() {
        running = false;
    }

    public void run() {
        String message;
        while (running) {
            try {
                if ((message = fromServer.readUTF()) != null)
                    textArea.appendText(message + "\n");
            } catch (IOException e) {}
        }
    }
}
