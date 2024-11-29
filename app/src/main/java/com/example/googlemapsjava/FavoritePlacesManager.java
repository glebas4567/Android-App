package com.example.googlemapsjava;

import java.util.ArrayList;

public class FavoritePlacesManager {
    private static FavoritePlacesManager instance;
    private ArrayList<ObjectInfo> favoritePlaces;

    private FavoritePlacesManager() {
        favoritePlaces = new ArrayList<>();
    }

    public static FavoritePlacesManager getInstance() {
        if (instance == null) {
            instance = new FavoritePlacesManager();
        }
        return instance;
    }

    public boolean isPlaceFavorite(ObjectInfo place) {
        for (ObjectInfo favoritePlace : favoritePlaces) {
            if (favoritePlace.getTitle().equals(place.getTitle())) {
                return true; // Место уже в избранных
            }
        }
        return false; // Место не найдено в избранных
    }

    public void addPlace(ObjectInfo place) {
        favoritePlaces.add(place);
    }

    public ArrayList<ObjectInfo> getFavoritePlaces() {
        return favoritePlaces;
    }
}
