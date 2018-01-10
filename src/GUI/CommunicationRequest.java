package GUI;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CommunicationRequest<E extends Serializable> implements Serializable{

    public enum CommType{
        CLOSE_THREAD,
        MESSAGE,
        NEW_USER,
        RETURN_USER,
        SUCCESSFUL_LOGIN,
        FAILED_LOGIN,
        USER_ONLINE,
        USER_OFFLINE,
        WHISPER,
        REPLY
    }

    private CommType type;
    private E data;

    public CommunicationRequest(CommType type, E data){
        this.type = type;
        this.data = data;
    }

    public CommType getType() {
        return type;
    }

    public E getData() {
        return data;
    }

    public static <E extends Serializable> void sendRequest(ObjectOutputStream outputStream, CommType type, E data){
        try {
            outputStream.writeObject(new CommunicationRequest<>(type, data));
            outputStream.flush();
        } catch(IOException ioe){}
    }

    public static void sendRequest(ObjectOutputStream outputStream, CommunicationRequest request){
        try {
            outputStream.writeObject(request);
            outputStream.flush();
        } catch(IOException ioe){}
    }
}
