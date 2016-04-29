package yiwejeje.staticrecallapp.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.util.UUID;

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
    CategoryManager categoryManager = CategoryManager.INSTANCE;
    private ImageButton typeIn;
    private ImageButton makeRecording;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;
    private Button stopButton;
    private Button playButton;
    private Button recordButton;

    private ImageView itemImageView;

    private Context context;
    private int duration;
    private CharSequence toastText;

    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final PackageManager pm = this.getPackageManager();
        final ImageButton camButton = (ImageButton) findViewById(R.id.CameraButton);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No camera installed", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        /*
        setup audio

         */

        recordButton = (Button) findViewById(R.id.recordButton);
        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        if (!hasMicrophone()) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
        } else {
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
        }

        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/myaudio.3gp";
        itemTitle = (EditText) findViewById(R.id.ItemText);
        itemCategory = (EditText) findViewById(R.id.CatText);
        itemLocation = (EditText) findViewById(R.id.LocationText);
        typeIn = (ImageButton) findViewById(R.id.TextButton);
        makeRecording=(ImageButton) findViewById(R.id.AudioButton);

        setDropDownMenu();
        setUpLocation();

        addNewItem = (Button) findViewById(R.id.AddButton);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strItemTitle = itemTitle.getText().toString();
                String strCategory = itemCategory.getText().toString();
                String strLocation = itemLocation.getText().toString();

                if (strItemTitle.equals("")) {
                    context = getApplicationContext();
                    toastText = "Item title cannot be blank.";
                    duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, toastText, duration);
                    toast.show();
                    return;
                }

                if (itemNameExists(strItemTitle)) {
                    context = getApplicationContext();
                    toastText = "This item name already exists.";
                    duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, toastText, duration);
                    toast.show();
                    return;
                }

                newItem = new Item(strItemTitle, strLocation);

                if (imageFile != null) {
                    newItem.setPicture(imageFile);
                    System.out.println("---> My image file is " + imageFile.getAbsolutePath());
                }

                boolean addingSuccessful = addItemToCategoryManager();
                persistEverything();
                displayResult(addingSuccessful);
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

    private boolean addItemToCategoryManager() {
        String typedText = itemCategory.getText().toString();

        if (selectedCategory != null) {
            finalCategory = selectedCategory;
        } else if (!typedText.equals("")) {
            finalCategory = typedText;
        } else {
            finalCategory = "Uncategorized";
        }

        itemCategory.getText().toString();

        System.out.println("-----> selected category is this: " + selectedCategory);

        boolean itemAdded = false;
        boolean categoryAdded = false;
        boolean addingSuccessful = false;
        ItemCategory existingCategory = categoryManager.getCategoryByName(finalCategory);
        if (existingCategory == null) {
            existingCategory = new ItemCategory(finalCategory);
            itemAdded = existingCategory.addItem(newItem);
            categoryAdded = categoryManager.addCategory(existingCategory);
            addingSuccessful = itemAdded && categoryAdded;
        } else {
            addingSuccessful = existingCategory.addItem(newItem);
        }

        return addingSuccessful;
    }




    //CAMERA CODE -- Picture intent and actual file-writing

    private void dispatchTakePictureIntent() {
        itemImageView.setVisibility(View.VISIBLE);
        itemLocation.setVisibility(View.INVISIBLE);
        recordButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);



        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Disables camera function if the device does not support the camera hardware
        //If camera installed, dispatches the picture intent.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            itemImageView.setImageBitmap(thumbnail);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (thumbnail != null) {
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                }
            imageFile = new File(this.getFilesDir() + File.separator + UUID.randomUUID() + ".jpg");
            try {
                FileOutputStream fo = new FileOutputStream(imageFile);
                System.out.println("----> Attempt to write file");
                fo.write(bytes.toByteArray());
                fo.close();
                System.out.println("File stored at" + imageFile.getAbsolutePath());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Uri uri = Uri.fromFile(imageFile);
            //itemImageView.setImageURI(uri);
            //File filePath = getFileStreamPath(imageFile.getAbsolutePath());
            //itemImageView.setImageDrawable(Drawable.createFromPath(imageFile.getAbsolutePath().toString()));
        }
    }


    private void displayResult(boolean addedResult){
        if (addedResult){
            itemTitle.setText("");
            itemCategory.setText("");

            itemLocation.setText("");
            spinner.setSelection(0);
            Toast message=Toast.makeText(getApplicationContext(),"Item Successfully Added",Toast.LENGTH_LONG);
            ViewGroup group = (ViewGroup) message.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(15);
            message.show();
        }
    }



    /*
    Code for the audio recording
     */

    //have it go away

    private void setUpLocation(){
        itemCategory.setVisibility(View.INVISIBLE);
        itemLocation.setVisibility(View.INVISIBLE);
        itemImageView = (ImageView) findViewById(R.id.ItemImageView);
        itemImageView.setVisibility(View.INVISIBLE);
        recordButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);

        typeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemLocation.setVisibility(View.VISIBLE);
                itemImageView.setVisibility(View.INVISIBLE);
                recordButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.INVISIBLE);
            }
        });

        makeRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemLocation.setVisibility(View.INVISIBLE);
                itemImageView.setVisibility(View.INVISIBLE);
                recordButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.VISIBLE);
            }
        });
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

    public void recordAudio (View view) throws IOException {
        isRecording = true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    public void stopAudio (View view) {
        stopButton.setEnabled(false);
        playButton.setEnabled(true);

        if (isRecording) {
            recordButton.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
        }
    }

    public void playAudio (View view) throws IOException {
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    /*
    For the dropdown list
     */
    private void setDropDownMenu(){
        CategoryManager myCategoryManager = CategoryManager.INSTANCE;
        Collection<ItemCategory> allCategories = myCategoryManager.getAllCategories();
        List<String> categories = new ArrayList<String>();
        categories.add("(Select from existing categories)");
        categories.add("add a new category...");
        for (ItemCategory c:allCategories){
            categories.add(c.toString());
        }
        spinner=(Spinner) findViewById(R.id.spinner);
        //spinner.setPrompt("aaa");

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                // do whatever you want with this text view
                textView.setTextSize(17);
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
        } else {
            itemCategory.setVisibility(View.INVISIBLE);
            selectedCategory = parent.getItemAtPosition(position).toString();

            Toast m=Toast.makeText(parent.getContext(), "Selected: " + selectedCategory, Toast.LENGTH_LONG);
            ViewGroup group = (ViewGroup) m.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(15);
            m.show();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


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

