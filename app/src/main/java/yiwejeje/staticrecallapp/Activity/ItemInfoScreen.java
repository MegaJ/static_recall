package yiwejeje.staticrecallapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Button;

import java.io.IOException;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

public class ItemInfoScreen extends AppCompatActivity {
    private EditText titleDisplay;
    private EditText catDisplay;
    private EditText locationDisplay;
    private Button saveBtn;

    private String originalItemName;
    private String originalCategoryName;

    CategoryManager categoryManager = CategoryManager.INSTANCE;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideTouchPad();
        return true;
    }

    private void hideTouchPad() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        titleDisplay=(EditText)findViewById(R.id.itemTitle);
        catDisplay=(EditText)findViewById(R.id.ItemCategory);
        locationDisplay=(EditText)findViewById(R.id.ItemLocation);
        saveBtn=(Button)findViewById(R.id.saveBtn1);

        Bundle extras = getIntent().getExtras();
        originalItemName = extras.getString("item title");
        originalCategoryName = extras.getString("item category");

        System.out.println("-----> item's original name: " + originalItemName);
        System.out.println("-----> category's original name: " + originalCategoryName);

        saveBtn.setVisibility(View.INVISIBLE);
        titleDisplay.setText(originalItemName);
        catDisplay.setText(originalCategoryName);
        titleDisplay.setEnabled(false);
        catDisplay.setEnabled(false);
        locationDisplay.setEnabled(false);

        //how to pass an object

        if (extras.getString("item location")!= null){
            String location=extras.getString("item location");
            locationDisplay.setText(location);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strItemTitle = titleDisplay.getText().toString();
                String strCategory = catDisplay.getText().toString();
                String strLocation = locationDisplay.getText().toString();

                ItemCategory originalCategory = categoryManager.
                        getCategoryByName(originalCategoryName);
                ItemCategory category = createNewCategoryIfNotExists(strCategory);
                Item itemToModify = category.getItemByName(originalItemName);

                System.out.println("------> itemToModify nullity: " + itemToModify);

                itemToModify.setName(strItemTitle);
                itemToModify.removeCategory(originalCategory);
                itemToModify.addCategory(category);
                itemToModify.setLocationDescription(strLocation);

                System.out.println("------> Save called from Item Location Activity!");
                try {
                    categoryManager.save(ItemInfoScreen.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        createEditableSlider();
    }

    private ItemCategory createNewCategoryIfNotExists(String categoryName) {
        ItemCategory existingCategory = categoryManager.getCategoryByName(categoryName);
        if (existingCategory == null) {
            existingCategory = new ItemCategory(categoryName);
            categoryManager.addCategory(existingCategory);
        }

        return existingCategory;
    }

    private void createEditableSlider() {
        Switch isEditable;
        isEditable=(Switch)findViewById(R.id.isEditable);

        //attach a listener to check for changes in state
        isEditable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    titleDisplay.setEnabled(true);
                    catDisplay.setEnabled(true);
                    locationDisplay.setEnabled(true);
                    saveBtn.setVisibility(View.VISIBLE);
                    //catch the intent
                    //delete the item first, then save it again
                } else {
                    titleDisplay.setEnabled(false);
                    catDisplay.setEnabled(false);
                    locationDisplay.setEnabled(false);
                }
            }
        });

        //check the current state before we display the screen
        if(isEditable.isChecked()){
            titleDisplay.setEnabled(false);
            catDisplay.setEnabled(false);
            locationDisplay.setEnabled(false);;
        }
        else {
            titleDisplay.setEnabled(false);
            catDisplay.setEnabled(false);
            locationDisplay.setEnabled(false);
        }
    }
}


