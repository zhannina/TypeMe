package com.example.zsarsenbayev.typeme;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private int numberOfTotalEnteredLetters = 0;

    ArrayList<Class<?>> classList;

    public static final String WORKING_DIRECTORY = "/TypeMeData/";
    final String HEADER = "Date, Participant, Gender, Condition, Block, " +
            "StartTimeStamp, EndTimeStamp, TimeToType(ms), DisplayedMessage, BackspaceCount, NumberOfTotalEnteredKeyStrokes, Difficulty, UserInputMessage, TextBeforeChange, TextAfterChange, CorrectedLettersArray\n";

    private File file;
    private BufferedWriter bufferedWriter;
    private StringBuilder stringBuilder;

    SharedPreferences prefs;
    String participantCode, genderCode, conditionCode, blockCode;
    public static final String MyPREFERENCES = "MyPrefs";

    InputMethodManager imm;

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

//    private ArrayList<String> mediumSentences = new ArrayList<String>(Arrays.asList("Whatever you are, be a good one",
//            "Be the change you wish to see in the world",
//            "Try and fail, but never fail to try",
//            "Do one thing every day that scares you",
//            "Believe you can and you're halfway there"));

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
//    private int myRandomNumber;


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
        setContentView(R.layout.activity_typing_task);
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        submitButton = (Button) findViewById(R.id.submitButton);
        userInputEditText = (EditText) findViewById(R.id.userInputEditText);
        stringBuilder = new StringBuilder();

        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        participantCode = prefs.getString("participantCode", "");
        genderCode = prefs.getString("genderCode", "");
        conditionCode = prefs.getString("conditionCode", "");
        blockCode = prefs.getString("blockCode", "");

        imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);

        oldTexts = new ArrayList<>();
        newTexts = new ArrayList<>();
        correctedLetters = new ArrayList<>();

        tempSimpleSentences = new ArrayList<>();
        tempDifficSentences = new ArrayList<>();

        if (tempSimpleSentences.size()==0){
            tempSimpleSentences.addAll(simpleSentences);
        }
        if (tempDifficSentences.size()==0){
            tempDifficSentences.addAll(difficultSentences);
        }

        userInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); // to disable autocompletion and autocorrection

        setMessage();

        File dataDirectory = new File(this.getExternalFilesDir(null), WORKING_DIRECTORY);
        if( ! dataDirectory.exists() ) { // create directory if not exist
            dataDirectory.mkdirs();
        }
        String base = "TypeMe-" + participantCode + "-" + genderCode + "-" + conditionCode;
        file = new File(dataDirectory, base + ".csv");
        Log.d("FILE", file+"");

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            if (prefs.getBoolean("TYPEHEADERS", false)) {
                bufferedWriter.append(HEADER, 0, HEADER.length());
                bufferedWriter.flush();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("TYPEHEADERS", true);
                editor.commit();
            }

        } catch (IOException e) {
            Log.i("MYDEBUG", "Error opening data files! Exception: " + e.toString());
            System.exit(0);
        }

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
                date = DateFormat.getDateTimeInstance().format(new Date());
                endTimeStamp = System.currentTimeMillis();
                endTimeStampStr = String.valueOf(endTimeStamp);
                timeMillis = endTimeStamp - startTimeStamp;
                Log.d("TIME end: ", "" + endTimeStamp);
                Log.d("timeMillis: ", "" + timeMillis);
                userInputString = userInputEditText.getText().toString();
                textViewString = messageTextView.getText().toString();

                Log.d("DIFF: 3 ", difficulty);
                stringBuilder.append(String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %d, %d, %s, %s, %s, %s, %s\n", date, participantCode,
                        genderCode, conditionCode, blockCode, startTimeStampStr, endTimeStampStr, String.valueOf(timeMillis), textViewString, backSpaceCount, numberOfTotalEnteredLetters, difficulty, userInputString, oldTextsString, newTextsString, correctedLettersString));

                try {
                    bufferedWriter.write(stringBuilder.toString(), 0, stringBuilder.length());
                    bufferedWriter.flush();
                } catch (IOException e) {
                    Log.e("MYDEBUG", "ERROR WRITING TO DATA FILES: e = " + e);
                }
                stringBuilder.delete(0, stringBuilder.length());

                //resetting the values
                userInputEditText.setCursorVisible(false);
                userInputEditText.setText("");
                imm.hideSoftInputFromWindow(userInputEditText.getWindowToken(), 0);
                resetValues();
            }
        });
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

//        numberOfTotalEnteredLetters = 0;

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
            Intent intent = new Intent(TypingTaskActivity.this, classList.get(i));
            classList.remove(i);
            Log.d("CLASSLIST TYPING", classList+"");
            intent.putExtra("activity", classList);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(TypingTaskActivity.this, "Please return the phone", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please do not press the back button", Toast.LENGTH_SHORT).show();
    }

}
