package yiwejeje.staticrecallapp.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.R;

/**
 * Created by Static Recall Heroes on 4/8/16.
 */
public class ListViewSearchActivity extends AppCompatActivity {

    CategoryManager categoryManager = CategoryManager.INSTANCE;

    List<Item> itemResultsList = new ArrayList<Item>();
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

                startActivity(intent);
            }
        });
    }

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
            public boolean onQueryTextChange(String newText)
            {
                listAdapter.getFilter().filter(newText);
                System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                listAdapter.getFilter().filter(query);
                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return true;
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
            case R.id.view_items:
                intent = new Intent(this, ExpandableListSearchActivity.class);
                startActivity(intent);
                ListViewSearchActivity.this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshList() {
        listAdapter.notifyDataSetChanged();
    }
}
