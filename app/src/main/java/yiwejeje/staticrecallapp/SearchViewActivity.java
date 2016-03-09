package yiwejeje.staticrecallapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchViewActivity extends AppCompatActivity {
    CategoryListAdapter listAdapter;
    ExpandableListView expListView;
    List<ItemCategory> itemCategories = new ArrayList<ItemCategory>();

    ItemCategory medical = new ItemCategory("Medical");
    ItemCategory docs = new ItemCategory("Important Documents");
    ItemCategory travel = new ItemCategory("Travel");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        listAdapter = new CategoryListAdapter(this, itemCategories);
        prepareListData();
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        itemCategories.get(groupPosition).getName()
                                + " : "
                                + itemCategories.get(groupPosition)
                                .getItems().get(childPosition).getName(), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
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
        System.out.println("search view ------------------------> " + searchView);
        System.out.println("------------------------> " + getComponentName());
        System.out.println("------------------------>" + searchManager.getSearchableInfo(getComponentName()));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(false);
        return true;

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
        System.out.println("-------------> NEW INTENT FIRED WITHIN SEARCHVIEW");
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            System.out.println("-------------> query:" + query);
            //use the query to search your data somehow
            searchItems(query);
        }
    }

    public void refreshList() {
        // TODO: implement this, after user adds something, they should see it
    }

    private void prepareListData() {
        // TODO: Cannot leave this hardcoded. Must load the categories dynamically.

        travel.add("Passport");
        travel.add("Suitcase");
        travel.add("Toothbrush");
        travel.add("Books");
        travel.add("Flight Ticket");
        travel.add("iPod");
        travel.add("Jacket");

        docs.add("Birth Certificate");
        docs.add("Social Security Card");
        docs.add("Academic Transcript");
        docs.add("W2 Forms");
        docs.add("Job Application");
        docs.add("Groupon for Pilates");

        medical.add("Shot Record");
        medical.add("Antibiotics");
        medical.add("Birth Control");
        medical.add("Pamphlet about the Flu Shot");
        medical.add("Doctor's Business Card");

        listAdapter.addItemCategoriesWithACategory(travel);
        listAdapter.addItemCategoriesWithACategory(travel);
        listAdapter.addItemCategoriesWithACategory(travel);

        // or should I use fillItemCategoriesWithACategory instead()?
    }

    private void fillItemCategoriesWithACategory(ItemCategory aCategory) {
        if(aCategory == null) {
            throw new IllegalArgumentException("aCategory is null");
        }
        itemCategories.add(aCategory);
    }

    public void searchItems (String query) {
        System.out.println("------------> itemManager stuff!: " + ItemManager.INSTANCE.getitemInt());
    }
}
