package lasers.ptui;

import lasers.Lasers;
import lasers.model.LasersModel;

import java.io.*;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author RIT CS
 * @author Yutong Wu
 */
public class ControllerPTUI  {
    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;
    private PrintWriter userOut;
    private BufferedReader userIn;


    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
        this.model.createSafe();
        /**
        this.userOut = new PrintWriter(System.out,true);
        this.userIn = new BufferedReader(new InputStreamReader(System.in));**/
        startListener();
    }


    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input command file, if specified
     */
    public void run(String inputFile) throws IOException {
        this.userIn = new BufferedReader(new InputStreamReader(System.in));
        if(inputFile != null){
            this.userIn = new BufferedReader(new FileReader(inputFile));
        }
        this.userOut =new PrintWriter(System.out,true);
        while(this.model.getStatus() == LasersModel.Status.OK){
            String input = this.userIn.readLine();
            if(input == null){
                break;
            }else if(input.equals("")){
                continue;
            }
            String[] info;
            info = input.split(" ");
            switch(info[0].charAt(0)){
                case 'a':
                    int row = Integer.parseInt(info[1]);
                    int col = Integer.parseInt(info[2]);
                    if(row >= this.model.getNumOfRow() || col >= this.model.getNumOfCol() || row < 0 || col < 0){
                        this.userOut.println("Coordinate out of bound");
                        break;
                    }
                    boolean statusa = this.model.add(row,col);
                    if(statusa){
                        this.userOut.println("Laser added at (" + row + "," + col + ")");
                    }else{
                        this.userOut.println("Error adding at (" + row + "," + col + ")");
                    }
                    break;
                case 'r':
                    int row1 = Integer.parseInt(info[1]);
                    int col1 = Integer.parseInt(info[2]);
                    if(row1 >= this.model.getNumOfRow() || col1 >= this.model.getNumOfCol() || row1 < 0 || col1 < 0){
                        this.userOut.println("Coordinates out of bound");
                    }
                    boolean statusr = this.model.remove(row1,col1);
                    if(statusr){
                        this.userOut.println("Laser removed at (" + row1 + "," + col1 + ")");
                    }else{
                        this.userOut.println("Error removing at (" + row1 + "," + col1 + ")");
                    }
                    break;
                case 'd':
                    this.userOut.println(this.model.display());
                    break;
                case 'h':
                    this.userOut.println(this.model.help());
                    break;
                case 'v':
                    int[] error;
                    error = this.model.verify();
                    if(error == null){
                        this.userOut.println("The safe is successfully verified!");
                    }else{
                        int row2 = error[0];
                        int col2 = error[1];
                        this.userOut.println("Error verifying at (" + row2 + "," + col2 + ")");
                    }
                    break;
                default:
                    System.out.println("Illegal Command");
                    break;

            }
        }
    }

    public void remove(int row, int col) {
        String msg = "rrr "+row+" "+col;
        Reader inputString = new StringReader(msg);
        this.userIn=new BufferedReader(inputString);
        this.model.remove(row,col);
    }


    public void add(int row, int col){
        String msg = "aaa "+row+" "+col;
        Reader inputString = new StringReader(msg);
        this.userIn=new BufferedReader(inputString);
        this.model.add(row,col);
    }

    public void verify(){
        String msg = "v";
        Reader inputString = new StringReader(msg);
        this.userIn=new BufferedReader(inputString);
        this.model.verify();
    }

    public void startListener() {
        new Thread(() -> {
            try {
                run(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void close() {
        try {
            this.userIn.close();
            this.userOut.close();
        } catch (IOException ioe) {
            // squash
        }
    }
}



