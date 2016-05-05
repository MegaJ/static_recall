package yiwejeje.staticrecallapp.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemSelectedListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

public class ItemInfoScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText titleDisplay;
    private EditText catDisplay;
    private EditText locationDisplay;
    private TextView locationplace;
    private Button saveBtn;
    private Button unsaveBtn;
    private Button deleteBtn;
    private Button newBtn;
    private Switch isEditable;
    private String originalItemName;
    private String originalCategoryName;
    private String location;
    //private String originalLocationName;
    private ImageView imageFileView;
    private Spinner thisSpinner;
    private String selectedCategory;


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
        unsaveBtn=(Button)findViewById(R.id.disregard);
        deleteBtn=(Button)findViewById(R.id.deleteBtn1);
        newBtn=(Button)findViewById(R.id.newPic);
        imageFileView= (ImageView)findViewById(R.id.imgFileView);
        imageFileView.setRotation(90);
        thisSpinner=(Spinner)findViewById(R.id.editSpinner);
        locationplace=(TextView)findViewById(R.id.textView6);
        selectedCategory=null;

        Bundle extras = getIntent().getExtras();
        originalItemName = extras.getString("item title");
        originalCategoryName = extras.getString("item category");
        //originalLocationName = extras.getString("item location");

        saveBtn.setVisibility(View.INVISIBLE);
        imageFileView.setVisibility(View.INVISIBLE);
        titleDisplay.setText(originalItemName);
        catDisplay.setText(originalCategoryName);
        titleDisplay.setEnabled(false);
        catDisplay.setEnabled(false);
        locationDisplay.setEnabled(false);
        isEditable=(Switch)findViewById(R.id.isEditable);



        if (extras.getString("item location")!= null){
            location=extras.getString("item location");
            locationDisplay.setText(location);
        }

        if (extras.getString("item picture path")!= null) {
            System.out.println("-----> Item's picture file is at" + extras.getString("item picture path"));
            Bitmap bitmap = BitmapFactory.decodeFile(extras.getString("item picture path"));
            imageFileView.setImageBitmap(bitmap);
            imageFileView.setVisibility(View.VISIBLE);
            newBtn.setVisibility(View.VISIBLE);
            locationDisplay.setVisibility(View.INVISIBLE);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        unsaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disregardChanges();
            }
        });




        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Item foundItem = findItemByName(originalItemName);

                                foundItem.oneSidedRemoveAllCategories();
                                ItemCategory originalCategory = categoryManager.
                                        getCategoryByName(originalCategoryName);
                                originalCategory.removeItem(foundItem);
                                foundItem.deleteImage();

                                Toast message=Toast.makeText(getApplicationContext(),"Item is successfully deleted",Toast.LENGTH_LONG);
                                ViewGroup group = (ViewGroup) message.getView();
                                TextView messageTextView = (TextView) group.getChildAt(0);
                                messageTextView.setTextSize(15);
                                message.show();
                        }

                        ItemInfoScreen.this.finish();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure to delete the item "+originalItemName).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        createEditableSlider();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            categoryManager.save(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Item foundItem = findItemByName(proposedName);
        if (foundItem != null) {
            return true;
        }
        return false;
    }

    private Item findItemByName(String itemName) {
        for (Item item : categoryManager.getAllItems()) {
            if (item.getName().toLowerCase().equals(itemName.toLowerCase())) {
                return item;
            }
        }
        return null;
    }


    private void disregardChanges(){
        titleDisplay.setText(originalItemName);
        catDisplay.setText(originalCategoryName);
        if (location != null ){
            locationDisplay.setText(location);
        }
        titleDisplay.setEnabled(false);
        catDisplay.setEnabled(false);
        locationDisplay.setEnabled(false);
        isEditable.setChecked(false);
        Toast message=Toast.makeText(getApplicationContext(),"Changes are disregarded.",Toast.LENGTH_LONG);
        ViewGroup group = (ViewGroup) message.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(15);
        message.show();

    }


    private void saveChanges(){
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
            Toast message=Toast.makeText(getApplicationContext(),"Changes are successfully saved.",Toast.LENGTH_LONG);
            ViewGroup group = (ViewGroup) message.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(15);
            message.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    unsaveBtn.setVisibility(View.VISIBLE);
                    thisSpinner.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    p.addRule(RelativeLayout.BELOW, R.id.editSpinner);
                    p.addRule(RelativeLayout.ALIGN_LEFT, R.id.editSpinner);
                    p.setMargins(10, 75, 5, 0);

                    locationplace.setLayoutParams(p);
                    setupDropdown();

                } else {
                    titleDisplay.setEnabled(false);
                    catDisplay.setEnabled(false);
                    locationDisplay.setEnabled(false);
                    saveBtn.setVisibility(View.INVISIBLE);
                    unsaveBtn.setVisibility(View.INVISIBLE);
                    thisSpinner.setVisibility(View.INVISIBLE);
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    p.addRule(RelativeLayout.BELOW, R.id.ItemCategory);
                    p.addRule(RelativeLayout.ALIGN_LEFT, R.id.ItemCategory);
                    p.setMargins(5, 75, 5, 0);

                    locationplace.setLayoutParams(p);
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
            unsaveBtn.setVisibility(View.INVISIBLE);
            thisSpinner.setVisibility(View.INVISIBLE);
            //newBtn.setVisibility(View.INVISIBLE);
        }
    }


    private void setupDropdown(){
        CategoryManager myCategoryManager = CategoryManager.INSTANCE;
        Collection<ItemCategory> allCategories = myCategoryManager.getAllCategories();
        List<String> categories = new ArrayList<String>();
        categories.add("Or select from existing categories...");
        for (ItemCategory c:allCategories){
            categories.add(c.toString());
        }


        thisSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                // do whatever you want with this text view
                textView.setTextSize(17);
                textView.setTextColor(Color.parseColor("#4281A4"));

                return view;
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thisSpinner.setAdapter(dataAdapter);

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != 1 && position !=0) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                catDisplay.setText(selectedCategory);
                Toast message=Toast.makeText(getApplicationContext(),"Selected: " + selectedCategory,Toast.LENGTH_LONG);
                ViewGroup group = (ViewGroup) message.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(15);
                message.show();


            }
        }

    @Override
    public void onNothingSelected(AdapterView<?> parentView){
    }

}


