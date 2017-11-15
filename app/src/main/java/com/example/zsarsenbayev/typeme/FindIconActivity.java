package com.example.zsarsenbayev.typeme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class FindIconActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 12;
    Button continueBtn;
    ImageView imageToShow;

    public static ArrayList<CellContent> icons = new ArrayList<CellContent>();
    CellContent iconToFind;
    int posToFind;
    String iconName;
    TextView iconNameTextView, findIconText;
    ArrayList<Class<?>> classList;

    public static HashMap<CellContent, Integer> iconsMap = new HashMap<>();

    Long startTime, endTime, diff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_icon);
        populateArrayList();
        SharedPreferences prefs = getSharedPreferences(DisplayGridActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("FIRSTTIME", true);
        editor.commit();
        requestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        continueBtn = (Button) findViewById(R.id.continue_btn);
        iconNameTextView = (TextView) findViewById(R.id.icon_text_view);
        imageToShow = (ImageView) findViewById(R.id.icon_img);
        findIconText = (TextView) findViewById(R.id.find_icon_text);

        continueBtn.setVisibility(View.VISIBLE);
        iconNameTextView.setVisibility(View.VISIBLE);
        imageToShow.setVisibility(View.VISIBLE);
        findIconText.setVisibility(View.VISIBLE);

        startTime = System.currentTimeMillis();

        classList = new ArrayList<Class<?>>();
        ArrayList test = getIntent().getParcelableArrayListExtra("activity");

        for(int i = 0; i < test.size(); i++){
            classList.add((Class<?>)test.get(i));
        }

        SharedPreferences prefs = getSharedPreferences(DisplayGridActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        if (iconsMap.isEmpty() && !prefs.getBoolean("FIRSTTIME", true)) {
//            finish();
            finishActivity();
        } else {
            initializeHash();

            Random r = new Random(System.nanoTime());
            int random  = r.nextInt(iconsMap.size());

            CellContent temp = new CellContent("", 0);
            int index = 0;
            findKey:
            for (CellContent key : iconsMap.keySet()) {
                if (index==random) {
                    posToFind = iconsMap.get(key);
                    iconToFind = key;
                    temp = key;
                    imageToShow.setImageResource(key.getDrawableID());
                    iconName = key.getName();
                    iconNameTextView.setText(key.getName());
                    break findKey;
                }
                index++;
            }


            iconsMap.remove(temp);

            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    endTime = System.currentTimeMillis();
                    diff = endTime-startTime;
                    //WITHOUT BLANK SCREEN
                    Intent intent = new Intent(getBaseContext(), DisplayGridActivity.class);
                    intent.putExtra("iconToFind", iconToFind);
                    intent.putExtra("positionToPlace", posToFind);
                    intent.putExtra("iconName", iconName);
                    intent.putExtra("timeToRemember", diff);
                    intent.putExtra("activity", classList);
                    startActivity(intent);

                    Log.d("timeToRemember", ""+diff);
                }
            });
        }

    }

    public void initializeHash() {
        SharedPreferences prefs = getSharedPreferences(DisplayGridActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        if (iconsMap.isEmpty()) {
            ArrayList<Integer> pos = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23));
            int i = 0;
            Collections.shuffle(pos, new Random(System.nanoTime()));
            for (CellContent c : icons) {
                iconsMap.put(c, pos.get(i));
                i++;
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("FIRSTTIME", false);
            editor.commit();
        }
    }


    public void populateArrayList() {
        icons.clear();
        icons.add(new CellContent("Adobe Acrobat", R.drawable.adobereader));
        icons.add(new CellContent("Angry Birds", R.drawable.angrybirds));
        icons.add(new CellContent("Candy Crush Saga", R.drawable.candycrush));
        icons.add(new CellContent("ChatON", R.drawable.chaton));
        icons.add(new CellContent("Chrome", R.drawable.chrome));
        icons.add(new CellContent("Drive", R.drawable.drive));
        icons.add(new CellContent("Dropbox", R.drawable.dropbox));
        icons.add(new CellContent("Facebook", R.drawable.facebook));
        icons.add(new CellContent("Fruit Ninja", R.drawable.fruitninja));
        icons.add(new CellContent("Gmail", R.drawable.gmail));
        icons.add(new CellContent("Maps", R.drawable.googlemaps));
        icons.add(new CellContent("Google+", R.drawable.googleplus));
        icons.add(new CellContent("Hangouts", R.drawable.hangouts));
        icons.add(new CellContent("Instagram", R.drawable.instagram));
        icons.add(new CellContent("Line", R.drawable.line));
        icons.add(new CellContent("Messenger", R.drawable.messenger));
        icons.add(new CellContent("Shazam", R.drawable.shazam));
        icons.add(new CellContent("Skype", R.drawable.skype));
        icons.add(new CellContent("Temple Run", R.drawable.templerun));
        icons.add(new CellContent("Translate", R.drawable.translate));
        icons.add(new CellContent("Twitter", R.drawable.twitter));
        icons.add(new CellContent("Viber", R.drawable.viber));
        icons.add(new CellContent("WhatsApp", R.drawable.whatsapp));
        icons.add(new CellContent("YouTube", R.drawable.youtube));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    requestPermissions();

                }
                return;
            }

        }
    }

    private void requestPermissions()
    {
        Log.d("TAG", "Whatever1");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Whatever2");

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
        else {
            Log.d("TAG", "Whatever3");

        }
    }

    public void finishActivity(){

        if(classList.size()!=0) {
            Random r = new Random();
            int i = r.nextInt(classList.size());
            Intent intent = new Intent(FindIconActivity.this, classList.get(i));
            classList.remove(i);
            Log.d("CLASSLIST ICONS", classList+"");
            intent.putExtra("activity", classList);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please return the phone", Toast.LENGTH_LONG).show();
            finish();
        }

    }


    public static class CellContent implements Serializable {

        private int drawableID;
        private String name;

        public CellContent(String nName, int nDrawableID) {
            name = nName;
            drawableID = nDrawableID;
        }

        public String getName() {
            return name;
        }

        public int getDrawableID() {
            return drawableID;
        }

        @Override
        public boolean equals(Object o) {
            // compare drawable id
            if (o.getClass().equals(this.getClass())) {
                CellContent obj = (CellContent) o;
                if (this.getDrawableID() == obj.getDrawableID()) {
                    return true;
                }
            }
            return false;
        }
    }
}
