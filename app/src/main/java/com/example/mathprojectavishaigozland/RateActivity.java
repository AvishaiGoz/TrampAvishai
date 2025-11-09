package com.example.mathprojectavishaigozland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RateActivity extends AppCompatActivity {
    private SeekBar skRate;
    private Button Btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate);

        initViews();
        createClickListener();
    }
    private void initViews(){
        skRate = findViewById(R.id.skRate);
        Btn_save = findViewById(R.id.save);

    }

    private void createClickListener(){
        Btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inn = new Intent();
                inn.putExtra("RATE_KEY", skRate.getProgress());
                setResult(RESULT_OK, inn);
                finish();
                }
        });
    }


}