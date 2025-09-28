package Items;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Review implements Serializable
{
    private Item item;
    private String username;
    private double stars; // out of 5
    private String message;
    private String time;

    public Review(Item item, String username, double stars, String message)
    {
        this.item = item;
        this.username = username;
        this.stars = stars;
        this.message = message;

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        this.time = currentDateTime.format(formatter);
    }

    public Item getItem() { return item; }
    public String getUser() { return username; }
    public double getStars() { return stars; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
}
