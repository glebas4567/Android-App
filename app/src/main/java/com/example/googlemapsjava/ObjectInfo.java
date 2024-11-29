package com.example.googlemapsjava;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

public class ObjectInfo implements Parcelable {
    private String title;
    private String workingHours;
    private String details;
    private int imageResourceId;
    private LatLng position; // Новое поле
    private String type;      // Новое поле

    // Конструктор для объекта с позицией и типом
    public ObjectInfo(String title, String workingHours, String details, int imageResourceId, LatLng position, String type) {
        this.title = title;
        this.workingHours = workingHours;
        this.details = details;
        this.imageResourceId = imageResourceId;
        this.position = position;
        this.type = type;
    }

    // Конструктор для объекта без позиции и типа
    public ObjectInfo(String title, String workingHours, String details, int imageResourceId) {
        this(title, workingHours, details, imageResourceId, null, null);
    }

    // Геттеры
    public String getTitle() {
        return title;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public String getDetails() {
        return details;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    // Реализация Parcelable
    protected ObjectInfo(Parcel in) {
        title = in.readString();
        workingHours = in.readString();
        details = in.readString();
        imageResourceId = in.readInt();
        position = in.readParcelable(LatLng.class.getClassLoader());
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(workingHours);
        dest.writeString(details);
        dest.writeInt(imageResourceId);
        dest.writeParcelable(position, flags);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ObjectInfo> CREATOR = new Creator<ObjectInfo>() {
        @Override
        public ObjectInfo createFromParcel(Parcel in) {
            return new ObjectInfo(in);
        }

        @Override
        public ObjectInfo[] newArray(int size) {
            return new ObjectInfo[size];
        }
    };
}
