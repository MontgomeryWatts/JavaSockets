package gui;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * How the clients and server communicate with one another. Objects of this class are sent back and forth,
 * with an element of the CommType enum in order to specify the type of request being made. Any information that is
 * relevant to the request may also be sent with it.
 * @param <E> A data type that is Serializable, typically a String or null
 */

public class CommunicationRequest<E extends Serializable> implements Serializable{


    /**
     * Represents the type of request being made.
     */

    public enum CommType{
        CLOSE_THREAD,
        MESSAGE,
        NEW_USER,
        RETURN_USER,
        SUCCESSFUL_LOGIN,
        FAILED_LOGIN,
        USER_ONLINE,
        USER_OFFLINE,
        WHISPER
    }

    private CommType type;
    private E data;
    private String relevantUser;


    /**
     * Creates a CommunicationRequest object
     * @param type CommType of the CommunicationRequest to create
     * @param data Any relevant data to attach to the request
     */

    public CommunicationRequest(CommType type, E data){
        this.type = type;
        this.data = data;
    }

    /**
     * Creates a CommunicationRequest object, with extra data field for user that may be relevant to the given request.
     * Used more for logging in, sending whispers.
     * @param type CommType of the CommunicationRequest to create
     * @param data Any relevant data to attach to the request
     * @param relevantUser String representing the username, assuming its relevant to the given CommType
     */

    public CommunicationRequest(CommType type, E data, String relevantUser){
        this.type = type;
        this.data = data;
        this.relevantUser = relevantUser;
    }


    /**
     * Returns type of the CommunicationRequest
     * @return the CommType of the CommunicationRequest
     */

    public CommType getType() {
        return type;
    }

    /**
     * Returns any data attached to the request
     * @return Whatever is in the data field
     */

    public E getData() {
        return data;
    }

    /**
     * Returns the username String
     * @return String representing the username
     */

    public String getRelevantUser() {
        return relevantUser;
    }

    /**
     * Sends a CommunicationRequest through a given ObjectOutputStream
     * @param outputStream The stream to send the CommunicationRequest on
     * @param request The CommunicationRequest to send
     */

    public static void sendRequest(ObjectOutputStream outputStream, CommunicationRequest request){
        try {
            outputStream.writeObject(request);
            outputStream.flush();
        } catch(IOException ioe){}
    }
}
