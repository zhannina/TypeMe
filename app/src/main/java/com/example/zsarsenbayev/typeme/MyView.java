package com.example.zsarsenbayev.typeme;

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
    public boolean drawbigcircles;

    SharedPreferences prefs;
    String participantCode, genderCode, conditionCode, blockCode;
    SharedPreferences.Editor editor;

    String hitCircle;

    int counter = 0;
    File file;
    BufferedWriter bufferedWriter;
    StringBuilder stringBuilder = new StringBuilder();
    final String HEADER = "TimeStamp,Date,Participant,Gender,Condition,Block,"
            + "Time(ms),GridTilePosition,Radius,IconCenterX,IconCenterY,TouchX,TouchY,InsideCircle\n";


    public final static String WORKING_DIRECTORY = "/HeatMapData/";

    Long startTime, endTime, diff;


    public MyView(Context context) {
        super(context);

        prefs = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        editor = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE).edit();
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        prefs = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        editor = context.getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE).edit();
        //prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
        drawbigcircles = false;

        participantCode = prefs.getString("participantCode", "");
        genderCode = prefs.getString("genderCode", "");
        conditionCode = prefs.getString("conditionCode", "");
        blockCode = prefs.getString("blockCode", "");

        classList = new ArrayList<Class<?>>();
        ArrayList test = ((Activity)getContext()).getIntent().getParcelableArrayListExtra("activity");
        for(int i = 0; i < test.size(); i++){
            classList.add((Class<?>)test.get(i));
        }

        File dataDirectory = new File(Environment.getExternalStorageDirectory() +
                WORKING_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            Log.e("MYDEBUG", "Error creating data directory! Exception: " + WORKING_DIRECTORY);
            finishActivity();
        }

        String base = "HeatMap-" + participantCode  + "-" +
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
            Log.e("MYDEBUG", "Error opening data files! Exception: " + e.toString());
            Log.e("MYDEBUG", "Error opening data files! Exception: " + e.getStackTrace());
            finishActivity();
        }

        counter = 0;
        // access time
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
                //Check if the point press is within the circle
                if (contains(event, points.get(pointsPos))) {
                    hitCircle = "True";
                    Long tsLong = System.currentTimeMillis();
                    String ts = tsLong.toString();
                    String date = DateFormat.getDateTimeInstance().format(new Date());

                    endTime=System.currentTimeMillis();

                    diff = endTime-startTime;
                    Log.d("time", ""+diff);

                    stringBuilder.append(String.format("%s,%s,%s,%s,%s,%s,%s,%d,%f,%d,%d,%f,%f,%s\n", ts, date, participantCode,
                            genderCode, conditionCode, blockCode, diff.toString(), pointsPos,
                            radius,
                            points.get(pointsPos).x, (points.get(pointsPos).y), touchX, touchY, hitCircle));
                    try {
                        bufferedWriter.write(stringBuilder.toString(), 0, stringBuilder.length());
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        Log.e("MYDEBUG", "ERROR WRITING TO DATA FILES: e = " + e);
                    }
                    stringBuilder.delete(0, stringBuilder.length());

                    if (positions.size() == points.size()) {
                        if (!drawbigcircles) {
                            drawbigcircles = true;
                            populateBigCirclesArrayList();
                        } else {
                            try {
                                bufferedWriter.close();
                            } catch (IOException e) {
                                Log.e("MYDEBUG", "ERROR CLOSING THE DATA FILES: e = " + e);
                            }
                            finishActivity();
                        }
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

                    stringBuilder.append(String.format("%s,%s,%s,%s,%s,%s,%s,%d,%f,%d,%d,%f,%f,%s\n", ts, date, participantCode,
                            genderCode, conditionCode, blockCode, diff.toString(), pointsPos,
                            radius,
                            points.get(pointsPos).x, (points.get(pointsPos).y), touchX, touchY, hitCircle));
                    try {
                        bufferedWriter.write(stringBuilder.toString(), 0, stringBuilder.length());
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        Log.e("MYDEBUG", "ERROR WRITING TO DATA FILES: e = " + e);
                    }
                    stringBuilder.delete(0, stringBuilder.length());
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

    public void populateBigCirclesArrayList(){

        width = prefs.getFloat("maxX", 0);
        height = prefs.getFloat("maxY", 0);
        radius = width/4;
        points.clear();
        positions.clear();


        points.add(new Point((int) (width / 4), (int) (height / 6)));
        points.add(new Point((int) (2 * width / 4), (int) (height / 6)));
        points.add(new Point((int) (3 * width / 4), (int) (height / 6)));

        points.add(new Point((int) (width / 4), (int) (2*height / 6)));
        points.add(new Point((int) (2 * width / 4), (int) (2*height / 6)));
        points.add(new Point((int) (3 * width / 4), (int) (2*height / 6)));

        points.add(new Point((int) (width / 4), (int) (3*height / 6)));
        points.add(new Point((int) (2 * width / 4), (int) (3*height / 6)));
        points.add(new Point((int) (3 * width / 4), (int) (3*height / 6)));

        points.add(new Point((int) (width / 4), (int) (4*height / 6)));
        points.add(new Point((int) (2 * width / 4), (int) (4*height / 6)));
        points.add(new Point((int) (3 * width / 4), (int) (4*height / 6)));

        points.add(new Point((int) (width / 4), (int) (5*height / 6)));
        points.add(new Point((int) (2 * width / 4), (int) (5*height / 6)));
        points.add(new Point((int) (3 * width / 4), (int) (5*height / 6)));

        Random r1 = new Random(System.nanoTime());
        pointsPos = r1.nextInt(points.size()); //between 0 and points.length
        positions.add(pointsPos);
        startTime = System.currentTimeMillis();

    }

    private void finishActivity() {

        if(classList.size()!=0) {
            Random r = new Random();
            int i = r.nextInt(classList.size());
            Intent intent  = new Intent(this.getContext(), classList.get(i));
            classList.remove(i);

            intent.putExtra("activity", classList);
            getContext().startActivity(intent);
        }else{
//            Toast.makeText(this.getContext(), "List Empty", Toast.LENGTH_SHORT).show();
            ((Activity)this.getContext()).finish();
        }
    }

}
