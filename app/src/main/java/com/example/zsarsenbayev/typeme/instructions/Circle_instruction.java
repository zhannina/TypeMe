package com.example.zsarsenbayev.typeme.instructions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.zsarsenbayev.typeme.R;

import java.util.ArrayList;

public class Circle_instruction extends AppCompatActivity {

    private Button next_button;
    private int activity_index;

    ArrayList<Class<?>> classList;
    ArrayList<Class<?>> instructionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.circle_instruction);

        classList = new ArrayList<Class<?>>();
        ArrayList test1 = getIntent().getParcelableArrayListExtra("activity");
        for(int i = 0; i < test1.size(); i++){
            classList.add((Class<?>)test1.get(i));
        }

        instructionList = new ArrayList<Class<?>>();
        ArrayList test2 = getIntent().getParcelableArrayListExtra("activity instruction");
        for(int i = 0; i < test2.size(); i++){
            instructionList.add((Class<?>)test2.get(i));
        }

        activity_index = getIntent().getIntExtra( "activity index" , 0);

        next_button = (Button) findViewById(R.id.circle_button_next);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
                finish();
            }
        });

    }

    public void startActivity(){
        Intent intent = new Intent(this, classList.get(activity_index));
        classList.remove(activity_index);
        intent.putExtra("activity", classList);
        intent.putExtra("activity instruction", instructionList);
        startActivity(intent);
        finish();
    }
}