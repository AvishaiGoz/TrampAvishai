package com.example.mathprojectavishaigozland.tramp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;

import java.util.List;

// האדאפטר הוא המתווך - הוא לוקח את רשימת הנתונים (הנסיעות) ומחבר אותן לעיצוב ה-XML
public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<Ride> rideList; // הרשימה שתכיל את כל הטרמפים ששלפנו מה-Firestore

    // בנאי (Constructor) - מקבל את הרשימה כשאנחנו יוצרים את האדאפטר ב-HomeTravelShow
    public RideAdapter(List<Ride> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // השורה הזו "מנפחת" (Inflate) את ה-XML של שורה בודדת (ride_item) והופכת אותו לאובייקט Java
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        // כאן קורה החיבור האמיתי: לוקחים נסיעה ספציפית מהרשימה לפי המיקום שלה (position)
        Ride ride = rideList.get(position);

        // מכניסים את הנתונים מהאובייקט לתוך רכיבי הטקסט שהגדרנו ב-ViewHolder
        holder.tvOriginDest.setText("נסיעה מ - " + ride.getOrigin() + " ל -  " + ride.getDestination());
        holder.tvDate.setText(ride.getDate());
        holder.tvTime.setText(ride.getTime());

        // לוגיקה להצגת תוכן משתנה לפי סוג הנסיעה
        if ("request".equals(ride.getType())) {
            // אם זו בקשה - נציג את ההערות (ואם הן ריקות, נכתוב "דרוש טרמפ")
            String noteText = ride.getNotes();
            if (noteText.isEmpty()) {
                holder.tvSeats.setText("דרוש טרמפ");
            } else {
                holder.tvSeats.setText("הערות: " + noteText);
            }
            // צבע כתום לבקשה
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#FFF3E0")); //
        } else {
            // אם זו הצעה - נציג את מספר המקומות
            holder.tvSeats.setText("מקומות פנויים: " + ride.getSeats());
            // צבע ירוק להצעה
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
        }

        // שליפת שם הנהג מהאוסף users לפי ה-driverId
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(ride.getDriverId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        holder.tvDriverName.setText("מאת: " + fullName);
                    }
                });
    }

    @Override
    public int getItemCount() {
        // מחזיר למערכת את מספר הפריטים שיש ברשימה
        return rideList.size();
    }

    // ה-ViewHolder הוא "מחזיק הרכיבים" - הוא מוצא פעם אחת את ה-ID של כל שדה ב-XML
    // כדי שלא נצטרך לעשות findViewById כל פעם מחדש (זה חוסך זיכרון ומשפר מהירות)
    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvOriginDest, tvDate, tvTime, tvSeats;
        TextView tvDriverName;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            // קישור המשתנים לרכיבים בתוך ה-XML של השורה הבודדת (ride_item)
            tvOriginDest = itemView.findViewById(R.id.itemOriginDest);
            tvDate = itemView.findViewById(R.id.itemDate);
            tvTime = itemView.findViewById(R.id.itemTime);
            tvSeats = itemView.findViewById(R.id.itemSeats);
            tvDriverName = itemView.findViewById(R.id.itemDriverName);

        }
    }
}