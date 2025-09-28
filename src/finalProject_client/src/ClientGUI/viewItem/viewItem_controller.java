package ClientGUI.viewItem;

import ClientGUI.ClientGUI;
import ClientGUI.homeScreen.homePage_controller;
import ClientGUI.homeScreen.searchScreen.searchResultScene_controller;
import ClientGUI.loginScene.loginScene_controller;
import ClientGUI.yourAccount.readingList.readingList_controller;
import ClientGUI.yourAccount.yourItems.yourItems_controller;
import Items.*;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
public class viewItem_controller implements Initializable {
    private static User loggedInUser;
    private Item itemToView;
    private static Client client;
    private static ArrayList<Item> searchResultItems;
    private static String userTyped;
    private ArrayList<Review> reviews;

    @FXML ImageView itemImage;
    @FXML Label itemTitle;
    @FXML Label itemRatings;
    @FXML Label itemYear;
    @FXML Label type;
    @FXML Label genre;
    @FXML Label uniqueField;
    @FXML Label copiesAvailable;
    @FXML Label description;

    @FXML Button addReadingListButton;
    @FXML Button checkoutButton;
    @FXML Button returnButton;
    @FXML Label checkoutAttemptStatus;
    @FXML Button yourAccountButton;
    @FXML Button signOutButton;
    @FXML Button searchButton;
    @FXML MenuButton filterButton;
    @FXML Button goBackButton;
    @FXML Label welcomeText;
    @FXML TextField searchText;

    @FXML AnchorPane checkedOutCopiesPane;
    @FXML VBox checkedOutCopiesVBox;
    @FXML Button returnConfirmButton;
    @FXML Button returnNevermindButton;
    @FXML CheckBox returnAllCheckBox;
    @FXML Label youHaveAmount;

    @FXML CheckMenuItem filterBooks;
    @FXML CheckMenuItem filterDVDs;
    @FXML CheckMenuItem filterGames;

    @FXML Button reviewsButton;
    @FXML ScrollPane reviewScrollPane;
    @FXML VBox reviewVBox;


    @FXML Button leaveAReviewButton;
    @FXML AnchorPane writeReviewPane;
    @FXML TextField writeReviewText;
    @FXML Button submitReviewButton;
    @FXML Button reviewNevermindButton;

    @FXML Button rating1;
    @FXML Button rating2;
    @FXML Button rating3;
    @FXML Button rating4;
    @FXML Button rating5;
    private ArrayList<Button> ratingButtons;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loggedInUser = Client.getUpdatedUser_fromServer(loginScene_controller.getLoggedInUser().getUsername());
        client = ClientGUI.getClient();
        welcomeText.setText("Welcome, " + loggedInUser.getUsername() + "!");

        //use the proper itemToView depending on where we came from
            //home page, search page, your items page, reading list page,
        if (searchResultScene_controller.getItemToView() != null) // came here from search page
        {
            itemToView = searchResultScene_controller.getItemToView();
            searchResultScene_controller.resetItemToView();
        } else if (yourItems_controller.getItemToView() != null) // came here from your items page
        {
            itemToView = yourItems_controller.getItemToView();
            yourItems_controller.resetItemToView();
        }
        else if(readingList_controller.getItemToView() != null) // came here from reading list page
        {
            itemToView = readingList_controller.getItemToView();
            readingList_controller.resetItemToView();
        }
        else //came here from home page
            itemToView = homePage_controller.getItemToView();
        itemToView = Client.getUpdatedItem_fromServer(itemToView); // get most up to date version from server


        //display info for the item to view
        itemTitle.setText(itemToView.getTitle());
        itemRatings.setText("" + itemToView.getAverageRating());
        itemYear.setText("" + itemToView.getPublishedYear());
        type.setText(itemToView.getItemType());
        genre.setText(itemToView.getGenre());
        description.setText(itemToView.getDescription());
        copiesAvailable.setText("Copies Available: " + itemToView.getNumAvailable());

        //set the unique field (dependent on item type)
        if (itemToView.getItemType().equals("Book"))
            uniqueField.setText("Author: " + ((Book) itemToView).getAuthor());
        else if (itemToView.getItemType().equals("DVD"))
            uniqueField.setText("Running Time: " + ((DVD) itemToView).getHours() + "h " + ((DVD) itemToView).getMinutes() + "m");
        else if (itemToView.getItemType().equals("Game"))
            uniqueField.setText("Platform: " + ((Game) itemToView).getPlatform());

        itemImage.setImage(new Image(itemToView.getImagePath())); // set image

        //if a user has this item checkedout, set the checkedOutItems VBox accordingly
        ArrayList<Item> userCheckedOutCopies = loggedInUser.getCurrentCheckedOutItem(itemToView);
        if (userCheckedOutCopies.size() > 0) // user has a copy(s) of the item checked out
        {
            returnButton.setVisible(true); //enable return button
            youHaveAmount.setVisible(true);
            for (Item i : userCheckedOutCopies) // add checkboxes to select return vbox
            {
                addCheckBox();
            }
            youHaveAmount.setText("You have " + (checkedOutCopiesVBox.getChildren().size() - 1) + " copies");
        }

        //populate reviews VBox
        reviews = itemToView.getReviews();
        reviewVBox.setSpacing(5);
        for (Review r : reviews) {
            GridPane reviewGridPane = createReviewGridPane(r);
            reviewVBox.getChildren().add(reviewGridPane);
        }

        //populate rating buttons arraylist
        ratingButtons = new ArrayList<>();
        ratingButtons.add(rating1);
        ratingButtons.add(rating2);
        ratingButtons.add(rating3);
        ratingButtons.add(rating4);
        ratingButtons.add(rating5);

        //setup Add To Reading List Button
            //if already in reading list, set it to "Remove" functionality
        if(loggedInUser.alreadyAddedToReadingList(itemToView))
        {
            addReadingListButton.setText("Remove from Reading List");
            addReadingListButton.setTextFill(Color.BLACK);
            addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");

            //hover effect
            addReadingListButton.setOnMouseEntered(e ->
            {
                addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
            });
            addReadingListButton.setOnMouseExited(e ->
            {
                addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
            });
        }
        else // not already in reading list, set it to "Add" functionality
        {
            addReadingListButton.setText("Add to Reading List");
            addReadingListButton.setTextFill(Color.WHITE);
            addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");

            //hover effect
            addReadingListButton.setOnMouseEntered(e ->
            {
                addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
            });
            addReadingListButton.setOnMouseExited(e ->
            {
                addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
            });
        }

        searchResultItems = null;
        userTyped = "";
    }

    /**
     * switches scene to home screen if user wants to go back
     */
    public void switchToHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/homeScreen/homePageScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) itemTitle.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("jLib Home");
            stage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * called when user clicks sign out button, switches back to login screen
     */
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

    /**
     * called when user hits check out button
     */
    public void checkout(ActionEvent event) {
        boolean requestApproval = client.checkout_serverSide(loggedInUser, itemToView.getTitle());
        if (requestApproval) {
            checkoutAttemptStatus.setText("Success!");
            checkoutAttemptStatus.setTextFill(Color.rgb(32, 164, 39));
            checkoutAttemptStatus.setVisible(true);

            loggedInUser = Client.getUpdatedUser_fromServer(this.loggedInUser.getUsername()); // update this controller's reference to the user
            itemToView = Client.getUpdatedItem_fromServer(itemToView); //update this current itemToView

            copiesAvailable.setText("Copies Available: " + itemToView.getNumAvailable());
            System.out.println("checkout success: " + itemToView.getTitle());

            //enable return button
            returnButton.setVisible(true);
            returnButton.setDisable(false);
            youHaveAmount.setVisible(true);

            //add to checked out copies VBox
            CheckBox checkBox = new CheckBox();
            checkBox.setText("Due in 7 days");
            checkBox.setStyle("-fx-font-size: 14; -fx-font-style: Ebrima");
            checkBox.setOnAction(e ->
            {
                returnAllCheckBox.setSelected(false);
            });
            checkedOutCopiesVBox.getChildren().add(checkBox);

            youHaveAmount.setText("You have " + (checkedOutCopiesVBox.getChildren().size() - 1) + " copies");

            //play checkout message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/checked_out_item.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();
        } else // not enough
        {
            checkoutAttemptStatus.setText("Error! No copies available!");
            checkoutAttemptStatus.setTextFill(Color.DARKRED);
            checkoutAttemptStatus.setVisible(true);
            System.out.println("checkout failed: " + itemToView.getTitle());
        }
    }



    private void addCheckBox() {
        //add to checked out copies VBox
        CheckBox checkBox = new CheckBox();
        checkBox.setText("Due in 7 days");
        checkBox.setStyle("-fx-font-size: 14; -fx-font-style: Ebrima");
        checkBox.setOnAction(e -> {
            returnAllCheckBox.setSelected(false);
        });
        checkedOutCopiesVBox.getChildren().add(checkBox);
    }

    /**
     * called when user hits return button, displays checked out copies menu
     */
    public void returnItem(ActionEvent event) {
        checkoutAttemptStatus.setVisible(false);
        checkedOutCopiesPane.setVisible(!checkedOutCopiesPane.isVisible()); // toggle the visibility return vbox
        for (int i = 0; i < checkedOutCopiesVBox.getChildren().size(); i++)
            ((CheckBox) checkedOutCopiesVBox.getChildren().get(i)).setSelected(false); // deselect all boxes
    }

    /**
     * toggles/untoggles return all checkbox
     */
    public void setReturnAllCheckBox(ActionEvent event) {
        // select/unselect every check box if the return all box is selected/unselected
        for (int i = 0; i < checkedOutCopiesVBox.getChildren().size(); i++)
            ((CheckBox) checkedOutCopiesVBox.getChildren().get(i)).setSelected(returnAllCheckBox.isSelected());
    }

    /**
     * returns all items that are selected
     */
    public void confirmReturns(ActionEvent event) {
        //find out which specific copies they want to return
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 1; i < checkedOutCopiesVBox.getChildren().size(); i++) {
            if (((CheckBox) checkedOutCopiesVBox.getChildren().get(i)).isSelected())
                indexes.add(i - 1);
        }

        loggedInUser = client.return_serverSide(loggedInUser, itemToView, indexes); // send return request to server. update reference to user

        itemToView = Client.getUpdatedItem_fromServer(itemToView); // update this controller's reference of the item

        //update copies available label
        copiesAvailable.setText("Copies Available: " + itemToView.getNumAvailable());

        //update checkbox - remove checkboxes at the indices
        Collections.reverse(indexes);
        for (int i : indexes)
            checkedOutCopiesVBox.getChildren().remove(i + 1); // dont remove the Return All checkbox

        //update you have label
        youHaveAmount.setText("You have " + (checkedOutCopiesVBox.getChildren().size() - 1) + " copies");

        if (loggedInUser.getCurrentCheckedOutItem(itemToView).isEmpty()) // returned everything
        {
            returnButton.setVisible(false);
            checkedOutCopiesPane.setVisible(false);
            youHaveAmount.setVisible(false);
        }

        returnAllCheckBox.setSelected(false);
        if (checkedOutCopiesVBox.getChildren().size() == 1) {
            checkedOutCopiesPane.setVisible(false);
            returnButton.setVisible(false);
            youHaveAmount.setVisible(false);
        }

        //play return message
        Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/returned_item.mp3").toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(0.30);
        player.play();


    }

    /**
     * hides checked out items menu
     */
    public void setReturnNevermindButton(ActionEvent event) {
        checkedOutCopiesPane.setVisible(false);
        for (int i = 1; i < checkedOutCopiesVBox.getChildren().size(); i++)
            ((CheckBox) checkedOutCopiesVBox.getChildren().get(i)).setSelected(false); // deselect all boxes
    }

    /**
     * called when user hits search button
     */

    public void search(ActionEvent event) {
        searchResultItems = new ArrayList<>();
        userTyped = searchText.getText().trim(); // remove any leading/trailing spaces
        if (!userTyped.isEmpty())  // only do work if they typed something
        {
            //play searching message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/searching.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();


            //search for matches in catalog
            for (Item i : Client.getUpdatedCatalog()) {
                if (i.getTitle().toUpperCase().contains(userTyped.toUpperCase())) // ignore case
                    searchResultItems.add(i); // add to static list of search results
            }

            //if a filter(s) was selected, then apply filters to remove unwanted item types
            if (filterBooks.isSelected() || filterDVDs.isSelected() || filterGames.isSelected()) {
                //only want books
                if (filterBooks.isSelected() && !filterDVDs.isSelected() && !filterGames.isSelected()) {
                    for (int i = searchResultItems.size() - 1; i >= 0; i--) {
                        if (!(searchResultItems.get(i).getItemType().equals("Book"))) // not a book, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want dvds
                else if (filterDVDs.isSelected() && !filterBooks.isSelected() && !filterGames.isSelected()) {
                    for (int i = searchResultItems.size() - 1; i >= 0; i--) {
                        if (!(searchResultItems.get(i).getItemType().equals("DVD"))) // not a DVD, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want games
                else if (filterGames.isSelected() && !filterBooks.isSelected() && !filterDVDs.isSelected()) {
                    for (int i = searchResultItems.size() - 1; i >= 0; i--) {
                        if (!(searchResultItems.get(i).getItemType().equals("Game"))) // not a DVD, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want books and dvds
                else if (filterBooks.isSelected() && filterDVDs.isSelected() && !filterGames.isSelected()) {
                    for (int i = searchResultItems.size() - 1; i >= 0; i--) {
                        if (searchResultItems.get(i).getItemType().equals("Game")) // not a book or dvd, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want books and games
                else if (filterBooks.isSelected() && filterGames.isSelected() && !filterDVDs.isSelected()) {
                    for (int i = searchResultItems.size() - 1; i >= 0; i--) {
                        if (searchResultItems.get(i).getItemType().equals("DVD")) // not a book or game, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want dvds and games
                else if (filterDVDs.isSelected() && filterGames.isSelected() && !filterBooks.isSelected()) {
                    for (int i = searchResultItems.size() - 1; i >= 0; i--) {
                        if (searchResultItems.get(i).getItemType().equals("Book")) // not a DVD or game, remove it
                            searchResultItems.remove(i);
                    }
                }
            }

            //switch to search results scene
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/homeScreen/searchScreen/searchResultScene.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) filterButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Search Results");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * switch to Your Items scene
     */
    public void switchToYourItems(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/yourItems/yourItemsScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) filterButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Your Items");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * creates a gridpane that represents a review
     */
    private GridPane createReviewGridPane(Review review) {
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-border-color: #e4e4e4");

        // set column constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setMaxWidth(280.6);
        column1.setMinWidth(10);
        column1.setPrefWidth(91.533);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setMaxWidth(492.067);
        column2.setMinWidth(10);
        column2.setPrefWidth(482.467);

        gridPane.getColumnConstraints().addAll(column1, column2);

        // Add children
        //set review star image
        ImageView imageView = new ImageView(new Image(getClass().getResource("/ClientGUI/Images/reviewStar.png").toString()));
        imageView.setFitHeight(26.0);
        imageView.setFitWidth(33.0);
        imageView.setPreserveRatio(true);
        gridPane.add(imageView, 0, 0);
        //GridPane.setMargin(imageView, new javafx.geometry.Insets(0, 0, 0, 2));

        //set rating label
        Label ratingLabel = new Label("" + review.getStars());
        ratingLabel.setFont(new Font("Ebrima Bold", 16.0));
        GridPane.setHalignment(ratingLabel, javafx.geometry.HPos.CENTER);
        GridPane.setValignment(ratingLabel, javafx.geometry.VPos.CENTER);
        gridPane.add(ratingLabel, 0, 0);

        //set username label
        Label usernameLabel = new Label(review.getUser());
        usernameLabel.setFont(new Font("Ebrima", 16.0));
        GridPane.setHalignment(usernameLabel, javafx.geometry.HPos.CENTER);
        gridPane.add(usernameLabel, 0, 1);

        //set review text
        Label reviewLabel = new Label(review.getMessage());
        reviewLabel.setFont(new Font("Ebrima", 18.0));
        reviewLabel.setWrapText(true);
        GridPane.setColumnIndex(reviewLabel, 1);
        gridPane.add(reviewLabel, 1, 0);

        //set review date
        Label dateLabel = new Label(review.getTime());
        dateLabel.setFont(new Font("System Italic", 16.0));
        GridPane.setHalignment(dateLabel, javafx.geometry.HPos.RIGHT);
        GridPane.setValignment(dateLabel, javafx.geometry.VPos.CENTER);
        //GridPane.setMargin(dateLabel, new javafx.geometry.Insets(0, 0, 0, 15));
        gridPane.add(dateLabel, 1, 1);

        return gridPane;
    }

    boolean isReviewsButtonSelected = true;
    boolean isLeaveAReviewButtonSelected = false;

    /** displays leave a review text box */
    public void leaveAReview(ActionEvent event) {
        reviewScrollPane.setVisible(false);
        writeReviewPane.setVisible(true);
        leaveAReviewButton.setStyle("-fx-background-color: #f2ca36");
        isLeaveAReviewButtonSelected = true;

        reviewsButton.setStyle("-fx-background-color: #e4e4e4");
        isReviewsButtonSelected = false;
    }



    /** displays reviews for the item */
    public void seeReviews(ActionEvent event) {
        writeReviewPane.setVisible(false);
        reviewsButton.setStyle("-fx-background-color: #f2ca36");
        isReviewsButtonSelected = true;

        leaveAReviewButton.setStyle("-fx-background-color: #e4e4e4");
        isLeaveAReviewButtonSelected = false;

        //reset leave a rating selections
        selectedRating = 0;
        for(Button b : ratingButtons)
            b.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");

        reviewScrollPane.setVisible(true); // display reviews
    }

    /** selects a rating*/
    int selectedRating = 0;
    public void selectRating(ActionEvent event)
    {
        Button selectedButton = (Button)event.getSource();
        selectedRating = Integer.parseInt(selectedButton.getText());

        //selection effect
        switch(selectedRating) {
            case 1:
                rating1.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
            case 2:
                rating2.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
            case 3:
                rating3.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
            case 4:
                rating4.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
            case 5:
                rating5.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
        }

        //deselect other buttons
        for(Button b : ratingButtons)
        {
            if(Integer.parseInt(b.getText()) != selectedRating)
                b.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
        }
    }

    /** called when user types their review*/
    public void userTypingReview()
    {
        submitReviewButton.setDisable(!(writeReviewText.getText().trim().length() >= 1)); // must write something
    }

    /** adds review to item review list and user review history*/
    public void submitReview(ActionEvent event)
    {
        if(writeReviewText.getText().trim().length() < 1) // user has to write something
            return;
        //play thank you message
        Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/thank_you.mp3").toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(0.30);
        player.play();

        if(selectedRating != 0 && writeReviewText.getText().trim().length() >= 1)
        {
            Review newReview = new Review(itemToView, loggedInUser.getUsername(), selectedRating, writeReviewText.getText().trim());
            loggedInUser.add_reviewHistory(newReview);
            itemToView.addReview(newReview);
            Client.updateUser_serverSide(loggedInUser); //update server version of user
            Client.updateItem_serverSide(itemToView); //update server version of item
            itemToView = Client.getUpdatedItem_fromServer(itemToView);

            itemRatings.setText("" + itemToView.getAverageRating()); //update average rating
            GridPane newReviewGridPane = createReviewGridPane(newReview); //create review gridpane and add to review vbox
            reviewVBox.getChildren().add(newReviewGridPane); // add to vbox
        }

        nevermindReview(event); // reset review selections, switch tabs
    }

    /** clears review selections, switches to see reviews tab*/
    public void nevermindReview(ActionEvent event)
    {
        //reset everything
        selectedRating = 0;
        submitReviewButton.setDisable(true);
        writeReviewText.clear();
        for(Button b : ratingButtons)
            b.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");

        //switch tabs
        isReviewsButtonSelected = true;
        isLeaveAReviewButtonSelected = false;
        writeReviewPane.setVisible(false);
        reviewScrollPane.setVisible(true);
        leaveAReviewButton.setStyle("-fx-background-color: #e4e4e4");
        reviewsButton.setStyle("-fx-background-color: #f2ca36");
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

    /** adds/removes item to reading list */
    public void addOrRemove_ReadingList(ActionEvent event)
    {
        //already added, so the button at time of press is "Remove", so remove item from list
        if(loggedInUser.alreadyAddedToReadingList(itemToView))
        {
            //play removed item from list message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/removedFromList.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();

            loggedInUser.removeFromReadingList(itemToView); // remove from user's reading list
            Client.updateUser_serverSide(loggedInUser); // update user on server side

            //change button back to "Add" button
            addReadingListButton.setText("Add to Reading List");
            addReadingListButton.setTextFill(Color.WHITE);
            addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");

            //hover effect
            addReadingListButton.setOnMouseEntered(e ->
            {
                addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
            });
            addReadingListButton.setOnMouseExited(e ->
            {
                addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
            });
        }
        else // not already added, so button at time of press is "Add", add item to list
        {
            //play added item to list message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/addedToList.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();

            loggedInUser.addToReadingList(itemToView); // add to user's reading list
            Client.updateUser_serverSide(loggedInUser); // update user on server side

            //change button back to "Remove" button
            addReadingListButton.setText("Remove from Reading List");
            addReadingListButton.setTextFill(Color.BLACK);
            addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");

            //hover effect
            addReadingListButton.setOnMouseEntered(e ->
            {
                addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
            });
            addReadingListButton.setOnMouseExited(e ->
            {
                addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
            });
        }
    }

    /** switch to Reading List scene*/
    public void switchToReadingList(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/readingList/readingListScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)filterButton.getScene().getWindow();
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
            Stage stage = (Stage)filterButton.getScene().getWindow();
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










    public static ArrayList<Item> getSearchResultItems() {
        return searchResultItems;
    }

    public static void resetSearchResults() {
        searchResultItems = null;
    }

    public static String getUserTyped() {
        return userTyped;
    }


    /**
     * mouse hover effect for your account button
     */
    public void hoverOver_yourAccountButton() {
        yourAccountButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_yourAccountButton() {
        yourAccountButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
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

    /**
     * mouse hover effect for search button
     */
    public void hoverOver_searchButton() {
        searchButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_searchButton() {
        searchButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /**
     * mouse hover effect for filter button
     */
    public void hoverOver_filterButton() {
        filterButton.setTextFill(Color.rgb(77, 139, 255));
    }

    public void hoverExit_filterButton() {
        filterButton.setTextFill(Color.rgb(61, 111, 205));
    }

    /**
     * mouse hover effect for go back button
     */
    public void hoverOver_goBackButton() {
        goBackButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }

    public void hoverExit_goBackButton() {
        goBackButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /**
     * mouse hover effect for check out button
     */
    public void hoverOver_checkoutButton() {
        checkoutButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3ad06c");
    }

    public void hoverExit_checkoutButton() {
        checkoutButton.setStyle("-fx-background-radius: 50; -fx-background-color: #30ae5a");
    }

    /**
     * mouse hover effect for return button
     */
    public void hoverOver_returnButton() {
        returnButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_returnButton() {
        returnButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /**
     * mouse hover effect for returnConfirm button
     */
    public void hoverOver_returnConfirmButton() {
        returnConfirmButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_returnConfirmButton() {
        returnConfirmButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /**
     * mouse hover effect for nevermind button
     */
    public void hoverOver_returnNevermindButton() {
        returnNevermindButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }

    public void hoverExit_returnNevermindButton() {
        returnNevermindButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }

    /**
     * mouse hover effect for leaveAReview button
     */
    public void hoverOver_leaveAReviewButton() {
        if (!isLeaveAReviewButtonSelected)
            leaveAReviewButton.setStyle("-fx-background-color: #ffe897");
    }

    public void hoverExit_leaveAReviewButton() {
        if (!isLeaveAReviewButtonSelected)
            leaveAReviewButton.setStyle("-fx-background-color: #e4e4e4");
    }

    /**
     * mouse hover effect for reviews button
     */
    public void hoverOver_reviewsButton() {
        if (!isReviewsButtonSelected)
            reviewsButton.setStyle("-fx-background-color: #ffe897");
    }

    public void hoverExit_reviewsButton() {
        if (!isReviewsButtonSelected)
            reviewsButton.setStyle("-fx-background-color: #e4e4e4");
    }

    /**
     * mouse hover effect for submitReview button
     */
    public void hoverOver_submitReviewButton() {
        submitReviewButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_submitReviewButton() {
        submitReviewButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }



    /**
     * mouse hover effect for reviewNevermind button
     */
    public void hoverOver_reviewNevermindButton() {
        reviewNevermindButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2f2f2");
    }

    public void hoverExit_reviewNevermindButton() {
        reviewNevermindButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }

    /** hover effects for rating buttons*/
    public void hoverOver_rating1Button() {
            if(selectedRating != 1)
                rating1.setStyle("-fx-background-radius: 50; -fx-background-color: #ffe897");
    }
    public void hoverExit_rating1Button() {
            if(selectedRating != 1)
                rating1.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }
    public void hoverOver_rating2Button() {
        if(selectedRating != 2)
            rating2.setStyle("-fx-background-radius: 50; -fx-background-color: #ffe897");
    }
    public void hoverExit_rating2Button() {
        if(selectedRating != 2)
            rating2.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }
    public void hoverOver_rating3Button() {
        if(selectedRating != 3)
            rating3.setStyle("-fx-background-radius: 50; -fx-background-color: #ffe897");
    }
    public void hoverExit_rating3Button() {
        if(selectedRating != 3)
            rating3.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }
    public void hoverOver_rating4Button() {
        if(selectedRating != 4)
            rating4.setStyle("-fx-background-radius: 50; -fx-background-color: #ffe897");
    }
    public void hoverExit_rating4Button() {
        if(selectedRating != 4)
            rating4.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }
    public void hoverOver_rating5Button() {
        if(selectedRating != 5)
            rating5.setStyle("-fx-background-radius: 50; -fx-background-color: #ffe897");
    }
    public void hoverExit_rating5Button() {
        if(selectedRating != 5)
            rating5.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }

    /** mouse hover effect for add to reading list button*/
    public void hoverOver_addReadingListButton() {
        addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_addReadingListButton() {
        addReadingListButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

}
