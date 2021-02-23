package lasers.backtracking;

import lasers.model.LasersModel;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the lasers.model
 * package and/or incorporate it into another class.
 *
 * @author RIT CS
 * @author Yutong Wu
 */
public class SafeConfig implements Configuration {

    private int numOfRow;
    private int numOfCol;
    private String[][] board;
    private LasersModel safe;
    private String filename;
    private int row;
    private int col;
    private HashSet<String> pillars;
    /**
     * Build the safe according to the file.
     */

    public SafeConfig(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.safe = new LasersModel(filename);
        this.safe.createSafe();
        this.numOfCol = this.safe.getNumOfCol();
        this.numOfRow = this.safe.getNumOfRow();
        this.board = this.safe.getBoard();
        this.row = 0;
        this.col = -1;
        Pillars p = new Pillars();
        this.pillars = p.pillars();
    }


    /**
     * Return whether the spot is notEmpty, that is, the spot is not equal to . or *.
     * @param row The row
     * @param col The column
     * @return A boolean statement.
     */
    public boolean notEmpty(int row, int col){
        return (!this.board[row][col].equals(".") && !this.board[row][col].equals("*"));
    }


    /**
     * A copy constructor for the SafeConfig.
     * Also checks whether the spot of the current coordinate is ".", if not, move to the next spot.
     * If the command asks to add laser to the spot, adds a laser to the board.
     * @param other The previous SafeConfig
     * @param laser Whether a laser should be added to the spot.
     */
    private SafeConfig(SafeConfig other, boolean laser) {
        this.numOfCol = other.numOfCol;
        this.numOfRow = other.numOfRow;
        this.safe = other.safe;
        this.filename = other.filename;
        this.row = other.row;
        this.col = other.col;
        this.col += 1;
        this.pillars = other.pillars;
        if(this.col == this.numOfCol){
            this.col =0;
            this.row += 1;
        }

        this.board = new String[other.numOfRow][other.numOfCol];
        for (int row = 0; row < this.numOfRow; row++) {
            System.arraycopy(other.board[row], 0, this.board[row], 0, this.board.length);
        }
        while(true){
            if (this.board[this.row][this.col].equals(".")) {
                break;
            }else {
                this.col++;
                if (this.col == this.numOfCol) {
                    this.col = 0;
                    this.row++;
                    if(this.row == this.numOfRow){
                        this.row = this.numOfRow - 1;
                        this.col = this.numOfCol - 1;
                        break;
                    }
                }
            }
        }
        if (laser) {
            this.board[this.row][this.col] = "L";
            for (int colLeft = this.col - 1; colLeft >= 0; colLeft--) { //Change the tiles on the left to *
                if (notEmpty(this.row,colLeft)) {
                    break;
                } else {
                    this.board[this.row][colLeft] = "*";
                }
            }

            for (int colRight = this.col + 1; colRight < this.numOfCol; colRight++) { //Change the tiles on the right to *
                if (notEmpty(this.row,colRight)) {
                    break;
                } else {
                    this.board[this.row][colRight] = "*";
                }
            }

            for (int rowUp = this.row - 1; rowUp >= 0; rowUp--) { //Change the tiles on top to *
                if (notEmpty(rowUp,this.col)) {
                    break;
                } else {
                    this.board[rowUp][this.col] = "*";
                }
            }

            for (int rowDown = row + 1; rowDown < this.numOfRow; rowDown++) { //Change the tiles at the bottom to *
                if (notEmpty(rowDown,this.col)) {
                    break;
                } else {
                    this.board[rowDown][this.col] = "*";
                }
            }
        }
    }


    /**
     * Gets the successor on the next spot.
     * Generate 2 successors: 1 with a laser in the spot, and the other without a laser in the spot.
     * @return A Linked List with the 2 successors generated.
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        Collection<Configuration> successors = new LinkedList<Configuration>();
        if(this.row == this.numOfRow - 1 && this.col == this.numOfCol - 1){
            return successors;
        }
            SafeConfig successor1 = new SafeConfig(this, false);
            successors.add(successor1);
            SafeConfig successor2 = new SafeConfig(this, true);
            successors.add(successor2);
            return successors;
    }


    /**
     * Checks the number of lasers around a pillar.
     * @param row The row
     * @param col The column
     * @return The number of lasers around this pillar.
     */
    public int checkNumValidity(int row, int col){
        //Checks whether it meets the number tiles' conditions.
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
        return numOfLaserAround;
    }


    /**
     * Checks whether the laser around the pillar breaks the rule(having more laser than the pillar requires)
     * @return A boolean statement.
     */
    public boolean checkNearbyPillars() {
        for (int r = this.row - 1; r < this.row + 2; r++) {
            if (r < 0 || r >= this.numOfRow) {
                continue;
            }
            if (this.pillars.contains(this.board[r][this.col])){
                int condition = Integer.parseInt(this.board[r][this.col]);
                int status = this.checkNumValidity(r, this.col);
                if (status > condition) {
                    return false;
                }

            }
        }
        for (int c = this.col - 1; c < this.col + 2; c++) {
            if (c < 0 || c >= this.numOfCol) {
                continue;
            }
            if (this.pillars.contains(this.board[this.row][c])) {
                int condition = Integer.parseInt(this.board[this.row][c]);
                int status = this.checkNumValidity(this.row, c);
                if (status > condition) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks whether the placement of the laser is valid.
     * @return Boolean statement.
     */
    @Override
    public boolean isValid() {
        return this.checkNearbyPillars();
    }


    /**
     * Check whether it reaches the goal.
     * @return Boolean statement
     */
    @Override
    public boolean isGoal() {
        for(int r = 0; r < this.numOfRow; r++){
            for(int col = 0; col < this.numOfCol; col ++){
                if(this.board[r][col].equals(".")){
                    return false;
                }else if(this.pillars.contains(this.board[r][col])){
                    int current = checkNumValidity(r, col);
                    int required = Integer.parseInt(this.board[r][col]);
                    if(current != required){
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Displays the board neatly.
     * @return The board.
     */
    @Override
    public String toString() {
        if(this.row < 0 || this.col < 0){
            return "null case";
        }
        String str = "";
        for (int r = 0; r < this.numOfRow; r++) {
            for (int c = 0; c < this.numOfCol; c++) {
                str += this.board[r][c];
                str += " ";
            }
            str += "\n";
        }
        return str;
    }

}
