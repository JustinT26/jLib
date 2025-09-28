package Items;

public class DVD extends Item
{
    private int hours;
    private int minutes;

    public DVD(String itemType, int quantity, String title, String description,
               int publishedYear, String imagePath, int hours, int minutes, String genre)
    {
        super(itemType, quantity, title, genre, description, publishedYear, imagePath);
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() { return hours; }
    public int getMinutes() { return minutes; }
}
