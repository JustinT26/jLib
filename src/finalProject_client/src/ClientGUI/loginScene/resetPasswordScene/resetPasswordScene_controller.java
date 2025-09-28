package ClientGUI.loginScene.resetPasswordScene;

import Users.User;
import finalProject_client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class resetPasswordScene_controller
{
    private User user;
    @FXML TextField username;
    @FXML TextField password;
    @FXML TextField confirmedPassword;
    @FXML Button resetPasswordButton;
    @FXML Button nevermindButton;
    @FXML Label invalidUserText;
    @FXML Label invalidPasswordText;
    @FXML Label invalidConfirmedPassText;


    /** resets password, returns to login screen*/
    public void resetPassword(ActionEvent event)
    {
        //1. verify that username belongs to a account
        //2. verify pass is atleast 2 chars
        //3. verify confirmed pass == pass
        //4. if all satisfied, change password, update user on server side, switch to login scene

        boolean foundUser = false;
        for(User u : Client.getUpdatedUserSet_fromServer())
        {
            if(u.getUsername().equals(username.getText()))
                foundUser = true; break;
        }

        if( foundUser
                && password.getText().length() >= 2
                && confirmedPassword.getText().equals(password.getText()) )
        {
            //change password
            Client.resetPassword_serverSide(username.getText(), password.getText());

            //TODO: play reset account message
//            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/new_account.mp3").toString());
//            MediaPlayer player = new MediaPlayer(media);
//            player.setVolume(0.15);
//            player.play();

            returnToLoginScene(event); // return to login screen
        }
        else // error; username not found or password doesn't meet criteria
        {
            if(!foundUser)
                invalidUserText.setVisible(true);

            if(password.getText().length() < 2)
                invalidPasswordText.setVisible(true);
            else if( !(confirmedPassword.getText().equals(password.getText())) )
                invalidConfirmedPassText.setVisible(true);
        }
    }



    /** return to login scene */
    public void returnToLoginScene(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/loginScene/loginScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");

            stage.show();
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** hides invalid info text when reentering info*/
    public void enteringInfo() {
        invalidUserText.setVisible(false);
        invalidPasswordText.setVisible(false);
        invalidConfirmedPassText.setVisible(false);
        resetPasswordButton.setDisable( !(confirmedPassword.getText().trim().length() >= 1) );
    }

    /** mouse hover effect for create account button */
    public void hoverOver_resetPasswordButton() { resetPasswordButton.setStyle("-fx-background-color: #4d8bff"); }
    public void hoverExit_resetPasswordButton() { resetPasswordButton.setStyle("-fx-background-color: #3d6fcd"); }

    /** mouse hover effect for nevermind button */
    public void hoverOver_nevermindButton() { nevermindButton.setStyle("-fx-background-color: #ffffff"); }
    public void hoverExit_nevermindButton() { nevermindButton.setStyle("-fx-background-color: #e4e4e4"); }
}
