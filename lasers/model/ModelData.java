package lasers.model;

import java.util.HashMap;

/**
 * Use this class to customize the data you wish to send from the model
 * to the view when the model changes state.
 *
 * @author RIT CS
 * @author YOUR NAME HERE
 */
public class ModelData {
    private final int row;
    private final int col;
    private final String val;

    public ModelData(int row, int col, String val) {
        this.row = row;
        this.col = col;
        this.val = val;
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return this.col;
    }

    public String getVal(){
        return this.val;
    }


}
