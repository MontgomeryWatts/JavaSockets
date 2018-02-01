package cli;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessageThread extends Thread {

    private DataInputStream fromServer;
    private volatile boolean smtRunning;

    public ReceiveMessageThread(Socket s) {
        try {
            fromServer = new DataInputStream(s.getInputStream());
            smtRunning = true;
        } catch (IOException e) {
            System.out.println("Error constructing ReceiveMessageThread");
        }
    }

    public void turnOff() {
        smtRunning = false;
    }

    public void run() {
        String message;
        while (smtRunning) {
            try {
                if ((message = fromServer.readUTF()) != null)
                    System.out.println(message);
            } catch (IOException e) {}
        }
    }
}
