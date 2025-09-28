package ClientGUI.yourAccount.requests;

import ClientGUI.loginScene.loginScene_controller;
import Users.ItemRequest;
import Users.Message;
import Users.User;
import finalProject_client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
public class requests_controller implements Initializable
{
    private User loggedInUser;

    @FXML Label welcomeText;
    @FXML Button signOutButton;
    @FXML Button yourAccountButton;
    @FXML Button goBackButton;
    @FXML VBox requestsVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loggedInUser = Client.getUpdatedUser_fromServer(loginScene_controller.getLoggedInUser().getUsername()); // get most up to date version of user from server
        welcomeText.setText("Welcome, " + loggedInUser.getUsername() + "!");

        //populate vbox with gridpanes representing requests
        for(ItemRequest ir : loggedInUser.getItemRequests())
        {
            AnchorPane irPane = createItemRequestPane(ir); //create item request grid pane
            requestsVBox.getChildren().add(irPane); //add to requests tab vbox
        }
    }

    /** creates a anchor pane representing a Item Request*/
    public AnchorPane createItemRequestPane(ItemRequest ir)
    {
        AnchorPane itemRequestPane = new AnchorPane();
        itemRequestPane.setPrefHeight(221.0);
        itemRequestPane.setPrefWidth(666.0);

        //item type
        Label itemTypeLabel = new Label(ir.getItemType());
        itemTypeLabel.setLayoutX(18.0);
        itemTypeLabel.setLayoutY(19.0);
        itemTypeLabel.setPrefHeight(47.0);
        itemTypeLabel.setPrefWidth(438.0);
        itemTypeLabel.setStyle("-fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        itemRequestPane.getChildren().add(itemTypeLabel);

        //item title
        Label itemTitleLabel = new Label(ir.getItemTitle());
        itemTitleLabel.setLayoutX(18.0);
        itemTitleLabel.setLayoutY(53.0);
        itemTitleLabel.setPrefHeight(47.0);
        itemTitleLabel.setPrefWidth(438.0);
        itemTitleLabel.setStyle("-fx-font-family: 'System Italic'; -fx-font-size: 24;");
        itemRequestPane.getChildren().add(itemTitleLabel);

        //status button
        Button statusButton = new Button("Pending");
        statusButton.setLayoutX(24.0);
        statusButton.setLayoutY(124.0);
        statusButton.setPrefHeight(55.0);
        statusButton.setPrefWidth(151.0);
        statusButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4; -fx-text-fill: black; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        statusButton.setDisable(true);


        if(ir.getStatus().equals("Accepted"))
        {
            statusButton.setText("ACCEPTED");
            statusButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3ad06c; -fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        }
        else if(ir.getStatus().equals("Denied"))
        {
            statusButton.setText("DENIED");
            statusButton.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d;-fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        }
        statusButton.setDisable(true);
        statusButton.setVisible(true);
        itemRequestPane.getChildren().add(statusButton);

        return itemRequestPane;
    }









    /** called when user clicks sign out button, switches back to login screen*/
    public void signOut(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/loginScene/loginScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Login");

            Stage currentStage = (Stage)welcomeText.getScene().getWindow();
            currentStage.close(); // close login screen

            //play sign out message
//            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/goodbye_darling.mp3").toString());
//            MediaPlayer player = new MediaPlayer(media);
//            player.setVolume(0.30);
//            player.play();

            stage.show(); // go back to login screen

        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** switches scene to home screen if user wants to go back*/
    public void switchToHomeScreen()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/homeScreen/homePageScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)goBackButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("jLib Home");
            stage.show();
        }
        catch(IOException ioe) { ioe.printStackTrace(); }
    }

    /** switches to your account scene*/
    public void switchYourAccountScene(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/yourAccountScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)yourAccountButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Your Account");
            stage.show();
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** switch to Reading List scene*/
    public void switchToReadingList(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/readingList/readingListScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)welcomeText.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Reading List");
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** switch to Friends scene*/
    public void switchToFriends(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/yourFriends/yourFriendsScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)welcomeText.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Friends");
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** switch to Friends scene*/
    public void switchToInbox(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/inbox/inboxScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)welcomeText.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Inbox");
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** switch to Your Items scene*/
    public void switchToYourItems(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/yourItems/yourItemsScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)welcomeText.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Your Items");
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }








    /** mouse hover effect for your account button */
    public void hoverOver_yourAccountButton() {
        yourAccountButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_yourAccountButton() {
        yourAccountButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for nevermind button */
    public void hoverOver_signOutButton() {
        signOutButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }
    public void hoverExit_signOutButton() {
        signOutButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }

    /** mouse hover effect for go back button */
    public void hoverOver_goBackButton() {
        goBackButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }

    public void hoverExit_goBackButton() {
        goBackButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }
}
