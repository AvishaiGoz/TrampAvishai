package com.example.mathprojectavishaigozland.tramp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeTravelShow extends AppCompatActivity {

    private RecyclerView rvRidesFeed;
    private RideAdapter adapter;
    private List<Ride> rideList;
    private FirebaseFirestore db;
    private Button btnNewOffer;
    private Button btnNewRequest;
    private Button btnFilterOffers;
    private Button btnFilterRequests;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_travel_show);

        db = FirebaseFirestore.getInstance();
        rvRidesFeed = findViewById(R.id.rvRidesFeed);
        btnNewOffer = findViewById(R.id.btnNewOffer);
        btnNewRequest = findViewById(R.id.btnNewRequest);
        btnFilterOffers = findViewById(R.id.btnFilterOffers);
        btnFilterRequests = findViewById(R.id.btnFilterRequests);

        btnFilterOffers.setOnClickListener(v -> {
            if (currentFilter.equals("offer")) {
                // אם כבר היה מסונן על הצעות - תחזור להכל
                currentFilter = "all";
            } else {
                // אם לא - תעבור להצעות
                currentFilter = "offer";
            }
            loadRidesFromFirestore(currentFilter);
        });

        btnFilterRequests.setOnClickListener(v -> {
            if (currentFilter.equals("request")) {
                currentFilter = "all";
            } else {
                currentFilter = "request";
            }
            loadRidesFromFirestore(currentFilter);
        });

        // הגדרת ה-RecyclerView
        rideList = new ArrayList<>();
        adapter = new RideAdapter(rideList);
        rvRidesFeed.setLayoutManager(new LinearLayoutManager(this));
        rvRidesFeed.setAdapter(adapter);

        // כפתור מעבר למסך הצעת נסיעה (offer)
        btnNewOffer.setOnClickListener(v -> {
            Intent intent = new Intent(this, offer.class);
            startActivity(intent);
        });

        // כפתור מעבר לבקשת נסיעה (request)
        btnNewRequest.setOnClickListener(v -> {
            Intent intent = new Intent(this, request.class);
            startActivity(intent);
        });

        loadRidesFromFirestore("all");
    }

    private void loadRidesFromFirestore(String filterType) {
        // הגדרת בסיס השאילתה תמיד למיין לפי זמן
        com.google.firebase.firestore.Query query = db.collection("rides");

        // סינון לפי סוג רק אם נבחר סוג ספציפי
        if (filterType.equals("all")) {
            // במצב "הכל" - רק מיון לפי זמן
            query = query.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING);
        } else {
            query = query.whereEqualTo("type", filterType)
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING);
        }

        // 3. האזנה לשינויים
        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                android.util.Log.e("FIRESTORE", "Error: " + error.getMessage());
                return;
            }

            if (value != null) {
                rideList.clear();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    Ride ride = doc.toObject(Ride.class);
                    rideList.add(ride);
                }
                adapter.notifyDataSetChanged();

                // הודעה קטנה למשתמש אם הרשימה ריקה
                if (rideList.isEmpty()) {
                    Toast.makeText(this, "אין נסיעות להצגה כרגע", Toast.LENGTH_SHORT).show();
                }
            }
            updateButtonUI(filterType);
        });
    }

    private void updateButtonUI(String selectedType) {
        if (selectedType.equals("all")) {
            // במצב "הכל" - כולם מודגשים במידה שווה
            btnFilterOffers.setAlpha(1.0f);
            btnFilterRequests.setAlpha(1.0f);
        } else if (selectedType.equals("offer")) {
            btnFilterOffers.setAlpha(1.0f);
            btnFilterRequests.setAlpha(0.3f); // מחשיך את הכפתור השני
        } else if (selectedType.equals("request")) {
            btnFilterOffers.setAlpha(0.3f);
            btnFilterRequests.setAlpha(1.0f);
        }
    }
}