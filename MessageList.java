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
        private boolean isUnread;

        Message(String sender, String message, boolean isUnread) {
            this.sender = sender;
            this.message = message;

            this.isUnread = !isUnread;
        }

        public String getMessage() {
            if (isUnread) {
                return "(new) " + sender + ": " + message;
            } else {
                return sender + ": " + message;
            }

        }

        public void setRead() {
            this.isUnread = false;
        }

        public boolean isUnread() {
            return isUnread;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "sender='" + sender + '\'' +
                    ", message='" + message + '\'' +
                    ", isUnread=" + isUnread +
                    '}';
        }
    }

    private List<Message> messagesList = new ArrayList<>();

    public void addMessage(String name, String message, boolean isUnread) {
        messagesList.add(new Message(name, message, isUnread));
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

