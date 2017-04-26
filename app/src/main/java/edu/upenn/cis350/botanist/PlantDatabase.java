package edu.upenn.cis350.botanist;

import com.google.firebase.database.*;
import com.google.gson.Gson;
import java.util.*;

/**
 * Created by kathdix on 4/4/17.
 */

public class PlantDatabase {
    private List<PlantModel> plantList;
    private static PlantDatabase instance;
    private DatabaseReference ref;
    private FirebaseDatabase db;
    private Map<String, Map<String, String>> plants;

    /**
     * Get an instance of the Firebase Database
     */
    private PlantDatabase() {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        ref.addValueEventListener(plantListener);
        plantList = new LinkedList<PlantModel>();
    }

    /**
     * Get an instance of the database. Only one instance allowed for the whole app.
     * @return
     */
    public static PlantDatabase getInstance() {
        if (instance == null) {
            instance = new PlantDatabase();
        }
        return instance;
    }

    ValueEventListener plantListener = new ValueEventListener() {
        /**
         * Get the information about the database. It is stored as a map.
         * @param dataSnapshot
         */
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Map<String, Map<String, String>> plants = (Map<String, Map<String, String>>) dataSnapshot.getValue();
            for (String plant : plants.keySet()) {
                PlantModel p = new PlantModel(plant, plants.get(plant).get("url"), plants.get(plant).get("light"));
                plantList.add(p);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };


    /**
     * Get a model for a plant based on the name of the plant type
     * @param name
     * @return
     */
    public PlantModel getPlantByName(String name) {
        for (PlantModel curr : plantList) {
            if (name.equals(curr.getName())) {
                return curr;
            }
        }
        return null;
    }

    /**
     * Check to see if a specific type of plant exists
     * @param name
     * @return
     */
    public boolean plantExists(String name) {
        for (PlantModel curr : plantList) {
            if (curr.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a new user submitted plant to the database
     * @param name
     * @param website
     * @param light
     */
    public void addPlant(String name, String website, String light) {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, String> info = new HashMap<String, String>();
        m.put("light", light);
        m.put("url", website);
        ref.child(name).setValue(m);
    }

    /**
     * Create an array containing the names of all of the plants
     * @return
     */
    public String[] getPlantNames() {
        String[] plantStrings = new String[plantList.size()];

        int count = 0;
        for (PlantModel curr : plantList) {
            plantStrings[count] = curr.getName();
            count++;
        }
        return plantStrings;
    }
}
