package chat;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageList implements Serializable {

    public class Message implements Serializable {
        private String sender;
        private String message;

        Message(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }

        public String getMessage() {
            return sender + ": " + message;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "sender='" + sender + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    private List<Message> messagesList = new ArrayList<>();

    public void addMessage(String name, String message) {
        messagesList.add(new Message(name, message));
    }

    public List<Message> getMessagesList() {
        return messagesList;
    }

    @Override
    public String toString() {
        return "MessageList{" +
                "messagesList=" + messagesList +
                '}';
    }
}

