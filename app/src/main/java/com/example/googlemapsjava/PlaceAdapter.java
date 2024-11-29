package com.example.googlemapsjava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PlaceAdapter extends ArrayAdapter<ObjectInfo> {
    private List<ObjectInfo> places;
    private Context context;

    public PlaceAdapter(Context context, List<ObjectInfo> places) {
        super(context, 0, places);
        this.context = context;
        this.places = places;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_catalog, parent, false);
        }

        ObjectInfo place = places.get(position);

        if (place != null) {
            TextView titleTextView = convertView.findViewById(R.id.placeTitle);
            TextView detailsTextView = convertView.findViewById(R.id.placeDetails);
            ImageView imageView = convertView.findViewById(R.id.placeImage);
            TextView hoursTextView = convertView.findViewById(R.id.placeHours);

            titleTextView.setText(place.getTitle());
            detailsTextView.setText(place.getDetails());
            imageView.setImageResource(place.getImageResourceId());
            hoursTextView.setText(place.getWorkingHours());
        }

        return convertView;
    }

    // Метод для обновления данных и уведомления адаптера
    public void updateData(List<ObjectInfo> newPlaces) {
        places.clear();
        places.addAll(newPlaces);
        notifyDataSetChanged();
    }
}
