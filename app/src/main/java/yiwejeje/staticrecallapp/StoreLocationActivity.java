package yiwejeje.staticrecallapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class StoreLocationActivity extends AppCompatActivity {

    Button addNewItem;
    EditText itemTitle;
    EditText itemCategory;  //right now only allow for one category for the simplicity
    EditText itemLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        itemTitle=(EditText)findViewById(R.id.ItemText);
        itemCategory=(EditText)findViewById(R.id.CatText);
        itemLocation=(EditText)findViewById(R.id.LocationText);
        addNewItem=(Button)findViewById(R.id.AddButton);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strItemTitle=itemTitle.getText().toString();
                String strCategory=itemCategory.getText().toString();
                String strLocation=itemLocation.getText().toString();
                Item newItem= new Item(strItemTitle,strLocation);
                CategoryManager myCategoryManager=CategoryManager.INSTANCE;
                List<ItemCategory> allCategories=myCategoryManager.getAllCategories();
                ItemCategory existedCategory=myCategoryManager.getCategoryByName(strCategory);
                if (existedCategory==null){
                    ItemCategory newCategory=new ItemCategory(strCategory);
                    newCategory.addItem(newItem);
                    boolean ifadded=myCategoryManager.addCategory(newCategory);
                }
                else{
                    boolean ifadded=existedCategory.addItem(newItem);
                }


            }
        });


    }



}
