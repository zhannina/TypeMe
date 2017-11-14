package com.example.zsarsenbayev.typeme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button saveButton;
    private Button exitButton;

    private Spinner spinParticipant, spinGender, spinCondition, spinSubCondition, spinBlock;

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
                startTypingActivity();
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
//        Toast.makeText(this, "Preferences saved!", Toast.LENGTH_SHORT).show();
    }

    private void startTypingActivity() {
        SharedPreferences prefs;
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("HEADERS", false);
        editor.commit();
//        Intent i = new Intent(MainActivity.this, TypingTaskActivity.class);
        Intent i = new Intent(MainActivity.this, CirclesActivity.class);
        startActivity(i);
    }

    public void clickExit() {
        this.finish(); // terminate
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
