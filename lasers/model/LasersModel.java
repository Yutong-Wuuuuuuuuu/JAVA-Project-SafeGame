package lasers.model;

import javax.net.ssl.SSLEngineResult;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The model of the lasers safe.  You are free to change this class however
 * you wish, but it should still follow the MVC architecture.
 *
 * @author RIT CS
 * @author Yutong Wu
 */
public class LasersModel {
    /** the observers who are registered with this model */
    private List<Observer<LasersModel, ModelData>> observers;
    private int numOfRow;
    private int numOfCol;
    private String[][] board;
    private Status status;
    private String safeFile;

    public enum Status{
        OK,
        GAME_OVER,
        ERROR
    }

    /**
     * Add a new observer.
     *
     * @param observer the new observer
     */
    public void addObserver(Observer<LasersModel, ModelData > observer) {
        this.observers.add(observer);
    }

    /**
     * Notify observers the model has changed.
     *
     * @param data optional data the model can send to the view
     */
    private void notifyObservers(ModelData data){
        for (Observer<LasersModel, ModelData> observer: observers) {
            observer.update(this, data);
        }
    }


    /**
     * Takes in a file name and initialize all the fields.
     * @throws FileNotFoundException
     */
    public LasersModel(String filename) throws FileNotFoundException {
        this.board = null;
        this.numOfRow = 0;
        this.numOfCol = 0;
        this.status = Status.OK;
        this.observers =new LinkedList<>();
        this.safeFile = filename;
    }

    public void setFilename(String filename){
        this.safeFile = filename;
    }

    public String getSafeFile(){
        return this.safeFile;
    }

    /**
     * Creates the safe when the file is known by the controller.
     */
    public void createSafe(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(this.safeFile));
            String line = reader.readLine();
            String[] info = line.split(" "); //splits the line to an array by white space.
            this.numOfRow = Integer.parseInt(info[0]);
            this.numOfCol = Integer.parseInt(info[1]);
            this.board = new String[this.numOfRow][this.numOfCol]; //Create a matrix with size (numOfRow * numOfCol)

            for (int tracker = 0; tracker < this.numOfRow; tracker ++) {
                line = reader.readLine();
                info = line.split(" "); //splits the line to an array by white space.
                for (int i = 0; i < this.numOfCol; i++) {
                    this.board[tracker][i] = info[i];
                }
            }
            notifyObservers(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the message stored in the coordinate provided
     * @param row the row
     * @param col the col
     * @return A string containing the message in the spot.
     */
    public String getCoordVal(int row, int col){
        return this.board[row][col];
    }


    /**
     * @return The status of the game;
     */
    public Status getStatus(){
        return this.status;
    }

    public void setStatus(Status status){
        this.status = status;
        notifyObservers(null);
    }

    /**
     * @return returns the height of the board.
     */
    public int getNumOfRow() {
        return this.numOfRow;
    }

    /**
     * @return returns the width of the board.
     */
    public int getNumOfCol() {
        return this.numOfCol;
    }

    /**
     * @return reuturns the current board.
     */
    public String[][] getBoard(){
        return this.board;
    }

    /**
     * @param row row of the coordinate
     * @param col col of the coordinate
     * @return Whether a laser can be added to this position.
     */
    public boolean validCoordToAdd(int row, int col){
        return (this.board[row][col].equals(".")||this.board[row][col].equals("*"));
    }

    /**
     * @param row the row
     * @param col the column
     * @return Whether the spot is empty(contains a . or a *)
     */
    public boolean notEmpty(int row, int col){
        return (!this.board[row][col].equals(".") && !this.board[row][col].equals("*"));
    }

    /**
     * Checks whether this is a proper position to add a laser.
     * Adds a laser at the coordinate.
     * Changes the "." in the same row & col to "*", meaning that there is a light beam
     * going through the coordinate.
     * Returns true if the laser is successfully added, otherwise return false.
     *
     * @param row The x coordinate.
     * @param col The y coordinate.
     */
    public boolean add(int row, int col){
        if (validCoordToAdd(row, col)) {
            //Does not handle the situation where two lasers are shooting at each other here.
            this.board[row][col] = "L";
            notifyObservers(new ModelData(row,col,"L"));
            for (int colLeft = col - 1; colLeft >= 0; colLeft--) { //Change the tiles on the left to *
                if (notEmpty(row,colLeft)) {
                    break;
                } else {
                    this.board[row][colLeft] = "*";
                    notifyObservers(new ModelData(row,colLeft,"*"));
                }
            }

            for (int colRight = col + 1; colRight < this.numOfCol; colRight++) { //Change the tiles on the right to *
                if (notEmpty(row,colRight)) {
                    break;
                } else {
                    this.board[row][colRight] = "*";
                    notifyObservers(new ModelData(row,colRight,"*"));
                }
            }

            for (int rowUp = row - 1; rowUp >= 0; rowUp--) { //Change the tiles on top to *
                if (notEmpty(rowUp,col)) {
                    break;
                } else {
                    this.board[rowUp][col] = "*";
                    notifyObservers(new ModelData(rowUp,col,"*"));
                }
            }

            for (int rowDown = row + 1; rowDown < this.numOfRow; rowDown++) { //Change the tiles at the bottom to *
                if (notEmpty(rowDown,col)) {
                    break;
                } else {
                    this.board[rowDown][col] = "*";
                    notifyObservers(new ModelData(rowDown,col,"*"));
                }
            }
            return true;
        } else {
            return false;
        }

    }



    /**
     * Checks whether there is a laser to be removed.
     * Removes a laser at the coordinate.
     * Change all the "*" that are in the same row & column of the laser to "."
     * Returns true if the laser is successfully removed, false otherwise.
     * @param row the x coordinate.
     * @param col the y coordinate
     */
    public boolean remove(int row, int col) {
        if (this.board[row][col].equals("L")) {
            this.board[row][col] = "*";
            notifyObservers(new ModelData(row,col,"*"));
            for (int colLeft = col; colLeft >= 0; colLeft--) { //Check coordinates on the left.
                if (!this.board[row][colLeft].equals("*")) {
                    break;
                } else {

                    Boolean needToBeChanged = true;
                    //Check also whether this spot is lighted by another laser by
                    //looping through the four cardinal directions of this spot. If a laser is
                    //found, this spot need not be changed to a "."
                    for (int adjRow = 0; adjRow < this.numOfRow; adjRow++) {
                        System.out.println("checking " + adjRow + " " + colLeft);
                        if (this.board[adjRow][colLeft].equals("L")) {
                            needToBeChanged = false;
                            break;
                        }
                    }
                    for (int adjCol = 0; adjCol < this.numOfCol; adjCol++) {
                        if (!needToBeChanged) {
                            break;
                        } else if (this.board[row][adjCol].equals("L")) {
                            needToBeChanged = false;
                        }
                    }
                    if (needToBeChanged) {
                        this.board[row][colLeft] = ".";
                        notifyObservers(new ModelData(row,colLeft,"."));
                    }
                }
            }

            for (int colRight = col + 1; colRight < this.numOfCol; colRight++) { //Check coordinates on the right of the laser
                if (!this.board[row][colRight].equals("*")) {
                    break;
                } else {
                    Boolean needToBeChanged = true;
                    //Check also whether this spot is lighted by another laser by
                    //looping through the four cardinal directions of this spot. If a laser is
                    //found, this spot need not be changed to a "."
                    for (int adjRow = 0; adjRow < this.numOfRow; adjRow++) {
                        if (this.board[adjRow][colRight].equals("L")) {
                            needToBeChanged = false;
                            break;
                        }
                    }
                    for (int adjCol = 0; adjCol < this.numOfCol; adjCol++) {
                        if (!needToBeChanged) {
                            break;
                        } else if (this.board[row][adjCol].equals("L")) {
                            needToBeChanged = false;
                        }
                    }
                    if (needToBeChanged) {
                        this.board[row][colRight] = ".";
                        notifyObservers(new ModelData(row,colRight,"."));
                    }
                }
            }

            for (int rowUp = row - 1; rowUp >= 0; rowUp--) { //Check coordinates on top of the laser
                if (!this.board[rowUp][col].equals("*")) {
                    break;
                } else {
                    Boolean needToBeChanged = true;
                    //Check also whether this spot is lighted by another laser by
                    //looping through the four cardinal directions of this spot. If a laser is
                    //found, this spot need not be changed to a "."
                    for (int adjRow = 0; adjRow < this.numOfRow; adjRow++) {
                        if (this.board[adjRow][col].equals("L")) {
                            needToBeChanged = false;
                            break;
                        }
                    }
                    for (int adjCol = 0; adjCol < this.numOfCol; adjCol++) {
                        if (!needToBeChanged) {
                            break;
                        } else if (this.board[rowUp][adjCol].equals("L")) {
                            needToBeChanged = false;
                        }
                    }
                    if (needToBeChanged) {
                        this.board[rowUp][col] = ".";
                        notifyObservers(new ModelData(rowUp,col,"."));
                    }
                }
            }

            for (int rowDown = row + 1; rowDown < this.numOfRow; rowDown++) { //Check the coordinates on the bottom of the laser
                if (!this.board[rowDown][col].equals("*")) {
                    break;
                } else {
                    Boolean needToBeChanged = true;
                    //Check also whether this spot is lighted by another laser by
                    //looping through the four cardinal directions of this spot. If a laser is
                    //found, this spot need not be changed to a "."
                    for (int adjRow = 0; adjRow < this.numOfRow; adjRow++) {
                        if (this.board[adjRow][col].equals("L")) {
                            needToBeChanged = false;
                            break;
                        }
                    }
                    for (int adjCol = 0; adjCol < this.numOfCol; adjCol++) {
                        if (!needToBeChanged) {
                            break;
                        } else if (this.board[rowDown][adjCol].equals("L")) {
                            needToBeChanged = false;
                        }
                    }
                    if (needToBeChanged) {
                        this.board[rowDown][col] = ".";
                        notifyObservers(new ModelData(rowDown,col,"."));
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Verifies the safe.
     * Returns an array with length 2 (row, column) if there is an error in the safe.
     * If the safe is successfully verified, return null.
     */
    public int[] verify() {
        for (int row = 0; row < this.numOfRow; row++) { //Checks from row to row
            boolean consecutiveLaser = false;
            //This variable is to check whether 2 lasers are shooting at each other without an obstacle in between.
            for (int col = 0; col < this.numOfCol; col++) {
                if (this.board[row][col].equals(".")) {
                    int[] error = {row, col};
                    return error; //Checks whether all spots are lighted with a laser beam
                } else if (this.board[row][col].equals("L")) {
                    if(consecutiveLaser){
                        int[] error = {row, col};
                        return error;
                    }else{
                        consecutiveLaser = true;
                    }
                } else if(this.board[row][col].equals("X")){
                    if(consecutiveLaser){
                        consecutiveLaser = false;
                    }
                }else if(this.board[row][col].equals("*")){
                    continue;
                }else{
                    //Checks whether it meets the number tiles' conditions.
                    int condition = Integer.parseInt(this.board[row][col]);
                    int numOfLaserAround = 0;
                    for (int hor = col - 1; (hor < col + 2 && hor < this.numOfCol); hor++) {
                        if (hor < 0) {
                            continue;
                        }
                        if (this.board[row][hor].equals("L")) {
                            numOfLaserAround++;
                        }
                    }
                    for (int ver = row - 1; (ver < row + 2 && ver < this.numOfRow); ver++) {
                        if (ver < 0) {
                            continue;
                        }
                        if (this.board[ver][col].equals("L")) {
                            numOfLaserAround++;
                        }
                    }
                    if (!(numOfLaserAround == condition)) {
                        int[] error = {row, col};
                        return error;
                    }
                }
            }
        }
        for(int col = 0; col<this.numOfCol; col++){ //checks column to column for consecutive lasers.
            boolean consecutiveLaser = false;
            for(int row = 0; row < this.numOfRow; row++){
                if(this.board[row][col].equals("L")){
                    if(consecutiveLaser){
                        int[] error = {row,col};
                        return error;
                    }else{
                        consecutiveLaser = true;
                    }
                }else if(this.board[row][col].equals("X")){
                    consecutiveLaser = false;
                }
            }
        }
        this.setStatus(Status.GAME_OVER);
        notifyObservers(null);
        return null; //return null if the safe is successfully verified.
    }

    /**
     * Displays the board.
     * @return A string with the display message of the board, to be printed out in the view.
     */
    public String display() {
        String displayMsg = "  ";
        for (int i = 0; i < this.getNumOfCol(); i++) {
            displayMsg += i + " "; //displays column number.
        }
        displayMsg += "\n";
        for (int j = 0; j < (this.getNumOfCol() * 5); j++) {
            displayMsg += "-"; //displays the dashes(-) under the row number
        }
        displayMsg += "\n";
        for (int row = 0; row < this.getNumOfRow(); row++) {
            displayMsg += row + "|"; //displays column number.
            for (int col = 0; col < this.getNumOfCol(); col++) {
                displayMsg += this.getBoard()[row][col] + " "; //displays board
            }
            displayMsg += "\n";
        }
        return displayMsg;
    }

    /**
     * Generates the help messgae of the program.
     * @return A string containing the help message, to be displayed in view.
     */
    public String help(){
        String helpMsg = "a|add r c: Add laser to (r,c)\n" +
                "d|display: Display safe\n" +
                "h|help: Print this help message\n" +
                "q|quit: Exit program\n" +
                "r|remove r c: Remove laser from (r,c)\n" +
                "v|verify:Verify safe correctness";
        return helpMsg;
    }

}
