package ClientGUI.homeScreen.searchScreen;
import ClientGUI.homeScreen.homePage_controller;
import ClientGUI.loginScene.loginScene_controller;
import ClientGUI.viewItem.viewItem_controller;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class searchResultScene_controller implements Initializable
{
    private static ArrayList<Item> searchResults;
    private ArrayList<Item> copyOfSearchResults;
    private static Item itemToView;
    private static String userTyped;

    @FXML Label welcomeText;
    @FXML Label numResultsFound;
    @FXML ScrollPane scrollPane;
    @FXML Button goBackButton;
    @FXML VBox resultsVBox;
    @FXML Button yourAccountButton;
    @FXML Button searchButton;
    @FXML Button signOutButton;

    @FXML MenuButton filterButton;
    @FXML TextField searchText;
    @FXML MenuButton chooseSortMenu;
    @FXML Button sortOption1Button;
    @FXML Button sortOption2Button;
    @FXML Button sortApplyButton;

    @FXML CheckMenuItem filterBooks;
    @FXML CheckMenuItem filterDVDs;
    @FXML CheckMenuItem filterGames;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        welcomeText.setText("Welcome, " + loginScene_controller.getLoggedInUser().getUsername() + "!");
        itemToView = null;

        //use the proper list of search results depending on if we came here from the home page,
            // a view item page, or from a search result page itself
        if(searchResults != null) { /* proceed */ }
        else if(viewItem_controller.getSearchResultItems() != null)
        {
            searchResults = viewItem_controller.getSearchResultItems();
            viewItem_controller.resetSearchResults();
            userTyped = viewItem_controller.getUserTyped();
        }
        else {
            searchResults = homePage_controller.getSearchResultItems();
            userTyped = homePage_controller.getUserTyped();
        }

        resultsVBox.setSpacing(20);

        if(searchResults.isEmpty())
        {
            numResultsFound.setText("0 results found for " + "\"" + userTyped + "\"");
            scrollPane.setVisible(false);
        }
        else
        {
            numResultsFound.setText("" + searchResults.size() + " results found for " + "\"" + userTyped + "\"");

            //set the VBox children anchorpanes with corresponding info
            for(int i = 0; i < searchResults.size(); i++)
            {
                AnchorPane newResultPane = ((AnchorPane)resultsVBox.getChildren().get(i));
                newResultPane.setVisible(true);

                //set the info
                Item item = searchResults.get(i);
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
                        Stage stage = (Stage) filterButton.getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle(item.getTitle());
                    }
                    catch (IOException ioe) { ioe.printStackTrace(); }
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


                resultsVBox.getChildren().set(i, newResultPane); // update the vbox
            }

            //remove the remaining extra uneeded anchorpanes frrom vbox (because i initially have it populated with placeholder anchorpanes)
            resultsVBox.getChildren().remove(searchResults.size(), resultsVBox.getChildren().size());
        }
        copyOfSearchResults = new ArrayList<>(searchResults);
        searchResults = null; // reset it for the next time we arrive here from a search result scene
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

    /** called when user hits search button */

    public void search(ActionEvent event) {
        searchResults = new ArrayList<>();
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
                    searchResults.add(i); // add to static list of search results
            }

            //if a filter(s) was selected, then apply filters to remove unwanted item types
            if(filterBooks.isSelected() || filterDVDs.isSelected() || filterGames.isSelected())
            {
                //only want books
                if(filterBooks.isSelected() && !filterDVDs.isSelected() && !filterGames.isSelected())
                {
                    for(int i = searchResults.size() - 1; i >= 0; i--)
                    {
                        if( !(searchResults.get(i).getItemType().equals("Book")) ) // not a book, remove it
                            searchResults.remove(i);
                    }
                }

                //only want dvds
                else if(filterDVDs.isSelected() && !filterBooks.isSelected() && !filterGames.isSelected())
                {
                    for(int i = searchResults.size() - 1; i >= 0; i--)
                    {
                        if( !(searchResults.get(i).getItemType().equals("DVD")) ) // not a DVD, remove it
                            searchResults.remove(i);
                    }
                }

                //only want games
                else if(filterGames.isSelected() && !filterBooks.isSelected() && !filterDVDs.isSelected())
                {
                    for(int i = searchResults.size() - 1; i >= 0; i--)
                    {
                        if( !(searchResults.get(i).getItemType().equals("Game")) ) // not a DVD, remove it
                            searchResults.remove(i);
                    }
                }

                //only want books and dvds
                else if(filterBooks.isSelected() && filterDVDs.isSelected() && !filterGames.isSelected())
                {
                    for(int i = searchResults.size() - 1; i >= 0; i--)
                    {
                        if(searchResults.get(i).getItemType().equals("Game")) // not a book or dvd, remove it
                            searchResults.remove(i);
                    }
                }

                //only want books and games
                else if(filterBooks.isSelected() && filterGames.isSelected() && !filterDVDs.isSelected())
                {
                    for(int i = searchResults.size() - 1; i >= 0; i--)
                    {
                        if(searchResults.get(i).getItemType().equals("DVD")) // not a book or game, remove it
                            searchResults.remove(i);
                    }
                }

                //only want dvds and games
                else if(filterDVDs.isSelected() && filterGames.isSelected() && !filterBooks.isSelected())
                {
                    for(int i = searchResults.size() - 1; i >= 0; i--)
                    {
                        if(searchResults.get(i).getItemType().equals("Book")) // not a DVD or game, remove it
                            searchResults.remove(i);
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

    /** switch to Your Items scene*/
    public void switchToYourItems(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourItems/yourItemsScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) filterButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Your Items");
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** choosing what to sort by*/
    public void chooseSort(ActionEvent event)
    {
        MenuItem menuItem = (MenuItem)event.getSource();
        String chosenSort = menuItem.getText();
        sortType = chosenSort;
        chooseSortMenu.setText(chosenSort);

        if(chosenSort.equals("Date Published"))
        {
            sortOption1Button.setText("Newest First");
            sortOption2Button.setText("Oldest First");
            sortOption1Button.setDisable(false);
            sortOption2Button.setDisable(false);
        }
        else if(chosenSort.equals("Ratings"))
        {
            sortOption1Button.setText("Highest First");
            sortOption2Button.setText("Lowest First");
            sortOption1Button.setDisable(false);
            sortOption2Button.setDisable(false);
        }
        else if(chosenSort.equals("Default"))
        {
            sortOption1Button.setText("");
            sortOption2Button.setText("");

            sortOption1Button.setDisable(true);
            sortOption2Button.setDisable(true);

            sortApplyButton.setDisable(true);
        }

        sortOption1Button.setStyle("-fx-background-color: #e4e4e4");
        sortOption2Button.setStyle("-fx-background-color: #e4e4e4");
        isOption1Selected = false;
        isOption2Selected = false;
        sortApplyButton.setDisable(true);
    }

    private boolean isOption1Selected = false;
    private boolean isOption2Selected = false;
    /** selected effect for sortOption1 button */
    public void select_sortOption1Button(ActionEvent event) {
        sortOption1Button.setStyle("-fx-background-color: #f2ca36");
        isOption1Selected = true;

        sortOption2Button.setStyle("-fx-background-color: #e4e4e4");
        isOption2Selected = false;
        sortApplyButton.setDisable(false);
    }

    /** selected effect for sortOption2 button */
    public void select_sortOption2Button(ActionEvent event) {
        sortOption2Button.setStyle("-fx-background-color: #f2ca36");
        isOption2Selected = true;

        sortOption1Button.setStyle("-fx-background-color: #e4e4e4");
        isOption1Selected = false;
        sortApplyButton.setDisable(false);
    }

    /** rearranges results order based on the sort option selected */
    String sortType;
    public void applySort(ActionEvent event)
    {
        //play sorting message
        Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/sorting_results.mp3").toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(0.30);
        player.play();

        if(sortType.equals("Date Published"))
        {
            if(isOption1Selected) // selection sort searchResults highest to lowest year
            {
                for (int i = 0; i < copyOfSearchResults.size() - 1; i++)
                {
                    int maxIndex = i;
                    for (int j = i + 1; j < copyOfSearchResults.size(); j++)
                    {
                        if (copyOfSearchResults.get(j).getPublishedYear() > copyOfSearchResults.get(maxIndex).getPublishedYear())
                            maxIndex = j;
                    }

                    Item temp = copyOfSearchResults.get(maxIndex);
                    copyOfSearchResults.set(maxIndex, copyOfSearchResults.get(i));
                    copyOfSearchResults.set(i, temp);
                }
            }
            else if(isOption2Selected) // selection sort sort lowest to highest year
            {
                for(int i = 0; i < copyOfSearchResults.size() - 1; i++)
                {
                    int minIndex = i;
                    for(int j = i + 1; j < copyOfSearchResults.size(); j++)
                    {
                        if(copyOfSearchResults.get(j).getPublishedYear() < copyOfSearchResults.get(minIndex).getPublishedYear())
                            minIndex = j;
                    }

                    Item temp = copyOfSearchResults.get(minIndex);
                    copyOfSearchResults.set(minIndex, copyOfSearchResults.get(i));
                    copyOfSearchResults.set(i, temp);
                }
            }
        }
        else if(sortType.equals("Ratings"))
        {
            if (isOption1Selected) // selection sort searchResults highest to lowest average rating
            {
                for(int i = 0; i < copyOfSearchResults.size() - 1; i++)
                {
                    int maxIndex = i;
                    for(int j = i + 1; j < copyOfSearchResults.size(); j++)
                    {
                        if(copyOfSearchResults.get(j).getAverageRating() > copyOfSearchResults.get(maxIndex).getAverageRating())
                            maxIndex = j;
                    }

                    Item temp = copyOfSearchResults.get(i);
                    copyOfSearchResults.set(i, copyOfSearchResults.get(maxIndex));
                    copyOfSearchResults.set(maxIndex, temp);
                }
            }
            else if(isOption2Selected) // selection sort lowest to highest average rating
            {
                for(int i = 0; i < copyOfSearchResults.size(); i++)
                {
                    int minIndex = i;
                    for(int j = i + 1; j < copyOfSearchResults.size(); j++)
                    {
                        if(copyOfSearchResults.get(j).getAverageRating() < copyOfSearchResults.get(minIndex).getAverageRating())
                            minIndex = j;
                    }

                    Item temp = copyOfSearchResults.get(i);
                    copyOfSearchResults.set(i, copyOfSearchResults.get(minIndex));
                    copyOfSearchResults.set(minIndex, temp);
                }
            }
        }

        //update search result display
        for(int i = 0; i < resultsVBox.getChildren().size(); i++)
        {
            AnchorPane newResultPane = ((AnchorPane)resultsVBox.getChildren().get(i));
            newResultPane.setVisible(true);

            //set the info
            Item item = copyOfSearchResults.get(i);
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
                    Stage stage = (Stage) filterButton.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle(item.getTitle());
                }
                catch (IOException ioe) { ioe.printStackTrace(); }
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

            resultsVBox.getChildren().set(i, newResultPane); // update the vbox
        }
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
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/goodbye_darling.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();

            stage.show(); // go back to login screen

        } catch (IOException ioe) { ioe.printStackTrace(); }
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






    /** to be able to get the item to view while in the viewItemScene*/
    public static Item getItemToView() { return itemToView; }
    public static void resetItemToView() { itemToView = null; }












    /** mouse hover effect for go back button */
    public void hoverOver_goBackButton() {
        goBackButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }

    public void hoverExit_goBackButton() {
        goBackButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for your account button */
    public void hoverOver_yourAccountButton() {
        yourAccountButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_yourAccountButton() {
        yourAccountButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for search button */
    public void hoverOver_searchButton() {
        searchButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_searchButton() {
        searchButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for filter button */
    public void hoverOver_filterButton() {
        filterButton.setTextFill(Color.rgb(77, 139, 255));
    }

    public void hoverExit_filterButton() {
        filterButton.setTextFill(Color.rgb(61, 111, 205));
    }

    /** mouse hover effect for nevermind button */
    public void hoverOver_signOutButton() {
        signOutButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }
    public void hoverExit_signOutButton() {
        signOutButton.setStyle("-fx-background-radius: 50; -fx-background-color: #e4e4e4");
    }

    /** mouse hover effect for sortOption1 button */
    public void hoverOver_sortOption1Button() {
        if(!isOption1Selected)
            sortOption1Button.setStyle("-fx-background-color: #ffe897");
    }
    public void hoverExit_sortOption1Button() {
        if(!isOption1Selected)
            sortOption1Button.setStyle("-fx-background-color: #e4e4e4");
    }

    /** mouse hover effect for sortOption2 button */
    public void hoverOver_sortOption2Button() {
        if(!isOption2Selected)
            sortOption2Button.setStyle("-fx-background-color: #ffe897");
    }
    public void hoverExit_sortOption2Button() {
        if(!isOption2Selected)
            sortOption2Button.setStyle("-fx-background-color: #e4e4e4");
    }

    /** mouse hover effect for sort apply button */
    public void hoverOver_sortApplyButton() {
        sortApplyButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_sortApplyButton() {
        sortApplyButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for chooseSort button */
    public void hoverOver_chooseSortButton() {
        chooseSortMenu.setStyle("-fx-background-color: #f2f2f2");
    }
    public void hoverExit_chooseSortButton() {
        chooseSortMenu.setStyle("-fx-background-color: #e4e4e4");
    }








}
