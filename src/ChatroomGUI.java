import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ChatroomGUI extends Application {

    public void start(Stage primaryStage){

        BorderPane pane = new BorderPane();
        BorderPane messageArea = new BorderPane();
        TextField text = new TextField();
        Button sendButton = new Button("Send");
        text.setPromptText("Enter messages here!");

        sendButton.setMinSize(30,20);
        messageArea.setCenter(text);
        messageArea.setRight(sendButton);

        pane.setCenter(new TextArea());
        pane.setBottom(messageArea);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Let's chat!");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

