package yiwejeje.staticrecallapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button deleteBtn;
    private Switch isEditable;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        titleDisplay=(EditText)findViewById(R.id.itemTitle);
        catDisplay=(EditText)findViewById(R.id.ItemCategory);
        locationDisplay=(EditText)findViewById(R.id.ItemLocation);
        saveBtn=(Button)findViewById(R.id.saveBtn1);
        deleteBtn=(Button)findViewById(R.id.deleteBtn1);

        Bundle extras = getIntent().getExtras();
        originalItemName = extras.getString("item title");
        originalCategoryName = extras.getString("item category");

        saveBtn.setVisibility(View.INVISIBLE);
        titleDisplay.setText(originalItemName);
        catDisplay.setText(originalCategoryName);
        titleDisplay.setEnabled(false);
        catDisplay.setEnabled(false);
        locationDisplay.setEnabled(false);
        isEditable=(Switch)findViewById(R.id.isEditable);



        if (extras.getString("item location")!= null){
            String location=extras.getString("item location");
            locationDisplay.setText(location);
        }

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ItemInfoScreen.this, "Item successfully deleted!",
                        Toast.LENGTH_LONG).show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strItemTitle = titleDisplay.getText().toString();
                String strCategory = catDisplay.getText().toString();
                String strLocation = locationDisplay.getText().toString();

                if (strItemTitle.equals("")) {
                    // TODO: put toast here because blank items aren't allowed
                    return;
                }

                // if the item is named something ELSE that exists, can't do it.
                if (!strItemTitle.equals(originalItemName)) {
                    if (itemNameExists(strItemTitle)) {
                        // TODO: put toast here for not being able to add an item with existing name
                        return;
                    }
                }

                ItemCategory originalCategory = categoryManager.
                        getCategoryByName(originalCategoryName);
                Item itemToModify = originalCategory.getItemByName(originalItemName);
                ItemCategory category = createNewCategoryIfNotExists(strCategory);

                itemToModify.setName(strItemTitle);
                itemToModify.removeCategory(originalCategory);
                itemToModify.addCategory(category);
                itemToModify.setLocationDescription(strLocation);

                originalItemName = strItemTitle;
                originalCategoryName = strCategory;

                System.out.println("------> Save called from Item Location Activity!");
                try {
                    categoryManager.save(ItemInfoScreen.this);
                    titleDisplay.setEnabled(false);
                    catDisplay.setEnabled(false);
                    locationDisplay.setEnabled(false);
                    isEditable.setChecked(false);
                    Toast message=Toast.makeText(getApplicationContext(),"Changes Successfully Saved",Toast.LENGTH_LONG);
                    ViewGroup group = (ViewGroup) message.getView();
                    TextView messageTextView = (TextView) group.getChildAt(0);
                    messageTextView.setTextSize(15);
                    message.show();

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

    private boolean itemNameExists(String proposedName) {
        for (Item item : categoryManager.getAllItems()) {
            if (item.getName().toLowerCase().equals(proposedName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void createEditableSlider() {
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
                } else {
                    titleDisplay.setEnabled(false);
                    catDisplay.setEnabled(false);
                    locationDisplay.setEnabled(false);
                    saveBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        //check the current state before we display the screen
        if(isEditable.isChecked()){
            titleDisplay.setEnabled(false);
            catDisplay.setEnabled(false);
            locationDisplay.setEnabled(false);
        }
        else {
            titleDisplay.setEnabled(false);
            catDisplay.setEnabled(false);
            locationDisplay.setEnabled(false);
        }
    }
}


