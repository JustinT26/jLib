package finalProject_server;

import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.io.*;
import Items.Book;
import Items.DVD;
import Items.Game;
import Items.Item;
import Users.Admin;
import Users.User;

public class ClientHandler implements Runnable {
    private Socket clientSocket;


    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {
            //send catalog and userbase over to client user
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            synchronized (Server.getCatalog()) {
                oos.writeObject(Server.getCatalog());
                oos.flush();
            }
            synchronized (Server.getUsers()) {
                oos.writeObject(Server.getUsers());
                oos.flush();
            }

            //receive commands from client
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            while (true)
            {
                //if command ==...
                   //call corresponding method to execute command

                String command = (String)(ois.readObject());
                if(command.equals("addUser")) // client will send string command "addUser",
                {                               // followed by sending serialized User object
                    synchronized(Server.getUsers()) {
                        User newUser = (User) ois.readObject();
                        Server.getUsers().add(newUser);
                        System.out.println("added user: " + newUser.getUsername());
                        Server.saveUserSet(); // update user set
                    }
                }
                else if(command.equals("addAdmin"))
                {
                    synchronized (Server.getUsers()) {
                        Admin newAdmin = (Admin) ois.readObject();
                        Server.getUsers().add(newAdmin);
                        Server.setAdmin(newAdmin);
                        Server.saveUserSet();
                        System.out.println("added admin");
                    }
                }
                else if(command.equals("getAdmin"))
                {
                    oos.reset();
                    oos.writeObject(Server.getAdmin()); // send Admin object
                    oos.flush();
                }
                else if(command.equals("getCatalog"))
                {
                    //send catalog to client
                    synchronized (Server.getCatalog()) {
                        oos.reset();
                        oos.writeObject(Server.getCatalog());
                        oos.flush();
                        System.out.println("sent catalog");
                    }
                }
                else if(command.equals("checkOutRequest"))
                {
                    synchronized (Server.getCatalog())
                    {
                        synchronized (Server.getUsers())
                        {
                            boolean flag = false;
                            User userRequesting = (User) ois.readObject(); //receive the user object
                            String itemName = (String) ois.readObject(); //receive the item name
                            for (Item i : Server.getCatalog()) //verify enough available of that item
                            {
                                if (i.getTitle().equals(itemName)) {
                                    if (i.getNumAvailable() > 0) // yes enough, go through with checkout
                                    {
                                        for (User u : Server.getUsers()) {
                                            if (u.getUsername().equals(userRequesting.getUsername())) {
                                                u.add_CheckOutItem(i);
                                                i.checkOutItem(u);
                                            }
                                        }
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            System.out.println("checkout attempt: " + flag);
                            Server.saveUserSet(); //save users to disk
                            Server.saveCatalog(); // save catalog on disk;


                            oos.writeObject(flag); //send request status
                            oos.flush();
                        }
                    }
                }
                else if(command.equals("updateUser"))
                {
                    synchronized (Server.getUsers()) {
                        User userRequesting = (User) ois.readObject(); //receive the user object
                        for (User u : Server.getUsers()) {
                            if (u.getUsername().equals(userRequesting.getUsername())) {
                                if (u.getUsername().equals("ADMIN"))
                                    Server.setAdmin((Admin) userRequesting);
                                Server.getUsers().remove(u);
                                Server.getUsers().add(userRequesting);
                                break;
                            }
                        }
                        Server.saveUserSet(); //save users to disk
                        System.out.println("updated user: " + userRequesting.getUsername());
                    }
                }
                else if (command.equals("getUpdatedUser"))
                {
                    String username = (String)ois.readObject(); // receive username frrom client

                    synchronized (Server.getUsers()) {
                        for (User u : Server.getUsers()) {
                            if (u.getUsername().equals(username)) {
                                oos.reset();
                                oos.writeObject(u); // send user
                                oos.flush();
                                System.out.println("sent updated user: " + u.getUsername());
                                break;
                            }
                        }
                    }
                }
                else if(command.equals("returnRequest"))
                {
                    synchronized (Server.getCatalog())
                    {
                        synchronized (Server.getUsers())
                        {
                            User u = (User) ois.readObject(); // receive User object
                            Item i = (Item) ois.readObject(); // receive Item object
                            ArrayList<Integer> indices = (ArrayList<Integer>) ois.readObject(); // receive indices to remove from checked out copies

                            for (User user : Server.getUsers()) {
                                if (user.getUsername().equals(u.getUsername())) {
                                    Server.getUsers().remove(user);
                                    Server.getUsers().add(u); // update server version
                                    break;
                                }
                            }
                            ArrayList<Item> checkedOutCopies = u.getCurrentCheckedOutItem(i);
                            //manually replace the contents with references to server version
                            Item itemToUpdate = checkedOutCopies.get(0);
                            int size = checkedOutCopies.size();
                            checkedOutCopies.clear();
                            for (Item item : Server.getCatalog()) {
                                if (item.getTitle().equals(itemToUpdate.getTitle())) {
                                    for (int j = 0; j < size; j++)
                                        checkedOutCopies.add(item);
                                    break;
                                }
                            }

                            for (int index : indices) {
                                i = checkedOutCopies.get(index);
                                checkedOutCopies.get(index).returnItem(u);
                                u.return_CheckedOutItem(checkedOutCopies.get(index));
                            }

                            //update catalog version of the item
                            for (Item item : Server.getCatalog()) {
                                if (item.getTitle().equals(i.getTitle())) {
                                    int index = Server.getCatalog().indexOf(item);
                                    Server.getCatalog().set(index, i);
                                    break;
                                }
                            }

                            Server.saveUserSet(); //save users to disk
                            Server.saveCatalog(); // save catalog on disk;

                            //send back updated user
                            oos.reset();
                            oos.writeObject(u);
                            oos.flush();
                            System.out.println("returned item: " + i.getTitle());
                        }
                    }
                }
                else if(command.equals("getUpdatedItem"))
                {
                    synchronized (Server.getCatalog()) {
                        Item i = (Item) ois.readObject();
                        for (Item item : Server.getCatalog()) {
                            if (item.getTitle().equals(i.getTitle())) {
                                oos.reset();
                                oos.writeObject(item); // send item to client
                                oos.flush();
                                break;
                            }
                        }
                    }
                }
                else if(command.equals("resetPassword"))
                {
                    synchronized (Server.getUsers()) {
                        String username = (String) ois.readObject(); // receive username of the account to change
                        String newPass = (String) ois.readObject(); // receive the new password

                        for (User u : Server.getUsers()) {
                            if (u.getUsername().equals(username)) {
                                u.resetPassword(newPass);
                                Server.saveUserSet(); // save updated user set
                                System.out.println("reset password for: " + u.getUsername());
                                break;
                            }
                        }
                    }

                }
                else if(command.equals("getUpdatedUserSet"))
                {
                    synchronized (Server.getUsers()) {
                        oos.reset();
                        oos.writeObject(Server.getUsers()); // send updated user set
                        System.out.println("sent updated user set");
                        oos.flush();
                    }
                }
                else if(command.equals("updateItem"))
                {
                    synchronized (Server.getCatalog()) {
                        Item receivedItem = (Item) ois.readObject(); // receive item object
                        for (Item i : Server.getCatalog()) {
                            if (i.getTitle().equals(receivedItem.getTitle())) {
                                Server.getCatalog().remove(i);
                                Server.getCatalog().add(receivedItem);
                                Server.saveCatalog(); // save catalog to disk
                                System.out.println("updated item: " + receivedItem.getTitle());
                                break;
                            }
                        }
                    }
                }


                Server.saveUserSet(); //save users to disk
                Server.saveCatalog(); // save catalog on disk;
            }
        }
        catch(SocketException se)
        {
            if(se.getMessage().equals("Connection reset")) // client disconnected
            {
                Server.removeClientHandler(this); // remove this client socket from running list of connected sockets
                System.out.println("client disconnected");
            }
        }
        catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
    }
}

