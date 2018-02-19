package gui.client.network;

import gui.client.Client;

import java.net.Socket;
import java.util.Observable;

/**
 * This is the class that will represent the connection to the server.
 * Fundamentally a wrapper for the Send- and ReceiveMessageThread classes
 * until I come up with a better idea.
 */

public class NetworkConnection extends Observable implements Runnable{

    private SendMessageThread smt;
    private ReceiveMessageThread rmt;

    public NetworkConnection(Socket socket, Client client){
        this.smt = new SendMessageThread(socket);
        //this.rmt = new ReceiveMessageThread(socket, client);
    }

    /**
     * Closes Send and ReceiveMessageThreads
     */
    public void close(){
        smt.close();
        rmt.close();
    }

    public void run() {

    }
}
