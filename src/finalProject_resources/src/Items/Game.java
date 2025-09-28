package Items;

public class Game extends Item
{
    private String platform;

    public Game(String itemType, int quantity, String title, String description,
                int publishedYear, String imagePath, String platform, String genre)
    {
        super(itemType, quantity, title, genre, description, publishedYear, imagePath);
        this.platform = platform;
    }

    public String getPlatform() { return platform; }
}
