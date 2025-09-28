package Users;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.Serializable;

public class Message implements Serializable
{
    private User from;
    private User to;
    private String message;
    private String time;

    public Message(User from, User to, String message)
    {
        this.from = from;
        this.to = to;
        this.message = message;

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        this.time = currentDateTime.format(formatter);
    }

    public User getFrom() { return from; }
    public User getTo() { return to; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
}
