package cs1302.gallery;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import java.net.URLEncoder;
import java.net.URL;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import java.util.Arrays;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.google.gson.*;
import java.lang.Exception;
import cs1302.gallery.GalleryApp;
import java.util.Random;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

/**
 * Custom Component to handle the main functions of the Gallery
 * @author Giao Pham
 */
public class ImageGallery extends TilePane{
    
    private GalleryApp app;
    private ImageView[] imgView = new ImageView[20];
    
    /**
     * Constructs a new Gallery with a default gallery of Images for the default
     * search term.
     * @param app the GalleryApp that contains all of the Instance Variables
     */
    public ImageGallery(GalleryApp app){
        super();
        this.app = app;
        this.setPrefColumns(5);
        this.setPrefRows(4);
        
        //Setting up ImageViews
        for(int i = 0; i < imgView.length; i++) {
            imgView[i] = new ImageView();
            imgView[i].setPreserveRatio(true);
            imgView[i].setFitWidth(100);
            imgView[i].setFitHeight(100);
        }
        
        //Making the default search for "rock" and putting such images into the Gallery
        updateImages();
        for(int i =0; i < imgView.length; i++) {
            this.getChildren().add(imgView[i]);
        }
    } // ImageGallery()
    
    /**
     * Method used to create a new search for Images and updates the gallery to the new Images.
     */
    public void updateImages(){
        app.getProgressBar().setProgress(0);
        setUrlList();
        convertToImg();
        
        //Sets images into corresponding ImageView
        Platform.runLater(()->{
                for(int i = 0; i < imgView.length;i++) {
                    imgView[i].setImage(app.getImgList()[i]);
                }
            });        
    } // updateImages()
    
    /**
     * Private method used to download URLs from the itunes search query. This is creates
     * a list of disinct images from the iTunes search.
     * Derived from Project 4 FAQ
     */
    private void setUrlList(){
        try{
            String searchTerm = app.getSearch().getText();
            searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
            
            //Download Results
            URL url = new URL("https://itunes.apple.com/search?term="+searchTerm+ "&limit=100");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonParser parser = new JsonParser();
            JsonElement jElement = parser.parse(reader);
            
            //Retrieving Results as an Array
            JsonObject root = jElement.getAsJsonObject();
            JsonArray results = root.getAsJsonArray("results");
            int numResults = results.size();
            String[] list = app.getList();
            list = new String[numResults];
            
            //Extracting image URLs into an Array
            for(int i = 0; i < numResults; i++) {
                JsonObject result = results.get(i).getAsJsonObject();  //Object i in "results" array
                JsonElement artwork = result.get("artworkUrl100");     //artworkUrl100 element
                if(artwork != null) {
                    list[i] = artwork.getAsString();
                }
            }
            
            //Filtering list to only unique URLs
            list = Arrays.stream(list)
                .distinct()
                .toArray(String[]::new);
            if(list.length >20){   
                app.setList(list);
                shuffle(app.listSize());
            }
            else{
                Platform.runLater(()->popUp());
            }
        }catch(Exception e){}
    } // setUrlList()
    
    /**
     * Private method that takes the URL list of images and converts them to actual
     * Images. When an image is created, the percentage on the {@code progress bar} increases
     * by 5%.
     */
    private void convertToImg(){
        String[] list = app.getList();
        for(int i = 0; i < 20; i++) {
            Image art = new Image(list[i]);
            app.setImg(i,art);
            
            //Add percentage for every image added
            if(app.getPercent() < 1) {
                app.getProgressBar().setProgress((i+1) * 0.05);
            }
        }
    } // convertToImg()
    
    /**
     * Private method that creates a dialog window warning the user that there are not enough
     * images to be displayed.
     */
    private void popUp(){
        //Creating componenets
        Button ok = new Button("OK");
        Text errorMsg = new Text("<<<<<<<ERROR!!!>>>>>>>");
        Text warning = new Text("Sorry. There are not enough images to create a gallery.");
        Text tryAgain = new Text("Try Again.");
        
        //Creating layout
        VBox message = new VBox();
        message.getChildren().addAll(errorMsg,warning,tryAgain,ok);
        message.setAlignment(Pos.CENTER);
        message.setPadding(new Insets(10));
        ok.setTranslateY(8);
        
        //Scene and Stage settings
        Scene scene = new Scene(message);
        Stage error = new Stage();
        ok.setOnAction(e-> error.close());
        error.setScene(scene);
        error.initModality(Modality.APPLICATION_MODAL); //Block input until this is handled
        error.setTitle("Error!");
        error.setMaxWidth(400);
        error.setMaxHeight(300);
        error.setResizable(false);
        error.sizeToScene();
        error.showAndWait();
    } // popUp()
    
    /**
     * Private method used to shuffle the list of URLs.
     */
    private void shuffle(int size){
        Random rand = new Random();
        for(int i = 0; i < 50; i++)
        {
            int num = rand.nextInt(size);
            int num2 = rand.nextInt(size);
            swap(num,num2);
        }
    } // getRandom()
    
    /**
     * Private method that swaps two specific URLs in the list.
     */
    private void swap(int first, int sec){
        String temp = app.getList()[first];
        app.setUrl(first, app.getList()[sec]);
        app.setUrl(sec, temp);
    } // swap() 
    
    /**
     * Method that rotates the images by putting in the 20th {@code Image}
     * from the URL list into a random {@code ImageView}, which will display the new 
     * {@code Image}.
     */
    public void rotateImages(){
        Random rand = new Random();
        int index;
        
        //Shuffle the unused URLs
        for(int i =0; i < 50 ;i++){
            index = rand.nextInt(app.listSize()-20);
            int secIndex = rand.nextInt(app.listSize()-20);
            index+=20;
            secIndex+=20;
            swap(index,secIndex);
        }
        
        //Pick a random Image to replace in List
        index = rand.nextInt(imgView.length);
        Image newImg = new Image(app.getList()[20]);
        app.setImg(index,newImg);
        //Place new image into the corresponding ImageView
        imgView[index].setImage(app.getImgList()[index]);
        swap(index,20);
    } // rotateImages()
} // ImageGallery
