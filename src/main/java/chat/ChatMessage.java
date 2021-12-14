package chat;

import net.jini.core.entry.Entry;

import java.util.Date;
import java.util.Objects;

public class ChatMessage implements Entry {
    public String user;
    public String message;
    public Date createdAt;

    public ChatMessage() {
    }

    @Override
    public String toString() {
        return "chat.ChatMessage{" +
                "user='" + user + '\'' +
                ", message='" + message + '\'' +
                ", createAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(user, that.user) && Objects.equals(message, that.message) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, message, createdAt);
    }
}
