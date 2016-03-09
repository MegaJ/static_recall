package yiwejeje.staticrecallapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Static Recall Heroes on 3/8/16
 */
public class ItemCategory {
    private String name;
    private ArrayList<Item> items;

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
            throw new IllegalArgumentException("ItemCategory cannot be null");
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

    public void add (Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add a null item to an category");
        }
        items.add(item);
    }

    public void add (String itemName, String locationDescription) {
        Item item = new Item(itemName, locationDescription);
        this.add(item);
    }

    public void add (String itemName) {
        this.add(itemName, "");
    }

    public String toString() {
        return name;
    }

}
