package Users;

public class ItemRequest extends Message
{
    private String itemType;
    private String itemTitle;
    private String status; //  Pending or Accepted or Denied

    public ItemRequest(User from, User to, String itemType, String itemTitle)
    {
        super(from, to, null);
        this.itemType = itemType;
        this.itemTitle = itemTitle;
        status = "Pending";
    }

    public String getItemType() { return itemType; }
    public String getItemTitle() { return itemTitle; }
    public String getStatus() { return status; }
    public void accept(boolean choice)
    {
        if(choice)
            status = "Accepted";
        else
            status = "Denied";
    }

}
