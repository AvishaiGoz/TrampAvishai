package com.example.mathprojectavishaigozland.tramp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyRides extends AppCompatActivity {

    private RecyclerView rvMyRides;
    private RideAdapter adapter;
    private List<Ride> myRideList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // וודא שזה השם של ה-XML שלך
        setContentView(R.layout.fragment_my_rides);

        // אתחול ה-RecyclerView
        rvMyRides = findViewById(R.id.rvMyRides);
        myRideList = new ArrayList<>();
        adapter = new RideAdapter(this, myRideList);

        rvMyRides.setLayoutManager(new LinearLayoutManager(this));
        rvMyRides.setAdapter(adapter);

        // טעינת הנסיעות ששייכות למשתמש המחובר
        loadMyRides();
    }

    private void loadMyRides() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        // סינון ב-Firebase: תביא רק נסיעות שה-userId שלהן שווה ל-ID שלי
        db.collection("rides")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        myRideList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Ride r = doc.toObject(Ride.class);
                            myRideList.add(r);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}