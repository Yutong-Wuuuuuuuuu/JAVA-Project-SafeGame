package lasers.gui;

import javafx.scene.control.Button;

/**
 * a buttom helper function to keep truck of the lasers placed on the botton.
 */
public class Buttonhelper {
    private boolean Lasers;
    private Button button;

    /**
     * Initialize the buttonhelper.
     */
    public Buttonhelper(){
        this.button = new Button();
        this.Lasers=false;
    }

    /**
     *
     * @return whether the button is a laser.
     */
    public boolean isLasers(){
        return this.Lasers;
    }

    /**
     * @return The button itself.
     */
    public Button getButton(){
        return this.button;
    }

    /**
     * Change the status of the button to lasers.
     */
    public void changetolaser(){
        this.Lasers=true;
    }

    /**
     * Change the status of the button to empty/ without lasers.
     */
    public void removelaser(){
        this.Lasers=false;
    }
}
