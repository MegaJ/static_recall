package yiwejeje.staticrecallapp.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.SearchView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

public class ExpandableListSearchActivity extends AppCompatActivity {
    CategoryListAdapter expListAdapter;
    ExpandableListView expListView;

    CategoryManager categoryManager = CategoryManager.INSTANCE;

    List<Item> itemsResultsList = new ArrayList<Item>();
    ItemCategory resultsCategory = new ItemCategory("Results");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            categoryManager.save(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-----> I have stopped!");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshList();
        //expListAdapter.
        System.out.println("-----> I have restarted!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

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
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                expListAdapter.filterData(query);
                return true;
            }
        });

        return true;
    }

    private void configureView() {
        setContentView(R.layout.activity_search_view);

        expListAdapter = new CategoryListAdapter(
                this, new ArrayList<>(categoryManager.getAllCategories()));
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setAdapter(expListAdapter);

        disableCategoryCollapse();

        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);
                int categoryPosition;

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    // Delete the category
                    categoryPosition = ExpandableListView.getPackedPositionGroup(id);

                    ItemCategory category = (ItemCategory) expListAdapter.getGroup(categoryPosition);
                    ItemCategory uncategorized = categoryManager.getCategoryByName("Uncategorized");

                    if (!category.equals(uncategorized)) {
                        List<Item> items = new ArrayList<Item>(category.getItems());
                        category.oneSidedRemoveAllItems();

                        for (Item item : items) {
                            item.removeCategory(category);
                            uncategorized.addItem(item);
                        }

                        categoryManager.removeCategory(category);
                    }
                    refreshList();
                    return true;

                } else {
                    // null item; we don't consume the click
                    return false;
                }
            }
        });

        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition,
                                        final int childPosition, long id) {
                // we have annoying sounds currently
                // playSound("sounds/onItemClick.wav");

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
                return super.onOptionsItemSelected(item);
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

    public void refreshList() {
        expListAdapter.updateCategories(
                new ArrayList<ItemCategory>(categoryManager.getAllCategories()));
        expListAdapter.notifyDataSetChanged();
        expandAll();
    }
}
