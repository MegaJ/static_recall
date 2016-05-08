package yiwejeje.staticrecallapp.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.R;

/**
 * Implements the screen for viewing a list of items. Includes search widget to search items
 * and an overflow menu for navigating to other activities.
 * <p>
 * Leverages {@code CategoryManager} for data.
 * @see CategoryManager
 */
public class ListViewSearchActivity extends AppCompatActivity {

    CategoryManager categoryManager = CategoryManager.INSTANCE;
    ListView listView;
    ArrayAdapter<Item> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshList();
    }

    /**
     * Sets up both the adapter and the view for the screen.
     * Accesses {@code categoryManager} to load data into adapter.
     */
    private void configureView() {
        setContentView(R.layout.activity_search_view_list);

        listAdapter = new ArrayAdapter <Item>(this,
                R.layout.list_item, R.id.lblListItem, new ArrayList<Item>(categoryManager.getAllItems()));
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListViewSearchActivity.this, ItemInfoScreen.class);
                Item selectedItem = listAdapter.getItem(position);
                intent.putExtra("item title", selectedItem.getName());
                intent.putExtra("item category", selectedItem.getCategories().get(0).toString());
                intent.putExtra("item location", selectedItem.getLocationDescription());
                intent.putExtra("item picture path", selectedItem.getPicturePath());

                startActivity(intent);
            }
        });
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
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu_list, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenu = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) searchMenu.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // http://www.androidhub4you.com/2014/04/android-action-bar-search-inside.html
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                listAdapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return true;
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
            case R.id.view_items:
                intent = new Intent(this, ExpandableListSearchActivity.class);
                startActivity(intent);
                ListViewSearchActivity.this.finish();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Repopulates the {@code listAdapter} with most updated categories.
     * Calling this is necessary for when ItemInfoScreen adds a new category OR
     * when adding an item from the StoreLocationActivity via the options menu
     * is done.
     *
     * @see ItemInfoScreen
     * @see StoreLocationActivity
     */
    public void refreshList() {
        listAdapter.clear();
        listAdapter.addAll(categoryManager.getAllItems());
        listAdapter.notifyDataSetChanged();
    }
}
