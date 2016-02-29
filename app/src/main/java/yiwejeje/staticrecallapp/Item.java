package yiwejeje.staticrecallapp;

import java.io.File;

/**
 * Created by Static Recall Heroes
 */
public class Item {
    private String name;
    private String description;
    private File picture;
    private File audioRecording;

    public Item (String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
