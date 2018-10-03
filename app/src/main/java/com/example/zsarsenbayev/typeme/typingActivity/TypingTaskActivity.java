package com.example.zsarsenbayev.typeme.typingActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zsarsenbayev.typeme.MainActivity;
import com.example.zsarsenbayev.typeme.R;
import com.example.zsarsenbayev.typeme.sensorData.AccelerometerSensor;
import com.example.zsarsenbayev.typeme.sensorData.BatterySensor;
import com.example.zsarsenbayev.typeme.sensorData.LightSensor;
import com.example.zsarsenbayev.typeme.sensorData.NetworkSensor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.StringJoiner;

public class TypingTaskActivity extends AppCompatActivity {

    private TextView messageTextView;
    private Button submitButton;
    private EditText userInputEditText;

    private int mode = 1;
    private int count = 0;
    private int backSpaceCount = 0;
    private String displayedSentence = "";
    private long timeMillis = 0;
    private String difficulty = "";
    private String date;

    private long startTimeStamp;
    private String startTimeStampStr = "";
    private long endTimeStamp;
    private String endTimeStampStr = "";

    private String userInputString;

    private String completeOldText;
    private String completeNewText;

    private ArrayList<String> oldTexts;
    private ArrayList<String> newTexts;
    private ArrayList<String> correctedLetters;
    private ArrayList<String> tempSimpleSentences;
    private ArrayList<String> tempDifficSentences;

    private String oldTextsString;
    private String newTextsString;
    private String userInputStr;
    private String correctedLettersString;
    private String textViewString;
    private String rootName = "Typing Activity";
    private Intent accelerometerIntent;
    private Intent batteryIntent;
    private Intent lightIntent;
    private Intent networkIntent;

    private int numberOfTotalEnteredLetters = 0;

    private final String ACCELEROMETER_HEADER = "DeviceID,Acceleration_X,Acceleration_Y,Acceleration_Z,AppVersion";
    private final String BATTERY_HEADER = "DeviceID,BatteryTemp,AppVersion";
    private final String LIGHT_HEADER = "DeviceID,LightLuminance,AppVersion";
    private final String NETWORK_HEADER = "deviceID,NetworkState,NetworkType,AppVersion";

    ArrayList<Class<?>> classList;

    //public static final String WORKING_DIRECTORY = "/TypeMeData/";
    final String HEADER = "deviceID," +
            "StartTimeStamp,EndTimeStamp,TimeToType(ms),DisplayedMessage,BackspaceCount,NumberOfTotalEnteredKeyStrokes,Difficulty,UserInputMessage,TextBeforeChange,TextAfterChange,CorrectedLettersArray,AppVersion";
    SharedPreferences prefs;
    String deviceID;

    InputMethodManager imm;

    private DatabaseReference mDatabase;

    private ArrayList<String> simpleSentences = new ArrayList<String>(Arrays.asList(
            "Joe went to the store.",
            "Sarah and Jessie are going swimming.",
            "The frog jumped and landed in the pond.",
            "Can I have some juice to drink?",
            "The pizza smells delicious.",
            "There is a fly in the car with us.",
            "Look on top of the refrigerator for the key.",
            "I am out of paper for the printer.",
            "Will you help me with the math homework?",
            "The music is too loud for my ears."));

    private ArrayList<String> difficultSentences = new ArrayList<String>(Arrays.asList(
            "Since saucy jacks so happy are in this Give them thy fingers me thy lips to kiss.",
            "Make thee another self for love of me That beauty still may live in thine or thee.",
            "Or else of thee this I prognosticate Thy end is truth's and beauty's doom and date.",
            "Yet do thy worst old Time despite thy wrong My love shall in my verse ever live young.",
            "O learn to read what silent love hath writ To hear with eyes belongs to love's fine wit.",
            "But day doth daily draw my sorrows longer And night doth nightly make grief's strength seem stronger.",
            "Yet him for this my love no whit disdaineth Suns of the world may stain when heaven's sun staineth.",
            "Ah but those tears are pearl which thy love sheds And they are rich and ransom all ill deeds.",
            "But why thy odour matcheth not thy show The solve is this that thou dost common grow.",
            "And thou in this shalt find thy monument When tyrants' crests and tombs of brass are spent."));


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        classList = new ArrayList<Class<?>>();
        ArrayList test = getIntent().getParcelableArrayListExtra("activity");
        for(int i = 0; i < test.size(); i++){
            classList.add((Class<?>)test.get(i));
        }

        fileHeader();
        accelerometerIntent = new Intent(TypingTaskActivity.this, AccelerometerSensor.class);
        accelerometerIntent.putExtra( "rootName", rootName );

        batteryIntent = new Intent(TypingTaskActivity.this, BatterySensor.class);
        batteryIntent.putExtra( "rootName", rootName );

        lightIntent = new Intent( TypingTaskActivity.this, LightSensor.class );
        lightIntent.putExtra( "rootName", rootName );

        networkIntent = new Intent( TypingTaskActivity.this, NetworkSensor.class );
        networkIntent.putExtra( "rootName", rootName );

        startService(accelerometerIntent);
        startService(batteryIntent);
        startService(lightIntent);
        startService(networkIntent);

    }

    public void fileHeader(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        deviceID = prefs.getString("device_id", "");

        if(prefs.getBoolean("FirstTypingAccelerometerService", true)){
            mDatabase.child(deviceID).child(rootName).child("Accelerometer").child("Date").setValue(ACCELEROMETER_HEADER);
            prefs.edit().putBoolean("FirstTypingAccelerometerService", false);
            prefs.edit().commit();
        }

        if(prefs.getBoolean("FirstTypingBatteryService", true)){
            mDatabase.child(deviceID).child(rootName).child("Battery").child("Date").setValue(BATTERY_HEADER);
            prefs.edit().putBoolean("FirstTypingBatteryService", false);
            prefs.edit().commit();
        }

        if(prefs.getBoolean("FirstTypingLightService", true)){
            mDatabase.child(deviceID).child(rootName).child("Light").child("Date").setValue(LIGHT_HEADER);
            prefs.edit().putBoolean("FirstTypingLightService", false);
            prefs.edit().commit();
        }

        if(prefs.getBoolean("FirstTypingNetworkService", true)){
            mDatabase.child(deviceID).child(rootName).child( "Network" ).child("Date").setValue(NETWORK_HEADER);
            prefs.edit().putBoolean("FirstTypingNetworkService", false);
            prefs.edit().commit();
        }



    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView( R.layout.activity_typing_task);
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        submitButton = (Button) findViewById(R.id.submitButton);
        userInputEditText = (EditText) findViewById(R.id.userInputEditText);
        prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        deviceID = prefs.getString("device_id", "");
        imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);

        oldTexts = new ArrayList<>();
        newTexts = new ArrayList<>();
        correctedLetters = new ArrayList<>();

        tempSimpleSentences = new ArrayList<>();
        tempDifficSentences = new ArrayList<>();

        if(prefs.getBoolean("FirstTypingTask", true)){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(deviceID).child(rootName).child("TypingData").child("Date").setValue(HEADER);
            prefs.edit().putBoolean("FirstTypingTask", false);
        }

        if (tempSimpleSentences.size()==0){
            tempSimpleSentences.addAll(simpleSentences);
        }
        if (tempDifficSentences.size()==0){
            tempDifficSentences.addAll(difficultSentences);
        }

        userInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); // to disable autocompletion and autocorrection

        setMessage();

        userInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeStamp = System.currentTimeMillis();
                startTimeStampStr = String.valueOf(startTimeStamp);
                userInputEditText.setCursorVisible(true);
                imm.showSoftInput(userInputEditText, InputMethodManager.SHOW_IMPLICIT);
                Log.d("TIME start: ", "" + startTimeStamp);
            }
        });

        userInputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction()==KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DEL) {
                    backSpaceCount = backSpaceCount + 1;
                    Log.d("BACK",  backSpaceCount + " times key pressed");
                }
                return false;
            }
        });

        userInputEditText.addTextChangedListener(new EditTextListener() {
            @Override
            protected void onTextChanged(String before, String old, String aNew, String after) {
                completeOldText = before + old + after;
                completeNewText = before + aNew + after;
                startUpdates();

                oldTexts.add(completeOldText);
                newTexts.add(completeNewText);
                correctedLetters.add(old);
                numberOfTotalEnteredLetters++;
                Log.d("STRING NEW", newTexts+"");
                Log.d("CorrectedLetters", numberOfTotalEnteredLetters +"");

                StringJoiner joiner = new StringJoiner(";");
                for (String s: oldTexts){
                    joiner.add(s);
                }
                oldTextsString = joiner.toString();

                StringJoiner joiner1 = new StringJoiner(";");
                for (String s: newTexts){
                    joiner1.add(s);
                }
                newTextsString = joiner1.toString();

                StringJoiner joiner2 = new StringJoiner(";");
                for (String s: correctedLetters){
                    joiner2.add(s);
                }
                correctedLettersString = joiner2.toString();

                endUpdates();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInputString = userInputEditText.getText().toString();
                if (userInputString.matches("")) {
                    notice();
                    return;
                }
                else {
                    date = DateFormat.getDateTimeInstance().format(new Date());
                    endTimeStamp = System.currentTimeMillis();
                    endTimeStampStr = String.valueOf(endTimeStamp);
                    timeMillis = endTimeStamp - startTimeStamp;
                    Log.d("TIME end: ", "" + endTimeStamp);
                    Log.d("timeMillis: ", "" + timeMillis);
                    textViewString = messageTextView.getText().toString();
                    Log.d("DIFF: 3 ", difficulty);
                    String a_message = deviceID + ", " + startTimeStampStr + ", " + endTimeStampStr + ", " + String.valueOf(timeMillis) + ", " + textViewString + ", " + backSpaceCount + ", " + numberOfTotalEnteredLetters + ", " + difficulty + ", " + userInputString + ", " + oldTextsString + ", " + newTextsString + ", " + correctedLettersString + ", " + MainActivity.AppVersion;
                    //TypingData typingData = new TypingData(date, deviceID, startTimeStampStr, endTimeStampStr, String.valueOf(timeMillis), textViewString, String.valueOf(backSpaceCount), String.valueOf(numberOfTotalEnteredLetters), difficulty, userInputString, oldTextsString, newTextsString, correctedLettersString);
                    mDatabase.child(deviceID).child(rootName).child("TypingData").child(date).setValue(a_message);
                    userInputEditText.setCursorVisible(false);
                    userInputEditText.setText("");
                    imm.hideSoftInputFromWindow(userInputEditText.getWindowToken(), 0);
                    resetValues();
                }

            }
        });
    }

    public void notice(){
        Toast.makeText(this, "Input field cannot be empty", Toast.LENGTH_SHORT).show();
    }


    private void resetValues(){

        numberOfTotalEnteredLetters = 0;
        backSpaceCount = 0;
        oldTexts.clear();
        newTexts.clear();
        correctedLetters.clear();
        oldTextsString = "";
        newTextsString = "";
        userInputString = "";

        if (count < 2) {
            setMessage();
        } else{
            finishActivity();
        }
    }

    private void setMessage() {
        Random r = new Random(System.nanoTime());
        int i = r.nextInt(tempSimpleSentences.size());

        Log.d("RANDOM", ""+i);
        if (mode == 1){
            displayedSentence = tempSimpleSentences.get(i);
            Log.d("DisplaySentence: ", displayedSentence);
            messageTextView.setText(displayedSentence);
            mode = 2;
            difficulty = "easy";
            count++;
            tempSimpleSentences.remove(i);
            Log.d("MODE", mode+"");
            Log.d("DIFF: 1 ", difficulty);
        } else if (mode == 2){
            displayedSentence = tempDifficSentences.get(i);
            Log.d("DisplaySentence: ", displayedSentence);
            messageTextView.setText(displayedSentence);
            mode = 1;
            difficulty = "difficult";
            count++;
            tempDifficSentences.remove(i);
            Log.d("MODE", mode+"");
            Log.d("DIFF: 2 ", difficulty);
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);
    }

    public void finishActivity(){

        if(classList.size()!=0) {

            stopServices();

            Random r = new Random();
            int i = r.nextInt(classList.size());
            Intent intent = new Intent(TypingTaskActivity.this, classList.get(i));
            classList.remove(i);
            Log.d("CLASSLIST TYPING", classList+"");
            intent.putExtra("activity", classList);
            startActivity(intent);
            finish();
        }
        else{
            stopServices();
            TypingTaskActivity.this.finish();
        }
    }

    public void stopServices(){
        stopService(accelerometerIntent);
        stopService(batteryIntent);
        stopService(lightIntent);
        stopService( networkIntent );
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please do not press the back button", Toast.LENGTH_SHORT).show();
    }

}
