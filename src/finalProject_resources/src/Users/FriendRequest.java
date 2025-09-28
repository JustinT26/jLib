package Users;

import java.io.Serializable;
import java.util.*;
public class FriendRequest implements Serializable
{
    private User requestFrom;
    private User requestTo;
    private boolean pending;
    private boolean accepted;

    public FriendRequest(User requestFrom, User requestTo)
    {
        this.requestFrom = requestFrom;
        this.requestTo = requestTo;
        pending = true;
        accepted = false;
    }

    public void accept(boolean choice)
    {
        pending = false;
        accepted = choice;

        if(accepted)
        {
            requestFrom.addFriend(requestTo);
            requestTo.addFriend(requestFrom);
        }
        else
        {

        }
    }

    public User getRequestFrom() { return requestFrom; }
    public User getRequestTo() { return requestTo;}
    public boolean isPending() { return pending; }
    public boolean isAccepted() { return accepted; }



}
