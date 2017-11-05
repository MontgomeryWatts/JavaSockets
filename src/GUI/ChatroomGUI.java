package GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.Socket;


public class ChatroomGUI extends Application {

    public void start(Stage primaryStage){
        Socket socket = null;
        ReceiveMessageThread rmt = null;
        SendMessageThread smt = null;

        BorderPane pane = new BorderPane();
        BorderPane messageArea = new BorderPane();
        TextArea messageDisplay = new TextArea();
        TextField text = new TextField();
        Button sendButton = new Button("Send");
        text.setPromptText("Enter messages here!");

        try {
            socket = new Socket("127.0.0.1", 6000);

            System.out.println("Connected to " + socket.getRemoteSocketAddress());
            rmt = new ReceiveMessageThread(socket, messageDisplay);
            smt = new SendMessageThread(socket);
            rmt.start();
            smt.start();

        } catch(Exception e){}
        final SendMessageThread newSmt = smt;

        sendButton.setMinSize(30,20);
        messageDisplay.setEditable(false);
        messageArea.setCenter(text);
        messageArea.setRight(sendButton);

        text.setOnAction(event -> {
             newSmt.send(text.getText());
             text.clear();
         });

        sendButton.setOnAction(event -> {
            newSmt.send(text.getText());
            text.clear();
        });


        pane.setCenter(messageDisplay);
        pane.setBottom(messageArea);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Let's chat!");
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(300);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

