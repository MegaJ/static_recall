package yiwejeje.staticrecallapp.Activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.R;

/**
 * Created by Static Recall Heroes on 4/8/16.
 */
public class ListViewSearchActivity extends ListActivity {

    CategoryManager categoryManager = CategoryManager.INSTANCE;

    List<Item> itemResultsList = new ArrayList<Item>();
    ListView listView;
    ArrayAdapter<Item> listAdapter;

    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureView();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void configureView() {
        setContentView(R.layout.activity_search_view_list);

        // initiate the listadapter
        listAdapter = new ArrayAdapter <Item>(this,
                R.layout.list_item, R.id.lblListItem, new ArrayList<Item>(categoryManager.getAllItems()));

        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListViewSearchActivity.this, SearchLocationScreen.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//
//        // these are declared final because they are used in an inner class
//        // Android Studio said so.
//        final MenuItem searchMenu = menu.findItem(R.id.search);
//        final android.widget.SearchView searchView =
//                (android.widget.SearchView) searchMenu.getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
//
//        // Getting the little x button in the search widget is hard
//        // http://stackoverflow.com/questions/25930380/android-search-widgethow-to-hide-the-close-button-in-search-view-by-default
//        // http://stackoverflow.com/questions/24794377/android-capture-searchview-text-clear-by-clicking-x-button
//        ImageView searchCloseButton;
//        try {
//            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
//            searchField.setAccessible(true);
//            searchCloseButton = (ImageView) searchField.get(searchView);
//
//            searchCloseButton.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    System.out.println("-----> Search closed");
//                    searchView.setQuery("", false);
//                    //Collapse the action view
//                    searchView.onActionViewCollapsed();
//                    //Collapse the search widget
//                    searchMenu.collapseActionView();
//                    loadCategories();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // autoExpandSearchWidget(menu, searchView);
        // I needed more room to add a button to the Action Bar
        // My preferences for the device I'm working on may not apply to other devices
        //disableAppNameOnActionBar();

        return true;
    }

    // This is so the xml's android:onClick can link with loadCategories
    public boolean loadCategories (MenuItem menuItem) {
        return loadCategories();
    }

    public boolean loadCategories () {
        // TODO: implement

        System.out.println("------> Attempt to reload categories!");
        //listAdapter.setItemCategories(new ArrayList<>(categoryManager.getAllCategories()));
        System.out.println("------> Item Categories: " + categoryManager.getAllCategories());
        //expListView.collapseGroup(0);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        System.out.println("-------------> NEW INTENT FIRED WITHIN SEARCHVIEW");
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            System.out.println("-------------> query:" + query);
            //use the query to search your data somehow
            searchItems(query);
            showResults();
        }
    }

    private void showResults() {
        // TODO: implement
//        itemResultsList.setItems(itemsResultsList);
//        listAdapter.
//        refreshList();
    }

    public void refreshList() {
        listAdapter.notifyDataSetChanged();
    }

    public void searchItems (String query) {
        String regexQuery = "(.*)" + query.toLowerCase() + "(.*)";

        this.itemResultsList.clear();
        boolean foundMatch = false;
        for (Item item : categoryManager.getAllItems()) {
            foundMatch = Pattern.matches(regexQuery, item.getName().toLowerCase());
            if (foundMatch) {
                this.itemResultsList.add(item);
            }
        }
        System.out.println("------> Results: " + this.itemResultsList);
    }
}
