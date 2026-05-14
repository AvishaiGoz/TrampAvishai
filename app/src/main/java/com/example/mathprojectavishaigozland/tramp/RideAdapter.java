package com.example.mathprojectavishaigozland.tramp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final Context context;
    private final List<Ride> rideList;

    public RideAdapter(Context context, List<Ride> rideList) {
        this.context = context;
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        String currentUid = FirebaseAuth.getInstance().getUid();

        // הגדרת טקסטים בסיסיים
        holder.tvRoute.setText(ride.getOrigin() + " ➔ " + ride.getDestination());
        holder.tvTime.setText(ride.getTime() + " | " + ride.getDate());

        // --- תיקון שם המפרסם: שליפה מה-DB אם חסר ---
        if (ride.getDriverName() != null && !ride.getDriverName().equals("מפרסם")) {
            holder.tvDriverName.setText("מפרסם: " + ride.getDriverName());
        } else {
            holder.tvDriverName.setText("מפרסם: טוען...");
            FirebaseFirestore.getInstance().collection("users").document(ride.getDriverId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            ride.setDriverName(name);
                            holder.tvDriverName.setText("מפרסם: " + name);
                        }
                    });
        }

        // חישוב מקומות פנויים
        int available = ride.getAvailableSeats();
        holder.tvSeats.setText("מקומות פנויים: " + available);

        // --- הבדלה בצבעים ---
        if ("request".equals(ride.getType())) {
            holder.cardView.setStrokeColor(Color.parseColor("#FF9800")); // כתום לבקשה
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.tvFuel.setVisibility(View.GONE);
        } else {
            holder.cardView.setStrokeColor(Color.parseColor("#2196F3")); // כחול להצעה
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.tvFuel.setVisibility(View.VISIBLE);
        }

        // לוגיקת כפתורים וסטטוסים
        boolean isCreator = ride.getDriverId().equals(currentUid);
        boolean isConfirmed = ride.getConfirmedUsers() != null && ride.getConfirmedUsers().containsKey(currentUid);
        boolean isPending = ride.getPendingUsers() != null && ride.getPendingUsers().containsKey(currentUid);

        if (isCreator) {
            holder.btnAction.setVisibility(View.GONE);
            holder.btnManageRequests.setVisibility(View.VISIBLE);
            holder.btnContact.setVisibility(View.GONE);
        } else {
            holder.btnManageRequests.setVisibility(View.GONE);
            holder.btnAction.setVisibility(View.VISIBLE);

            if (isConfirmed) {
                holder.btnAction.setText("מאושר ✓");
                holder.btnAction.setEnabled(false);
                holder.btnContact.setVisibility(View.VISIBLE);
            } else if (isPending) {
                holder.btnAction.setText("ממתין לאישור...");
                holder.btnAction.setEnabled(false);
                holder.btnContact.setVisibility(View.GONE);
            } else {
                holder.btnAction.setText("אני מעוניין");
                holder.btnAction.setEnabled(true);
                holder.btnContact.setVisibility(View.GONE);
                // לחיצה לפתיחת דיאלוג מקומות
                holder.btnAction.setOnClickListener(v -> showSeatsDialog(ride, available));
            }
        }

        holder.btnContact.setOnClickListener(v -> showContactBottomSheet(ride));
        holder.btnManageRequests.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManageRequestsActivity.class);
            intent.putExtra("rideId", ride.getRideId());
            context.startActivity(intent);
        });
    }

    private void showSeatsDialog(Ride ride, int maxAvailable) {
        if (maxAvailable <= 0) {
            Toast.makeText(context, "אין מקומות פנויים בנסיעה זו", Toast.LENGTH_SHORT).show();
            return;
        }

        // בניית אפשרויות לפי מספר המקומות הפנויים (עד 5)
        int limit = Math.min(maxAvailable, 5);
        String[] options = new String[limit];
        for (int i = 0; i < limit; i++) {
            options[i] = String.valueOf(i + 1);
        }

        new AlertDialog.Builder(context)
                .setTitle("כמה מקומות תרצה להזמין?")
                .setItems(options, (dialog, which) -> {
                    int selectedSeats = Integer.parseInt(options[which]);
                    sendRideRequest(ride.getRideId(), selectedSeats);
                })
                .show();
    }

    private void sendRideRequest(String rideId, int seats) {
        String currentUid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("rides")
                .document(rideId)
                .update("pendingUsers." + currentUid, seats)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "בקשתך נשלחה!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "שגיאה בשליחה: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showContactBottomSheet(Ride ride) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        Button btnWhatsApp = view.findViewById(R.id.btnWhatsApp);
        Button btnCall = view.findViewById(R.id.btnCall);
        String phone = ride.getPhoneNumber();

        btnWhatsApp.setOnClickListener(v -> {
            if (phone != null && !phone.isEmpty()) {
                String formattedPhone = phone.startsWith("0") ? "972" + phone.substring(1) : phone;
                String url = "https://api.whatsapp.com/send?phone=" + formattedPhone;
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                bottomSheetDialog.dismiss();
            }
        });

        btnCall.setOnClickListener(v -> {
            if (phone != null && !phone.isEmpty()) {
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDriverName, tvTime, tvSeats, tvFuel;
        Button btnAction, btnManageRequests, btnContact;
        MaterialCardView cardView;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.itemOriginDest);
            tvDriverName = itemView.findViewById(R.id.itemDriverName);
            tvTime = itemView.findViewById(R.id.itemDate);
            tvSeats = itemView.findViewById(R.id.itemSeats);
            tvFuel = itemView.findViewById(R.id.itemFuel);
            btnAction = itemView.findViewById(R.id.btnAction);
            btnManageRequests = itemView.findViewById(R.id.btnManageRequests);
            btnContact = itemView.findViewById(R.id.btnContact);
            cardView = itemView.findViewById(R.id.cardViewItem);
        }
    }
}