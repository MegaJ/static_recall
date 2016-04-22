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
import android.widget.ArrayAdapter;
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

public class ExpandableListSearchActivity extends AppCompatActivity {
    CategoryListAdapter expListAdapter;
    ExpandableListView expListView;
    ArrayAdapter<Item> listAdapter;

    CategoryManager categoryManager = CategoryManager.INSTANCE;

    List<Item> itemsResultsList = new ArrayList<Item>();
    ItemCategory resultsCategory = new ItemCategory("Results");

    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureView();
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

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                collapseAll();
                return true;
            }
        });

        return true;
    }

    private void configureView() {
        setContentView(R.layout.activity_search_view);

        expListAdapter = new CategoryListAdapter(
                this, new ArrayList<>(categoryManager.getAllCategories()));

        listAdapter = new ArrayAdapter<Item>(this,
                R.layout.list_item, R.id.lblListItem, new ArrayList<Item>(categoryManager.getAllItems()));

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setAdapter(expListAdapter);

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
                Intent intent = new Intent(ExpandableListSearchActivity.this, ItemInfoScreen.class);

                Item item = (Item) expListAdapter.getChild(groupPosition, childPosition);
                intent.putExtra("item", item.getLocationDescription());

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

    private void collapseAll() {
        int count = expListAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expListView.collapseGroup(i);
        }
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
