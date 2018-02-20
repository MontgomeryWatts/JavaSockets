package gui.client.user;

import gui.CommunicationRequest;
import gui.client.Client;
import gui.client.network.ReceiveMessageThread;
import gui.client.network.SendMessageThread;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import static gui.CommunicationRequest.CommType.*;
import static java.lang.Thread.sleep;


public class ChatroomGUI extends Application implements Observer, Client{
    private Stage stage;
    private Scene chatScene;
    private SendMessageThread smt;
    private String username;
    private TextField text;
    private TextField userField;
    private PasswordField passField;
    private TextArea messageDisplay;
    private TextArea peopleOnline;
    private String lastWhispered;

    /**
     * Creates the window which users will use to login/register
     * @return A Scene to be used by the user for login.
     */
    private Scene createLoginScene(){

        VBox userPassBox = new VBox();
        userField = new TextField();
        userField.setPromptText("Username");
        HBox usernameArea = new HBox();
        HBox.setMargin(userField, new Insets(6,12,6,12));
        usernameArea.getChildren().add(userField);

        //Initialize PasswordField, set an action where it will send a login request to the server when enter is pressed
        passField = new PasswordField();
        passField.setPromptText("Password");
        HBox passArea = new HBox();
        HBox.setMargin(passField, new Insets(6,12,6,12));
        passField.setOnAction(event -> sendUserInfoEvent(RETURN_USER));
        passArea.getChildren().add(passField);
        userField.setOnAction(event -> passField.requestFocus());

        //Create a button that will also send a login request to server
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(loginButton, Priority.ALWAYS);
        HBox.setMargin(loginButton, new Insets(6,12,6,0));
        loginButton.setOnAction(event -> sendUserInfoEvent(RETURN_USER));

        //Create a button that will inform the server a new user should be created
        Button registerButton = new Button("Register");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(registerButton, Priority.ALWAYS);
        HBox.setMargin(registerButton, new Insets(6,12,6,12));
        registerButton.setOnAction(event -> sendUserInfoEvent(NEW_USER));

        HBox buttonArea = new HBox();
        buttonArea.getChildren().addAll(registerButton, loginButton);

        userPassBox.getChildren().addAll(usernameArea, passArea);
        BorderPane pane = new BorderPane();
        pane.setCenter(userPassBox);
        pane.setBottom(buttonArea);
        return new Scene(pane);
    }

    /**
     * Prevents user from creating a username with non alphanumeric characters.
     * @param username The username to check
     * @return true if there are only alphanumeric characters.
     */
    private boolean isValidUsername(String username){
        char[] nameAsArray = username.toCharArray();
        for(Character c: nameAsArray)
            if(!Character.isLetterOrDigit(c))
                return false;
        return true;
    }

    /**
     * Gets the text input by the user and sends it through the SendMessageThread.
     */
    private void sendMessageEvent(){
        String userText = text.getText();

        if(userText.contains(" ")){
            int spaceIndex = userText.indexOf(" ");

            if((userText.substring(0, spaceIndex).equals("/whisper"))
                    || userText.substring(0, spaceIndex).equals("/w")){
                try{
                    String nameAndMessage = userText.substring(spaceIndex + 1);
                    smt.send(new CommunicationRequest(WHISPER, nameAndMessage.substring(nameAndMessage.indexOf(" ") + 1),
                            nameAndMessage.substring(0, nameAndMessage.indexOf(" "))));
                    text.clear();
                } catch(Exception e){
                    //If the user did not format the message correctly.
                }
            }

            else if((userText.substring(0, spaceIndex).equals("/reply"))
                    || userText.substring(0, spaceIndex).equals("/r")){
                try{
                    String message = userText.substring(spaceIndex + 1);
                    smt.send(new CommunicationRequest(WHISPER, message, lastWhispered));
                    text.clear();
                } catch(Exception e){
                    //If the user did not format the message correctly.
                }
            }

            else{
                smt.send(new CommunicationRequest(MESSAGE, username + ": " + userText));
                text.clear();
            }
        }

        //Prevents empty messages from being sent
        else if(!userText.replaceAll("\\s+", "").equals("")){
            smt.send(new CommunicationRequest(MESSAGE, username + ": " + userText));
            text.clear();
        }
    }

    /**
     * Used when attempting to login/register a new account. Sends username
     * and password to the server to see if it is correct/register.
     * @param type String representing if a login/register is being made.
     */
    private void sendUserInfoEvent(CommunicationRequest.CommType type){
        username = userField.getText();
        //If username has no whitespace and username is not empty.
        if((username.equals(username.replaceAll("\\s+","")))
                && (!username.replaceAll("\\s+", "").equals(""))
                && (isValidUsername(username))){
            smt.send(new CommunicationRequest<>(type, passField.getText(), username));
            passField.clear();
        }
        else{
            //Indicate usernames cannot have whitespace in them or be empty.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning bucko!");
            alert.setHeaderText(null);
            alert.setContentText("Your username either contains whitespace, non-alphanumeric" +
                    " characters, or was left empty.");
            alert.showAndWait();
        }
    }

    @Override
    public void setLastWhispered(String lastWhispered) {
        this.lastWhispered = lastWhispered;
    }

    /**
     * Creates everything for the JavaFX window.
     * @param primaryStage the JavaFX stage
     */
    public void start(Stage primaryStage){
        stage = primaryStage;

        //Initialize JavaFX elements used and Socket
        Socket socket = null;

        messageDisplay = new TextArea();
        messageDisplay.setEditable(false);

        peopleOnline = new TextArea();
        peopleOnline.appendText("Users online:\n");
        peopleOnline.setEditable(false);
        peopleOnline.setPrefWidth(150);

        text = new TextField();
        text.setPromptText("Enter messages here!");

        Button sendButton = new Button("Send");
        sendButton.setMinSize(30,20);

        BorderPane messageArea = new BorderPane();
        messageArea.setCenter(text);
        messageArea.setRight(sendButton);

        BorderPane pane = new BorderPane();
        pane.setCenter(messageDisplay);
        pane.setRight(peopleOnline);
        pane.setBottom(messageArea);

        //Try to create a socket to communicate on
        try {
            socket = new Socket("129.21.130.62", 6000);
            sleep(1000);
        } catch(Exception e){
            System.exit(1);
        }

        //Initialize and start Threads to send and retrieve messages to/from server
        ReceiveMessageThread rmt = new ReceiveMessageThread(socket, this);
        rmt.addObserver(this);
        smt = new SendMessageThread(socket);
        Thread thread = new Thread(rmt);
        thread.setDaemon(true);
        thread.start();
        smt.start();


        //Set actions for TextField and Button to send text
        text.setOnAction(event ->  sendMessageEvent());
        sendButton.setOnAction(event -> sendMessageEvent());


        chatScene = new Scene(pane);
        stage.setScene(createLoginScene());
        stage.setTitle("Let's chat!");

        //Tell the SocketServer to close the thread that corresponds to this client
        //and close SendMessageThread
        stage.setOnCloseRequest(event -> {
            smt.send(new CommunicationRequest(CLOSE_THREAD, null));
            smt.close();
        });

        stage.show();
    }

    /**
     * Overrides the Client interface's method. Used to add messages
     * to the messageDisplay area.
     * @param message The message to add
     */
    public void updateMessages(String message) {
        Platform.runLater(() -> messageDisplay.appendText(message));
    }

    /**
     * Updates the gui with the users who are online
     * @param username String to add/remove from the list
     * @param type Tells whether username is being added or removed
     */
    public void updateUsersOnline(String username, CommunicationRequest.CommType type) {
        Platform.runLater(()-> {
            if(type == USER_ONLINE){
                peopleOnline.appendText(username + "\n");
            }
            else{
                String newText = peopleOnline.getText();
                peopleOnline.clear();
                newText = newText.replaceAll(username + "\n", "");
                peopleOnline.appendText(newText);
                messageDisplay.appendText(username + " has disconnected.\n");
            }
        });
    }

    /**
     * Called when the ReceieveMessageThread receives SUCCESSFUL_LOGIN from the server.
     * Switches the scene to the main chat window.
     * @param observable The ReceiveMessageThread, not directly used.
     * @param o  Not used.
     */
    public void update(Observable observable, Object o) {
        smt.send(new CommunicationRequest(SUCCESSFUL_LOGIN, null));
        Platform.runLater(() -> {
            stage.setScene(chatScene);
            stage.setMinHeight(300);
            stage.setMinWidth(400);
        });
    }

    public static void main(String[] args) {
        Application.launch(args);
    }



}