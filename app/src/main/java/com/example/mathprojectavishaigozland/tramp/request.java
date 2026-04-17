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

public class request extends AppCompatActivity {

    private TextInputEditText etOrigin, etDestination, etDate, etRequestTime, etNotes;
    private Button btnSubmitRequest;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etOrigin = findViewById(R.id.etPickupLocation);
        etDestination = findViewById(R.id.etDropOffLocation);
        etDate = findViewById(R.id.etRequestDate);
        etRequestTime = findViewById(R.id.etRequestTime);
        etNotes = findViewById(R.id.etNotes);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);

        btnSubmitRequest.setOnClickListener(v -> publishRequest());

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

        etRequestTime.setOnClickListener(v -> {
            final java.util.Calendar c = java.util.Calendar.getInstance();
            int hour = c.get(java.util.Calendar.HOUR_OF_DAY);
            int minute = c.get(java.util.Calendar.MINUTE);

            // יצירת הדיאלוג של השעון
            android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(this,
                    (view, hourOfDay, minuteOfHour) -> {
                        // עיצוב השעה שתוצג (למשל 08:05 במקום 8:5)
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                        etRequestTime.setText(formattedTime);
                    }, hour, minute, true); // true אומר פורמט של 24 שעות
            timePickerDialog.show();
        });
    }

    private void publishRequest() {
        String origin = etOrigin.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etRequestTime.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (origin.isEmpty() || destination.isEmpty() || date.isEmpty() || time.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> rideRequest = new HashMap<>();
        rideRequest.put("origin", origin);
        rideRequest.put("destination", destination);
        rideRequest.put("date", date);
        rideRequest.put("time", time);
        rideRequest.put("notes", notes);
        rideRequest.put("driverId", userId);
        rideRequest.put("type", "request");
        rideRequest.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("rides")
                .add(rideRequest)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "בקשת הנסיעה פורסמה!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // זה ידפיס ב-Logcat בדיוק מה הבעיה (למשל: חוסר בהרשאות)
                    android.util.Log.e("FIRESTORE_ERROR", "Error writing document", e);
                    Toast.makeText(this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}