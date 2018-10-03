package com.example.zsarsenbayev.typeme.circleActivity;

/**
 * Created by zsarsenbayev on 11/14/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.zsarsenbayev.typeme.MainActivity;
import com.example.zsarsenbayev.typeme.sensorData.AccelerometerSensor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * TODO: document your custom view class.
 */
public class MyView extends View {

    Paint paint, paint1;
    ArrayList<Point> points = new ArrayList<>();
    private int pointsPos; //Which point we will be drawing

    ArrayList<Class<?>> classList;

    public float radius;
    public float width;
    public float height;
    public HashSet<Integer> positions = new HashSet<>();

    SharedPreferences prefs;
    String deviceID;
    SharedPreferences.Editor editor;

    String hitCircle;

    int counter = 0;
    final String HEADER = "TimeStamp, deviceID, "
            + "Time(ms), GridTilePosition, Radius, IconCenterX, IconCenterY, TouchX, TouchY, InsideCircle, AppVersion";

    Long startTime, endTime, diff;

    private DatabaseReference mDatabase;
    private String rootName = "Circle Activity";


    public MyView(Context context) {
        super(context);
        prefs = context.getSharedPreferences( MainActivity.MyPREFERENCES, MODE_PRIVATE);
        editor = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE).edit();
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        editor = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE).edit();
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        prefs = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        editor = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE).edit();
        init();
    }

    private void init() {
        // Load attributes
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);

        paint1 = new Paint();
        paint1.setColor(Color.BLUE);
        paint1.setStrokeWidth(9);
        paint1.setStyle(Paint.Style.STROKE);

        populateSmallCirclesArrayList();
        positions.clear();

        deviceID = prefs.getString("device_id", "");

        classList = new ArrayList<Class<?>>();
        ArrayList test = ((Activity)getContext()).getIntent().getParcelableArrayListExtra("activity");
        for(int i = 0; i < test.size(); i++){
            classList.add((Class<?>)test.get(i));
        }

        if(prefs.getBoolean("FirstCircleTask", true)){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(deviceID).child(rootName).child("CircleData").child("Date").setValue(HEADER);
            prefs.edit().putBoolean("FirstCircleTask", false);
        }

        counter = 0;
        startTime = System.currentTimeMillis();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint1.setStyle(Paint.Style.STROKE);

        canvas.drawColor(Color.WHITE);

        canvas.drawCircle(points.get(pointsPos).x, points.get(pointsPos).y, radius, paint);
        canvas.drawPoint(points.get(pointsPos).x, points.get(pointsPos).y, paint1);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX, touchY;
        touchX = event.getX();
        touchY = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                if (contains(event, points.get(pointsPos))) {
                    hitCircle = "True";
                    Long tsLong = System.currentTimeMillis();
                    String ts = tsLong.toString();
                    String date = DateFormat.getDateTimeInstance().format(new Date());

                    endTime=System.currentTimeMillis();

                    diff = endTime-startTime;
                    Log.d("time", "" + diff);

                    String circle_hit = ts + ", " + deviceID + ", " + diff.toString() + ", " + pointsPos + ", "
                            + radius + ", " + points.get(pointsPos).x + ", " + points.get(pointsPos).y +
                            ", " + touchX + ", " + touchY + ", " + hitCircle + ", " + MainActivity.AppVersion;
                    mDatabase.child(deviceID).child(rootName).child("CircleData").child(date).setValue(circle_hit);

                    if (positions.size() == points.size()) {
                        finishActivity();
                    } else {
                        Random r = new Random(System.nanoTime());
                        pointsPos = r.nextInt(points.size()); //between 0 and points.length
                        positions.add(pointsPos);
                        startTime = System.currentTimeMillis();
                        postInvalidate();
                    }
                } else {
                    hitCircle = "False";
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    String date = DateFormat.getDateTimeInstance().format(new Date());

                    diff = endTime - startTime;

                    String circle_hit = ts + ", " + deviceID + ", " + diff.toString() + ", " + pointsPos + ", "
                            + radius + ", " + points.get(pointsPos).x + ", " + points.get(pointsPos).y +
                            ", " + touchX + ", " + touchY + ", " + hitCircle + ", " + MainActivity.AppVersion;
                    mDatabase.child(deviceID).child(rootName).child("CircleData").child(date).setValue(circle_hit);

                }
        }

        postInvalidate();
        return true;

    }


    private boolean contains(MotionEvent event, Point point) {
        float xTouch = event.getX();
        float yTouch = event.getY();
        if ((xTouch - point.x) * (xTouch - point.x) + (yTouch - point.y) * (yTouch - point.y) <= radius * radius) {
            return true;
        }
        else {
            return false;
        }
    }

    public void populateSmallCirclesArrayList(){

        width = prefs.getFloat("maxX", 0);
        height = prefs.getFloat("maxY", 0);
        radius = width/8;
        Log.d("screen",prefs.getFloat("maxX", 0)+"");
        Log.d("screen", "x: " + width + " y: " + height);
        points.clear();
        points.add(new Point((int) (width / 8), (int) (height / 12)));
        points.add(new Point((int) (3 * width / 8), (int) (height / 12)));
        points.add(new Point((int) (5 * width/8), (int) (height/12)));
        points.add(new Point((int) (7 * width / 8), (int) (height / 12)));

        points.add(new Point((int) (width/8), (int) (3*height/12)));
        points.add(new Point((int) (3*width/8), (int) (3*height/12)));
        points.add(new Point((int) (5 * width/8), (int) (3*height/12)));
        points.add(new Point((int) (7*width/8), (int) (3*height/12)));

        points.add(new Point((int) (width/8), (int) (5*height/12)));
        points.add(new Point((int) (3*width/8), (int) (5*height/12)));
        points.add(new Point((int) (5 * width/8), (int) (5*height/12)));
        points.add(new Point((int) (7*width/8), (int) (5*height/12)));

        points.add(new Point((int) (width/8), (int) (7*height/12)));
        points.add(new Point((int) (3*width/8), (int) (7*height/12)));
        points.add(new Point((int) (5 * width/8), (int) (7*height/12)));
        points.add(new Point((int) (7*width/8), (int) (7*height/12)));

        points.add(new Point((int) (width/8), (int) (9*height/12)));
        points.add(new Point((int) (3*width/8), (int) (9*height/12)));
        points.add(new Point((int) (5 * width/8), (int) (9*height/12)));
        points.add(new Point((int) (7*width/8), (int) (9*height/12)));

        points.add(new Point((int) (width/8), (int) (11*height/12)));
        points.add(new Point((int) (3*width/8), (int) (11*height/12)));
        points.add(new Point((int) (5 * width/8), (int) (11*height/12)));
        points.add(new Point((int) (7*width/8), (int) (11*height/12)));

        Random r1 = new Random(System.nanoTime());
        pointsPos = r1.nextInt(points.size()); //between 0 and points.length
        positions.add(pointsPos);

    }

    private void finishActivity() {

        if(classList.size()!=0) {
            stopServices();
            Random r = new Random();
            int i = r.nextInt(classList.size());
            Intent intent  = new Intent(this.getContext(), classList.get(i));
            classList.remove(i);
            Log.d("CLASSLIST CIRCLES", classList+"");
            intent.putExtra("activity", classList);
            getContext().startActivity(intent);
            ((Activity)this.getContext()).finish();
        }else{
            stopServices();
            ((Activity)this.getContext()).finish();
        }
    }



    public void stopServices(){

        getContext().stopService( CirclesActivity.accelerometerIntent );
        getContext().stopService( CirclesActivity.batteryIntent );
        getContext().stopService( CirclesActivity.lightIntent );
        getContext().stopService( CirclesActivity.networkIntent );

//        ((Activity) this.getContext()).

    }

}
