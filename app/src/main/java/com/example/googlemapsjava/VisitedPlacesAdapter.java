package com.example.googlemapsjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VisitedPlacesAdapter extends RecyclerView.Adapter<VisitedPlacesAdapter.ViewHolder> {
    private List<ObjectInfo> visitedPlaces;
    private OnPlaceClickListener onPlaceClickListener;

    public interface OnPlaceClickListener {
        void onPlaceClick(ObjectInfo place);
    }

    public VisitedPlacesAdapter(List<ObjectInfo> visitedPlaces, OnPlaceClickListener listener) {
        this.visitedPlaces = visitedPlaces;
        this.onPlaceClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visited_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ObjectInfo place = visitedPlaces.get(position);
        holder.titleTextView.setText(place.getTitle());
        holder.detailsTextView.setText(place.getDetails());
        holder.workingHoursTextView.setText(place.getWorkingHours());
        holder.imageView.setImageResource(place.getImageResourceId());

        // Устанавливаем обработчик клика
        holder.itemView.setOnClickListener(v -> onPlaceClickListener.onPlaceClick(place));
    }

    @Override
    public int getItemCount() {
        return visitedPlaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, detailsTextView, workingHoursTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.placeTitle);
            detailsTextView = itemView.findViewById(R.id.placeDetails);
            workingHoursTextView = itemView.findViewById(R.id.placeHours);
            imageView = itemView.findViewById(R.id.placeImage);
        }
    }
}
