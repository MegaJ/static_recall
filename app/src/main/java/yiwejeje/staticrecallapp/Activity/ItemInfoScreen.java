package yiwejeje.staticrecallapp.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

public class ItemInfoScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText titleDisplay;
    private EditText catDisplay;
    private EditText locationDisplay;
    private TextView locationPlace;
    private Button saveBtn;
    private Button unsaveBtn;
    private Button deleteBtn;
    private Switch isEditable;
    private String originalItemName;
    private String originalCategoryName;
    private String originalLocationName;
    private String selectedCategory;
    private ImageView imageFileView;
    private Spinner thisSpinner;
    private Toast toast;
    private android.net.Uri mImageUri;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private File imageFile;
    private String imageFilePath;

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

        imageFileView= (ImageView)findViewById(R.id.imgFileView);
        imageFileView.setRotation(90);

        thisSpinner=(Spinner)findViewById(R.id.editSpinner);
        locationPlace =(TextView)findViewById(R.id.textView6);
        selectedCategory=null;

        Bundle extras = getIntent().getExtras();

        originalItemName = extras.getString("item title");
        originalCategoryName = extras.getString("item category");
        originalLocationName = extras.getString("item location");


        setupTextField();
        setupToast();
        if (extras.getString("item picture path")!= null) {
            Bitmap bitmap = BitmapFactory.decodeFile(extras.getString("item picture path"));
            imageFileView.setImageBitmap(bitmap);
            imageFileView.setVisibility(View.VISIBLE);
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
                deleteItem(v);
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

    private void deleteItem(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Item foundItem = findItemByName(originalItemName);
                        foundItem.oneSidedRemoveAllCategories();
                        ItemCategory originalCategory = categoryManager.
                                getCategoryByName(originalCategoryName);
                        originalCategory.removeItem(foundItem);
                        foundItem.deleteImage();
                        updateToast("Item is successfully deleted", Toast.LENGTH_LONG);
                }

                ItemInfoScreen.this.finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are you sure to delete the item "+originalItemName).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void setupTextField(){
        uncheckedVisibility();
        initialDisplay();
        uncheckedStatus();
        isEditable=(Switch)findViewById(R.id.isEditable);
    }

    private void initialDisplay(){
        titleDisplay.setText(originalItemName);
        catDisplay.setText(originalCategoryName);
        locationDisplay.setText(originalLocationName);
    }

    private void setupToast() {
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    private void updateToast(String text, int duration) {
        toast.setText(text);
        toast.setDuration(duration);
        toast.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            contentResolver.notifyChange(mImageUri, null);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageUri);
                contentResolver.delete(mImageUri, null, null);

                imageFile = new File(this.getFilesDir() + File.separator + UUID.randomUUID() + ".jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException e) {
                updateToast("Failed to load", Toast.LENGTH_SHORT);
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
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
        locationDisplay.setText(originalLocationName);
        titleDisplay.setEnabled(false);
        catDisplay.setEnabled(false);
        locationDisplay.setEnabled(false);
        isEditable.setChecked(false);
        updateToast("Changes are disregarded.", Toast.LENGTH_LONG);
    }


    private void saveChanges(){
        String strItemTitle = titleDisplay.getText().toString();
        String strCategory = catDisplay.getText().toString();
        String strLocation = locationDisplay.getText().toString();
        checkItemTitle(strItemTitle);
        ItemCategory originalCategory = categoryManager.
                getCategoryByName(originalCategoryName);
        Item itemToModify = originalCategory.getItemByName(originalItemName);
        ItemCategory category = createNewCategoryIfNotExists(strCategory);
        itemToModify.setName(strItemTitle);
        itemToModify.removeCategory(originalCategory);
        itemToModify.addCategory(category);
        itemToModify.setLocationDescription(strLocation);
        if (imageFile != null) {
            imageFilePath = imageFile.getAbsolutePath();
            itemToModify.setPicturePath(imageFilePath);
        }
        originalItemName = strItemTitle;
        originalCategoryName = strCategory;
        originalLocationName = strLocation;
        try {
            categoryManager.save(ItemInfoScreen.this);
            titleDisplay.setEnabled(false);
            catDisplay.setEnabled(false);
            locationDisplay.setEnabled(false);
            isEditable.setChecked(false);
            updateToast("Changes are successfully saved.", Toast.LENGTH_LONG);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkItemTitle(String strItemTitle){
        if (strItemTitle.equals("")) {
            updateToast("Item title cannot be blank.", Toast.LENGTH_LONG);;
            return;
        }

        if (!strItemTitle.equals(originalItemName)) {
            if (itemNameExists(strItemTitle)) {
                updateToast("This item name already exists.", Toast.LENGTH_LONG);
                return;
            }
        }
    }



    private void createEditableSlider() {
        isEditable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    checkedDisplay();
                    checkedSpacing();
                    setupDropdown();
                } else {
                    setUncheckedVisibility();
                    uncheckedSpacing();
                }
            }
        });
        initialStatus();
        if(isEditable.isChecked()){
            checkedStatus();
        }
        else {
            uncheckedStatus();
            uncheckedVisibility();
        }
    }

    private void initialStatus(){
        if(isEditable.isChecked()){
            checkedStatus();
        }
        else {
            uncheckedStatus();
            uncheckedVisibility();
        }
    }

    private void checkedVisibility(){
        saveBtn.setVisibility(View.VISIBLE);
        unsaveBtn.setVisibility(View.VISIBLE);
        thisSpinner.setVisibility(View.VISIBLE);
    }

    private void checkedStatus(){
        saveBtn.setVisibility(View.VISIBLE);
        unsaveBtn.setVisibility(View.VISIBLE);
        thisSpinner.setVisibility(View.VISIBLE);
    }

    private void checkedDisplay(){
        checkedVisibility();
        checkedStatus();
    }

    private void uncheckedVisibility(){
        saveBtn.setVisibility(View.INVISIBLE);
        unsaveBtn.setVisibility(View.INVISIBLE);
        thisSpinner.setVisibility(View.INVISIBLE);
    }

    private void uncheckedStatus(){
        titleDisplay.setEnabled(false);
        catDisplay.setEnabled(false);
        locationDisplay.setEnabled(false);
    }

    private void setUncheckedVisibility(){
        uncheckedStatus();
        uncheckedVisibility();
    }

    private void checkedSpacing(){
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.BELOW, R.id.editSpinner);
        p.addRule(RelativeLayout.ALIGN_LEFT, R.id.editSpinner);
        p.setMargins(10, 75, 5, 0);

        locationPlace.setLayoutParams(p);
    }

    private void uncheckedSpacing(){
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        p.addRule(RelativeLayout.BELOW, R.id.ItemCategory);
        p.addRule(RelativeLayout.ALIGN_LEFT, R.id.ItemCategory);
        p.setMargins(5, 75, 5, 0);
        locationPlace.setLayoutParams(p);
    }


    private void setupDropdown(){
        List<String> categories=categoriesForDropDown();
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
            if (position != 0) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                catDisplay.setText(selectedCategory);
                updateToast("Selected: " + selectedCategory, Toast.LENGTH_LONG);
            }
        }

    @Override
    public void onNothingSelected(AdapterView<?> parentView){
    }

    private List<String> categoriesForDropDown(){
        CategoryManager myCategoryManager = CategoryManager.INSTANCE;
        Collection<ItemCategory> allCategories = myCategoryManager.getAllCategories();
        List<String> categories = new ArrayList<String>();
        categories.add("Or select from existing categories...");
        for (ItemCategory c:allCategories){
            categories.add(c.toString());
        }
        return categories;
    }

}


