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

public class offer extends AppCompatActivity {
    private TextInputEditText etOrigin, etDest, etSeats, etDate, etTime;
    private Button btnPublish;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        // אתחול רכיבים
        etOrigin = findViewById(R.id.etOrigin);
        etDest = findViewById(R.id.etDestination);
        etSeats = findViewById(R.id.etSeats);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        btnPublish = findViewById(R.id.btnPublishRide);

        // בחירת תאריך
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                etDate.setText(day + "/" + (month + 1) + "/" + year);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // בחירת שעה
        etTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                etTime.setText(String.format("%02d:%02d", hour, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        btnPublish.setOnClickListener(v -> publishRide());
    }

    private void publishRide() {
        String o = etOrigin.getText().toString().trim();
        String d = etDest.getText().toString().trim();
        String s = etSeats.getText().toString().trim();

        if (o.isEmpty() || d.isEmpty() || s.isEmpty() || etDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        DocumentReference ref = db.collection("rides").document();

        // יצירת אובייקט נסיעה עם ה-Timestamp מהקלינדר
        Ride ride = new Ride(ref.getId(), uid, o, d, "offer", new Timestamp(calendar.getTime()), Integer.parseInt(s));

        ref.set(ride).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "נסיעה פורסמה בהצלחה!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}