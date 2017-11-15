package com.example.zsarsenbayev.typeme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class DisplayGridActivity extends AppCompatActivity {

    public static final String WORKING_DIRECTORY = "/FindWaldoData/";
    final String HEADER = "TimeStamp,Date,Participant,Gender,Condition,Block,"
            + "Time(ms),TimeToRemember(ms)," +
            "ActualGridPosition,SelectedGridPosition,PassedDrawableID,PassedIconName,StartViewTouchX,StartViewTouchY,ViewCenterX,ViewCenterY,TouchX,TouchY," +
            "IconCenterX,IconCenterY,TextCenterX,TextCenterY," +
            "WrongHitsCount,CorrectHit\n";
    public static final String MyPREFERENCES = "MyPrefs";

    int counter = 0;
    File file;
    BufferedWriter bufferedWriter;
    StringBuilder stringBuilder;


    SharedPreferences prefs;
    String participantCode, genderCode, conditionCode, blockCode;

    GridView gridView;
    GridViewCustomAdapter gridViewCustomAdapter;

    ArrayList<Class<?>> classList;

    FindIconActivity.CellContent passedIcon;
    ArrayList<FindIconActivity.CellContent> iconsCopy = new ArrayList<FindIconActivity.CellContent>(FindIconActivity.icons);
    int passedPosition;

    Long startTime, endTime, diff, timeToRemember;

    ArrayList<String> errorRows;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classList = new ArrayList<Class<?>>();
        ArrayList test = getIntent().getParcelableArrayListExtra("activity");

        for(int i = 0; i < test.size(); i++){
            classList.add((Class<?>)test.get(i));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_display_grid);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            passedIcon = (FindIconActivity.CellContent) extras.getSerializable("iconToFind");
            passedPosition = extras.getInt("positionToPlace");
            timeToRemember = extras.getLong("timeToRemember");
        }

        startTime = System.currentTimeMillis();
        errorRows = new ArrayList<>();

        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        participantCode = prefs.getString("participantCode", "");
        genderCode = prefs.getString("genderCode", "");
        conditionCode = prefs.getString("conditionCode", "");
        blockCode = prefs.getString("blockCode", "");


        gridView = (GridView) findViewById(R.id.gridViewCustom);
        // Create the Custom Adapter Object
        gridViewCustomAdapter = new GridViewCustomAdapter(this, FindIconActivity.icons, passedPosition, passedIcon);
        // Set the Adapter to GridView
        gridView.setAdapter(gridViewCustomAdapter);

        File dataDirectory = new File(Environment.getExternalStorageDirectory() +
                WORKING_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            Log.e("MYDEBUG", "Failed to create directory: " + WORKING_DIRECTORY);
            Toast.makeText(this, "Couldn't create directory", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }

        String base = "FindWaldo-" + participantCode + "-" +
                genderCode + "-" + conditionCode;

        file = new File(dataDirectory, base + ".csv");

        try {

            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            if (!prefs.getBoolean("HEADERS", false) && blockCode.equals("B01")) {
                bufferedWriter.append(HEADER, 0, HEADER.length());
                bufferedWriter.flush();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("HEADERS", true);
                editor.commit();
            }

        } catch (IOException e) {
            Log.i("MYDEBUG", "Error opening data files! Exception: " + e.toString());
            System.exit(0);
        }

        gridView.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            // check that the even onUP (when the release the finger)
                                            //
                                            switch (event.getAction()) {
                                                case MotionEvent.ACTION_DOWN:
                                                    Log.d("DOWN", "DOWN");
                                                    break;
                                                case MotionEvent.ACTION_MOVE:

                                                    break;

                                                case MotionEvent.ACTION_UP:
                                                    Log.d("UP", "UP");
                                                    //record x, y
                                                    float x = event.getX();
                                                    float y = event.getY();
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.putFloat("TouchX", x);
                                                    editor.putFloat("TouchY", y);
                                                    editor.commit();

                                                    // log them and see if the values are the same if hit the center
                                                    Log.d("TouchX", "" + x);
                                                    Log.d("TouchY", "" + y);
                                                    break;
                                            }
                                            return false;
                                        }
                                    }

        );


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FindIconActivity.CellContent selected = iconsCopy.get(position); // iconsCopy[position] is another icon, should just remove selected item from here

                // get shared prefs: x and y
                float touchX = prefs.getFloat("TouchX", 0);
                float touchY = prefs.getFloat("TouchY", 0);
                // coordinates of the iconview
                float viewStartX = view.getX();
                float viewStartY = view.getY();
                // get center of the view (cell of grid)
                float viewCenterX = view.getWidth() / 2 + viewStartX;
                float viewCenterY = view.getHeight() / 2 + viewStartY;


                float iconCenterX = view.findViewById(R.id.imageView).getX() + view.findViewById(R.id.imageView).getWidth() / 2 + viewStartX;
                float iconCenterY = view.findViewById(R.id.imageView).getY() + view.findViewById(R.id.imageView).getHeight() / 2 + viewStartY;

                float textCenterX = view.findViewById(R.id.textView).getX() + view.findViewById(R.id.textView).getWidth() / 2 + viewStartX;
                float textCenterY = view.findViewById(R.id.textView).getY() + view.findViewById(R.id.textView).getHeight() / 2 + viewStartY;



                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                String date = DateFormat.getDateTimeInstance().format(new Date());
                endTime = System.currentTimeMillis();
                diff = endTime - startTime;

                stringBuilder = new StringBuilder();
                if (passedPosition == position) {

                    for (int i = 0; i< errorRows.size(); i++) {
                        String tempRow = errorRows.get(i);

                        tempRow = tempRow.replace("#1",String.valueOf(viewStartX));
                        tempRow = tempRow.replace("#2",String.valueOf(viewStartY));
                        tempRow = tempRow.replace("#3",String.valueOf(viewCenterX));
                        tempRow = tempRow.replace("#4",String.valueOf(viewCenterY));
                        tempRow = tempRow.replace("#5",String.valueOf(iconCenterX));
                        tempRow = tempRow.replace("#6",String.valueOf(iconCenterY));
                        tempRow = tempRow.replace("#7",String.valueOf(textCenterX));
                        tempRow = tempRow.replace("#8",String.valueOf(textCenterY));
                        try {
                            bufferedWriter.write(tempRow, 0, tempRow.length());
                            bufferedWriter.flush();
                        } catch (IOException e) {
                            Log.e("MYDEBUG", "ERROR WRITING TO DATA FILES: e = " + e);
                        }
                    }

                    errorRows.clear();

                    // StartViewTouchX,StartViewTouchY,IconCenterX,IconCenterY,TouchX,TouchY
                    stringBuilder.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%d,true\n", ts, date, participantCode,
                            genderCode, conditionCode, blockCode, diff.toString(),timeToRemember.toString(), passedPosition, position, passedIcon.getDrawableID(),
                            passedIcon.getName(),
                            viewStartX, viewStartY, viewCenterX, viewCenterY, touchX, touchY,
                            iconCenterX, iconCenterY, textCenterX, textCenterY,
                            counter));
                    try {
                        bufferedWriter.write(stringBuilder.toString(), 0, stringBuilder.length());
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        Log.e("MYDEBUG", "ERROR WRITING TO DATA FILES: e = " + e);
                    }
                    stringBuilder.delete(0, stringBuilder.length());
                    finishActivity();
                } else {
                    counter++;
                    stringBuilder.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%s,%s,#1,#2,#3,#4,%f,%f,#5,#6,#7,#8,%d,false\n", ts, date, participantCode,
                            genderCode, conditionCode, blockCode, diff.toString(),timeToRemember.toString(), passedPosition, position, passedIcon.getDrawableID(),
                            passedIcon.getName(), touchX, touchY,counter));
                    errorRows.add(stringBuilder.toString());
                    stringBuilder.delete(0, stringBuilder.length());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            Log.e("MYDEBUG", "ERROR CLOSING THE DATA FILES: e = " + e);
        }
        finish();
    }

    public void finishActivity(){

        if(classList.size()!=0) {
            Random r = new Random();
            int i = r.nextInt(classList.size());
            Intent intent = new Intent(DisplayGridActivity.this, classList.get(i));
            classList.remove(i);

            intent.putExtra("activity", classList);
            startActivity(intent);
        }else{
            finish();
        }

    }
}
