package com.example.googlemapsjava;

import java.util.ArrayList;

public class VisitedPlacesManager {
    private static VisitedPlacesManager instance;
    private ArrayList<ObjectInfo> visitedPlaces;

    private VisitedPlacesManager() {
        visitedPlaces = new ArrayList<>();
    }

    public static VisitedPlacesManager getInstance() {
        if (instance == null) {
            instance = new VisitedPlacesManager();
        }
        return instance;
    }

    public void addPlace(ObjectInfo place) {
        visitedPlaces.add(place);
    }

    public ArrayList<ObjectInfo> getVisitedPlaces() {
        return visitedPlaces;
    }
}
