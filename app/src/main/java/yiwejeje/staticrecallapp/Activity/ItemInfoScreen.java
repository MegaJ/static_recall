package yiwejeje.staticrecallapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;

import java.io.IOException;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.R;

public class ItemInfoScreen extends AppCompatActivity {
    private EditText titleDisplay;
    private EditText catDisplay;
    private EditText locationDisplay;
    private Button saveBtn;

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
        setContentView(R.layout.activity_search_location_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        titleDisplay=(EditText)findViewById(R.id.itemTitle);
        catDisplay=(EditText)findViewById(R.id.ItemCategory);
        locationDisplay=(EditText)findViewById(R.id.ItemLocation);
        saveBtn=(Button)findViewById(R.id.saveBtn1);

        // TODO: set an onClickListener on the saveBtn

        Bundle extras = getIntent().getExtras();
        String title=extras.getString("item title");
        String category=extras.getString("item category");
        saveBtn.setVisibility(View.INVISIBLE);
        titleDisplay.setText(title);
        catDisplay.setText(category);
        titleDisplay.setEnabled(false);
        catDisplay.setEnabled(false);
        locationDisplay.setEnabled(false);

        //how to pass an object

.
        if (extras.getString("item location")!= null){
            String location=extras.getString("item location");
            locationDisplay.setText(location);
        }

        createEditableSlider();
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


