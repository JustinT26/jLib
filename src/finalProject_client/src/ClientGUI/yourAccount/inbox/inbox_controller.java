package ClientGUI.yourAccount.inbox;

import ClientGUI.loginScene.loginScene_controller;
import ClientGUI.yourAccount.yourFriends.yourFriends_controller;
import Users.Message;
import Users.User;
import finalProject_client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class inbox_controller implements Initializable
{
    private User loggedInUser;
    @FXML Label welcomeText;
    @FXML Button signOutButton;
    @FXML Button yourAccountButton;
    @FXML Button goBackButton;

    @FXML Button inboxTab_Button;
    @FXML ScrollPane inboxPane;
    @FXML VBox inboxVbox;

    @FXML Button sendMsgTab_Button;
    @FXML AnchorPane sendMsgPane;
    @FXML MenuButton sendMsgTo_Menu;
    @FXML TextField messageBox;
    @FXML Button sendMsg;
    @FXML Label sentLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loggedInUser = Client.getUpdatedUser_fromServer(loginScene_controller.getLoggedInUser().getUsername());
        welcomeText.setText("Welcome, " + loggedInUser.getUsername() + "!");

        //populate inbox vbox with user's inbox, sort newest first - selection sort
        ArrayList<Message> sortedInbox = new ArrayList<>(loggedInUser.getInbox());

        for(int i = 0; i < sortedInbox.size(); i++)
        {
            int maxIndex = i;
            for(int j = i + 1; j < sortedInbox.size() - 1; j++)
            {
                if(sortedInbox.get(j).getTime().compareTo(sortedInbox.get(maxIndex).getTime()) > 0)
                    maxIndex = j;
            }

            Message temp = sortedInbox.get(i);
            sortedInbox.set(i, sortedInbox.get(maxIndex));
            sortedInbox.set(maxIndex, temp);
        }

        for(Message m : sortedInbox)
        {
            //create new grid pane, add to inbox vbox
            GridPane msgGridPane = createMessageGridPane(m);
            inboxVbox.getChildren().add(msgGridPane);
        }

        //populate send message to drop down with the user's friends
        for(User friend : loggedInUser.getFriends())
        {
            MenuItem option = new MenuItem();
            option.setText(friend.getUsername());
            option.setOnAction(e ->
            {
                choseRecipient(e);
            });
            sendMsgTo_Menu.getItems().add(option);
        }

        //if came here from Your Friends scene (user wants to send msg to a specific friend)
        if(yourFriends_controller.getSendMsgTo() != null)
        {
            chosenRecipient = yourFriends_controller.getSendMsgTo();
            //start with send msg tab opened
            inboxPane.setVisible(false);
            isInboxTabSelected = false;
            inboxTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");

            isSendMsgTabSelected = true;
            sendMsgPane.setVisible(true);
            sendMsgTab_Button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");

            sendMsgTo_Menu.setText("Send To: " + chosenRecipient);
            if(chosenRecipient.equals("ADMIN"))
            {
                sendMsgTo_Menu.setTextFill(Color.rgb(0xff, 0x00, 0x00));
                sendMsgTo_Menu.setStyle("-fx-font-weight: bold");
            }
            yourFriends_controller.resetSendMsgTo();
        }
    }

    private GridPane createMessageGridPane(Message m)
    {
        User from = Client.getUpdatedUser_fromServer(m.getFrom().getUsername());
        GridPane gridPane = new GridPane();
        gridPane.setPrefHeight(161);
        gridPane.setPrefWidth(654);

        // Column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        col1.setMaxWidth(330.0);
        col1.setMinWidth(10.0);
        col1.setPrefWidth(179.0);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        col2.setMaxWidth(529.0);
        col2.setMinWidth(10.0);
        col2.setPrefWidth(495.0);

        gridPane.getColumnConstraints().addAll(col1, col2);

        // Row constraints
        RowConstraints row1 = new RowConstraints();
        row1.setMaxHeight(170);
        row1.setMinHeight(0.0);
        row1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        gridPane.getRowConstraints().addAll(row1);

        // set from user image
        ImageView imageView = new ImageView(new Image(from.getPfpImagePath()));
        if(from.getUsername().equals("ADMIN"))
            imageView = new ImageView(new Image("/ClientGUI/Images/adminProfilePicture.jpg"));
        imageView.setFitHeight(170.0);
        imageView.setFitWidth(170.0);
        gridPane.add(imageView, 0, 0);

        // from user label
        Label usernameLabel = new Label("From: " + from.getUsername());
        usernameLabel.setFont(new Font("Ebrima Bold", 24.0));
        if(from.getUsername().equals("ADMIN"))
        {
            usernameLabel.setEffect(new Glow());
            usernameLabel.setStyle("-fx-text-fill: RED; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 36");
        }

        usernameLabel.setPrefHeight(49);
        usernameLabel.setPrefWidth(285);
        GridPane.setMargin(usernameLabel, new Insets(0, 0, 100.0, 20.0));
        gridPane.add(usernameLabel, 1, 0);

        // reply button
        Button replyButton = new Button("Reply");
        replyButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd;");
        replyButton.setTextFill(javafx.scene.paint.Color.WHITE);
        replyButton.setFont(new Font("Ebrima Bold", 14.0));
        replyButton.setPrefHeight(31);
        replyButton.setPrefWidth(144);

        replyButton.setOnAction(e ->
        {
            switchTo_sendMsgTab(e);
            chosenRecipient = from.getUsername();
            sendMsgTo_Menu.setText("Send To: " + chosenRecipient);
        });

        // hover effect
        replyButton.setOnMouseEntered(e ->
        {
            replyButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
        });
        replyButton.setOnMouseExited(e ->
        {
            replyButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
        });


        GridPane.setMargin(replyButton, new Insets(0, 0, -130, 300.0));
        gridPane.add(replyButton, 1, 0);

        // date label
        Label dateTimeLabel = new Label(m.getTime());
        dateTimeLabel.setFont(new Font("System Italic", 16.0));
        dateTimeLabel.setPrefWidth(165);
        dateTimeLabel.setPrefHeight(25);
        GridPane.setMargin(dateTimeLabel, new Insets(0, 0, 100.0, 305.0));
        gridPane.add(dateTimeLabel, 1, 0);

        // message label
        Label messageLabel = new Label(m.getMessage());
        messageLabel.setFont(new Font(18.0));
        messageLabel.setPrefHeight(63);
        messageLabel.setPrefWidth(446);
        messageLabel.setWrapText(true);
        GridPane.setMargin(messageLabel, new Insets(0, 0, -20, 20.0));
        gridPane.add(messageLabel, 1, 0);

        return gridPane;
    }

    /** switches tab to send msg */
    boolean isSendMsgTabSelected = false;
    public void switchTo_sendMsgTab(ActionEvent event)
    {
        sendMsgTo_Menu.setText("Send To: ");
        messageBox.clear();
        sentLabel.setVisible(false);
        isSendMsgTabSelected = true;
        sendMsgPane.setVisible(true);
        sendMsgTab_Button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");

        isInboxTabSelected = false;
        inboxPane.setVisible(false);
        inboxTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** switches tab to inbox */
    boolean isInboxTabSelected = true;
    public void switchTo_inboxTab(ActionEvent event)
    {
        isInboxTabSelected = true;
        inboxTab_Button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");
        inboxPane.setVisible(true);

        isSendMsgTabSelected = false;
        sendMsgPane.setVisible(false);
        sendMsgTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** updates dropdown menu text to show selected recipient*/
    String chosenRecipient;
    public void choseRecipient(ActionEvent event)
    {
        chosenRecipient = ((MenuItem)event.getSource()).getText();
        sendMsgTo_Menu.setText("Send To: " + chosenRecipient);
        if(chosenRecipient.equals("ADMIN"))
        {
            sendMsgTo_Menu.setTextFill(Color.rgb(0xff, 0x00, 0x00));
            sendMsgTo_Menu.setStyle("-fx-font-weight: bold;-fx-background-radius: 50; -fx-background-color: #e4e4e4");
        }
        else
        {
            sendMsgTo_Menu.setTextFill(Color.BLACK);
            sendMsgTo_Menu.setStyle("-fx-font-weight: 0;-fx-background-radius: 50; -fx-background-color: #e4e4e4");
        }
    }

    /** enables/disables send message button if user typed a message */
    public void userTypingMsg()
    {
        //disable if didn't type anything. otherwise enable it
        sendMsg.setDisable(messageBox.getText().trim().length() < 1);
    }


    /** creates message object and sends to recipient's inbox */
    public void sendMessageToRecipient(ActionEvent event)
    {
        if(sendMsgTo_Menu.getText().equals("Send To: "))
            return; // didn't choose a recipient

        for(User u : Client.getUpdatedUserSet_fromServer())
        {
            if(u.getUsername().equals(chosenRecipient))
            {
                Message m = new Message(loggedInUser, u, messageBox.getText().trim()); // create msg
                u.receiveMessage(m); // send msg to recipient
                System.out.println("sent message to: " + u.getUsername());
                Client.updateUser_serverSide(u); // update recipient user on server side

                //play sent item request message
                Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/sentMessage.mp3").toString());
                MediaPlayer player = new MediaPlayer(media);
                player.setVolume(0.30);
                player.play();

                break;
            }
        }
        sentLabel.setVisible(true);
        messageBox.clear();
        sendMsg.setDisable(true);
        sendMsgTo_Menu.setText("Send To: ");

        //play sent message sound effect


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

    /** mouse hover effect for inbox tab button */
    public void hoverOver_inboxTabButton() {
        if (!isInboxTabSelected)
            inboxTab_Button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_inboxTabButton() {
        if (!isInboxTabSelected)
            inboxTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for send msg tab button */
    public void hoverOver_sendMsgTabButton() {
        if (!isSendMsgTabSelected)
            sendMsgTab_Button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_sendMsgTabButton() {
        if (!isSendMsgTabSelected)
            sendMsgTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for send message button */
    public void hoverOver_sendMsgButton() {
        sendMsg.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_sendMsgButton() {
        sendMsg.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }
}
