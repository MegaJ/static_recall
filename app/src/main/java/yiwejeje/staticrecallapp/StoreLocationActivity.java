package yiwejeje.staticrecallapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreLocationActivity extends AppCompatActivity {

    Button addNewItem;
    EditText itemTitle;
    EditText itemCategory;  //right now only allow for one category for the simplicity
    EditText itemLocation;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImage;

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
        final ImageButton camButton = (ImageButton) findViewById(R.id.CameraButton);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        setContentView(R.layout.camera_content);
        mImage = (ImageView) findViewById(R.id.camera_image);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            //2
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            mImage.setImageBitmap(thumbnail);
            //3
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //4
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
