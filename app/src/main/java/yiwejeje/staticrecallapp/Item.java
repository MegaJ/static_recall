package yiwejeje.staticrecallapp;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Static Recall Heroes
 */
public class Item {
    private String name;
    private String locationDescription;
    private File picture;
    private File audioRecording;

    private Set<ItemCategory> categories = new HashSet<ItemCategory>();

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

    public Item (String name) {
        if (name == null) {
            throw new IllegalArgumentException("Item name cannot be null");
        }
        this.name = name;
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

    public Set<ItemCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<ItemCategory> categories) {
        this.categories = categories;
    }

    public boolean belongsToCategory(ItemCategory aCategory) {
        return categories.contains(aCategory);
    }

    public boolean addCategory(ItemCategory aCategory) {
        if (aCategory == null) {
            throw new IllegalArgumentException("Cannot add an item to a null category");
        }

        // TODO: implement requirement for uniqueness category's name or use a hashmap

        if (!this.belongsToCategory(aCategory) || !aCategory.hasItem(this)) {
            this.categories.add(aCategory);
            aCategory.addItem(this);
        }

        return true;
    }

    public boolean removeCategory(ItemCategory aCategory) {

        if (this.belongsToCategory(aCategory) || aCategory.hasItem(this)) {
            this.categories.remove(aCategory);
            aCategory.removeItem(this);
        } else {
            return false;
        }
        return true;
    }

    public String toString() {
        return name;
    }
}
