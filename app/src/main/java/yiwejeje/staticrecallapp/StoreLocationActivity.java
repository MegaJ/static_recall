package yiwejeje.staticrecallapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class StoreLocationActivity extends AppCompatActivity {

    Button addNewItem;
    EditText itemTitle;
    EditText itemCategory;  //right now only allow for one category for the simplicity
    EditText itemLocation;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changeBG();
        itemTitle=(EditText)findViewById(R.id.ItemText);
        itemCategory=(EditText)findViewById(R.id.CatText);
        itemLocation=(EditText)findViewById(R.id.LocationText);
        addNewItem=(Button)findViewById(R.id.AddButton);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strItemTitle = itemTitle.getText().toString();
                String strCategory = itemCategory.getText().toString();
                String strLocation = itemLocation.getText().toString();
                Item newItem = new Item(strItemTitle, strLocation);
                CategoryManager myCategoryManager = CategoryManager.INSTANCE;
                List<ItemCategory> allCategories = myCategoryManager.getAllCategories();
                ItemCategory existedCategory = myCategoryManager.getCategoryByName(strCategory);
                if (existedCategory == null) {
                    ItemCategory newCategory = new ItemCategory(strCategory);
                    newCategory.addItem(newItem);
                    boolean ifAdded = myCategoryManager.addCategory(newCategory);
                    displayResult(ifAdded);
                } else {
                    boolean ifAdded = existedCategory.addItem(newItem);
                    displayResult(ifAdded);
                }
            }
        });
    }

    public void displayResult(boolean addedResult){
        if (addedResult){
            itemTitle.setText("");
            itemCategory.setText("");
            itemLocation.setText("");
            Toast message=Toast.makeText(getApplicationContext(),"Item Successfully Added",Toast.LENGTH_LONG);
            ViewGroup group = (ViewGroup) message.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(25);
            message.show();
        }

    }

    public void changeBG(){
        View backgroundimage = findViewById(R.id.background);
        Drawable background = backgroundimage.getBackground();
        background.setAlpha(150);

    }



}
