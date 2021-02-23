package lasers.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import lasers.backtracking.Backtracker;
import lasers.backtracking.Configuration;
import lasers.backtracking.SafeConfig;
import lasers.backtracking.SafeSolver;
import lasers.model.*;
import lasers.ptui.ControllerPTUI;

import javax.swing.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the lasers.lasers.model
 * and receives updates from it.
 *
 * @author RIT CS
 * @author Ziyi Su
 */
public class LasersGUI extends Application implements Observer<LasersModel, ModelData> {
    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;

    /** this can be removed - it is used to demonstrates the button toggle */
    private static boolean status = true;

    private ControllerPTUI controller;
    private Label header;
    private HashMap<String,Image> imageHashMap;
    private HashMap<String, Buttonhelper> buttonHashMap = new HashMap<>();
    private ArrayList<Buttonhelper> pillarmap = new ArrayList<>();
    private Stage satge;

    @Override
    public void init() throws Exception {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            String filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
            this.imageHashMap=new Imagehelper().getHashMap();
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image( getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * This is a private demo method that shows how to create a button
     * and attach a foreground image with a background image that
     * toggles from yellow to red each time it is pressed.
     *
     * @param stage the stage to add components into
     */
    private void buttonDemo(Stage stage) {
        // this demonstrates how to create a button and attach a foreground and
        // background image to it.
        Button button = new Button();
        Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"));
        ImageView laserIcon = new ImageView(laserImg);
        button.setGraphic(laserIcon);
        setButtonBackground(button, "yellow.png");
        button.setOnAction(e -> {
            // toggles background between yellow and red
            if (!status) {
                setButtonBackground(button, "yellow.png");
            } else {
                setButtonBackground(button, "red.png");
            }
            status = !status;
        });

        Scene scene = new Scene(button);
        stage.setScene(scene);
    }

    /**
     * The initialization of all GUI component happens here.
     *
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) throws FileNotFoundException {

            // initiate the controller
            this.controller = new ControllerPTUI(this.model);
            this.header= new Label("text");
            BorderPane borderPane = make_boder();
            Scene scene = new Scene(borderPane);
            stage.setScene(scene);

    }

    /**
     * Sets the button's action depending on the value it sends.
     * @param button The button
     * @param buttonhelper The button helper
     * @param row The row of the button.
     * @param col The column of the button.
     */
    private void Buttonaction(Button button,Buttonhelper buttonhelper,int row, int col){
        button.setOnAction(event -> {
            if (!buttonhelper.isLasers()) {
                this.controller.add(row, col);
                buttonhelper.getButton().setGraphic(new ImageView(imageHashMap.get("L")));
                setButtonBackground(buttonhelper.getButton(), "yellow.png");
                buttonhelper.changetolaser();
                this.controller.add(row, col);
                Platform.runLater(() -> this.header.setText("Laser added at:  ( " + row+", "+col+" )"));
            }else {
                this.controller.remove(row,col);
                Platform.runLater(() -> this.header.setText("Laser removed at:  ( " + row+", "+col+" )"));
            }
        });
    }

    /**
     * Creates the center grid of the GUI.
     * @return the center grid that's been created.
     */
    private GridPane make_centergrid() {
        GridPane gridPane = new GridPane();
        for (int row = 0; row < this.model.getNumOfRow(); ++row) {
            for (int col = 0; col < this.model.getNumOfCol(); ++col) {
                Buttonhelper buttonhelper = new Buttonhelper();
                int finalRow = row;
                int finalCol = col;
                String val=this.model.getCoordVal(finalRow,finalCol);
                if (val.equals(".")) {
                    buttonhelper.getButton().setGraphic(new ImageView(imageHashMap.get("white")));
                    Buttonaction(buttonhelper.getButton(),buttonhelper,finalRow,finalCol);
                    /**
                    buttonhelper.getButton().setOnAction(e -> {
                        if (!buttonhelper.isLasers()) {
                            buttonhelper.getButton().setGraphic(new ImageView(imageHashMap.get("L")));
                            setButtonBackground(buttonhelper.getButton(), "yellow.png");
                            buttonhelper.changetolaser();
                            this.controller.add(finalRow,finalCol);
                            Platform.runLater(() -> this.header.setText("adaaaaaa"+finalRow+finalCol));
                        } else {
                            buttonhelper.getButton().setGraphic(new ImageView(imageHashMap.get("white")));
                            this.controller.remove(finalRow,finalCol);
                            Platform.runLater(() -> this.header.setText("removeeee"+finalRow+finalCol));
                        }

                    });**/
                }else {
                    buttonhelper.getButton().setGraphic(new ImageView(imageHashMap.get("pillar"+val)));
                    pillarmap.add(buttonhelper);
                }
                buttonHashMap.put(finalRow + Integer.toString(finalCol), buttonhelper);
                gridPane.add(buttonhelper.getButton(), col, row);
            }
        }
        return gridPane;
    }

    /**
     * Makes all the bottoms.
     * @return The border pane containing all the buttons.
     */
    public BorderPane make_bottom(){
        BorderPane bottom=new BorderPane();
        HBox hbox = new HBox();
        Button Check = new Button("Check");
        Button Hint = new Button("Hint");
        Button Solve=new Button("Solve");
        Button Restart=new Button("Restart");
        Button Load=new Button("Load");
        hbox.getChildren().addAll(Check, Hint,Solve,Restart,Load);
        bottom.setCenter(hbox);


        Check.setOnAction(actionEvent -> {
            if (this.model.verify()==null){
                Platform.runLater(() -> this.header.setText("Safe is fully verified."));
            }else {
                int[] error=this.model.verify();
                if (this.model.getCoordVal(error[0],error[1]).equals(".")
                        ||this.model.getCoordVal(error[0],error[1]).equals("*")){
                    buttonHashMap.get(error[0]+Integer.toString(error[1])).getButton().
                            setGraphic(new ImageView(imageHashMap.get("red")));
                }else{
                    setButtonBackground(buttonHashMap.get(error[0]+Integer.toString(error[1])).
                            getButton(), "red.png");
                }
                Platform.runLater(() -> this.header.setText("Error verifying at ( " + error[0]+", "+error[1]+" )"));
            }
            this.controller.verify();

        });
        Platform.runLater(() -> this.header.setText(this.model.getSafeFile() + "  loaded."));
        Hint.setOnAction(null);
        Solve.setOnAction(actionEvent -> {
            //make a bt
            //get a solution
            //add lasers to model
            Configuration init = null;
            try {
                init = new SafeConfig(this.model.getSafeFile());
                Backtracker bt = new Backtracker(true);
                Optional<Configuration> sol = bt.solve(init);
                if (sol.isPresent()) {
                    Platform.runLater(() -> this.header.setText(this.model.getSafeFile()+"  solved!"));

                    String info=""+sol.get();
                    String[] some=info.strip().split("\\s+");
                    int i=0;
                    for (int row=0; row<this.model.getNumOfRow();row++){
                        for(int col=0; col<this.model.getNumOfRow();col++){
                            int finalRow = row;
                            int finalCol = col;
                            if (some[i].equals("*")){
                                buttonHashMap.get(finalRow+Integer.toString(finalCol)).
                                        getButton().setGraphic(new ImageView(imageHashMap.get("beam")));
                            }else if(some[i].equals("L")) {
                                buttonHashMap.get(finalRow+Integer.toString(finalCol)).
                                        getButton().setGraphic(new ImageView(imageHashMap.get("L")));
                                setButtonBackground(buttonHashMap.get(finalRow+Integer.toString(finalCol)).
                                        getButton(), "yellow.png");
                            }
                            i++;
                        }
                    }
                    for (String s:some){
                        System.out.print(s);
                    }
                } else {
                    Platform.runLater(() -> this.header.setText(this.model.getSafeFile()+"  has  no  Solution!"));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        });

        Restart.setOnAction(actionEvent -> {
            String file=this.model.getSafeFile();
            try {
                this.model = new LasersModel(file);
                this.model.addObserver(this);
                this.start(this.satge);
                Platform.runLater(() -> this.header.setText(this.model.getSafeFile() + "  has been restarted."));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Load.setOnAction(actionEvent -> {
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(this.satge);
            if (file != null) {
                String filename= String.valueOf(file);
                try {
                    this.model = new LasersModel(filename);
                    this.model.addObserver(this);
                    this.start(this.satge);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }});

        return bottom;
    }


    /**
     * Creates the border pane to contain all the grid panes and buttons.
     * @return The border pane.
     */
    public BorderPane make_boder() {
        BorderPane borderPane = new BorderPane();
        BorderPane top = new BorderPane();
        top.setCenter(this.header);
        borderPane.setTop(top);

        GridPane gridPane = make_centergrid();
        borderPane.setCenter(gridPane);

        BorderPane bottom=make_bottom();
        borderPane.setBottom(bottom);
        return borderPane;
    }

    /**
     * The start method that starts this GUI.
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.satge=stage;
        init(stage);  // do all your UI initialization here
        stage.setTitle("Lasers GUI");
        stage.show();
    }

    /**
     * A helper method for the update method.
     * Updates the images of the buttons according to the data.
     * @param data The data that's being sent from the controller.
     */
    public void refresh(ModelData data) {
        Buttonhelper buttonhelper = buttonHashMap.get(Integer.toString(data.getRow()) + data.getCol());
        Button button = buttonHashMap.get(data.getRow() + Integer.toString(data.getCol())).getButton();
        this.header.setText(this.controller.toString());
        for (int i=0;i<pillarmap.size();i++){
            setButtonBackground(pillarmap.get(i).getButton(),"white.png");
        }
        if (!buttonhelper.isLasers() && data.getVal().equals("*")) {
            // beam
            button.setGraphic(new ImageView(imageHashMap.get("beam")));
            setButtonBackground(button, "yellow.png");
            Buttonaction(button,buttonhelper,data.getRow(),data.getCol());
        } else if (!buttonhelper.isLasers()&&data.getVal().equals(".")){
            //unlighten the bulb/las
            button.setGraphic(new ImageView(imageHashMap.get("white")));
            Buttonaction(button,buttonhelper,data.getRow(),data.getCol());
            buttonhelper.removelaser();
        }else if(!buttonhelper.isLasers()&&data.getVal().equals("L")) {
            //turn on the laser when space is empty
            button.setOnAction(event -> {
                button.setGraphic(new ImageView(imageHashMap.get("L")));
                this.controller.add(data.getRow(),data.getCol());
                buttonhelper.changetolaser();
            });
            Platform.runLater(() -> this.header.setText("add"+data.getRow()+data.getCol()));
       }else if(buttonhelper.isLasers()&&data.getVal().equals(".")){
            buttonhelper.removelaser();
            button.setGraphic(new ImageView(imageHashMap.get("white")));
            this.controller.remove(data.getRow(),data.getCol());
        }else if(buttonhelper.isLasers()&&data.getVal().equals("*")){
            buttonhelper.removelaser();
            button.setGraphic(new ImageView(imageHashMap.get("beam")));
        }
    }

    /**
     * Receives data from the controller and add changes to the GUI according to it.
     * @param model The model
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(LasersModel model, ModelData data) {
        if (model.getStatus() == LasersModel.Status.OK && data != null) {
            Platform.runLater(() -> this.header.setText("MOVES:"));
            Platform.runLater(() -> refresh(data));
        } else if (data == null) {

        }
    }

    /**
     * Terminate the program.
     */
    @Override
    public void stop() {
        if (controller != null) {
            controller.close();
            System.exit(0);
        }
    }
}
