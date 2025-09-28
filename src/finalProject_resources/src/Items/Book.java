package Items;

import java.io.Serializable;

public class Book extends Item
{
    private String author;
    private int pages;

    public Book(String itemType, int quantity, String title, String description, int publishedYear,
                String imagePath, String author, int pages, String genre)
    {
        super(itemType, quantity, title, genre, description, publishedYear, imagePath);
        this.author = author;
        this.pages = pages;
    }

    public String getAuthor() { return author; }
    public int getPages() { return pages; }
}
