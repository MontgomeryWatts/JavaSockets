package gui.client.network;

import gui.CommunicationRequest;
import gui.client.Client;
import gui.client.user.ChatroomGUI;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Observable;

public class ReceiveMessageThread extends Observable implements Runnable{

    private ObjectInputStream fromServer;
    private boolean running;
    private Client client;

    /**
     * Constructor for ReceiveMessageThread. Used to retrieve messages from
     * the server and append them to the gui's text area.
     * @param s The socket to communicate on.
     * @param client The ChatroomGUI this thread is communicating for
     */
    public ReceiveMessageThread(Socket s, Client client) {
        this.client = client;
        try {
            fromServer = new ObjectInputStream(s.getInputStream());
            running = true;
        } catch (IOException e) {
            System.out.println("Error constructing ReceiveMessageThread");
        }
    }

    /**
     * Stops the run method
     */
    void close(){
        running = false;
    }

    /**
     * Displays alert notifying that login info was incorrect.
     */
    private void wrongInfoAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning bucko!");
        alert.setHeaderText(null);
        alert.setContentText("Incorrect username or password.");
        alert.showAndWait();
    }

    /**
     * Checks for messages from the server and appends them to the text area.
     */
    public void run() {
        CommunicationRequest<?> serverInput = null;
        do{
            try{
                serverInput = (CommunicationRequest<?>)fromServer.readObject();
            } catch(IOException ioe){
                System.err.println("IOException thrown while reading from server.");
            } catch (ClassNotFoundException cnfe){
                //Should never happen
                System.err.println("ClassNotFoundException thrown while reading from server.");
            }

            if(serverInput.getType() == CommunicationRequest.CommType.FAILED_LOGIN)
                Platform.runLater(this::wrongInfoAlert);
        } while((serverInput != null) &&(serverInput.getType() != CommunicationRequest.CommType.SUCCESSFUL_LOGIN));
        super.setChanged();
        super.notifyObservers();
        while (running) {
            try {
                serverInput = (CommunicationRequest<?>)fromServer.readObject();
                if(serverInput.getType() == CommunicationRequest.CommType.USER_ONLINE)
                    client.updateUsersOnline((String)serverInput.getData(),
                            CommunicationRequest.CommType.USER_ONLINE);
                else if (serverInput.getType() == CommunicationRequest.CommType.USER_OFFLINE){
                    client.updateUsersOnline((String)serverInput.getData(),
                            CommunicationRequest.CommType.USER_OFFLINE);
                }
                else {
                    client.updateMessages(serverInput.getData() + "\n");
                    if(serverInput.getType() == CommunicationRequest.CommType.WHISPER)
                        client.setLastWhispered(serverInput.getRelevantUser());
                }
            } catch (IOException ioe){
                System.err.println("IOException thrown while reading server input sometime after successful login");
            } catch(ClassNotFoundException cnfe){
                //Should never happen
                System.err.println("Un-throwable exception thrown!!");
            } catch(NoSuchElementException nse){
                //This will always be thrown once client closes the window so used to end RMT.
                running = false;
            }
        }
    }
}
