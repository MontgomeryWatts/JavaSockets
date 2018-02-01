package gui.client;

import gui.CommunicationRequest;
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
    private TextArea messageArea;
    private TextArea peopleOnline;

    /**
     * Constructor for ReceiveMessageThread. Used to retrieve messages from
     * the server and append them to the gui's text area.
     * @param s The socket to communicate on.
     * @param messageArea The text area of the gui to append messages to.
     * @param peopleOnline The text area of the gui to append usernames to.
     */
    ReceiveMessageThread(Socket s, TextArea messageArea, TextArea peopleOnline) {
        this.messageArea = messageArea;
        this.peopleOnline = peopleOnline;
        try {
            fromServer = new ObjectInputStream(s.getInputStream());
            running = true;
        } catch (IOException e) {
            System.out.println("Error constructing ReceiveMessageThread");
        }
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

            } catch (ClassNotFoundException cnfe){
                //Should never happen
            }

            if(serverInput.getType() == CommunicationRequest.CommType.FAILED_LOGIN)
                Platform.runLater(this::wrongInfoAlert);
        } while((serverInput.getType() != CommunicationRequest.CommType.SUCCESSFUL_LOGIN));
        super.setChanged();
        super.notifyObservers();
        while (running) {
            try {
                serverInput = (CommunicationRequest<?>)fromServer.readObject();
                if(serverInput.getType() == CommunicationRequest.CommType.USER_ONLINE)
                    peopleOnline.appendText(serverInput.getData() + "\n");
                else if (serverInput.getType() == CommunicationRequest.CommType.USER_OFFLINE){
                    String newText = peopleOnline.getText();
                    peopleOnline.clear();
                    newText = newText.replaceAll(serverInput.getData() + "\n", "");
                    peopleOnline.appendText(newText);
                    messageArea.appendText(serverInput.getData() + " has disconnected.\n");
                }
                else
                    messageArea.appendText(serverInput.getData() + "\n");
            } catch (IOException ioe){

            } catch(ClassNotFoundException cnfe){
                //Should never happen
            } catch(NoSuchElementException nse){
                //This will always be thrown once client closes the window so used to end RMT.
                running = false;
            }
        }
    }
}
