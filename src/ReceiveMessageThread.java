import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessageThread extends Thread{
    private Socket socket;
    private DataInputStream fromServer;
    public ReceiveMessageThread(Socket s){
        try {
            this.socket = s;
            this.fromServer = new DataInputStream(s.getInputStream());
        } catch (IOException e){}
    }

    public void run(){
        String message;
        while(true)
            try {
                if ((message = fromServer.readUTF()) != null)
                    System.out.println(message);
            } catch (IOException e){}
    }
}
