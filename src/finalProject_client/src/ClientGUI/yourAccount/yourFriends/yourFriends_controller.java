package ClientGUI.yourAccount.yourFriends;

import ClientGUI.loginScene.loginScene_controller;
import Items.Item;
import Users.FriendRequest;
import Users.User;
import finalProject_client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
public class yourFriends_controller implements Initializable
{
    private User loggedInUser;

    @FXML Label welcomeText;
    @FXML Button signOutButton;
    @FXML Button yourAccountButton;
    @FXML Button goBackButton;

    @FXML Button friendsTab_Button;
    @FXML Button sentRequestsTab_Button;
    @FXML Button receivedRequestsTab_Button;

    @FXML ScrollPane friendsTab_Pane;
    @FXML ScrollPane sentRequestsTab_Pane;
    @FXML ScrollPane receivedRequestsTab_Pane;

    @FXML Button adminSendMsgButton;
    @FXML VBox friends_VBox;
    @FXML VBox sentRequests_VBox;
    @FXML VBox receivedRequests_VBox;

    @FXML AnchorPane findUserSearch_Pane;
    @FXML TextField findUser_Text;
    @FXML Button sendRequest_Button;
    @FXML Label userNotFound_Label;

    private static String sendMsgTo; // if user wants to send msg to a friend, store the friend's user here



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loggedInUser = Client.getUpdatedUser_fromServer(loginScene_controller.getLoggedInUser().getUsername());

        welcomeText.setText("Welcome, " + loggedInUser.getUsername() + "!");

        //populate friends vbox
        for(User u : loggedInUser.getFriends())
        {
            GridPane friendGridpane = createFriendGridPane(Client.getUpdatedUser_fromServer(u.getUsername()), "friendsTab"); //create a gridpane representing the friend info
            friends_VBox.getChildren().add(friendGridpane); // add to friends vbox
        }

        //populate sent friend requests vbox
        for(FriendRequest fr : loggedInUser.getSentFriendRequests())
        {
            GridPane requestToGridPane = createFriendGridPane(Client.getUpdatedUser_fromServer(fr.getRequestTo().getUsername()), "sentRequestsTab"); // create gridpane representing the person sent to
            sentRequests_VBox.getChildren().add(requestToGridPane); // add to sent requests vbox
        }

        //populate received friend requests vbox
        for(FriendRequest fr : loggedInUser.getReceivedFriendRequests())
        {
            GridPane receivedFromGridPane = createFriendGridPane(Client.getUpdatedUser_fromServer(fr.getRequestFrom().getUsername()), "receivedRequestsTab"); // create gridpane representing the person received from
            receivedRequests_VBox.getChildren().add(receivedFromGridPane); // add to received to vbox
        }
    }

    boolean isFriendsSelected = true; // initially display friends tab
    /** switch tabs to display friends*/
    public void switchTo_friendsTab(ActionEvent event)
    {
        if(!isFriendsSelected)
        {
            friendsTab_Button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");
            isFriendsSelected = true;
            friendsTab_Pane.setVisible(true);

            sentRequestsTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            receivedRequestsTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            isSentSelected = false;
            isReceivedSelected = false;
            sentRequestsTab_Pane.setVisible(false);
            receivedRequestsTab_Pane.setVisible(false);

            findUserSearch_Pane.setVisible(false);
            userNotFound_Label.setVisible(false);
        }
    }

    boolean isSentSelected = false;
    /** switch tabs to display sent requests*/
    public void switchTo_sentTab(ActionEvent event)
    {
        if(!isSentSelected)
        {
            findUser_Text.clear();
            sendRequest_Button.setDisable(true);
            findUserSearch_Pane.setVisible(true);
            userNotFound_Label.setVisible(false);
            sentRequestsTab_Button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");
            isSentSelected = true;
            sentRequestsTab_Pane.setVisible(true);


            friendsTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            receivedRequestsTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            isFriendsSelected = false;
            isReceivedSelected = false;
            friendsTab_Pane.setVisible(false);
            receivedRequestsTab_Pane.setVisible(false);
        }
    }

    boolean isReceivedSelected = false;
    /** switch tabs to display received requests*/
    public void switchTo_receivedTab(ActionEvent event)
    {
        if(!isReceivedSelected)
        {
            receivedRequestsTab_Button.setStyle("-fx-background-color: #f2ca36");
            isReceivedSelected = true;
            receivedRequestsTab_Pane.setVisible(true);

            friendsTab_Button.setStyle("-fx-background-color: #e4e4e4");
            sentRequestsTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            isFriendsSelected = false;
            isSentSelected = false;
            friendsTab_Pane.setVisible(false);
            sentRequestsTab_Pane.setVisible(false);

            findUserSearch_Pane.setVisible(false);
            userNotFound_Label.setVisible(false);
        }
    }

    private GridPane createFriendGridPane(User friend, String tab)
    {
        GridPane gridPane = new GridPane();

        // set column constraints
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
        row1.setMaxHeight(149.0);
        row1.setMinHeight(0.0);
        row1.setPrefHeight(119.0);
        row1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        gridPane.getRowConstraints().addAll(row1);

        //profile picture
        ImageView imageView = new ImageView(new Image(friend.getPfpImagePath()));
        imageView.setFitHeight(170.0);
        imageView.setFitWidth(170.0);
        GridPane.setRowIndex(imageView, 0);
        gridPane.getChildren().add(imageView);

        // username label
        Label usernameLabel = new Label(friend.getUsername());
        usernameLabel.setFont(new javafx.scene.text.Font("Ebrima Bold", 36.0));
        GridPane.setColumnIndex(usernameLabel, 1);
        GridPane.setRowIndex(usernameLabel, 0);
        GridPane.setMargin(usernameLabel, new Insets(0, 0, 100.0, 20.0));
        gridPane.getChildren().add(usernameLabel);

        if(tab.equals("friendsTab")) // create "Send Message" and "Remove Friend" buttons
        {
            // Send Message Button
            Button sendMessageButton = new Button("Send Message");
            sendMessageButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd;");
            sendMessageButton.setTextFill(javafx.scene.paint.Color.WHITE);
            sendMessageButton.setFont(new javafx.scene.text.Font("Ebrima Bold", 14.0));
            sendMessageButton.setPrefWidth(144);
            sendMessageButton.setPrefHeight(31);

            sendMessageButton.setOnAction(e ->
            {
                sendMsgTo = friend.getUsername();
                switchToInbox(e);
            });

            //hover effect
            sendMessageButton.setOnMouseEntered(e ->
            {
                sendMessageButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
            });
            sendMessageButton.setOnMouseExited(e ->
            {
                sendMessageButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
            });

            GridPane.setColumnIndex(sendMessageButton, 1);
            GridPane.setRowIndex(sendMessageButton, 0);
            GridPane.setMargin(sendMessageButton, new Insets(0, 0, -25, 20));
            gridPane.getChildren().add(sendMessageButton);

            // Remove Friend Button
            Button removeFriendButton = new Button("Remove Friend");
            removeFriendButton.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d;");
            removeFriendButton.setTextFill(javafx.scene.paint.Color.WHITE);
            removeFriendButton.setFont(new javafx.scene.text.Font("Ebrima Bold", 14.0));
            removeFriendButton.setPrefWidth(144);
            removeFriendButton.setPrefHeight(31);

            removeFriendButton.setOnAction(e ->
            {
                //play unfriended message
                Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/unfriended.mp3").toString());
                MediaPlayer player = new MediaPlayer(media);
                player.setVolume(0.30);
                player.play();

                loggedInUser.removeFriend(friend);
                friend.removeFriend(loggedInUser);
                Client.updateUser_serverSide(loggedInUser); // update user on server side
                Client.updateUser_serverSide(friend); //update friend on serverSide

                //remove this gridpane from friends tab
                for (Iterator<Node> iterator = friends_VBox.getChildren().iterator(); iterator.hasNext();) {
                    Node n = iterator.next();
                    if (n instanceof GridPane) {
                        //if the friend gridpane matches the username of the friend we want to remove
                        if (((Label) ((GridPane) n).getChildren().get(1)).getText().equals(friend.getUsername()))
                            iterator.remove();
                    }
                }
            });

            //hover effect
            removeFriendButton.setOnMouseEntered(e ->
            {
                removeFriendButton.setStyle("-fx-background-radius: 50; -fx-background-color: #ED5E68");
            });
            removeFriendButton.setOnMouseExited(e ->
            {
                removeFriendButton.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d");
            });

            GridPane.setColumnIndex(removeFriendButton, 1);
            GridPane.setRowIndex(removeFriendButton, 0);
            GridPane.setMargin(removeFriendButton, new Insets(0, 0, -25, 210.0));
            gridPane.getChildren().add(removeFriendButton);
        }
        else if(tab.equals("sentRequestsTab")) // create a "Pending" button; disabled
        {
            Button pendingButton = new Button("Pending");
            pendingButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4;");
            pendingButton.setTextFill(javafx.scene.paint.Color.BLACK);
            pendingButton.setFont(new javafx.scene.text.Font("Ebrima Bold", 14.0));
            pendingButton.setPrefWidth(144);
            pendingButton.setPrefHeight(31);
            pendingButton.setDisable(true);

            GridPane.setColumnIndex(pendingButton, 1);
            GridPane.setRowIndex(pendingButton, 0);
            GridPane.setMargin(pendingButton, new Insets(0, 0, -25, 20.0));
            gridPane.getChildren().add(pendingButton);
        }
        else if(tab.equals("receivedRequestsTab")) // create "Accept" and "Deny" buttons
        {
            // Accept Button
            Button acceptButton = new Button("Accept");
            acceptButton.setStyle("-fx-background-radius: 50; -fx-background-color: #30ae5a;");
            acceptButton.setTextFill(javafx.scene.paint.Color.WHITE);
            acceptButton.setFont(new javafx.scene.text.Font("Ebrima Bold", 14.0));
            acceptButton.setPrefWidth(144);
            acceptButton.setPrefHeight(31);

            acceptButton.setOnAction(e ->
            {
                //play accepted request  message
                Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/acceptedFriendRequest.mp3").toString());
                MediaPlayer player = new MediaPlayer(media);
                player.setVolume(0.30);
                player.play();

                loggedInUser.addFriend(friend);
                friend.addFriend(loggedInUser);
                loggedInUser.removeFromReceivedRequests(friend);
                friend.removeFromSentRequests(loggedInUser);

                Client.updateUser_serverSide(loggedInUser);
                Client.updateUser_serverSide(friend);

                GridPane newFriend = createFriendGridPane(friend, "friendsTab");
                friends_VBox.getChildren().add(newFriend);

                //remove this gridpane from received requests tab
                for (Iterator<Node> iterator = receivedRequests_VBox.getChildren().iterator(); iterator.hasNext();) {
                    Node n = iterator.next();
                    if (n instanceof GridPane) {
                        //if the friend gridpane matches the username of the friend we want to remove
                        if (((Label) ((GridPane) n).getChildren().get(1)).getText().equals(friend.getUsername()))
                            iterator.remove();
                    }
                }
            });

            //hover effect
            acceptButton.setOnMouseEntered(e ->
            {
                acceptButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3ad06c");
            });
            acceptButton.setOnMouseExited(e ->
            {
                acceptButton.setStyle("-fx-background-radius: 50; -fx-background-color: #30ae5a");
            });

            GridPane.setColumnIndex(acceptButton, 1);
            GridPane.setRowIndex(acceptButton, 0);
            GridPane.setMargin(acceptButton, new Insets(0, 0, -25, 20.0));
            gridPane.getChildren().add(acceptButton);

            // Deny Button
            Button denyButton = new Button("Deny");
            denyButton.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d;");
            denyButton.setTextFill(javafx.scene.paint.Color.WHITE);
            denyButton.setFont(new javafx.scene.text.Font("Ebrima Bold", 14.0));
            denyButton.setPrefWidth(144);
            denyButton.setPrefHeight(31);

            denyButton.setOnAction(e ->
            {
                //play denied request  message
                Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/deniedFriendRequest.mp3").toString());
                MediaPlayer player = new MediaPlayer(media);
                player.setVolume(0.30);
                player.play();

                loggedInUser.removeFromReceivedRequests(friend);
                friend.removeFromSentRequests(loggedInUser);

                Client.updateUser_serverSide(loggedInUser);
                Client.updateUser_serverSide(friend);

                //remove this gridpane from received requests tab
                for (Iterator<Node> iterator = receivedRequests_VBox.getChildren().iterator(); iterator.hasNext();) {
                    Node n = iterator.next();
                    if (n instanceof GridPane) {
                        //if the friend gridpane matches the username of the friend we want to remove
                        if (((Label) ((GridPane) n).getChildren().get(1)).getText().equals(friend.getUsername()))
                            iterator.remove();
                    }
                }
            });

            //hover effect
            denyButton.setOnMouseEntered(e ->
            {
                denyButton.setStyle("-fx-background-radius: 50; -fx-background-color: #ED5E68");
            });
            denyButton.setOnMouseExited(e ->
            {
                denyButton.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d");
            });

            GridPane.setColumnIndex(denyButton, 1);
            GridPane.setRowIndex(denyButton, 0);
            GridPane.setMargin(denyButton, new Insets(0, 0, -25, 210.0));
            gridPane.getChildren().add(denyButton);
        }

        return gridPane;
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

    /** enables send request button if user typed in something*/
    public void typingUserToSendTo()
    {
        //if typed something, enable button
        //else, keep it disabled
        sendRequest_Button.setDisable(!(findUser_Text.getText().trim().length() > 0));
    }

    /** sends friend request to typed in user*/
    public void sendRequest(ActionEvent event)
    {
        String typedUser = findUser_Text.getText().trim();
        if(!(typedUser.length() > 0))
            return;

        if(typedUser.equals("ADMIN")) // already friends with ADMIN
        {
            userNotFound_Label.setText("Already friends!");
            userNotFound_Label.setTextFill(Color.rgb(212, 47, 61));
            userNotFound_Label.setVisible(true);
            return;
        }

        for(User friend : loggedInUser.getFriends())
        {
            if(friend.getUsername().equals(typedUser)) // already a friend
            {
                userNotFound_Label.setText("Already friends!");
                userNotFound_Label.setTextFill(Color.rgb(212, 47, 61));
                userNotFound_Label.setVisible(true);
                return;
            }
        }

        for(FriendRequest fr : loggedInUser.getReceivedFriendRequests())
        {
            if(fr.getRequestFrom().getUsername().equals(typedUser)
                || fr.getRequestFrom().getUsername().equals(loggedInUser.getUsername())) // already have a request
            {
                userNotFound_Label.setText("Already have request!");
                userNotFound_Label.setTextFill(Color.rgb(212, 47, 61));
                userNotFound_Label.setVisible(true);
                return;
            }
        }

        if(typedUser.equals(loggedInUser.getUsername()))
        {
            userNotFound_Label.setText("Can't be friends with yourself!");
            userNotFound_Label.setTextFill(Color.rgb(212, 47, 61));
            userNotFound_Label.setVisible(true);
            return; // don't send request to self
        }

        //find user
        for(User u : Client.getUpdatedUserSet_fromServer())
        {
            if(u.getUsername().equals(typedUser)) // found the user to send to
            {
                boolean ableToSend = loggedInUser.sendFriendRequest(u);
                if(ableToSend) // request was sent successfully
                {
                    userNotFound_Label.setText("Sent!");
                    userNotFound_Label.setTextFill(Color.rgb(0x00, 0xdb, 0x3b));
                    userNotFound_Label.setVisible(true);

                    Client.updateUser_serverSide(loggedInUser); // update user server side
                    Client.updateUser_serverSide(u); // update other user server side

                    GridPane newFriendRequest = createFriendGridPane(u, "sentRequestsTab");
                    sentRequests_VBox.getChildren().add(newFriendRequest); // add to sent requests vbox

                    //play sent item request message
                    Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/sentFriendRequest.mp3").toString());
                    MediaPlayer player = new MediaPlayer(media);
                    player.setVolume(0.30);
                    player.play();
                }
                else // request failed, already have request in progress
                {
                    userNotFound_Label.setText("Already sent a request!");
                    userNotFound_Label.setTextFill(Color.rgb(212, 47, 61));
                    userNotFound_Label.setVisible(true);
                }
                sendRequest_Button.setDisable(true);
                findUser_Text.clear();
                return;
            }
        }
        //user not found
        userNotFound_Label.setText("User not found!");
        userNotFound_Label.setTextFill(Color.rgb(212, 47, 61));
        userNotFound_Label.setVisible(true);

        sendRequest_Button.setDisable(true);
        findUser_Text.clear();
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

    public static String getSendMsgTo() { return sendMsgTo; }
    public static void resetSendMsgTo() { sendMsgTo = null; }

    /** send message to admin*/
    public void sendMessageToAdmin(ActionEvent event)
    {
        sendMsgTo = "ADMIN";
        switchToInbox(event);
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

    /** mouse hover effect for friends tab button */
    public void hoverOver_friendsTabButton() {
        if (!isFriendsSelected)
            friendsTab_Button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_friendsTabButton() {
        if (!isFriendsSelected)
            friendsTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for sent requests tab button */
    public void hoverOver_sentRequestsButton() {
        if (!isSentSelected)
            sentRequestsTab_Button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_sentRequestsButton() {
        if (!isSentSelected)
            sentRequestsTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for received requests tab button */
    public void hoverOver_receivedRequestsButton() {
        if (!isReceivedSelected)
            receivedRequestsTab_Button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_receivedRequestsButton() {
        if (!isReceivedSelected)
            receivedRequestsTab_Button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect forsend msg to admin button */
    public void hoverOver_adminSendMsgButton() {
        adminSendMsgButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_adminSendMsgButton() {
        adminSendMsgButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for send request button */
    public void hoverOver_sendRequestButton() {
        sendRequest_Button.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_sendRequestButton() {
        sendRequest_Button.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

}
