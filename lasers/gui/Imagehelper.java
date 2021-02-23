package lasers.gui;
import javafx.scene.image.Image;
import java.util.HashMap;

/**
 * a image helper function to store all the images.
 *
 */
public class Imagehelper {
    /**
     * All the images.
     */
    private Image beam = new Image(getClass().getResourceAsStream(
            "resources/beam.png"));
    private Image L = new Image(getClass().getResourceAsStream(
            "resources/laser.png"));
    private Image pillar0 = new Image(getClass().getResourceAsStream(
            "resources/pillar0.png"));
    private Image pillar1 = new Image(getClass().getResourceAsStream(
            "resources/pillar1.png"));
    private Image pillar2 = new Image(getClass().getResourceAsStream(
            "resources/pillar2.png"));
    private Image pillar3 = new Image(getClass().getResourceAsStream(
            "resources/pillar3.png"));
    private Image pillar4 = new Image(getClass().getResourceAsStream(
            "resources/pillar4.png"));
    private Image pillarX = new Image(getClass().getResourceAsStream(
            "resources/pillarX.png"));
    private Image red = new Image(getClass().getResourceAsStream(
            "resources/red.png"));
    private Image white = new Image(getClass().getResourceAsStream(
            "resources/white.png"));
    private Image yellow = new Image(getClass().getResourceAsStream(
            "resources/yellow.png"));
    private HashMap<String, Image> hashMap;

    /**
     * Put all card value and pokemon images to a hashmap.
     * initializing the class.
     */
    public Imagehelper() {
        hashMap = new HashMap<>();
        this.hashMap.put("beam", beam);
        this.hashMap.put("L", L);
        this.hashMap.put("pillar0", pillar0);
        this.hashMap.put("pillar1", pillar1);
        this.hashMap.put("pillar2", pillar2);
        this.hashMap.put("pillar3", pillar3);
        this.hashMap.put("pillar4", pillar4);
        this.hashMap.put("pillarX", pillarX);
        this.hashMap.put("red", red);
        this.hashMap.put("yellow", yellow);
        this.hashMap.put("white", white);
    }

    /**
     * Get the hashmap of the stored pokemon images.
     *
     * @return
     */
    public HashMap<String, Image> getHashMap() {
        return this.hashMap;
    }


}