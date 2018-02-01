package gui.nio;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class TestServer {
    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel;
        Selector selector;
        try{
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            SocketAddress address = new InetSocketAddress(6000);
            serverSocket.bind(address);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch(IOException ioe){
            ioe.printStackTrace();
            return;
        }
        while(true){
            try{
                selector.select();
            } catch(IOException ioe){
                ioe.printStackTrace();
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                try{
                    if(key.isAcceptable()){
                        ServerSocketChannel socketChannel = (ServerSocketChannel)key.channel();
                        SocketChannel client = socketChannel.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        ByteBuffer buffer = ByteBuffer.allocate(100);
                        clientKey.attach(buffer);
                    }
                    else if(key.isReadable()){
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        client.read(buffer);
                    }
                    else if(key.isWritable()){ //If the client connection is going to write
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        buffer.flip();
                        client.write(buffer);
                        buffer.compact();
                    }
                    Thread.sleep(50);
                } catch(IOException ioe){
                    key.cancel();
                    try{
                        key.channel().close();
                    } catch(IOException e){

                    }
                } catch(InterruptedException ie){

                }
            }
        }
    }
}
