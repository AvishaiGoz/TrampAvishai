package com.example.mathprojectavishaigozland.mathproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mathprojectavishaigozland.R;
import com.google.gson.Gson;


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


    /**
     * משתנה האחראי על הפעלת Activity לקבלת תוצאה.
     * הוא מקבל את הדירוג טמציג אותו בToast כשה-RateActivity נסגרת
     */
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int myRate = result.getData().getIntExtra("RATE_KEY", -1);
                     Toast.makeText(MainActivity.this, "youre rate is: "+myRate, Toast.LENGTH_SHORT).show();

                    user.setRating(myRate);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String RATE_KEY = getIntent().getStringExtra("RATE_KEY");
        //אתחול רכיבי הממשק
        initViews();

        //קבלת שם המשתמש והודעת ברוך הבא
        welcomeUser();

        //מימוש ה-Interface לעדכון הטקסט כשיש תרגיל חדש
        exercisCallbackInterface = new ExercisCallbackInterface() {
            @Override
            public void showNumber(int firstNum, int secondNum) {
                TV_firstNum.setText(""+firstNum);
                TV_secondNum.setText(""+secondNum);
            }
        };

        //בניית המאזינים לכל הכפתורים
        createClickListener();

        //יצירת אובייקט של התרגילים
         ex = new Exercis(exercisCallbackInterface);
    }

    /**
     * פעולה המקשרת בין המשתנים בקוד לרכיבים הגרפיים ב-XML באמצעות ה-ID
     */
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

    /**
     * פעולה המגדירה את ה-Listener עבור כל הכפתורים
     */
    private void createClickListener() {

        //האזנה לכפתור לוח הכפל - יוצר תרגילי כפל רגילים
        Btn_kefel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){
                 ex.multy();
            }
        });

        //האזנה לכפתור עד 20 - יוצר תרגילי כפל בטווח של עד 20
        Btn_up20.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ex.multy20();
            }
        }));

        //האזנה לכפתור אתגר - יוצר תרגילי כפל מאתגרים
        Btn_etgar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ex.multyEtgar();
            }
        });

        //האזנה לכפתור בדיקה - בודק אם התשובה נכונה ומעדכן ניקוד
        Btn_chek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answear = ex.answearAfirst(ET_answer);
                int score = ex.point; //קבלת הניקוד עבור התרגיל הספציפי
                if (answear) {
                    Toast.makeText(MainActivity.this, "מעולה!", Toast.LENGTH_SHORT).show();
                    user.setScore(user.getScore() + score); //עדכון הניקוד לאובייקט המשתמש
                    Toast.makeText(MainActivity.this, "score = "+user.getScore(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "נסה שוב", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //האזנה לכפתור דירוג - פותח את מסך הדירוג ומצפה לקבל תשובה חזרה
        Btn_Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent packageContext;
                Intent intent = new Intent(MainActivity.this, RateActivity.class);
                activityResultLauncher.launch(intent);
            }
        });


        //יצירת אובייקט משתמש חדש עם השם שהתקבל
        user = new User(userName);

        //האזנה לכפתור הצגת משתמשים - הופך את המשתמש ל-JSON ומציג אותו ב-Fragment
        Btn_allUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //המרת אובייקט המשתמש למחרוזת JSON
                Gson gson = new Gson();
                String json = gson.toJson(user);

                //העברת הנתונים ל-Fragment באמצעות Bundel
                Bundle bundle = new Bundle();
                bundle.putString("myUser", json);

                //יצירת מופע של ה-Fragment והצמדת Bundle אליו
                fragment_showusers fragment = new fragment_showusers();
                fragment.setArguments(bundle);

                //החלפת התצוגה ב-Fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment, "all users")
                        .commit();
            }
        });
    }

    /**
     * פעולה המקבלת את שם המשתמש שנשלח מה-Activity הקודם ומציגה הודעת ברוך הבא
     */
    public void welcomeUser(){
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        Toast.makeText(MainActivity.this, "welcome "+userName+"!", Toast.LENGTH_SHORT).show();
    }


}



