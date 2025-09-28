package finalProject_client;



import java.net.Socket;
import java.util.*;
import java.io.*;

import ClientGUI.loginScene.loginScene_controller;
import Items.Item;
import Users.Admin;
import Users.User;
import ClientGUI.homeScreen.homePage_controller;
import ClientGUI.viewItem.viewItem_controller;
import javafx.application.Platform;

public class Client
{
    private static ArrayList<Item> receivedCatalog;
    private static HashSet<User> receivedUsers;
    private Socket serverSocket;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;



    public Client() {
        receivedCatalog = new ArrayList<>();
        receivedUsers = new HashSet<>();
        serverSocket = new Socket();
    }

    public void start() {
        try
        {
            serverSocket = new Socket("localhost", 2525);
            System.out.println("connection established");

            ois = new ObjectInputStream(serverSocket.getInputStream());
            oos = new ObjectOutputStream(serverSocket.getOutputStream());

            receivedCatalog = (ArrayList<Item>)(ois.readObject());
            receivedUsers = (HashSet<User>)(ois.readObject());

            if(receivedCatalog != null && receivedUsers != null)
                System.out.println("received catalog and users");
        }
        catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
    }

    public void addUser_serverSide(User user)
    {
        try
        {
            oos.writeObject("addUser"); //1. send "addUser" command to server

            oos.writeObject(user);  //2. send User object to server
            oos.flush();
        }
        catch(IOException ioe) { ioe.printStackTrace(); }
    }
    public void addAdmin_serverSide(Admin admin)
    {
        try
        {
            oos.writeObject("addAdmin"); //1. send "addAdmin" command to server

            oos.writeObject(admin);  //2. send Admin object to server
            oos.flush();
        }
        catch(IOException ioe) { ioe.printStackTrace(); }
    }


    public boolean checkout_serverSide(User user, String itemName)
    {
        try
        {
            //oos.reset();
            oos.writeObject("checkOutRequest"); //1. send "checkOutRequest" command to server
            oos.flush();

            oos.writeObject(user);  //2. send User object to server
            oos.flush();

            oos.writeObject(itemName);//3. send item name to server
            oos.flush();


            boolean requestApproval = (boolean)ois.readObject(); //receive and return request status from server
            return requestApproval;
        }
        catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateUser_serverSide(User user)
    {
        try
        {
            oos.reset();
            oos.writeObject("updateUser"); //1. send "updateUser" command to server
            oos.flush();

            oos.writeObject(user);  //2. send User object to server
            oos.flush();
        }
        catch(IOException e) { e.printStackTrace(); }
    }

    public User return_serverSide(User user, Item i, ArrayList<Integer> indices)
    {
        try
        {
            oos.writeObject("returnRequest"); //1. send "return" command to server
            oos.flush();

            oos.writeObject(user);  //2. send User object to server
            oos.flush();

            oos.writeObject(i); //3. send Item object to server
            oos.flush();

            oos.writeObject(indices); //4. send indices to server

           return (User)ois.readObject(); //receive updated user, return
        }
        catch(IOException | ClassNotFoundException e) { e.printStackTrace(); return null; }
    }

    public static User getUpdatedUser_fromServer(String username)
    {
        try
        {
            oos.reset();
            oos.writeObject("getUpdatedUser"); //1. send "getUpdatedUser" command to server
            oos.flush();

            oos.writeObject(username);  //2. send username string to server
            oos.flush();

            return  (User)ois.readObject(); // 3. receive updated user from server
        }
        catch(IOException | ClassNotFoundException e) { e.printStackTrace(); return null; }
    }
    public static Admin getAdmin_fromServer()
    {
        try
        {
            oos.reset();
            oos.writeObject("getAdmin"); // send getAdmin command
            oos.flush();
            Admin a = (Admin)ois.readObject(); // receive updated Admin from server
            return a;
        }
        catch(IOException | ClassNotFoundException e) { e.printStackTrace(); return null; }
    }


    public static ArrayList<Item> getUpdatedCatalog()
    {
        try
        {
            oos.writeObject("getCatalog"); //1. send "addUser" command to server
            oos.flush();

            receivedCatalog = (ArrayList<Item>)ois.readObject(); //2. receive updated catalog
            System.out.println("received catalog");
            return receivedCatalog;
        }
        catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Item getUpdatedItem_fromServer(Item i)
    {
        try
        {
            //oos.reset();
            oos.writeObject("getUpdatedItem"); //1. send "getUpdatedItem" command to server
            oos.flush();

            oos.writeObject(i);  //2. send item to server
            oos.flush();

            Item updatedItem = (Item)ois.readObject(); // 3. receive updated item from server
            return updatedItem;
        }
        catch(IOException | ClassNotFoundException e) { e.printStackTrace(); return null; }
    }

    public static void resetPassword_serverSide(String username, String newPass)
    {
        try{
            oos.reset();
            oos.writeObject("resetPassword"); // send resetPassword command
            oos.writeObject(username); // send username;
            oos.writeObject(newPass); // send new pass
        }
        catch(IOException e) { e.printStackTrace(); }

    }

    public static HashSet<User> getUpdatedUserSet_fromServer()
    {
        try {
            oos.reset();
            oos.writeObject("getUpdatedUserSet"); // send getUpdatedUserSet command
            oos.flush();

            receivedUsers = (HashSet<User>) ois.readObject(); // receive updated user set from server
            return receivedUsers;
        } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); return null; }
    }

    public static void updateItem_serverSide(Item i)
    {
        try{
            oos.reset();
            oos.writeObject("updateItem"); // send updateItem command
            oos.flush();
            oos.writeObject(i); // send item object
            oos.flush();
        }
        catch(IOException  e) { e.printStackTrace(); }
    }

    /** verifies if a potential username is unique */
    public boolean isUniqueUser (String username)
    {
        for (User user : receivedUsers) {
            if (user.getUsername().equalsIgnoreCase(username))
                return false;
        }
        return true;
    }




}
