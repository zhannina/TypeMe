package com.example.zsarsenbayev.typeme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.zsarsenbayev.typeme.typingActivity.TypingTaskActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class QuestionnaireActivity extends AppCompatActivity {

    public static final String MYPREFS = "MyPrefs";
    private SharedPreferences prefs;
    private DatabaseReference mDatabase;
    private String rootName = "Questionnaire";
    private String QUESTIONNAIRE_HEAD = "deviceID,EmotionLevel,SpiritLevel,AppVersion";
    private String deviceID;
    private String date;

    private RadioGroup radioGroup1, radioGroup2;
    private Button clearButton, nextButton;

    private String firstChoice = "";
    private String secondChoice = "";

    ArrayList<Class<?>> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.questionnaire);

        classList = new ArrayList<Class<?>>();
        ArrayList test = getIntent().getParcelableArrayListExtra("activity");
        for(int i = 0; i < test.size(); i++){
            classList.add((Class<?>)test.get(i));
        }

        prefs = getApplicationContext().getSharedPreferences(MYPREFS, Context.MODE_PRIVATE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        deviceID = prefs.getString("device_id", "");

        fileHeader();
        radioGroups();

        clearButton = (Button) findViewById(R.id.clear_selection);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup1.clearCheck();
                radioGroup2.clearCheck();
            }
        });


        nextButton = (Button) findViewById(R.id.questionnaire_next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstChoice == "" || secondChoice == ""){
                    Toast.makeText(QuestionnaireActivity.this,
                            "Please fill in all the selections", Toast.LENGTH_SHORT).show();
                }
                else{

                    date = DateFormat.getDateTimeInstance().format(new Date());
                    String questionnaire_message = deviceID + "," + firstChoice + "," +
                            secondChoice + "," + MainActivity.AppVersion;
                    mDatabase.child(deviceID).child(rootName).child(date).setValue(questionnaire_message);
//                    finish();
                    finishActivity();

//                    finishActivity();
                }
            }
        });

    }


    public void fileHeader(){
        if(prefs.getBoolean("FirstQuestionnaireActivity", true)){
            mDatabase.child(deviceID).child(rootName).child("Date").setValue(QUESTIONNAIRE_HEAD);
            prefs.edit().putBoolean("FirstQuestionnaireActivity", false);
            prefs.edit().commit();
        }
    }


    public void radioGroups(){
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);

        radioGroup1.setOnCheckedChangeListener
                ( new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(checkedId == R.id.q1_b1){
                            firstChoice = "Very miserable";
                        }
                        else if(checkedId == R.id.q1_b2){
                            firstChoice = "A little miserable";
                        }
                        else if(checkedId == R.id.q1_b3){
                            firstChoice = "Normal";
                        }
                        else if(checkedId == R.id.q1_b4){
                            firstChoice = "A little pleased";
                        }
                        else if(checkedId == R.id.q1_b5){
                            firstChoice = "Very pleased";
                        }
                    }
                });

        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);

        radioGroup2.setOnCheckedChangeListener
                ( new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(checkedId == R.id.q2_b1){
                            secondChoice = "Very sleepy";
                        }
                        else if(checkedId == R.id.q2_b2){
                            secondChoice = "A little sleepy";
                        }
                        else if(checkedId == R.id.q2_b3){
                            secondChoice = "Normal";
                        }
                        else if(checkedId == R.id.q2_b4){
                            secondChoice = "A little aroused";
                        }
                        else if(checkedId == R.id.q2_b5){
                            secondChoice = "Very aroused";
                        }
                    }
                });
    }


    @Override
    protected void onResume(){
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please do not press the back button", Toast.LENGTH_SHORT).show();
    }

    public void finishActivity(){

        if(classList.size()!=0) {
            Random r = new Random();
            int i = r.nextInt(classList.size());
            Intent intent = new Intent(QuestionnaireActivity.this, classList.get(i));
            classList.remove(i);
            intent.putExtra("activity", classList);
            startActivity(intent);
            finish();
        }
        else{
            QuestionnaireActivity.this.finish();
        }
    }

}
