package yiwejeje.staticrecallapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SearchView extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> medical = new ArrayList<String>();
    List<String> docs = new ArrayList<String>();
    List<String> travel = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list_view);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

    }

    public void refreshList() {
        // TODO: implement this, after user adds something, they should see it
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Travel");
        listDataHeader.add("Important Documents");
        listDataHeader.add("Medical");

        // Adding child data

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

        listDataChild.put(listDataHeader.get(0), travel); // Header, Child data
        listDataChild.put(listDataHeader.get(1), docs);
        listDataChild.put(listDataHeader.get(2), medical);
    }
}
