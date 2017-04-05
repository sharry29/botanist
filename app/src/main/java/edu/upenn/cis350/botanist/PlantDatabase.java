package edu.upenn.cis350.botanist;

import com.google.firebase.database.*;
import com.google.gson.Gson;
import java.util.*;

/**
 * Created by kathdix on 4/4/17.
 */

public class PlantDatabase {
    private List<Plant> plantList;
    private class Plant {
        public String name;
        public String url;
        public String light;

        public Plant() {

        }

    }

    public PlantDatabase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        ref.addValueEventListener(plantListener);
        plantList = new LinkedList<Plant>();
    }

    ValueEventListener plantListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Map<String, Map<String, String>> plants = (Map<String, Map<String, String>>) dataSnapshot.getValue();
            for (String plant : plants.keySet()) {
                Plant p = new Plant();
                p.name = plant;
                p.url = plants.get(plant).get("url");
                p.light = plants.get(plant).get("light");
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

    public String[] getPlantStrings() {
        String[] plantStrings = new String[plantList.size()];
        int count = 0;
        for (Plant curr : plantList) {
            Gson g = new Gson();
            plantStrings[count] = g.toJson(curr);
            count++;
        }
        return plantStrings;
    }
}
