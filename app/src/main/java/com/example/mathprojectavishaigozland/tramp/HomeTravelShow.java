package com.example.mathprojectavishaigozland.tramp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeTravelShow extends AppCompatActivity {

    private RecyclerView rvRides;
    private RideAdapter adapter;
    private List<Ride> rideList;
    private List<Ride> filteredList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentFilter = "all";
    private LinearLayout llFilterButtons; // שורת כפתורי הסינון

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_travel_show);

        // אתחול אלמנטים
        rvRides = findViewById(R.id.rvRidesFeed);
        llFilterButtons = findViewById(R.id.llFilterButtons);
        rideList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new RideAdapter(this, filteredList);
        rvRides.setLayoutManager(new LinearLayoutManager(this));
        rvRides.setAdapter(adapter);

        // --- כפתורי סינון פיד ראשי ---
        Button btnOffers = findViewById(R.id.btnFilterOffers);
        Button btnRequests = findViewById(R.id.btnFilterRequests);

        btnOffers.setOnClickListener(v -> toggleFilter("offer", btnOffers, btnRequests));
        btnRequests.setOnClickListener(v -> toggleFilter("request", btnRequests, btnOffers));

        // --- ניווט לפרגמנט "הנסיעות שלי" ---
        findViewById(R.id.btnMyRides).setOnClickListener(v -> {
            // הסתרת אלמנטים של המסך הראשי כדי למנוע כפילויות
            rvRides.setVisibility(View.GONE);
            llFilterButtons.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MyRidesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // כפתורי פרסום
        findViewById(R.id.btnNewOffer).setOnClickListener(v -> startActivity(new Intent(this, offer.class)));
        findViewById(R.id.btnNewRequest).setOnClickListener(v -> startActivity(new Intent(this, request.class)));

        loadRides();
    }

    private void toggleFilter(String type, Button selectedBtn, Button otherBtn) {
        if (currentFilter.equals(type)) {
            currentFilter = "all";
            selectedBtn.setAlpha(1.0f);
        } else {
            currentFilter = type;
            selectedBtn.setAlpha(0.5f);
            otherBtn.setAlpha(1.0f);
        }
        refreshDisplay();
    }

    private void refreshDisplay() {
        filteredList.clear();
        if (currentFilter.equals("all")) {
            filteredList.addAll(rideList);
        } else {
            for (Ride r : rideList) {
                if (r.getType() != null && r.getType().equalsIgnoreCase(currentFilter)) {
                    filteredList.add(r);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadRides() {
        db.collection("rides").orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        rideList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Ride r = doc.toObject(Ride.class);
                            rideList.add(r);
                        }
                        refreshDisplay();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // החזרת הפיד הראשי למצב גלוי ביציאה מהפרגמנט
        if (rvRides.getVisibility() == View.GONE) {
            rvRides.setVisibility(View.VISIBLE);
            llFilterButtons.setVisibility(View.VISIBLE);
        }
    }
}