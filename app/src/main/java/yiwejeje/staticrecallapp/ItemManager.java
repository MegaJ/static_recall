package yiwejeje.staticrecallapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Static Recall Heroes on 3/8/16.
 */

public enum ItemManager {
    INSTANCE;
    int itemInt;
    List<ItemCategory> allCategories;
    Set<Item> allItems;

    private ItemManager() {
        itemInt = 5;
    }

    public int getitemInt () {
        return itemInt;
    }
}