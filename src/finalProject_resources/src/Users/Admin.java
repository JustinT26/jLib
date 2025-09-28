package Users;

import java.util.*;

public class Admin extends User
{
    private ArrayList<ItemRequest> receivedItemRequests;
    public Admin(String username, String password)
    {
        super(username, password);
        receivedItemRequests = new ArrayList<>();
    }

    public ArrayList<ItemRequest> getReceivedItemRequests() { return receivedItemRequests; }
    public void receiveItemRequest(ItemRequest ir) { receivedItemRequests.add(ir); }

}
