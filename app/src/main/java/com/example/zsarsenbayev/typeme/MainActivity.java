package com.example.zsarsenbayev.typeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button saveButton;
    private Button exitButton;

    public static ArrayList<Integer> randomsArray;// = new ArrayList<>(Arrays.asList(0, 1, 2));
    public static ArrayList<Class<?>> activities;
    public static ArrayList<Class> shuffledActivities = new ArrayList<>();


    private Spinner spinParticipant, spinGender, spinCondition, spinBlock;
    int randomNumber;

    private String[] participantCode = {"P01", "P02", "P03", "P04", "P05", "P06", "P07", "P08",
          "P09", "P10", "P11", "P12", "P13", "P14", "P15", "P16", "P17", "P18", "P19", "P20",
          "P21", "P22", "P23", "P24", "P25", "P26", "P27", "P28", "P29", "P30", "P99"};
    private String[] genderCode = {"M", "F"};
    private String[] conditionCode = {"Music-Fast", "Music-Slow", "Urban-Indoor", "Urban-Outdoor", "Speech-English", "Speech-Foreign", "Silent", "Training"};
    private String[] blockCode = {"B01"};

    SharedPreferences sharedPrefs;

    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.startButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        spinParticipant = (Spinner) findViewById(R.id.paramParticipant);
        spinGender = (Spinner) findViewById(R.id.paramGender);
        spinCondition = (Spinner) findViewById(R.id.paramCondition);
        spinBlock = (Spinner) findViewById(R.id.paramBlock);

        activities = new ArrayList<Class<?>>();

        activities.add(CirclesActivity.class);
        activities.add(FindIconActivity.class);
        activities.add(TypingTaskActivity.class);


        sharedPrefs = this.getPreferences(MODE_PRIVATE);
        participantCode[0] = sharedPrefs.getString("participantCode", participantCode[0]);
        genderCode[0] = sharedPrefs.getString("genderCode", genderCode[0]);
        conditionCode[0] = sharedPrefs.getString("conditionCode", conditionCode[0]);
        blockCode[0] = sharedPrefs.getString("blockCode", blockCode[0]);

        ArrayAdapter<CharSequence> adapterPC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                participantCode);
        spinParticipant.setAdapter(adapterPC);

        ArrayAdapter<CharSequence> adapterG = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, genderCode);
        spinGender.setAdapter(adapterG);

        ArrayAdapter<CharSequence> adapterC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, conditionCode);
        spinCondition.setAdapter(adapterC);

        ArrayAdapter<CharSequence> adapterB = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, blockCode);
        spinBlock.setAdapter(adapterB);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSave();
                startMyActivity();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSave();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickExit();
            }
        });
    }

    public void clickSave() {
        SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        editor.putString("participantCode", participantCode[spinParticipant.getSelectedItemPosition()]);
        editor.putString("genderCode", genderCode[spinGender.getSelectedItemPosition()]);
        editor.putString("conditionCode", conditionCode[spinCondition.getSelectedItemPosition()]);
        editor.putString("blockCode", blockCode[spinBlock.getSelectedItemPosition()]);
        editor.commit();
    }

    private void startMyActivity() {

        Random r = new Random();
        randomNumber = r.nextInt(activities.size());
        Intent intent  = new Intent(MainActivity.this, activities.get(randomNumber));
        activities.remove(randomNumber);
        intent.putExtra("activity", activities);
        startActivity(intent);
    }

    public void clickExit() {
        this.finish(); // terminate
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
