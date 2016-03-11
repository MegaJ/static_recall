package yiwejeje.staticrecallapp;

import java.util.ArrayList;
import java.util.Collections;
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

    public Set<Item> getAllItems() {
        return allItems;
    }

    public void setAllItems(Set<Item> allItems) {
        this.allItems = allItems;
    }

    public ItemCategory getCategoryByName(String categoryName) {
        for (ItemCategory itemCategory : allCategories) {
            if (itemCategory.getName().equals(categoryName)) {
                return itemCategory;
            }
        }
        return null;
    }

    private boolean hasCategoryWithName(String categoryName) {
        ItemCategory itemCategory = getCategoryByName(categoryName);
        if (itemCategory == null) {
            return false;
        }
        return true;
    }

    // ------ Adding ------

    public boolean addCategory(ItemCategory aCategory) {
        if (aCategory == null) {
            throw new IllegalArgumentException("Cannot add null category in ItemManager");
        }

        if (this.hasCategoryWithName(aCategory.getName())) {
            throw new IllegalArgumentException("Cannot add an existing category");
        }

        boolean added = allCategories.add(aCategory);
        Collections.sort(allCategories, new CategoryComparator());
        return added;
    }

    public boolean addCategory(String categoryName) {

        if (this.hasCategoryWithName(categoryName)) {
            return false;
        } else {
            ItemCategory newCategory = new ItemCategory(categoryName);
            return addCategory(newCategory);
        }
    }

    // ItemCategory must already be held inside ItemManager
    public boolean addItemToCategory(Item item, ItemCategory aCategory) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        if (aCategory == null) {
            throw new IllegalArgumentException("Item category cannot be null");
        }

        addToSetOfItems(item);
        aCategory.add(item);
        return true;
    }

    public boolean addItemToCategory(String itemName, ItemCategory aCategory) {
        Item item = new Item(itemName);
        return addItemToCategory(item, aCategory);
    }

    public boolean addItemToCategory(Item item, String categoryName) {
        addToSetOfItems(item);
        ItemCategory candidateCategory = this.getCategoryByName(categoryName);
        return addItemToCategory(item, candidateCategory);
    }

    public boolean addItemToCategory(String itemName, String categoryName) {
        Item item = new Item(itemName);
        return addItemToCategory(item, categoryName);
    }

    public boolean addItem(Item item) {
        addToSetOfItems(item);
        ItemCategory defaultCategory = getCategoryByName("Uncategorized");

        if (defaultCategory != null) {
            defaultCategory.add(item);
        } else {
            ItemCategory uncategorized = new ItemCategory("Uncategorized");
            allCategories.add(uncategorized);
        }

        return true;
    }

    public boolean addItem(String string) {
        Item item = new Item(string);
        return addItem(item);
    }

    private boolean addToSetOfItems(Item item) {
        return allItems.add(item);
    }

    // ------ Removal ------

    public Item removeByGroupAndChildIndex(int groupIndex, int childIndex) {
        return allCategories.get(groupIndex).getItems().remove(childIndex);
    }

    // Items can belong to multiple categories so just remove
    // the copy of the item from that category
    public boolean removeCategory(ItemCategory aCategory) {
        // TODO: finish implementing
        removeItemsInCategory(aCategory);
        boolean removed = allCategories.remove(aCategory);
        Collections.sort(allCategories, new CategoryComparator());
        return removed && removeItemsInCategory(aCategory);
    }

    private boolean removeItemsInCategory(ItemCategory aCategory) {
        //TODO: implement removal in this.allItems
        return true;
    }

    public boolean removeItem(Item item) {
        // TODO: implement
        boolean removedFromSet = removeItemFromSet(item);
        boolean removedFromAllCategories;

        return true;
    }

    private boolean removeItemFromSet(Item item) {
        return allItems.remove(item);
    }

    // ------ Setup ------

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