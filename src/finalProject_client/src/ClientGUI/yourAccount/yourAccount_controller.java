package ClientGUI.yourAccount;

import ClientGUI.loginScene.loginScene_controller;
import Users.User;
import finalProject_client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
public class yourAccount_controller implements Initializable
{
    private User loggedInUser;

    @FXML Label username;
    @FXML ImageView profilePicture;
    @FXML Hyperlink yourItemsLink;
    @FXML Hyperlink friendsLink;
    @FXML Hyperlink yourReviewsLink;
    @FXML Hyperlink inboxLink;
    @FXML Hyperlink submittedRequestsLink;
    @FXML Hyperlink readingListLink;
    @FXML Button changeProfilePictureButton;
    @FXML Button removeProfilePictureButton;
    @FXML Button resetPasswordButton;
    @FXML Button deleteAccountButton;

    @FXML Label welcomeText;
    @FXML Button goBackButton;
    @FXML Button signOutButton;
    @FXML Hyperlink yourItemsLinkTop;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loggedInUser = Client.getUpdatedUser_fromServer(loginScene_controller.getLoggedInUser().getUsername());
        username.setText(loggedInUser.getUsername());
        welcomeText.setText("Welcome, " + loggedInUser.getUsername() + "!");

        //set pfp
        if(loggedInUser.isHasPfp())
        {
            profilePicture.setImage(new Image(loggedInUser.getPfpImagePath()));
            removeProfilePictureButton.setVisible(true);
        }
        else
        {
            profilePicture.setImage(new Image("ClientGUI/Images/defaultProfilePicture.jpg")); // default if dont have a pfp
            removeProfilePictureButton.setVisible(false);
        }


    }

    /** switches to home screen */
    public void switchToHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/homeScreen/homePageScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)username.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("jLib Home");
            stage.show();
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** signs out and switches to login screen*/
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

    /** switch to Your Items scene*/
    public void switchToYourItems(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/yourItems/yourItemsScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)yourItemsLink.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Your Items");
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** updates profile picture, sets user pfp image path, updates user on server side */
    public void uploadPFP(ActionEvent event)
    {
        //open file chooser
        //grab image path
        //set imageview to show that image
        //set user image path
        //update user server side

        //open file chooser
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter // filter to image file types
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedImage = fileChooser.showOpenDialog(changeProfilePictureButton.getScene().getWindow());

        if(selectedImage != null)
        {
            try {
                String imagePath = selectedImage.toURL().toString();
                profilePicture.setImage(new Image(imagePath)); // update pfp
                loggedInUser.setPfpImagePath(imagePath); // update user's pfp image path
                Client.updateUser_serverSide(loggedInUser); // update user on server side
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            }
        }

        removeProfilePictureButton.setVisible(true);
    }

    /** resets user PFP to default */
    public void removePFP(ActionEvent event)
    {
        profilePicture.setImage(new Image("/ClientGUI/Images/defaultProfilePicture.jpg"));
        loggedInUser.resetPfp(); // update user's pfp image path to the default image path
        Client.updateUser_serverSide(loggedInUser); // update user on server side
        removeProfilePictureButton.setVisible(false);
    }

    /** switch to Reading List scene*/
    public void switchToReadingList(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/readingList/readingListScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)readingListLink.getScene().getWindow();
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

    /** switch to Inbox scene*/
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

    /** switches to reset pass scene*/
    public void resetPass(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/loginScene/resetPasswordScene/resetPasswordScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Reset Password");

            Stage currentStage = (Stage)welcomeText.getScene().getWindow();
            currentStage.close(); // close login screen

            //play sign out message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/goodbye_darling.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();

            stage.show(); // go back to login screen

        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** switches to your requests scene*/
    public void switchYourRequestsScene(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/requests/requestsScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)welcomeText.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Your Requests");
            stage.show();
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }














    /**
     * mouse hover effect for signout button
     */
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

    /** hover effect change profile picture button*/
    public void hoverOver_changePFPButton() {
        changeProfilePictureButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_changePFPButton() {
        changeProfilePictureButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for reset pass button */
    public void hoverOver_resetPassButton() {
        resetPasswordButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }

    public void hoverExit_resetPassButton() {
        resetPasswordButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }

    /** mouse hover effect for delete account button */
    public void hoverOver_deleteAccountButton() {
        deleteAccountButton.setStyle("-fx-background-radius: 50; -fx-background-color: #ED5E68");
    }

    public void hoverExit_deleteAccountButton() {
        deleteAccountButton.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d");
    }

    /** mouse hover effect for remove pfp button */
    public void hoverOver_removePFPButton() {
        removeProfilePictureButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }

    public void hoverExit_removePFPButton() {
        removeProfilePictureButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }







}
