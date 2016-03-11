package yiwejeje.staticrecallapp;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by el-swug on 3/10/16.
 */
public class CategoryComparator implements Comparator<ItemCategory> {

    @Override
    public int compare(final ItemCategory category1, final ItemCategory category2) {
        return category1.getName().toLowerCase()
                .compareTo(category2.getName().toLowerCase());
    }
}

