package Items;



import Users.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Item implements Serializable
{
    private String itemType;
    private int quantity;
    private int numAvailable;
    private String title;
    private String genre;
    private String description;
    private int publishedYear;
    private String imagePath;
    private double averageRating;

    private ArrayList<User> currentCheckedOut;
    private ArrayList<User> checkoutHistory;
    private LocalDateTime lastCheckoutTime;
    private ArrayList<Review> reviews;


    public Item(String itemType, int quantity, String title, String genre, String description, int publishedYear,
                String imagePath)
    {
        this.itemType = itemType;
        this.quantity = quantity;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.publishedYear = publishedYear;
        this.imagePath = imagePath;

        averageRating = 0.0;
        lastCheckoutTime = null;
    }


    public void checkOutItem(User user)
    {
        currentCheckedOut.add(user);
        checkoutHistory.add(user);
        numAvailable--;
        //user.add_CheckOutItem(this);
    }

    public void returnItem(User user)
    {
        currentCheckedOut.remove(user);
        //user.return_CheckedOutItem(this);

        numAvailable++;
    }

    public void addReview(Review review)
    {
        reviews.add(review);

        //update average rating
        int sum = 0;
        for(Review r : reviews)
            sum += r.getStars();

        averageRating = (double) Math.round((sum / reviews.size()) * 10.0) / 10;
        //averageRating = Math.round( (sum / reviews.size()) * 10.0) / 10.0; // calculate average, round it to 1 decimal
    }



    /** Getter methods
     */
    public String getItemType() { return itemType; }
    public int getQuantity() { return quantity; }
    public int getNumAvailable() { return numAvailable; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }
    public int getPublishedYear() { return publishedYear; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String newPath) { imagePath = newPath; }
    public double getAverageRating() { return averageRating; }
    public ArrayList<Review> getReviews() { return reviews; }
    public ArrayList<User> getCurrentCheckedOut() { return currentCheckedOut; }
    public ArrayList<User> getCheckoutHistory() { return checkoutHistory; }
    public LocalDateTime getLastCheckoutTime() { return lastCheckoutTime; }

    /** Manual initialize methods; JSON/GSON can't initialize these
     */
    public void setNumAvailable(int x) { numAvailable = x; }
    public void initializeLists()
    {
        currentCheckedOut = new ArrayList<>();
        checkoutHistory = new ArrayList<>();
        reviews = new ArrayList<>();
    }
}
