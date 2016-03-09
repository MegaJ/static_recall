package yiwejeje.staticrecallapp;

import java.io.File;

/**
 * Created by Static Recall Heroes
 */
public class Item {
    private String name;
    private String locationDescription;
    private File picture;
    private File audioRecording;

    public Item (String name, String locationDescription) {
        if (name == null) {
            throw new IllegalArgumentException("Item name cannot be null");
        }
        this.name = name;

        if (locationDescription == null) {
            locationDescription = "";
        }
        this.locationDescription = locationDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public File getPicture() {
        return picture;
    }

    public void setPicture(File picture) {
        this.picture = picture;
    }

    public File getAudioRecording() {
        return audioRecording;
    }

    public void setAudioRecording(File audioRecording) {
        this.audioRecording = audioRecording;
    }

    public String toString() {
        return name;
    }
}
