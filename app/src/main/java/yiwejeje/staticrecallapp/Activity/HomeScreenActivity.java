package yiwejeje.staticrecallapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import yiwejeje.staticrecallapp.Model.CategoryManager;
import yiwejeje.staticrecallapp.R;

public class HomeScreenActivity extends AppCompatActivity {
    CategoryManager categoryManager = CategoryManager.INSTANCE;
    private View mContentView;
    boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);
        mContentView = findViewById(R.id.fullscreen_content);
        
        // Load saved data, since this activity launches first in the app
        if (firstLoad) {
            try {
                categoryManager.load(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            firstLoad = false;
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
        Intent intent = new Intent(this, ListViewSearchActivity.class);
        startActivity(intent);
    }

    public void storeLocation(View view){
        Intent intent = new Intent(this, StoreLocationActivity.class);
        startActivity(intent);
    }

    public void goToAbout(View view){
        Intent intent = new Intent(this, AboutScreenActivity.class);
        startActivity(intent);
    }
}
