package yiwejeje.staticrecallapp.Model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Static Recall Heroes
 */
public class Item implements Serializable {
    private String name;
    private String locationDescription;
    private String picturePath;
    private File audioRecording;

    private List<ItemCategory> categories = new ArrayList<ItemCategory>();

    public Item (String name, String locationDescription) {
        if (name == null) {
            throw new IllegalArgumentException("Item name cannot be null");
        }
        this.name = name;

        if (locationDescription == null) {
            this.locationDescription = "";
        } else {
            this.locationDescription = locationDescription;
        }
    }

    public Item (String name) {
        if (name == null) {
            throw new IllegalArgumentException("Item name cannot be null");
        }
        this.name = name;
        this.locationDescription = "";
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

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public File getAudioRecording() {
        return audioRecording;
    }

    public void setAudioRecording(File audioRecording) {
        this.audioRecording = audioRecording;
    }

    public List<ItemCategory> getCategories() {
        return categories;
    }

    public ItemCategory getCategoryByName(String categoryName) {
        for(ItemCategory category : categories) {
            if (categoryName.equals(category.getName())) {
                return category;
            }
        }

        return null;
    }

    public void setCategories(List<ItemCategory> categories) {
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

    public boolean oneSidedRemoveAllCategories() {
        this.categories.clear();
        return true;
    }

    public void deleteImage() {
        if (picturePath != null) {
            File imageFile = new File(picturePath);
            imageFile.delete();
        }

    }

    public String toString() {
        return name;
    }
}
