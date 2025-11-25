 package com.example.mathprojectavishaigozland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class LogInActivity extends AppCompatActivity {

    private Button Btn_NextPage;
    private EditText ED_UserName;
    Intent intent = new Intent(this, MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        createClickListener();

        SharedPreferences sh = getSharedPreferences("MyShardPref", MODE_PRIVATE);
        String s1 = sh.getString("name","");
        ED_UserName.setText(s1);
    }

    private void initViews() {
        Btn_NextPage=findViewById(R.id.NextPage);
        ED_UserName=findViewById(R.id.UserName);
    }
    public void createClickListener() {
        Btn_NextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyShardPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("name", ED_UserName.getText().toString());
                myEdit.apply();

                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.putExtra("userName", ED_UserName.getText().toString());
                startActivity(intent);
            }
        });

    }
}