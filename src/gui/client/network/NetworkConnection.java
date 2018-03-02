package gui.client.network;

import gui.client.Client;

import java.net.Socket;
import java.util.Observable;

/**
 * This is the class that will represent the connection to the server.
 */

public class NetworkConnection extends Observable implements Runnable{

    private ReceiveMessageThread rmt;

    public NetworkConnection(Socket socket, Client client){
        //this.rmt = new ReceiveMessageThread(socket, client);
    }

    /**
     * Closes Send and ReceiveMessageThreads
     */
    public void close(){
        rmt.close();
    }

    public void run() {

    }
}
