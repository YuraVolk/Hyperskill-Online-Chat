package chat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class Database implements java.io.Serializable {
    private List<List<List<MessageList>>> correspondenceMessages = new ArrayList<>();
    private Map<String, String> users = new LinkedHashMap<>();
    private Map<String, UserStatus> userStatus = new HashMap<>();

    public void addUserChat(String name, String password) throws IOException {
        users.put(name, password);
        userStatus.put(name, new UserStatus());

        if (correspondenceMessages.size() == 0) {
            correspondenceMessages.add(new ArrayList<>(){{
                add(new ArrayList<>(){{
                    add(new MessageList());
                }});
            }});

        } else {
            for(int i = 0; i < correspondenceMessages.size(); i++){
                correspondenceMessages.get(i).add(new ArrayList<>(){{add(new MessageList());}});
            }

            List<List<MessageList>> tempList = new ArrayList<>();
            for (int i = 0; i < correspondenceMessages.get(0).size(); i++) {
                tempList.add(new ArrayList<>(){{
                    add(new MessageList());
                }});
            }

            correspondenceMessages.add(tempList);
        }

        serialize();
    }

    public void addModerator(String name) {
        userStatus.put(name, userStatus.get(name).setType("moderator"));
    }

    public void addAdministrator(String name) {
        userStatus.put(name, userStatus.get(name).setType("admin"));
    }

    public void removeModerator(String name) {
        userStatus.put(name, userStatus.get(name).setType("user"));
    }

    public void ban(String name) {
        userStatus.put(name, userStatus.get(name).setBanned(true));
    }

    public List<List<List<MessageList>>> getCorrespondenceMessages() {
        return  correspondenceMessages;
    }

    public boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    public String getPassword(String username) {
        return users.get(username);
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public int getUserPosition(String key) {
        List keys = new ArrayList(users.keySet());
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).equals(key)) {
                return i;
            }
        }

        return -1;
    }

    public void addMessage(String name, String line, int senderId, int addresseeId) throws IOException {
        correspondenceMessages.get(senderId).get(addresseeId).get(0).addMessage(name, line);
        correspondenceMessages.get(addresseeId).get(senderId).get(0).addMessage(name, line);
        serialize();
    }

    private void serialize() throws IOException {
        FileOutputStream fileOut =
                new FileOutputStream("db.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();
    }

    public MessageList getMessages(int id1, int id2) {
        return correspondenceMessages.get(id1).get(id2).get(0);
    }
}
