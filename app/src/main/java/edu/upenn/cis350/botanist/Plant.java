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
    * to be conscious of how it's stored in file.
    * */

    public Plant(String name, String type) {
        this.name = name;
        this.type = type;
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
