package chat;

import java.util.HashMap;
import java.util.Map;

public class MessageList {
    private Map<String, String> messages = new HashMap<>();

    public void addMessage(String name, String message) {
        messages.put(name, message);
    }

    public Map<String, String> getMessages() {
        return messages;
    }
}

