package finalProject_server;

import Items.Book;
import Items.DVD;
import Items.Game;
import Items.Item;
import Users.Admin;
import Users.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.Gson;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class Server
{
    private static ArrayList<Item> catalog;
    private static HashSet<User> users;
    private static Admin admin;
    private static ArrayList<ClientHandler> connectedClients;


    public Server() {
        catalog = new ArrayList<>();
        users = new HashSet<>();
        connectedClients = new ArrayList<>();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start()
    {
        initialize(); // initialize catalog, users

        try
        {
            ServerSocket serverSocket = new ServerSocket(2525);
            System.out.println("server started");
            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                connectedClients.add(clientHandler);
                Thread t = new Thread(clientHandler);
                t.start();
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    /** initializes catalog and users upon startup */
    private void initialize()
    {
        if(!isCatalogInitialized())  //at very first startup, initialize catalog using initialCatalog.json data
        {
            catalog = loadCatalogFromJson("../finalProject_resources/src/CatalogResources/initialCatalog.json");
            for(Item item : catalog) // manually initialize certain fields that JSON/GSON can't
            {
                item.setNumAvailable(item.getQuantity());
                item.initializeLists();
            }
            users = new HashSet<>(); // empty set of users

            saveCatalog();//create initial catalog.ser file
            saveUserSet();//create initial users.ser file
        }
        else //anytime after that, load catalog and users from existing .ser files
        {
            try {
                File catalogFile = new File("catalog.ser");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(catalogFile));
                catalog = (ArrayList<Item>)ois.readObject();
            }
            catch(IOException | ClassNotFoundException e) { e.printStackTrace(); }

            try{
                File usersFile = new File("users.ser");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(usersFile));
                users = (HashSet<User>)ois.readObject();
                for(User u : users)
                {
                    if(u.getUsername().equals("ADMIN") && u instanceof Admin)
                        admin = (Admin)u;
                }
            }
            catch(IOException | ClassNotFoundException e) { e.printStackTrace(); }


        }
    }


    /**
     * Used to determine if need to initialize catalog using data from initialCatalog.json
     * Only want to initialize catalog from .json file at very first startup
     * @return true if catalog.ser exists, false otherwise
     */
    private boolean isCatalogInitialized() {
        File catalogFile = new File("catalog.ser");
        return catalogFile.exists();
    }

    /**
     * Used to initialize the catalog using initialCatalog.json
     * @return ArrayList<Item> containing initialized Item objects using data from .json
     */
    private ArrayList<Item> loadCatalogFromJson(String filename) {
        Gson g = new Gson();
        try
        {
            FileReader reader = new FileReader(filename);
            JsonArray jsonArray = g.fromJson(reader, JsonArray.class);
            ArrayList<Item> result = new ArrayList<>();
            for (JsonElement jsonElement : jsonArray)
            {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
            String itemType = jsonObject.get("itemType").getAsString();

                if(itemType.equalsIgnoreCase("Book"))
                    result.add(g.fromJson(jsonElement, Book.class));
                else if(itemType.equalsIgnoreCase("DVD"))
                    result.add(g.fromJson(jsonElement, DVD.class));
                else if(itemType.equalsIgnoreCase("Game"))
                    result.add(g.fromJson(jsonElement, Game.class));
            }
            return result;
        }
        catch (FileNotFoundException fnfe) { return null; }
    }

    /** save catalog to .ser file*/
    public static void saveCatalog()
    {
        try {
            File f = new File("catalog.ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(catalog);
            oos.flush();
        }
        catch(IOException e) { e.printStackTrace(); }
    }

    /** save userset to .ser file*/
    public static void saveUserSet()
    {
        try {
            File f = new File("users.ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(users);
            oos.flush();
        }
        catch(IOException e) { e.printStackTrace(); }
    }


    public static void removeClientHandler(ClientHandler clientHandler) { connectedClients.remove(clientHandler); }


    /** getters */
    public static ArrayList<Item> getCatalog() { return catalog; }
    public static HashSet<User> getUsers() { return users; }
    public static void setAdmin(Admin a) { admin = a; }
    public static Admin getAdmin() { return admin; }

}



