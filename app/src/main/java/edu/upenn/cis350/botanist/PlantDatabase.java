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

    private PlantDatabase() {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        ref.addValueEventListener(plantListener);
        plantList = new LinkedList<PlantModel>();
    }

    public static PlantDatabase getInstance() {
        if (instance == null) {
            instance = new PlantDatabase();
        }
        return instance;
    }

    ValueEventListener plantListener = new ValueEventListener() {

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
            // Getting Post failed, log a message
            System.out.println(databaseError);
            // ...
        }
    };

    public String[] getPlantNames() {
        String[] plantStrings = new String[plantList.size()];

        int count = 0;
        for (PlantModel curr : plantList) {
            plantStrings[count] = curr.getName();
            count++;
        }
        return plantStrings;
    }

    public PlantModel getPlantByName(String name) {
        for (PlantModel curr : plantList) {
            if (name.equals(curr.getName())) {
                return curr;
            }
        }
        return null;
    }

    public boolean plantExists(String name) {
        return plantList.contains(name);
    }

    public void addPlant(String name, String website, String light) {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, String> info = new HashMap<String, String>();
        m.put("light", light);
        m.put("url", website);
        //m.put(name, info);
        //ref.push().setValue(m);
        ref.child(name).setValue(m);
    }
}
