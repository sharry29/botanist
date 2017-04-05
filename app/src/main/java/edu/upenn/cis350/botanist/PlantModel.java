package edu.upenn.cis350.botanist;

/**
 * Created by kathdix on 4/5/17.
 */

public class PlantModel {

    public String name;
    public String url;
    public String light;

    public PlantModel(String name, String url, String light) {
        this.name = name;
        this.url = url;
        this.light = light;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLight() {
        return light;
    }
}
