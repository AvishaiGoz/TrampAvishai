package com.example.mathprojectavishaigozland.tramp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mathprojectavishaigozland.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // הגדרת המרכיבים מה-XML
    private TextInputEditText etEmail, etPassword, etFullName;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;

    // אובייקט ה-Authentication של Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // בדיקה אם יש משתמש שמחובר כרגע
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // אם כן, עבור ישר לפיד
            goToFeed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tremp_log_in);

        // אתחול Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // חיבור המשתנים לרכיבים ב-XML לפי ה-ID שלהם
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // הגדרת לחיצה על כפתור הרשמה
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();

                // בדיקה נוספת שהשם לא ריק
                if (fullName.isEmpty()) {
                    etFullName.setError("נא להזין שם מלא");
                    return;
                }

                if (validateInput(email, password)) {
                    registerUser(email, password, fullName); //
                }
            }
        });

        // הגדרת לחיצה על כפתור התחברות
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (validateInput(email, password)) {
                    loginUser(email, password);
                }
            }
        });
    }

    // פונקציה ליצירת משתמש חדש
    private void registerUser(String email, String password, String fullName) {
        progressBar.setVisibility(View.VISIBLE); //עיגול המתנה
        mAuth.createUserWithEmailAndPassword(email, password) // חיבור ל-Firebase ליצירת המשתמש ב-Auth
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // אם יצירת המשתמש הצליחה, נשמור את הפרטים הנוספים ב-Firestore
                        saveUserData(fullName, email);
                    } else {
                        progressBar.setVisibility(View.GONE); // מכבים את עיגול ההמתנה במקרה של שגיאה
                        Toast.makeText(MainActivity.this, "שגיאת הרשמה: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // פונקציה חדשה ששומרת את המידע הנוסף ב-Database
    private void saveUserData(String fullName, String email) {
        // קבלת ה-ID הייחודי של המשתמש שכרגע נרשם
        String userId = mAuth.getCurrentUser().getUid();

        // יצירת מפה של הנתונים
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("email", email);

        // גישה ל-Firestore ושמירת הנתונים תחת ה-UID של המשתמש
        FirebaseFirestore db = FirebaseFirestore.getInstance();  //עיגול המתנה
        db.collection("users").document(userId).set(userMap)  // חיבור ל-Firebase
                .addOnCompleteListener(task -> { //מאזין לסיום הבדיקה מול גוגל
                    progressBar.setVisibility(View.GONE); // סיימנו הכל - מכבים את העיגול
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                        goToFeed();
                    } else {
                        Toast.makeText(MainActivity.this, "שגיאה בשמירת נתונים: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // פונקציה להתחברות משתמש קיים
    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE); //עיגול המתנה
        mAuth.signInWithEmailAndPassword(email, password)  // חיבור ל-Firebase
                .addOnCompleteListener(this, task -> { //מאזין לסיום הבדיקה בגוגל
                    progressBar.setVisibility(View.GONE);  //לא משנה מה התשובה מהשרת - מכבים את עיגול ההמתנה
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                        goToFeed();
                    } else {
                        Toast.makeText(MainActivity.this, "שגיאת התחברות: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // בדיקת תקינות קלט בסיסית
    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("נא להזין אימייל");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("סיסמה חייבת להיות לפחות 6 תווים");
            return false;
        }
        return true;
    }

    // מעבר למסך הפיד אחרי הצלחה
    private void goToFeed() {
        //  החלפה ל-Intent שיפתח את ה-Activity של הפיד
        Intent intent = new Intent(this, HomeTravelShow.class);
        startActivity(intent);
        finish();
    }
}