package com.example.zsarsenbayev.typeme.findIconActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.zsarsenbayev.typeme.MainActivity;
import com.example.zsarsenbayev.typeme.R;
import com.example.zsarsenbayev.typeme.findIconActivity.FindIconActivity;
import com.example.zsarsenbayev.typeme.findIconActivity.GridViewCustomAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DisplayGridActivity extends AppCompatActivity {

    final String HEADER = "TimeStamp,deviceID,"
            + "Time(ms),TimeToRemember(ms)," +
            "ActualGridPosition,SelectedGridPosition,PassedDrawableID,PassedIconName,StartViewTouchX,StartViewTouchY,ViewCenterX,ViewCenterY,TouchX,TouchY," +
            "IconCenterX,IconCenterY,TextCenterX,TextCenterY," +
            "WrongHitsCount,CorrectHit,AppVersion";
    public static final String MyPREFERENCES = "MyPrefs";

    int counter = 0;

    SharedPreferences prefs;
    String deviceID;

    GridView gridView;
    GridViewCustomAdapter gridViewCustomAdapter;

    FindIconActivity.CellContent passedIcon;
    ArrayList<FindIconActivity.CellContent> iconsCopy = new ArrayList<FindIconActivity.CellContent>(FindIconActivity.icons);
    int passedPosition;

    Long startTime, endTime, diff, timeToRemember;

    ArrayList<String> errorRows;
    private DatabaseReference mDatabase;
    private String rootName = "FindIcon Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView( R.layout.activity_display_grid);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            passedIcon = (FindIconActivity.CellContent) extras.getSerializable("iconToFind");
            passedPosition = extras.getInt("positionToPlace");
            timeToRemember = extras.getLong("timeToRemember");
        }

        startTime = System.currentTimeMillis();
        errorRows = new ArrayList<>();

        prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        deviceID = prefs.getString("device_id", "");

        gridView = (GridView) findViewById(R.id.gridViewCustom);
        // Create the Custom Adapter Object
        gridViewCustomAdapter = new GridViewCustomAdapter(this, FindIconActivity.icons, passedPosition, passedIcon);
        // Set the Adapter to GridView
        gridView.setAdapter(gridViewCustomAdapter);


        if(prefs.getBoolean("FirstIconTask", true)){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(deviceID).child(rootName).child("FindIconData").child("Date").setValue(HEADER);
            prefs.edit().putBoolean("FirstIconTask", false);
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

                //stringBuilder = new StringBuilder();
                if (passedPosition == position) {

                    String correctIcon = ts + ", " + deviceID + ", " + diff.toString() + ", " +
                            timeToRemember.toString() + ", " + passedPosition + ", " + position + ", "
                            + passedIcon.getDrawableID() + ", " + passedIcon.getName() + ", " +
                            viewStartX + ", " + viewStartY + ", " + viewCenterX + ", " +
                            viewCenterY + ", " + touchX + ", " + touchY + ", " + iconCenterX + ", " +
                            iconCenterY + ", " + textCenterX + ", " + textCenterY + ", " + counter + " true" + MainActivity.AppVersion;

                    mDatabase.child(deviceID).child(rootName).child("FindIconData").child(date).setValue(correctIcon);

                    finish();
                } else {
                    counter++;
                    String wrongIcon = ts + ", " + deviceID + ", " + diff.toString() + ", " +
                            timeToRemember.toString() + ", " + passedPosition + ", " + position + ", "
                            + passedIcon.getDrawableID() + ", " + passedIcon.getName() + ", " +
                            viewStartX + ", " + viewStartY + ", " + viewCenterX + ", " +
                            viewCenterY + ", " + touchX + ", " + touchY + ", " + iconCenterX + ", " +
                            iconCenterY + ", " + textCenterX + ", " + textCenterY + ", " + counter + " false" + MainActivity.AppVersion;

                    mDatabase.child(deviceID).child(rootName).child("FindIconData").child(date).setValue(wrongIcon);

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please do not press the back button", Toast.LENGTH_SHORT).show();
    }


}
