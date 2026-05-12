package com.example.mathprojectavishaigozland.tramp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyRidesFragment extends Fragment {

    private RecyclerView rvMyRides;
    private RideAdapter adapter;
    private final List<Ride> allMyRidesRaw = new ArrayList<>();
    private final List<Ride> filteredMyRides = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // שים לב: הפרגמנט משתמש ב-Layout משלו (fragment_my_rides) שבו הוספת את הכפתורים שלו
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);

        rvMyRides = view.findViewById(R.id.rvMyRides);
        adapter = new RideAdapter(getContext(), filteredMyRides);
        rvMyRides.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyRides.setAdapter(adapter);

        // כפתורי סינון של הפרגמנט
        Button btnOffers = view.findViewById(R.id.btnFilterOffersMy);
        Button btnRequests = view.findViewById(R.id.btnFilterRequestsMy);

        btnOffers.setOnClickListener(v -> toggleFilter("offer", btnOffers, btnRequests));
        btnRequests.setOnClickListener(v -> toggleFilter("request", btnRequests, btnOffers));

        loadMyRides();
        return view;
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
        applyLocalFilter();
    }

    private void applyLocalFilter() {
        filteredMyRides.clear();
        if (currentFilter.equals("all")) {
            filteredMyRides.addAll(allMyRidesRaw);
        } else {
            for (Ride r : allMyRidesRaw) {
                if (r.getType() != null && r.getType().equalsIgnoreCase(currentFilter)) {
                    filteredMyRides.add(r);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadMyRides() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("rides").addSnapshotListener((value, error) -> {
            if (value != null) {
                allMyRidesRaw.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Ride r = doc.toObject(Ride.class);

                    // בדיקה האם הנסיעה רלוונטית למשתמש
                    boolean isCreator = r.getDriverId() != null && r.getDriverId().equals(uid);
                    boolean isConfirmed = r.getConfirmedUsers() != null && r.getConfirmedUsers().containsKey(uid);
                    boolean isPending = r.getPendingUsers() != null && r.getPendingUsers().containsKey(uid);

                    if (isCreator || isConfirmed || isPending) {
                        allMyRidesRaw.add(r);
                    }
                }
                applyLocalFilter();
            }
        });
    }
}