package com.example.mathprojectavishaigozland.mathproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;

import java.util.ArrayList;

public class MyUsersAdapter extends RecyclerView.Adapter<
        MyUsersAdapter.MyViewHolder> {
    // רשימת הנתונים שתכיל את המשתמשים להצגה
    private final ArrayList<User> users;
    //Interface לטיפול באירועי לחיצה על פריט ברשימה
    private final InterMyOnItemClickListener listener;
    private RecyclerView rcShowFruits;


    //פעולה בונה - מקבלת את רשימת המשתמשים ואת המאזין ללחיצות
    public MyUsersAdapter(ArrayList<User> users, InterMyOnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    /**
     * פונקציה זו נקראת כאשר ה-RecyclerView צריך ליצור "שורה" חדשה בזיכרון.
     * כאן אנחנו מחברים (Inflate) את קובץ ה-XML של הפריט הבודד.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //הופך את קובץ ה-XML לאובייקט Java אמיתי ב-View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new MyViewHolder(view);
    }

    //  מדביק את פריט XML שאמור להיות מוכפל

    /**
     * פונקציה זו מחברת בין הנתון הספציפי (המשתמש) לבין התצוגה
     * היא נקראת עבור כל שורה שמוצגת על המסך
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //שואב את המשתמש מהרשימה לפי המיקום שלו ומבצע השמה
        holder.bind(users.get(position), listener);
    }

    // מחזיר את גודל המערך
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * מחלקה פנימית שתפקידה להחזיק את ההפניות לרכיבי ה-UI של פריט בודד
     * זה מונע קריאות חוזרות ל-findViewById ובכך משפר ביצועים
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvUserScore;
        ImageView ivUserImg;

        // קונסטרקטור לViewHolder מוצא את הרכיבים בתוך ה-View של השורה
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserScore = itemView.findViewById(R.id.tvItemScore);
            ivUserImg = itemView.findViewById(R.id.ivUserPic);
        }

        //פונקציית עזר להצגת הנתונים בתוך רכיבי התצוגה וקביעת מאזין ללחיצה
        public void bind(final User item, final InterMyOnItemClickListener listener) {
            // הגדרת הטקסטים
            tvUserName.setText(item.getUserName());
            tvUserScore.setText(item.getScore());
            // הגדרת מאזין ללחיצה על תמונת המשתמש
            ivUserImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //הפעלת הפונקציה מה-Interface שהוגדר ב-Activity
                    listener.onItemClick(item);
                }
            });
        }

    }//end inner class
}//end class

