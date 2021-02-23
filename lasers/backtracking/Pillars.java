package lasers.backtracking;

import java.util.HashSet;

public class Pillars {

    private HashSet<String> pillars;

    /**
     * A constructor of the program. Generates the pillars.
     */
    public Pillars(){
        this.pillars = this.pillars();
    }

    /**
     * Returns a hashset with all the pillar possibilities.
     * @return
     */
    public HashSet<String> pillars(){
        HashSet<String> pillars = new HashSet<String>();
        pillars.add("0");
        pillars.add("1");
        pillars.add("2");
        pillars.add("3");
        pillars.add("4");
        return pillars;
    }

}
