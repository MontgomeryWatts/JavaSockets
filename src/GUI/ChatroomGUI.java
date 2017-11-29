package GUI;

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

import static GUI.CommunicationProtocol.*;
import static java.lang.Thread.sleep;


public class ChatroomGUI extends Application implements Observer{
    private Stage stage;
    private Scene chatScene;
    private SendMessageThread smt;

    /**
     * Creates the window which users will use to login/register
     * @return A Scene to be used by the user for login.
     */
    private Scene createLoginScene(){

        VBox userPassBox = new VBox();
        TextField userField = new TextField();
        userField.setPromptText("Username");
        HBox usernameArea = new HBox();
        HBox.setMargin(userField, new Insets(6,12,6,12));
        usernameArea.getChildren().add(userField);

        //Initialize PasswordField, set an action where it will send a login request to the server when enter is pressed
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        HBox passArea = new HBox();
        HBox.setMargin(passField, new Insets(6,12,6,12));
        passField.setOnAction(event ->{
            //If username has no whitespace and username is not empty.
            if((userField.getText().equals(userField.getText().replaceAll("\\s+","")))
                    && (!userField.getText().replaceAll("\\s+", "").equals(""))) {
                smt.send(RETURN_USER);
                smt.send(userField.getText());
                smt.send(passField.getText());
                passField.clear();
            }
            else{
                //Indicate usernames cannot have whitespace in them or be empty
            }
        });
        passArea.getChildren().add(passField);

        //Now that passField is initialized, when enter is pressed in userField, switch to passField
        userField.setOnAction(event -> passField.requestFocus());

        //Create a button that will also send a login request to server
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(loginButton, Priority.ALWAYS);
        HBox.setMargin(loginButton, new Insets(6,12,6,0));
        loginButton.setOnAction(event ->{
            //If username has no whitespace and username is not empty.
            if((userField.getText().equals(userField.getText().replaceAll("\\s+","")))
                    && (!userField.getText().replaceAll("\\s+", "").equals(""))) {
                smt.send(RETURN_USER);
                smt.send(userField.getText());
                smt.send(passField.getText());
                passField.clear();
            }
            else{
                //Indicate usernames cannot have whitespace in them or be empty
            }
        });

        //Create a button that will inform the server a new user should be created
        Button registerButton = new Button("Register");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(registerButton, Priority.ALWAYS);
        HBox.setMargin(registerButton, new Insets(6,12,6,12));
        registerButton.setOnAction(event ->{
            //If username has no whitespace and username is not empty.
            if((userField.getText().equals(userField.getText().replaceAll("\\s+","")))
                    && (!userField.getText().replaceAll("\\s+", "").equals(""))) {
                smt.send(NEW_USER);
                smt.send(userField.getText());
                smt.send(passField.getText());
                passField.clear();
            }
            else{
                //Indicate usernames cannot have whitespace in them or be empty
            }
        });

        HBox buttonArea = new HBox();
        buttonArea.getChildren().addAll(registerButton, loginButton);

        userPassBox.getChildren().addAll(usernameArea, passArea);
        BorderPane pane = new BorderPane();
        pane.setCenter(userPassBox);
        pane.setBottom(buttonArea);
        return new Scene(pane);
    }

    /**
     * Called when the ReceieveMessageThread receives SUCCESSFUL_LOGIN from the server.
     * Switches the scene to the main chat window.
     * @param observable The ReceiveMessageThread, not directly used.
     * @param o  Not used.
     */
    public void update(Observable observable, Object o) {
        smt.send(SUCCESSFUL_LOGIN);
        Platform.runLater(() -> {
            stage.setScene(chatScene);
            stage.setMinHeight(300);
            stage.setMinWidth(400);
        });
    }

    public void start(Stage primaryStage){
        stage = primaryStage;

        //Initialize JavaFX elements used and Socket
        Socket socket = null;

        TextArea messageDisplay = new TextArea();
        messageDisplay.setEditable(false);

        TextArea peopleOnline = new TextArea();
        peopleOnline.appendText("Users online:\n");
        peopleOnline.setEditable(false);
        peopleOnline.setPrefWidth(150);

        TextField text = new TextField();
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
            socket = new Socket("127.0.0.1", 6000);
            sleep(1000);
        } catch(Exception e){
            System.exit(1);
        }

        //Initialize and start Threads to send and retrieve messages to/from server
        ReceiveMessageThread rmt = new ReceiveMessageThread(socket, messageDisplay, peopleOnline);
        rmt.addObserver(this);
        smt = new SendMessageThread(socket);
        Thread thread = new Thread(rmt);
        thread.setDaemon(true);
        thread.start();
        smt.start();


        //Set actions for TextField and Button to send text
        text.setOnAction(event ->  {
            //Prevents empty messages from being sent
            if(!text.getText().replaceAll("\\s+", "").equals("")){
                smt.send(text.getText());
                text.clear();
            }
        });

        sendButton.setOnAction(event ->  {
            //Prevents empty messages from being sent
            if(!text.getText().replaceAll("\\s+", "").equals("")) {
                smt.send(text.getText());
                text.clear();
            }
        });


        chatScene = new Scene(pane);
        stage.setScene(createLoginScene());
        stage.setTitle("Let's chat!");

        //Tell the SocketServer to close the thread that corresponds to this client
        //and close SendMessageThread
        stage.setOnCloseRequest(event -> {
            smt.send(CLOSE_THREAD);
            smt.close();
        });

        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}