package yiwejeje.staticrecallapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class HomeScreenActivity extends AppCompatActivity {
    CategoryManager categoryManager = CategoryManager.INSTANCE;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);
        mContentView = findViewById(R.id.fullscreen_content);

        // Load saved data, since this activity launches first in the app.
        try {
            categoryManager.load(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setAppNameFont();
    }

    private void setAppNameFont() {
        TextView textView = (TextView) mContentView;
        Context context = this;
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/CODE Bold.otf");
        // Font from: http://www.fontfabric.com/code-free-font-3/
        textView.setTypeface(customFont);
    }

    public void showItemsList(View view) {
        Intent intent = new Intent(this, SearchViewActivity.class);
        startActivity(intent);
    }

    public void storeLocation(View view){
        Intent intent = new Intent(this, StoreLocationActivity.class);
        startActivity(intent);
    }
}
