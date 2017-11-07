package GUI;

import javafx.scene.control.TextField;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SendMessageThread extends Thread{
    private DataOutputStream toServer;
    private boolean running;

    public SendMessageThread(Socket s){
        running = true;
        try{
            toServer = new DataOutputStream(s.getOutputStream());
        } catch(IOException e){
            System.out.println("Error constructing SendMessageThread");
        }
    }

    public void close(){ running = false;}

    public void send(String message) {
        try {
            toServer.writeUTF(message);
            toServer.flush();
        } catch(IOException e) {
            System.out.println("Connection to server lost");
            close();
        }
    }

    public void run(){
        while (running){
            try {
                sleep(50);
            } catch(InterruptedException e) {}
        }
        System.out.println("SMT closed");
    }
}

