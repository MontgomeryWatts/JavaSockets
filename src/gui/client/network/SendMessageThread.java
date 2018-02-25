package gui.client.network;

import gui.CommunicationRequest;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Used solely to send CommunicationRequests to the server.
 */

public class SendMessageThread extends Thread{

    private ObjectOutputStream toServer;
    private boolean running;

    /**
     * Constructor for SendMessageThread. Used to send messages to the server.
     * @param s The socket to communicate on.
     */

    public SendMessageThread(Socket s){
        running = true;
        try{
            toServer = new ObjectOutputStream(s.getOutputStream());
            toServer.flush();
        } catch(IOException e){
            System.out.println("Error constructing SendMessageThread");
        }
    }

    /**
     * Used to terminate the loop within run()
     */

    public void close(){
        running = false;
    }


    /**
     * A wrapper method to sendRequest
     * @param request The CommunicationRequest to send
     * @param <E> Data type that is Serializable, in practice only String or null
     */

    public <E extends Serializable> void send(CommunicationRequest request) {
        CommunicationRequest.sendRequest(toServer, request);
    }

    /**
     * Sleeps perpetually until asked to send something by another thread.
     */

    public void run(){
        while (running){
            try {
                sleep(1000);
            } catch(InterruptedException e) {}
        }
    }
}

