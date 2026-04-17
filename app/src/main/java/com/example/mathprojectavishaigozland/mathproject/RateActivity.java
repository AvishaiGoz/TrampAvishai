package com.example.mathprojectavishaigozland.mathproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mathprojectavishaigozland.R;

public class RateActivity extends AppCompatActivity {
    private SeekBar skRate;
    private Button Btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //מאפשר תצוגה "מקצה לקצה"
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate);

        // קריאה לפעולות עזר לאתחול הרכיבים והמאזינים
        initViews();
        createClickListener();
    }

    //פעולה לקישור רכיבי ה-UI למשתנים מה-XML
    private void initViews(){
        skRate = findViewById(R.id.skRate);
        Btn_save = findViewById(R.id.save);

    }

    //פעולה להגדרת מאזיני לחיצה
    private void createClickListener(){
        Btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //יצירת Intent ריק שישמש כצינור להעברת נתונים
                Intent inn = new Intent();
                //הכנסת הערך של סרגל הדירוג לתוך ה-Intent
                inn.putExtra("RATE_KEY", skRate.getProgress());
                //הגדרת התוצאה כהצלחה וצירוף הנתונים
                setResult(RESULT_OK, inn);
                finish();
                }
        });
    }


}