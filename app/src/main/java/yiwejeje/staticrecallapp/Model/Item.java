package yiwejeje.staticrecallapp.Model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the user's information for the item. A symmetry exists between Item and ItemCategory.
 * Items hold a list of categories. Categories hold a list of items. Bidirectional update methods
 * such as {@code addCategory()} and {@code removeCategory()} update this class and
 * {@code ItemCategory} to preserve the symmetry.
 * <p>
 * Implementation for serializable is present to allow gson to write Item objects
 * to json files in the application's local storage.
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

    /**
     * A bidirectional adding that updates both the category and the item.
     * May throw a concurrent modification exception if this function is called
     * in a loop--untested case.
     * @param aCategory
     * @return true if adding was successful in updating
     *      both {@code aCategory} and this {@code Item}. False otherwise.
     */
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

    /**
     * A bidirectional remove that updates both the category and the item.
     * Do not use this function if iterating over a list of categories or items and removing
     * objects or else a concurrent modification exception will be thrown.
     * @param aCategory
     *      A category that this item no longer should belong to.
     * @return true if removing was successful in updating
     *      both {@code aCategory} and this {@code Item}. False otherwise.
     */
    public boolean removeCategory(ItemCategory aCategory) {

        if (this.belongsToCategory(aCategory) || aCategory.hasItem(this)) {
            this.categories.remove(aCategory);
            aCategory.removeItem(this);
        } else {
            return false;
        }
        return true;
    }

    /**
     * A unidirectional remove that clears all the categories from this item.
     * This function is required for getting around the case when one tries to loop
     * over a list of categories or items to update both the category and the items.
     * The bidirectional remove will cause a concurrent modification exception.
     * @return true
     */
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
