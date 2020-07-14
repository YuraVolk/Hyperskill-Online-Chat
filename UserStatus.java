package chat;

public class UserStatus {
    private boolean isBanned;
    private String type = "user";

    public boolean isBanned() {
        return isBanned;
    }

    public UserStatus setBanned(boolean banned) {
        isBanned = banned;
        return this;
    }

    public String getType() {
        return type;
    }

    public UserStatus setType(String type) {
        this.type = type;
        return this;
    }
}
