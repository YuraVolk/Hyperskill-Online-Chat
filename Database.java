package chat;

import java.util.*;

public class Database {
    private List<List<List<MessageList>>> correspondenceMessages = new ArrayList<>();
    private Map<String, String> users = new LinkedHashMap<>();

    public void addUserChat(String name, String password) {
        users.put(name, password);

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

    }

    public List<List<List<MessageList>>> getCorrespondenceMessages() {
        return  correspondenceMessages;
    }

    public boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    public Map<String, String> getUsers() {
        return users;
    }
}
