package com.example.mathprojectavishaigozland.tramp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mathprojectavishaigozland.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class offer extends AppCompatActivity {

    private TextInputEditText etOrigin, etDestination, etDate, etTime, etSeats;
    private Button btnPublishedRide;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        // אתחול Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // חיבור רכיבי ה-XML
        etOrigin = findViewById(R.id.etOrigin);
        etDestination = findViewById(R.id.etDestination);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etSeats = findViewById(R.id.etSeats);
        btnPublishedRide = findViewById(R.id.btnPublishRide);

        etDate.setOnClickListener(v -> {
            // השגת התאריך של היום כדי שהיומן יפתח על התאריך הנוכחי
            final java.util.Calendar c = java.util.Calendar.getInstance();
            int year = c.get(java.util.Calendar.YEAR);
            int month = c.get(java.util.Calendar.MONTH);
            int day = c.get(java.util.Calendar.DAY_OF_MONTH);

            // יצירת הדיאלוג של היומן
            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // מה קורה אחרי שהמשתמש בחר תאריך? כותבים אותו לשדה
                        etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                    }, year, month, day);
            datePickerDialog.show();
        });

        etTime.setOnClickListener(v -> {
            final java.util.Calendar c = java.util.Calendar.getInstance();
            int hour = c.get(java.util.Calendar.HOUR_OF_DAY);
            int minute = c.get(java.util.Calendar.MINUTE);

            // יצירת הדיאלוג של השעון
            android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(this,
                    (view, hourOfDay, minuteOfHour) -> {
                        // עיצוב השעה שתוצג (למשל 08:05 במקום 8:5)
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                        etTime.setText(formattedTime);
                    }, hour, minute, true); // true אומר פורמט של 24 שעות
            timePickerDialog.show();
        });

        // לחיצה על כפתור פרסום
        btnPublishedRide.setOnClickListener(v -> {
            publishRide();
        });
    }

    private void publishRide() {
        // שליפת הנתונים מהשדות
        String origin = etOrigin.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String seats = etSeats.getText().toString().trim();

        // בדיקה שכל השדות מלאים
        if (origin.isEmpty() || destination.isEmpty() || date.isEmpty() || time.isEmpty() || seats.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        // קבלת ה-UID של המשתמש המחובר (הנהג)
        String driverId = mAuth.getCurrentUser().getUid();

        // יצירת אובייקט הנסיעה
        Map<String, Object> ride = new HashMap<>();
        ride.put("origin", origin);
        ride.put("destination", destination);
        ride.put("date", date);
        ride.put("time", time);
        ride.put("seats", seats);
        ride.put("driverId", driverId); // שומרים מי הנהג כדי שנוכל לשלוף את שמו אחר כך
        ride.put("type", "offer");
        ride.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp()); // זמן יצירה לסידור בפיד

        // שמירה באוסף rides
        db.collection("rides")
                .add(ride) // add מייצר ID אוטומטי לכל נסיעה
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "הנסיעה פורסמה בהצלחה!", Toast.LENGTH_SHORT).show();
                    finish(); // סגירת המסך וחזרה לפיד
                })
                .addOnFailureListener(e -> {
                    // זה ידפיס ב-Logcat בדיוק מה הבעיה (למשל: חוסר בהרשאות)
                    android.util.Log.e("FIRESTORE_ERROR", "Error writing document", e);
                    Toast.makeText(this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}