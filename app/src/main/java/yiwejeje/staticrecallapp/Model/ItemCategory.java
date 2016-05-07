package yiwejeje.staticrecallapp.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds items. A symmetry exists between Item and ItemCategory.
 * Items hold a list of categories. Categories hold a list of items. Bidirectional update methods
 * such as {@code addItem()} and {@code removeItem()} update this class and
 * {@code Item} to preserve the symmetry.
 * <p>
 * Implementation for serializable is present to allow gson to write Item objects
 * to json files in the application's local storage.
 */
public class ItemCategory implements Serializable {
    private String name;
    private List<Item> items;

    public ItemCategory (String name, List<Item> items) {
        if (name == null) {
            throw new IllegalArgumentException("ItemCategory cannot be null");
        }
        this.name = name;

        if (items == null) {
            this.items = new ArrayList<Item>();
        } else {
            this.items = items;
            for (Item item : items) {
                item.addCategory(this);
            }
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

    public Item getItemByName(String itemName) {
        for (Item item : items) {
            if (itemName.equals(item.getName())) {
                return item;
            }
        }
        return null;
    }


    public void setItems(List<Item> listOfItems) {
        // TODO: Somehow set the items in a defensive programming way
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
     * A bidirectional adding that updates both the category and the item.
     * May throw a concurrent modification exception if this function is called
     * in a loop--untested case.
     * @param item
     *      An item that should belong to this category.
     * @return true if adding was successful in updating
     *      both {@code item} and this {@code ItemCategory}. False otherwise.
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

    /**
     * A bidirectional remove that updates both the category and the item.
     * Do not use this function if iterating over a list of categories or items and removing
     * objects or else a concurrent modification exception will be thrown.
     * @param item
     *      An item to remove from this category.
     * @return true if removing was successful in updating
     *      both {@code item} and this {@code ItemCategory}. False otherwise.
     */
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

    /**
     * A unidirectional remove that clears all the items from this category.
     * This function is required for getting around the case when one tries to loop
     * over a list of categories or items to update both the category and the items.
     * The bidirectional remove will cause a concurrent modification exception.
     * @return true
     */
    public boolean oneSidedRemoveAllItems() {
        this.items.clear();
        return true;
    }

    public String toString() {
        return name;
    }

}
