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
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private final List<PendingRequest> list;
    private final OnRequestActionListener listener;
    private Context context; // הוספת Context

    public RequestAdapter(List<PendingRequest> list, OnRequestActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext(); // שמירת ה-Context
        View v = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingRequest req = list.get(position);
        holder.tvInfo.setText(req.getUserName() + " (" + req.getRequestedSeats() + " מקומות)");

        if ("מאושר".equals(req.getStatus())) {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.tvStatusLabel.setVisibility(View.VISIBLE);
            holder.tvStatusLabel.setText("מאושר בנסיעה ✓");
            holder.tvStatusLabel.setTextColor(Color.parseColor("#4CAF50"));

            // הצגת כפתור צור קשר
            holder.btnContact.setVisibility(View.VISIBLE);
            holder.btnContact.setOnClickListener(v -> showContactBottomSheet(req));
        } else {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.tvStatusLabel.setVisibility(View.GONE);
            holder.btnContact.setVisibility(View.GONE);

            holder.btnApprove.setOnClickListener(v -> listener.onApprove(req));
            holder.btnReject.setOnClickListener(v -> listener.onReject(req));
        }
    }

    private void showContactBottomSheet(PendingRequest req) {
        // הנחה: הוספת שדה phoneNumber ל-PendingRequest
        String phone = req.getUserId(); // כאן כדאי להחליף ל-req.getPhoneNumber() אחרי שתעדכן את המודל

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        Button btnWhatsApp = view.findViewById(R.id.btnWhatsApp);
        Button btnCall = view.findViewById(R.id.btnCall);

        btnWhatsApp.setOnClickListener(v -> {
            // לוגיקת וואטסאפ (דומה למה שעשינו ב-RideAdapter)
            String url = "https://api.whatsapp.com/send?phone=" + phone;
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            bottomSheetDialog.dismiss();
        });

        btnCall.setOnClickListener(v -> {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnRequestActionListener {
        void onApprove(PendingRequest request);
        void onReject(PendingRequest request);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfo, tvStatusLabel;
        Button btnApprove, btnReject, btnContact;

        public ViewHolder(View v) {
            super(v);
            tvInfo = v.findViewById(R.id.tvRequesterName);
            tvStatusLabel = v.findViewById(R.id.tvRequestedSeats); // משמש כתווית סטטוס במצב מאושר
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject = v.findViewById(R.id.btnReject);
            btnContact = v.findViewById(R.id.btnContactUser);
        }
    }
}