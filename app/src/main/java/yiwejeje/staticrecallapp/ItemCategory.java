package yiwejeje.staticrecallapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Static Recall Heroes on 3/8/16
 */
public class ItemCategory {
    private String name;
    private List<Item> items;

    public ItemCategory (String name, List<Item> items) {
        if (name == null) {
            throw new IllegalArgumentException("ItemCategory cannot be null");
        }
        this.name = name;

        if (items == null) {
            items = new ArrayList<Item>();
        } else {
            items = this.items;
        }
    }

    public ItemCategory(String name) {
        if (name == null) {
            throw new IllegalArgumentException("ItemCategory's name cannot be null");
        }
        this.name = name;
        items = new ArrayList<Item>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> listOfItems) {
        items = listOfItems;
    }

    public String getName() {
        return name;
    }

    public void setCategoryName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Category name cannot be null");
        }
        this.name = name;
    }

    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    private boolean isItemUniqueByName (Item item) {
        for (Item existingItem : items) {
            boolean notUniqeItemName = existingItem.getName().equals(item.getName());
            if (notUniqeItemName) {
                return false;
            }
        }

        return true;
    }

    // ------ Adding ------

    /**
     * Enforces uniqueness of item names. Not using a list since it's convenient for the
     * adapter to access a list of items.
     *
     */
    public boolean addItem (Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add a null item to a category");
        }

        if (!isItemUniqueByName(item)) {
            return false;
        }

        if (!item.belongsToCategory(this) || !this.hasItem(item)) {
            this.items.add(item);
            item.addCategory(this);
        }

        Collections.sort(items, new ItemComparator());
        return true;
    }

    public void addItem (String itemName, String locationDescription) {
        Item item = new Item(itemName, locationDescription);
        this.addItem(item);
    }

    public void addItem (String itemName) {
        this.addItem(itemName, "");
    }

    // ------ Removing ------

    public boolean removeItem(Item item) {

        if (item.belongsToCategory(this) || this.hasItem(item)) {
            this.items.remove(item);
            item.removeCategory(this);
        } else {
            return false;
        }

        Collections.sort(items, new ItemComparator());
        return true;
    }

    public String toString() {
        return name;
    }

}
