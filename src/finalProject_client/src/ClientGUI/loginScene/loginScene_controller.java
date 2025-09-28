package ClientGUI.loginScene;

import java.io.IOException;
import java.util.*;

import ClientGUI.ClientGUI;
import finalProject_client.Client;
import Users.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class loginScene_controller
{
    private HashSet<User> users;
    private static User loggedInUser;

    @FXML TextField username;
    @FXML TextField password;
    @FXML Hyperlink signUpButton;
    @FXML Button loginButton;
    @FXML Label invalidLoginText;
    @FXML AnchorPane background;
    @FXML Hyperlink forgotPassword;



    /** to handle Login button */
    public void login(ActionEvent event)
    {
        users = Client.getUpdatedUserSet_fromServer();
        invalidLoginText.setVisible(false);
        forgotPassword.setVisible(false);
        boolean successLogIn = false;
        boolean incorrectPassword = false;

        if( username.getText() != null
            && password.getText() != null
            && users != null) {
            if (Client.getAdmin_fromServer() != null) {
                if (username.getText().equals("ADMIN")
                        && password.getText().equals(Client.getAdmin_fromServer().getPassword())) {
                    loggedInUser = Client.getAdmin_fromServer();
                    successLogIn = true;
                    //TODO: play welcome message for admin
                    switchToAdminGUI();
                } else if (username.getText().equals("ADMIN") // username found, incorrect password
                        && !password.getText().equals(Client.getAdmin_fromServer().getPassword())) {
                    incorrectPassword = true;
                }
            }
            if (!username.getText().equals("ADMIN"))
            {
                for (User user : users) {
                    if (user.getUsername().equals(username.getText())
                            && user.getPassword().equals(password.getText())) {
                        System.out.println("logged in: " + user.getUsername());
                        loggedInUser = Client.getUpdatedUser_fromServer(user.getUsername());
                        successLogIn = true;

                        //play welcome message
//                        Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/welcome_jlib_darling2.mp3").toString());
//                        MediaPlayer player = new MediaPlayer(media);
//                        player.setVolume(0.30);
//                        player.play();

                        switchToHomeScreen(); // switch to home page

                    } else if (user.getUsername().equals(username.getText()) // username found, incorrect password
                            && !user.getPassword().equals(password.getText())) {
                        forgotPassword.setVisible(true); // display button
                        incorrectPassword = true;
                    }
                }
            }
        }

        if(!successLogIn)  //at this point, login failed, prompt user to try again
        {
            username.clear();
            password.clear();
            if(incorrectPassword)
                invalidLoginText.setText("Incorrect Password"); //ask to reset password
            else
                invalidLoginText.setText("Invalid login, please try again");
            invalidLoginText.setVisible(true);
        }
    }

    /** to handle Sign Up button*/
    public void signUp(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/loginScene/signUpScene/signUpScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();
        }
        catch(IOException ioe) { ioe.printStackTrace(); }
    }

    /** hides invalid login text when reentering info*/
    public void enteringInfo() {
        forgotPassword.setVisible(false);
        invalidLoginText.setVisible(false);
    }

    /** switches scene to home screen after user logs in*/
    public void switchToHomeScreen()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/homeScreen/homePageScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("jLib Home");

            Stage currentStage = (Stage)background.getScene().getWindow();
            currentStage.close(); // close login screen

            stage.show(); // open home page
        }
        catch(IOException ioe) { ioe.printStackTrace(); }
    }

    /** switch to reset password scene */
    public void switchToResetPassword(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/loginScene/resetPasswordScene/resetPasswordScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Reset Password");
            stage.show();
        }
        catch(IOException ioe) { ioe.printStackTrace(); }
    }

    /** displays admin GUI for admin login*/
    public void switchToAdminGUI()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/adminGUI/adminGUI_scene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("jLib ADMIN");

            Stage currentStage = (Stage)background.getScene().getWindow();
            currentStage.close(); // close login screen

            stage.show(); // open home page
        }
        catch(IOException ioe) { ioe.printStackTrace(); }
    }
















    /** mouse hover effect for login button */
    public void hoverOver_loginButton() { loginButton.setStyle("-fx-background-color: #4d8bff"); }
    public void hoverExit_loginButton() { loginButton.setStyle("-fx-background-color: #3d6fcd"); }

    /** mouse hover effect for sign up button */
    public void hoverOver_signUpButton() { signUpButton.setTextFill(Color.rgb(77, 139, 255)); }
    public void hoverExit_signUpButton() { signUpButton.setTextFill(Color.rgb(61, 111, 205)); }

    /** mouse hover effect for forgot password? button */
    public void hoverOver_forgotPasswordButton() { forgotPassword.setTextFill(Color.rgb(77, 139, 255)); }
    public void hoverExit_forgotPasswordButton() { forgotPassword.setTextFill(Color.rgb(61, 111, 205)); }

    /** getter */
    public static User getLoggedInUser() { return loggedInUser; }
    public static void updateLoggedInUser(User u) { loggedInUser = u; }

}
