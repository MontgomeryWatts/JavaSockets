package gui.client.bot;

public class Bot {
    private String username;
    private String password;

    private Bot(String username, String password){
        this.username = username;
        this.password = password;

    }

    private void run(){
        //Going to be all pseudocode sorry
        /*
        Attempt to connect to server
        If connect to server is made
        Attempt to log on
        try registering account first
        if registering doesnt work try logging in as prev user
        if cant login as prev user bail out, unless want to see how spamming for connections works

        assuming login was successful, we can now send messages to server
        send message with timestamp of when message was sent?
        maybe keep RMT alive to know if we were booted off server
         */
    }

    public static void main(String[] args) {

    }
}
