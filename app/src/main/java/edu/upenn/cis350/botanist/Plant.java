package edu.upenn.cis350.botanist;

/**
 * Created by Ben on 2/23/2017.
 */

public class Plant {

    private String name;
    private String type;
    /*
    * Other plant features may be added later. But I feel this class
    * keeps it modular so we can expand on what a Plant is without having
    * to be as conscious of how it's stored in file.
    * */

    public Plant(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Plant(String fileLine) {
        /*Pass in a line from the file so that if changes are made to the save file, this
        is the only place that needs to be changed.*/
        try {
            String[] split = fileLine.split(":");
            this.name = split[0];
            this.type = split[1];
        } catch (Exception e) {
            System.out.println("An error occurred trying to create this plant.");
        }
    }

    //Writing setters so users can edit their plants later
    public void setName(String newname) {
        this.name = newname;
    }
    public void setType(String newtype) {
        this.type = newtype;
    }

    //Getters
    public String getName() {return this.name;}
    public String getType() {return this.type;}
}
