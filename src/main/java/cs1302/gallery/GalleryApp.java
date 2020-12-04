package cs1302.gallery;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.TilePane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.lang.Thread;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.geometry.Pos;

/** 
 * Represents an iTunes GalleryApp!
 * @author Giao Pham
 */
public class GalleryApp extends Application {
    //Boxes
    protected VBox vbox = new VBox();
    protected HBox pane = new HBox(10);
    protected HBox load = new HBox();
    protected ImageGallery gallery;
    protected double height = 480;
    protected double width = 640;
    
    KeyFrame keyFrame;
    Timeline timeline;
    
    //Menu- Instance Variables
    protected MenuBar menu;
    protected Menu menuFile;
    protected MenuItem exit;
    protected Menu menuHelp;
    protected MenuItem about;
    
    //ToolBar- Instance Variables
    protected Button pausePlay;
    protected Button updateImg;
    protected Text searchQ;
    protected TextField searchBar;
    
    //Loading bar- Instance Variables
    protected Text status;
    protected ProgressBar progress;
    
    //Shared
    protected String[] urlList;
    protected Image[] imageList = new Image[20];
    protected double loadingPercent;
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) {
        //Sets up Menu and ToolBar
        setMenu();
        setToolBar();
        
        //Setting progress bar
        status = new Text("Images provided courtesy of iTunes");
        progress = new ProgressBar(1);
        gallery = new ImageGallery(this);
        
        //Creating UI
        load.getChildren().addAll(progress,status);
        vbox.getChildren().addAll(menu,pane,gallery,load);
        
        if(listSize()>=21){
            startRotate();  //Begin by changing images per 2 secs
        }
        
        //Update Image Button
        updateImg.setOnAction(e->{
                Thread task = new Thread(()->gallery.updateImages());
                task.setDaemon(true);
                task.start();
            });
        
        //Pause/Play Button
        pausePlay.setOnAction(e->{
                if(pausePlay.getText().equals("Pause")){
                    timeline.pause();
                    pausePlay.setText("Play");
                }
                else{
                    timeline.play();
                    pausePlay.setText("Pause");
                }
            });
        
        //Scene
        Scene scene = new Scene(vbox,500,500);
        stage.setMaxWidth(width);
        stage.setMaxHeight(800);
        stage.setResizable(false);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    } // start
    
    /**
     * Private method used to initialize the Instance Variables of the toolbar
     */
    private void setToolBar(){
        pausePlay = new Button ("Pause");
        pausePlay.setMinWidth(70);
        updateImg = new Button("Update Images");
        searchQ = new Text("Search Query:");
        searchQ.setTranslateY(7);
        
        //Adjusting Search Bar
        searchBar = new TextField("rock");
        searchBar.setPromptText("Search for...");
        pane.setPadding(new Insets(10));
        pane.setHgrow(searchBar, Priority.ALWAYS);
        
        pane.getChildren().addAll(pausePlay,searchQ, searchBar, updateImg);
    } // setToolBar()
    
    /**
     * Private method used to create the Menu Bar
     */
    private void setMenu(){
        menu = new MenuBar();
        
        //Adding File-Exit
        menuFile = new Menu("File");
        exit = new MenuItem("Exit");
        exit.setOnAction(e->System.exit(0));
        menuFile.getItems().add(exit);
        
        //Adding Help-About (Extra Credit)
        menuHelp = new Menu("Help");
        about = new MenuItem("About");
        about.setOnAction(e->aboutMe());
        menuHelp.getItems().add(about);
        
        menu.getMenus().addAll(menuFile,menuHelp);
    } // setMenu()
    
    /**
     * Private method used to create the About Me page [Extra Credit]
     */
    private void aboutMe(){
        //Creating componenets
        Button close = new Button("Close");
        Image profile = new Image("file:profilePic.jpg");
        ImageView frame = new ImageView(profile);
        frame.setPreserveRatio(true);
        frame.setFitWidth(150);
        Text name = new Text("Name: Giao Pham");
        Text email = new Text("Email: gpp60493@uga.edu");
        Text version = new Text("Version 1.0.0");
        
        //Creating layout      
        VBox bio = new VBox();
        bio.getChildren().addAll(name, email, version,close);
        HBox layout = new HBox();
        layout.getChildren().addAll(frame, bio);
        close.setTranslateY(10);
        close.setTranslateX(60);
        bio.setPadding(new Insets(10));
        
        //Scene and Stage settings       
        Scene scene = new Scene(layout);
        Stage aboutPage = new Stage();
        close.setOnAction(e-> aboutPage.close());
        aboutPage.setScene(scene);
        aboutPage.initModality(Modality.APPLICATION_MODAL); //Block input until this is handled     
        aboutPage.setTitle("About GIAO PHAM");
        aboutPage.setMaxWidth(500);
        aboutPage.setMaxHeight(500);
        aboutPage.setResizable(false);
        aboutPage.sizeToScene();
        aboutPage.showAndWait();
    } // aboutMe()
    
    /**
     * Private method used to start the rotation of images when the app is first running.
     * Also toggles the rotation for when the Play button is pressed.
     */
    private void startRotate(){
        keyFrame = new KeyFrame(Duration.seconds(2), e->gallery.rotateImages());
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    } // startRotate()
    
    /**
     * Method to retrieve the search words that the user inputs into the search bar
     * @return the search query
     */
    public TextField getSearch(){
        return searchBar;
    } // getSearch()
    
    /**
     * Method to return the list of unique URLs that are associated with the search query.
     * @return the list of unique URLs
     */
    public String[] getList(){
        return urlList;
    } // getList()
    
    /**
     * Method that sets the current URL list to a new list.
     * @param newList a new list of URLs 
     */
    public void setList(String[] newList){
        urlList = newList;
    } // setList()
    
    /**
     * Method that sets a specific index in the URL list to a new URL
     * @param index specified index fro the new URL
     * @param s string of a new URL that will be placed in the array
     */
    public void setUrl(int index, String s){
        urlList[index] = s;
    } // setUrl()
    
    /**
     * Method that returns the size of the URL list
     * @return size of the URL list.
     */
    public int listSize(){
        return urlList.length;
    } // listSize()

    /**
     * Method that returns the list of 20 Images that are currently on display
     * @return list of active Images
     */
    public Image[] getImgList(){
        return imageList;
    } //getImgList()
    
    /**
     * Method that sets a new Image to the current list of Images at a specific index.
     * @param index specific index of where the new Image is placed
     * @param img new Image that will be added to list
     */
    public void setImg(int index, Image img){
        imageList[index] = img;
    } // setImg()
    
    /**
     * Method that returns the current loading progress of the Images.
     * @return percent of how many Images have been loaded into the Gallery. 
     */
    public double getPercent(){
        return loadingPercent;
    } // getPercent()
    
    /**
     * Method that returns the progress bar to allow its methods to be called
     * @return progress bar
     */
    public ProgressBar getProgressBar(){
        return progress;
    } // getProgressBar()    
} // GalleryApp

