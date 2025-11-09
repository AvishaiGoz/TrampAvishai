package com.example.mathprojectavishaigozland;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    private Button Btn_etgar;
    private Button Btn_up20;
    private Button Btn_kefel;
    private TextView TV_firstNum;
    private TextView TV_secondNum;
    private EditText ET_answer;
    private Button Btn_chek;
    private Button Btn_save;
    private Button Btn_Rate;
    private Button Btn_allUsers;
    private int result;
    Exercis ex;
    private ExercisCallbackInterface exercisCallbackInterface;
    private User user;
    private String userName;


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int myRate = result.getData().getIntExtra("RATE_KEY", -1);
                     Toast.makeText(MainActivity.this, "youre rate is: "+myRate, Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String RATE_KEY = getIntent().getStringExtra("RATE_KEY");

        initViews();
        welcomeUser();
        exercisCallbackInterface = new ExercisCallbackInterface() {
            @Override
            public void showNumber(int firstNum, int secondNum) {
                TV_firstNum.setText(""+firstNum);
                TV_secondNum.setText(""+secondNum);
            }
        };
        createClickListener();
         ex = new Exercis(exercisCallbackInterface);
    }

    private void initViews() {
        TV_firstNum = findViewById(R.id.firstNum);
        TV_secondNum = findViewById(R.id.secondNum);
        ET_answer = findViewById(R.id.answer);
        Btn_etgar = findViewById(R.id.etgar);
        Btn_up20 = findViewById(R.id.up20);
        Btn_kefel = findViewById(R.id.kefel);
        Btn_chek = findViewById(R.id.chek);
        Btn_save = findViewById(R.id.save);
        Btn_allUsers = findViewById(R.id.allUsers);
        Btn_Rate = findViewById(R.id.BT_Rate);

    }

    private void createClickListener() {
        Btn_kefel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){
                 ex.multy();
            }
        });
        Btn_up20.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ex.multy20();
            }
        }));
        Btn_etgar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ex.multyEtgar();
            }
        });
        Btn_chek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answear = ex.answearAfirst(ET_answer);
                int score = ex.point;
                if (answear == true) {
                    Toast.makeText(MainActivity.this, "מעולה!", Toast.LENGTH_SHORT).show();
                    user.setScore(user.getScore()+score);
                    Toast.makeText(MainActivity.this, "score = "+user.getScore(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "נסה שוב", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Btn_Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent packageContext;
                Intent intent = new Intent(MainActivity.this, RateActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        user = new User(userName);
    }
    public void welcomeUser(){
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        Toast.makeText(MainActivity.this, "welcome "+userName+"!", Toast.LENGTH_SHORT).show();
    }

}



