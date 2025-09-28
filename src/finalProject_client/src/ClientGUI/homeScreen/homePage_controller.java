package ClientGUI.homeScreen;

import java.io.IOException;

import Users.Admin;
import Users.ItemRequest;
import finalProject_client.Client;
import java.util.*;
import java.util.Collections;
import ClientGUI.ClientGUI;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.*;
import sun.awt.image.ImageWatched;

import java.net.URL;
import java.util.ResourceBundle;

public class homePage_controller implements Initializable
{
    private static ArrayList<Item> catalog;
    private Queue<Item> displayCatalogQueue;
    private static User loggedInUser;
    private static Item itemToView;
    private static ArrayList<Item> searchResultItems;
    private static String userTyped;

    @FXML Button yourAccountButton;
    @FXML Button signOutButton;
    @FXML AnchorPane background;
    @FXML Label welcomeText;
    @FXML Button searchButton;
    @FXML MenuButton filterButton;
    @FXML Button item1Button;
    @FXML Button item2Button;
    @FXML Button item3Button;
    @FXML Button rightScrollButton;
    @FXML Button leftScrollButton;
    @FXML AnchorPane item1Pane;
    @FXML AnchorPane item2Pane;
    @FXML AnchorPane item3Pane;
    @FXML TextField searchText;
    @FXML Button viewAllButton;

    @FXML CheckMenuItem filterBooks;
    @FXML CheckMenuItem filterDVDs;
    @FXML CheckMenuItem filterGames;

    @FXML MenuButton submitRequestItemType;
    @FXML TextField requestItemTitle;
    @FXML Button submitRequestButton;
    @FXML Label submitSuccessOrFail;

    @FXML AnchorPane popularItem1Pane;
    @FXML AnchorPane popularItem2Pane;
    @FXML AnchorPane popularItem3Pane;
    @FXML AnchorPane popularItem4Pane;
    @FXML AnchorPane popularItem5Pane;
    @FXML AnchorPane popularItem6Pane;

    @FXML Button popularItem1Button;
    @FXML Button popularItem2Button;
    @FXML Button popularItem3Button;
    @FXML Button popularItem4Button;
    @FXML Button popularItem5Button;
    @FXML Button popularItem6Button;




    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        catalog = Client.getUpdatedCatalog();
        loggedInUser = Client.getUpdatedUser_fromServer(loginScene_controller.getLoggedInUser().getUsername());
        welcomeText.setText("Welcome, " + loggedInUser.getUsername() + "!");
        searchResultItems = new ArrayList<>();

        displayCatalogQueue = new LinkedList<>();
        displayCatalogQueue.addAll(catalog); // populate queue with catalog

        //set the first 3 items to be at the back of queue, update their average ratings
        int itemsRemoved = 0;
        while(itemsRemoved != 3)
        {
            Item catalogItem = Client.getUpdatedItem_fromServer(displayCatalogQueue.remove());
            if(catalogItem.getTitle().equals("Call of Duty: Black Ops II"))
            {
                itemsRemoved++;
                ((Label)item1Pane.getChildren().get(0)).setText(catalogItem.getTitle()); // set title
                ((Label)item1Pane.getChildren().get(1)).setText("" + catalogItem.getAverageRating()); // update average review
                ((Label)item1Pane.getChildren().get(2)).setText("" + catalogItem.getPublishedYear()); // set year
                ((Label)item1Pane.getChildren().get(3)).setText(catalogItem.getItemType()); // set item type
                ((Label)item1Pane.getChildren().get(4)).setText(catalogItem.getDescription()); // set description
                ((ImageView)item1Pane.getChildren().get(5)).setImage(new Image(catalogItem.getImagePath())); //set image
            }
            else if(catalogItem.getTitle().equals("Holes"))
            {
                itemsRemoved++;
                ((Label)item2Pane.getChildren().get(0)).setText(catalogItem.getTitle()); // set title
                ((Label)item2Pane.getChildren().get(1)).setText("" + catalogItem.getAverageRating()); // update average review
                ((Label)item2Pane.getChildren().get(2)).setText("" + catalogItem.getPublishedYear()); // set year
                ((Label)item2Pane.getChildren().get(3)).setText(catalogItem.getItemType()); // set item type
                ((Label)item2Pane.getChildren().get(4)).setText(catalogItem.getDescription()); // set description
                ((ImageView)item2Pane.getChildren().get(5)).setImage(new Image(catalogItem.getImagePath())); //set image
            }

            else if(catalogItem.getTitle().equals("Regular Show: The Complete First Season"))
            {
                itemsRemoved++;
                ((Label)item3Pane.getChildren().get(0)).setText(catalogItem.getTitle()); // set title
                ((Label)item3Pane.getChildren().get(1)).setText("" + catalogItem.getAverageRating()); // update average review
                ((Label)item3Pane.getChildren().get(2)).setText("" + catalogItem.getPublishedYear()); // set year
                ((Label)item3Pane.getChildren().get(3)).setText(catalogItem.getItemType()); // set item type
                ((Label)item3Pane.getChildren().get(4)).setText(catalogItem.getDescription()); // set description
                ((ImageView)item3Pane.getChildren().get(5)).setImage(new Image(catalogItem.getImagePath())); //set image
            }
            else displayCatalogQueue.add(catalogItem); // not an item i want to remove, put it back
        }

        //display the most popular items
        ArrayList<Item> mostPopularItems = new ArrayList<>(catalog);
        //selection sort highest to lowest rating
        for(int i = 0; i < mostPopularItems.size() - 1; i++)
        {
            int maxIndex = i;
            for(int j = i + 1; j < mostPopularItems.size(); j++)
            {
                if(mostPopularItems.get(j).getAverageRating() > mostPopularItems.get(maxIndex).getAverageRating())
                    maxIndex = j;
            }

            Item temp = Client.getUpdatedItem_fromServer(mostPopularItems.get(i));
            mostPopularItems.set(i, mostPopularItems.get(maxIndex));
            mostPopularItems.set(maxIndex, temp);
        }

        //populate popular items anchor panes
        ArrayList<AnchorPane> popularAnchorPanes = new ArrayList<>();
        popularAnchorPanes.add(popularItem1Pane);
        popularAnchorPanes.add(popularItem2Pane);
        popularAnchorPanes.add(popularItem3Pane);
        popularAnchorPanes.add(popularItem4Pane);
        popularAnchorPanes.add(popularItem5Pane);
        popularAnchorPanes.add(popularItem6Pane);

        for(int i = 0; i < popularAnchorPanes.size(); i++)
        {
            //set image
            ImageView imageView = (ImageView)popularAnchorPanes.get(i).getChildren().get(0);
            imageView.setImage(new Image(mostPopularItems.get(i).getImagePath()));

            //set title
            ((Label)popularAnchorPanes.get(i).getChildren().get(1)).setText(mostPopularItems.get(i).getTitle());

            //set average rating
            ((Label)popularAnchorPanes.get(i).getChildren().get(3)).setText("" + mostPopularItems.get(i).getAverageRating());

            //set view button hover effects
            ((Button)popularAnchorPanes.get(i).getChildren().get(4)).setOnMouseEntered(e ->
            {
                ((Button)e.getSource()).setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
            });
            ((Button)popularAnchorPanes.get(i).getChildren().get(4)).setOnMouseExited(e ->
            {
                ((Button)e.getSource()).setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
            });
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

            Stage currentStage = (Stage)background.getScene().getWindow();
            currentStage.close(); // close login screen

            //play sign out message
//            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/goodbye_darling.mp3").toString());
//            MediaPlayer player = new MediaPlayer(media);
//            player.setVolume(0.30);
//            player.play();

            stage.show(); // go back to login screen

        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** called when user hits right scroll button, shifts catalog entries to the left*/
    public void scrollRight(ActionEvent event)
    {
        //item1 gets "removed" by moving it to back of queue
        //item2 shifts left into item1 spot
        //item3 shifts left into item2 spot
        //item3 is updated to be the next item in queue


        String item1Title = ((Label)item1Pane.getChildren().get(0)).getText();
        Item item1 = getItemFromTitle(item1Title);
        displayCatalogQueue.add(item1);

        //shift the items in positon 2 and 3 to the left
        moveItem2ToItem1Spot();
        moveItem3ToItem2Spot();

        //pop item from catalog display queue, put it in 3rd position
        Item new3rdItem = displayCatalogQueue.remove();
        set3rdItem(new3rdItem);
    }

    /** helper function, returns Item based on matching title */
    private Item getItemFromTitle(String title)
    {
        for(Item i : catalog)
        {
            if(i.getTitle().equals(title))
                return Client.getUpdatedItem_fromServer(i);
        }
        return null;
    }

    /** helper function, sets item in 3rd spot to be new item popped from queue*/
    private void set3rdItem(Item new3rdItem)
    {
        ((Label)item3Pane.getChildren().get(0)).setText(new3rdItem.getTitle()); // set title
        ((Label)item3Pane.getChildren().get(1)).setText("" + new3rdItem.getAverageRating()); // set rating
        ((Label)item3Pane.getChildren().get(2)).setText("" + new3rdItem.getPublishedYear()); // set year
        ((Label)item3Pane.getChildren().get(3)).setText(new3rdItem.getItemType()); // set item type
        ((Label)item3Pane.getChildren().get(4)).setText(new3rdItem.getDescription()); // set description

        ImageView imageViewInItem3 = (ImageView)item3Pane.getChildren().get(5);
        imageViewInItem3.setImage(new Image(new3rdItem.getImagePath())); // set image
    }

    /** helper function, moves the item in 2nd position to the 1st position*/
    private void moveItem2ToItem1Spot()
    {
        String newItem1Title = ((Label)item2Pane.getChildren().get(0)).getText();
        ((Label)item1Pane.getChildren().get(0)).setText(newItem1Title);

        String newItem1Rating = ((Label)item2Pane.getChildren().get(1)).getText();
        ((Label)item1Pane.getChildren().get(1)).setText(newItem1Rating);

        String newItem1Year = ((Label)item2Pane.getChildren().get(2)).getText();
        ((Label)item1Pane.getChildren().get(2)).setText(newItem1Year);

        String newItem1Type = ((Label)item2Pane.getChildren().get(3)).getText();
        ((Label)item1Pane.getChildren().get(3)).setText(newItem1Type);

        String newItem1Description = ((Label)item2Pane.getChildren().get(4)).getText();
        ((Label)item1Pane.getChildren().get(4)).setText(newItem1Description);

        // get image from the ImageView in item2Pane
        ImageView imageViewFromItem2 = (ImageView) item2Pane.getChildren().get(5);
        Image newItem1Image = imageViewFromItem2.getImage();

        // set image to the ImageView in item1Pane
        ImageView imageViewInItem1 = (ImageView) item1Pane.getChildren().get(5);
        imageViewInItem1.setImage(newItem1Image);
    }

    /** helper function, moves the item in 3rd position to the 2nd position*/
    private void moveItem3ToItem2Spot()
    {
        String newItem2Title = ((Label)item3Pane.getChildren().get(0)).getText();
        ((Label)item2Pane.getChildren().get(0)).setText(newItem2Title);

        String newItem2Rating = ((Label)item3Pane.getChildren().get(1)).getText();
        ((Label)item2Pane.getChildren().get(1)).setText(newItem2Rating);

        String newItem2Year = ((Label)item3Pane.getChildren().get(2)).getText();
        ((Label)item2Pane.getChildren().get(2)).setText(newItem2Year);

        String newItem2Type = ((Label)item3Pane.getChildren().get(3)).getText();
        ((Label)item2Pane.getChildren().get(3)).setText(newItem2Type);

        String newItem2Description = ((Label)item3Pane.getChildren().get(4)).getText();
        ((Label)item2Pane.getChildren().get(4)).setText(newItem2Description);

        // get image from the ImageView in item3Pane
        ImageView imageViewFromItem3 = (ImageView)item3Pane.getChildren().get(5);
        Image newItem2Image = imageViewFromItem3.getImage();

        // set image to the ImageView in item2Pane
        ImageView imageViewInItem2 = (ImageView)item2Pane.getChildren().get(5);
        imageViewInItem2.setImage(newItem2Image);
    }

    /** called when user hits left scroll button, shifts catalog entries to the right */
    public void scrollLeft(ActionEvent event)
    {
        //item3 gets set to old item2
        //item2 gets set to old item1
        //old item 3 gets put at the front of the queue
        //item1 gets set to item at the back of queue


        //reference to title of old item3 to front of display queue
        String item3Title = ((Label)item3Pane.getChildren().get(0)).getText();

        //shift items in position 1 and 2 to the right
        moveItem2ToItem3Spot();
        moveItem1ToItem2Spot();

        //add old item3 to be at beginning of queue
        Item item3 = getItemFromTitle(item3Title);
        LinkedList<Item> ll = new LinkedList<>(displayCatalogQueue);
        ll.addFirst(item3);

        //set first item to be last item in queue,
        Item lastItemInQueue = ll.removeLast();
        set1stItem(lastItemInQueue);
        displayCatalogQueue = new LinkedList<>(ll);
    }


    /** helper function, sets item in 1st position to be item */
    private void set1stItem(Item i)
    {
        ((Label)item1Pane.getChildren().get(0)).setText(i.getTitle()); // set title
        ((Label)item1Pane.getChildren().get(1)).setText("" + i.getAverageRating()); // set rating
        ((Label)item1Pane.getChildren().get(2)).setText("" + i.getPublishedYear()); // set year
        ((Label)item1Pane.getChildren().get(3)).setText(i.getItemType()); // set item type
        ((Label)item1Pane.getChildren().get(4)).setText(i.getDescription()); // set description

        ImageView imageViewInItem1 = (ImageView)item1Pane.getChildren().get(5);
        imageViewInItem1.setImage(new Image(i.getImagePath())); // set image
    }

    /** helper function, moves the item in 1st position to the 2nd position*/
    private void moveItem1ToItem2Spot()
    {
        String newItem2Title = ((Label)item1Pane.getChildren().get(0)).getText();
        ((Label)item2Pane.getChildren().get(0)).setText(newItem2Title);

        String newItem2Rating = ((Label)item1Pane.getChildren().get(1)).getText();
        ((Label)item2Pane.getChildren().get(1)).setText(newItem2Rating);

        String newItem2Year = ((Label)item1Pane.getChildren().get(2)).getText();
        ((Label)item2Pane.getChildren().get(2)).setText(newItem2Year);

        String newItem2Type = ((Label)item1Pane.getChildren().get(3)).getText();
        ((Label)item2Pane.getChildren().get(3)).setText(newItem2Type);

        String newItem2Description = ((Label)item1Pane.getChildren().get(4)).getText();
        ((Label)item2Pane.getChildren().get(4)).setText(newItem2Description);

        // get image from the ImageView in item1Pane
        ImageView imageViewFromItem1 = (ImageView)item1Pane.getChildren().get(5);
        Image newItem2Image = imageViewFromItem1.getImage();

        // set image to the ImageView in item2Pane
        ImageView imageViewInItem2 = (ImageView)item2Pane.getChildren().get(5);
        imageViewInItem2.setImage(newItem2Image);
    }

    /** helper function, moves the item in 2nd position to the 3rd position*/
    private void moveItem2ToItem3Spot()
    {
        String newItem3Title = ((Label)item2Pane.getChildren().get(0)).getText();
        ((Label)item3Pane.getChildren().get(0)).setText(newItem3Title);

        String newItem3Rating = ((Label)item2Pane.getChildren().get(1)).getText();
        ((Label)item3Pane.getChildren().get(1)).setText(newItem3Rating);

        String newItem3Year = ((Label)item2Pane.getChildren().get(2)).getText();
        ((Label)item3Pane.getChildren().get(2)).setText(newItem3Year);

        String newItem3Type = ((Label)item2Pane.getChildren().get(3)).getText();
        ((Label)item3Pane.getChildren().get(3)).setText(newItem3Type);

        String newItem3Description = ((Label)item2Pane.getChildren().get(4)).getText();
        ((Label)item3Pane.getChildren().get(4)).setText(newItem3Description);

        // get image from the ImageView in item2Pane
        ImageView imageViewFromItem2 = (ImageView)item2Pane.getChildren().get(5);
        Image newItem3Image = imageViewFromItem2.getImage();

        // set image to the ImageView in item3Pane
        ImageView imageViewInItem3 = (ImageView)item3Pane.getChildren().get(5);
        imageViewInItem3.setImage(newItem3Image);
    }


    /** gets called when user clicks 1st view button, switches to viewItem scene */
    public void viewItem1(ActionEvent event)
    {
        String item1Title = ((Label)item1Pane.getChildren().get(0)).getText();
        itemToView = getItemFromTitle(item1Title);
        switchToViewItemScene();
    }

    /** gets called when user clicks 2nd view button, switches to viewItem scene */
    public void viewItem2(ActionEvent event)
    {
        String item2Title = ((Label)item2Pane.getChildren().get(0)).getText();
        itemToView = getItemFromTitle(item2Title);
        switchToViewItemScene();
    }

    /** gets called when user clicks 3rd view button, switches to viewItem scene */
    public void viewItem3(ActionEvent event)
    {
        String item3Title = ((Label)item3Pane.getChildren().get(0)).getText();
        itemToView = getItemFromTitle(item3Title);
        switchToViewItemScene();
    }

    /** helper method, switches to view item scene */
    private void switchToViewItemScene()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/viewItem/viewItemScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) filterButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(itemToView.getTitle());
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** called when user hits search button */
    public void search(ActionEvent event)
    {
        searchResultItems = new ArrayList<>();
        userTyped = searchText.getText().trim(); // remove any leading/trailing spaces
        if(!userTyped.isEmpty())  // only do work if they typed something
        {
            //play searching message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/searching.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();



           // search for matches in catalog
            for (Item i : catalog) {
                if (i.getTitle().toUpperCase().contains(userTyped.toUpperCase())) // ignore case
                    searchResultItems.add(i); // add to static list of search results
            }

            //if a filter(s) was selected, then apply filters to remove unwanted item types
            if(filterBooks.isSelected() || filterDVDs.isSelected() || filterGames.isSelected())
            {
                //only want books
                if(filterBooks.isSelected() && !filterDVDs.isSelected() && !filterGames.isSelected())
                {
                    for(int i = searchResultItems.size() - 1; i >= 0; i--)
                    {
                        if( !(searchResultItems.get(i).getItemType().equals("Book")) ) // not a book, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want dvds
                else if(filterDVDs.isSelected() && !filterBooks.isSelected() && !filterGames.isSelected())
                {
                    for(int i = searchResultItems.size() - 1; i >= 0; i--)
                    {
                        if( !(searchResultItems.get(i).getItemType().equals("DVD")) ) // not a DVD, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want games
                else if(filterGames.isSelected() && !filterBooks.isSelected() && !filterDVDs.isSelected())
                {
                    for(int i = searchResultItems.size() - 1; i >= 0; i--)
                    {
                        if( !(searchResultItems.get(i).getItemType().equals("Game")) ) // not a DVD, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want books and dvds
                else if(filterBooks.isSelected() && filterDVDs.isSelected() && !filterGames.isSelected())
                {
                    for(int i = searchResultItems.size() - 1; i >= 0; i--)
                    {
                        if(searchResultItems.get(i).getItemType().equals("Game")) // not a book or dvd, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want books and games
                else if(filterBooks.isSelected() && filterGames.isSelected() && !filterDVDs.isSelected())
                {
                    for(int i = searchResultItems.size() - 1; i >= 0; i--)
                    {
                        if(searchResultItems.get(i).getItemType().equals("DVD")) // not a book or game, remove it
                            searchResultItems.remove(i);
                    }
                }

                //only want dvds and games
                else if(filterDVDs.isSelected() && filterGames.isSelected() && !filterBooks.isSelected())
                {
                    for(int i = searchResultItems.size() - 1; i >= 0; i--)
                    {
                        if(searchResultItems.get(i).getItemType().equals("Book")) // not a DVD or game, remove it
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
            }
            catch (IOException ioe) { ioe.printStackTrace(); }
        }
    }


    /** switch to Your Items scene*/
    public void switchToYourItems(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/yourAccount/yourItems/yourItemsScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) filterButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Your Items");
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** views all items in catalog */
    public void viewAll(ActionEvent event)
    {
        searchResultItems.addAll(catalog);
        userTyped = "view all";
        //switch to search results scene
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientGUI/homeScreen/searchScreen/searchResultScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) filterButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Search Results");
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /** called when user selects a filter; prevents user from checking all boxes*/
    public void selectFilter(ActionEvent event)
    {
        //if they check all boxes, then it is just a normal search, so uncheck the boxes
        if(filterBooks.isSelected() && filterDVDs.isSelected() && filterGames.isSelected())
        {
            filterBooks.setSelected(false);
            filterDVDs.setSelected(false);
            filterGames.setSelected(false);
        }
    }

    /** submit request choose item type*/
    public void requestItemType(ActionEvent event)
    {
        MenuItem menuItem = (MenuItem)event.getSource();
        String chosenSort = menuItem.getText();
        submitRequestItemType.setText(chosenSort);
        submitSuccessOrFail.setVisible(false);
    }

    /** enables submit request button*/
    public void typingRequestedTitle()
    {
        //if didnt type anything, disable = true
        //if typed something, disable = false
        submitRequestButton.setDisable(requestItemTitle.getText().trim().isEmpty() || submitRequestItemType.getText().equals("Item Type"));
        submitSuccessOrFail.setVisible(false);
    }

    /** submits request to librarian account */
    public void submitRequest(ActionEvent event)
    {
        String requestedItemType = submitRequestItemType.getText();
        String requestedItemTitle = requestItemTitle.getText().trim();
        Admin admin = Client.getAdmin_fromServer();
        ItemRequest newRequest = new ItemRequest(loggedInUser, admin, requestedItemType, requestedItemTitle);
        boolean result = loggedInUser.addItemRequest(newRequest);
        if(result)
        {
            admin.receiveItemRequest(newRequest);
            //display success message
            submitSuccessOrFail.setText("Sent!");
            submitSuccessOrFail.setTextFill(Color.rgb(0x1d, 0xc7, 0x36));
            submitSuccessOrFail.setVisible(true);

            //play sent item request message
            Media media = new Media(getClass().getResource("/ClientGUI/soundEffects/madeItemRequest.mp3").toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.30);
            player.play();
        }
        else // display error message
        {
            submitSuccessOrFail.setText("Request already made");
            submitSuccessOrFail.setTextFill(Color.RED);
            submitSuccessOrFail.setVisible(true);
        }

        //update on server side
        Client.updateUser_serverSide(loggedInUser);
        Client.updateUser_serverSide(admin);


        submitRequestButton.setDisable(true);
        requestItemTitle.clear();
        requestItemTitle.setFocusTraversable(false); // stop the flashing caret
        submitRequestItemType.setText("Item Type");
    }

    /** set popular item view buttons on action effect*/
    public void viewPopularItem1(ActionEvent event)
    {
        itemToView = getItemFromTitle(((Label)popularItem1Pane.getChildren().get(1)).getText());
        switchToViewItemScene();
    }
    public void viewPopularItem2(ActionEvent event)
    {
        itemToView = getItemFromTitle(((Label)popularItem2Pane.getChildren().get(1)).getText());
        switchToViewItemScene();
    }
    public void viewPopularItem3(ActionEvent event)
    {
        itemToView = getItemFromTitle(((Label)popularItem3Pane.getChildren().get(1)).getText());
        switchToViewItemScene();
    }
    public void viewPopularItem4(ActionEvent event)
    {
        itemToView = getItemFromTitle(((Label)popularItem4Pane.getChildren().get(1)).getText());
        switchToViewItemScene();
    }
    public void viewPopularItem5(ActionEvent event)
    {
        itemToView = getItemFromTitle(((Label)popularItem5Pane.getChildren().get(1)).getText());
        switchToViewItemScene();
    }
    public void viewPopularItem6(ActionEvent event)
    {
        itemToView = getItemFromTitle(((Label)popularItem6Pane.getChildren().get(1)).getText());
        switchToViewItemScene();
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

    /** mouse hover effect for item1 button */
    public void hoverOver_item1Button() {
        item1Button.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_item1Button() {
        item1Button.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for item2 button */
    public void hoverOver_item2Button() {
        item2Button.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_item2Button() {
        item2Button.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for item3 button */
    public void hoverOver_item3Button() {
        item3Button.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_item3Button() {
        item3Button.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for right scroll button */
    public void hoverOver_rightScrollButton() {
        rightScrollButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f4ec2a");
    }

    public void hoverExit_rightScrollButton() {
        rightScrollButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }

    /** mouse hover effect for left scroll button */
    public void hoverOver_leftScrollButton() {
        leftScrollButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f4ec2a");
    }

    public void hoverExit_leftScrollButton() {
        leftScrollButton.setStyle("-fx-background-radius: 50; -fx-background-color: #f2ca36");
    }


    /** mouse hover effect for view all button */
    public void hoverOver_viewAllButton() {
        viewAllButton.setStyle("-fx-background-color: #f4ec2a;  -fx-border-color: #b9b9b9");
    }

    public void hoverExit_viewAllButton() {
        viewAllButton.setStyle("-fx-background-color: #f2ca36;  -fx-border-color: #b9b9b9");
    }

    /** mouse hover effect for submit request button */
    public void hoverOver_submitRequestButton() {
        submitRequestButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4d8bff");
    }

    public void hoverExit_submitRequestButton() {
        submitRequestButton.setStyle("-fx-background-radius: 50; -fx-background-color: #3d6fcd");
    }

    /** mouse hover effect for request item type button */
    public void hoverOver_requestItemTypeButton() {
        submitRequestItemType.setStyle("-fx-background-color: #f2f2f2");
    }
    public void hoverExit_requestItemTypeButton() {
        submitRequestItemType.setStyle("-fx-background-color: #e4e4e4");
    }



    public static User getLoggedInUser() { return loggedInUser; }
    public static Item getItemToView() { return itemToView; }
    public static void updateUser(User user) { loggedInUser = user;}
    public static void updateCatalog(ArrayList<Item> newCatalog) { catalog = new ArrayList<>(newCatalog);}
    public static ArrayList<Item> getSearchResultItems() { return searchResultItems; }
    public static String getUserTyped() { return userTyped; }

}
