package gui.client;

import gui.CommunicationRequest;

public interface Client {
    void updateUsersOnline(String username, CommunicationRequest.CommType type);
    void updateMessages(String message);
    void setLastWhispered(String username);
}
