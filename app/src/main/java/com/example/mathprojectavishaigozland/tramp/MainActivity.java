package com.example.mathprojectavishaigozland.tramp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mathprojectavishaigozland.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // הגדרת המרכיבים מה-XML
    private TextInputEditText etEmail, etPassword, etFullName, etPhone;
    private TextInputLayout layoutFullName, layoutPhone; // נצטרך אותם כדי להסתיר/להציג את השדות
    private Button btnMainAction; // הכפתור המרכזי שמתחלף
    private TextView tvTitle, tvSwitchMode; // כותרת המסך והטקסט להחלפת מצב
    private ProgressBar progressBar;

    // אובייקט ה-Authentication של Firebase
    private FirebaseAuth mAuth;

    // משתנה שעוקב אחרי המצב הנוכחי: true = הרשמה, false = התחברות
    private boolean isSignUpMode = true;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // בדיקה אם יש משתמש שמחובר כרגע
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // אם כן, עבור ישר לפיד
//            goToFeed();
//        }
//    }

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
        etPhone = findViewById(R.id.etPhone);

        layoutFullName = findViewById(R.id.layoutFullName); // העטיפה של שדה השם
        layoutPhone = findViewById(R.id.layoutPhone); // העטיפה של שדה הטלפון

        btnMainAction = findViewById(R.id.btnMainAction); // הכפתור המאוחד
        tvTitle = findViewById(R.id.tvTitle); // כותרת המסך
        tvSwitchMode = findViewById(R.id.tvSwitchMode); // הטקסט להחלפה

        progressBar = findViewById(R.id.progressBar);

        // הגדרת לחיצה על טקסט החלפת המצב (בין הרשמה להתחברות)
        tvSwitchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode();
            }
        });

        // הגדרת לחיצה על הכפתור המרכזי
        btnMainAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (isSignUpMode) {
                    // לוגיקת הרשמה
                    String fullName = etFullName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();

                    if (fullName.isEmpty()) {
                        etFullName.setError("נא להזין שם מלא");
                        return;
                    }
                    if (phone.isEmpty()) {
                        etPhone.setError("נא להזין מספר טלפון");
                        return;
                    }

                    if (validateInput(email, password)) {
                        registerUser(email, password, fullName, phone); // שליחת טלפון לפונקציית הרישום
                    }
                } else {
                    // לוגיקת התחברות
                    if (validateInput(email, password)) {
                        loginUser(email, password);
                    }
                }
            }
        });
    }

    // פונקציה שמחליפה בין מצב הרשמה למצב התחברות ב-UI
    private void switchMode() {
        if (isSignUpMode) {
            // עוברים למצב התחברות
            tvTitle.setText("התחברות");
            btnMainAction.setText("התחבר");
            tvSwitchMode.setText("משתמש חדש? צור חשבון כאן");
            layoutFullName.setVisibility(View.GONE); // מסתירים שם מלא
            layoutPhone.setVisibility(View.GONE); // מסתירים טלפון
            isSignUpMode = false;
        } else {
            // עוברים למצב הרשמה
            tvTitle.setText("יצירת חשבון חדש");
            btnMainAction.setText("הירשם עכשיו");
            tvSwitchMode.setText("כבר יש לך חשבון? התחבר כאן");
            layoutFullName.setVisibility(View.VISIBLE); // מראים שם מלא
            layoutPhone.setVisibility(View.VISIBLE); // מראים טלפון
            isSignUpMode = true;
        }
    }

    // פונקציה ליצירת משתמש חדש - מעודכנת עם טלפון
    private void registerUser(String email, String password, String fullName, String phone) {
        progressBar.setVisibility(View.VISIBLE); // עיגול המתנה
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // אם הצליח, נשמור את כל הפרטים (כולל טלפון) ב-Firestore
                        saveUserData(fullName, email, phone);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "שגיאת הרשמה: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // שמירת נתונים ב-Database - כולל שדה טלפון ליצירת קשר עתידית
    private void saveUserData(String fullName, String email, String phone) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("email", email);
        userMap.put("phoneNumber", phone); // שמירת המספר לצורך וואטסאפ בהמשך

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).set(userMap)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                        goToFeed();
                    } else {
                        Toast.makeText(MainActivity.this, "שגיאה בשמירת נתונים: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // פונקציה להתחברות משתמש קיים (ללא שינוי לוגי)
    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
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
        Intent intent = new Intent(this, HomeTravelShow.class);
        startActivity(intent);
        finish();
    }
}