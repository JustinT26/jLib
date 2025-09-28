package Users;

import Items.Item;
import Items.Review;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;


public class User implements Serializable
{
    private String username;
    private String password;
    private ArrayList<Item> currentCheckedOutItems;
    private ArrayList<Item> checkoutHistory;
    private ArrayList<Review> reviewHistory;
    private String pfpImagePath;
    private boolean hasPfp;
    private HashSet<Item> readingList;
    private ArrayList<User> friends;
    private ArrayList<FriendRequest> sentFriendRequests;
    private ArrayList<FriendRequest> receivedFriendRequests;
    private ArrayList<ItemRequest> itemRequests;
    private ArrayList<Message> inbox;

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
        hasPfp = false;
        pfpImagePath = "/ClientGUI/Images/defaultProfilePicture.jpg";

        currentCheckedOutItems = new ArrayList<>();
        checkoutHistory = new ArrayList<>();
        reviewHistory = new ArrayList<>();
        readingList = new HashSet<>();
        friends = new ArrayList<>();
        sentFriendRequests = new ArrayList<>();
        receivedFriendRequests = new ArrayList<>();
        itemRequests = new ArrayList<>();
        inbox = new ArrayList<>();
    }

    public void add_CheckOutItem(Item item) {
        currentCheckedOutItems.add(item);
        checkoutHistory.add(item);
    }

    public void return_CheckedOutItem(Item item) {
        for(Item i: currentCheckedOutItems)
        {
            if(i.getTitle().equals(item.getTitle()))
            {
                currentCheckedOutItems.remove(i);
                break;
            }
        }
    }

    public void add_reviewHistory(Review review) { reviewHistory.add(review); }

    public void resetPassword(String newPassword) { this.password = newPassword; }

    public void addToReadingList(Item i) {
        if(readingList.isEmpty())
            readingList.add(i);
        else
        {
            if(!alreadyAddedToReadingList(i))
                readingList.add(i);
        }
    }

    public void removeFromReadingList(Item i)
    {
        for(Item item : readingList)
        {
            if(item.getTitle().equals(i.getTitle()))
            {
                readingList.remove(item);
                break;
            }
        }
    }

    public boolean alreadyAddedToReadingList(Item i)
    {
        for(Item item : readingList)
        {
            if(item.getTitle().equals(i.getTitle()))
                return true;
        }
        return false;
    }

    public boolean sendFriendRequest(User to)
    {
        FriendRequest friendRequest = new FriendRequest(this, to);
        int requestCount = 0;
        for (FriendRequest fr : sentFriendRequests) {
            if (fr.getRequestTo().getUsername().equals(to.getUsername()))
                requestCount++;
        }
        if (requestCount == 0) // only allow one one request to be sent to another user
        {
            sentFriendRequests.add(friendRequest);
            to.receiveFriendRequest(friendRequest);
            System.out.println("sent friend request to: " + to.getUsername());
            return true; // sent request successfully
        }
        return false; // already have request in process
    }

    public void receiveFriendRequest(FriendRequest fr)
    {
        receivedFriendRequests.add(fr);
        System.out.println("received friend request from: " + fr.getRequestFrom().getUsername());
    }

    public void addFriend(User newFriend)
    {
        friends.add(newFriend);
        System.out.println("added friend: " + newFriend.getUsername());
    }

    public void removeFriend(User oldFriend) {
        for(User u : friends)
        {
            if(u.getUsername().equals(oldFriend.getUsername()))
            {
                friends.remove(u);
                System.out.println("removed friend: " + oldFriend.getUsername());
                break;
            }
        }
    }






    /** Getter methods
     */
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getPfpImagePath() { return pfpImagePath; }
    public boolean isHasPfp() { return hasPfp; }
    public void resetPfp()
    {
        hasPfp = false;
        pfpImagePath = "/ClientGUI/Images/defaultProfilePicture.jpg"; // default pfp
    }
    public void setPfpImagePath(String imagePath)
    {
        hasPfp = true;
        this.pfpImagePath = imagePath;
    }
    public ArrayList<Item> getCurrentCheckedOutItems() { return currentCheckedOutItems; } // return all checked out items
    public ArrayList<Item> getCurrentCheckedOutItem(Item item) // return list containing checked out items of a specific item
    {
        ArrayList<Item> result = new ArrayList<>();
        for(Item i : currentCheckedOutItems)
        {
            if(i.getTitle().equals(item.getTitle()))
                result.add(i);
        }
        return result;
    }
    public ArrayList<Item> getCheckoutHistory() { return checkoutHistory; }
    public ArrayList<Review> getReviewHistory() { return reviewHistory; }
    public HashSet<Item> getReadingList() { return readingList; }
    public ArrayList<User> getFriends() { return friends; }
    public ArrayList<FriendRequest> getSentFriendRequests() { return sentFriendRequests; }
    public ArrayList<FriendRequest> getReceivedFriendRequests() { return receivedFriendRequests; }
    public void removeFromSentRequests(User other)
    {
        for(FriendRequest fr : sentFriendRequests)
        {
            if(fr.getRequestTo().getUsername().equals(other.getUsername()))
            {
                sentFriendRequests.remove(fr);
                break;
            }
        }
    }
    public void removeFromReceivedRequests(User other)
    {
        for(FriendRequest fr : receivedFriendRequests)
        {
            if(fr.getRequestFrom().getUsername().equals(other.getUsername()))
            {
                receivedFriendRequests.remove(fr);
                break;
            }
        }
    }

    public ArrayList<ItemRequest> getItemRequests() { return itemRequests; }
    public boolean addItemRequest(ItemRequest ir)
    {
        for(ItemRequest itemRequest : itemRequests)
        {
            if(itemRequest.getItemTitle().trim().equalsIgnoreCase(ir.getItemTitle()))
                return false; // don't add another request for same item
        }
        itemRequests.add(ir);
        return true;
    }

    public ArrayList<Message> getInbox() { return inbox; }
    public void receiveMessage(Message m) { inbox.add(m); }



}
