package lasers.ptui;

import java.io.*;

import lasers.model.LasersModel;
import lasers.model.ModelData;
import lasers.model.Observer;

/**
 * This class represents the view portion of the plain text UI.  It
 * is initialized first, followed by the controller (ControllerPTUI).
 * You should create the model here, and then implement the update method.
 *
 * @author Sean Strout @ RIT CS
 * @author YOUR NAME HERE
 */
public class LasersPTUI implements Observer<LasersModel, ModelData>{
    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the lasers.lasers.model and initialize the view.
     * @param filename the safe file name
     * @throws FileNotFoundException if file not found
     */
    public LasersPTUI(String filename) throws FileNotFoundException {
        try {
            this.model = new LasersModel(filename);
            this.model.addObserver(this);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Accessor for the model the PTUI create.
     *
     * @return the model
     */
    public LasersModel getModel() { return this.model; }

    @Override
    public void update(LasersModel model, ModelData data) {
        if(this.model.getStatus() == LasersModel.Status.OK){
            if(data != null){
                System.out.println("Update: "+ data.getRow() + data.getCol() + data.getVal());

            }
            if(data != null){
                System.out.println(data.getVal());
            }
            // If the status is ok, print the board;
            System.out.println(this.model.display());
        }else if(model.getStatus() == LasersModel.Status.GAME_OVER){
            System.out.println("You won!");
            System.exit(0);
        }else{
            System.out.println("An error has occured.");
        }

    }

}

