package yiwejeje.staticrecallapp.Model;

import java.util.Comparator;

/**
 * Created by Static Recall Heroes on 3/10/16.
 *
 * Compares categories alphabetically by name. Case insensitive.
 */
public class CategoryComparator implements Comparator<ItemCategory> {

    @Override
    public int compare(final ItemCategory category1, final ItemCategory category2) {
        return category1.getName().toLowerCase()
                .compareTo(category2.getName().toLowerCase());
    }
}

