package gui.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) {
        SocketChannel channel;
        Scanner s = new Scanner(System.in);
        try{
            SocketAddress address = new InetSocketAddress("localhost", 6000);
            WritableByteChannel fromServer = Channels.newChannel(System.out);
            channel = SocketChannel.open(address);
            ByteBuffer buffer;
            while(true){
                do{
                    buffer = ByteBuffer.wrap((s.nextLine() + "\n").getBytes());
                    channel.write(buffer);
                    buffer.flip();
                    fromServer.write(buffer);
                    buffer.clear();
                } while(channel.read(buffer) != -1);
                Thread.sleep(50);
            }
        } catch(IOException ioe){
            return;
        } catch(InterruptedException ie){
            System.err.println("interrupted during sleep somehow.");
        }
    }
}
