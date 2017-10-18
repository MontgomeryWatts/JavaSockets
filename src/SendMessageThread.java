import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SendMessageThread extends Thread{
    private Socket socket;
    private DataOutputStream toServer;

    public SendMessageThread(Socket s){
        try{
            this.socket = s;
            this.toServer = new DataOutputStream(s.getOutputStream());
        } catch(IOException e){}
    }

    public void run(){
        System.out.println("Type in to send a message to the server. Enter q to quit.");
        String message = "";
        Scanner s = new Scanner(System.in);
        while (!message.equals("q")){
            message = s.nextLine();
            if(!message.equals("q"))
                try {
                    toServer.writeUTF(message);
                } catch(IOException e){}
        }
    }
}
