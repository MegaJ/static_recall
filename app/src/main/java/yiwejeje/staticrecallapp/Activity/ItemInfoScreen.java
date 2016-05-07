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
    private Button newBtn;
    private Switch isEditable;
    private String originalItemName;
    private String originalCategoryName;
    private String originalLocationName;

    private ImageView imageFileView;
    private Spinner thisSpinner;
    private Toast toast;

    private String selectedCategory;

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

        saveBtn.setVisibility(View.INVISIBLE);
        imageFileView.setVisibility(View.INVISIBLE);

        titleDisplay.setText(originalItemName);
        catDisplay.setText(originalCategoryName);
        locationDisplay.setText(originalLocationName);

        titleDisplay.setEnabled(false);
        catDisplay.setEnabled(false);
        locationDisplay.setEnabled(false);

        isEditable=(Switch)findViewById(R.id.isEditable);

        setupToast();

        if (extras.getString("item picture path")!= null) {
            System.out.println("-----> Item's picture file is at" + extras.getString("item picture path"));
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

                                updateToast("Item is successfully deleted", Toast.LENGTH_LONG);
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

    private void setupToast() {
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    private void updateToast(String text, int duration) {
        toast.setText(text);
        toast.setDuration(duration);
        toast.show();
    }

    private void dispatchTakePictureIntent(){
        mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

        //Disables camera function if the device does not support the camera hardware
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            contentResolver.notifyChange(mImageUri, null);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageUri);
//                itemImageView.setVisibility(View.VISIBLE);
//                itemImageView.setImageBitmap(bitmap);
                System.out.println("-----> Bitmap is " + bitmap.getWidth() + " by " + bitmap.getHeight());

                contentResolver.delete(mImageUri, null, null);

                imageFile = new File(this.getFilesDir() + File.separator + UUID.randomUUID() + ".jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();

                System.out.println("-------> Image path of jpeg is: " + imageFile.getAbsolutePath());

            } catch (IOException e) {
                updateToast("Failed to load", Toast.LENGTH_SHORT);
                Log.d("StoreLocationActivity", "Failed to load", e);
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

        if (strItemTitle.equals("")) {
            updateToast("Item title cannot be blank.", Toast.LENGTH_LONG);;
            return;
        }

        // if the item is named something ELSE that exists, can't do it.
        if (!strItemTitle.equals(originalItemName)) {
            if (itemNameExists(strItemTitle)) {
                updateToast("This item name already exists.", Toast.LENGTH_LONG);
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

        if (imageFile != null) {
            imageFilePath = imageFile.getAbsolutePath();
            itemToModify.setPicturePath(imageFilePath);
        }

        originalItemName = strItemTitle;
        originalCategoryName = strCategory;
        originalLocationName = strLocation;

        System.out.println("------> Save called from Item Location Activity!");
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

                    locationPlace.setLayoutParams(p);
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

                    locationPlace.setLayoutParams(p);
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
            if (position != 0) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                catDisplay.setText(selectedCategory);
                updateToast("Selected: " + selectedCategory, Toast.LENGTH_LONG);
            }
        }

    @Override
    public void onNothingSelected(AdapterView<?> parentView){
    }

}


