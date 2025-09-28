package ClientGUI.yourAccount.readingList;

import ClientGUI.loginScene.loginScene_controller;
import Items.Item;
import Users.User;
import finalProject_client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

public class readingList_controller implements Initializable
{
    private User loggedInUser;
    private static Item itemToView;

    @FXML Label welcomeText;
    @FXML Button signOutButton;
    @FXML Button yourAccountButton;
    @FXML ScrollPane scrollPane;
    @FXML VBox userItemsVBox;
    @FXML Button goBackButton;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        userItemsVBox.setSpacing(20);
        itemToView = null;

        loggedInUser = Client.getUpdatedUser_fromServer(loginScene_controller.getLoggedInUser().getUsername()); // get most up to date version of user from server
        welcomeText.setText("Welcome, " + loggedInUser.getUsername() + "!");

        if(loggedInUser.getReadingList().isEmpty()) // empty reading list
            scrollPane.setVisible(false);
        else
        {
            ArrayList<Item> readingList = new ArrayList<>(loggedInUser.getReadingList());
            //set the VBox children anchorpanes with corresponding info
            for(int i = 0; i < readingList.size(); i++)
            {
                AnchorPane newResultPane = ((AnchorPane)userItemsVBox.getChildren().get(i));
                newResultPane.setVisible(true);

                //set the info
                Item item = Client.getUpdatedItem_fromServer(readingList.get(i));
                ((Label)newResultPane.getChildren().get(0)).setText(item.getTitle()); //set title
                ((Label)newResultPane.getChildren().get(1)).setText("" + item.getAverageRating());//set average rating
                ((Label)newResultPane.getChildren().get(2)).setText("" + item.getPublishedYear());//set year
                ((Label)newResultPane.getChildren().get(3)).setText(item.getItemType());//set type
                ((ImageView)newResultPane.getChildren().get(4)).setImage(new Image(item.getImagePath())); // set image

                ((Button)newResultPane.getChildren().get(5)).setOnAction(e -> // set view button action
                {
                    //find the item to view
                    itemToView = item;

                    //switch to view item scene
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/viewItem/viewItemScene.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage)scrollPane.getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle(item.getTitle());
                    }
                    catch (IOException ioe) { ioe.printStackTrace(); }
                });

                // set view button hover effect
                ((Button)newResultPane.getChildren().get(5)).setOnMouseEntered(e ->
                {
                    ((Button)newResultPane.getChildren().get(5)).setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
                });
                ((Button)newResultPane.getChildren().get(5)).setOnMouseExited(e ->
                {
                    ((Button)newResultPane.getChildren().get(5)).setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
                });

                userItemsVBox.getChildren().set(i, newResultPane); // update the vbox
            }

            // remove the remaining extra uneeded anchorpanes frrom vbox (because i initially have it populated with placeholder anchorpanes)
            userItemsVBox.getChildren().remove(readingList.size(), userItemsVBox.getChildren().size());
        }

    }

    /** called when user clicks sign out button, switches back to login screen*/
    public void signOut(ActionEvent event)
    {
        loggedInUser = null;
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

    public static Item getItemToView() { return itemToView; }
    public static void resetItemToView() { itemToView = null; }












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
