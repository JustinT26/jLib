package ClientGUI.adminGUI;

import Items.Item;
import Users.Admin;
import Users.ItemRequest;
import Users.Message;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
public class adminGUI_controller implements Initializable
{
    private Admin adminAccount;
    private HashSet<User> users;
    private ArrayList<Item> catalog;

    @FXML Button signOutButton;
    @FXML ScrollPane usersPane;
    @FXML Button userTab_button;
    @FXML VBox usersVBox;

    @FXML ScrollPane catalogPane;
    @FXML Button catalogTab_button;
    @FXML VBox catalogVBox;

    @FXML ScrollPane requestsPane;
    @FXML Button requestTab_button;
    @FXML VBox requestsVBox;

    @FXML ScrollPane inboxPane;
    @FXML Button inboxTab_button;
    @FXML VBox inboxVBox;

    @FXML AnchorPane sendMsgPane;
    @FXML Button sendMsgTab_button;
    @FXML MenuButton sendMsgTo_Menu;
    private String sendMsgTo; // if user wants to send msg to a user from the list, store their username here
    @FXML TextField messageBox;
    @FXML Button sendMsg;
    @FXML Label sentLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        adminAccount = Client.getAdmin_fromServer();
        users = Client.getUpdatedUserSet_fromServer();
        catalog = Client.getUpdatedCatalog();

        //populate users vbox
        usersVBox.setSpacing(75);
        for(User u : users)
        {
            if(!u.getUsername().equals("ADMIN"))
            {
                GridPane userGridP = createFriendGridPane(u);
                usersVBox.getChildren().add(userGridP);
            }
        }

        //populate catalog vbox
        catalogVBox.setSpacing(25);
        for(int i = 0; i < catalog.size(); i++)
        {
            AnchorPane newResultPane = ((AnchorPane)catalogVBox.getChildren().get(i));
            newResultPane.setVisible(true);

            //set the info
            Item item = catalog.get(i);
            ((Label)newResultPane.getChildren().get(0)).setText(item.getTitle()); //set title
            ((Label)newResultPane.getChildren().get(1)).setText("" + item.getAverageRating());//set average rating
            ((Label)newResultPane.getChildren().get(2)).setText("" + item.getPublishedYear());//set year
            ((Label)newResultPane.getChildren().get(3)).setText(item.getItemType());//set type
            ((ImageView)newResultPane.getChildren().get(4)).setImage(new Image(item.getImagePath())); // set image

            ((Button)newResultPane.getChildren().get(5)).setOnAction(e -> // set edit picture button action
            {
                //open file chooser
                //grab image path
                //set imageview to show that image
                //update catalog server side

                //open file chooser
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter imageFilter // filter to image file types
                        = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg");
                fileChooser.getExtensionFilters().add(imageFilter);
                File selectedImage = fileChooser.showOpenDialog(signOutButton.getScene().getWindow());

                if(selectedImage != null)
                {
                    try {
                        String imagePath = selectedImage.toURL().toString();
                        ((ImageView)newResultPane.getChildren().get(4)).setImage(new Image(imagePath)); // set image
                        item.setImagePath(imagePath); // update item's image path
                        Client.updateItem_serverSide(item); // update item on server side
                    } catch (MalformedURLException mue) {
                        mue.printStackTrace();
                    }
                }
            });

            // set hover effect
            ((Button)newResultPane.getChildren().get(5)).setOnMouseEntered(e ->
            {
                ((Button)newResultPane.getChildren().get(5)).setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
            });
            ((Button)newResultPane.getChildren().get(5)).setOnMouseExited(e ->
            {
                ((Button)newResultPane.getChildren().get(5)).setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
            });


            catalogVBox.getChildren().set(i, newResultPane); // update the vbox
        }
        //remove the remaining extra uneeded anchorpanes frrom vbox (because i initially have it populated with placeholder anchorpanes)
        catalogVBox.getChildren().remove(catalog.size(), catalogVBox.getChildren().size());


        //populate requests tab
        for(ItemRequest ir : adminAccount.getReceivedItemRequests())
        {
            AnchorPane irPane = createItemRequestPane(ir); //create item request grid pane
            requestsVBox.getChildren().add(irPane); //add to requests tab vbox
        }

        //populate inbox vbox with, sort newest first - selection sort
        ArrayList<Message> sortedInbox = new ArrayList<>(adminAccount.getInbox());

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
            inboxVBox.getChildren().add(msgGridPane);
        }

        //populate send message to drop down menu
        for(User u : users)
        {
            if(!(u.getUsername().equals("ADMIN")))
            {
                MenuItem menuItem = new MenuItem();
                menuItem.setText(u.getUsername());
                menuItem.setOnAction(e ->
                {
                    choseRecipient(e);
                });
                sendMsgTo_Menu.getItems().add(menuItem);
            }
        }


    }







    /** creates a gridpane representing a user*/
    private GridPane createFriendGridPane(User friend)
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
            switchSendMsg_tab(e);
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

        return gridPane;
    }

    /** creates a gridpane representing a message*/
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
        imageView.setFitHeight(170.0);
        imageView.setFitWidth(170.0);
        gridPane.add(imageView, 0, 0);

        // from user label
        Label usernameLabel = new Label("From: " + from.getUsername());
        usernameLabel.setFont(new Font("Ebrima Bold", 24.0));
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
            switchSendMsg_tab(e);
            sendMsgTo = from.getUsername();
            sendMsgTo_Menu.setText("Send To: " + sendMsgTo);
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
        GridPane.setMargin(messageLabel, new Insets(0, 0, -20, 20.0));
        gridPane.add(messageLabel, 1, 0);

        return gridPane;
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

        //accept button
        Button btnAccept = new Button("ACCEPT");
        btnAccept.setLayoutX(24.0);
        btnAccept.setLayoutY(124.0);
        btnAccept.setPrefHeight(55.0);
        btnAccept.setPrefWidth(151.0);
        btnAccept.setStyle("-fx-background-radius: 50; -fx-background-color: #30ae5a; -fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");

        //hover effect
        btnAccept.setOnMouseEntered(e ->
        {
            btnAccept.setStyle("-fx-background-radius: 50; -fx-background-color: #3ad06c; -fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        });
        btnAccept.setOnMouseExited(e ->
        {
            btnAccept.setStyle("-fx-background-radius: 50; -fx-background-color: #30ae5a;-fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        });

        //deny button
        Button btnDeny = new Button("DENY");
        btnDeny.setLayoutX(210.0);
        btnDeny.setLayoutY(124.0);
        btnDeny.setPrefHeight(55.0);
        btnDeny.setPrefWidth(151.0);
        btnDeny.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d; -fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");

        //hover effect
        btnDeny.setOnMouseEntered(e ->
        {
            btnDeny.setStyle("-fx-background-radius: 50; -fx-background-color: #ED5E68;-fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        });
        btnDeny.setOnMouseExited(e ->
        {
            btnDeny.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d;-fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        });


        btnAccept.setOnAction(e ->
        {
            btnAccept.setText("ACCEPTED");
            btnAccept.setStyle("-fx-background-radius: 50; -fx-background-color: #3ad06c; -fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
            btnDeny.setVisible(false);
            btnAccept.setDisable(true);

            ir.accept(true); // accept
            //Send message to user
            User sendTo = Client.getUpdatedUser_fromServer(ir.getFrom().getUsername());
            for(ItemRequest itemRequest : sendTo.getItemRequests())
            {
                if(itemRequest.getItemTitle().equals(ir.getItemTitle()))
                    itemRequest.accept(true);
            }
            String msgString = "Your request for: " + ir.getItemTitle() + " (" + ir.getItemType() +") was ACCEPTED.";
            Message message = new Message(adminAccount, sendTo, msgString);
            sendTo.receiveMessage(message);

            Client.updateUser_serverSide(adminAccount);
            Client.updateUser_serverSide(sendTo);
        });
        btnDeny.setOnAction(e ->
        {
            btnDeny.setText("DENIED");
            btnDeny.setStyle("-fx-background-radius: 50; -fx-background-color: #d42f3d;-fx-text-fill: white; -fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
            btnAccept.setVisible(false);
            btnDeny.setDisable(true);

            ir.accept(false); // deny
            //Send message to user
            User sendTo = Client.getUpdatedUser_fromServer(ir.getFrom().getUsername());
            for(ItemRequest itemRequest : sendTo.getItemRequests())
            {
                if(itemRequest.getItemTitle().equals(ir.getItemTitle()))
                    itemRequest.accept(false);
            }
            String msgString = "Your request for: " + ir.getItemTitle() + " (" + ir.getItemType() +") was DENIED.";
            Message message = new Message(adminAccount, sendTo, msgString);
            sendTo.receiveMessage(message);

            Client.updateUser_serverSide(adminAccount);
            Client.updateUser_serverSide(sendTo);
        });

        if(ir.getStatus().equals("Accepted"))
        {
            btnAccept.setText("ACCEPTED");
            btnAccept.setDisable(true);
            btnDeny.setVisible(false);
        }
        else if(ir.getStatus().equals("Denied"))
        {
            btnDeny.setText("DENIED");
            btnDeny.setDisable(true);
            btnAccept.setVisible(false);
        }
        itemRequestPane.getChildren().add(btnAccept);
        itemRequestPane.getChildren().add(btnDeny);



        //user pfp
        ImageView userImage = new ImageView(new Image(Client.getUpdatedUser_fromServer(ir.getFrom().getUsername()).getPfpImagePath()));
        userImage.setFitHeight(150.0);
        userImage.setFitWidth(150.0);
        userImage.setLayoutX(476.0);
        userImage.setLayoutY(20.0);
        itemRequestPane.getChildren().add(userImage);


        //username
        Label labelUsername = new Label(ir.getFrom().getUsername());
        labelUsername.setLayoutX(441.0);
        labelUsername.setLayoutY(175.0);
        labelUsername.setPrefHeight(31.0);
        labelUsername.setPrefWidth(217.0);
        labelUsername.setStyle("-fx-font-family: 'Ebrima Bold'; -fx-font-size: 24;");
        labelUsername.setAlignment(javafx.geometry.Pos.CENTER);
        itemRequestPane.getChildren().add(labelUsername);

        return itemRequestPane;
    }

    /** switches to inbox tab */
    boolean isUsersTabSelected = true;
    public void switchUsers_tab(ActionEvent event)
    {
        if(!isUsersTabSelected)
        {
            usersPane.setVisible(true);
            userTab_button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");
            isUsersTabSelected = true;

            //disable all other tabs
            catalogPane.setVisible(false);
            requestsPane.setVisible(false);
            inboxPane.setVisible(false);
            sendMsgPane.setVisible(false);

            isCatalogTabSelected = false;
            isRequestsTabSelected = false;
            isInboxTabSelected = false;
            isSendMsgTabSelected = false;

            catalogTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            requestTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            inboxTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            sendMsgTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");

        }


    }

    /** switches to catalog tab */
    boolean isCatalogTabSelected = false;
    public void switchCatalog_tab(ActionEvent event)
    {
        if(!isCatalogTabSelected)
        {
            catalogPane.setVisible(true);
            catalogTab_button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");
            isCatalogTabSelected = true;

            //disable all other tabs
            usersPane.setVisible(false);
            requestsPane.setVisible(false);
            inboxPane.setVisible(false);
            sendMsgPane.setVisible(false);

            isUsersTabSelected = false;
            isRequestsTabSelected = false;
            isInboxTabSelected = false;
            isSendMsgTabSelected = false;

            userTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            requestTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            inboxTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            sendMsgTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
        }

    }

    /** switch to requests tab */
    boolean isRequestsTabSelected = false;
    public void switchRequests_tab(ActionEvent event)
    {
        if(!isRequestsTabSelected)
        {
            requestsPane.setVisible(true);
            requestTab_button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");
            isRequestsTabSelected = true;

            //disable all other tabs
            usersPane.setVisible(false);
            catalogPane.setVisible(false);
            inboxPane.setVisible(false);
            sendMsgPane.setVisible(false);

            isUsersTabSelected = false;
            isCatalogTabSelected = false;
            isInboxTabSelected = false;
            isSendMsgTabSelected = false;

            userTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            catalogTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            inboxTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            sendMsgTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
        }

    }

    /** switches to inbox tab */
    boolean isInboxTabSelected = false;
    public void switchInbox_tab(ActionEvent event)
    {
        if(!isInboxTabSelected)
        {
            inboxPane.setVisible(true);
            inboxTab_button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");
            isInboxTabSelected = true;

            //disable all other tabs
            usersPane.setVisible(false);
            catalogPane.setVisible(false);
            requestsPane.setVisible(false);
            sendMsgPane.setVisible(false);

            isUsersTabSelected = false;
            isCatalogTabSelected = false;
            isRequestsTabSelected = false;
            isSendMsgTabSelected = false;

            userTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            catalogTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            requestTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            sendMsgTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
        }

    }

    /** public void switch to send message tab */
    boolean isSendMsgTabSelected = false;
    public void switchSendMsg_tab(ActionEvent event)
    {
        if(!isSendMsgTabSelected)
        {
            sendMsgTo_Menu.setText("Send To: ");
            messageBox.clear();
            sentLabel.setVisible(false);

            sendMsgPane.setVisible(true);
            sendMsgTab_button.setStyle("-fx-background-color: #f2ca36; -fx-border-color: #b9b9b9");
            isSendMsgTabSelected = true;

            if(sendMsgTo != null)
                sendMsgTo_Menu.setText("Send To: " + sendMsgTo);


            //disable all other tabs
            usersPane.setVisible(false);
            catalogPane.setVisible(false);
            requestsPane.setVisible(false);
            inboxPane.setVisible(false);

            isUsersTabSelected = false;
            isCatalogTabSelected = false;
            isRequestsTabSelected = false;
            isInboxTabSelected = false;

            userTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            catalogTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            requestTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
            inboxTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
        }
    }

    /** updates dropdown menu text to show selected recipient*/
    public void choseRecipient(ActionEvent event)
    {
        sendMsgTo = ((MenuItem)event.getSource()).getText();
        sendMsgTo_Menu.setText("Send To: " + sendMsgTo);
//        if(sendMsgTo.equals("ADMIN"))
//        {
//            sendMsgTo_Menu.setTextFill(Color.rgb(0xff, 0x00, 0x00));
//            sendMsgTo_Menu.setStyle("-fx-font-weight: bold;-fx-background-radius: 50; -fx-background-color: #e4e4e4");
//        }
//        else
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

        //if want to send to all
        if(sendMsgTo_Menu.getText().equals("Send To: ALL"))
        {
            for(User u : Client.getUpdatedUserSet_fromServer())
            {
                if(!u.getUsername().equals("ADMIN"))
                {
                    Message m = new Message(adminAccount, u, messageBox.getText().trim()); // create msg
                    u.receiveMessage(m); // send msg to recipient
                    System.out.println("sent message to: " + u.getUsername());
                    Client.updateUser_serverSide(u); // update recipient user on server side
                }
            }
        }
        else // send to one person
        {
            for (User u : Client.getUpdatedUserSet_fromServer()) {
                if (u.getUsername().equals(sendMsgTo)) {
                    Message m = new Message(adminAccount, u, messageBox.getText().trim()); // create msg
                    u.receiveMessage(m); // send msg to recipient
                    System.out.println("sent message to: " + u.getUsername());
                    Client.updateUser_serverSide(u); // update recipient user on server side
                    break;
                }
            }
            sendMsgTo = null;
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

            Stage currentStage = (Stage)signOutButton.getScene().getWindow();
            currentStage.close(); // close login screen

            //play sign out message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/goodbye_darling.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();

            stage.show(); // go back to login screen

        } catch (IOException ioe) { ioe.printStackTrace(); }
    }










    /** mouse hover effect for users tab button */
    public void hoverOver_UsersTabButton() {
        if (!isUsersTabSelected)
            userTab_button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_UsersTabButton() {
        if (!isUsersTabSelected)
            userTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for catalog tab button */
    public void hoverOver_CatalogTabButton() {
        if (!isCatalogTabSelected)
            catalogTab_button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_CatalogTabButton() {
        if (!isCatalogTabSelected)
            catalogTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for requests tab button */
    public void hoverOver_requestsTabButton() {
        if (!isRequestsTabSelected)
            requestTab_button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_requestsTabButton() {
        if (!isRequestsTabSelected)
            requestTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for inbox tab button */
    public void hoverOver_inboxTabButton() {
        if (!isInboxTabSelected)
            inboxTab_button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_inboxTabButton() {
        if (!isInboxTabSelected)
            inboxTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for send message tab button */
    public void hoverOver_sendMsgTabButton() {
        if (!isSendMsgTabSelected)
            sendMsgTab_button.setStyle("-fx-background-color: #ffe897; -fx-border-color: #b9b9b9");
    }

    public void hoverExit_sendMsgTabButton() {
        if (!isSendMsgTabSelected)
            sendMsgTab_button.setStyle("-fx-background-color: #e4e4e4; -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for nevermind button */
    public void hoverOver_signOutButton() {
        signOutButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }
    public void hoverExit_signOutButton() {
        signOutButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }

    /** mouse hover effect for send message button */
    public void hoverOver_sendMsgButton() {
        sendMsg.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_sendMsgButton() {
        sendMsg.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }




}
