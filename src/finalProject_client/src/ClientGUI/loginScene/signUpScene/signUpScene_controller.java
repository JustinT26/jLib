package ClientGUI.loginScene.signUpScene;

import java.net.URL;
import java.util.*;

import ClientGUI.ClientGUI;
import Users.Admin;
import Users.User;
import finalProject_client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;

public class signUpScene_controller implements Initializable
{
    @FXML TextField username;
    @FXML TextField password;
    @FXML TextField confirmedPassword;
    @FXML Button createAccountButton;
    @FXML Button nevermindButton;
    @FXML Label invalidUserText;
    @FXML Label invalidPasswordText;
    @FXML Label invalidConfirmedPassText;

    private HashSet<User> users;
    private Client client = ClientGUI.getClient();


    public void initialize(URL url, ResourceBundle resourceBundle) // do this when loading scene
    {
        users = Client.getUpdatedUserSet_fromServer();
        //prevent special characters, spaces, etc. from being entered
        username.addEventFilter(KeyEvent.KEY_TYPED, event ->
        {
            String keyTyped = event.getCharacter();

            if (!keyTyped.matches("[a-zA-Z0-9]") //if key typed is not alphanumeric or is a backspace/delete, "ignore" it
                    && !event.getCode().equals(KeyCode.BACK_SPACE)
                    && !event.getCode().equals(KeyCode.DELETE))
            {
                event.consume();
            }
        });

        password.addEventFilter(KeyEvent.KEY_TYPED, event ->
        {
            String keyTyped = event.getCharacter();

            if (!keyTyped.matches("[a-zA-Z0-9]") //if key typed is not alphanumeric or is a backspace/delete, "ignore" it
                    && !event.getCode().equals(KeyCode.BACK_SPACE)
                    && !event.getCode().equals(KeyCode.DELETE))
            {
                event.consume();
            }
        });

        confirmedPassword.addEventFilter(KeyEvent.KEY_TYPED, event ->
        {
            String keyTyped = event.getCharacter();

            if (!keyTyped.matches("[a-zA-Z0-9]") //if key typed is not alphanumeric or is a backspace/delete, "ignore" it
                    && !event.getCode().equals(KeyCode.BACK_SPACE)
                    && !event.getCode().equals(KeyCode.DELETE))
            {
                event.consume();
            }
        });

    }

    /** goes back to login screen*/
    public void createAccount(ActionEvent event)
    {
        //1. verify that username is atleast 2 chars
        //2. verify username is not already taken
        //3. verify pass is atleast 2 chars
        //4. verify confirmed pass == pass
        //5. if all satisfied, create new user, add user to userbase, switch to login scene

        if( username.getText().length() >= 2
            && client.isUniqueUser(username.getText())
            && password.getText().length() >= 2
            && confirmedPassword.getText().equals(password.getText()) )
        {
            //create admin account
            if(username.getText().equals("ADMIN"))
            {
                Admin adminAccount = new Admin(username.getText(), confirmedPassword.getText());
                users.add(adminAccount);
                client.addAdmin_serverSide(adminAccount);
            }
            //create regular user
            else
            {
                User newUser = new User(username.getText(), password.getText());
                users.add(newUser);
                client.addUser_serverSide(newUser); //update user base on server side
            }

            //play created account message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/new_account.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();


            returnToLoginScene(event); // return to login screen
        }
        else // invalid attempt to make an account
        {
            if(username.getText().length() < 2 )
            {
                invalidUserText.setVisible(true);
                invalidUserText.setText("must be atleast 2 characters");
            }
            if(password.getText().length() < 2)
                invalidPasswordText.setVisible(true);
            else if( !(confirmedPassword.getText().equals(password.getText())) )
                invalidConfirmedPassText.setVisible(true);
            if(!client.isUniqueUser(username.getText()))
            {
                invalidUserText.setText("Username is already taken");
                invalidUserText.setVisible(true);
            }

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
    }

    /** mouse hover effect for create account button */
    public void hoverOver_createAccountButton() { createAccountButton.setStyle("-fx-background-color: #4d8bff"); }
    public void hoverExit_createAccountButton() { createAccountButton.setStyle("-fx-background-color: #3d6fcd"); }

    /** mouse hover effect for nevermind button */
    public void hoverOver_nevermindButton() { nevermindButton.setStyle("-fx-background-color: #ffffff"); }
    public void hoverExit_nevermindButton() { nevermindButton.setStyle("-fx-background-color: #e4e4e4"); }
}
