import net.jini.core.entry.Entry;

import java.util.Date;

public class ChatMessage implements Entry {
    public String user;
    public String message;
    public Date createAt;

    public ChatMessage() {
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "user='" + user + '\'' +
                ", message='" + message + '\'' +
                ", createAt=" + createAt +
                '}';
    }
}
