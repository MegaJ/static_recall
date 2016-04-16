package yiwejeje.staticrecallapp.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.Model.Item;
import yiwejeje.staticrecallapp.Model.ItemCategory;
import yiwejeje.staticrecallapp.R;

public class StoreLocationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button addNewItem;
    private Item newItem;
    private EditText itemTitle;
    private EditText itemCategory;  //right now only allow for one category for the simplicity
    private EditText itemLocation;
    private File imageFile;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Spinner spinner;
    private String selectedCategory;
    private String finalCategory;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //changeBG();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final PackageManager pm = this.getPackageManager();
        final ImageButton camButton = (ImageButton) findViewById(R.id.CameraButton);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No camera installed", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    dispatchTakePictureIntent();
                }
            }
        });


        itemTitle=(EditText)findViewById(R.id.ItemText);
        itemCategory=(EditText)findViewById(R.id.CatText);
        itemLocation=(EditText)findViewById(R.id.LocationText);
        setDropDownMenu();
        selectedCategory="";
        finalCategory="";
        addNewItem=(Button)findViewById(R.id.AddButton);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strItemTitle = itemTitle.getText().toString();
                String strCategory = itemCategory.getText().toString();
                String strLocation = itemLocation.getText().toString();
                newItem = new Item(strItemTitle, strLocation);
                if (imageFile != null){
                    newItem.setPicture(imageFile);
                    System.out.println("---> My image file is " + imageFile.getAbsolutePath());
                }

                CategoryManager myCategoryManager = CategoryManager.INSTANCE;
                try {
                    System.out.println("-----> Attempt at saving!");
                    myCategoryManager.save(StoreLocationActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (selectedCategory != "(Select from existing categories)") {
                    finalCategory = selectedCategory;
                } else {
                    finalCategory = strCategory;
                }
                //Collection<ItemCategory> allCategories = myCategoryManager.getAllCategories();
                ItemCategory existedCategory = myCategoryManager.getCategoryByName(finalCategory);

                //Collection<ItemCategory> allCategories = myCategoryManager.getAllCategories();
                //ItemCategory existedCategory = myCategoryManager.getCategoryByName(strCategory);

                if (existedCategory == null) {
                    ItemCategory newCategory = new ItemCategory(finalCategory);
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

    private void setDropDownMenu(){
        CategoryManager myCategoryManager = CategoryManager.INSTANCE;
        Collection<ItemCategory> allCategories = myCategoryManager.getAllCategories();
        List<String> categories = new ArrayList<String>();
        categories.add("(Select from existing categories)");
        for (ItemCategory c:allCategories){
            categories.add(c.toString());
        }
        spinner=(Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                // do whatever you want with this text view
                textView.setTextSize(25);
                return view;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }


    //*******************new code

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //setContentView(R.layout.camera_content);
        //mImage = (ImageView) findViewById(R.id.camera_image);

        //Disables camera function if the device does not support the camera hardware
        //If camera installed, dispatches the picture intent.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            //2
            //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            //mImage.setImageBitmap(thumbnail);
            //3
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //if (thumbnail != null) {
                //thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //}
            //4
            imageFile = new File(this.getFilesDir() + File.separator + "image.jpg");
            //System.out.println("-----> Filepath = " + file.getAbsolutePath());
            try {
                //file.createNewFile();
                FileOutputStream fo = new FileOutputStream(imageFile);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private void displayResult(boolean addedResult){
        if (addedResult){
            itemTitle.setText("");
            itemCategory.setText("");
            //TO DO: DO SOMETHING WITH THE SPINNER
            itemLocation.setText("");
            Toast message=Toast.makeText(getApplicationContext(),"Item Successfully Added",Toast.LENGTH_LONG);
            ViewGroup group = (ViewGroup) message.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(25);
            message.show();
        }

    }

    private void changeBG(){
        View backgroundimage = findViewById(R.id.background);
        Drawable background = backgroundimage.getBackground();
        background.setAlpha(150);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Selected: " + selectedCategory, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}

