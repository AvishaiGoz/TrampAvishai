package com.example.mathprojectavishaigozland.tramp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mathprojectavishaigozland.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class request extends AppCompatActivity {

    private TextInputEditText etOrigin, etDestination, etDate, etTime;
    private Button btnSubmitRequest;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // אתחול רכיבים - ודא שה-IDs תואמים בדיוק ל-XML שלך!
        etOrigin = findViewById(R.id.etPickupLocation);
        etDestination = findViewById(R.id.etDropOffLocation);
        etDate = findViewById(R.id.etRequestDate);
        etTime = findViewById(R.id.etRequestTime);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);

        // פתיחת בחירת תאריך
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                etDate.setText(day + "/" + (month + 1) + "/" + year);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // פתיחת בחירת שעה
        etTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                etTime.setText(String.format("%02d:%02d", hour, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        btnSubmitRequest.setOnClickListener(v -> publishRideRequest());
    }

    private void publishRideRequest() {
        String origin = etOrigin.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String dateStr = etDate.getText().toString().trim();

        if (origin.isEmpty() || destination.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "שגיאת התחברות", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference ref = db.collection("rides").document();

        // יצירת האובייקט - שים לב ל-type "request"
        Ride newRequest = new Ride(
                ref.getId(),
                uid,
                origin,
                destination,
                "request",
                new Timestamp(calendar.getTime()),
                1 // כמות נוסעים בבקשה
        );

        // חשוב מאוד: אתחול מפות ריקות כדי למנוע קריסות ב-Adapter
        newRequest.setPendingUsers(new HashMap<>());
        newRequest.setConfirmedUsers(new HashMap<>());

        btnSubmitRequest.setEnabled(false); // מניעת לחיצות כפולות

        ref.set(newRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "הבקשה פורסמה בהצלחה!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmitRequest.setEnabled(true);
                    Toast.makeText(this, "שגיאה בפרסום: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}