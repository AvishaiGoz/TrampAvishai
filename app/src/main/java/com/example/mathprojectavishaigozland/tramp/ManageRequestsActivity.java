package com.example.mathprojectavishaigozland.tramp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageRequestsActivity extends AppCompatActivity {
    private String rideId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<PendingRequest> requestList = new ArrayList<>();
    private RequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);

        rideId = getIntent().getStringExtra("rideId");
        RecyclerView rv = findViewById(R.id.rvPendingRequests);

        adapter = new RequestAdapter(requestList, new RequestAdapter.OnRequestActionListener() {
            @Override
            public void onApprove(PendingRequest r) {
                approve(r);
            }

            @Override
            public void onReject(PendingRequest r) {
                reject(r);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        db.collection("rides").document(rideId).addSnapshotListener((doc, error) -> {
            if (doc == null || !doc.exists()) return;
            requestList.clear();

            Map<String, Long> pending = (Map<String, Long>) doc.get("pendingUsers");
            if (pending != null) {
                for (Map.Entry<String, Long> entry : pending.entrySet()) {
                    fetchUser(entry.getKey(), entry.getValue().intValue(), "ממתין");
                }
            }

            Map<String, Long> confirmed = (Map<String, Long>) doc.get("confirmedUsers");
            if (confirmed != null) {
                for (Map.Entry<String, Long> entry : confirmed.entrySet()) {
                    fetchUser(entry.getKey(), entry.getValue().intValue(), "מאושר");
                }
            }
        });
    }

    private void fetchUser(String uid, int seats, String status) {
        db.collection("users").document(uid).get().addOnSuccessListener(uDoc -> {
            if (uDoc != null && uDoc.exists()) {
                String name = uDoc.getString("fullName");
                String phone = uDoc.getString("phone"); // שליפת מספר הטלפון מה-DB

                requestList.add(new PendingRequest(uid, name, seats, status, phone));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void approve(PendingRequest req) {
        db.collection("rides").document(rideId).update(
                "pendingUsers." + req.getUserId(), FieldValue.delete(),
                "confirmedUsers." + req.getUserId(), req.getRequestedSeats()
        );
    }

    private void reject(PendingRequest req) {
        db.collection("rides").document(rideId).update(
                "pendingUsers." + req.getUserId(), FieldValue.delete()
        );
    }
}