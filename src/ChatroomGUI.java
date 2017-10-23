import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatroomGUI extends JFrame{
    private JTextArea messageArea;
    private JTextField enterText;
    private JButton sendMessage;
    private JPanel mainPanel;

    public ChatroomGUI(){
        super("Chatroom");
        setContentPane(mainPanel);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        enterText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = enterText.getText();
                if(message.equals("")){} //Prevents empty lines from being sent
                else {
                    messageArea.append(message + "\n");
                    enterText.setText("");
                }
            }
        });
        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = enterText.getText();
                if(message.equals("")){}
                else{
                    messageArea.append(message + "\n");
                    enterText.setText("");
                }
            }
        });
    }

    public static void main(String[] args) {
        ChatroomGUI test = new ChatroomGUI();
    }
}
