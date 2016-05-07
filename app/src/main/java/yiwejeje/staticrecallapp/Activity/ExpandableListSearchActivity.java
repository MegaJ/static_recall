package yiwejeje.staticrecallapp.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SearchView;

import java.io.IOException;
import java.util.ArrayList;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

/**
 * Implements the screen for viewing the categories. Expansion means allowing
 * a user to look at the items in each category. Includes search widget to search items
 * and an overflow menu for navigating to other activities.
 * Saves when {@code onStop()} is called.
 * <p>
 * Leverages a custom adapter. Leverages {@code CategoryManager} for data.
 * @see CategoryListAdapter
 * @see CategoryManager
 */
public class ExpandableListSearchActivity extends AppCompatActivity {
    CategoryListAdapter expListAdapter;
    ExpandableListView expListView;

    CategoryManager categoryManager = CategoryManager.INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureView();
    }

    @Override
    /**
     * Saves when the activity is finished. Necessary for when categories are able to be
     * deleted.
     */
    protected void onStop() {
        super.onStop();
        try {
            categoryManager.save(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    /**
     * Sets up functionality in the right side of the ActionBar.
     * Mostly works to set up the search widget.
     * @param menu
     *      Provided by the framework.
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu_expandable_list, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchMenu = menu.findItem(R.id.search);
        android.widget.SearchView searchView =
                (android.widget.SearchView) searchMenu.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                expListAdapter.filterData(newText);
                expandAll();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                expListAdapter.filterData(query);
                expandAll();
                return true;
            }
        });

        return true;
    }

    /**
     * Sets up both the adapter and the view for the screen.
     * Accesses {@code categoryManager} to load data into adapter.
     */
    private void configureView() {
        setContentView(R.layout.activity_search_view);

        expListAdapter = new CategoryListAdapter(
                this, new ArrayList<>(categoryManager.getAllCategories()));
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setAdapter(expListAdapter);

        disableCategoryCollapse();

        expListView.setOnChildClickListener(new OnChildClickListener() {
            /**
             * Fires an intent to ItemInfoScreen.
             *
             * @param parent
             * @param v
             * @param groupPosition
             * @param childPosition
             * @param id
             * @return false, but doesn't really matter what is returned
             */
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition,
                                        final int childPosition, long id) {
                Intent intent = new Intent(ExpandableListSearchActivity.this, ItemInfoScreen.class);

                Item selectedItem = (Item) expListAdapter.getChild(groupPosition, childPosition);
                intent.putExtra("item title", selectedItem.getName());
                intent.putExtra("item category", selectedItem.getCategories().get(0).toString());
                intent.putExtra("item location", selectedItem.getLocationDescription());
                intent.putExtra("item picture path", selectedItem.getPicturePath());

                startActivity(intent);
                return false;
            }
        });
    }

    /**
     * Sets up the Android overflow menu for when user clicks.
     * Routes user to StoreLocationActivity and classic ListView
     *
     * @param menuItem
     * @return
     *
     * @see StoreLocationActivity
     * @see ItemInfoScreen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.search:
                // ignore, has an onClickListener already
                return true;
            case R.id.store:
                Intent intent = new Intent(this, StoreLocationActivity.class);
                startActivity(intent);
                return true;
            case R.id.view_categories:
                intent = new Intent(this, ListViewSearchActivity.class);
                startActivity(intent);
                ExpandableListSearchActivity.this.finish();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void expandAll() {
        int count = expListAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expListView.expandGroup(i);
        }
    }

    private void disableCategoryCollapse() {
        expandAll();
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    /**
     * Repopulates the {@code expListAdapter} with most updated categories.
     * Calling this is necessary for when ItemInfoScreen adds a new category OR
     * when adding an item from the StoreLocationActivity via the options menu
     * is done.
     *
     * @see ItemInfoScreen
     * @see StoreLocationActivity
     */
    public void refreshList() {
        expListAdapter.updateCategories(
                new ArrayList<ItemCategory>(categoryManager.getAllCategories()));
        expListAdapter.notifyDataSetChanged();
        expandAll();
    }
}
