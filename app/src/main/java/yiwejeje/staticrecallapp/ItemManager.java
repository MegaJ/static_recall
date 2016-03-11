package yiwejeje.staticrecallapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        allCategories = new ArrayList<ItemCategory>();;
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
        boolean added = allCategories.add(aCategory);
        Collections.sort(allCategories, new CategoryComparator());
        return added;
    }

    public boolean addCategory(String categoryName) {
        if (categoryName == null) {
            throw new IllegalArgumentException("A category cannot have a null name");
        }

        if (this.hasCategoryWithName(categoryName)) {
            return false;
        } else {
            ItemCategory newCategory = new ItemCategory(categoryName);
            return addCategory(newCategory);
        }
    }

    private boolean hasCategoryWithName(String categoryName) {

        for (ItemCategory itemCategory : allCategories) {
            if (itemCategory.getName().equals(categoryName)) {
                return true;
            }
        }

        return false;
    }

    public boolean removeCategory(ItemCategory aCategory) {
        // TODO: ask the user if they are sure they want to remove all the items
        // TODO: remove from the set of items as well
        boolean removed = allCategories.remove(aCategory);
        Collections.sort(allCategories, new CategoryComparator());
        return removed;
    }

    public Item removeByGroupAndChildIndex(int groupIndex, int childIndex) {
        return allCategories.get(groupIndex).getItems().remove(childIndex);
    }

    public Set<Item> getAllItems() {
        return allItems;
    }

    public void setAllItems(Set<Item> allItems) {
        this.allItems = allItems;
    }

    // add item to a category
    public boolean addItemToCategory(Item item, ItemCategory aCategory) {
        addToSetOfItems(item);
        // if allCategories contains aCategory, add the item in that category
        allCategories.add(aCategory);
        return true;
    }

    public boolean addItem(Item item) {
        //adds item into default category
        addToSetOfItems(item);
        boolean hasUncategorized = false;
        for(int i=0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getName().equals("Uncategorized")) {
                hasUncategorized = true;
                allCategories.get(i).add(item);
            }
        }

        if (!hasUncategorized) {
            ItemCategory uncategorized = new ItemCategory("Uncategorized");
            allCategories.add(uncategorized);
        }

        return true;
    }

    private boolean addToSetOfItems(Item item) {
        return allItems.add(item);
    }

    public boolean removeItem(Item item) {
        boolean removedFromSet = removeItemFromSet(item);
        boolean removedFromAllCategories;

        return true;
    }

    private boolean removeItemFromSet(Item item) {
        return allItems.remove(item);
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