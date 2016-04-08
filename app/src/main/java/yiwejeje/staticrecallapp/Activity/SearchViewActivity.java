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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.SearchView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

public class SearchViewActivity extends AppCompatActivity {
    CategoryListAdapter listAdapter;
    ExpandableListView expListView;

    CategoryManager categoryManager = CategoryManager.INSTANCE;

    List<Item> itemsResultsList = new ArrayList<Item>();
    ItemCategory resultsCategory = new ItemCategory("Results");

    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureListView();

        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("-----> I have stopped!");
        mp.release();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mp = new MediaPlayer();
        System.out.println("-----> I have restarted!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        // these are declared final because they are used in an inner class
        // Android Studio said so.
        final MenuItem searchMenu = menu.findItem(R.id.search);
        final android.widget.SearchView searchView =
                (android.widget.SearchView) searchMenu.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Getting the little x button in the search widget is hard
        // http://stackoverflow.com/questions/25930380/android-search-widgethow-to-hide-the-close-button-in-search-view-by-default
        // http://stackoverflow.com/questions/24794377/android-capture-searchview-text-clear-by-clicking-x-button
        ImageView searchCloseButton;
        try {
            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            searchCloseButton = (ImageView) searchField.get(searchView);

            searchCloseButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    System.out.println("-----> Search closed");
                    searchView.setQuery("", false);
                    //Collapse the action view
                    searchView.onActionViewCollapsed();
                    //Collapse the search widget
                    searchMenu.collapseActionView();
                    loadCategories();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // autoExpandSearchWidget(menu, searchView);
        // I needed more room to add a button to the Action Bar
        // My preferences for the device I'm working on may not apply to other devices
        disableAppNameOnActionBar();
        return true;
    }

    private void configureListView() {
        setContentView(R.layout.activity_search_view);

        listAdapter = new CategoryListAdapter(
                this, new ArrayList<>(categoryManager.getAllCategories()));

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setAdapter(listAdapter);

        // play a sound when a category is touched
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // we have annoying sounds currently
                // playSound("sounds/onCategoryClick.wav");
                return false;
            }
        });

        // play a sound when an item is touched
        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                // we have annoying sounds currently
                // playSound("sounds/onItemClick.wav");

                System.out.println("-------> group " + groupPosition + " child " + childPosition);
                Intent intent = new Intent(SearchViewActivity.this, SearchLocationScreen.class);

                Item item = (Item) listAdapter.getChild(groupPosition, childPosition);
                intent.putExtra("item", item.getLocationDescription());

                startActivity(intent);

                /*
                final String locationText = item.getLocationDescription();

                TextView locationChild = (TextView) v.findViewById(R.id.textView2);
                System.out.println("---------------> location child" + locationChild);
                System.out.println("---------> location" + v);

                locationChild.setText(locationText);
                */

                return false;
            }
        });
    }

    // This is so the xml's android:onClick can link with loadCategories
    public boolean loadCategories (MenuItem menuItem) {
        return loadCategories();
    }

    public boolean loadCategories () {
        System.out.println("------> Attempt to reload categories!");
        listAdapter.setItemCategories(new ArrayList<>(categoryManager.getAllCategories()));
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
    // http://stackoverflow.com/questions/19464782/android-how-to-make-a-button-click-play-a-sound-file-every-time-it-been-presse
    private void playSound(String soundLocation) {
        if (mp.isPlaying()) {
            mp.stop();
        }

        try {
            mp.reset();
            AssetFileDescriptor afd;
            afd = getAssets().openFd(soundLocation);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
