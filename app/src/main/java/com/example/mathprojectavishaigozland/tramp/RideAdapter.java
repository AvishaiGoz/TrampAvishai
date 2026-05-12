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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

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

        // 1. הגדרת נתונים בסיסיים
        holder.tvRoute.setText(ride.getOrigin() + " ➔ " + ride.getDestination());
        holder.tvDriverName.setText(ride.getDriverName());
        holder.tvTime.setText(ride.getTime() + " | " + ride.getDate());

        // 2. סעיף 2: צבעוניות לפי סוג נסיעה (הצעה = ירוק, בקשה = כתום)
        if ("offer".equalsIgnoreCase(ride.getType())) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E8F5E9")); // ירוק בהיר
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // כתום בהיר
        }

        // 3. לוגיקת כפתורים וסטטוסים
        boolean isCreator = ride.getDriverId() != null && ride.getDriverId().equals(currentUid);
        boolean isPending = ride.getPendingUsers() != null && ride.getPendingUsers().containsKey(currentUid);
        boolean isConfirmed = ride.getConfirmedUsers() != null && ride.getConfirmedUsers().containsKey(currentUid);

        if (isCreator) {
            // בעל הנסיעה - רואה ניהול בקשות וצור קשר (סעיף 3)
            holder.btnAction.setVisibility(View.GONE);
            holder.btnManageRequests.setVisibility(View.VISIBLE);
            holder.btnContact.setVisibility(View.VISIBLE); // הנהג תמיד יכול ליצור קשר
            holder.btnManageRequests.setOnClickListener(v -> {
                Intent intent = new Intent(context, ManageRequestsActivity.class);
                intent.putExtra("rideId", ride.getRideId());
                context.startActivity(intent);
            });
        } else {
            // משתמש רגיל
            holder.btnManageRequests.setVisibility(View.GONE);
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnContact.setVisibility(isConfirmed ? View.VISIBLE : View.GONE); // רק מאושר רואה "צור קשר"

            if (isConfirmed) {
                holder.btnAction.setText("מאושר!");
                holder.btnAction.setBackgroundColor(Color.GREEN);
                holder.btnAction.setEnabled(false);
            } else if (isPending) {
                holder.btnAction.setText("בהמתנה");
                holder.btnAction.setBackgroundColor(Color.GRAY);
                holder.btnAction.setEnabled(false);
            } else {
                holder.btnAction.setText("אני מעוניין");
                holder.btnAction.setBackgroundColor(context.getResources().getColor(R.color.purplle_500)); // צבע ברירת מחדל
                holder.btnAction.setEnabled(true);
            }
        }

        // כפתור צור קשר פותח Bottom Sheet
        holder.btnContact.setOnClickListener(v -> showContactBottomSheet(ride));
    }

    private void showContactBottomSheet(Ride ride) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        Button btnWhatsApp = view.findViewById(R.id.btnWhatsApp);
        Button btnCall = view.findViewById(R.id.btnCall);
        btnCall.setText("התקשר ל" + ride.getDriverName());

        btnWhatsApp.setOnClickListener(v -> {
            String phone = ride.getPhoneNumber();
            if (phone != null) {
                if (phone.startsWith("0")) phone = "972" + phone.substring(1);
                String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + Uri.encode("היי, לגבי הטרמפ...");
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        btnCall.setOnClickListener(v -> {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ride.getPhoneNumber())));
        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDriverName, tvTime;
        Button btnAction, btnManageRequests, btnContact;
        CardView cardView;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.itemOriginDest);
            tvDriverName = itemView.findViewById(R.id.itemDriverName);
            tvTime = itemView.findViewById(R.id.itemDate); // אפשר לשלב עם itemTime בקוד
            btnAction = itemView.findViewById(R.id.btnAction);
            btnManageRequests = itemView.findViewById(R.id.btnManageRequests);
            cardView = itemView.findViewById(R.id.cardViewItem);

            // שים לב: אם אין לך btnContact ב-XML, האפליקציה תקרוס כאן.
            // תוודא שהוספת אותו או תחליף ל-ID קיים.
            btnContact = itemView.findViewById(R.id.btnContact);
        }
    }
}