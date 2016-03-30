package yiwejeje.staticrecallapp;

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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchViewActivity extends AppCompatActivity {
    CategoryListAdapter listAdapter;
    ExpandableListView expListView;

    List<ItemCategory> itemCategories;
    CategoryManager categoryManager = CategoryManager.INSTANCE;

    List<Item> itemsResultsList = new ArrayList<Item>();
    ItemCategory resultsCategory = new ItemCategory("Results");

    final MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        itemCategories = categoryManager.getAllCategories();
        listAdapter = new CategoryListAdapter(this, itemCategories);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setAdapter(listAdapter);

        // play a sound when a category is touched
        // http://stackoverflow.com/questions/19464782/android-how-to-make-a-button-click-play-a-sound-file-every-time-it-been-presse
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (mp.isPlaying()) {
                    mp.stop();
                }

                try {
                    mp.reset();
                    AssetFileDescriptor afd;
                    afd = getAssets().openFd("sounds/onCategoryClick.wav");
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mp.prepare();
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        // play a sound when an item is touched
        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                // TODO: Do UI things to show information on the clicked item.

                if (mp.isPlaying()) {
                    mp.stop();
                }

                try {
                    mp.reset();
                    AssetFileDescriptor afd;
                    afd = getAssets().openFd("sounds/onItemClick.wav");
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mp.prepare();
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.widget.SearchView searchView =
                (android.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        autoExpandSearchWidget(menu, searchView);
        // I needed more room to add a button to the Action Bar
        // My preferences for the device I'm working on may not apply to other devices
        disableAppNameOnActionBar();
        return true;
    }

    public boolean loadCategories (MenuItem menuItem) {
        System.out.println("------> Attempt to reload categories!");
        listAdapter.setItemCategories(categoryManager.getAllCategories());
        System.out.println("------> Item Categories: " + categoryManager.getAllCategories());
        expListView.collapseGroup(0);
        return true;
    }

    private void autoExpandSearchWidget(Menu menu, SearchView searchView) {
        MenuItem searchMenuItem = menu.findItem( R.id.search ); // get my MenuItem with placeholder submenu
        searchMenuItem.expandActionView(); // Expand the search menu item in order to show by default the query
        searchView.setIconifiedByDefault(false);
    }

    private void disableAppNameOnActionBar () {
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    public void refreshList() {
        listAdapter.notifyDataSetChanged();
    }

    public void searchItems (String query) {
        String regexQuery = "(.*)" + query.toLowerCase() + "(.*)";

        this.itemsResultsList.clear();
        boolean foundMatch = false;
        for (Item item : categoryManager.getAllItems()) {
            foundMatch = Pattern.matches(regexQuery, item.getName().toLowerCase());
            if (foundMatch) {
                this.itemsResultsList.add(item);
            }
        }
        System.out.println("------> Results: " + this.itemsResultsList);
    }

    private void showResults() {
        this.resultsCategory.setItems(itemsResultsList);
        listAdapter.setSingleCategory(resultsCategory);

        // We want the list of found items expanded by default
        expListView.expandGroup(0);
    }
}
