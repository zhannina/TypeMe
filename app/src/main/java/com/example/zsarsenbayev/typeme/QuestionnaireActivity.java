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
    ArrayList<Class<?>> instructionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.questionnaire);

        //pass the remaining activity classes to intent
        classList = new ArrayList<Class<?>>();
        ArrayList test1 = getIntent().getParcelableArrayListExtra("activity");
        for(int i = 0; i < test1.size(); i++){
            classList.add((Class<?>)test1.get(i));
        }

        //Pass the remaining instruction activity classes to intent
        instructionList = new ArrayList<Class<?>>();
        ArrayList test2 = getIntent().getParcelableArrayListExtra("activity instruction");
        for(int i = 0; i < test2.size(); i++){
            instructionList.add((Class<?>)test2.get(i));
        }

        prefs = getApplicationContext().getSharedPreferences(MYPREFS, Context.MODE_PRIVATE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        deviceID = prefs.getString("device_id", "");

        //set the file head if the activity is invoked for the first time
        fileHeader();
        radioGroups();

        //The clear button will clear user selections for the radio group
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
                //If user has not select one radio group
                //send message to inform the user
                if(firstChoice == "" || secondChoice == ""){
                    Toast.makeText(QuestionnaireActivity.this,
                            "Please fill in all the selections", Toast.LENGTH_SHORT).show();
                }
                else{

                    //If user has made all the required decisions
                    // finish current activity and continue
                    date = DateFormat.getDateTimeInstance().format(new Date());
                    String questionnaire_message = deviceID + "," + firstChoice + "," +
                            secondChoice + "," + MainActivity.AppVersion;
                    mDatabase.child(deviceID).child(rootName).child(date).setValue(questionnaire_message);
                    finishActivity();
                }
            }
        });

    }


    //Set the file head if this is the first time opening this activity
    public void fileHeader(){
        if(prefs.getBoolean("FirstQuestionnaireActivity", true)){
            mDatabase.child(deviceID).child(rootName).child("Date").setValue(QUESTIONNAIRE_HEAD);
            prefs.edit().putBoolean("FirstQuestionnaireActivity", false);
            prefs.edit().commit();
        }
    }


    public void radioGroups(){
        //This radio group is about the emotion level of te user
        // 1 - "Very miserable"
        // 2 - "A little miserable"
        // 3 - "Normal"
        // 4 - "A little pleased"
        // 5 - "Very pleased"
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup1.setOnCheckedChangeListener
                ( new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(checkedId == R.id.q1_b1){
                            firstChoice = "1";
                        }
                        else if(checkedId == R.id.q1_b2){
                            firstChoice = "2";
                        }
                        else if(checkedId == R.id.q1_b3){
                            firstChoice = "3";
                        }
                        else if(checkedId == R.id.q1_b4){
                            firstChoice = "4";
                        }
                        else if(checkedId == R.id.q1_b5){
                            firstChoice = "5";
                        }
                    }
                });

        // This radio group is about the spirit level of the user
        // 1 - "Very sleepy"
        // 2 - "A little sleepy"
        // 3 - "Normal"
        // 4 - "A little aroused"
        // 5 - "Very aroused"
        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        radioGroup2.setOnCheckedChangeListener
                ( new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(checkedId == R.id.q2_b1){
                            secondChoice = "1";
                        }
                        else if(checkedId == R.id.q2_b2){
                            secondChoice = "2";
                        }
                        else if(checkedId == R.id.q2_b3){
                            secondChoice = "3";
                        }
                        else if(checkedId == R.id.q2_b4){
                            secondChoice = "4";
                        }
                        else if(checkedId == R.id.q2_b5){
                            secondChoice = "5";
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

        // The instruction list size should not be 0 here, just in case
        if(instructionList.size()!=0) {
            Random r = new Random();
            int i = r.nextInt(instructionList.size());
            //Start a random activity from the remaining instruction activity list
            Intent intent = new Intent(QuestionnaireActivity.this, instructionList.get(i));
            instructionList.remove(i);
            intent.putExtra("activity", classList);
            intent.putExtra("activity instruction", instructionList);
            intent.putExtra( "activity index", i );

            startActivity(intent);
            finish();
        }
        else{
            QuestionnaireActivity.this.finish();
        }
    }

}
