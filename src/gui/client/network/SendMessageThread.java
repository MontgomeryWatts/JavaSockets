package gui.client.network;

import gui.CommunicationRequest;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class SendMessageThread extends Thread{
    private ObjectOutputStream toServer;
    private boolean running;

    /**
     * Constructor for SendMessageThread. Used to send messages to the
     * server.
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


    public <E extends Serializable> void send(CommunicationRequest request) {
        CommunicationRequest.sendRequest(toServer, request);
    }

    public void run(){
        while (running){
            try {
                sleep(1000);
            } catch(InterruptedException e) {}
        }
    }
}

