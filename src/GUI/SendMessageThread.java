package GUI;

import javafx.scene.control.TextField;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SendMessageThread extends Thread{
    private DataOutputStream toServer;

    public SendMessageThread(Socket s){
        try{
            toServer = new DataOutputStream(s.getOutputStream());
        } catch(IOException e){
            System.out.println("Error constructing SendMessageThread");
        }
    }

    public void send(String message) {
        try {
            toServer.writeUTF(message);
        } catch(IOException e) {
            System.out.println("Error sending message to server.");
        }
    }

    public void run(){
        while (true){}
    }
}

