package yiwejeje.staticrecallapp.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

/**
 * This is a custom adapter for a class that inflates an ExpandableListView.
 * The assumption is that only ExpandableListSearchActivity uses this class to
 * interface with the ItemCategories and Items within each category.
 * <p>
 * This class also provides Item search capability to the ExpandableListSearchActivity.
 */
public class CategoryListAdapter extends BaseExpandableListAdapter {

    private Context _context;

    /**
     * Gets mutated by calls from (@code filterQuery()) to show updated search results.
     */
    private List<ItemCategory>  itemCategories;

    /**
     * A copy of all original items. Changed on call to {@code updateCategories()}
     * Used so itemCategories can return to a previous state.
     */
    private ArrayList<ItemCategory> originalList;

    public CategoryListAdapter(Context context, List<ItemCategory> itemCategories) {
        this._context = context;
        if (itemCategories == null) {
            throw new IllegalArgumentException("itemCategories cannot be null");
        }
        this.itemCategories = itemCategories;
        this.originalList = new ArrayList<ItemCategory>();
        this.originalList.addAll(itemCategories);
    }

    /**
     * Replaces {@code this.itemCategories} with a new list of ItemCategory
     * @param itemCategories
     */
    public void updateCategories(List<ItemCategory> itemCategories) {
        this.itemCategories = itemCategories;
        this.originalList.clear();
        this.originalList.addAll(itemCategories);
    }

    @Override
    public Object getChild(int categoryPosition, int itemPosition) {
        ItemCategory itemCategory = itemCategories.get(categoryPosition);
        Item item = itemCategory.getItems().get(itemPosition);
        return item;
    }

    @Override
    public long getChildId(int categoryPosition, int itemPosition) {
        return itemPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Item item = (Item) getChild(groupPosition, childPosition);
        final String childText = item.getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int categoryPosition) {
        ItemCategory itemCategory = itemCategories.get(categoryPosition);
        return itemCategory.getItems().size();
    }

    @Override
    public Object getGroup(int categoryPosition) {
        return this.itemCategories.get(categoryPosition);
    }

    @Override
    public int getGroupCount() {
        return this.itemCategories.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ItemCategory itemCategory = (ItemCategory) this.getGroup(groupPosition);
        String headerTitle = itemCategory.getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // http://www.mysamplecode.com/2012/11/android-expandablelistview-search.html
    /**
     * Mutates {@code itemCategories} but not {@code originalList}
     * Search is done by substring matching on {@code Item}. Calls {@code notifyDataSetChanged()}.
     * If query is empty, the {@code originalList} is the list that itemCategories holds.
     * @param query
     *          This should be an attempt at naming an item.
     */
    public void filterData(String query) {

        query = query.toLowerCase();
        Log.v("MyListAdapter", String.valueOf(itemCategories.size()));
        itemCategories.clear();

        if(query.isEmpty()) {
            itemCategories.addAll(originalList);
        } else {

            for(ItemCategory category: originalList) {

                List<Item> itemList = category.getItems();
                ArrayList<Item> newList = new ArrayList<Item>();
                for(Item item: itemList) {
                    if(item.getName().toLowerCase().contains(query) ||
                            item.getName().toLowerCase().contains(query)) {
                        newList.add(item);
                    }
                }
                if(newList.size() > 0) {
                    ItemCategory nItemCategory = new ItemCategory(category.getName(), newList);
                    itemCategories.add(nItemCategory);
                }
            }
        }
        Log.v("MyListAdapter", String.valueOf(itemCategories.size()));
        notifyDataSetChanged();
    }
}