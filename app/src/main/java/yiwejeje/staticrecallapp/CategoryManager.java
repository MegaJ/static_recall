package yiwejeje.staticrecallapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Static Recall Heroes on 3/8/16.
 */

public enum CategoryManager {
    INSTANCE;
    List<ItemCategory> allCategories;
    public static final String DEFAULT_CATEGORY = "Uncategorized";

    private CategoryManager() {

        allCategories = new ArrayList<ItemCategory>();
        initializePresetData();
    }

    public List<ItemCategory> getAllCategories() {
        return Collections.unmodifiableList(allCategories);
    }

    public void setAllCategories(List<ItemCategory> allCategories) {
        this.allCategories = allCategories;
    }

    public Set<Item> getAllItems() {
        Set<Item> allItems = new HashSet<Item>();
        for (ItemCategory category : allCategories) {
            allItems.addAll(category.getItems());
        }
        return allItems;
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
            throw new IllegalArgumentException("Cannot add null category in CategoryManager");
        }

        // Exception instead of returning false because I want
        // Buttons and widgets to be greyed out if adding can't happen.
        // This function shouldn't be used for doing that, so throw exception instead.
        if (this.hasCategoryWithName(aCategory.getName())) {
            throw new IllegalArgumentException("Cannot add an existing category");
        }

        boolean added = allCategories.add(aCategory);
        Collections.sort(allCategories, new CategoryComparator());
        return added;
    }

    public boolean addCategory(String categoryName) {
        ItemCategory newCategory = new ItemCategory(categoryName);
        return addCategory(newCategory);
    }

    // ------ Removal ------

    public Item removeByGroupAndChildIndex(int groupIndex, int childIndex) {
        return allCategories.get(groupIndex).getItems().remove(childIndex);
    }

    // Items can belong to multiple categories so just remove
    // the copy of the item from that category
    public boolean removeCategory(ItemCategory aCategory) {
        boolean removed = allCategories.remove(aCategory);
        Collections.sort(allCategories, new CategoryComparator());
        return removed;
    }

    public boolean removeCategory(String categoryName) {
        return removeCategory(getCategoryByName(categoryName));
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

        Item momsSpaghettiRecipe = new Item("Mom's Spaghetti Recipe");
        momsSpaghettiRecipe.addCategory(docs);

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

        getCategoryByName("Travel").setCategoryName("Travole");

        for (Item item : this.getAllItems()) {
            System.out.println("----------> TEST: " + item + ": categories: " + item.getCategories());
        }

        for (ItemCategory aCategory : allCategories) {
            System.out.println("----------> TEST: " + aCategory + ": items: " + aCategory.getItems());
        }
    }
}