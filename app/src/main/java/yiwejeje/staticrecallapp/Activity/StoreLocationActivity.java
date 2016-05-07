package yiwejeje.staticrecallapp.Activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.net.Uri;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

public class StoreLocationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText itemTitle;
    private EditText itemCategory;  //right now only allow for one category for the simplicity
    private EditText itemLocation;
    private TextView locationView;


    private File imageFile;
    private String imageFilePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Spinner spinner;
    private String selectedCategory;

    CategoryManager categoryManager = CategoryManager.INSTANCE;
    private ImageButton typeIn;
    private ImageButton makeRecording;



    private ImageView itemImageView;
    private android.net.Uri mImageUri;

    private Toast toast;

    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        itemTitle = (EditText) findViewById(R.id.ItemText);
        itemCategory = (EditText) findViewById(R.id.CatText);
        itemLocation = (EditText) findViewById(R.id.LocationText);
        locationView=(TextView) findViewById(R.id.textView);
        typeIn = (ImageButton) findViewById(R.id.TextButton);
        setup();

    }

    private void setup(){
        setupToast();
        setupCamera();
        setDropDownMenu();
        setUpLocation();
        setupAddItemButton();

    }

    private void updateToast(String text, int duration) {
        toast.setText(text);
        toast.setDuration(duration);
        toast.show();
    }

    private void setupCamera() {
        final PackageManager pm = this.getPackageManager();
        final ImageButton camButton = (ImageButton) findViewById(R.id.CameraButton);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    updateToast("No camera installed", Toast.LENGTH_SHORT);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });
    }


    private void persistEverything() {
        try {
            System.out.println("-----> Attempt at saving!");
            categoryManager.save(StoreLocationActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean addItemToCategoryManager(Item item) {
        String typedText = itemCategory.getText().toString();
        String finalCategoryName;

        if (selectedCategory != null) {
            finalCategoryName = selectedCategory;
        } else if (!typedText.equals("")) {
            finalCategoryName = typedText;
        } else {
            finalCategoryName = "Uncategorized";
        }

        boolean itemAdded = false;
        boolean categoryAdded = false;
        boolean addingSuccessful = false;
        ItemCategory existingCategory = categoryManager.getCategoryByName(finalCategoryName);
        if (existingCategory == null) {
            existingCategory = new ItemCategory(finalCategoryName);
            itemAdded = existingCategory.addItem(item);
            categoryAdded = categoryManager.addCategory(existingCategory);
            addingSuccessful = itemAdded && categoryAdded;
        } else {
            addingSuccessful = existingCategory.addItem(item);
        }

        return addingSuccessful;
    }

    private void setupAddItemButton() {
        Button addNewItem;
        addNewItem = (Button) findViewById(R.id.AddButton);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
    }

    private void addNewItem(){
        String strItemTitle = itemTitle.getText().toString();
        String strCategory = itemCategory.getText().toString();
        String strLocation = itemLocation.getText().toString();

        if (strItemTitle.equals("")) {
            updateToast("Item title cannot be blank.", Toast.LENGTH_SHORT);
            return;
        }

        if (itemNameExists(strItemTitle)) {
            updateToast("This item name already exists.", Toast.LENGTH_SHORT);
            return;
        }

        Item newItem = new Item(strItemTitle, strLocation);

        if (imageFile != null) {
            imageFilePath = imageFile.getAbsolutePath();
            newItem.setPicturePath(imageFilePath);
            System.out.println("---> My image file is " + imageFile.getAbsolutePath());
        }

        boolean addingSuccessful = addItemToCategoryManager(newItem);
        persistEverything();
        displayAddItemResult(addingSuccessful);

    }

    private void setupToast() {
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    //CAMERA CODE -- Picture intent and actual file-writing

    private void dispatchTakePictureIntent() {
        itemLocation.setVisibility(View.INVISIBLE);
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
                itemImageView.setVisibility(View.VISIBLE);
                itemImageView.setImageBitmap(bitmap);
                System.out.println("-----> Bitmap is " + bitmap.getWidth() + " by " + bitmap.getHeight());

                contentResolver.delete(mImageUri, null, null);

                imageFile = new File(this.getFilesDir() + File.separator + UUID.randomUUID() + ".jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();

                System.out.println("-------> Image path of jpeg is: " + imageFile.getAbsolutePath());

            } catch (IOException e) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.d("StoreLocationActivity", "Failed to load", e);
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void displayAddItemResult(boolean addedResult){
        if (addedResult) {
            resetVisibleFields();
            updateToast("Item is successfully added.", Toast.LENGTH_LONG);
        }
    }

    private void resetVisibleFields() {
        clearTextField();
        clearImageView();
    }

    private void clearTextField(){
        itemTitle.getText().clear();
        itemCategory.getText().clear();
        itemLocation.getText().clear();
        spinner.setSelection(0);
    }

    private void clearImageView(){
        itemImageView.setImageBitmap(null);
        itemImageView.setImageResource(R.drawable.camera);
        itemImageView.setVisibility(View.INVISIBLE);
    }

    private void setUpLocation(){
        setVisibility();
        typeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemLocation.setVisibility(View.VISIBLE);
                itemImageView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setVisibility(){
        itemCategory.setVisibility(View.INVISIBLE);
        itemLocation.setVisibility(View.INVISIBLE);
        itemImageView = (ImageView) findViewById(R.id.ItemImageView);
        itemImageView.setRotation(90);
        itemImageView.setVisibility(View.INVISIBLE);
    }



    private void setDropDownMenu(){
        CategoryManager myCategoryManager = CategoryManager.INSTANCE;
        Collection<ItemCategory> allCategories = myCategoryManager.getAllCategories();
        List<String> categories = new ArrayList<String>();
        categories.add("Select a category");
        categories.add("Add A New Category...");
        for (ItemCategory c:allCategories){
            categories.add(c.toString());
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

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
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            itemCategory.setVisibility(View.INVISIBLE);
            return;
        }
        if (position == 1) {
            itemCategory.setVisibility(View.VISIBLE);
            changeSpacing();
        } else {
            itemCategory.setVisibility(View.INVISIBLE);
            selectedCategory = parent.getItemAtPosition(position).toString();
            updateToast("Selected: " + selectedCategory, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void changeSpacing(){
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.BELOW, R.id.CatText);
        p.addRule(RelativeLayout.ALIGN_LEFT, R.id.CatText);
        p.setMargins(5,70,5,0);
        locationView.setLayoutParams(p);
    }

    // http://stackoverflow.com/questions/33733075/close-keypad-when-touch-or-click-outside-of-edittext-in-android
    /*
    To hide the keyboard every time the user touches anywhere on screen
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    private boolean itemNameExists(String proposedName) {
        for (Item item : categoryManager.getAllItems()) {
            if (item.getName().toLowerCase().equals(proposedName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

