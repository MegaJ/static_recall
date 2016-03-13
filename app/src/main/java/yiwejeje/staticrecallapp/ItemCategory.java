package yiwejeje.staticrecallapp;

import java.util.ArrayList;
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
        System.out.println("------> category has item?: " + item + items.contains(item));
        return items.contains(item);
    }

    // ------ Adding ------

    public boolean addItem (Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add a null item to a category");
        }

        if (!item.belongsToCategory(this)) {
            this.items.add(item);
            item.addCategory(this);
        }

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

    public boolean remove(Item item) {
        item.removeCategory(this);
        return this.items.remove(item);
    }

    public String toString() {
        return name;
    }

}
