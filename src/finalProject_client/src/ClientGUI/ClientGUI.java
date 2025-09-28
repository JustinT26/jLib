package ClientGUI;

import ClientGUI.loginScene.loginScene_controller;
import Items.Item;
import Users.User;
import finalProject_client.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;

public class ClientGUI extends Application
{

    private static Client client;
    public static Stage applicationStage;


    @Override
    public void start(Stage applicationStage)
    {
        this.applicationStage = applicationStage;
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/loginScene/loginScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            applicationStage.setScene(scene);
            applicationStage.setTitle("Login");

            // //create a Client, start it, receive userbase from server
            client = new Client();
            client.start();

            applicationStage.show(); // show login screen
        }
        catch(Exception e) { e.printStackTrace(); };
    }

    /** getters */

    public static Client getClient() { return client; }

    public static void main(String[] args) {
        launch(args); // Launch application
    }
}

