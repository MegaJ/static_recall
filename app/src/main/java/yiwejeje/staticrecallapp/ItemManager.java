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
    public static final String DEFAULT_CATEGORY = "Uncategorized";

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
        addItemsFromCategoryToSetOfItems(aCategory);
        return added;
    }

    public boolean addCategory(String categoryName) {
        ItemCategory newCategory = new ItemCategory(categoryName);
        return addCategory(newCategory);
    }

    private void addItemsFromCategoryToSetOfItems (ItemCategory aCategory) {
        for (Item item : aCategory.getItems()) {
            allItems.add(item);
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
        aCategory.addItem(item);
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
            defaultCategory.addItem(item);
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

    // Remove an item from allItems if it no longer belongs to any categories
    // TODO: must test
    public void removeItemsInCategoryFromSetOfItems (ItemCategory aCategory) {
        // Items may belong to multiple categories
        for (Item item : aCategory.getItems()) {
            Set<ItemCategory> belongsToCategories = item.getCategories();
            belongsToCategories.remove(aCategory);
            if (belongsToCategories.isEmpty()) {
                removeItemFromSet(item);
            }
        }
    }

    // ------ Setup ------

    private void initializePresetData() {
        ItemCategory uncategorized = new ItemCategory("Uncategorized");
        ItemCategory medical = new ItemCategory("Medical");
        ItemCategory docs = new ItemCategory("Important Documents");
        ItemCategory travel = new ItemCategory("Travel");

        uncategorized.addItem("Birthday present for mom");
        uncategorized.addItem("Birthday present for mom");

        travel.addItem("Passport");
        travel.addItem("Suitcase");
        travel.addItem("Toothbrush");
        travel.addItem("Books");
        travel.addItem("Flight Ticket");
        travel.addItem("iPod");
        travel.addItem("Jacket");
        travel.addItem("Mom's goodluck charm");

        docs.addItem("Birth Certificate");
        docs.addItem("Social Security Card");
        docs.addItem("Academic Transcript");
        docs.addItem("W2 Forms");
        docs.addItem("Job Application");
        docs.addItem("Groupon for Pilates");

        medical.addItem("Shot Record");
        medical.addItem("Antibiotics");
        medical.addItem("Birth Control");
        medical.addItem("Pamphlet about the Flu Shot");
        medical.addItem("Doctor's Business Card");

        Item raisinBread = new Item("Raisin Bread");
        travel.addItem(raisinBread);
        medical.addItem(raisinBread);
        medical.removeItem(raisinBread);
        raisinBread.removeCategory(travel);

        raisinBread.addCategory(medical);
        raisinBread.addCategory(medical);
        raisinBread.addCategory(medical);

        this.addCategory(uncategorized);
        this.addCategory(docs);
        this.addCategory(medical);
        this.addCategory(travel);



        for (Item item : allItems) {
            System.out.println("----------> TEST: " + item + ": categories: " + item.getCategories());
        }

        for (ItemCategory aCategory : allCategories) {
            System.out.println("----------> TEST: " + aCategory + ": items: " + aCategory.getItems());
        }
    }
}