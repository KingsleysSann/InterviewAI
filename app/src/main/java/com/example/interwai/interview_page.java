package com.example.interwai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class interview_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_page);

        Button switchButton = findViewById(R.id.button);

        EditText ques = findViewById(R.id.question);
        EditText criteria = findViewById(R.id.criteria);



        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to execute when the button is clicked
                String questions = String.valueOf(ques.getText());
                String crit = String.valueOf(criteria.getText());
                Intent intent = new Intent(interview_page.this, MainActivity.class);
                intent.putExtra("QUESTION", questions);
                intent.putExtra("CRITERIA", crit);

                startActivity(intent);
            }
        });
    }
}