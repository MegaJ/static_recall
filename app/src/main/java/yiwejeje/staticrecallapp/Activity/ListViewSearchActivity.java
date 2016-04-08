package yiwejeje.staticrecallapp.Activity;

import android.app.ListActivity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.R;

/**
 * Created by Static Recall Heroes on 4/8/16.
 */
public class ListViewSearchActivity extends ListActivity {

    CategoryManager categoryManager = CategoryManager.INSTANCE;

    List<Item> itemsResultsList = new ArrayList<Item>();
    ListView listView;
    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("------> creating listViewSearchActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_view_list);

        // initiate the listadapter
        ArrayAdapter<Item> myAdapter = new ArrayAdapter <Item>(this,
                R.layout.list_item, R.id.lblListItem, new ArrayList<Item>(categoryManager.getAllItems()));

        // assign the list adapter
        // setListAdapter(myAdapter);

        listView = (ListView) findViewById(android.R.id.list);
        System.out.println("-----> Is listview null? " + listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = (Item) getListView().getItemAtPosition(position);
                System.out.println("-----> You clicked " + selectedItem + " at position " + position);
            }
        });

        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
}
