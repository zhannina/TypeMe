package com.example.zsarsenbayev.typeme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

public class CirclesActivity extends AppCompatActivity {

    SharedPreferences prefs;
    public static final String MYPREFS = "MyPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
    }

    @Override
    protected void onResume(){
        super.onResume();

        getDisplayContentSize();
    }

    public void getDisplayContentSize(){
        float result;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        result = getResources().getDimensionPixelSize(resourceId);

        Display mdisp = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        float maxX = mdispSize.x;
        float maxY = mdispSize.y-result;
        prefs = getApplicationContext().getSharedPreferences(MYPREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(MYPREFS, MODE_PRIVATE).edit();
        editor.putFloat("maxX", maxX);
        editor.putFloat("maxY", maxY);

        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        // super.onBackPressed(); // Comment this super call to avoid calling finish() or fragmentmanager's backstack pop operation.
//        moveTaskToBack(true);
        Toast.makeText(this, "Please do not press the back button", Toast.LENGTH_SHORT).show();
    }
}
