package GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.Socket;


public class ChatroomGUI extends Application {

    public void start(Stage primaryStage){

        //Initialize JavaFX elements used and Socket
        Socket socket = null;

        TextArea messageDisplay = new TextArea();
        messageDisplay.setEditable(false);

        TextArea peopleOnline = new TextArea();
        peopleOnline.setEditable(false);
        peopleOnline.setPrefWidth(100);

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
        } catch(Exception e){}

        //Initialize and start Threads to send and retrieve messages to/from server
        final ReceiveMessageThread rmt = new ReceiveMessageThread(socket, messageDisplay);
        final SendMessageThread smt = new SendMessageThread(socket);
        rmt.start();
        smt.start();


        //Set actions for TextField and Button to send text
        text.setOnAction(event ->  {
            if(text.getText().replaceAll("\\s+", "").equals("")){}
            else{
                if(smt.isAlive()) {
                    smt.send(text.getText());
                    text.clear();
                }
            }
        });

        sendButton.setOnAction(event ->  {
            if(text.getText().replaceAll("\\s+", "").equals("")){}
            else{
                if(smt.isAlive()) {
                    smt.send(text.getText());
                    text.clear();
                }
            }
        });

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Let's chat!");
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(300);

        //Tell the SocketServer to close the thread that corresponds to this client
        //and close Send/ReceiveMessageThreads
        primaryStage.setOnCloseRequest(event -> {
            smt.send(SocketServer.CLOSE_THREAD_MESSAGE);
            rmt.close();
            smt.close();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}