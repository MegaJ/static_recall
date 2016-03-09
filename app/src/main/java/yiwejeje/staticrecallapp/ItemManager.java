package yiwejeje.staticrecallapp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Static Recall Heroes on 3/8/16.
 */

public enum ItemManager {
    INSTANCE;
    List<ItemCategory> allCategories;
    Set<Item> allItems;

    private ItemManager() {

        allCategories = new ArrayList<ItemCategory>();
        allItems = new HashSet<Item>();
        initializePresetData();
    }

    public List<ItemCategory> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(List<ItemCategory> allCategories) {
        this.allCategories = allCategories;
    }

    public boolean addCategory(ItemCategory aCategory) {
        if (aCategory == null) {
            throw new IllegalArgumentException("Cannot add null category in ItemManager");
        }
        return allCategories.add(aCategory);
    }

    public boolean removeCategory(ItemCategory aCategory) {
        // TODO: ask the user if they are sure they want to remove all the items
        // TODO: remove from the set of items as well
        return allCategories.remove(aCategory);
    }

    public Set<Item> getAllItems() {
        return allItems;
    }

    public void setAllItems(Set<Item> allItems) {
        this.allItems = allItems;
    }

    public boolean addItem() {
        return true;
    }

    public boolean removeItem() {
        return true;
    }

    private void initializePresetData() {
        ItemCategory uncategorized = new ItemCategory("Uncategorized");
        ItemCategory medical = new ItemCategory("Medical");
        ItemCategory docs = new ItemCategory("Important Documents");
        ItemCategory travel = new ItemCategory("Travel");

        uncategorized.add("Birthday present for mom");

        travel.add("Passport");
        travel.add("Suitcase");
        travel.add("Toothbrush");
        travel.add("Books");
        travel.add("Flight Ticket");
        travel.add("iPod");
        travel.add("Jacket");

        docs.add("Birth Certificate");
        docs.add("Social Security Card");
        docs.add("Academic Transcript");
        docs.add("W2 Forms");
        docs.add("Job Application");
        docs.add("Groupon for Pilates");

        medical.add("Shot Record");
        medical.add("Antibiotics");
        medical.add("Birth Control");
        medical.add("Pamphlet about the Flu Shot");
        medical.add("Doctor's Business Card");

        this.addCategory(uncategorized);
        this.addCategory(docs);
        this.addCategory(medical);
        this.addCategory(travel);
    }
}